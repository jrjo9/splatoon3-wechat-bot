package com.mayday9.splatoonbot.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lianjiannan
 * @since 2024/10/10 11:20
 **/
@Target({ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface AuthWxMsg {
}
