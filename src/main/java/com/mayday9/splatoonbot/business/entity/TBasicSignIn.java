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

import java.util.Date;

/**
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
@ApiModel(value = "TBasicSignIn对象", description = "签到表")
public class TBasicSignIn extends BaseEntity {

    @ApiModelProperty(value = "微信号", required = true)
    @TableField("wxid")
    private String wxid;

    @ApiModelProperty(value = "微信群ID", required = true)
    @TableField("gid")
    private String gid;

    @ApiModelProperty(value = "用户名称", required = true)
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "签到时间", required = true)
    @TableField("sign_in_time")
    private Date signInTime;


}
