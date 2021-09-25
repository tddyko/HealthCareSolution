
package com.greencross.greencare.chartview.sugar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import com.greencross.greencare.R;
import com.greencross.greencare.base.value.TypeDataSet;
import com.greencross.greencare.charting.charts.SugarStickChart;
import com.greencross.greencare.charting.components.XAxis;
import com.greencross.greencare.charting.components.YAxis;
import com.greencross.greencare.charting.data.SticData;
import com.greencross.greencare.charting.data.SticDataSet;
import com.greencross.greencare.charting.data.SticEntry;
import com.greencross.greencare.charting.formatter.IAxisValueFormatter;
import com.greencross.greencare.util.ChartTimeUtil;
import com.greencross.greencare.util.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class SugarStickChartView {

    private final String TAG = SugarStickChartView.class.getSimpleName();
    private SugarStickChart mChart;

    private Context mContext;

    public SugarStickChartView(Context context, View view, ChartTimeUtil timeClass) {
        mContext = context;
        Typeface mTfRegular = Typeface.createFromAsset(mContext.getAssets(), context.getString(R.string.KelsonSansRegular));
        Typeface mTfLight = Typeface.createFromAsset(mContext.getAssets(), context.getString(R.string.KelsonSansLight));

        mChart = (SugarStickChart) view.findViewById(R.id.candle_stic_chart);
        mChart.setTimeClass(timeClass);

        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled(false);

        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);      // x축 기준 세로 라인 그리기
        xAxis.setLabelCount(15);

        final YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(20, false);
        leftAxis.setDrawGridLines(true);    // mrsohn 배경 라인 그리기
        leftAxis.setDrawAxisLine(true);     // Y축 경계 라인 그리기
        // 식전 60~240
        // 식후 120~240
        leftAxis.setAxisMinimum(60f);
        leftAxis.setAxisMaximum(240f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getLegend().setEnabled(false);
    }

    /**
     * X축 하단 라벨 설정
     * @param f
     */
    public void setXValueFormat(IAxisValueFormatter f) {
        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(f);
    }

    /**
     * X축 최대 값을 설정 한다
     * @param max
     */
    public void setXvalMinMax(float min, float max, int labelCnt) {
        XAxis xAxis = mChart.getXAxis();
        xAxis.setAxisMinimum(min);
        xAxis.setAxisMaximum(max);
        xAxis.setLabelCount(labelCnt);
    }

    public void setXvalMinMax(ChartTimeUtil timeClass) {
        TypeDataSet.Period period = timeClass.getPeriodType();
        if (period == TypeDataSet.Period.PERIOD_DAY) {
            setXvalMinMax(-1, 24, 24);
        } else  if (period == TypeDataSet.Period.PERIOD_WEEK) {
            setXvalMinMax(0, 8, 8);
        } else  if (period == TypeDataSet.Period.PERIOD_MONTH) {
            int maxX = timeClass.getStartTimeCal().getActualMaximum(Calendar.DAY_OF_MONTH)+1;
            setXvalMinMax(0, maxX, (maxX));
        } else  if (period == TypeDataSet.Period.PERIOD_YEAR) {
            setXvalMinMax(0, 13, 15);
        }
    }

    /**
     * Y축 최소값 설정
     * @param minVal
     */
    public void setYAxisMinimum(float minVal, float maxVal, int labelCnt) {
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(minVal);
        leftAxis.setAxisMaximum(maxVal);
        leftAxis.setLabelCount(labelCnt, false);
    }

    public void setLabelCnt(int cnt) {
        XAxis xAxis = mChart.getXAxis();
        xAxis.setLabelCount(cnt);
    }

    public void animateY() {
        mChart.animateY(500);
    }

    public void animateX() {
        mChart.animateX(500);
    }

    public void invalidate() {
        mChart.invalidate();
    }

    public void setTestData(int prog) {
        mChart.resetTracking();

        List<SticEntry> yVals1 = new ArrayList<SticEntry>();
        for (int i = 0; i < prog; i++) {
            float mult = 150;
            float val = (float) (Math.random() * 40) + mult;

            float high = (float) (Math.random() * 9) + 8f;
            float low = (float) (Math.random() * 9) + 8f;
            float open = (float) (Math.random() * 6) + 1f;

            boolean even = i % 2 == 0;
            Log.i("", "low["+i+"]="+(val - low)+", high="+(val + high)+", open="+(even ? val + open : val - open));

            yVals1.add(new SticEntry(i
                    , val + high
                    , 0 //val - low
                    , even ? val + open : val - open
            ));

        }
        setChartData(yVals1);
    }

    public void setData(List<SticEntry> yVals1) {
        if (yVals1.size() == 0) {
            Logger.e(TAG, "setTestData size zero");
            return;
        }
        mChart.resetTracking();
        setChartData(yVals1);
    }

    private void setChartData(List<SticEntry> yVals1) {
        SticDataSet set1 = new SticDataSet(yVals1, "Data Set");

        Logger.i(TAG, "setChartData.size="+yVals1.size());

        set1.setDrawIcons(false);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setShadowColor(Color.DKGRAY);
        set1.setShadowWidth(0.7f);
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.rgb(122, 242, 84));
        set1.setIncreasingPaintStyle(Paint.Style.STROKE);
        set1.setNeutralColor(Color.BLUE);

        SticData data = new SticData(set1);

        mChart.setData(data);
        mChart.invalidate();
    }
}