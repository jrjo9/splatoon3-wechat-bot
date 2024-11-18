package com.mayday9.splatoonbot.common.util;

import com.mayday9.splatoonbot.common.enums.DateTypeEnum;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

//时间工具类
public class DateTimeUtils {

    //时间格式化yyyy-MM-dd
    public static String dateDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    //时间格式化yyyy-MM-dd HH:mm:ss
    public static String dateTimeDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    //long毫秒转成localdatetime
    public static LocalDateTime formatTime(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());
    }

    //localdatetime转成long毫秒
    public static long toTime(LocalDateTime dateTime) {
        return dateTime.toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
    }

    //字符串转localdatetime
    public static LocalDateTime toLocalDateTime(String str) {
        if (!StringUtils.hasText(str)) {
            return null;
        }
        if (str.contains("T")) {
            if (str.contains("+")) {
                //1 2017-12-22708:09: 15.9467651+08:00
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                return LocalDateTime.parse(str, formatter);
            } else {
                //1/2018- 11-29T07:09:51.775Z
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                return LocalDateTime.parse(str, formatter);
            }
        }
        str = str.replaceAll("[-/\\._: ]", "");
        if (str.length() == 8) {
            LocalDate date = toLocalDate(str);
            return date == null ? null : date.atTime(0, 0);

        } else if (str.length() == 14) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" yyyyMMddHHmmss");
            return LocalDateTime.parse(str, formatter);
        } else if (str.length() == 17) {
            //带亳秒的没点号会出错，手动加点号
            str = str.substring(0, 14) + "." + str.substring(14);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS");
            return LocalDateTime.parse(str, formatter);
        }
        throw new IllegalArgumentException(str + " could : not : parse to LocalDateTime");
    }

    private static final List<String> DATE_FORMARTS = new ArrayList<>(4);

    static {
        DATE_FORMARTS.add("yyyy-MM");
        DATE_FORMARTS.add("yyyy-MM-dd");
        DATE_FORMARTS.add("yyyy-MM-dd hh:mm");
        DATE_FORMARTS.add("yyyy-MM-dd hh:mm:ss");
    }

    public static Date convert(String source) {
        String value = source.trim();
        if ("".equals(value)) {
            return null;
        }
        if (source.matches("^\\d{4}-\\d{1,2}$")) {
            return parseDate(source, DATE_FORMARTS.get(0));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return parseDate(source, DATE_FORMARTS.get(1));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
            return parseDate(source, DATE_FORMARTS.get(2));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDate(source, DATE_FORMARTS.get(3));
        } else {
            throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
        }
    }

    /**
     * 格式化日期
     *
     * @param dateStr String 字符型日期
     * @param format  String 格式
     * @return Date 日期
     */
    public static Date parseDate(String dateStr, String format) {
        Date date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {
            System.out.println(String.format("日期%s转换%s错误", dateStr, format));
        }
        return date;
    }

    public static Date toDate(String str) {
        return convert(str);
    }

    public static LocalDate toLocalDate(String str) {
        if (!StringUtils.hasText(str)) {
            return null;
        }
        if (str.length() >= 8) {
            str = str.replaceAll("[-/\\._]", "");
        }
        if (str.length() == 6 && !str.startsWith("20")) {
            str = "20" + str;
        }
        if (str.length() == 8) {
            return LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyyMMdd"));

        }
        throw new IllegalArgumentException(str + " could not. parse to LocalDat");
    }


    // *字符串转为LocalTime
    public static LocalTime toLocalTime(String str) {
        if (!StringUtils.hasText(str)) {
            return null;
        } else {
            str = str.trim();
        }
        if (str.length() == 5) {
            return LocalTime.parse(str + ":00", DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        if (str.length() == 8) {
            return LocalTime.parse(str, DateTimeFormatter.ofPattern("HH: mm:ss"));
        }
        throw new IllegalArgumentException(str + " could not. parse to LocalDat");
    }

    //* @descr iption根据日期类型格式化loca IDateTime
    // @author huang wen jie
    // @param: . dateType
//*. @param: 1oca ÍDateTime
//*. @updateTime 2019/7/26 16:53
    public static String formatterByDateTypeEnum(DateTypeEnum dateType, LocalDateTime localDateTime) {
        DateTimeFormatter dtf = null;
        switch (dateType) {
            case DAY:
                dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                break;
            case MONTH:
                dtf = DateTimeFormatter.ofPattern("yyyy-MM");
                break;
            case YEAR:
                dtf = DateTimeFormatter.ofPattern("yyyy");
                break;
        }
        return localDateTime.format(dtf);
    }

    //@description根据日期类型格式化7oca IDateTime后缀
    //@author huang wen jie
    //@param: da teType
    //@param: . loca ÍDateTime
    //@updateTime 2019/7/26 19:05
    public static LocalDateTime beginTimeSuffix(DateTypeEnum dateType, LocalDateTime localDateTime) {
        String dateTime = formatterByDateTypeEnum(dateType, localDateTime);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        switch (dateType) {
            case DAY:
                return LocalDateTime.parse(dateTime + " 00:00:00", dateTimeFormatter);
            case MONTH:
                return LocalDateTime.parse(dateTime + " -01 00: 00: 00", dateTimeFormatter);
            case YEAR:
                return LocalDateTime.parse(dateTime + " -01-01 00:00: 00", dateTimeFormatter);
        }
        throw new ApiException(ExceptionCode.ParamIllegal);
    }

    //f,
//@descr iption根据日期类型格式化loca IDateTime后缀
//* @author huang wen jie
//@param: dateType
//* @param: 1oca iDateTime
//*. @upda teTime 201917/26 19:056
    public static LocalDateTime endTimeSuffix(DateTypeEnum dateType, LocalDateTime localDateTime) {
        String dateTime = formatterByDateTypeEnum(dateType, localDateTime);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        switch (dateType) {
            case DAY:
                return LocalDateTime.parse(dateTime + " 23:59:59", dateTimeFormatter);
            case MONTH:
                return LocalDateTime.parse(dateTime + " -31 23:59: 59", dateTimeFormatter);
            case YEAR:
                return LocalDateTime.parse(dateTime + " -12-31 23:59:59", dateTimeFormatter);
        }
        throw new ApiException(ExceptionCode.ParamIllegal);
    }

    //**
    //* @descr iption获取两个日期之间所有的日期: beginTimestr ing必须在endTimestring之前
    //* @author huang wen
//nre
    //@param: dateType 5T : DAY, MONTH. YEAR
// @param: beginTime
// @param: endT ime
    // * @updateTime 2019/7/29 11:49
    public static List<String> between(DateTypeEnum dateType, LocalDateTime beginTime, LocalDateTime endTime) {
        if (beginTime.isAfter(endTime)) {
            throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "开始时间必须小于结束时间 ! ");
        }
        List<String> betweenBeginEndDateList = new LinkedList<>();
        String beginTimeString = formatterByDateTypeEnum(dateType, beginTime);
        String endTimeString = formatterByDateTypeEnum(dateType, endTime);
        betweenBeginEndDateList.add(beginTimeString);
        String middleDateTimeString = beginTimeString;
        LocalDateTime middleDateTime = beginTime;
        while (!middleDateTimeString.equals(endTimeString)) {
            middleDateTime = beginTimeSuffix(dateType, middleDateTime);
            LocalDateTime plusDaysResult = null;
            switch (dateType) {
                case DAY:
                    plusDaysResult = middleDateTime.plusDays(1L);
                    break;
                case MONTH:
                    plusDaysResult = middleDateTime.plusMonths(1L);
                    break;
                case YEAR:
                    plusDaysResult = middleDateTime.plusYears(1L);
                    break;
            }
            middleDateTimeString = DateTimeUtils.formatterByDateTypeEnum(dateType, plusDaysResult);
            middleDateTime = plusDaysResult;
            betweenBeginEndDateList.add(middleDateTimeString);
        }
        return betweenBeginEndDateList;
    }

    public static void main(String[] args) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.now();
        String localTime = df.format(time);
        LocalDateTime ldt = LocalDateTime.parse("2017-09-20 17:07 :05", df);
        LocalDateTime ldt2 = LocalDateTime.parse("2018-10-28 17 :07:05", df);
        List<String> betweenBeginEndDateList = between(DateTypeEnum.YEAR, ldt, ldt2);
        betweenBeginEndDateList.forEach(o -> {
            System.out.println(o);
        });
    }
}
