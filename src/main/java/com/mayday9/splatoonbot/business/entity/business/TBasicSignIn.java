package com.mayday9.splatoonbot.business.entity.business;

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
* 签到表
*
* @author AutoGenerator
* @since 2024-09-23
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_basic_sign_in")
@ApiModel(value="TBasicSignIn对象", description="签到表")
public class TBasicSignIn extends BaseEntity {

    @ApiModelProperty(value = "微信号", required = true)
    @TableField("wxid")
    private String wxid;

    @ApiModelProperty(value = "用户名称", required = true)
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "签到时间", required = true)
    @TableField("sign_in_time")
    private Date signInTime;


}
