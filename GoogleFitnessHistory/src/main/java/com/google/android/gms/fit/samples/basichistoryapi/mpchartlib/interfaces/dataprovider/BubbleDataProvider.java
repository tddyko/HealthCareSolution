package com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.interfaces.dataprovider;

import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
