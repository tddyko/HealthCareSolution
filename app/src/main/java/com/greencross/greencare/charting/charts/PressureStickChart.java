
package com.greencross.greencare.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.greencross.greencare.charting.data.PressureData;
import com.greencross.greencare.charting.interfaces.dataprovider.PresureDataProvider;
import com.greencross.greencare.charting.renderer.PressureChartRenderer;
import com.greencross.greencare.util.ChartTimeUtil;

/**
 * Financial chart type that draws candle-sticks (OHCL chart).
 *
 * @author Philipp Jahoda
 */
public class PressureStickChart extends BarLineChartBase<PressureData> implements PresureDataProvider {

    private ChartTimeUtil timeClass;

    public PressureStickChart(Context context) {
        super(context);
    }

    public PressureStickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PressureStickChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private ChartTimeUtil mTimeClass;

    @Override
    protected void init() {
        super.init();

        mRenderer = new PressureChartRenderer(this, mAnimator, mViewPortHandler, getContext());

        getXAxis().setSpaceMin(0.5f);
        getXAxis().setSpaceMax(0.5f);

    }


    @Override
    public PressureData getPresureData() {
        return mData;
    }

    public void setTimeClass(ChartTimeUtil timeClass) {
        this.timeClass = timeClass;
        mRenderer.setTimeClass(timeClass);
    }
}
