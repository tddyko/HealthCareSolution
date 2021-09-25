package com.greencross.greencare.charting.interfaces.dataprovider;

import com.greencross.greencare.charting.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
