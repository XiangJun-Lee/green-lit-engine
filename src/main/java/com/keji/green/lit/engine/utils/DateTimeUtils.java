package com.keji.green.lit.engine.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间工具类
 * 提供常用的日期时间转换、计算和格式化方法
 */
public class DateTimeUtils {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * 获取当前时间
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 日期转字符串
     */
    public static String formatDate(Date date) {
        return format(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * 日期时间转字符串
     */
    public static String formatDateTime(Date date) {
        return format(date, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * 时间转字符串
     */
    public static String formatTime(Date date) {
        return format(date, DEFAULT_TIME_FORMAT);
    }

    /**
     * 自定义格式的日期转字符串
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 字符串转日期
     */
    public static Date parseDate(String dateStr) {
        return parse(dateStr, DEFAULT_DATE_FORMAT);
    }

    /**
     * 字符串转日期时间
     */
    public static Date parseDateTime(String dateTimeStr) {
        return parse(dateTimeStr, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * 自定义格式的字符串转日期
     */
    public static Date parse(String dateStr, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr, e);
        }
    }

    /**
     * 计算两个日期之间的天数
     */
    public static long daysBetween(Date startDate, Date endDate) {
        long diffInMillis = endDate.getTime() - startDate.getTime();
        return diffInMillis / (1000 * 60 * 60 * 24);
    }

    /**
     * 计算两个日期之间的小时数
     */
    public static long hoursBetween(Date startDate, Date endDate) {
        long diffInMillis = endDate.getTime() - startDate.getTime();
        return diffInMillis / (1000 * 60 * 60);
    }

    /**
     * 计算两个日期之间的分钟数
     */
    public static long minutesBetween(Date startDate, Date endDate) {
        long diffInMillis = endDate.getTime() - startDate.getTime();
        return diffInMillis / (1000 * 60);
    }

    /**
     * 日期加减天数
     */
    public static Date plusDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     * 日期加减小时
     */
    public static Date plusHours(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    /**
     * 日期加减分钟
     */
    public static Date plusMinutes(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    /**
     * 获取指定日期的开始时间（00:00:00）
     */
    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取指定日期的结束时间（23:59:59.999）
     */
    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 判断日期是否在指定范围内
     */
    public static boolean isDateInRange(Date date, Date startDate, Date endDate) {
        return !date.before(startDate) && !date.after(endDate);
    }
    public static Date addMinute(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }
    public static Date getDateFromString(String src, String pattern) {
        SimpleDateFormat f = new SimpleDateFormat(pattern);
        try {
            return f.parse(src);
        } catch (ParseException e) {
            return null;
        }
    }

} 