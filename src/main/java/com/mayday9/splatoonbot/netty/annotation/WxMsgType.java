package com.mayday9.splatoonbot.netty.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lianjiannan
 * @since 2024/9/12 17:23
 **/
@Target({ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface WxMsgType {

    public String desc() default "";

    public String value() default "";

}
