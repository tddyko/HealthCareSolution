package com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.renderer;

import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.animation.ChartAnimator;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.ChartEntry;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.DataSet;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.interfaces.datasets.IDataSet;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 09/06/16.
 */
public abstract class BarLineScatterCandleBubbleRenderer extends DataRenderer {

    /**
     * buffer for storing the current minimum and maximum visible x
     */
    protected XBounds mXBounds = new XBounds();

    public BarLineScatterCandleBubbleRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
    }

    /**
     * Returns true if the DataSet values should be drawn, false if not.
     *
     * @param set
     * @return
     */
    protected boolean shouldDrawValues(IDataSet set) {
        return set.isVisible() && (set.isDrawValuesEnabled() || set.isDrawIconsEnabled());
    }

    /**
     * Checks if the provided entry object is in bounds for drawing considering the current animation phase.
     *
     * @param e
     * @param set
     * @return
     */
    protected boolean isInBoundsX(ChartEntry e, IBarLineScatterCandleBubbleDataSet set) {

        if (e == null)
            return false;

        float entryIndex = set.getEntryIndex(e);

        if (e == null || entryIndex >= set.getEntryCount() * mAnimator.getPhaseX()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Class representing the bounds of the current viewport in terms of indices in the values array of a DataSet.
     */
    protected class XBounds {

        /**
         * minimum visible entry index
         */
        public int min;

        /**
         * maximum visible entry index
         */
        public int max;

        /**
         * range of visible entry indices
         */
        public int range;

        /**
         * Calculates the minimum and maximum x values as well as the range between them.
         *
         * @param chart
         * @param dataSet
         */
        public void set(BarLineScatterCandleBubbleDataProvider chart, IBarLineScatterCandleBubbleDataSet dataSet) {
            float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));

            float low = chart.getLowestVisibleX();
            float high = chart.getHighestVisibleX();

            ChartEntry chartEntryFrom = dataSet.getEntryForXValue(low, Float.NaN, DataSet.Rounding.DOWN);
            ChartEntry chartEntryTo = dataSet.getEntryForXValue(high, Float.NaN, DataSet.Rounding.UP);

            min = chartEntryFrom == null ? 0 : dataSet.getEntryIndex(chartEntryFrom);
            max = chartEntryTo == null ? 0 : dataSet.getEntryIndex(chartEntryTo);
            range = (int) ((max - min) * phaseX);
        }
    }
}
