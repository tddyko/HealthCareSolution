package com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.interfaces.dataprovider;

import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.YAxis.AxisDependency;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.BarLineScatterCandleBubbleData;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
