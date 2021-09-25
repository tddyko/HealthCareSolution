package com.google.android.gms.fit.samples.basichistoryapi.chart.valueFormat;


import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.ChartEntry;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.formatter.IValueFormatter;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.utils.ViewPortHandler;

/**
 * Created by mrsohn on 2017. 3. 1..
 */

public class BarDataFormatter implements IValueFormatter {

    @Override
    public String getFormattedValue(float value, ChartEntry chartEntry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        int idx = (int) value;
        return idx == 0 ? "": String.format("%,d", idx);
    }
}
