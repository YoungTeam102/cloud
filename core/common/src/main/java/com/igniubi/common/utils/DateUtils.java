package com.igniubi.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 日期工具类
 * <p>
 *
 * @author ：迈克擂
 * @version : 1.0.0
 * @date ：2018/12/5
 */
public class DateUtils {
    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);

    public static final String FORMAT_NORMAL = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_HMS = "HH:mm:ss";
    public static final String FORMAT_HM = "HH:mm";
    public static final String FORMAT_H = "HH";

    /**
     * 获取当前时间
     *
     * @param format 日期格式
     * @return String
     * @author ：迈克擂
     * @date ：2018/12/5
     */
    public static String getCurrentTime(String format) {
        String formatDate = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format == null ? "yyyy-MM-dd HH:mm:ss" : format);
            formatDate = dateFormat.format(new Date());
        } catch (Exception e) {
            log.error("日期格式转化异常", e);
            return null;
        }
        return formatDate;
    }

    /**
     * 字符串转日期
     * <p>
     *
     * @param dateStr 待转换的日期字符串
     * @param format  待转换的日期字符串格式
     * @return Date
     * @author ：迈克擂
     * @date ：2018/12/5
     */
    public static Date getDateByStr(String dateStr, String format) {
        try {
            return new SimpleDateFormat(format).parse(dateStr);
        } catch (ParseException e) {
            log.error("字符串转日期异常：", e);
            return null;
        }
    }

    /**
     * 日期加减天数
     * <p>
     *
     * @param date 日期
     * @param days 加减天数 正为加，负为减
     * @return Date
     * @author ：迈克擂
     * @date ：2018/12/5
     */
    public static Date plusDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + days);
        return calendar.getTime();
    }

    /**
     * 日期加减天数
     * <p>
     *
     * @param date       日期
     * @param days       加减天数 正为加，负为减
     * @param dateFormat 格式化 默认为 yyyy-MM-dd HH:mm:ss
     * @return String
     * @throws
     * @author ：迈克擂
     * @date ：2018/12/5
     */
    public static String plusDate(Date date, int days, String dateFormat) {
        if (StringUtils.isBlank(dateFormat)) {
            dateFormat = FORMAT_NORMAL;
        }
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + days);
        return df.format(calendar.getTime());
    }

}
