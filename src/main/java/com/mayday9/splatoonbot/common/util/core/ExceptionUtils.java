package com.mayday9.splatoonbot.common.util.core;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

    public static String getExceptionMessage(Throwable ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        String errorMessage = stringWriter.toString();

        if (null != errorMessage && errorMessage.length() > 1000) {
            return errorMessage.substring(0, 1000);
        }
        return errorMessage;
    }
}
