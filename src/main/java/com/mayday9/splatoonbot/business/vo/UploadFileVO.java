package com.mayday9.splatoonbot.business.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(value = "UploadFileVO对象", description = "UploadFileVO")
public class UploadFileVO {

    @ApiModelProperty(value = "文件标题")
    private String fileTitle;

    @ApiModelProperty(value = "文件目录 单纯文件夹路径，不带文件名，如：F:\\新建文件夹\\")
    private String filePath;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件大小")
    private Long fileSize;

    @ApiModelProperty(value = "文件类型")
    private String fileType;

    @ApiModelProperty(value = "高度")
    private int height;

    @ApiModelProperty(value = "宽度")
    private int width;

    @ApiModelProperty(value = "横坐标")
    private int x;

    @ApiModelProperty(value = "纵坐标")
    private int y;

    @ApiModelProperty(value = "原图片路径保存前缀")
    private String saveFilePath;

    @ApiModelProperty(value = "压缩图片路径保存前缀")
    private String saveCompressFilePath;

    @ApiModelProperty(value = "剪切图片路径保存前缀")
    private String saveCutFilePath;

    @ApiModelProperty(value = "扩展参数")
    private Map<String, Object> params;
}
