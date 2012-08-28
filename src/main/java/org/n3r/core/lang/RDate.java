package org.n3r.core.lang;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.n3r.core.date4j.DateTime;

/**
 * 时间日期相关的操作小函数。
 * @author Bingoo
 *
 */
public class RDate {
    /** 
     * 缺省的日期显示格式： yyyy-MM-dd 
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /** 
     * 缺省的日期时间显示格式：yyyy-MM-dd HH:mm:ss 
     */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** 
     * 得到系统当前日期时间 
     * @return 当前日期时间 
     */
    public static Date getNow() {
        return Calendar.getInstance().getTime();
    }

    /** 
     * 得到用缺省方式格式化的当前日期 
     * @return 当前日期 
     */
    public static String toDateStr() {
        return toStr(Calendar.getInstance().getTime(), DEFAULT_DATE_FORMAT);
    }

    public static String toDateStr(String pattern) {
        return toStr(Calendar.getInstance().getTime(), pattern);
    }

    public static String toDateStr(Date date) {
        return toStr(date, DEFAULT_DATE_FORMAT);
    }

    /** 
     * 得到用缺省方式格式化的当前日期及时间 
     * @return 当前日期及时间 
     */
    public static String toDateTimeStr() {
        return toStr(Calendar.getInstance().getTime(), DEFAULT_DATETIME_FORMAT);
    }

    public static String toDateTimeStr(Date date) {
        return toStr(date, DEFAULT_DATETIME_FORMAT);
    }

    /** 
     * 得到系统当前日期及时间，并用指定的方式格式化 
     * @param pattern 显示格式 
     * @return 当前日期及时间 
     */
    public static String toDateTimeStr(String pattern) {
        Date datetime = Calendar.getInstance().getTime();
        return toStr(datetime, pattern);
    }

    /** 
     * 得到用指定方式格式化的日期 
     * @param date 需要进行格式化的日期 
     * @param pattern 显示格式 
     * @return 日期时间字符串 
     */
    public static String toStr(Date date, String pattern) {
        if (null == pattern || "".equals(pattern)) {
            pattern = DEFAULT_DATETIME_FORMAT;
        }

        return new SimpleDateFormat(pattern).format(date);
    }

    /** 
     * 得到当前年份 . 
     * @return 当前年份 
     */
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 得到指定日期的年份。
     * 
     * @param date Date to get year from.
     * @return Year of given date.
     */
    public static int getYear(Date date) {
        return getCalendar(date).get(Calendar.YEAR);
    }

    /** 
     * 得到当前月份(1-12)。
     * @return 当前月份 
     */
    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * 得到指定日期的月份(1-12).
     * 
     * @param date Date to get mont from.
     * @return Month (1-12) for given date.
     */
    public static int getMonth(Date date) {
        return getCalendar(date).get(Calendar.MONTH) + 1;
    }

    /** 
     * 得到当前日 
     * @return 当前日 
     */
    public static int getDay() {
        return Calendar.getInstance().get(Calendar.DATE);
    }

    /** 
     * 得到指定日期的天 
     * @return 当前日 
     */
    public static int getDay(Date date) {
        return getCalendar(date).get(Calendar.DATE);
    }

    /** 
     * 得到当前小时
     * @return 当前日 
     */
    public static int getHourOfDay() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    /** 
     * 得到当前小时 
     * @return 当前日 
     */
    public static int getHourOfDay(Date date) {
        return getCalendar(date).get(Calendar.HOUR_OF_DAY);
    }

    /** 
     * 得到当前分
     * @return 当前日 
     */
    public static int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    /** 
     * 得到当前小时 
     * @return 当前日 
     */
    public static int getMinute(Date date) {
        return getCalendar(date).get(Calendar.MINUTE);
    }

    /** 
     * 得到当前秒
     * @return 当前日 
     */
    public static int getSecond() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }

    /** 
     * 得到当前小秒
     * @return 当前日 
     */
    public static int getSecond(Date date) {
        return getCalendar(date).get(Calendar.SECOND);
    }

    /**
     * 得到从零点开始当天经过多少秒。
     * @return
     */
    public static int getSecondsFromMidnight() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60
                + calendar.get(Calendar.MINUTE) * 60
                + calendar.get(Calendar.SECOND);

    }

    /**
     * 得到当月第一天。
     * @return
     */
    public static Date getBeginOfMonth() {
        return getBeginOfMonth(new Date());
    }

    /**
     * 得到当月第一天。
     * @param date 当月
     * @return
     */
    public static Date getBeginOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.clear(Calendar.HOUR);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTime();
    }

    /**
     * 得到当月最后一天所在的日期。
     * @return
     */
    public static Date getEndOfMonth() {
        return getEndOfMonth(new Date());
    }

    /**
     * 得到当月最后一天所在的日期。
     * @param date 当月
     * @return
     */
    public static Date getEndOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        final int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    /**
     * 得到当月最后一天。
     * @return
     */
    public static int getLastDayOfMonth() {
        return getLastDayOfMonth(new Date());
    }

    /**
     * 得到当月最后一天。
     * @param date 当月
     * @return
     */
    public static int getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前星期几
     * @return Calendar.SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, and SATURDAY.
     */
    public static int getDayOfWeek() {
        return getCalendar().get(Calendar.DAY_OF_WEEK);//今天是星期几
    }

    /**
     * 获取当前星期几
     * @return Calendar.SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, and SATURDAY.
     */
    public static int getDayOfWeek(Date date) {
        return getCalendar(date).get(Calendar.DAY_OF_WEEK);//今天是星期几
    }

    /**
     * 得到下个月的第一天日期。
     * @param date
     * @return
     */
    public static Date getFirstDayOfNextMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        removeTimeFields(calendar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 获得周一的日期 
     * @param date
     * @return
     */
    public static Date getMonday(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return c.getTime();
    }

    /**
     * 获得周五的日期.
     * @param date
     * @return
     */
    public static Date getFriday(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        return c.getTime();
    }

    /** 
     * 取得当前日期以后若干天的日期。如果要得到以前的日期，参数用负数。 
     * 例如要得到上星期同一天的日期，参数则为-7 
     * @param days 增加的日期数 
     * @return 增加以后的日期 
     */
    public static Date addDays(int days) {
        return add(getNow(), days, Calendar.DATE);
    }

    /** 
     * 取得指定日期以后若干天的日期。如果要得到以前的日期，参数用负数。 
     * @param date 基准日期 
     * @param days 增加的日期数 
     * @return 增加以后的日期 
     */
    public static Date addDays(Date date, int days) {
        return add(date, days, Calendar.DATE);
    }

    /** 
     * 取得当前日期以后某月的日期。如果要得到以前月份的日期，参数用负数。 
     * @param months 增加的月份数 
     * @return 增加以后的日期 
     */
    public static Date addMonths(int months) {
        return add(getNow(), months, Calendar.MONTH);
    }

    /** 
     * 取得指定日期以后某月的日期。如果要得到以前月份的日期，参数用负数。 
     * 注意，可能不是同一日子，例如2003-1-31加上一个月是2003-2-28 
     * @param date 基准日期 
     * @param months 增加的月份数 
    
     * @return 增加以后的日期 
     */
    public static Date addMonths(Date date, int months) {
        return add(date, months, Calendar.MONTH);
    }

    /** 
     * 为指定日期增加相应的天数或月数 
     * @param date 基准日期 
     * @param amount 增加的数量 
     * @param field 增加的单位，年，月或者日 
     * @return 增加以后的日期 
     */
    public static Date add(Date date, int amount, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    /** 
     * 计算两个日期相差天数。 
     * 用第一个日期减去第二个。如果前一个日期小于后一个日期，则返回负数 
     * @param one 第一个日期数，作为基准 
     * @param two 第二个日期数，作为比较 
     * @return 两个日期相差天数 
     */
    public static long diffDays(Date one, Date two) {
        return (one.getTime() - two.getTime()) / (24 * 60 * 60 * 1000);
    }

    /** 
     * 计算两个日期相差月份数 
     * 如果前一个日期小于后一个日期，则返回负数 
     * @param one 第一个日期数，作为基准 
     * @param two 第二个日期数，作为比较 
     * @return 两个日期相差月份数 
     */
    public static int diffMonths(Date one, Date two) {
        Calendar calendar = Calendar.getInstance();
        //得到第一个日期的年分和月份数  
        calendar.setTime(one);
        int yearOne = calendar.get(Calendar.YEAR);
        int monthOne = calendar.get(Calendar.MONDAY);
        //得到第二个日期的年份和月份  
        calendar.setTime(two);
        int yearTwo = calendar.get(Calendar.YEAR);
        int monthTwo = calendar.get(Calendar.MONDAY);
        return (yearOne - yearTwo) * 12 + (monthOne - monthTwo);
    }

    /** 
     * 将一个字符串用给定的格式转换为日期类型。<br> 
     * 注意：如果返回null，则表示解析失败 
     * @param datestr 需要解析的日期字符串 
     * @param pattern 日期字符串的格式，默认为“yyyy-MM-dd”的形式 
     * @return 解析后的日期 
     */
    public static Date parse(String datestr, String pattern) {
        Date date = null;
        if (null == pattern || "".equals(pattern)) {
            pattern = DEFAULT_DATE_FORMAT;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            date = dateFormat.parse(datestr);
        }
        catch (ParseException e) {}
        return date;
    }

    /**
     * 字符串转换为日期。
     * <P>All of the following dates can be parsed by this class to make a <tt>DateTime</tt> :
     * <ul>
     * <li><tt>2009-12-31 00:00:00.123456789</tt>
     * <li><tt>2009-12-31T00:00:00.123456789</tt>
     * <li><tt>2009-12-31 00:00:00.12345678</tt>
     * <li><tt>2009-12-31 00:00:00.1234567</tt>
     * <li><tt>2009-12-31 00:00:00.123456</tt>
     * <li><tt>2009-12-31 23:59:59.12345</tt>
     * <li><tt>2009-01-31 16:01:01.1234</tt>
     * <li><tt>2009-01-01 16:59:00.123</tt>
     * <li><tt>2009-01-01 16:00:01.12</tt>
     * <li><tt>2009-02-28 16:25:17.1</tt>
     * <li><tt>2009-01-01 00:01:01</tt>
     * <li><tt>2009-01-01T00:01:01</tt>
     * <li><tt>2009-01-01 16:01</tt>
     * <li><tt>2009-01-01 16</tt>
     * <li><tt>2009-01-01</tt>
     * <li><tt>2009-01</tt>
     * <li><tt>2009</tt>
     * <li><tt>0009</tt>
     * <li><tt>9</tt>
     * <li><tt>00:00:00.123456789</tt>
     * <li><tt>00:00:00.12345678</tt>
     * <li><tt>00:00:00.1234567</tt>
     * <li><tt>00:00:00.123456</tt>
     * <li><tt>23:59:59.12345</tt>
     * <li><tt>01:59:59.1234</tt>
     * <li><tt>23:01:59.123</tt>
     * <li><tt>00:00:00.12</tt>
     * <li><tt>00:59:59.1</tt>
     * <li><tt>23:59:00</tt>
     * <li><tt>23:00:10</tt>
     * <li><tt>00:59</tt>
     * </ul>
     * @param strDate
     * @return
     */
    public static Date parse(String strDate) {
        DateTime dt = new DateTime(strDate);
        Calendar cal = Calendar.getInstance();
        cal.set(dt.getYear() != null ? dt.getYear() : 1970,
                dt.getMonth() != null ? dt.getMonth() - 1 : 0,
                dt.getDay() != null ? dt.getDay() : 1,
                dt.getHour() != null ? dt.getHour() : 0,
                dt.getMinute() != null ? dt.getMinute() : 0,
                dt.getSecond() != null ? dt.getSecond() : 0);
        return cal.getTime();
    }

    /**
     * 将long类型的转为Date对象.
     */
    public static Date toDate(long aDate) {
        return new Date(aDate);
    }

    public static void removeTimeFields(Calendar date) {
        date.clear(Calendar.HOUR);
        date.clear(Calendar.HOUR_OF_DAY);
        date.clear(Calendar.MINUTE);
        date.clear(Calendar.SECOND);
        date.clear(Calendar.MILLISECOND);
    }

    /**   
     * 是否闰年   
     * @param year 年   
     * @return    
     */
    public static boolean isLeapYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return isLeapYear(calendar.get(Calendar.YEAR));
    }

    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * 判断两个日期是否在同一周   
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameWeek(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) return true;
        }
        else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周   
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) return true;
        }
        else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) return true;
        }

        return false;
    }

    /**
     * Return age in years for a given birth date.
     * 
     * @param dateOfBirth Date of birth.
     * @return Age in years.
     */
    public static int getAgeInYears(Date dateOfBirth) {
        return getYear() - getYear(dateOfBirth);
    }

    /**
     * Get calendar for current date.
     * 
     * @return new Calendar
     */
    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }

    /**
     * 
     * Get calendar for given date.
     * 
     * @param date Date to use with calendar
     * @return new Calendar set to given date.
     */
    public static Calendar getCalendar(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Get new date with given year,month and day.
     * 
     * @param year Year as integer (ex; 1965)
     * @param month Month as integer (0-11)
     * @param day Day as integer (First day is 1)
     * @return new Date.
     */
    public static java.util.Date getDate(int year, int month, int day) {
        final Calendar c = getCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }

    /**
     * Return current time as java.sql.Date
     * 
     * @return current time as java.sql.Date
     */
    public static java.sql.Date toSqlDate() {
        return new java.sql.Date(System.currentTimeMillis());
    }

    /**
     * Return current time as java.sql.Timestamp
     * 
     * @return current time as java.sql.Timestamp
     */
    public static java.sql.Timestamp getSqlTimestamp() {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }

    /**
     * Get current time in milliseconds.
     * 
     * @return Current time in milliseconds
     */
    public static long getTime() {
        return System.currentTimeMillis();
    }

    /**
     * Test if date is in the future.
     * 
     * @param date Date to test.
     * @return True if date is in the future or today.
     */
    public static boolean isFutureDate(Date date) {
        return date.getTime() > getTime() || isToday(date);
    }

    /**
     * Test if date is in the past.
     * 
     * @param date Date to test.
     * @return True if date is in the past or today.
     */
    public static boolean isPastDate(Date date) {
        return date.getTime() < getTime() || isToday(date);
    }

    /**
     * Test if date is same year,month and day as today.
     * 
     * @param date Date to test
     * @return True if date is same year,month and day as today.
     */
    public static boolean isToday(Date date) {
        final Calendar c1 = getCalendar(date);
        final Calendar c2 = getCalendar();
        return c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

    /**
     * Get new date with given year,month,day,hour,minutes and seconds.
     * 
     * @param year Year as integer (ex; 1965)
     * @param month Month as integer (0-11)
     * @param day Day as integer (First day is 1)
     * @param hours Hours as integer (0-23)
     * @param minutes Minutes as integer (0-59)
     * @param seconds Seconds as integer (0-59)
     * @return new Date.
     */
    public static Date toDate(int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        return c.getTime();
    }

    public static Date toDate(int year, int month, int day, int hours, int minutes, int seconds) {
        final Calendar c = Calendar.getInstance();
        c.set(year, month, day, hours, minutes, seconds);
        return c.getTime();
    }

}
