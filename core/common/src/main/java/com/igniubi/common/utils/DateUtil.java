package com.igniubi.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {

    public static final String yyyy_MM_dd = "yyyy-MM-dd";

    public static final String yyyy_MM_dd_HH_mm_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String yyyyMMdd_HH_mm_SS = "yyyy.MM.dd HH:mm:ss";

    /**
     * 将通过getTime获得的long型的时间，转换成format类型的时间，如"2015-08-09"
     *
     * @param time   long型的时间戳
     * @param format 需要转化的类型 “yyyy-MM-dd”
     * @return String "2015-08-09"
     */
    public static String long2DateStr(long time, String format) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateStr = sdf.format(date);
        return dateStr;
    }

    /**
     * 获取当前时间, 并且将当前时间转换称INT类型
     *
     * @return 当前时间的INT类型
     */
    public static int getCurrentTimeSeconds() {
        long longTime = new Date().getTime();
        return (int) (longTime / 1000);
    }


    /**
     * 获取 格林威治时间 1970 年 1 月 1 日 00:00:00）到当前时间的秒数
     *
     * @return
     * @throws ParseException
     */
    public static long getDateOfSenconds() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.setTime(new Date());
        return c.getTimeInMillis() / 1000;
    }

    /**
     * 将通过getTime获得的int型的时间，转换成format类型的时间，如"2015-08-09"
     *
     * @param time   int型的时间戳---这里的int时间是秒数
     * @param format 需要转化的类型 “yyyy-MM-dd”
     * @return String "2015-08-09"
     */
    public static String int2DateStr(long time, String format) {
        Date date = new Date(time * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateStr = sdf.format(date);
        return dateStr;
    }


    /**
     * 将通过getTime获得的Date型的时间
     *
     * @param time long型的时间戳
     * @return java.util.Date
     */
    public static Date long2Date(long time) {
        return new Date(time);
    }

    static Logger log = LoggerFactory.getLogger(DateUtil.class);
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * 按照格式，取得当前时间
     *
     * @param str
     * @return
     */
    public static String getToday(String str) {
        return dateToString(new Date(), str);
    }

    /**
     * 获取今天的日期时间（yyyy-MM-dd HH:mm:ss）
     *
     * @return
     */
    public static String getcurrentDateTime() {
        return getToday(DATE_TIME_FORMAT);
    }

    /**
     * 获取今天的日期（yyyy-MM-dd）
     *
     * @return
     */
    public static String getcurrentDate() {
        return getToday(DATE_FORMAT);
    }

    /**
     * 获取今天的时间（HH:mm:ss）
     *
     * @return
     */
    public static String getcurrentTime() {
        return getToday(TIME_FORMAT);
    }

    /**
     * 对日期进行加减
     *
     * @param date             被转换的日期
     * @param type             转换类型(y-年,M-月,d-日, H-小时, m-分钟, s-秒)
     * @param offset           转换的单位
     * @param simpleDateFormat 日期格式
     * @return
     */
    public static String dateAdd(String date, String type, int offset, String simpleDateFormat) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        if (StringUtils.isEmpty(type)) {
            return date;
        }
        if (offset == 0) {
            return date;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(stringToDate(date, simpleDateFormat));
        if (type.equals("y")) {
            cal.add(Calendar.YEAR, offset);
        } else if (type.equals("M")) {
            cal.add(Calendar.MONTH, offset);
        } else if (type.equals("d")) {
            cal.add(Calendar.DATE, offset);
        } else if (type.equals("H")) {
            cal.add(Calendar.HOUR, offset);
        } else if (type.equals("m")) {
            cal.add(Calendar.MINUTE, offset);
        } else if (type.equals("s")) {
            cal.add(Calendar.SECOND, offset);
        }
        return dateToString(cal.getTime(), simpleDateFormat);
    }

    /**
     * ͳDate型日期转换为String型
     *
     * @param date
     * @param simpleDateFormat
     * @return
     */
    public static String dateToString(Date date, String simpleDateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat);
        if (date == null) {
            return null;
        } else {
            return format.format(date);
        }
    }

    /**
     * String型日期转换为Date型
     *
     * @param date
     * @param simpleDateFormat
     * @return
     */
    public static Date stringToDate(String date, String simpleDateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat);
        Date d = null;
        if (StringUtils.isEmpty(date)) {
            return null;
        } else {
            try {
                d = format.parse(date);
            } catch (ParseException e) {
                log.error("stringToDate: param is {}, error is{}", date, e.getMessage());
            }
            return d;
        }
    }

    /**
     * 日期格式转换
     *
     * @param date
     * @param from
     * @param to
     * @return
     */
    public static String formatDate(String date, String from, String to) {
        return dateToString(stringToDate(date, from), to);
    }

    /**
     * 获取当前时间距离输入时间的毫秒数。如果输入时间小于当前时间，则取第二天的同一时间
     * 时间格式（HH:mm:ss）
     *
     * @param time
     * @return
     */
    public static long getTimeDiff(String time) {
        if (StringUtils.isEmpty(time)) {
            return 0;
        }
        Date d1 = new Date();
        Date d2 = null;
        String nowDate = dateToString(d1, "yyyy-MM-dd");
        String nowTime = dateToString(d1, "HH:mm:ss");
        if (time.compareTo(nowTime) >= 0) {
            d2 = stringToDate(nowDate + " " + time, "yyyy-MM-dd HH:mm:ss");
        } else {
            d2 = stringToDate(dateAdd(nowDate, "d", 1, "yyyy-MM-dd") + " " + time, "yyyy-MM-dd HH:mm:ss");
        }
        return d2.getTime() - d1.getTime();
    }

    /**
     * 从start到end的毫秒数
     *
     * @param start
     * @param end
     * @param format
     * @return
     * @throws Exception
     */
    public static long getTimeDiff(String start, String end, String format) {
        if (StringUtils.isEmpty(start) || StringUtils.isEmpty(end) || StringUtils.isEmpty(format)) {
            return 0;
        }
        return stringToDate(end, format).getTime() - stringToDate(start, format).getTime();
    }


    /**
     * 根据当前时间距离1970－01-01的秒数，获取当前时间的date类型
     *
     * @param second
     * @return
     */
    public static Date getDateBySecond(int second) {
        long secondLong = (long) second * 1000;
        return new Date(secondLong);
    }

    /**
     * 获取当前年的最后一秒
     *
     * @return
     */
    public static int getCurrentYearLastSec() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return getYearLastSec(year);
    }

    /**
     * 获取当前年的下一年的最后一秒
     *
     * @return
     */
    public static int getNextYearLastSec() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return getYearLastSec(year + 1);
    }

    /**
     * 获取当前年的上一年的最后一秒
     *
     * @return
     */
    public static int getLastYearLastSec() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return getYearLastSec(year - 1);
    }

    /**
     * 根据年份获取年的最后一秒
     *
     * @param year
     * @return
     */
    public static int getYearLastSec(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year + 1, 0, 1, 0, 0, 0);
        Long millsec = calendar.getTimeInMillis() - 1000;
        return (int) (millsec / 1000);
    }

    public static int getCurrentDayLastSecond() {
        String time = dateToString(new Date(), yyyy_MM_dd) + " 23:59:59";
        Date dt = createDateWith(time, yyyy_MM_dd_HH_mm_SS);
        return (int) (dt.getTime() / 1000);
    }

    public static int getCurrentDayBeginSecond() {
        String time = dateToString(new Date(), yyyy_MM_dd) + " 00:00:00";
        Date dt = createDateWith(time, yyyy_MM_dd_HH_mm_SS);
        return (int) (dt.getTime() / 1000);
    }


    // 获得本年最后一天的日期 *
    public static long getNextYearEndTime() {
        String time = getNextYearEnd() + " 23:59:59";
        Date dt = createDateWith(time, yyyy_MM_dd_HH_mm_SS);
        return dt.getTime() / 1000;
    }

    private static Date createDateWith(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date start = null;
        try {
            start = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return start;
    }

    // 获得明年最后一天的日期
    public static String getNextYearEnd() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_dd);

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.YEAR, 1);// 加一个年
        lastDate.set(Calendar.DAY_OF_YEAR, 1);
        lastDate.roll(Calendar.DAY_OF_YEAR, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    public static Date getTomorrowZero(int offsetSecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
        calendar.add(Calendar.SECOND, offsetSecond);
        return calendar.getTime();
    }

    public static Date getDayAfterTomorrowZero(int offsetSecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) + 2, 0, 0, 0);
        calendar.add(Calendar.SECOND, offsetSecond);
        return calendar.getTime();
    }

    public static int getCurrentMonthBeginSecond() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    public static int getCurrentMonthLastSecond() {
        Calendar calendar = Calendar.getInstance();
        // 设置Calendar月份数为下一个月
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        // 设置Calendar日期为下一个月一号
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
//		// 设置Calendar日期减一,为本月末
//		calendar.add(Calendar.DATE, -1);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    /**
     * 去除时间，保留日期
     */
    public static int trimTime(int second) {
        long secondLong = (long) second * 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(secondLong);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    /**
     * 计算 timeA - timeB 相差天数
     *
     * @param timeA 秒
     * @param timeB 秒
     * @return
     */
    public static int getDateDiff(int timeA, int timeB) {
        double diff = (double) Math.abs(trimTime(timeA) - trimTime(timeB)) / (24 * 60 * 60);
        return (int) diff;
    }

    /**
     * 是否是今天
     *
     * @param time
     * @return
     */
    public static boolean isToday(long time) {
        //当前时间
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        //获取今天的日期
        String nowDay = sf.format(now);


        //对比的时间
        String day = sf.format(new Date(time));

        return day.equals(nowDay);

    }

    /**
     * 获取与当天相差n天的时间
     * 当n=-5,5天前的时间
     * 当n=5,5天后的时间
     */
    public static int getSomeDayBeforeInt(int lastday) {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        now.add(Calendar.DAY_OF_YEAR, lastday);
        return (int) (now.getTimeInMillis() / 1000);
    }

    public static void main(String args[]) {

    }
}
