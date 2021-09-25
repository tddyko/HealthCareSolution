package com.greencross.greencare.charting.interfaces.dataprovider;

import com.greencross.greencare.charting.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
