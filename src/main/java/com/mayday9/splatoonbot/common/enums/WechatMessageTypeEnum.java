package com.mayday9.splatoonbot.common.enums;

/**
 * @author Lianjiannan
 * @since 2024/9/12 17:31
 **/
public enum WechatMessageTypeEnum implements IIntegerEnum {

    TEXT(1, "消息"),
    IMAGE(2, "图片"),
    ;

    private Integer value;
    private String display;

    WechatMessageTypeEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDisplay() {

        return display;
    }

    public static WechatMessageTypeEnum fromValue(Integer value) {
        return IIntegerEnum.fromValue(WechatMessageTypeEnum.class, value);
    }
}
