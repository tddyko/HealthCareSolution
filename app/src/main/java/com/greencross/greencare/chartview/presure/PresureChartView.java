
package com.greencross.greencare.chartview.presure;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.greencross.greencare.R;
import com.greencross.greencare.base.value.TypeDataSet;
import com.greencross.greencare.charting.charts.PressureStickChart;
import com.greencross.greencare.charting.components.XAxis;
import com.greencross.greencare.charting.components.YAxis;
import com.greencross.greencare.charting.data.PressureData;
import com.greencross.greencare.charting.data.PressureDataSet;
import com.greencross.greencare.charting.data.PressureEntry;
import com.greencross.greencare.charting.formatter.IAxisValueFormatter;
import com.greencross.greencare.util.ChartTimeUtil;
import com.greencross.greencare.util.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PresureChartView {

    private static final String TAG = PresureChartView.class.getSimpleName();
    private PressureStickChart mChart;

    private Context mContext;

    public PresureChartView(Context context, View view) {
        mContext = context;
        Typeface mTfRegular = Typeface.createFromAsset(mContext.getAssets(), "Kelson-Sans-Regular.otf");
        Typeface mTfLight = Typeface.createFromAsset(mContext.getAssets(), "Kelson-Sans-Light.otf");

        mChart = (PressureStickChart) view.findViewById(R.id.presure_stic_chart);

        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);      // x축 기준 세로 라인 그리기

        int yLabelCnt = 12;

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(yLabelCnt);
        leftAxis.setDrawGridLines(true);    // mrsohn 배경 라인 그리기
        leftAxis.setDrawAxisLine(true);     // Y축 경계 라인 그리기

        //혈압 30~210 고정
        leftAxis.setAxisMinimum(30);
        leftAxis.setAxisMaximum(210);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(true);
        rightAxis.setDrawLabels(false);
        rightAxis.setInverted(true);
        rightAxis.setLabelCount(yLabelCnt);
        rightAxis.setAxisMinimum(30);
        rightAxis.setAxisMaximum(210);

        // setting data
        mChart.getLegend().setEnabled(false);
    }

    public void setData(List<PressureEntry> yVals1, ChartTimeUtil timeClass) {
        if (yVals1.size() == 0) {
            Logger.e(TAG, "PressureEntry size is zero");
            return;
        }
        mChart.setTimeClass(timeClass);

        mChart.resetTracking();

        PressureDataSet set1 = new PressureDataSet(yVals1, "Data Set");

        set1.setDrawIcons(false);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setShadowColor(Color.DKGRAY);
        set1.setShadowWidth(0.7f);
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.rgb(122, 242, 84));
        set1.setIncreasingPaintStyle(Paint.Style.STROKE);
        set1.setNeutralColor(Color.BLUE);

        PressureData data = new PressureData(set1);

        mChart.setData(data, timeClass);
        mChart.invalidate();

    }

    public void setXvalMinMax(ChartTimeUtil timeClass) {
        TypeDataSet.Period period = timeClass.getPeriodType();
        if (period == TypeDataSet.Period.PERIOD_DAY) {
            setXvalMinMax(-1, 24, 24);
        } else  if (period == TypeDataSet.Period.PERIOD_WEEK) {
            setXvalMinMax(0, 8, 8);
        } else  if (period == TypeDataSet.Period.PERIOD_MONTH) {
            int maxX = timeClass.getStartTimeCal().getActualMaximum(Calendar.DAY_OF_MONTH)+1;
            setXvalMinMax(0, maxX+1, maxX+1);
        } else  if (period == TypeDataSet.Period.PERIOD_YEAR) {
            setXvalMinMax(0, 13, 15);
        }
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

    /**
     * X축 라벨 숫자 세팅
     * @param cnt
     */
    public void setLabelCnt(int cnt) {
        XAxis xAxis = mChart.getXAxis();
        xAxis.setLabelCount(cnt);
    }


    public void setTestData(int prog, int yVal) {
        mChart.resetTracking();

        List<PressureEntry> yVals1 = new ArrayList<>();

        for (int i = 0; i < prog; i++) {
            float mult = (yVal + 1);
            float val = (float) (Math.random() * 40) + mult;

            float high = (float) (Math.random() * 9) + 8f;
            float open = (float) (Math.random() * 6) + 1f;

            float close = (float) (Math.random() * 6) + 1f;
            float low = (float) (Math.random() * 9) + 8f;

            yVals1.add(new PressureEntry(
                    i, val + high,
                    val - low-20,
                    val - open, //even ? val + open : val - open,
                    val - close-20,
                    ContextCompat.getDrawable(mContext, R.drawable.icon_new)
            ));
        }

        PressureDataSet set1 = new PressureDataSet(yVals1, "Data Set");

        set1.setDrawIcons(false);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setShadowColor(Color.DKGRAY);
        set1.setShadowWidth(0.7f);
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.rgb(122, 242, 84));
        set1.setIncreasingPaintStyle(Paint.Style.STROKE);
        set1.setNeutralColor(Color.BLUE);

        PressureData data = new PressureData(set1);

        mChart.setData(data);
        mChart.invalidate();

    }

    public void setXValueFormat(IAxisValueFormatter f) {
        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(f);
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

}
