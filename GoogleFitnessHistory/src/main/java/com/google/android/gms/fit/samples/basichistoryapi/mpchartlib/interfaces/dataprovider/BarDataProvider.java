package com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.interfaces.dataprovider;

import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.BarData;

public interface BarDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BarData getBarData();
    boolean isDrawBarShadowEnabled();
    boolean isDrawValueAboveBarEnabled();
    boolean isHighlightFullBarEnabled();
}
