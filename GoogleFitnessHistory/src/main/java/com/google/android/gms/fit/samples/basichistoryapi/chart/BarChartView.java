package com.google.android.gms.fit.samples.basichistoryapi.chart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.android.gms.fit.samples.basichistoryapi.R;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.charts.BarChart;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.Legend;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.XAxis;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.YAxis;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.BarChartEntry;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.BarData;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.BarDataSet;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.ChartEntry;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.formatter.IAxisValueFormatter;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.highlight.Highlight;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.interfaces.datasets.IBarDataSet;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.listener.OnChartValueSelectedListener;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.utils.ColorTemplate;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.utils.MPPointF;
import com.google.android.gms.fit.samples.basichistoryapi.chart.valueFormat.AxisValueFormatter;
import com.google.android.gms.fit.samples.basichistoryapi.chart.valueFormat.BarDataFormatter;
import com.google.android.gms.fit.samples.basichistoryapi.chart.valueFormat.MyAxisValueFormatter;
import com.google.android.gms.fit.samples.basichistoryapi.chart.valueFormat.XYMarkerView;

import java.util.ArrayList;

public class BarChartView implements OnSeekBarChangeListener, OnChartValueSelectedListener {

    protected BarChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    private Context mContext;

    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    public BarChartView(Context context, View v) {
        mContext = context;

        mTfRegular = Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Light.ttf");

        tvX = (TextView) v.findViewById(R.id.tvXMax);
        tvY = (TextView) v.findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) v.findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) v.findViewById(R.id.seekBar2);

        mChart = (BarChart) v.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
//        mChart.setPinchZoom(false);
        mChart.setPinchZoom(true);
        mChart.setDoubleTapToZoomEnabled(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

//        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);
//        WeekAxisValueFormatter xAxisFormatter = new WeekAxisValueFormatter();   // 일월화수목금토일

        AxisValueFormatter xAxisFormatter = new AxisValueFormatter(null);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(20);
        xAxis.setLabelCount(15);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
//        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setTypeface(mTfLight);
//        rightAxis.setLabelCount(8, false);
//        rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);
//        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setEnabled(false);

        // 하단 설명 문구 (The Year 2017)
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

        // 차트 클릭시 나오는 마커
        XYMarkerView mv = new XYMarkerView(mContext, xAxisFormatter);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

//        mv.setEnabled(false);

        // DummyData
//        setData(6, 20000);

        // setting data
        mSeekBarY.setProgress(50);
        mSeekBarX.setProgress(12);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this);

        // mChart.setDrawLegend(false);
    }

    public void setXValueFormat(IAxisValueFormatter f) {
        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(f);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText("" + (mSeekBarX.getProgress() + 2));
        tvY.setText("" + (mSeekBarY.getProgress()));

//        setData(mSeekBarX.getProgress() + 1 , mSeekBarY.getProgress());

        mChart.invalidate();
    }

    public void invalidate() {
        if (mChart != null)
            mChart.invalidate();
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    public void animateXY() {
        mChart.animateXY(500, 500);
    }

    public void animateY() {
        mChart.animateY(500);
    }

    public void setData(ArrayList<BarChartEntry> yVals1) {
        BarDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "The year 2017");

            set1.setDrawIcons(false);

            // 그래프 색상 설정
            set1.setColors(ColorTemplate.MATERIAL_COLORS);
//            set1.setColor(Color.BLUE);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            // 그래프 두께 및 상단 값 세팅
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
//            data.setValueTextColor(Color.TRANSPARENT);
            data.setValueTextColor(Color.RED);
            data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new BarDataFormatter());

            mChart.setData(data);
        }
    }

//    private void setData(int count, float range) {
//
//        float start = 1f;
//
//        ArrayList<BarChartEntry> yVals1 = new ArrayList<BarChartEntry>();
//
//        for (int i = (int) start; i < start + count + 1; i++) {
//            float mult = (range + 1);
//            float val = (float) (Math.random() * mult);
//
//            if (Math.random() * 100 < 25) {
//                yVals1.add(new BarChartEntry(i, val, ContextCompat.getDrawable(mContext, android.R.drawable.btn_star)));
//            } else {
//                yVals1.add(new BarChartEntry(i, val));
//            }
//        }
//
//        BarDataSet set1;
//
//        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
//            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
//            set1.setValues(yVals1);
//            mChart.getData().notifyDataChanged();
//            mChart.notifyDataSetChanged();
//        } else {
//            set1 = new BarDataSet(yVals1, "The year 2017");
//
//            set1.setDrawIcons(false);
//
//            // 그래프 색상 설정
//            set1.setColors(ColorTemplate.MATERIAL_COLORS);
////            set1.setColor(Color.BLUE);
//
//            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
//            dataSets.add(set1);
//
//            // 그래프 두께 및 상단 값 세팅
//            BarData data = new BarData(dataSets);
//            data.setValueTextSize(10f);
////            data.setValueTextColor(Color.TRANSPARENT);
//            data.setValueTextColor(Color.RED);
//            data.setValueTypeface(mTfLight);
//            data.setBarWidth(0.9f);
//            data.setValueFormatter(new BarDataFormatter());
//
//            mChart.setData(data);
//        }
//    }

    protected RectF mOnValueSelectedRectF = new RectF();

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(ChartEntry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        mChart.getBarBounds((BarChartEntry) e, bounds);
        MPPointF position = mChart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + mChart.getLowestVisibleX() + ", high: "
                        + mChart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() { }



//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.actionToggleValues: {
//                for (IDataSet set : mChart.getData().getDataSets())
//                    set.setDrawValues(!set.isDrawValuesEnabled());
//
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleIcons: {
//                for (IDataSet set : mChart.getData().getDataSets())
//                    set.setDrawIcons(!set.isDrawIconsEnabled());
//
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleHighlight: {
//                if (mChart.getData() != null) {
//                    mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
//                    mChart.invalidate();
//                }
//                break;
//            }
//            case R.id.actionTogglePinch: {
//                if (mChart.isPinchZoomEnabled())
//                    mChart.setPinchZoom(false);
//                else
//                    mChart.setPinchZoom(true);
//
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleAutoScaleMinMax: {
//                mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
//                mChart.notifyDataSetChanged();
//                break;
//            }
//            case R.id.actionToggleBarBorders: {
//                for (IBarDataSet set : mChart.getData().getDataSets())
//                    ((BarDataSet) set).setBarBorderWidth(set.getBarBorderWidth() == 1.f ? 0.f : 1.f);
//
//                mChart.invalidate();
//                break;
//            }
//            case R.id.animateX: {
//                mChart.animateX(3000);
//                break;
//            }
//            case R.id.animateY: {
//                mChart.animateY(3000);
//                break;
//            }
//            case R.id.animateXY: {
//
//                mChart.animateXY(500, 500);
//                break;
//            }
//            case R.id.actionSave: {
//                if (mChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
//                    Toast.makeText(mContext, "Saving SUCCESSFUL!",
//                            Toast.LENGTH_SHORT).show();
//                } else
//                    Toast.makeText(mContext, "Saving FAILED!", Toast.LENGTH_SHORT)
//                            .show();
//                break;
//            }
//        }
//        return true;
//    }
}
