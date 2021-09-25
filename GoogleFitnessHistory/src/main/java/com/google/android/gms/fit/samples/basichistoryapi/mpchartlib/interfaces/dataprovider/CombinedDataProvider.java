package com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.interfaces.dataprovider;

import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.CombinedData;

/**
 * Created by philipp on 11/06/16.
 */
public interface CombinedDataProvider extends LineDataProvider, BarDataProvider, BubbleDataProvider, CandleDataProvider, ScatterDataProvider {

    CombinedData getCombinedData();
}
