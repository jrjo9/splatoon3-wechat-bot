package com.mayday9.splatoonbot.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mayday9.splatoonbot.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author AutoGenerator
 * @since 2024-09-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_lang_cn")
@ApiModel(value = "TLangCn对象", description = "")
public class TLangCn extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "对照类型", required = true)
    @TableField("type")
    private String type;

    @ApiModelProperty(value = "键值", required = true)
    @TableField("keyword")
    private String keyword;

    @ApiModelProperty(value = "中文名称", required = true)
    @TableField("cn_name")
    private String cnName;

}
