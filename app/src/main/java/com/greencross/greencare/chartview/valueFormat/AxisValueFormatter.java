package com.greencross.greencare.chartview.valueFormat;


import com.greencross.greencare.charting.components.AxisBase;
import com.greencross.greencare.charting.formatter.IAxisValueFormatter;
import com.greencross.greencare.base.value.TypeDataSet;

/**
 * Created by philipp on 02/06/16.
 */
public class AxisValueFormatter implements IAxisValueFormatter {
    private final String[] mWeeks = new String[] {
            "일", "월", "화", "수", "목", "금", "토"
    };

    private TypeDataSet.Period mPeriod;

    public AxisValueFormatter(TypeDataSet.Period period) {
        mPeriod = period;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int idx = (int) value;

        if (mPeriod == TypeDataSet.Period.PERIOD_DAY) {
            return ""+ (idx);
        } else if (mPeriod == TypeDataSet.Period.PERIOD_WEEK) {
            if (mWeeks.length > idx) {
                return mWeeks[idx];
            } else {
                return "";
            }
        } else if (mPeriod == TypeDataSet.Period.PERIOD_MONTH
                || mPeriod == TypeDataSet.Period.PERIOD_YEAR) {
            return ""+ (idx+1);
        } else {
            return "!"+idx;
        }
    }
}
