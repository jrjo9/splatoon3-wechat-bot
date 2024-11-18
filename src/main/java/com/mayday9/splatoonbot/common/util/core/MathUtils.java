package com.mayday9.splatoonbot.common.util.core;


public final class MathUtils {
    public static double round(double number, int digits) {
        double scale = Math.pow(10, digits);
        return Math.round(number * scale) / scale;
    }

    private MathUtils() {
    }
}
