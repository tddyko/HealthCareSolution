package com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.interfaces.datasets;

import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.ChartEntry;

/**
 * Created by philipp on 21/10/15.
 */
public interface IBarLineScatterCandleBubbleDataSet<T extends ChartEntry> extends IDataSet<T> {

    /**
     * Returns the color that is used for drawing the highlight indicators.
     *
     * @return
     */
    int getHighLightColor();
}
