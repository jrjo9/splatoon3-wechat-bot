package com.mayday9.splatoonbot.business.service;

import com.mayday9.splatoonbot.business.dto.TSplatMatchInfoAddDTO;
import com.mayday9.splatoonbot.business.dto.TSplatSalmonRunInfoAddDTO;

import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/9/29 9:43
 **/
public interface TSplatMatchInfoService {

    void saveMatchInfoBatch(List<TSplatMatchInfoAddDTO> matchInfoInsertList, String matchType);

    void saveSalmonRunBatch(List<TSplatSalmonRunInfoAddDTO> salmonRunInfoAddDTOList, String salmonRunType) throws Exception;
}
