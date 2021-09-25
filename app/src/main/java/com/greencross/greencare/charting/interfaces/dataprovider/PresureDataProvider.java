package com.greencross.greencare.charting.interfaces.dataprovider;

import com.greencross.greencare.charting.data.PressureData;

public interface PresureDataProvider extends BarLineScatterCandleBubbleDataProvider {

    PressureData getPresureData();
}
