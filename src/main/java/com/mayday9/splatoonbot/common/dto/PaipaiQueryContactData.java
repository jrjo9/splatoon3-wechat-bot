package com.mayday9.splatoonbot.common.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 查询联系人数据
 *
 * @author Lianjiannan
 * @since 2024/9/14
 **/
@Setter
@Getter
@NoArgsConstructor
public class PaipaiQueryContactData {

    // 联系人WxID或群ID
    @Alias("ToID")
    private String toId;

    // 群ID（查询群内昵称时使用）
    @Alias("RoomID")
    private String roomId;

}