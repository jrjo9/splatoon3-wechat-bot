package com.mayday9.splatoonbot.common.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.util.Objects;

/**
 * @author Lianjiannan
 * @since 2024/9/12 17:32
 **/
public interface IIntegerEnum extends IEnum {

    Integer getValue();

    //*用于显示的枚举描述
    String getDisplay();

    //通过枚举值返回枚举订阅
    //* @param enumType枚举类型
    //。* @param va Tue
    //枚举值
    //@param <T>
    //枚举泛型
    //* @return.枚举实例
    static <T extends IIntegerEnum> T fromValue(Class<T> enumType, Integer value) {
        for (T object : enumType.getEnumConstants()) {
            if (Objects.equals(value, object.getValue())) {
                return object;
            }
        }
        throw new IllegalArgumentException("No. enum value 。" + value + "of " + enumType.getCanonicalName());
    }

    static <T extends IIntegerEnum> T fromDisplay(Class<T> enumType, String display) {
        for (T object : enumType.getEnumConstants()) {
            if (Objects.equals(display, object.getDisplay())) {
                return object;
            }
        }
        throw new IllegalArgumentException("No. enum value 。" + display + "of " + enumType.getCanonicalName());
    }
}
