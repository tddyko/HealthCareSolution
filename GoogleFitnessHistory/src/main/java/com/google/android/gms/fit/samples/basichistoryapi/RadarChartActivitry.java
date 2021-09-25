package com.google.android.gms.fit.samples.basichistoryapi;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.android.gms.fit.samples.basichistoryapi.chart.RadarChartView;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.charts.RadarChart;


public class RadarChartActivitry extends AppCompatActivity {

    private RadarChart mChart;
    private Typeface mTfRegular;
    private Typeface mTfLight;

    private String[] mActivities = new String[]{"Burger", "Steak", "Salad", "Pasta", "Pizza"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_radarchart_noseekbar);

        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        new RadarChartView(RadarChartActivitry.this, getWindow().getDecorView());
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
