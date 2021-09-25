package com.google.android.gms.fit.samples.basichistoryapi.chart.valueFormat;

import com.google.android.gms.fit.samples.basichistoryapi.FitnessDataSet;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.AxisBase;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.formatter.IAxisValueFormatter;

/**
 * Created by philipp on 02/06/16.
 */
public class AxisValueFormatter implements IAxisValueFormatter {
    private final String[] mWeeks = new String[] {
            "일", "월", "화", "수", "목", "금", "토"
    };

    private FitnessDataSet.Period mPeriod;

    public AxisValueFormatter(FitnessDataSet.Period period) {
        mPeriod = period;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int idx = (int) value;

        if (mPeriod == FitnessDataSet.Period.PERIOD_DAY) {
            return ""+ (idx);
        } else if (mPeriod == FitnessDataSet.Period.PERIOD_WEEK) {
            if (mWeeks.length > idx) {
                return mWeeks[idx];
            } else {
                return "";
            }
        } else if (mPeriod == FitnessDataSet.Period.PERIOD_MONTH) {
            return ""+ (idx+1);
        } else {
            return ":"+idx;
        }


    }
}
