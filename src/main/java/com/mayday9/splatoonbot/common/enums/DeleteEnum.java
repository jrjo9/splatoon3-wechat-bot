package com.mayday9.splatoonbot.common.enums;

public enum DeleteEnum implements IIntegerEnum {
    YES(1, "是"),
    NO(0, "否");

    private Integer value;
    private String display;

    DeleteEnum(Integer value, String display) {
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

    public static DeleteEnum fromValue(Integer value) {
        return IIntegerEnum.fromValue(DeleteEnum.class, value);
    }
}
