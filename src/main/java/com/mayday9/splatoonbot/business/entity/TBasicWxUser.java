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
 * 微信人员信息表
 *
 * @author AutoGenerator
 * @since 2024-09-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_basic_wx_user")
@ApiModel(value = "TBasicWxUser对象", description = "微信人员信息表")
public class TBasicWxUser extends BaseEntity {

    @ApiModelProperty(value = "微信号", required = true)
    @TableField("wxid")
    private String wxid;

    @ApiModelProperty(value = "用户名称", required = true)
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "微信群ID", required = true)
    @TableField("gid")
    private String gid;

    @ApiModelProperty(value = "用户群昵称", required = true)
    @TableField("nickname")
    private String nickname;

    @ApiModelProperty(value = "鲑鱼蛋数量", required = true)
    @TableField("salmon_eggs")
    private Integer salmonEggs;

    @ApiModelProperty(value = "连续签到天数", required = true)
    @TableField("sign_in_days_keep")
    private Integer signInDaysKeep;

    @ApiModelProperty(value = "总签到天数", required = true)
    @TableField("sign_in_days_total")
    private Integer signInDaysTotal;

    @ApiModelProperty(value = "签到时间", required = true)
    @TableField("last_sign_in_time")
    private Date lastSignInTime;

    public TBasicWxUser(String wxid, String username, String gid, String nickname) {
        this.wxid = wxid;
        this.username = username;
        this.gid = gid;
        this.nickname = nickname;
        this.salmonEggs = 0;
        this.signInDaysKeep = 0;
        this.signInDaysTotal = 0;
    }
}
