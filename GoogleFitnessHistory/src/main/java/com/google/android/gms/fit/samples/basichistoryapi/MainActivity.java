/*
 * Copyright (C) 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.fit.samples.basichistoryapi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fit.samples.basichistoryapi.chart.BarChartView;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.BarChartEntry;
import com.google.android.gms.fit.samples.basichistoryapi.chart.valueFormat.AxisValueFormatter;
import com.google.android.gms.fit.samples.common.logger.Log;
import com.google.android.gms.fit.samples.common.logger.LogView;
import com.google.android.gms.fit.samples.common.logger.LogWrapper;
import com.google.android.gms.fit.samples.common.logger.MessageOnlyLogFilter;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fit.samples.basichistoryapi.FitnessDataSet.Period.PERIOD_DAY;
import static com.google.android.gms.fit.samples.basichistoryapi.FitnessDataSet.Period.PERIOD_MONTH;
import static com.google.android.gms.fit.samples.basichistoryapi.FitnessDataSet.Period.PERIOD_WEEK;
import static com.google.android.gms.fit.samples.basichistoryapi.FitnessDataSet.Type.TYPE_CALORY;
import static com.google.android.gms.fit.samples.basichistoryapi.FitnessDataSet.Type.TYPE_STEP;
import static java.text.DateFormat.getDateTimeInstance;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "BasicHistoryApi";
    private static final int REQUEST_OAUTH = 1;
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";

    protected ArrayList<BarChartEntry> mYVals = new ArrayList<>();

    public RadioGroup mPariodRg;
    public RadioGroup mTypeRg;

    private int mArrIdx = 0;
    public TimeClass mTimeClass;
    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private static boolean authInProgress = false;

    public static GoogleApiClient mClient = null;
    protected BarChartView mBarChartView;

    protected Button mNextbtn;
    protected Button mPrebtn;
    private TextView mStartDateTv;
    private TextView mEndDateTv;


    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private OnDataPointListener mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeLogging();    // 로그 뷰

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
            // 차트 Fragment 세팅
//            getSupportFragmentManager().beginTransaction().add(R.id.container, new ColumnChartFragment()).commit();
        }

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            buildFitnessHistoryClient();
        }

        mTypeRg = (RadioGroup)findViewById(R.id.type_radio_group);
        mPariodRg = (RadioGroup)findViewById(R.id.interval_radio_group);

        mPrebtn = (Button) findViewById(R.id.pre_btn);
        mNextbtn = (Button) findViewById(R.id.next_btn);
        mStartDateTv = (TextView) findViewById(R.id.start_date_tv);
        mEndDateTv = (TextView) findViewById(R.id.end_date_tv);
        mBarChartView = new BarChartView(MainActivity.this, getWindow().getDecorView());

        mTimeClass = new TimeClass();

    }

    /**
     * 위치 정보 퍼미션
     * @return
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    protected void getTime() {
        if (mTimeClass != null)
            mTimeClass.getTime();
    }

    protected void clearCalTime() {
        if (mTimeClass != null)
            mTimeClass.clearTime();
    }

    protected void calTime(int calTime) {
        if (mTimeClass != null)
            mTimeClass.calTime(calTime);
    }

    private void buildFitnessHistoryClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Look at some data!!
//                                new InsertAndVerifyDataTask().execute();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.i(TAG, "Google Play services connection failed. Cause: " +
                                result.toString());
                        Snackbar.make(
                                MainActivity.this.findViewById(R.id.main_activity_view),
                                "Exception while connecting to Google Play services: " +
                                        result.getErrorMessage(),
                                Snackbar.LENGTH_INDEFINITE).show();
                    }
                })
                .build();
    }


    private void buildFitnessClientSensor() {
        if (mClient == null && checkPermissions()) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.SENSORS_API)
                    .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    Log.i(TAG, "Connected!!!");
                                    // Now you can make calls to the Fitness APIs.
                                    findFitnessDataSources();

                                    buildFitnessHistoryClient();
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                    } else if (i
                                            == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Log.i(TAG,
                                                "Connection lost.  Reason: Service Disconnected");
                                    }
                                }
                            }
                    )
                    .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Log.i(TAG, "Google Play services connection failed. Cause: " +
                                    result.toString());
                            Snackbar.make(
                                    MainActivity.this.findViewById(R.id.main_activity_view),
                                    "Exception while connecting to Google Play services: " +
                                            result.getErrorMessage(),
                                    Snackbar.LENGTH_INDEFINITE).show();
                        }
                    })
                    .build();
        }
    }

    private void findFitnessDataSources() {
        // [START find_data_sources]
        // Note: Fitness.SensorsApi.findDataSources() requires the ACCESS_FINE_LOCATION permission.
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                // At least one datatype must be specified.
                .setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
                // Can specify whether data type is raw or derived.
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            Log.i(TAG, "Data source found: " + dataSource.toString());
                            Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());

                            //Let's register a listener to receive Activity data!
                            if (dataSource.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)
                                    && mListener == null) {
                                Log.i(TAG, "Data source for LOCATION_SAMPLE found!  Registering.");
                                registerFitnessDataListener(dataSource,
                                        DataType.TYPE_LOCATION_SAMPLE);
                            }
                        }
                    }
                });
        // [END find_data_sources]
    }

    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        // [START register_data_listener]
        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(TAG, "Detected DataPoint value: " + val);
                }
            }
        };

        Fitness.SensorsApi.add(
                mClient,
                new SensorRequest.Builder()
                        .setDataSource(dataSource) // Optional but recommended for custom data sets.
                        .setDataType(dataType) // Can't be omitted.
                        .setSamplingRate(10, TimeUnit.SECONDS)
                        .build(),
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener registered!");
                        } else {
                            Log.i(TAG, "Listener not registered.");
                        }
                    }
                });
        // [END register_data_listener]
    }

    // 시작 시간으로 설정 00:00:00
    private void setStartTimeCal(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
    }

    // 마지막 시간으로 설정 11:59:59
    private void setLastTimeCal(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
    }


    /**
     * Return a {@link DataReadRequest} for all step count changes in the past week.
     */
    public DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        DataType dataType1 = null;
        DataType dataType2 = null;
//        TimeUnit timeUnit = mTimeClass.getTimeUnit();


        /* 조회 타입 설정(칼로리, 걸음수) */
        if (getType() == FitnessDataSet.Type.TYPE_CALORY) {
            // 칼로리
            dataType1 = DataType.TYPE_CALORIES_EXPENDED;
            dataType2 = DataType.AGGREGATE_CALORIES_EXPENDED;
        } else if (getType() == TYPE_STEP) {
            // 걸음수
            dataType1 = DataType.TYPE_STEP_COUNT_DELTA;
            dataType2 = DataType.AGGREGATE_STEP_COUNT_DELTA;
        }

//        /* 시간 설정 */
//        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
//        cal.setTime(now);
//        long endTime = cal.getTimeInMillis();
//
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        if (getPeriod() == PERIOD_DAY) {
//            // 일간
//            timeUnit = TimeUnit.HOURS;
//        } else if (getPeriod() == PERIOD_WEEK) {
//            // 주간
////            cal.add(Calendar.WEEK_OF_YEAR, -1);
//            cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK) -1));
//            timeUnit = TimeUnit.DAYS;
//        } else if (getPeriod() == PERIOD_MONTH) {
//            // 월간
//            cal.add(Calendar.DAY_OF_MONTH, -(cal.get(Calendar.DAY_OF_MONTH) -1));
//            timeUnit = TimeUnit.DAYS;
//        } else {
//            cal.add(Calendar.WEEK_OF_YEAR, -1);
//            timeUnit = TimeUnit.DAYS;
//        }
//
//        long startTime = cal.getTimeInMillis();

        long startTime = mTimeClass.getStartTime();
        long endTime = mTimeClass.getEndTime();

        DateFormat dateFormat = getDateTimeInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(dataType1, dataType2)
//                    .bucketByActivityType(1, TimeUnit.SECONDS)
//                .bucketByTime(1, timeUnit)
                .bucketByTime(1, mTimeClass.getTimeUnit())
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        return readRequest;
    }

    public FitnessDataSet.Period getPeriod() {

        FitnessDataSet.Period period = PERIOD_DAY;
        if (mPariodRg != null) {
            if (mPariodRg.getCheckedRadioButtonId() == R.id.radio_btn_day) {
                period = PERIOD_DAY;
            } else if (mPariodRg.getCheckedRadioButtonId() == R.id.radio_btn_week) {
                period = PERIOD_WEEK;
            } else if (mPariodRg.getCheckedRadioButtonId() == R.id.radio_btn_month) {
                period = PERIOD_MONTH;
            }
        }

        return period;
    }

    public FitnessDataSet.Type getType() {
        if (mTypeRg != null) {
            if (mTypeRg.getCheckedRadioButtonId() == R.id.radio_btn_calory) {
                return FitnessDataSet.Type.TYPE_CALORY;
            } else if (mTypeRg.getCheckedRadioButtonId() == R.id.radio_btn_step) {
                return FitnessDataSet.Type.TYPE_STEP;
            }
        }
        return FitnessDataSet.Type.TYPE_CALORY;
    }

    /**
     * 시간 관리 클래스
     */
    private class TimeClass {
        private int calTime = 0;

        private long startTime;
        private long endTime;
        private TimeUnit timeUnit;

        public TimeClass() {
            getTime();
        }

        public void clearTime() {
            setCalTime(0);
        }

        public void calTime(int calTime) {
            setCalTime(getCalTime() + calTime);
            getTime();
        }

        public void getTime() {
            /* 시간 설정 */
            Calendar cal = Calendar.getInstance(Locale.KOREA);
            Date now = new Date();
            cal.setTime(now);
            long endTime = cal.getTimeInMillis();

            if (getPeriod() == PERIOD_DAY) {
                // 일간
                setTimeUnit(TimeUnit.HOURS);
                if (getCalTime() != 0) {
                    cal.add(Calendar.DATE, getCalTime());
                    setLastTimeCal(cal);
                }
                endTime = cal.getTimeInMillis();
            } else if (getPeriod() == PERIOD_WEEK) {
                // 주간
                setTimeUnit(TimeUnit.DAYS);
                if (getCalTime() == 0) {
                    int startDayOfWeek = -(cal.get(Calendar.DAY_OF_WEEK) -1);
                    cal.add(Calendar.DAY_OF_WEEK, startDayOfWeek);
                } else {
                    int minusDay = 0;
                    if (getCalTime() != -1) {
                        minusDay = 7*(getCalTime()+1);
                    }

                    minusDay = minusDay - cal.get(Calendar.DAY_OF_WEEK);
                    cal.add(Calendar.DATE, minusDay);
                    setLastTimeCal(cal);
                    endTime = cal.getTimeInMillis();

                    Log.i(TAG, "minusDay="+minusDay);
                    cal.add(Calendar.DATE, -6);
                }
            } else if (getPeriod() == PERIOD_MONTH) {
                // 월간
                setTimeUnit(TimeUnit.DAYS);

                if (getCalTime() == 0) {
                    cal.add(Calendar.DAY_OF_MONTH, -(cal.get(Calendar.DAY_OF_MONTH) -1 ));
                } else {
                    cal.add(Calendar.MONTH, getCalTime());
                    cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    setLastTimeCal(cal);
                    endTime = cal.getTimeInMillis();

                    cal.set(Calendar.DATE, cal.getMinimum(Calendar.DAY_OF_MONTH));//1일로 설정

                    Log.i(TAG, "month="+cal.get(Calendar.MONTH)+", maximum="+cal.getActualMaximum(Calendar.DAY_OF_MONTH)+", minuteMum="+cal.getMinimum(Calendar.DAY_OF_MONTH));
                }
            } else {
                cal.add(Calendar.WEEK_OF_YEAR, -1);
                setTimeUnit(TimeUnit.DAYS);
            }

            setStartTimeCal(cal);
            long startTime = cal.getTimeInMillis();

            DateFormat dateFormat = getDateTimeInstance();
            Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
            Log.i(TAG, "Range End: " + dateFormat.format(endTime));

            mStartDateTv.setText(dateFormat.format(startTime));
            mEndDateTv.setText(dateFormat.format(endTime));

            setStartTime(startTime);
            setEndTime(endTime);

            mBarChartView.setXValueFormat(new AxisValueFormatter(getPeriod()));
        }

        public int getCalTime() {
            return calTime;
        }
        public void setCalTime(int calTime) {
            this.calTime = calTime;
            if (calTime ==0 ) {
                mNextbtn.setEnabled(false);
            } else {
                mNextbtn.setEnabled(true);
            }
        }
        public TimeUnit getTimeUnit() {
            return timeUnit;
        }
        public void setTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
        }
        public long getStartTime() {
            return startTime;
        }
        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }
        public long getEndTime() {
            return endTime;
        }
        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }
    }

    public void printData(DataReadResult dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        mArrIdx = 0;
        if (dataReadResult.getBuckets().size() > 0) {
//            Log.i(TAG, "Number of returned buckets of DataSets is: "
//                    + dataReadResult.getBuckets().size());
            Log.i(TAG, "BucketSize="+ dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }

            // 주간 조회시 남은 주간 채워주기
            if (getPeriod() == PERIOD_WEEK) {

                int max = 7 - (mYVals.size());
                for (int i = 0; i < max; i++) {
//                    mCaloryArr.add(0f);
                    mYVals.add(new BarChartEntry(mArrIdx++, 0f));
                    Log.i(TAG, "PERIOD_WEEK.size="+mYVals.size()+", idx="+mArrIdx);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
//            Log.i(TAG, "Number of returned DataSets is: "
//                    + dataReadResult.getDataSets().size());

            Log.i(TAG, "DataSetsSize="+ dataReadResult.getBuckets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    private void dumpDataSet(DataSet dataSet) {
//        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        Log.i(TAG, "====================");
//        DateFormat dateFormat = getTimeInstance();
        DateFormat dateFormat = getDateTimeInstance();

//        mCaloryArr.add(0f);
        mYVals.add(new BarChartEntry(mArrIdx, 0f));

        for (DataPoint dp : dataSet.getDataPoints()) {
//            Log.i(TAG, "Data point:");
//            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
//                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                Log.i(TAG, "\t Value: " + dp.getValue(field)+"["+ mArrIdx +"]");
                if (getType() == TYPE_CALORY) {
//                    mCaloryArr.set(mArrIdx, dp.getValue(field).asFloat());
                    mYVals.set(mArrIdx, new BarChartEntry(mArrIdx, dp.getValue(field).asFloat()));
                } else if (getType() == TYPE_STEP) {
                    int steps = 0;
                    try {
                        steps = dp.getValue(field).asInt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    mCaloryArr.set(mArrIdx, (float)steps);
                    mYVals.set(mArrIdx, new BarChartEntry(mArrIdx, (float)steps));
                }
            }
        }

        mArrIdx++;
    }
    // [END parse_dataset]

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.action_delete_data) {
////            deleteData();
//            return true;
//        } else
        if (id == R.id.action_update_data){
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  Initialize a custom log class that outputs both to in-app targets and logcat.
     */
    private void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);
        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);
        // On screen logging via a customized TextView.
        LogView logView = (LogView) findViewById(R.id.sample_logview);

        // Fixing this lint error adds logic without benefit.
        //noinspection AndroidLintDeprecation
        logView.setTextAppearance(this, R.style.Log);

        logView.setBackgroundColor(Color.WHITE);
        msgFilter.setNext(logView);
        Log.i(TAG, "Ready.");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
        buildFitnessClientSensor();
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.main_activity_view),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                buildFitnessClientSensor();
            } else {
                // Permission denied.
                Snackbar.make(
                        findViewById(R.id.main_activity_view),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

}
