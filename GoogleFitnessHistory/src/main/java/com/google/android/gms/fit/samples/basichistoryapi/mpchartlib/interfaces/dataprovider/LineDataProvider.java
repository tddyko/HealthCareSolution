package com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.interfaces.dataprovider;

import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.YAxis;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
