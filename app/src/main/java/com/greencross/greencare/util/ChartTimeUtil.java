package com.greencross.greencare.util;

import android.util.Log;
import android.widget.RadioButton;

import com.greencross.greencare.base.value.TypeDataSet;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.greencross.greencare.base.value.TypeDataSet.Period.PERIOD_DAY;
import static com.greencross.greencare.base.value.TypeDataSet.Period.PERIOD_MONTH;
import static com.greencross.greencare.base.value.TypeDataSet.Period.PERIOD_WEEK;
import static com.greencross.greencare.base.value.TypeDataSet.Period.PERIOD_YEAR;
import static java.text.DateFormat.getDateTimeInstance;

/**
 * Created by mrsohn on 2017. 3. 21..
 */

public class ChartTimeUtil {
    private final String TAG = ChartTimeUtil.class.getSimpleName();

    private int calTime = 0;

    private long startTime;
    private long endTime;
    private TimeUnit timeUnit;

    private TypeDataSet.Period mPeriod = TypeDataSet.Period.PERIOD_DAY; // 기간 설정 (월,주,일)
    private TypeDataSet.EatState mEatState = TypeDataSet.EatState.TYPE_ALL; // 기간 설정 (월,주,일)

    private RadioButton mRadioButtonDay, mRadioButtonWeek, mRadioButtonMonth, mRadioButtonYear, mRadioButtonTypeAll, mRadioButtonTypeBefore, mRadioButtonTypeAfter;

//    public ChartTimeUtil() {
//        getTime();
//    }

    public ChartTimeUtil(TypeDataSet.Period period) {
        mPeriod = period;
        getTime();
    }

    /**
     * 일간, 주간, 월간
     * @param radioButtonDay
     * @param radioButtonWeek
     * @param radioButtonMonth
     */
    public ChartTimeUtil(RadioButton radioButtonDay, RadioButton radioButtonWeek, RadioButton radioButtonMonth) {
        mRadioButtonDay = radioButtonDay;
        mRadioButtonWeek = radioButtonWeek;
        mRadioButtonMonth = radioButtonMonth;

        getTime();
    }

    /**
     * 일간, 주간, 월간, 년간
     * @param radioButtonDay
     * @param radioButtonWeek
     * @param radioButtonMonth
     * @param radioButtonYear
     */
    public ChartTimeUtil(RadioButton radioButtonDay, RadioButton radioButtonWeek, RadioButton radioButtonMonth, RadioButton radioButtonYear) {
        mRadioButtonDay = radioButtonDay;
        mRadioButtonWeek = radioButtonWeek;
        mRadioButtonMonth = radioButtonMonth;
        mRadioButtonYear = radioButtonYear;

        getTime();
    }

    /**
     * 일간, 주간, 월간
     * @param radioButtonDay
     * @param radioButtonWeek
     * @param radioButtonMonth
     * @param radioButtonTypeAll
     * @param radioButtonTypeBefore
     * @param radioButtonTypeAfter
     */
    public ChartTimeUtil(RadioButton radioButtonDay, RadioButton radioButtonWeek, RadioButton radioButtonMonth, RadioButton radioButtonTypeAll, RadioButton radioButtonTypeBefore, RadioButton radioButtonTypeAfter) {
        mRadioButtonDay = radioButtonDay;
        mRadioButtonWeek = radioButtonWeek;
        mRadioButtonMonth = radioButtonMonth;
        mRadioButtonTypeAll = radioButtonTypeAll;
        mRadioButtonTypeBefore = radioButtonTypeBefore;
        mRadioButtonTypeAfter = radioButtonTypeAfter;

        getTime();
    }


    public void clearTime() {
        setCalTime(0);
        getTime();
    }

    /**
     * 일별의 경우 -1일 +1일
     * 주간의 경우 -1주 +1주
     * 월간의경우 -1월 +1월
     * @param calTime
     * @return
     */
    public ChartTimeUtil calTime(int calTime) {
        setCalTime(getCalTime() + calTime);
        return  getTime();
    }

    /**
     * period에 따라 월,주,일 startTime, endTime를 세팅하여 보내준다
     * @return
     */
    public ChartTimeUtil getTime() {
            /* 시간 설정 */
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();

        TypeDataSet.Period periodType = getPeriodType();

        if (periodType == PERIOD_DAY) {
            // 일간
            setTimeUnit(TimeUnit.HOURS);
            if (getCalTime() != 0) {
                cal.add(Calendar.DATE, getCalTime());
                setLastTimeCal(cal);
            }
            endTime = cal.getTimeInMillis();
        } else if (periodType == PERIOD_WEEK) {
            // 주간
            setTimeUnit(TimeUnit.DAYS);
            if (getCalTime() == 0) {
                int startDayOfWeek = -(cal.get(Calendar.DAY_OF_WEEK) - 1);
                cal.add(Calendar.DAY_OF_WEEK, startDayOfWeek);
            } else {
                int minusDay = 0;
                if (getCalTime() != -1) {
                    minusDay = 7 * (getCalTime() + 1);
                }

                minusDay = minusDay - cal.get(Calendar.DAY_OF_WEEK);
                cal.add(Calendar.DATE, minusDay);
                setLastTimeCal(cal);
                endTime = cal.getTimeInMillis();

                Log.i(TAG, "minusDay=" + minusDay);
                cal.add(Calendar.DATE, -6);
            }
        } else if (periodType == PERIOD_MONTH) {
            // 월간
            setTimeUnit(TimeUnit.DAYS);

            if (getCalTime() == 0) {
                cal.add(Calendar.DAY_OF_MONTH, -(cal.get(Calendar.DAY_OF_MONTH) - 1));
            } else {
                cal.add(Calendar.MONTH, getCalTime());
                cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                setLastTimeCal(cal);
                endTime = cal.getTimeInMillis();

                cal.set(Calendar.DATE, cal.getMinimum(Calendar.DAY_OF_MONTH));//1일로 설정

                Log.i(TAG, "month=" + cal.get(Calendar.MONTH) + ", maximum=" + cal.getActualMaximum(Calendar.DAY_OF_MONTH) + ", minuteMum=" + cal.getMinimum(Calendar.DAY_OF_MONTH));
            }
        } else if (periodType == PERIOD_YEAR) {

            if (getCalTime() == 0) {
                cal.add(Calendar.DAY_OF_MONTH, -(cal.get(Calendar.DAY_OF_MONTH) - 1));
            } else {
                cal.add(Calendar.YEAR, getCalTime());
                cal.set(Calendar.MONTH, cal.getActualMaximum(Calendar.MONTH));
                cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
                cal.set(Calendar.HOUR, cal.getActualMaximum(Calendar.HOUR));
                cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));

//                setLastTimeCal(cal);
                endTime = cal.getTimeInMillis();
            }
        } else {
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            setTimeUnit(TimeUnit.DAYS);
        }

        if (periodType == PERIOD_YEAR) {
            setStartTimeCalYear(cal);
        } else {
            setStartTimeCal(cal);
        }

        long startTime = cal.getTimeInMillis();

        DateFormat dateFormat = getDateTimeInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

//        mStartDateTv.setText(dateFormat.format(startTime));
//        mEndDateTv.setText(dateFormat.format(endTime));

        setStartTime(startTime);
        setEndTime(endTime);

        return this;
//        mBarChartView.setXValueFormat(new AxisValueFormatter(getPeriod()));
    }

    /**
     * 0 초기값(현재일자), -1(-1일,-1주,-1달,-1년)
     * @return
     */
    public int getCalTime() {
        return calTime;
    }

    public void setCalTime(int calTime) {
        this.calTime = calTime;
    }

    // 시작 시간으로 설정 00:00:00
    private void setStartTimeCal(Calendar cal) {
        Logger.i(TAG, "getMinimumHour="+cal.getMinimum(Calendar.HOUR));
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
    }

    // 마지막 시간으로 설정 11:59:59
    private void setLastTimeCal(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
    }

    // 시작 시간으로 설정 00:00:00
    private void setStartTimeCalYear(Calendar cal) {
        cal.set(Calendar.MONTH, cal.getMinimum(Calendar.MONTH));
        cal.set(Calendar.DATE, cal.getMinimum(Calendar.DATE));
        cal.set(Calendar.HOUR, cal.getMinimum(Calendar.HOUR));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
    }

    // yyyy_MM_dd 형태로 들어왔을때 세팅
    private void setStartTimeCalYear(Calendar cal, String yyyy_MM_dd) {
        yyyy_MM_dd = StringUtil.getIntString(yyyy_MM_dd);
        int yyyy = StringUtil.getIntVal(yyyy_MM_dd.substring(0, 4));
        int MM = StringUtil.getIntVal(yyyy_MM_dd.substring(4, 6));
        int dd = StringUtil.getIntVal(yyyy_MM_dd.substring(6, 8));
        cal.set(Calendar.MONTH, yyyy);
        cal.set(Calendar.DATE, MM);
        cal.set(Calendar.HOUR, dd);
    }

    /**
     * 이번달의 최대 일자를 구함
     * @return
     */
    public int getDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(getStartTime()));
        int dayCnt = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return dayCnt;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    /**
     * 조회 시작일자시간
     * @return
     */
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * 조회 종료시간
     * @return
     */
    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Calendar getStartTimeCal() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        return cal;
    }

    public Calendar getEndTimeCal() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endTime);
        return cal;
    }

//    public TypeDataSet.Period getPeriod() {
//        return mPeriod;
//    }

//    public void setPeriod(TypeDataSet.Period mPeriod) {
//        clearTime();
//        this.mPeriod = mPeriod;
//    }

    public TypeDataSet.Period getPeriodType() {
        if (mRadioButtonDay != null && mRadioButtonDay.isChecked()) {
            return TypeDataSet.Period.PERIOD_DAY;
        } else if (mRadioButtonWeek != null && mRadioButtonWeek.isChecked()) {
            return TypeDataSet.Period.PERIOD_WEEK;
        } else if (mRadioButtonMonth != null && mRadioButtonMonth.isChecked()) {
            return TypeDataSet.Period.PERIOD_MONTH;
        } else if (mRadioButtonYear != null && mRadioButtonYear.isChecked()) {
            return TypeDataSet.Period.PERIOD_YEAR;
        }
//        return TypeDataSet.Period.PERIOD_DAY;
        return mPeriod;
    }

    public TypeDataSet.EatState getEatType() {
        if (mRadioButtonTypeAll != null && mRadioButtonTypeAll.isChecked()) {
            return TypeDataSet.EatState.TYPE_ALL;
        } else if (mRadioButtonTypeBefore != null && mRadioButtonTypeBefore.isChecked()) {
            return TypeDataSet.EatState.TYPE_BEFORE;
        } else if (mRadioButtonTypeAfter != null && mRadioButtonTypeAfter.isChecked()) {
            return TypeDataSet.EatState.TYPE_AFTER;
        }

        return mEatState;
    }
}