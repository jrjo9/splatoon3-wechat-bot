package com.mayday9.splatoonbot.netty.strategy;

import com.mayday9.splatoonbot.common.dto.WechatMessage;

/**
 * @author Lianjiannan
 * @since 2024/9/10 17:24
 **/
public abstract class PaipaiWxMsgStrategy {

    /**
     * 业务
     *
     * @param wechatMessage 消息
     * @return void
     */
    public final void doStrategyBusiness(WechatMessage wechatMessage) throws Exception {
        // 业务
        doBusiness(wechatMessage);
    }


    /**
     * 根据转换后的业务实体执行业务代码
     *
     * @param wechatMessage 业务实体
     * @return void
     */
    public abstract void doBusiness(WechatMessage wechatMessage) throws Exception;
}
