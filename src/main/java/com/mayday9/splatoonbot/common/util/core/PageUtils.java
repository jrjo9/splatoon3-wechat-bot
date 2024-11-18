package com.mayday9.splatoonbot.common.util.core;

public final class PageUtils {
    public static int getTotalPage(int totalRecords, int pageSize) {
        AssertUtils.assertTrue(pageSize > 0, "pageSize must great than 0,but now is {}", pageSize);
        return totalRecords % pageSize == 0 ? totalRecords / pageSize : totalRecords / pageSize + 1;
    }
}
