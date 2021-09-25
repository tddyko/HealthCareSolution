
package com.google.android.gms.fit.samples.basichistoryapi.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;

import com.google.android.gms.fit.samples.basichistoryapi.R;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.animation.Easing;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.charts.RadarChart;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.AxisBase;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.Legend;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.MarkerView;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.XAxis;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.YAxis;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.RadarChartEntry;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.RadarData;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.RadarDataSet;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.formatter.IAxisValueFormatter;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;


public class RadarChartView {

    private RadarChart mChart;
    private Context mContext;

    private Typeface mTfRegular;
    private Typeface mTfLight;
    private String[] mActivities = new String[]{"Burger", "Steak", "Salad", "Pasta", "Pizza"};

    public RadarChartView(Context context, View view) {
        mContext = context;
//        setContentView(R.layout.activity_radarchart_noseekbar);

        mTfRegular = Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Light.ttf");

//        TextView tv = (TextView) view.findViewById(R.id.textView);
//        tv.setTypeface(mTfLight);
//        tv.setTextColor(Color.WHITE);
//        tv.setBackgroundColor(Color.rgb(60, 65, 82));

        mChart = (RadarChart) view.findViewById(R.id.chart1);
        mChart.setBackgroundColor(Color.rgb(60, 65, 82));

        mChart.getDescription().setEnabled(false);

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.LTGRAY);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.LTGRAY);
        mChart.setWebAlpha(100);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new RadarMarkerView(mContext, R.layout.radar_markerview);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        setData();

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

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.WHITE);

        YAxis yAxis = mChart.getYAxis();
        yAxis.setTypeface(mTfLight);
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTypeface(mTfLight);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setTextColor(Color.WHITE);
    }

    public void setData() {

        float mult = 80;
        float min = 20;
        int cnt = mActivities.length;

        ArrayList<RadarChartEntry> entries1 = new ArrayList<RadarChartEntry>();
        ArrayList<RadarChartEntry> entries2 = new ArrayList<RadarChartEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < cnt; i++) {
            float val1 = (float) (Math.random() * mult) + min;
            entries1.add(new RadarChartEntry(val1));

            float val2 = (float) (Math.random() * mult) + min;
            entries2.add(new RadarChartEntry(val2));
        }

        RadarDataSet set1 = new RadarDataSet(entries1, "Last Week");
        set1.setColor(Color.rgb(103, 110, 129));
        set1.setFillColor(Color.rgb(103, 110, 129));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        RadarDataSet set2 = new RadarDataSet(entries2, "This Week");
        set2.setColor(Color.rgb(121, 162, 175));
        set2.setFillColor(Color.rgb(121, 162, 175));
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData(sets);
        data.setValueTypeface(mTfLight);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.invalidate();
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.radar, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.actionToggleValues: {
//                for (IDataSet<?> set : mChart.getData().getDataSets())
//                    set.setDrawValues(!set.isDrawValuesEnabled());
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
//            case R.id.actionToggleRotate: {
//                if (mChart.isRotationEnabled())
//                    mChart.setRotationEnabled(false);
//                else
//                    mChart.setRotationEnabled(true);
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleFilled: {
//
//                ArrayList<IRadarDataSet> sets = (ArrayList<IRadarDataSet>) mChart.getData()
//                        .getDataSets();
//
//                for (IRadarDataSet set : sets) {
//                    if (set.isDrawFilledEnabled())
//                        set.setDrawFilled(false);
//                    else
//                        set.setDrawFilled(true);
//                }
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleHighlightCircle: {
//
//                ArrayList<IRadarDataSet> sets = (ArrayList<IRadarDataSet>) mChart.getData()
//                        .getDataSets();
//
//                for (IRadarDataSet set : sets) {
//                    set.setDrawHighlightCircleEnabled(!set.isDrawHighlightCircleEnabled());
//                }
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionSave: {
//                if (mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
//                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
//                            Toast.LENGTH_SHORT).show();
//                } else
//                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
//                            .show();
//                break;
//            }
//            case R.id.actionToggleXLabels: {
//                mChart.getXAxis().setEnabled(!mChart.getXAxis().isEnabled());
//                mChart.notifyDataSetChanged();
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleYLabels: {
//
//                mChart.getYAxis().setEnabled(!mChart.getYAxis().isEnabled());
//                mChart.invalidate();
//                break;
//            }
//            case R.id.animateX: {
//                mChart.animateX(1400);
//                break;
//            }
//            case R.id.animateY: {
//                mChart.animateY(1400);
//                break;
//            }
//            case R.id.animateXY: {
//                mChart.animateXY(1400, 1400);
//                break;
//            }
//            case R.id.actionToggleSpin: {
//                mChart.spin(2000, mChart.getRotationAngle(), mChart.getRotationAngle() + 360, Easing.EasingOption
//                        .EaseInCubic);
//                break;
//            }
//        }
//        return true;
//    }
}
