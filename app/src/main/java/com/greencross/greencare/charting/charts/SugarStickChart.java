
package com.greencross.greencare.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.greencross.greencare.charting.data.SticData;
import com.greencross.greencare.charting.interfaces.dataprovider.SticDataProvider;
import com.greencross.greencare.charting.renderer.SugarStickChartRenderer;
import com.greencross.greencare.util.ChartTimeUtil;

/**
 * Financial chart type that draws candle-sticks (OHCL chart).
 *
 * @author Philipp Jahoda
 */
public class SugarStickChart extends StickBarLineChartBase<SticData> implements SticDataProvider {

    public SugarStickChart(Context context) {
        super(context);
    }

    public SugarStickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SugarStickChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private ChartTimeUtil mTimeClass;

    @Override
    protected void init() {
        super.init();
    }

    /**
     * 현재 차트 시간 받을수 있도록 만든 메소드
     * @param timeClass
     */
    public void setTimeClass(ChartTimeUtil timeClass) {
        mTimeClass = timeClass;
        super.init();

        mRenderer = new SugarStickChartRenderer(this, mAnimator, mViewPortHandler, getContext(), mTimeClass);

        getXAxis().setSpaceMin(0.5f);
        getXAxis().setSpaceMax(0.5f);
    }

    @Override
    public SticData getCandleData() {
        return mData;
    }
}
