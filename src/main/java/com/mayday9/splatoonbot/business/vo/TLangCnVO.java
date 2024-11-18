package com.mayday9.splatoonbot.business.vo;

import com.mayday9.splatoonbot.common.enums.DeleteEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * VO
 *
 * @author AutoGenerator
 * @since 2024-09-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "TLangCnVO对象", description = "")
public class TLangCnVO implements Serializable {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "对照类型")
    private String type;

    @ApiModelProperty(value = "键值")
    private String keyword;

    @ApiModelProperty(value = "中文名称")
    private String cnName;

    @ApiModelProperty(value = "是否作废(1：是，0：否)")
    private DeleteEnum isDelete;

    @ApiModelProperty(value = "创建人")
    private Long createBy;

    @ApiModelProperty(value = "更新人")
    private Long updateBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
