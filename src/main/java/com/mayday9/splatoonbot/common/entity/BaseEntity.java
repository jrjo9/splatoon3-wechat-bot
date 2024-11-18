package com.mayday9.splatoonbot.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mayday9.splatoonbot.common.enums.DeleteEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Lianjiannan
 * @since 2024/9/23 17:47
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity implements Serializable {

    @ApiModelProperty(value = "主键", required = true)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "是否作废(1：是，0：否)", required = true)
    @TableField("is_delete")
    private DeleteEnum isDelete;

    @ApiModelProperty(value = "创建人", required = true)
    @TableField("create_by")
    private Long createBy;

    @ApiModelProperty(value = "更新人", required = true)
    @TableField("update_by")
    private Long updateBy;

    @ApiModelProperty(value = "创建时间", required = true)
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "更新时间", required = true)
    @TableField("update_time")
    private Date updateTime;
}
