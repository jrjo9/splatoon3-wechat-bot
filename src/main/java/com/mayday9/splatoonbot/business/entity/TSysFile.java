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

/**
 * 附件表
 *
 * @author AutoGenerator
 * @since 2024-09-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_file")
@ApiModel(value = "TSysFile对象", description = "附件表")
public class TSysFile extends BaseEntity {

    @ApiModelProperty(value = "业务ID", required = true)
    @TableField("biz_id")
    private Long bizId;

    @ApiModelProperty(value = "业务参数", required = true)
    @TableField("biz_args")
    private String bizArgs;

    @ApiModelProperty(value = "文件标题", required = true)
    @TableField("file_title")
    private String fileTitle;

    @ApiModelProperty(value = "文件类型", required = true)
    @TableField("file_type")
    private String fileType;

    @ApiModelProperty(value = "排序号", required = true)
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty(value = "文件URL", required = true)
    @TableField("file_url")
    private String fileUrl;

    @ApiModelProperty(value = "文件大小", required = true)
    @TableField("file_size")
    private Long fileSize;

    @ApiModelProperty(value = "图片宽度", required = true)
    @TableField("width")
    private Integer width;

    @ApiModelProperty(value = "图片高度", required = true)
    @TableField("height")
    private Integer height;


}
