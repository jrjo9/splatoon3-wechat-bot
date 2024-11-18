package com.mayday9.splatoonbot.business.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mayday9.splatoonbot.business.entity.TSplatMatchInfo;
import com.mayday9.splatoonbot.business.vo.TSplatMatchInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 喷喷比赛信息表 Mapper 接口
 *
 * @author AutoGenerator
 * @since 2024-09-29
 */
@Mapper
public interface TSplatMatchInfoMapper extends BaseMapper<TSplatMatchInfo> {

    List<TSplatMatchInfoVO> findMatchByDate(@Param("date") Date date, @Param("matchType") String matchType, @Param("matchNumber") Integer matchNumber);
}
