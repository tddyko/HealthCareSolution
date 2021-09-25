package com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.listener;

import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.ChartEntry;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.highlight.Highlight;

/**
 * Listener for callbacks when selecting values inside the chart by
 * touch-gesture.
 *
 * @author Philipp Jahoda
 */
public interface OnChartValueSelectedListener {

    /**
     * Called when a value has been selected inside the chart.
     *
     * @param e The selected ChartEntry
     * @param h The corresponding highlight object that contains information
     *          about the highlighted position such as dataSetIndex, ...
     */
    void onValueSelected(ChartEntry e, Highlight h);

    /**
     * Called when nothing has been selected or an "un-select" has been made.
     */
    void onNothingSelected();
}
