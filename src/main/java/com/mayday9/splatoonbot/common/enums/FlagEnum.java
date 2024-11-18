package com.mayday9.splatoonbot.common.enums;

/**
 * 标记位
 */
public enum FlagEnum implements IIntegerEnum {

    NO(0, "否、关闭"),

    YES(1, "是、开启"),
    ;
    private Integer value;
    private String display;

    FlagEnum(Integer value, String display) {
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

    public static FlagEnum fromValue(Integer value) {
        return IIntegerEnum.fromValue(FlagEnum.class, value);
    }
}
