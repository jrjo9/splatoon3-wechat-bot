package com.mayday9.splatoonbot.common.util.core.json;

import com.mayday9.splatoonbot.common.util.core.Convert;
import com.mayday9.splatoonbot.common.util.core.StringUtils;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

public class JSONDateFormat extends DateFormat {
    private static final long serialVersionUID = 1L;

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        toAppendTo.append(Convert.toString(date, Convert.DATE_FORMAT_ISO_WITH_TIMEZONE));
        return toAppendTo;
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        pos.setIndex(source.length());

        if (isContainChar(source, '-')) {
            if (Convert.DATA_FORMAT_DATETIME_SLASH.length() == source.length()) {
                return Convert.toDate(source, Convert.DATA_FORMAT_DATETIME_SLASH);
            } else {
                return Convert.toDate(source, getISOPattern(source));
            }
        }
        if ("MM/dd/yyyyTHH:mm:ss".length() == source.length()) {
            //TODO: remove legacy format
            return Convert.toDate(source, Convert.DATE_FORMAT_DATETIME);
        }

        return Convert.toDate(source, Convert.DATE_FORMAT_DATE);

    }


    public boolean isContainChar(String source, char character) {
        if (!StringUtils.hasText(source)) {
            return false;
        }

        for (char c : source.toCharArray()) {
            if (character == c) {
                return true;
            }
        }
        return false;
    }

    public String getISOPattern(String source) {
        StringBuilder b = new StringBuilder("yyyy-MM-dd'T'HH:mm:ss");
        int precision = 0;
        int state = 0;

        for (int i = "yyyy-MM-ddTHH:mm:ss".length(); i < source.length(); i++) {
            char c = source.charAt(i);

            if (c == '.' && state == 0) {
                state = 1;
            } else if (c == '-' || c == '+' || c == 'Z') {
                if (state > 0) {
                    b.append('.');
                    //support million seconds
                    for (int j = 0; j < precision; j++) {
                        b.append('S');
                    }
                }
                b.append("XXX");
                break;
            } else if (state == 1) {
                precision++;
            }
        }
        return b.toString();
    }

    @Override
    public Object clone() {
        return this;
    }
}
