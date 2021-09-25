package com.greencross.greencare.charting.interfaces.dataprovider;

import com.greencross.greencare.charting.data.SticData;

public interface SticDataProvider extends BarLineScatterCandleBubbleDataProvider {

    SticData getCandleData();
}
