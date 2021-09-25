
package com.greencross.greencare.chartview.weight;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.charting.charts.WeightChart;
import com.greencross.greencare.charting.components.Legend;
import com.greencross.greencare.charting.components.XAxis;
import com.greencross.greencare.charting.components.YAxis;
import com.greencross.greencare.charting.data.BarData;
import com.greencross.greencare.charting.data.BarDataSet;
import com.greencross.greencare.charting.data.BarEntry;
import com.greencross.greencare.charting.formatter.IAxisValueFormatter;
import com.greencross.greencare.charting.interfaces.datasets.IBarDataSet;
import com.greencross.greencare.chartview.valueFormat.AxisValueFormatter;
import com.greencross.greencare.chartview.valueFormat.BarDataFormatter;
import com.greencross.greencare.chartview.valueFormat.MyAxisValueFormatter;
import com.greencross.greencare.chartview.valueFormat.XYMarkerView;
import com.greencross.greencare.base.value.TypeDataSet;

import java.util.ArrayList;
import java.util.List;


public class WeightChartView {

    protected WeightChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    private Context mContext;

    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    public WeightChartView(Context context, View v) {
        mContext = context;

        mTfRegular = Typeface.createFromAsset(mContext.getAssets(), "Kelson-Sans-Regular.otf");
        mTfLight = Typeface.createFromAsset(mContext.getAssets(), "Kelson-Sans-Light.otf");

        tvX = (TextView) v.findViewById(R.id.tvXMax);
        tvY = (TextView) v.findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) v.findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) v.findViewById(R.id.seekBar2);

        mChart = (WeightChart) v.findViewById(R.id.chart1);
        mChart.setTouchEnabled(false);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);
        mChart.getDefaultValueFormatter();

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        mChart.setDoubleTapToZoomEnabled(false);

        mChart.setDrawGridBackground(false);

        AxisValueFormatter xAxisFormatter = new AxisValueFormatter(TypeDataSet.Period.PERIOD_DAY);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(15);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        // 하단 설명 문구 (bottom label)
        Legend l = mChart.getLegend();
        l.setEnabled(false);

        // 차트 클릭시 나오는 마커
        XYMarkerView mv = new XYMarkerView(mContext, xAxisFormatter);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        // setting data
        mSeekBarY.setProgress(50);
        mSeekBarX.setProgress(12);
    }

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

    public void invalidate() {
        if (mChart != null)
            mChart.invalidate();
    }


    public void animateXY() {
        mChart.animateXY(500, 500);
    }

    public void animateY() {
        mChart.animateY(500);
    }

    public void setData(List<BarEntry> yVals1) {
        BarDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "bottom label");

            set1.setDrawIcons(false);

            // 그래프 색상 설정
            set1.setColor(ContextCompat.getColor(mContext, R.color.colorMain));

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            // 그래프 두께 및 상단 값 세팅
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);
            data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new BarDataFormatter());

            mChart.setData(data);
        }
    }

    public void setTestData(int count, float range) {

        float start = 0f;

        List<BarEntry> yVals1 = new ArrayList<>();
        for (int i = (int) start; i < start + count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);

            if (Math.random() * 100 < 25) {
                yVals1.add(new BarEntry(i, val, ContextCompat.getDrawable(mContext, android.R.drawable.btn_star)));
            } else {
                yVals1.add(new BarEntry(i, val));
            }
        }

        BarDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "bottom label");

            set1.setDrawIcons(false);

            // 그래프 색상 설정
            set1.setColor(ContextCompat.getColor(mContext, R.color.colorMain));

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            // 그래프 두께 및 상단 값 세팅
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.RED);
            data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new BarDataFormatter());

            mChart.setData(data);
        }
    }

    protected RectF mOnValueSelectedRectF = new RectF();
}
