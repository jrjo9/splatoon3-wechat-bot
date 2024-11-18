package com.mayday9.splatoonbot.business.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/9/29 13:57
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "TSplatSalmonRunWeaponVO", description = "")
public class TSplatSalmonRunWeaponVO {

    @ApiModelProperty(value = "日程ID")
    private Long salmonRunWeaponId;

    @ApiModelProperty(value = "地图图片")
    private String weaponName;

    @ApiModelProperty(value = "地图图片")
    private String weaponImageFileUrl;

}
