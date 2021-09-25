package com.greencross.greencare.googleFitness;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.util.Logger;

import java.text.DateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getTimeInstance;


/**
 * Created by MrsWin on 2017-05-02.
 */

public class GoogleFitInstance {
    private final String TAG = GoogleFitInstance.class.getSimpleName();

    private static BaseFragment mBaseFragment;
    private OnDataPointListener mListener;

    public GoogleFitInstance(BaseFragment baseFragment) {
        mBaseFragment = baseFragment;
    }

    public void findFitnessDataSources(final GoogleApiClient client, final ApiData.IStep callBack) {
        // [START find_data_sources]
        // Note: Fitness.SensorsApi.findDataSources() requires the ACCESS_FINE_LOCATION permission.
        Fitness.SensorsApi.findDataSources(client, new DataSourcesRequest.Builder()
                // At least one datatype must be specified.
                .setDataTypes(DataType.TYPE_STEP_COUNT_CADENCE)
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                .setDataTypes(DataType.TYPE_CALORIES_EXPENDED)
                .setDataTypes(DataType.AGGREGATE_CALORIES_EXPENDED)
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
                            if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA) && mListener == null) {
                                Log.i(TAG, "Data source for LOCATION_SAMPLE found!  Registering.");
                            }
                        }

                        registerFitnessDataListener();
                        subscribeRecordingApi(client);
                        callBack.next(null);

                    }
                });

    }

    private void dumpDataSet(DataSet dataSet) {
        Logger.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        for (DataPoint dp : dataSet.getDataPoints()) {
            DateFormat dateFormat = getTimeInstance();
            Logger.i(TAG, "Data point:");
            Logger.i(TAG, "\tType: " + dp.getDataType().getName());
            Logger.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Logger.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Logger.i(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }
        }
    }

    private void dumpSession(Session session) {
        DateFormat dateFormat = getTimeInstance();
        Logger.i(TAG, "Data returned for Session: " + session.getName()
                + "\n\tDescription: " + session.getDescription()
                + "\n\tStart: " + dateFormat.format(session.getStartTime(TimeUnit.MILLISECONDS))
                + "\n\tEnd: " + dateFormat.format(session.getEndTime(TimeUnit.MILLISECONDS)));
    }

    /**
     * register_data_listener
     */
    private void registerFitnessDataListener() {
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
    }

    /**
     * 기록 API 활성
     * 피트니스 데이터가 기록 됨
     *  To create a subscription, invoke the Recording API. As soon as the subscription is
     *  active, fitness data will start recording.
     * @param client
     */
    public void subscribeRecordingApi(GoogleApiClient client) {

        DataType[] dataTypes = {
                DataType.TYPE_STEP_COUNT_DELTA
                ,DataType.AGGREGATE_STEP_COUNT_DELTA
                ,DataType.TYPE_CALORIES_EXPENDED
                ,DataType.AGGREGATE_CALORIES_EXPENDED
                ,DataType.TYPE_ACTIVITY_SAMPLES
                ,DataType.TYPE_ACTIVITY_SEGMENT
                ,DataType.AGGREGATE_ACTIVITY_SUMMARY
        };

        for (DataType dataType : dataTypes) {
            Fitness.RecordingApi.subscribe(client, dataType).setResultCallback(mSubcribeResultCallback);
        }
    }

    ResultCallback mSubcribeResultCallback = new ResultCallback<Status>() {
        @Override
        public void onResult(Status status) {
            if (status.isSuccess()) {
                if (status.getStatusCode() == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                    Log.i(TAG, "Existing subscription for activity detected.");
                } else {
                    Log.i(TAG, "Successfully subscribed!");
                }
            } else {
                Log.w(TAG, "There was a problem subscribing.");
            }
        }
    };

    /**
     * 총걸음수, 칼로리 구하기
     */
    public void totalValDataTask(final GoogleApiClient client, final DataType dataType, final ApiData.IStep callBack) {
        new TotalValDataTask(client, dataType, callBack).execute();
    }
    private class TotalValDataTask extends AsyncTask<Object, Object, Long> {
        private GoogleApiClient client = null;
        private DataType dataType;
        private ApiData.IStep callBack;

        public TotalValDataTask(final GoogleApiClient client, DataType dataType, ApiData.IStep callBack) {
            this.client = client;
            this.dataType = dataType;
            this.callBack = callBack;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Long doInBackground(Object... params) {
            long total = 0;
            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(client, dataType);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                if (dataType == DataType.TYPE_STEP_COUNT_DELTA
                    || dataType == DataType.TYPE_STEP_COUNT_CADENCE
                    || dataType == DataType.AGGREGATE_STEP_COUNT_DELTA
                        ) {
                    total = totalSet.isEmpty()
                            ? 0
                            : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                } else if (dataType == DataType.TYPE_CALORIES_EXPENDED
                        || dataType == DataType.AGGREGATE_CALORIES_EXPENDED) {
                    float calorie = totalSet.isEmpty()
                            ? 0
                            : totalSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat();
                    total = (long) calorie;
                } else if (dataType == DataType.AGGREGATE_ACTIVITY_SUMMARY) {

                }
            } else {
                Logger.w(TAG, "There was a problem getting the step count.");
            }
            Logger.i(TAG, "TotalValDataTask.total="+total+", "+dataType.getName());

            return total;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            if (callBack != null)
                callBack.next(aLong);
        }
    }

    /**
     * 총걸음수, 칼로리 구하기
     */
    public void totalReqDataTask(final GoogleApiClient client, DataReadRequest readRequest, final ApiData.IStep callBack) {
        new TotalValReqDataTask(client, readRequest, callBack).execute();
    }
    private class TotalValReqDataTask extends AsyncTask<Object, Object, Long> {
        private GoogleApiClient client = null;
        private DataReadRequest readRequest;
        private ApiData.IStep callBack;

        public TotalValReqDataTask(final GoogleApiClient client, DataReadRequest readRequest, ApiData.IStep callBack) {
            this.client = client;
            this.readRequest = readRequest;
            this.callBack = callBack;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Long doInBackground(Object... params) {
            long total = 0;

            PendingResult<DataReadResult> result = Fitness.HistoryApi.readData(client, readRequest);
            DataReadResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                List<DataSet> totalSet = totalResult.getDataSets();
                for (DataSet dataSet : totalSet) {
                    Logger.i(TAG, "dataSet="+dataSet);
                }

            } else {
                Logger.w(TAG, "There was a problem getting the step count.");
            }

            return total;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            if (callBack != null)
                callBack.next(aLong);
        }
    }
}
