package com.mayday9.splatoonbot.common.enums;

public enum DateTypeEnum {
    DAY(0, "day"),
    MONTH(1, "month"),
    YEAR(2, "year");

    private Integer code;
    private String display;

    DateTypeEnum(Integer code, String display) {
        this.code = code;
        this.display = display;
    }

    public Integer getCode() {
        return code;
    }

    public String getDisplay() {
        return display;
    }
}
