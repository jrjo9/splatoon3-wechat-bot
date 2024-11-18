package com.mayday9.splatoonbot.business.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mayday9.splatoonbot.business.entity.TSplatSalmonRunInfo;
import com.mayday9.splatoonbot.business.vo.TSplatSalmonRunInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 喷喷鲑鱼跑打工信息表 Mapper 接口
 *
 * @author AutoGenerator
 * @since 2024-10-08
 */
@Mapper
public interface TSplatSalmonRunInfoMapper extends BaseMapper<TSplatSalmonRunInfo> {

    List<TSplatSalmonRunInfoVO> findMatchByDate(@Param("date") Date date, @Param("matchNumber") Integer matchNumber);
}
