package com.greencross.greencare.charting.interfaces.dataprovider;

import com.greencross.greencare.charting.components.YAxis.AxisDependency;
import com.greencross.greencare.charting.data.BarLineScatterCandleBubbleData;
import com.greencross.greencare.charting.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
