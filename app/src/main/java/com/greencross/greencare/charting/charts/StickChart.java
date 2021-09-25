
package com.greencross.greencare.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.greencross.greencare.charting.data.SticData;
import com.greencross.greencare.charting.interfaces.dataprovider.SticDataProvider;
import com.greencross.greencare.charting.renderer.StickChartRenderer;

/**
 * Financial chart type that draws candle-sticks (OHCL chart).
 *
 * @author Philipp Jahoda
 */
public class StickChart extends BarLineChartBase<SticData> implements SticDataProvider {

    public StickChart(Context context) {
        super(context);
    }

    public StickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new StickChartRenderer(this, mAnimator, mViewPortHandler);

        getXAxis().setSpaceMin(0.5f);
        getXAxis().setSpaceMax(0.5f);
    }

    @Override
    public SticData getCandleData() {
        return mData;
    }
}
