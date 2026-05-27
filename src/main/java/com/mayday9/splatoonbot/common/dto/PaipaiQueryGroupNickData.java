package com.mayday9.splatoonbot.common.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 查询群内昵称数据
 *
 * @author Lianjiannan
 * @since 2024/9/14
 **/
@Setter
@Getter
@NoArgsConstructor
public class PaipaiQueryGroupNickData {

    // 成员WxID
    @Alias("WxID")
    private String wxId;

    // 群ID
    @Alias("RoomID")
    private String roomId;

}