package com.mayday9.splatoonbot.business.service.impl;

import com.mayday9.splatoonbot.business.dto.basic.WxGroupRegisterDTO;
import com.mayday9.splatoonbot.business.entity.TBasicWxAdmin;
import com.mayday9.splatoonbot.business.entity.TBasicWxGroup;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicWxAdminDao;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicWxGroupDao;
import com.mayday9.splatoonbot.business.service.WxSystemService;
import com.mayday9.splatoonbot.business.service.wxmsg.query.WxGroupNameQueryService;
import com.mayday9.splatoonbot.common.enums.FlagEnum;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author Lianjiannan
 * @since 2024/10/10 10:01
 **/
@Service
public class WxSystemServiceImpl implements WxSystemService {

    @Resource
    private TBasicWxAdminDao tBasicWxAdminDao;

    @Resource
    private TBasicWxGroupDao tBasicWxGroupDao;

    @Resource
    private WxGroupNameQueryService wxGroupNameQueryService;

    /**
     * 注册激活群组
     *
     * @param wxGroupRegisterDTO DTO
     * @return Boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void registerWxGroup(WxGroupRegisterDTO wxGroupRegisterDTO) {
        // 判断操作人是否管理员
        TBasicWxAdmin tBasicWxAdmin = tBasicWxAdminDao.findOneBy("wxid", wxGroupRegisterDTO.getWxid());
        if (tBasicWxAdmin == null) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "需管理员进行群组功能激活！");
        }
        // 判断是否已存在群组，若不存在则创建群组，已存在未激活则激活，若为已激活状态进行提示
        TBasicWxGroup tBasicWxGroup = tBasicWxGroupDao.findGroupByGid(wxGroupRegisterDTO.getGid());
        if (tBasicWxGroup == null) {
            // 查找群组名称
            String groupName = wxGroupNameQueryService.queryGroupName(wxGroupRegisterDTO.getGid());
            tBasicWxGroup = new TBasicWxGroup();
            tBasicWxGroup.setGid(wxGroupRegisterDTO.getGid());
            tBasicWxGroup.setGroupName(groupName);
            tBasicWxGroup.setActiveFlag(FlagEnum.YES);
            tBasicWxGroup.setAutoStatisticsFlag(FlagEnum.NO);
            tBasicWxGroupDao.save(tBasicWxGroup);
        } else {
            if (FlagEnum.YES.equals(tBasicWxGroup.getActiveFlag())) {
                throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "本群组已激活，请勿重复操作~");
            }
            tBasicWxGroup.setActiveFlag(FlagEnum.YES);
            tBasicWxGroupDao.updateById(tBasicWxGroup);
        }
    }

    /**
     * 转换群组自动统计开关
     *
     * @param gid 群组ID
     * @return void
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlagEnum switchWxGroupAutoStatistics(String gid) {
        TBasicWxGroup tBasicWxGroup = tBasicWxGroupDao.findGroupByGid(gid);
        if (tBasicWxGroup == null || FlagEnum.NO.equals(tBasicWxGroup.getActiveFlag())) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "请先激活群组！");
        }
        tBasicWxGroup.setAutoStatisticsFlag(tBasicWxGroup.getAutoStatisticsFlag() == FlagEnum.YES ? FlagEnum.NO : FlagEnum.YES);
        tBasicWxGroupDao.updateById(tBasicWxGroup);
        return tBasicWxGroup.getAutoStatisticsFlag();
    }

}
