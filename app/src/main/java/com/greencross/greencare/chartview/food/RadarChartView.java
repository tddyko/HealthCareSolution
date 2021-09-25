
package com.greencross.greencare.chartview.food;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;

import com.greencross.greencare.R;
import com.greencross.greencare.charting.animation.Easing;
import com.greencross.greencare.charting.charts.RadarChart;
import com.greencross.greencare.charting.components.AxisBase;
import com.greencross.greencare.charting.components.Legend;
import com.greencross.greencare.charting.components.MarkerView;
import com.greencross.greencare.charting.components.XAxis;
import com.greencross.greencare.charting.components.YAxis;
import com.greencross.greencare.charting.data.RadarData;
import com.greencross.greencare.charting.data.RadarDataSet;
import com.greencross.greencare.charting.data.RadarEntry;
import com.greencross.greencare.charting.formatter.IAxisValueFormatter;
import com.greencross.greencare.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;
import java.util.List;


public class RadarChartView {

    private RadarChart mChart;

    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    public RadarChartView(Context context, View view) {

        mTfRegular = Typeface.createFromAsset(context.getAssets(), "Kelson-Sans-Regular.otf");
        mTfLight = Typeface.createFromAsset(context.getAssets(), "Kelson-Sans-Light.otf");

        mChart = (RadarChart) view.findViewById(R.id.radar_chart);

        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(false);

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.BLACK);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.RED);
        mChart.setWebAlpha(100);

        mChart.setRotationEnabled(false);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new RadarMarkerView(context, R.layout.radar_markerview);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        mChart.animateXY(
                1400, 1400,
                Easing.EasingOption.EaseInOutQuad,
                Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            private String[] mActivities = new String[]{"열량", "단백질", "나트륨", "지방", "탄수화물"};
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.BLACK);    // 글자 색

        YAxis yAxis = mChart.getYAxis();
        yAxis.setTypeface(mTfLight);
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);

        // 상단 설명 라벨 설정
        Legend l = mChart.getLegend();
        l.setEnabled(false);
    }
    public void setData(List<RadarEntry> entries1) {
        float maxVal = 0f;
        for (RadarEntry entry : entries1) {
            if (maxVal < entry.getValue())
                maxVal = entry.getValue();
        }
        // Dummy 값
        maxVal = maxVal <= 0f ? 100f : maxVal;

        YAxis yAxis = mChart.getYAxis();
        yAxis.setLabelCount(4, true);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setDrawLabels(false);

        RadarDataSet set1 = new RadarDataSet(entries1, "Food Radar Chart");
        set1.setColor(Color.rgb(103, 110, 129));        // 내부 값 라인 색깔
        set1.setFillColor(Color.rgb(103, 110, 129));    // 내부 값 채우기 색깔
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(true);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);

        RadarData data = new RadarData(sets);
        data.setValueTypeface(mTfLight);
        data.setValueTextSize(8f);
        data.setDrawValues(true);              // 내부 차트 안에 값 표시 여부
        data.setValueTextColor(Color.BLUE);

        mChart.setData(data);
        mChart.invalidate();
    }

    public void setTestData() {

        float mult = 80;
        float min = 20;
        int cnt = 5;

        List<RadarEntry> entries1 = new ArrayList<RadarEntry>();
        List<RadarEntry> entries2 = new ArrayList<RadarEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < cnt; i++) {
            float val1 = (float) (Math.random() * mult) + min;
            entries1.add(new RadarEntry(val1));

            float val2 = (float) (Math.random() * mult) + min;
            entries2.add(new RadarEntry(val2));
        }


        RadarDataSet set1 = new RadarDataSet(entries1, "Last Week");
        set1.setColor(Color.rgb(103, 110, 129));        // 내부 값 라인 색깔
        set1.setFillColor(Color.rgb(103, 110, 129));    // 내부 값 채우기 색깔
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(false);
        set1.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);

        RadarData data = new RadarData(sets);
        data.setValueTypeface(mTfLight);
        data.setValueTextSize(8f);
        data.setDrawValues(false);              // 내부 차트 안에 값 표시 여부
        data.setValueTextColor(Color.BLUE);

        mChart.setData(data);
        mChart.invalidate();
    }
}
