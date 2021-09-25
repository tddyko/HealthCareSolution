package com.greencross.greencare.charting.interfaces.dataprovider;

import com.greencross.greencare.charting.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
