package springboot.shopjob.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 *
 * 项目名称：evcard-iss
 * 类名称：DateUtil
 * 类描述：日期工具类
 * 创建人：wangcm-王成名
 * 创建时间：2017年7月6日 下午5:47:00
 * 修改备注：
 * @version1.0
 *
 */
public class DateUtil {
    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

    public static final String DATE_TYPE1 = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TYPE2 = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String DATE_TYPE3 = "yyyyMMddHHmmssSSS";

    public static final String DATE_TYPE4 = "yyyyMMddHHmmss";

    public static final String DATE_TYPE5 = "yyyy-MM-dd";

    public static final String DATE_TYPE6 = "yy-MM-dd-HH-mm-ss";

    public static final String DATE_TYPE7 = "yyyy-MM-dd HH:mm";

    public static final String DATE_TYPE8 = "yyyyMMdd";

    public static final String DATE_TYPE9 = "yyyy-M-d H:m:s:S";

    public static final String DATE_TYPE10 = "yyyyMMddHHmm";

    public static final String DATE_TYPE11 = "yyyy-M-d H:m:s";

    public static final String DATE_TYPE12 = "yy-MM-dd HH:mm:ss";

    public static final String DATE_TYPE13 = "yyyy/MM/dd HH:mm:ss";

    public static final String DATE_TYPE14 = "MM-dd HH:mm:ss";

    public static final String DATE_TYPE15 = "yyyy年MM月dd日 HH:mm";
    public static final String DATE_TYPE16 = "MM月dd日 HH:mm";

    public static final String DATE_TYPE17 = "yyyy-MM";
    public static final String DATE_TYPE18 = "yyyyMM";
    public static final String DATE_TYPE19 = "HHmm";
    public static final String DATE_TYPE20 = "yyyy年MM月dd日";
    public static final String DATE_TYPE21 = "yyyy年MM月";
    public static final String DATE_TYPE22 = "MMdd";
    public static final String DATE_TYPE23 = "yyMMdd";
    public static final String DATE_TYPE24 = "HHmmss";
    public static final String DATE_TYPE25 = "HH:mm:ss";

    public static final String DATE_TYPE26 = "yyyy";
    public static final String DATE_TYPE27 = "MM";
    

    public static final String TIMES_TYPE1 = "%d天%d小时%d分";

    public static TimeZone timeZoneChina = TimeZone.getTimeZone("Asia/Shanghai");// 获取时区

    /**
     * 日期转字符串
     *
     * @param date 日期
     * @param pattern 格式
     * @return
     */
    public static String dateToString(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(date);
        }
        return "";
    }

    /**
     * 字符串转日期
     * @param str 字符串
     * @param pattern 格式
     * @return
     */
    public static Date stringToDate(String str, String pattern) {
        if (!"".equals(str)) {

            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            try {
                return sdf.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 日期转字符串
     *
     * @param date
     * @return
     */
    public static String dateToString(Date date) {
        return dateToString(date, DATE_TYPE1);
    }

    /**
     * 获取当前时间
     *
     * @param type 指定格式
     * @return
     */
    public static String getSystemDate(String type) {

        // 指定格式
        DateFormat date_format = new SimpleDateFormat(type);
        date_format.setTimeZone(timeZoneChina);

        // 范围指定格式的字符串
        return date_format.format(new Date());
    }

    /**
     * 时间运算
     *
     * @param date 运算前时间
     * @param min add的分钟数
     * @return
     */
    public static Date AddMin(Date date, int min) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, min);
        return cal.getTime();
    }

    /**
     * 时间运算
     *
     * @param date 运算前时间
     * @param value add的值
     * @return
     */
    public static Date AddTime(Date date, int value, int type) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(type, value);
        return cal.getTime();
    }

    /**
     * 时间运算 (月份加算)
     * @return String
     */
    public static String AddMonth(String dateStr, String format, int mon) {
        DateFormat dateFmt = new SimpleDateFormat(format);
        dateFmt.setTimeZone(timeZoneChina);
        Date tmpDate;

        try {
            tmpDate = dateFmt.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(tmpDate);
            cal.add(Calendar.MONTH, mon);

            return dateFmt.format(cal.getTime());

        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 取得月的最后一天
     *
     * @return String 参数日期所在月份的最后一天
     */
    public static String getMonthLastDay(String dateStr, String fromFmt, String toFmt) {
        DateFormat dateFmt = new SimpleDateFormat(fromFmt);
        dateFmt.setTimeZone(timeZoneChina);
        Date tmpDate;

        try {
            tmpDate = dateFmt.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(tmpDate);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

            DateFormat dateToFmt = new SimpleDateFormat(toFmt);
            dateToFmt.setTimeZone(timeZoneChina);
            return dateToFmt.format(cal.getTime());
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 时间转化
     *
     * @param dateStr
     * @param fromType
     * @param toType
     * @return
     */
    public static String getFormatDate(String dateStr, String fromType, String toType) {

        try {
            DateFormat dateFromFmt = new SimpleDateFormat(fromType);
            dateFromFmt.setTimeZone(timeZoneChina);
            DateFormat dateToFmt = new SimpleDateFormat(toType);
            dateToFmt.setTimeZone(timeZoneChina);

            // 非空检查
            if ("".equals(dateStr)) {
                return "";
            } else {
                Date tmpDate = dateFromFmt.parse(dateStr);

                if (dateFromFmt.format(tmpDate).equals(dateStr)) {
                    return dateToFmt.format(tmpDate);
                } else {
                    return "";
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    public static String getFormatDate(Date date, String toType) {

        try {
            DateFormat dateToFmt = new SimpleDateFormat(toType);
            dateToFmt.setTimeZone(timeZoneChina);
            // 非空检查
            if (date == null) {
                return "";
            } else {
                return dateToFmt.format(date);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    public static Date getDateFromStr(String dateStr, String fromType) {

        try {
            DateFormat dateFromFmt = new SimpleDateFormat(fromType);
            dateFromFmt.setTimeZone(timeZoneChina);

            return dateFromFmt.parse(dateStr);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static boolean isValidDate(String str, String fromFmt) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat(fromFmt);
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 返回天数差
     * @param early
     * @param late
     * @return
     */
    public static int daysBetween(Date early, Date late) {
        Calendar calst = Calendar.getInstance();
        Calendar caled = Calendar.getInstance();
        calst.setTime(early);
        caled.setTime(late);
        // 得到两个日期相差的小时数
        float days = (((caled.getTime().getTime() / 1000) - (calst.getTime().getTime() / 1000)) / 3600f / 24f);
        return (int) Math.ceil(days);
    }

    /**
     * 返回天数差
     * @param earlyDate
     * @param lateDate
     * @param dateType
     * @return
     */
    public static int daysBetween(String earlyDate, String lateDate, String dateType) {

        Date early = DateUtil.getDateFromStr(earlyDate, dateType);
        Date late = DateUtil.getDateFromStr(lateDate, dateType);

        Calendar calst = Calendar.getInstance();
        Calendar caled = Calendar.getInstance();
        calst.setTime(early);
        caled.setTime(late);
        // 得到两个日期相差的小时数
        float days = (((caled.getTime().getTime() / 1000) - (calst.getTime().getTime() / 1000)) / 3600f / 24f);
        return (int) Math.ceil(days);
    }

    /**
     * 返回两个日期相差的小时数
     * @param early
     * @param late
     * @return BedDecimal 四舍五入保留两位小数
     */
    public static BigDecimal hoursBetween(Date early, Date late) {
        Calendar calst = Calendar.getInstance();
        Calendar caled = Calendar.getInstance();
        calst.setTime(early);
        caled.setTime(late);
        // 得到两个日期相差的小时数
        float hours = ((caled.getTime().getTime() / 1000) - (calst.getTime().getTime() / 1000)) / 3600f;
        return new BigDecimal(hours).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 返回两个日期相差的分钟数
     * @param early
     * @param late
     * @return BedDecimal 四舍五入不保留小数位
     */
    public static BigDecimal MinutesBetween(Date early, Date late) {
        Calendar calst = Calendar.getInstance();
        Calendar caled = Calendar.getInstance();
        calst.setTime(early);
        caled.setTime(late);
        // 得到两个日期相差的小时数
        float minutes = ((caled.getTime().getTime() / 1000) - (calst.getTime().getTime() / 1000)) / 60f;
        return new BigDecimal(minutes).setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 分钟格式化成 x天x小时x分
     * @param times 分钟
     * @return 格式化的 x天x小时x分
     */
    public static String getFormatDayHourMinute(Integer times) {
        if (times == null) {
            return String.format(TIMES_TYPE1, 0, 0, 0);
        }

        int days = times / (24 * 60);
        int hours = (times - days * (24 * 60)) / 60;
        int minutes = times - days * (24 * 60) - hours * 60;

        return String.format(TIMES_TYPE1, days, hours, minutes);
    }

    /**
     * 取当前时间 并格式化为 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getTimestampWithMsec() {
        String msecStr = "";
        DateFormat sdf = new SimpleDateFormat(DATE_TYPE1);
        Timestamp datetime = new Timestamp(System.currentTimeMillis());
        msecStr = sdf.format(datetime);

        return msecStr;
    }

    /**
     * Timestamp 转为 yyyy-MM-dd HH:mm:ss
     * @param timestamp
     * @return
     */
    public static String TimestampToString(Timestamp timestamp) {
        String temp = "";
        DateFormat sdf = new SimpleDateFormat(DATE_TYPE1);
        temp = sdf.format(timestamp);
        return temp;

    }

    /**
     * String yyyy-MM-dd HH:mm  转为  Timestamp
     * @param date
     * @return
     */
    public static Timestamp dateMend(String date) {
        String hour = date.trim().substring(0, 10);
        String Minute = date.trim().substring(10, date.trim().length());
        // String temp = date + ":00";
        String temp = hour + "" + Minute + ":00";
        Timestamp time = Timestamp.valueOf(temp);
        return time;
    }

    /**
     * String yyyy-MM  转为  Timestamp
     * @param date
     * @return
     */
    public static Timestamp dateMendMonth(String date) {
        String temp = date + "-01 00:00:01";
        Timestamp time = Timestamp.valueOf(temp);
        return time;
    }
    
    /**
     * 把字符串转化为 long时间
     * @param date
     * @param type
     * @return
     */
    public static Long getTime(String date, String type) {
    	SimpleDateFormat sdf = new SimpleDateFormat(type);
    	Date tempDate = null;
    	try {
			tempDate = sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return tempDate.getTime();
    }
    
    public static List<String> getMonthBetween(String minDate, String maxDate) throws ParseException {
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");//格式化为年月
     
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
     
        min.setTime(sdf.parse(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
     
        max.setTime(sdf.parse(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
     
        Calendar curr = min;
        while (curr.before(max)) {
         result.add(sdf.format(curr.getTime()));
         curr.add(Calendar.MONTH, 1);
        }
     
        return result;
    }
    
    /**
     * 计算月份差
     * @param maxDtStr
     * @param minDtStr
     * @return
     */
    public static long monthCompare(String maxDtStr, String minDtStr) {
        LocalDate localDate1 = LocalDate.parse(minDtStr);
        LocalDate localDate2 = LocalDate.parse(maxDtStr);
        return localDate2.until(localDate1, ChronoUnit.MONTHS) + 1;
    }

    /**
     * 时间戳转时间
     * @param stampString
     * @param type
     * @return
     */
    public static String stampToDate(String stampString, String type){
        String ss;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(type);
        long t = new Long(stampString);
        Date date = new Date(t);
        ss = simpleDateFormat.format(date);
        return ss;
    }

    /**
     * 天数运算
     * @param dateStr
     * @param format
     * @param days
     * @return
     */
    public static String AddDay(String dateStr, String format, int days) {
        DateFormat dateFmt = new SimpleDateFormat(format);
        dateFmt.setTimeZone(timeZoneChina);
        Date tmpDate;

        try {
            tmpDate = dateFmt.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(tmpDate);
            cal.add(Calendar.DATE, days);

            return dateFmt.format(cal.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


}
