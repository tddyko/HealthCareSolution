package com.greencross.greencare.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 시간 클래스의 시간 문자열 처리 & 시간 문자열의 시간 클래스 처리
 *
 * @author yss1976
 */
public class CDateUtil {
    public static final String DEFAULT_FORMAT           = "yyyy-MM-dd HH:mm:ss";
    public static final String PULL_TO_REFRESH_FORMAT   = "yyyy.MM.dd aa KK:mm";
    private static final String FORMAT_YYYYMMDD         = "yyyy.MM.dd";
    private static final String FORMAT_yyMMddHHmmssSSS  = "yyMMddHHmmssSSS";
    private static final String FORMAT_yyyy_MM_dd       = "yyyy-MM-dd";

//    yyyy-mm-dd hh:mi:ss.mmm
//    yyyy-mm-dd hh:mi:ss

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_FORMAT);
    private static final String TAG = CDateUtil.class.getSimpleName();

    private CDateUtil() {
    }
    public static String getPullToRefreshString(long timeMillis) {
        SimpleDateFormat tempFormat = new SimpleDateFormat(PULL_TO_REFRESH_FORMAT);
        return tempFormat.format(new Date(timeMillis));
    }

    /**
     * milliseconds를 시간 포멧 문자열로 변환
     *
     * @param format
     * @return
     */
    public static String getFormattedString(long timeMillis, String format) {
        SimpleDateFormat tempFormat = new SimpleDateFormat(format);
        return tempFormat.format(new Date(timeMillis));
    }

    /**
     * milliseconds를 표준 시간 포멧 문자열로 변환
     *
     * @return
     */
    public static String getFormattedString(long timeMillis) {
        return simpleDateFormat.format(new Date(timeMillis));
    }

    public static String getFormattedString_yyyy_MM_dd(long timeMillis) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_yyyy_MM_dd);
        return format.format(new Date(timeMillis));
    }

    public static String getFormattedString_hh_mm(long timeMillis) {
        SimpleDateFormat format = new SimpleDateFormat("hh_mm");
        return format.format(new Date(timeMillis));
    }

    public static String getFormattedString_yyyy(long timeMillis) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return format.format(new Date(timeMillis));
    }

    public static String getFormattedString_MM(long timeMillis) {
        SimpleDateFormat format = new SimpleDateFormat("MM");
        return format.format(new Date(timeMillis));
    }

    /**
     * 시간 포멧 문자열을 milliseconds로 변환
     *
     * @param formatStr
     * @param timeStr
     * @return
     */
    public static long getTime(String formatStr, String timeStr) {
        long timeMillis = 0L;
        try {
            SimpleDateFormat dform  = new SimpleDateFormat(formatStr);
            timeMillis              = dform.parse(timeStr).getTime();
        } catch (ParseException e) {
            timeMillis              = 0L;
        }
        return timeMillis;
    }

    /**
     * 시간 yyyy-mm-dd hh:mm을 yyyymmddhhmm으로 변환
     */
    public static String getFormatyyyymmddHHmm(String timeStr) {
        timeStr = StringUtil.getIntString(timeStr);
        if (timeStr.length() >= 15) {
            timeStr = timeStr.substring(0, 4) + timeStr.substring(5, 7) + timeStr.substring(8, 10) + timeStr.substring(11, 13) + timeStr.substring(14, 16);
        }

        return timeStr;
    }

    /**
     * 시간 yyyy-mm-dd hh:mm을 mm월dd일(월) 오전 00시00분 변환
     */
    public static String getFormatmmddhhmm(String timeStr) {
        Calendar cal    = Calendar.getInstance();
        cal.set(Integer.parseInt(timeStr.substring(0, 4)), Integer.parseInt(timeStr.substring(5, 7)), Integer.parseInt(timeStr.substring(8, 10)), Integer.parseInt(timeStr.substring(11, 13)), Integer.parseInt(timeStr.substring(14, 16)));
        timeStr         = StringUtil.getIntString(timeStr);
        if (timeStr.length() >= 15) {
            timeStr     = timeStr.substring(5, 7) + "월" + timeStr.substring(8, 10) + "일" + "(" + getDateToWeek(cal) + ")" + getAmPmString(timeStr.substring(11, 13)) + timeStr.substring(11, 13) + "시" + timeStr.substring(14, 16) + "분";
        }
        return timeStr;
    }


    /**
     * 시간 포멧 문자열 변환
     * 200001.01
     *
     * @return
     */
    public static String getFormatYYYYMMDD(String timeStr) {
        timeStr     = StringUtil.getIntString(timeStr);
        if (timeStr.length() >= 8) {
            timeStr = timeStr.substring(0, 4) + "." + timeStr.substring(4, 6) + "." + timeStr.substring(6);
        }
        return timeStr;
    }

    /**
     * yyyy-mm-dd hh:mi:ss
     * 2017-04-13-23:24:35
     *
     * @param timeStr
     * @return
     */
    public static String getRegDateFormat_yyyyMMddHHss(String timeStr) {
        String result = StringUtil.getIntString(timeStr);
        int strLen = result.length();
        StringBuffer sb = new StringBuffer();
        // yyyy-MM-dd
        if (strLen >= 8) {
            sb.append(result.substring(0, 4) + "-"   // yyyy
                    + result.substring(4, 6) + "-"
                    + result.substring(6, 8));   // MM

            // HH:mm
            if (strLen >= 12) {
                sb.append(" "   // dd
                        + result.substring(8, 10) + ":"  // HH
                        + result.substring(10, 12)); // mm
                // :ss
                if (strLen >= 14) {
                    sb.append(":"   // dd
                            + result.substring(12, 14));  // ss

                    // :ms
                    if (strLen >= 16) {
                        sb.append(":"   // dd
                                + result.substring(14, 16));  // ms
                    }
                }
            }
            result = sb.toString();
        } else {
            result = timeStr;
        }
        return result;
    }

    public static String getRegDateFormat_yyyyMMdd_HHss(String timeStr, String yySep, String timeSep) {
        timeStr = StringUtil.getIntString(timeStr);
        if (timeStr.length() >= 12) {
            timeStr = timeStr.substring(0, 4) + yySep +  // yyyy
                    timeStr.substring(4, 6) + yySep +    // MM
                    timeStr.substring(6, 8) + " " +   // dd
                    timeStr.substring(8, 10) + timeSep +  // HH
                    timeStr.substring(10, 12); // ss
        }
        return timeStr;
    }

    public static String getForamtyyMMddHHmmssSS(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssSS");
        return format.format(date);
    }

    public static String HH_MM(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("HH시 mm분");
        String rtnValue = format.format(date);

        return rtnValue;
    }


    public static String getForamtyyyyMMddHHmm(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        return format.format(date);
    }


    public static String getForamtyyyyMMddHOUR0000(int hour, int diff) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, diff);

        System.out.println("Calendar 년: " +  cal.get(Calendar.YEAR));
        System.out.println("Calendar 월: " + (cal.get(Calendar.MONTH) + 1));
        System.out.println("Calendar 일: " +  cal.get(Calendar.DAY_OF_MONTH));
        Date date = cal.getTime();

        String sHour = hour<10?"0"+hour:""+hour;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd@5959");
        String nowHour = format.format(date);
        return nowHour.replace("@", sHour);
    }

    /**
     * 기기 시간과 매칭하여 포멧팅
     *
     * @param date
     * @param time
     * @return
     */
    public static String getIdxFormatMMddHHmmssSS(Date date, int time) {
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssSS");
        String str = format.format(date);
        str = str.substring(0, 6) + time + str.substring(8);
        return str;
    }

    public static String getForamtyyyyMMddHHmmss(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(date);
    }

    public static String getForamtyyyy_MM_dd_HH_mm_ss(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static String getForamt_monthday(int n) {
        if (n < 10)
            return "0" + n;
        else
            return n + "";
    }

    /**
     * @param yyyy_MM_dd_HH_mm
     * @return
     */
    public static Calendar getCalendar_yyyy_MM_dd_HH_mm(String yyyy_MM_dd_HH_mm) {
        Calendar cal = Calendar.getInstance();
        Logger.i(TAG, "getCalendar_yyyy_MM_dd_HH_mm=" + yyyy_MM_dd_HH_mm);
        try {
            String regDe = StringUtil.getIntString(yyyy_MM_dd_HH_mm);
            String format = "yyyyMMddHHmm";
            if (regDe.length() == 8)
                format = "yyyyMMdd";

            SimpleDateFormat formatter = new SimpleDateFormat(format);
            Date date = formatter.parse(regDe);
            cal.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cal;
    }

    public static Calendar getCalendar_yyyyMMdd(String yyyyMMdd) {
        String date     = StringUtil.getIntString(yyyyMMdd);
        Logger.i(TAG, "getCalendar_yyyyMMdd yyyyMMdd=" + yyyyMMdd + ", date=" + date);
        Calendar cal    = Calendar.getInstance();
        try {
            cal.set(Calendar.YEAR, StringUtil.getIntVal(date.substring(0, 4)));
            cal.set(Calendar.MONTH, StringUtil.getIntVal(date.substring(4, 6)));
            cal.set(Calendar.DATE, StringUtil.getIntVal(date.substring(6, 8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cal;
    }

    public static String getDateToWeek(String dateStr) {
        Logger.i(TAG, "getDateToWeek.dateStr=" + dateStr);
        try {
            dateStr                     = StringUtil.getIntString(dateStr);
            SimpleDateFormat formatter  = new SimpleDateFormat("yyyyMMdd");
            Date date                   = formatter.parse(dateStr);  // 날짜 입력하는곳 .
            date                        = new Date(date.getTime() + (1000 * 60 * 60 * 24 * +1));  // 날짜에 하루를 더한 값
            // 하루를 뺄려면 (1000*60*60*24*-1) 해주시면 됩니다.

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);              // 하루더한 날자 값을 Calendar  넣는다.

            return getDateToWeek(cal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    /**
     * 특정 날짜에 대하여 요일을 구함(일 ~ 토)
     *
     * @return
     * @throws Exception
     */
    public static String getDateToWeek(Calendar cal) {
        String convertedString  = "";

        try {
            int dayNum = cal.get(Calendar.DAY_OF_WEEK) - 1;   // 요일을 구해온다.

            switch (dayNum) {
                case 1:
                    convertedString = "일";
                    break;
                case 2:
                    convertedString = "월";
                    break;
                case 3:
                    convertedString = "화";
                    break;
                case 4:
                    convertedString = "수";
                    break;
                case 5:
                    convertedString = "목";
                    break;
                case 6:
                    convertedString = "금";
                    break;
                case 0:
                    convertedString = "토";
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertedString;
    }

    public static String getToday_yyyyMMdd() {
        Calendar cal            = Calendar.getInstance(Locale.KOREA);
        Date now                = new Date();
        cal.setTime(now);
        long time               = cal.getTimeInMillis();
        SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(time);
    }

    public static String getToday_yyyy_MM_dd() {
        Calendar cal            = Calendar.getInstance(Locale.KOREA);
        Date now                = new Date();
        cal.setTime(now);
        long time               = cal.getTimeInMillis();
        SimpleDateFormat sdf    = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(time);
    }

    public static String getTodayNowYear() {

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
        String hour = sdfNow.format(new Date(System.currentTimeMillis()));
        return hour.substring(0, 4);
    }

    public static String getTodayNowMonth() {

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
        String hour = sdfNow.format(new Date(System.currentTimeMillis()));
        return hour.substring(4, 6);
    }
    public static String getTodayNowDay() {

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
        String hour = sdfNow.format(new Date(System.currentTimeMillis()));
        return hour.substring(6, 8);
    }
    public static String getTodayNowHour() {

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
        String hour = sdfNow.format(new Date(System.currentTimeMillis()));
        return hour.substring(8, 10);
    }

    public static int getTodayNowWeek(){
        Calendar cal = Calendar.getInstance();
        int week = cal.get(Calendar.DAY_OF_WEEK)-1;
        return week;
    }

    public static boolean getTodayForBetweenDate(String sDate, String eDate){

        Calendar cal            = Calendar.getInstance(Locale.KOREA);
        Date now                = new Date();
        cal.setTime(now);
        long time               = cal.getTimeInMillis();
        SimpleDateFormat sdf    = new SimpleDateFormat("yyyyMMdd");

        int ssDate = StringUtil.getIntger(sDate.replace("-",""));
        int eeDate = StringUtil.getIntger(eDate.replace("-",""));
        int ttDay  = StringUtil.getIntger(sdf.format(time));

        if (ssDate==eeDate){
            if (ssDate==ttDay)
                return true;
        }else{
            if (ssDate <= ttDay && eeDate >= ttDay){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }


    public static String getFormat_yyyy_MM_dd(String date) {
        date = StringUtil.getIntString(date);
        if (date.length() >= 8) {
            date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
            return date;
        } else {
            return date;
        }
    }

    /**
     * 오전 오후 String 로 변환해준다
     *
     * @param hourOfDay
     * @return
     */
    public static String getAmPmString(int hourOfDay) {
        return getAmPmString("" + hourOfDay);
    }

    public static String getAmPmString(String hourOfDay) {
        if (hourOfDay.length() > 2)
            hourOfDay = StringUtil.getIntString(hourOfDay).substring(0, 2);

        String amPm = "오전";
        int hour = StringUtil.getIntVal(hourOfDay);
        if (hour > 11) {
            amPm = "오후";
        }
        return amPm;
    }
}
