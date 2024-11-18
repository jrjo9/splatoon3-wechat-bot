package com.mayday9.splatoonbot.business.vo;

import cn.hutool.core.date.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Lianjiannan
 * @since 2024/9/24 16:09
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxUserSignInVO {

    /**
     * @xxx 签到成功！今日本群频道第1个签到！
     * 获得了2个鲑鱼蛋，总共拥有2个鲑鱼蛋
     * 连续签到：1天
     * 总签到：1天
     * 最近签到：2024-09-23 16:00:00
     */
    private String gid;

    private String wxid;

    private String username;

    private Integer salmonEggsToday;

    private Integer salmonEggsTotal;

    private Integer signInDaysKeep;

    private Integer signInDaysTotal;

    private Date lastSignInTime;

    @Override
    public String toString() {
        return "签到成功！\r" +
            "获得了" + salmonEggsToday + "个鲑鱼蛋，总共拥有" + salmonEggsTotal + "个鲑鱼蛋\r" +
            "连续签到：" + signInDaysKeep + "天\r" +
            "总签到：" + signInDaysTotal + "天\r" +
            "签到时间：" + DateUtil.format(lastSignInTime, "yyyy-MM-dd HH:mm:ss");
    }
}
