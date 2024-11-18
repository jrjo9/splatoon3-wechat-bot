package com.mayday9.splatoonbot.business.service.impl;

import cn.hutool.core.util.IdUtil;
import com.mayday9.splatoonbot.business.dto.TSplatMatchInfoAddDTO;
import com.mayday9.splatoonbot.business.dto.TSplatMatchInfoDetailAddDTO;
import com.mayday9.splatoonbot.business.dto.TSplatSalmonRunInfoAddDTO;
import com.mayday9.splatoonbot.business.dto.TSplatSalmonRunInfoWeaponAddDTO;
import com.mayday9.splatoonbot.business.entity.TSplatMatchInfo;
import com.mayday9.splatoonbot.business.entity.TSplatMatchInfoDetail;
import com.mayday9.splatoonbot.business.entity.TSplatSalmonRunInfo;
import com.mayday9.splatoonbot.business.entity.TSplatSalmonRunStage;
import com.mayday9.splatoonbot.business.entity.TSplatSalmonRunWeapon;
import com.mayday9.splatoonbot.business.entity.TSplatWeapon;
import com.mayday9.splatoonbot.business.entity.TSysFile;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSplatMatchInfoDao;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSplatMatchInfoDetailDao;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSplatSalmonRunInfoDao;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSplatSalmonRunStageDao;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSplatSalmonRunWeaponDao;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSplatWeaponDao;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSysFileDao;
import com.mayday9.splatoonbot.business.service.IFileUploadService;
import com.mayday9.splatoonbot.business.service.TSplatMatchInfoService;
import com.mayday9.splatoonbot.business.vo.UploadFileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Lianjiannan
 * @since 2024/9/29 9:44
 **/
@Slf4j
@Service
public class TSplatMatchInfoServiceImpl implements TSplatMatchInfoService {

    @Resource
    private TSplatMatchInfoDao tSplatMatchInfoDao;

    @Resource
    private TSplatMatchInfoDetailDao tSplatMatchInfoDetailDao;

    @Resource
    private TSplatSalmonRunInfoDao tSplatSalmonRunInfoDao;

    @Resource
    private TSplatSalmonRunWeaponDao tSplatSalmonRunWeaponDao;

    @Resource
    private TSplatWeaponDao tSplatWeaponDao;

    @Resource
    private TSplatSalmonRunStageDao tSplatSalmonRunStageDao;

    @Resource
    private IFileUploadService fileUploadService;

    @Resource
    private TSysFileDao tSysFileDao;

    /**
     * 保存赛程信息
     *
     * @param matchInfoInsertList 赛程数据
     * @param matchType           赛程类型
     * @return void
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMatchInfoBatch(List<TSplatMatchInfoAddDTO> matchInfoInsertList, String matchType) {
        // 判断时间
        List<Date> startTimeList = matchInfoInsertList.stream().map(TSplatMatchInfoAddDTO::getStartTime).collect(Collectors.toList());
        Map<String, Object> params = new HashMap<>();
        params.put("matchType", matchType);
        params.put("startTimeList", startTimeList);
        List<TSplatMatchInfo> existMatchInfoList = tSplatMatchInfoDao.findMatch(params);
        Map<Date, TSplatMatchInfo> existMatchInfoMap = existMatchInfoList.stream().collect(Collectors.toMap(TSplatMatchInfo::getStartTime, Function.identity(), (oldValue, newValue) -> oldValue));
        List<TSplatMatchInfo> matchInfoList = new ArrayList<>();
        Map<String, List<TSplatMatchInfoDetailAddDTO>> matchInfoDetailMap = new HashMap<>();
        for (TSplatMatchInfoAddDTO tSplatMatchInfoAddDTO : matchInfoInsertList) {
            if (existMatchInfoMap.containsKey(tSplatMatchInfoAddDTO.getStartTime())) {
                continue;
            }
            TSplatMatchInfo matchInfo = new TSplatMatchInfo();
            matchInfo.setTempId(IdUtil.simpleUUID());
            matchInfo.setStartTime(tSplatMatchInfoAddDTO.getStartTime());
            matchInfo.setEndTime(tSplatMatchInfoAddDTO.getEndTime());
            matchInfo.setMatchType(tSplatMatchInfoAddDTO.getMatchType());
            matchInfoList.add(matchInfo);

            List<TSplatMatchInfoDetailAddDTO> detailList = tSplatMatchInfoAddDTO.getDetailList();
            matchInfoDetailMap.put(matchInfo.getTempId(), detailList);
        }
        tSplatMatchInfoDao.saveBatch(matchInfoList);

        List<TSplatMatchInfoDetail> detailInsertList = new ArrayList<>();
        for (TSplatMatchInfo matchInfo : matchInfoList) {
            List<TSplatMatchInfoDetailAddDTO> detailList = matchInfoDetailMap.get(matchInfo.getTempId());
            for (TSplatMatchInfoDetailAddDTO detailAddDTO : detailList) {
                TSplatMatchInfoDetail detail = new TSplatMatchInfoDetail();
                detail.setMatchMode(detailAddDTO.getMatchMode());
                detail.setMatchRule(detailAddDTO.getMatchRule());
                detail.setMatchId(matchInfo.getId());
                detail.setVsStageId(detailAddDTO.getVsStageId());
                detailInsertList.add(detail);
            }
        }
        tSplatMatchInfoDetailDao.saveBatch(detailInsertList);
    }

    /**
     * 保存鲑鱼跑日程信息
     *
     * @param salmonRunInfoAddDTOList 鲑鱼跑日常信息
     * @return void
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSalmonRunBatch(List<TSplatSalmonRunInfoAddDTO> salmonRunInfoAddDTOList, String salmonRunType) throws Exception {
        // 判断时间
        List<Date> startTimeList = salmonRunInfoAddDTOList.stream().map(TSplatSalmonRunInfoAddDTO::getStartTime).collect(Collectors.toList());
        Map<String, Object> params = new HashMap<>();
        params.put("salmonRunType", salmonRunType);
        params.put("startTimeList", startTimeList);
        List<TSplatSalmonRunInfo> existMatchInfoList = tSplatSalmonRunInfoDao.findMatch(params);
        Map<Date, TSplatSalmonRunInfo> existMatchInfoMap = existMatchInfoList.stream()
            .collect(Collectors.toMap(TSplatSalmonRunInfo::getStartTime, Function.identity(), (o, n) -> o));
        List<TSplatSalmonRunInfo> salmonRunInfoList = new ArrayList<>();
        Map<String, List<TSplatSalmonRunInfoWeaponAddDTO>> salmonRunWeaponMap = new HashMap<>();
        for (TSplatSalmonRunInfoAddDTO tSplatMatchInfoAddDTO : salmonRunInfoAddDTOList) {
            if (existMatchInfoMap.containsKey(tSplatMatchInfoAddDTO.getStartTime())) {
                continue;
            }
            TSplatSalmonRunInfo salmonRunInfo = new TSplatSalmonRunInfo();
            salmonRunInfo.setTempId(IdUtil.simpleUUID());
            salmonRunInfo.setStartTime(tSplatMatchInfoAddDTO.getStartTime());
            salmonRunInfo.setEndTime(tSplatMatchInfoAddDTO.getEndTime());
            salmonRunInfo.setSalmonRunType(tSplatMatchInfoAddDTO.getSalmonRunType());
            salmonRunInfo.setBossKeyword(tSplatMatchInfoAddDTO.getBossKeyword());
            salmonRunInfo.setBossName(tSplatMatchInfoAddDTO.getBossName());
            salmonRunInfo.setStageKeyword(tSplatMatchInfoAddDTO.getStageKeyword());
            salmonRunInfo.setStageName(tSplatMatchInfoAddDTO.getStageName());
            salmonRunInfo.setStageThumbnailImage(tSplatMatchInfoAddDTO.getStageThumbnailImage());
            salmonRunInfo.setStageImage(tSplatMatchInfoAddDTO.getStageImage());
            salmonRunInfoList.add(salmonRunInfo);
            List<TSplatSalmonRunInfoWeaponAddDTO> weaponAddDTOList = tSplatMatchInfoAddDTO.getWeaponList();
            salmonRunWeaponMap.put(salmonRunInfo.getTempId(), weaponAddDTOList);
        }
        tSplatSalmonRunInfoDao.saveBatch(salmonRunInfoList);

        List<TSplatSalmonRunWeapon> weaponInsertList = new ArrayList<>();
        for (TSplatSalmonRunInfo salmonRunInfo : salmonRunInfoList) {
            List<TSplatSalmonRunInfoWeaponAddDTO> weaponAddDTOList = salmonRunWeaponMap.get(salmonRunInfo.getTempId());
            for (TSplatSalmonRunInfoWeaponAddDTO weaponAddDTO : weaponAddDTOList) {
                TSplatSalmonRunWeapon weapon = new TSplatSalmonRunWeapon();
                weapon.setSalmonRunMainId(salmonRunInfo.getId());
                weapon.setWeaponImage(weaponAddDTO.getWeaponImage());
                weapon.setWeaponKeyword(weaponAddDTO.getWeaponKeyword());
                weapon.setWeaponName(weaponAddDTO.getWeaponName());
                weaponInsertList.add(weapon);
            }
        }
        tSplatSalmonRunWeaponDao.saveBatch(weaponInsertList);

        List<String> weaponKeywordList = weaponInsertList.stream().map(TSplatSalmonRunWeapon::getWeaponKeyword).collect(Collectors.toList());
        List<TSplatWeapon> weaponList = tSplatWeaponDao.findBy("keyword", weaponKeywordList);
        Map<String, TSplatWeapon> weaponMap = weaponList.stream().collect(Collectors.toMap(TSplatWeapon::getKeyword, Function.identity(), (oldValue, newValue) -> oldValue));
        List<TSplatWeapon> weaponInserList = new ArrayList<>();
        for (TSplatSalmonRunWeapon salmonRunWeapon : weaponInsertList) {
            if (!weaponMap.containsKey(salmonRunWeapon.getWeaponKeyword())) {
                TSplatWeapon tSplatWeapon = new TSplatWeapon();
                tSplatWeapon.setKeyword(salmonRunWeapon.getWeaponKeyword());
                tSplatWeapon.setWeaponName(salmonRunWeapon.getWeaponName());
                tSplatWeapon.setWeaponImage(salmonRunWeapon.getWeaponImage());
                weaponInserList.add(tSplatWeapon);
                weaponMap.put(salmonRunWeapon.getWeaponKeyword(), tSplatWeapon);
            }
        }
        tSplatWeaponDao.saveBatch(weaponInserList);
        List<TSysFile> fileInsertList = new ArrayList<>();

        for (TSplatWeapon weapon : weaponInserList) {
            // 上传图片
            UploadFileVO uploadFileVO = fileUploadService.uploadSplatoonFile(weapon.getWeaponImage());
            TSysFile tSysFileDTO = new TSysFile();
            tSysFileDTO.setBizArgs("weapon");
            tSysFileDTO.setFileTitle(uploadFileVO.getFileTitle());
            tSysFileDTO.setFileSize(uploadFileVO.getFileSize());
            tSysFileDTO.setFileType(uploadFileVO.getFileType());
            tSysFileDTO.setFileUrl(uploadFileVO.getFilePath());
            tSysFileDTO.setWidth(uploadFileVO.getWidth());
            tSysFileDTO.setHeight(uploadFileVO.getHeight());
            tSysFileDTO.setBizId(weapon.getId());
            fileInsertList.add(tSysFileDTO);
        }

        // 判断是否有场地信息，没有则存储，并缓存图片
        List<String> stageKeywordList = salmonRunInfoList.stream().map(TSplatSalmonRunInfo::getStageKeyword).collect(Collectors.toList());
        List<TSplatSalmonRunStage> stageList = tSplatSalmonRunStageDao.findBy("stageKeyword", stageKeywordList);
        Map<String, TSplatSalmonRunStage> stageMap = stageList.stream().collect(Collectors.toMap(TSplatSalmonRunStage::getStageKeyword, Function.identity(), (oldValue, newValue) -> oldValue));
        List<TSplatSalmonRunStage> stageInsertList = new ArrayList<>();
        Set<String> stageImageSet = new HashSet<>();
        for (TSplatSalmonRunInfo salmonRunInfo : salmonRunInfoList) {
            if (!stageMap.containsKey(salmonRunInfo.getStageKeyword())) {
                TSplatSalmonRunStage stage = new TSplatSalmonRunStage();
                stage.setStageKeyword(salmonRunInfo.getStageKeyword());
                stage.setStageImage(salmonRunInfo.getStageImage());
                stage.setStageThumbnailImage(salmonRunInfo.getStageImage());
                stage.setStageName(salmonRunInfo.getStageName());
                stageInsertList.add(stage);
                stageMap.put(salmonRunInfo.getStageKeyword(), stage);
            }
        }
        tSplatSalmonRunStageDao.saveBatch(stageInsertList);
        // 上传图片
        for (TSplatSalmonRunStage stage : stageInsertList) {
            UploadFileVO uploadFileVO = fileUploadService.uploadSplatoonFile(stage.getStageThumbnailImage());
            TSysFile tSysFileDTO = new TSysFile();
            tSysFileDTO.setBizArgs("salmon_run_stage");
            tSysFileDTO.setFileTitle(uploadFileVO.getFileTitle());
            tSysFileDTO.setFileSize(uploadFileVO.getFileSize());
            tSysFileDTO.setFileType(uploadFileVO.getFileType());
            tSysFileDTO.setFileUrl(uploadFileVO.getFilePath());
            tSysFileDTO.setWidth(uploadFileVO.getWidth());
            tSysFileDTO.setHeight(uploadFileVO.getHeight());
            tSysFileDTO.setBizId(stage.getId());
            fileInsertList.add(tSysFileDTO);
        }
        if (!CollectionUtils.isEmpty(fileInsertList)) {
            tSysFileDao.saveBatch(fileInsertList);
        }


    }

}
