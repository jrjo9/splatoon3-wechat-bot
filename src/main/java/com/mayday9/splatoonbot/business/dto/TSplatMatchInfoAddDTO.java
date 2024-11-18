package com.mayday9.splatoonbot.business.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/9/29 9:38
 **/
@Setter
@Getter
@NoArgsConstructor
public class TSplatMatchInfoAddDTO {

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "比赛类型")
    private String matchType;

    @ApiModelProperty(value = "结束时间")
    private List<TSplatMatchInfoDetailAddDTO> detailList;

}
