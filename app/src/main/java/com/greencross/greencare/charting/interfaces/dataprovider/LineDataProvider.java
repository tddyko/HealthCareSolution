package com.greencross.greencare.charting.interfaces.dataprovider;

import com.greencross.greencare.charting.components.YAxis;
import com.greencross.greencare.charting.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
