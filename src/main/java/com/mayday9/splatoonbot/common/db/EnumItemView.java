package com.mayday9.splatoonbot.common.db;

public class EnumItemView {

    private Integer value;
    private String display;

    public EnumItemView() {
    }

    public EnumItemView(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
