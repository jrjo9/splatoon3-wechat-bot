package com.mayday9.splatoonbot.business.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.mayday9.splatoonbot.business.convert.TBasicWxUserConvert;
import com.mayday9.splatoonbot.business.dto.basic.GroupWxUserInfoDTO;
import com.mayday9.splatoonbot.business.dto.basic.WxUserSignInDTO;
import com.mayday9.splatoonbot.business.dto.wxmsg.resp.WxUserInfoQueryRespDTO;
import com.mayday9.splatoonbot.business.entity.TBasicSignIn;
import com.mayday9.splatoonbot.business.entity.TBasicWxGroup;
import com.mayday9.splatoonbot.business.entity.TBasicWxUser;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicSignInDao;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicWxGroupDao;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicWxUserDao;
import com.mayday9.splatoonbot.business.service.SignInService;
import com.mayday9.splatoonbot.business.service.wxmsg.query.WxGroupUserNameQueryService;
import com.mayday9.splatoonbot.business.service.wxmsg.query.WxUserInfoQueryService;
import com.mayday9.splatoonbot.business.vo.WxUserSignInVO;
import com.mayday9.splatoonbot.common.enums.FlagEnum;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author Lianjiannan
 * @since 2024/9/24 15:05
 **/
@Slf4j
@Service
public class SignInServiceImpl implements SignInService {

    @Resource
    private WxUserInfoQueryService wxUserInfoQueryService;

    @Resource
    private WxGroupUserNameQueryService wxGroupUserNameQueryService;

    @Resource
    private TBasicWxGroupDao tBasicWxGroupDao;

    @Resource
    private TBasicWxUserDao tBasicWxUserDao;

    @Resource
    private TBasicSignInDao tBasicSignInDao;


    /**
     * 用户签到
     *
     * @param wxUserSignInDTO 签到DTO
     * @return WxUserSignInVO
     */
    @Override
    @Transactional
    public WxUserSignInVO wxUserSignIn(WxUserSignInDTO wxUserSignInDTO) {
        // 判断该微信组是否激活
        TBasicWxGroup wxGroup = tBasicWxGroupDao.findGroupByGid(wxUserSignInDTO.getGid());
        if (wxGroup == null || FlagEnum.NO.equals(wxGroup.getActiveFlag())) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "请先激活当前群组！");
        }
        // 判断该微信用户信息是否存在或激活
        GroupWxUserInfoDTO groupWxUserInfo = tBasicWxUserDao.findGroupWxUserInfo(wxUserSignInDTO.getGid(), wxUserSignInDTO.getWxid());
        if (groupWxUserInfo == null) {
            // 微信用户未存在，新增记录
            // 查联系人详细信息
            WxUserInfoQueryRespDTO wxUserInfoQueryRespDTO = wxUserInfoQueryService.queryUserInfo(wxUserSignInDTO.getWxid());
            String nickName = wxGroupUserNameQueryService.queryGroupUserName(wxUserSignInDTO.getGid(), wxUserSignInDTO.getWxid());
            TBasicWxUser wxUser = new TBasicWxUser(wxUserSignInDTO.getWxid(), wxUserInfoQueryRespDTO.getName(), wxUserSignInDTO.getGid(), nickName);
            tBasicWxUserDao.save(wxUser);
            groupWxUserInfo = TBasicWxUserConvert.INSTANCE.convertDO(wxUser);
            groupWxUserInfo.setUserId(wxUser.getId());
        }

        Date signInTime = new Date();

        TBasicSignIn todaySignIn = tBasicSignInDao.findByDate(wxUserSignInDTO.getWxid(), wxUserSignInDTO.getGid(), signInTime);
        if (todaySignIn != null) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "今天已签到过咯鱿！");
        }

        // 记录签到信息
        TBasicSignIn tBasicSignIn = new TBasicSignIn();
        tBasicSignIn.setWxid(wxUserSignInDTO.getWxid());
        tBasicSignIn.setGid(wxUserSignInDTO.getGid());
        tBasicSignIn.setUsername(groupWxUserInfo.getUsername());
        tBasicSignIn.setSignInTime(signInTime);
        tBasicSignInDao.add(tBasicSignIn);


        // 随机获取1~5鲑鱼蛋
        Integer salmonEggs = RandomUtil.randomInt(1, 6);

        // 更新用户签到信息
        TBasicWxUser wxUser = tBasicWxUserDao.findById(groupWxUserInfo.getUserId());
        if (wxUser == null) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "微信号：" + wxUserSignInDTO.getWxid() + "不存在！");
        }
        wxUser.setUsername(groupWxUserInfo.getUsername());
        wxUser.setNickname(groupWxUserInfo.getNickname());
        wxUser.setSalmonEggs(wxUser.getSalmonEggs() != null ? wxUser.getSalmonEggs() + salmonEggs : salmonEggs);
        wxUser.setSignInDaysTotal(wxUser.getSignInDaysTotal() != null ? wxUser.getSignInDaysTotal() + 1 : 1);
        // 判断前一天是否有签到信息
        Date yesterdayDate = DateUtil.offsetDay(signInTime, -1);
        TBasicSignIn yesterdaySignIn = tBasicSignInDao.findByDate(wxUserSignInDTO.getWxid(), wxUserSignInDTO.getGid(), yesterdayDate);
        if (yesterdaySignIn == null) {
            wxUser.setSignInDaysKeep(1);
        } else {
            wxUser.setSignInDaysKeep(wxUser.getSignInDaysKeep() != null ? wxUser.getSignInDaysKeep() + 1 : 1);
        }
        wxUser.setLastSignInTime(signInTime);
        tBasicWxUserDao.updateById(wxUser);


        // 返回结果
        WxUserSignInVO wxUserSignInVO = new WxUserSignInVO();
        wxUserSignInVO.setGid(wxUserSignInDTO.getGid());
        wxUserSignInVO.setWxid(wxUserSignInDTO.getWxid());
        wxUserSignInVO.setUsername(wxUser.getUsername());
        wxUserSignInVO.setSalmonEggsToday(salmonEggs);
        wxUserSignInVO.setSalmonEggsTotal(wxUser.getSalmonEggs());
        wxUserSignInVO.setSignInDaysKeep(wxUser.getSignInDaysKeep());
        wxUserSignInVO.setSignInDaysTotal(wxUser.getSignInDaysTotal());
        wxUserSignInVO.setLastSignInTime(wxUser.getLastSignInTime());
        log.info("@{} 签到成功！\n" +
                "获得了{}个鲑鱼蛋，总共拥有{}个鲑鱼蛋\n" +
                "连续签到：{}天\n" +
                "总签到：{}天\n" +
                "签到时间：{}",
            wxUserSignInVO.getUsername(),
            wxUserSignInVO.getSalmonEggsToday(),
            wxUserSignInVO.getSalmonEggsTotal(),
            wxUserSignInVO.getSignInDaysKeep(),
            wxUserSignInVO.getSignInDaysTotal(),
            DateUtil.format(wxUserSignInVO.getLastSignInTime(), "yyyy-MM-dd HH:mm:ss"));
        return wxUserSignInVO;
    }
}
