package com.mayday9.splatoonbot.business.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/9/29 13:57
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "TSplatMatchInfoVO", description = "")
public class TSplatMatchInfoVO {

    @ApiModelProperty(value = "比赛ID")
    private Long matchId;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "比赛类型")
    private String matchType;

    @ApiModelProperty(value = "比赛明细")
    private List<TSplatMatchInfoDetailVO> matchInfoDetailVOList;

}
