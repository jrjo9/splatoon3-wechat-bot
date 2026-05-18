package com.mayday9.splatoonbot.netty.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WsMsgType {

    /**
     * 消息类型值
     */
    @AliasFor("value")
    String type() default "";

    /**
     * 消息类型值，与type等效
     */
    @AliasFor("type")
    String value() default "";

    /**
     * 描述信息
     */
    String desc() default "";
}