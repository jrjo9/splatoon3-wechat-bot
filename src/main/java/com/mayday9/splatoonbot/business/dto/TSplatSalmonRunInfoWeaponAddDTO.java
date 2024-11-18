package com.mayday9.splatoonbot.business.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lianjiannan
 * @since 2024/9/29 9:38
 **/
@Setter
@Getter
@NoArgsConstructor
public class TSplatSalmonRunInfoWeaponAddDTO {

    @ApiModelProperty(value = "比赛ID")
    private Long salmonRunMainId;

    @ApiModelProperty(value = "武器键值")
    private String weaponKeyword;

    @ApiModelProperty(value = "武器名称")
    private String weaponName;

    @ApiModelProperty(value = "武器图片")
    private String weaponImage;

}
