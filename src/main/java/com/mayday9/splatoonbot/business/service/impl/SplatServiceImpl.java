package com.mayday9.splatoonbot.business.service.impl;

import cn.hutool.core.date.DateUtil;
import com.mayday9.splatoonbot.business.constants.SplatoonConstant;
import com.mayday9.splatoonbot.business.dto.GetMatchDailyInfoDTO;
import com.mayday9.splatoonbot.business.dto.GetSalmonRunDailyInfoDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatBankaraSchedulesDrawDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatBankaraSchedulesDrawVsDetailDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatRegularSchedulesDrawDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatRegularSchedulesDrawVsDetailDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatSalmonRunDrawDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatSalmonRunDrawWeaponDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatXSchedulesDrawDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.draw.SplatXSchedulesDrawVsDetailDTO;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSplatMatchInfoDao;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSplatSalmonRunInfoDao;
import com.mayday9.splatoonbot.business.service.SplatService;
import com.mayday9.splatoonbot.business.vo.TSplatMatchInfoDetailVO;
import com.mayday9.splatoonbot.business.vo.TSplatMatchInfoVO;
import com.mayday9.splatoonbot.business.vo.TSplatSalmonRunInfoVO;
import com.mayday9.splatoonbot.business.vo.TSplatSalmonRunWeaponVO;
import com.mayday9.splatoonbot.common.util.ImageGenerator;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 喷喷业务
 *
 * @author Lianjiannan
 * @since 2024/9/29 11:44
 **/
@Slf4j
@Service
public class SplatServiceImpl implements SplatService {

    @Resource
    private TSplatMatchInfoDao tSplatMatchInfoDao;

    @Resource
    private TSplatSalmonRunInfoDao tSplatSalmonRunInfoDao;

    /**
     * 获取赛程信息
     *
     * @param getMatchDailyInfoDTO 数据DTO
     * @return void
     */
    @Override
    public File getMatchDailyInfo(GetMatchDailyInfoDTO getMatchDailyInfoDTO) {
        if (SplatoonConstant.MATCH_TYPE_SALMON_RUN.equals(getMatchDailyInfoDTO.getMatchType())) {
            GetSalmonRunDailyInfoDTO salmonRunDailyInfoDTO = new GetSalmonRunDailyInfoDTO();
            salmonRunDailyInfoDTO.setMatchNumber(getMatchDailyInfoDTO.getMatchNumber());
            return this.getSalmonRunDailyInfo(salmonRunDailyInfoDTO);
        }
        // 查找当前时间往后的比赛记录
        // 获取最近的偶数时间进行过滤
        LocalDateTime now = LocalDateTime.now();
        int currentHour = now.getHour();
        int nearestEvenHour = currentHour % 2 == 0 ? currentHour : currentHour - 1;
        LocalDateTime nearestEvenHourTime = now.withHour(nearestEvenHour).withMinute(0).withSecond(0).withNano(0);
        Date date = Date.from(nearestEvenHourTime.atZone(ZoneId.systemDefault()).toInstant());
        List<TSplatMatchInfoVO> matchInfoVOList = tSplatMatchInfoDao.findMatchByDate(date, getMatchDailyInfoDTO.getMatchType(), getMatchDailyInfoDTO.getMatchNumber());
        if (CollectionUtils.isEmpty(matchInfoVOList)) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "找不到比赛记录！");
        }
        // 组装赛程图片数据
        File file = null;
        if (SplatoonConstant.MATCH_TYPE_BANKARA.equals(getMatchDailyInfoDTO.getMatchType())) {
            file = this.generateBankaraMatchImage(matchInfoVOList);
        }
        if (SplatoonConstant.MATCH_TYPE_REGULAR.equals(getMatchDailyInfoDTO.getMatchType())) {
            file = this.generateRegularMatchImage(matchInfoVOList);
        }
        if (SplatoonConstant.MATCH_TYPE_X.equals(getMatchDailyInfoDTO.getMatchType())) {
            file = this.generateXMatchImage(matchInfoVOList);
        }
        if (file == null) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "获取赛程图片失败！");
        }
        return file;
    }

    private File generateXMatchImage(List<TSplatMatchInfoVO> matchInfoVOList) {
        List<SplatXSchedulesDrawDTO> dataList = new ArrayList<>();
        for (TSplatMatchInfoVO matchInfoVO : matchInfoVOList) {
            SplatXSchedulesDrawDTO data = new SplatXSchedulesDrawDTO();
            data.setTimeBetween(DateUtil.format(matchInfoVO.getStartTime(), "HH:mm") + " - " + DateUtil.format(matchInfoVO.getEndTime(), "HH:mm"));
            String matchRule = null;
            List<TSplatMatchInfoDetailVO> matchInfoDetailVOList = matchInfoVO.getMatchInfoDetailVOList();
            List<SplatXSchedulesDrawVsDetailDTO> matchVsDetailList = new ArrayList<>();
            for (TSplatMatchInfoDetailVO matchInfoDetailVO : matchInfoDetailVOList) {
                matchRule = matchInfoDetailVO.getMatchRule();
                SplatXSchedulesDrawVsDetailDTO matchVsDetail = new SplatXSchedulesDrawVsDetailDTO();
                matchVsDetail.setName(matchInfoDetailVO.getCnName());
                matchVsDetail.setImageUrl(matchInfoDetailVO.getFileUrl());
                matchVsDetailList.add(matchVsDetail);
            }
            data.setMatchRule(this.formatMatchRule(matchRule));
            data.setMatchVsDetailList(matchVsDetailList);
            dataList.add(data);
        }
        // 生成临时图片
        try {
            return ImageGenerator.generateXMatchImage(dataList);
        } catch (Exception e) {
            log.error("生成图片失败！{}", e.getMessage());
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "生成图片失败！");
        }
    }

    /**
     * 生成涂地图片
     *
     * @param matchInfoVOList 赛程信息
     * @return void
     */
    private File generateRegularMatchImage(List<TSplatMatchInfoVO> matchInfoVOList) {
        List<SplatRegularSchedulesDrawDTO> dataList = new ArrayList<>();
        for (TSplatMatchInfoVO matchInfoVO : matchInfoVOList) {
            SplatRegularSchedulesDrawDTO data = new SplatRegularSchedulesDrawDTO();
            data.setTimeBetween(DateUtil.format(matchInfoVO.getStartTime(), "HH:mm") + " - " + DateUtil.format(matchInfoVO.getEndTime(), "HH:mm"));
            List<TSplatMatchInfoDetailVO> matchInfoDetailVOList = matchInfoVO.getMatchInfoDetailVOList();
            List<SplatRegularSchedulesDrawVsDetailDTO> matchVsDetailList = new ArrayList<>();
            for (TSplatMatchInfoDetailVO matchInfoDetailVO : matchInfoDetailVOList) {
                SplatRegularSchedulesDrawVsDetailDTO matchVsDetail = new SplatRegularSchedulesDrawVsDetailDTO();
                matchVsDetail.setName(matchInfoDetailVO.getCnName());
                matchVsDetail.setImageUrl(matchInfoDetailVO.getFileUrl());
                matchVsDetailList.add(matchVsDetail);
            }
            data.setMatchVsDetailList(matchVsDetailList);
            dataList.add(data);
        }
        // 生成临时图片
        try {
            return ImageGenerator.generateRegularMatchImage(dataList);
        } catch (Exception e) {
            log.error("生成图片失败！{}", e.getMessage());
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "生成图片失败！");
        }
    }

    /**
     * 生成真格图片
     *
     * @param matchInfoVOList 赛程信息
     * @return void
     */
    private File generateBankaraMatchImage(List<TSplatMatchInfoVO> matchInfoVOList) {
        List<SplatBankaraSchedulesDrawDTO> dataList = new ArrayList<>();
        for (TSplatMatchInfoVO matchInfoVO : matchInfoVOList) {
            SplatBankaraSchedulesDrawDTO data = new SplatBankaraSchedulesDrawDTO();
            data.setTimeBetween(DateUtil.format(matchInfoVO.getStartTime(), "HH:mm") + " ~ " + DateUtil.format(matchInfoVO.getEndTime(), "HH:mm"));
            List<TSplatMatchInfoDetailVO> matchInfoDetailVOList = matchInfoVO.getMatchInfoDetailVOList();
            String openMatchRule = null;
            String challengeMatchRule = null;
            List<SplatBankaraSchedulesDrawVsDetailDTO> openMatchVsDetailList = new ArrayList<>();
            List<SplatBankaraSchedulesDrawVsDetailDTO> challengeMatchVsDetailList = new ArrayList<>();
            for (TSplatMatchInfoDetailVO matchInfoDetailVO : matchInfoDetailVOList) {
                if (SplatoonConstant.MATCH_MODE_OPEN.equals(matchInfoDetailVO.getMatchMode())
                    || SplatoonConstant.MATCH_MODE_REGULAR.equals(matchInfoDetailVO.getMatchMode())) {
                    openMatchRule = matchInfoDetailVO.getMatchRule();
                    SplatBankaraSchedulesDrawVsDetailDTO openMatchVsDetail = new SplatBankaraSchedulesDrawVsDetailDTO();
                    openMatchVsDetail.setName(matchInfoDetailVO.getCnName());
                    openMatchVsDetail.setImageUrl(matchInfoDetailVO.getFileUrl());
                    openMatchVsDetailList.add(openMatchVsDetail);
                }
                if (SplatoonConstant.MATCH_MODE_CHALLENGE.equals(matchInfoDetailVO.getMatchMode())) {
                    challengeMatchRule = matchInfoDetailVO.getMatchRule();
                    SplatBankaraSchedulesDrawVsDetailDTO challengeMatchVsDetail = new SplatBankaraSchedulesDrawVsDetailDTO();
                    challengeMatchVsDetail.setName(matchInfoDetailVO.getCnName());
                    challengeMatchVsDetail.setImageUrl(matchInfoDetailVO.getFileUrl());
                    challengeMatchVsDetailList.add(challengeMatchVsDetail);
                }
            }
            if (SplatoonConstant.MATCH_TYPE_BANKARA.equals(matchInfoVO.getMatchType())) {
                data.setOpenMatchRule(this.formatMatchRule(openMatchRule));
                data.setChallengeMatchRule(this.formatMatchRule(challengeMatchRule));
            } else {
                data.setOpenMatchRule("祭典");
                data.setChallengeMatchRule("祭典");
            }
            data.setOpenMatchVsDetailList(openMatchVsDetailList);
            data.setChallengeMatchVsDetailList(challengeMatchVsDetailList);
            dataList.add(data);
        }
        // 生成临时图片
        try {
            return ImageGenerator.generateBankaraMatchImage(dataList);
        } catch (Exception e) {
            log.error("生成图片失败！{}", e.getMessage());
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "生成图片失败！");
        }
    }

    /**
     * 格式化成中文
     *
     * @param matchRule 比赛规则
     * @return String
     */
    private String formatMatchRule(String matchRule) {
        String result = "";
        if (StringUtils.isEmpty(matchRule)) {
            return result;
        }
        switch (matchRule) {
            case "area":
                result = "区域";
                break;
            case "clam":
                result = "蛤蜊";
                break;
            case "goal":
                result = "鱼虎";
                break;
            case "loft":
                result = "塔楼";
                break;
            default:
                break;
        }
        return result;
    }


    @Override
    public File getSalmonRunDailyInfo(GetSalmonRunDailyInfoDTO getSalmonRunDailyInfoDTO) {
        // 查找当前时间往后的比赛记录
        // 获取最近的偶数时间进行过滤
//        LocalDateTime now = LocalDateTime.now();
//        int currentHour = now.getHour();
//        int nearestEvenHour = currentHour % 2 == 0 ? currentHour : currentHour - 1;
//        LocalDateTime nearestEvenHourTime = now.withHour(nearestEvenHour).withMinute(0).withSecond(0).withNano(0);
//        Date date = Date.from(nearestEvenHourTime.atZone(ZoneId.systemDefault()).toInstant());
        List<TSplatSalmonRunInfoVO> matchInfoVOList = tSplatSalmonRunInfoDao.findMatchByDate(new Date(), getSalmonRunDailyInfoDTO.getMatchNumber() - 1);
        if (CollectionUtils.isEmpty(matchInfoVOList)) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "找不到比赛记录！");
        }
        // 排序
        matchInfoVOList.sort(Comparator.comparing(TSplatSalmonRunInfoVO::getStartTime));
        List<SplatSalmonRunDrawDTO> dataList = new ArrayList<>();
        for (TSplatSalmonRunInfoVO salmonRunInfoVO : matchInfoVOList) {
            SplatSalmonRunDrawDTO salmonRunDrawDTO = new SplatSalmonRunDrawDTO();
            // 10/2 周三 16:00 - 10/4 8:00
            String dayOfWeek = this.getDayOfWeek(salmonRunInfoVO.getStartTime());
            salmonRunDrawDTO.setTimeBetween(DateUtil.format(salmonRunInfoVO.getStartTime(), "MM/dd ")
                + dayOfWeek
                + DateUtil.format(salmonRunInfoVO.getStartTime(), " HH:mm")
                + " - "
                + DateUtil.format(salmonRunInfoVO.getEndTime(), "MM/dd HH:mm"));
            salmonRunDrawDTO.setBossName(salmonRunInfoVO.getBossName());
            salmonRunDrawDTO.setStageName(salmonRunInfoVO.getStageCnName());
            salmonRunDrawDTO.setStageImage(salmonRunInfoVO.getStageImageFileUrl());
            List<SplatSalmonRunDrawWeaponDTO> weaponDTOList = new ArrayList<>();
            for (TSplatSalmonRunWeaponVO weaponInfoVO : salmonRunInfoVO.getWeaponList()) {
                SplatSalmonRunDrawWeaponDTO weaponDTO = new SplatSalmonRunDrawWeaponDTO();
                weaponDTO.setName(weaponInfoVO.getWeaponName());
                weaponDTO.setImageUrl(weaponInfoVO.getWeaponImageFileUrl());
                weaponDTOList.add(weaponDTO);
            }
            salmonRunDrawDTO.setWeaponDTOList(weaponDTOList);
            dataList.add(salmonRunDrawDTO);
        }
        // 生成临时图片
        try {
            return ImageGenerator.generateSalmonRunImage(dataList);
        } catch (Exception e) {
            log.error("生成图片失败！{}", e.getMessage());
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "生成图片失败！");
        }
    }

    private String getDayOfWeek(Date date) {
        // 创建一个Calendar对象
        Calendar calendar = Calendar.getInstance();
        // 将日期对象设置给Calendar对象
        calendar.setTime(date);
        // 获取周几，注意周日是1，周一是2，以此类推
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return "周" + (dayOfWeek == 1 ? "日" : dayOfWeek == 2 ? "一" : dayOfWeek == 3 ?
            "二" : dayOfWeek == 4 ? "三" : dayOfWeek == 5 ? "四" : dayOfWeek ==
            6 ? "五" : "六");
    }


}
