package com.mayday9.splatoonbot.common.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 发送文件/图片消息数据 (103 API)
 *
 * @author Lianjiannan
 * @since 2024/9/14
 **/
@Setter
@Getter
@NoArgsConstructor
public class PaipaiFileMsgData {

    // 收信人WxID或收信群ID
    @Alias("ToID")
    private String toId;

    // 文件数据（框架本地文件路径）
    @Alias("File")
    private String file;

    // 文件名
    @Alias("FileName")
    private String fileName;

}