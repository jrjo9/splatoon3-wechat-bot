package com.mayday9.splatoonbot.business.dto.basic;

import com.mayday9.splatoonbot.common.enums.FlagEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lianjiannan
 * @since 2024/9/25 9:17
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupWxUserInfoDTO {

    @ApiModelProperty(value = "组ID")
    private String gid;

    @ApiModelProperty(value = "组名称")
    private String groupName;

    @ApiModelProperty(value = "激活标识")
    private FlagEnum activeFlag;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "微信号")
    private String wxid;

    @ApiModelProperty(value = "用户名称")
    private String username;

    @ApiModelProperty(value = "用户群昵称")
    private String nickname;

    @ApiModelProperty(value = "鲑鱼蛋数量")
    private Integer salmonEggs;

    @ApiModelProperty(value = "连续签到天数")
    private Integer signInDaysKeep;

    @ApiModelProperty(value = "总签到天数")
    private Integer signInDaysTotal;

    @ApiModelProperty(value = "签到时间")
    private Date lastSignInTime;
}
