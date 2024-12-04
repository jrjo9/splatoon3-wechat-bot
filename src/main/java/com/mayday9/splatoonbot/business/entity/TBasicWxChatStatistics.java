package com.mayday9.splatoonbot.business.entity;

import com.mayday9.splatoonbot.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.mayday9.splatoonbot.common.enums.*;

/**
*
* 
*
* @author AutoGenerator
* @since 2024-12-04
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_basic_wx_chat_statistics")
@ApiModel(value="TBasicWxChatStatistics对象", description="")
public class TBasicWxChatStatistics extends BaseEntity {

    @ApiModelProperty(value = "聊天日期", required = true)
    @TableField("chat_date")
    private Date chatDate;

    @ApiModelProperty(value = "微信群ID", required = true)
    @TableField("gid")
    private String gid;

    @ApiModelProperty(value = "微信号", required = true)
    @TableField("wxid")
    private String wxid;

    @ApiModelProperty(value = "聊天数量", required = true)
    @TableField("chat_num")
    private Integer chatNum;


}
