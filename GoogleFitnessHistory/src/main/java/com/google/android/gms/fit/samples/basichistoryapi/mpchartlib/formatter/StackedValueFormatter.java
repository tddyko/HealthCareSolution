package com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.formatter;

import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.BarChartEntry;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.ChartEntry;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Philipp Jahoda on 28/01/16.
 * <p/>
 * A formatter specifically for stacked BarChart that allows to specify whether the all stack values
 * or just the top value should be drawn.
 */
public class StackedValueFormatter implements IValueFormatter
{

    /**
     * if true, all stack values of the stacked bar entry are drawn, else only top
     */
    private boolean mDrawWholeStack;

    /**
     * a string that should be appended behind the value
     */
    private String mAppendix;

    private DecimalFormat mFormat;

    /**
     * Constructor.
     *
     * @param drawWholeStack if true, all stack values of the stacked bar entry are drawn, else only top
     * @param appendix       a string that should be appended behind the value
     * @param decimals       the number of decimal digits to use
     */
    public StackedValueFormatter(boolean drawWholeStack, String appendix, int decimals) {
        this.mDrawWholeStack = drawWholeStack;
        this.mAppendix = appendix;

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < decimals; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        this.mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    @Override
    public String getFormattedValue(float value, ChartEntry chartEntry, int dataSetIndex, ViewPortHandler viewPortHandler) {

        if (!mDrawWholeStack && chartEntry instanceof BarChartEntry) {

            BarChartEntry barEntry = (BarChartEntry) chartEntry;
            float[] vals = barEntry.getYVals();

            if (vals != null) {

                // find out if we are on top of the stack
                if (vals[vals.length - 1] == value) {

                    // return the "sum" across all stack values
                    return mFormat.format(barEntry.getY()) + mAppendix;
                } else {
                    return ""; // return empty
                }
            }
        }

        // return the "proposed" value
        return mFormat.format(value) + mAppendix;
    }
}
