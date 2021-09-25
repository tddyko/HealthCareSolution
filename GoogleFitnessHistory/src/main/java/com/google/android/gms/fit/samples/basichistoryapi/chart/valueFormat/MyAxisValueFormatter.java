package com.google.android.gms.fit.samples.basichistoryapi.chart.valueFormat;


import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.AxisBase;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class MyAxisValueFormatter implements IAxisValueFormatter
{

    private DecimalFormat mFormat;

    public MyAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format(value) + " $";
    }
}
