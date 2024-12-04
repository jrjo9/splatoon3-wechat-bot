package com.mayday9.splatoonbot.business.service.impl;

import com.mayday9.splatoonbot.business.dto.statistics.MonthNoTalkUserDTO;
import com.mayday9.splatoonbot.business.dto.statistics.TodayStatisticsRankDTO;
import com.mayday9.splatoonbot.business.dto.wxmsg.resp.WxGroupUserInfoDetailRespDTO;
import com.mayday9.splatoonbot.business.dto.wxmsg.resp.WxUserInfoQueryRespDTO;
import com.mayday9.splatoonbot.business.entity.TBasicWxChatStatistics;
import com.mayday9.splatoonbot.business.entity.TBasicWxUser;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicWxChatStatisticsDao;
import com.mayday9.splatoonbot.business.infrastructure.dao.TBasicWxUserDao;
import com.mayday9.splatoonbot.business.service.GroupStatisticsService;
import com.mayday9.splatoonbot.business.service.wxmsg.query.WxGroupUserListQueryService;
import com.mayday9.splatoonbot.business.service.wxmsg.query.WxGroupUserNameQueryService;
import com.mayday9.splatoonbot.business.service.wxmsg.query.WxUserInfoQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 群组统计
 *
 * @author Lianjiannan
 * @since 2024/12/4 10:20
 **/
@Slf4j
@Service
public class GroupStatisticsServiceImpl implements GroupStatisticsService {

    @Resource
    private TBasicWxChatStatisticsDao tBasicWxChatStatisticsDao;

    @Resource
    private WxGroupUserListQueryService wxGroupUserListQueryService;

    @Resource
    private WxGroupUserNameQueryService wxGroupUserNameQueryService;

    @Resource
    private WxUserInfoQueryService wxUserInfoQueryService;

    @Resource
    private TBasicWxUserDao tBasicWxUserDao;

    /**
     * 生成群组统计消息
     *
     * @param wxid 微信ID
     * @param gid  群组ID
     * @return void
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void generateGroupStatistics(String wxid, String gid) {
        Date chatDate = new Date();
        Map<String, Object> param = new HashMap<>();
        param.put("wxid", wxid);
        param.put("gid", gid);
        param.put("chatDate", chatDate);
        TBasicWxChatStatistics statistics = tBasicWxChatStatisticsDao.findByGroupUserDate(param);
        if (statistics == null) {
            statistics = new TBasicWxChatStatistics();
            statistics.setGid(gid);
            statistics.setWxid(wxid);
            statistics.setChatDate(chatDate);
            statistics.setChatNum(1);
            tBasicWxChatStatisticsDao.save(statistics);
        } else {
            statistics.setChatNum(statistics.getChatNum() + 1);
            tBasicWxChatStatisticsDao.updateById(statistics);
        }
    }

    /**
     * 获取当天统计排名
     *
     * @param gid 群组ID
     * @return List<TodayStatisticsRankDTO>
     */
    @Override
    public List<TodayStatisticsRankDTO> getTodayStatisticsRank(String gid) {
        Date chatDate = new Date();
        List<TBasicWxChatStatistics> statisticsList = tBasicWxChatStatisticsDao.findStatisticsRankByDate(chatDate, gid);
        List<TodayStatisticsRankDTO> statisticsRankDTOList = new ArrayList<>(statisticsList.size());
        // 查询群昵称
        int rankIndex = 1;
        for (TBasicWxChatStatistics statistics : statisticsList) {
            String groupUserName = wxGroupUserNameQueryService.queryGroupUserName(statistics.getGid(), statistics.getWxid());
            TodayStatisticsRankDTO todayStatisticsRankDTO = new TodayStatisticsRankDTO();
            todayStatisticsRankDTO.setRank(rankIndex);
            todayStatisticsRankDTO.setUserGroupNickName(groupUserName);
            todayStatisticsRankDTO.setChatNumber(statistics.getChatNum());
            rankIndex++;
            statisticsRankDTOList.add(todayStatisticsRankDTO);
        }
        // 组装结果返回
        return statisticsRankDTOList;
    }

    /**
     * 获取月度未发言人员列表
     *
     * @param gid 群组ID
     * @return List<MonthNoTalkUserDTO>
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<MonthNoTalkUserDTO> getMonthNoTalkUserList(String gid) {
        this.updateGroup(gid);
        Date chatDate = new Date();
        List<TBasicWxChatStatistics> statisticsList = tBasicWxChatStatisticsDao.findMonthTalkUserList(chatDate, gid);
        List<TBasicWxUser> tBasicWxUsers = tBasicWxUserDao.findBy("gid", gid);
        List<TBasicWxUser> noTalkWxUserList = tBasicWxUsers.stream().filter(
                user -> !statisticsList.stream().map(TBasicWxChatStatistics::getWxid).collect(Collectors.joining()).contains(user.getWxid()))
            .collect(Collectors.toList());

        List<MonthNoTalkUserDTO> statisticsRankDTOList = new ArrayList<>();
        // 查询群昵称
        for (TBasicWxUser noTalkWxUser : noTalkWxUserList) {
            MonthNoTalkUserDTO monthNoTalkUserDTO = new MonthNoTalkUserDTO();
            monthNoTalkUserDTO.setUserGroupNickName(noTalkWxUser.getNickname() == null ? noTalkWxUser.getUsername() : noTalkWxUser.getNickname());
            statisticsRankDTOList.add(monthNoTalkUserDTO);
        }
        // 组装结果返回
        return statisticsRankDTOList;
    }

    /**
     * 更新群组信息
     *
     * @param gid
     * @return void
     */
    private void updateGroup(String gid) {
        List<TBasicWxUser> tBasicWxUsers = tBasicWxUserDao.findBy("gid", gid);
        List<TBasicWxUser> updateUserList = new ArrayList<>();
        List<TBasicWxUser> insertUserList = new ArrayList<>();
        // 获取群成员列表
        List<WxGroupUserInfoDetailRespDTO> wxGroupUserInfoRespDTOS = wxGroupUserListQueryService.queryGroupUserList(gid);
        for (WxGroupUserInfoDetailRespDTO wxGroupUserInfoRespDTO : wxGroupUserInfoRespDTOS) {
            // 获取成员微信昵称
            WxUserInfoQueryRespDTO wxUserInfoQueryRespDTO = wxUserInfoQueryService.queryUserInfo(wxGroupUserInfoRespDTO.getWxid());
            if (wxUserInfoQueryRespDTO == null) {
                continue;
            }
            TBasicWxUser userInfo = new TBasicWxUser();
            for (TBasicWxUser tBasicWxUser : tBasicWxUsers) {
                if (tBasicWxUser.getWxid().equals(wxGroupUserInfoRespDTO.getWxid())) {
                    userInfo = tBasicWxUser;
                    break;
                }
            }
            if (userInfo.getId() != null) {
                userInfo.setUsername(wxUserInfoQueryRespDTO.getName());
                userInfo.setNickname(wxGroupUserInfoRespDTO.getName());
                updateUserList.add(userInfo);
            } else {
                userInfo = new TBasicWxUser(wxGroupUserInfoRespDTO.getWxid(), wxUserInfoQueryRespDTO.getName(), gid, wxGroupUserInfoRespDTO.getName());
                insertUserList.add(userInfo);
            }
        }
        if (!updateUserList.isEmpty()) {
            tBasicWxUserDao.updateBatchById(updateUserList);
        }
        if (!insertUserList.isEmpty()) {
            tBasicWxUserDao.saveBatch(insertUserList);
        }
    }

}
