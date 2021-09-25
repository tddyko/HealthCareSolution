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
package com.greencross.greencare.step;

import android.Manifest;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.result.DataReadResult;
import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.base.value.TypeDataSet;
import com.greencross.greencare.bluetooth.manager.DeviceDataUtil;
import com.greencross.greencare.bluetooth.model.BandModel;
import com.greencross.greencare.charting.data.BarEntry;
import com.greencross.greencare.chartview.valueFormat.AxisValueFormatter;
import com.greencross.greencare.chartview.walk.BarChartView;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperPPG;
import com.greencross.greencare.database.DBHelperStep;
import com.greencross.greencare.database.DBHelperWeight;
import com.greencross.greencare.googleFitness.GoogleFitInstance;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.ChartTimeUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.SharedPref;
import com.greencross.greencare.util.StringUtil;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.greencross.greencare.R.id.chart_unit;
import static com.greencross.greencare.base.value.TypeDataSet.Period.PERIOD_DAY;
import static com.greencross.greencare.base.value.TypeDataSet.Period.PERIOD_MONTH;
import static com.greencross.greencare.base.value.TypeDataSet.Period.PERIOD_WEEK;
import static com.greencross.greencare.base.value.TypeDataSet.Type.TYPE_CALORY;
import static com.greencross.greencare.base.value.TypeDataSet.Type.TYPE_STEP;
import static com.greencross.greencare.util.StringUtil.getFloat;
import static java.text.DateFormat.getDateTimeInstance;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class GoogleFitChartView {
    public static final String TAG = GoogleFitChartView.class.getSimpleName();

    protected List<BarEntry> mYVals = new ArrayList<>();
    protected Map<Integer, Integer> mGoogleFitStepMap = new HashMap<>();           // 구글 피트니스 조회 데이터 저장
    private Map<Integer, Integer> mDbResultMap = new HashMap<>();   // Sqlite 조회 내용 저장

    private BaseFragment mBaseFragment;
    private Context mContext;

    public RadioGroup mTypeRg;

    private boolean mChartType;

    private int mArrIdx = 0;
    public ChartTimeUtil mTimeClass;

    public static GoogleApiClient mClient = null;
    protected BarChartView mChart;

    protected ImageButton mNextbtn;
    protected ImageButton mPrebtn;
    private TextView mDateTv;

    private Long mTotalVal = 0L;
    private DecimalFormat mFloatFormat = new DecimalFormat("#,###.00");

    private TextView tvStepBoxTitle01;
    private TextView tvStepBoxTitle02;
    private TextView tvStepBoxTitle03;
    private TextView tvStepBoxTitle04;
    private TextView tvStepBoxValue01;
    private TextView tvStepBoxValue02;
    private TextView tvStepBoxValue03;
    private TextView tvStepBoxValue04;

    private ImageView IvStepBoxImage02;
    private ImageView IvStepBoxImage03;
    private ImageView IvStepBoxImage04;

    private TextView tvActiveTitle;
    private TextView tvActiveValue;
    private TextView tvActiveValueTag;
    private TextView tvTargetValue;
    private TextView tvTargetValueTag;
    private TextView chartunit;

    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private OnDataPointListener mListener;

    int tmp=0;
    String _startDate;
    String _endDate;

    public GoogleFitChartView(BaseFragment baseFragment, View view) {
        mContext = baseFragment.getContext();
        mBaseFragment = baseFragment;
        initView(view);
    }

    private void initView(View view) {
        mChartType = false;

        mTypeRg = (RadioGroup) view.findViewById(R.id.type_radio_group);
        RadioButton radioBtnCal = (RadioButton) view.findViewById(R.id.radio_btn_calory);
        radioBtnCal.setChecked(true);

        mPrebtn = (ImageButton) view.findViewById(R.id.pre_btn);
        mNextbtn = (ImageButton) view.findViewById(R.id.next_btn);
        mDateTv = (TextView) view.findViewById(R.id.period_date_textview);

        RadioGroup periodRg = (RadioGroup) view.findViewById(R.id.period_radio_group);
        RadioButton radioBtnDay = (RadioButton) view.findViewById(R.id.period_radio_btn_day);
        RadioButton radioBtnWeek = (RadioButton) view.findViewById(R.id.period_radio_btn_week);
        RadioButton radioBtnMonth = (RadioButton) view.findViewById(R.id.period_radio_btn_month);
        mTimeClass = new ChartTimeUtil(radioBtnDay, radioBtnWeek, radioBtnMonth);


        mChart = new BarChartView(mContext, view);
        mChart.setDefaultDummyData(mTimeClass);

        tvStepBoxTitle01 = (TextView) view.findViewById(R.id.tvStepBoxTitle01);   //목표 달성율
        tvStepBoxTitle02 = (TextView) view.findViewById(R.id.tvStepBoxTitle02);   //총이동거리
        tvStepBoxTitle03 = (TextView) view.findViewById(R.id.tvStepBoxTitle03);   //칼로리비교
        tvStepBoxTitle04 = (TextView) view.findViewById(R.id.tvStepBoxTitle04);   //최장연속활동시간
        tvStepBoxValue01 = (TextView) view.findViewById(R.id.tvStepBoxValue01);   // 타이틀
        tvStepBoxValue02 = (TextView) view.findViewById(R.id.tvStepBoxValue02);
        tvStepBoxValue03 = (TextView) view.findViewById(R.id.tvStepBoxValue03);
        tvStepBoxValue04 = (TextView) view.findViewById(R.id.tvStepBoxValue04);

        IvStepBoxImage02 = (ImageView) view.findViewById(R.id.IvStepBoxImage02);
        IvStepBoxImage03 = (ImageView) view.findViewById(R.id.IvStepBoxImage03);
        IvStepBoxImage04 = (ImageView) view.findViewById(R.id.IvStepBoxImage04);

        chartunit = (TextView) view.findViewById(chart_unit);
        tvActiveTitle = (TextView) view.findViewById(R.id.tvActiveTitle);
        tvActiveValue = (TextView) view.findViewById(R.id.tvActiveValue);
        tvActiveValueTag = (TextView) view.findViewById(R.id.tvActiveValueaTag);
        tvTargetValue = (TextView) view.findViewById(R.id.tvTargetValue);
        tvTargetValueTag = (TextView) view.findViewById(R.id.tvTargetValueTag);

        mPrebtn.setOnClickListener(mClickListener);
        mNextbtn.setOnClickListener(mClickListener);
        periodRg.setOnCheckedChangeListener(mCheckedChangeListener);
        mTypeRg.setOnCheckedChangeListener(mTypeCheckedChangeListener);

        mBaseFragment.showProgress();
        setNextButtonVisible();
    }

    /**
     * 걸음수 조회 하기
     */
    public DataReadRequest queryFitnessData() {
        DataType dataType1 = null;
        DataType dataType2 = null;

        /* 조회 타입 설정(칼로리, 걸음수) */
        if (getType() == TYPE_CALORY) {
            // 칼로리
            dataType1 = DataType.TYPE_STEP_COUNT_DELTA;
            dataType2 = DataType.AGGREGATE_STEP_COUNT_DELTA;
        } else if (getType() == TYPE_STEP) {
            // 걸음수
            dataType1 = DataType.TYPE_STEP_COUNT_DELTA;
            dataType2 = DataType.AGGREGATE_STEP_COUNT_DELTA;
        }

        long startTime = mTimeClass.getStartTime();
        long endTime = mTimeClass.getEndTime();

        DateFormat dateFormat = getDateTimeInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(dataType1, dataType2)
                .bucketByTime(1, mTimeClass.getTimeUnit())
                .setTimeRange(startTime, endTime, MILLISECONDS)
                .build();

        return readRequest;
    }

    public RadioGroup.OnCheckedChangeListener mCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // 일간, 주간, 월간
            TypeDataSet.Period periodType = mTimeClass.getPeriodType();
            mTimeClass.clearTime();         // 날자 초기화

            AxisValueFormatter formatter = new AxisValueFormatter(periodType);
            mChart.setXValueFormat(formatter);

            if (mYVals != null)
                mYVals.clear();

            getData();
        }
    };

    public RadioGroup.OnCheckedChangeListener mTypeCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            getTime();

            if (mYVals != null)
                mYVals.clear();

            getData();
        }
    };


    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.pre_btn) {
                mTimeClass.calTime(-1);
            } else if (vId == R.id.next_btn) {
                // 초기값 일때 다음날 데이터는 없으므로 리턴
                if (mTimeClass.getCalTime() == 0)
                    return;

                mTimeClass.calTime(1);
            }

            if (mYVals != null)
                mYVals.clear();

            getData();
            setNextButtonVisible();
        }
    };

    private void setNextButtonVisible(){
        // 초기값 일때 다음날 데이터는 없으므로 리턴
        if (mTimeClass.getCalTime() == 0) {
            mNextbtn.setVisibility(View.INVISIBLE);
        }else{
            mNextbtn.setVisibility(View.VISIBLE);
        }
    }


    protected void getTime() {
        if (mTimeClass != null)
            mTimeClass.getTime();
    }

    /**
     * 구글 API 세팅
     */
    private void googleApiSetting() {
        buildFitnessClientApis(new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                getData();
            }
        });
    }

    /**
     * 날자 계산 후 조회
     */
    private void getData() {
        if (mClient == null) {
            googleApiSetting();
            return;
        }

        // 활동소모 초기화
        mFoodCalories = new ArrayList<>();
        String[] name = mContext.getResources().getStringArray(R.array.life_food);
        String[] unit = mContext.getResources().getStringArray(R.array.life_food_unit);
        String[] value = mContext.getResources().getStringArray(R.array.life_food_calorie);

        for (int i = 0; i < name.length; i++)
            mFoodCalories.add(new FoodCalories(name[i], unit[i], Integer.valueOf(value[i])));

        long startTime = mTimeClass.getStartTime();
        long endTime = mTimeClass.getEndTime();

        String format = "yyyy.MM.dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        _startDate = sdf.format(startTime);
        _endDate = sdf.format(endTime);

        if (mTimeClass.getPeriodType() == TypeDataSet.Period.PERIOD_DAY) {
            mDateTv.setText(_startDate);
        } else {
            mDateTv.setText(_startDate + " ~ " + _endDate);
        }

        new QeuryVerifyDataTask().execute();
    }
    /**
     * 구글피트니스 데이터 조회 하기
     */
    public class QeuryVerifyDataTask extends AsyncTask<Void, Void, Void> {
        DataReadRequest readRequest;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setBottomField();
            mBaseFragment.showProgress();
        }

        protected Void doInBackground(Void... params) {
            // Create the query.
            readRequest = queryFitnessData();

            if (mClient != null) {
                DataReadResult dataReadResult = Fitness.HistoryApi.readData(mClient, readRequest)
                        .await(1, TimeUnit.MINUTES);
                readData(dataReadResult);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Logger.i(TAG, "mTotalVal=" + mTotalVal);
            mChart.setData(mYVals, mTimeClass);
            mChart.animateY();
            TypeDataSet.Period period = mTimeClass.getPeriodType();

            Tr_login login = Define.getInstance().getLoginInfo();
            Long dayDate = 1L;
            if (period == TypeDataSet.Period.PERIOD_DAY) {
                dayDate = 1L;
            } else if (period == TypeDataSet.Period.PERIOD_WEEK) {
                dayDate = 7L;
            } else if (period == TypeDataSet.Period.PERIOD_MONTH) {
                dayDate = (long) mTimeClass.getDayOfMonth();
            }

            Long Goal_Cal = StringUtil.getLong(login.goal_mvm_calory) * dayDate;
            Long Goal_Step = StringUtil.getLong(login.goal_mvm_stepcnt) * dayDate;

            int sex = StringUtil.getIntVal(login.mber_sex);
            float weight = 0.0f;
            DBHelper helper = new DBHelper(mBaseFragment.getContext());
            DBHelperWeight weightDb = helper.getWeightDb();
            DBHelperPPG ppgDB = helper.getPPGDb();

            DBHelperWeight.WeightStaticData bottomData = weightDb.getResultStatic();
            if (!bottomData.getWeight().isEmpty()) {
                weight = StringUtil.getFloatVal(login.mber_bdwgh_app);
            } else {
                weight = StringUtil.getFloatVal(login.mber_bdwgh);
            }
            float height = StringUtil.getFloatVal(login.mber_height);

            mBaseFragment.hideProgress();

            float avgStepDistance = (Float.parseFloat(login.mber_height) - 100f); // 평균보폭
            if (getType() == TYPE_CALORY) {    // 칼로리

                int totalActiveTime = (int) ((avgStepDistance * mTotalStep) * 1.8);
                int maxActiveTime = (int) ((avgStepDistance * mMaxStep) * 1.8);

                // 녹십자 헬스케어의 계산식을 사용한 칼로리계산
                // tvActiveValue.setText(StringUtil.getFormatPrice("" + mBaseFragment.getCalroriTargetCalulator(sex, height, weight, StringUtil.getIntVal("" + mTotalVal))));
                // 구글피트의 칼로리 계산시을 사용한 칼로리계산
                 tvActiveValue.setText("" + mTotalVal);

                tvTargetValue.setText(StringUtil.getFormatPrice("" + Goal_Cal));
                float value = ((float) StringUtil.getFloatVal(tvActiveValue.getText().toString()) / (float) Goal_Cal) * 100;
                if (value == 0.0f) {
                    tvStepBoxValue01.setText("-");
                } else {
                    if (value == 0.0f) {
                        tvStepBoxValue01.setText("-");
                    } else {
                        tvStepBoxValue01.setText(String.format("%.1f", value) + "%");
                    }
                }
                value = (totalActiveTime / 60f) * 0.01f;
                tvStepBoxValue02.setText(String.format("%,d", (int) value) + "분");   // 총활동시간
                tvStepBoxValue03.setText(calcClaorieCompare(mTotalVal.intValue()));

                DBHelperPPG.PPGValue ppgvalue = null;
                if (period == TypeDataSet.Period.PERIOD_DAY) {  // 최근
                    ppgvalue = ppgDB.getResultFastPPG(_startDate, _endDate);
                }else{                                          // 주간, 월간
                    ppgvalue = ppgDB.getResultPPG(_startDate, _endDate);
                }
                if (ppgvalue.hrm.equals("0")){
                    tvStepBoxValue04.setText("-");
                }else{
                    tvStepBoxValue04.setText(StringUtil.getIntVal(ppgvalue.hrm) + "회/분");   // 심박수 (구 최장연속활동시간)
                }

            } else if (getType() == TYPE_STEP) {
                float totalMoveDistance = (avgStepDistance * mTotalVal) * 0.1f;
                Logger.i(TAG, "avgStepDistince=" + avgStepDistance + ", mTotalVal=" + mTotalVal);
                tvActiveValue.setText(StringUtil.getFormatPrice("" + mTotalVal));
                tvTargetValue.setText(StringUtil.getFormatPrice("" + Goal_Step));
                float value = ((float) mTotalVal / (float) Goal_Step) * 100f;
                if (value == 0.0f) {
                    tvStepBoxValue01.setText("-");
                } else {

                    if (value == 0.0f) {
                        tvStepBoxValue01.setText("-");
                    } else {
                        tvStepBoxValue01.setText(String.format("%.1f", value) + "%");
                    }
                }
                float dispTotalDistance = totalMoveDistance / 1000f;
                value = dispTotalDistance * 0.1f;
                tvStepBoxValue02.setText(String.format("%.2f", value) + "km");


//                tmp += 200;
//                dispTotalDistance = dispTotalDistance + (tmp);
//
//                CDialog.showDlg(mBaseFragment.getContext(), "dispTotalDistance:"+dispTotalDistance +", tmp:" +tmp);

                float savedMoney = calcSaveMoney(dispTotalDistance*100);
                tvStepBoxValue03.setText(StringUtil.getFormatPrice("" + (int)savedMoney) + "원");
                float kmcm = StringUtil.getFloatVal(String.format("%.2f", value)) * 100000;
                if(kmcm == 0.0f){
                    tvStepBoxValue04.setText("0cm");
                }else{
                    tvStepBoxValue04.setText(String.format("%.1f", (kmcm / mTotalVal)) + "cm");
                }


            }
            uploadGoogleFitStepData();
            setNextButtonVisible();
        }
    }

    /**
     * 구글 걸음 데이터를 서버에 전송 및 Sqlite에 저장하기
     * 일별 조회 일때만 저장하기
     */
    private void uploadGoogleFitStepData() {
        if (mTimeClass.getPeriodType() == PERIOD_DAY) {
            int hour = 0;
            List<BandModel> dataModelArr = new ArrayList<>();

            for(int key : mGoogleFitStepMap.keySet() ){
                hour = key;
                int step = mGoogleFitStepMap.get(key);
                System.out.println( String.format("key(hour)=%s, value=%s", key, step)+", mDbResultMap.get("+hour+")="+mDbResultMap.get(hour));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mTimeClass.getStartTime());

                calendar.set(Calendar.HOUR, hour);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                BandModel model = new BandModel();
                model.setStep(step);
                model.setRegtype("G");  // 구글 피트니스
                model.setIdx(CDateUtil.getForamtyyMMddHHmmssSS(new Date(calendar.getTimeInMillis())));
                model.setRegDate(CDateUtil.getForamtyyyyMMddHHmmss(new Date(calendar.getTimeInMillis())));

//                Logger.i(TAG, "StepGoogle.regDate=" + model.getRegDate() + ", idx=" + model.getIdx() + ", step=" + model.getStep()
//                        +", mDbResultMap.get("+hour+") ="+mDbResultMap.get(hour) );

                // Sqlite에서 조회 했던 결과가 없으면 서버저장 전문에 사용할 데이터와
                // Sqlite에서에 저장할 데이터를 생성
                if (mDbResultMap.get(hour) == null) {
                    if (isToday() && getNowHour() == hour) {
                        // 현재 시간은 저장하지 않음
                    } else {
                        dataModelArr.add(model);
                    }
                 }
            }

            if (dataModelArr.size() <= 0)
                return;

            DBHelper helper = new DBHelper(mContext);
            DBHelperStep db = helper.getStepDb();
            List<BandModel> newModelArr = db.getResultRegistData(dataModelArr);

            if (newModelArr.size() > 0)
                new DeviceDataUtil().uploadStepData(mBaseFragment, newModelArr);
        }
    }

    /*
        Save Money 계산 함수
     */
    private float calcSaveMoney(float distance) {
        if (distance == 0)
            return 0.0f;

//        distance = distance * 1000;

        final int initDist = 2000;
        final int initFee = 3000;
        final int unitDist = 142;
        final int unitDistFee = 100;

        if (distance <= initDist)
            return initFee;

        distance -= initDist;
        float tmp = (initFee + (distance / unitDist + 1) * unitDistFee);
        return tmp;
    }

    // 활동소모 칼로리비교 가져오기 : 삼계탕 0.0인분
    private String calcClaorieCompare(int calories) {
        if (calories == 0)
            return "-";

        Random random = new Random();
        int r = random.nextInt(mFoodCalories.size());

        FoodCalories target = mFoodCalories.get(r);

        String value = String.format("%.1f", calories / (float) target.calories);

        if (value != null && value.endsWith(".0"))
            value = value.substring(0, value.length() - 2);

        String result = target.name + " " + value + " " + target.unit;
        return result;
    }

    // 활동소모 선언
    private ArrayList<FoodCalories> mFoodCalories;

    private class FoodCalories {
        FoodCalories(String name, String unit, int calories) {
            this.name = name;
            this.unit = unit;
            this.calories = calories;
        }

        String name, unit;
        int calories;
    }

    /**
     * 구글 fit API칼로리, 걸음수 최대 값을 가져온다.
     *
     * @param dataType
     */
    private void getTotalVal(DataType dataType) {
        new GoogleFitInstance(mBaseFragment).totalValDataTask(mClient, dataType, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                Long val = (long) obj;
                tvActiveValue.setText(StringUtil.getFormatPrice("" + val));
            }
        });
    }

    private int mMaxStep = 0;
    private int mTotalStep = 0;

    /**
     * 조회된 구글 피트니스 데이터 데이터 형태로 파싱하기
     * @param dataReadResult
     */
    public void readData(DataReadResult dataReadResult) {
        mArrIdx = 0;
        mTotalVal = 0L;

        mMaxStep = 0;
        mTotalStep = 0;
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "BucketSize=" + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    mTotalVal += dumpDataSet(dataSet);
                }
            }
            TypeDataSet.Period period = mTimeClass.getPeriodType();
            // 주간 조회시 남은 주간 채워주기
            int max = 10;
            if (period == PERIOD_WEEK) {
                max = 7 - (mYVals.size());
            } else if (period == PERIOD_DAY) {
                max = 24 - (mYVals.size());
            } else if (period == PERIOD_MONTH) {
                max = mTimeClass.getDayOfMonth() - (mYVals.size());
            }

            for (int i = 0; i < max; i++) {
                mYVals.add(new BarEntry(mArrIdx++, 0f));
                Log.i(TAG, "PERIOD.size=" + mYVals.size() + ", idx=" + mArrIdx);
            }

        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "DataSetsSize=" + dataReadResult.getBuckets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                mTotalVal += dumpDataSet(dataSet);
            }
        }
    }

    /**
     * 구글 피트니스 데이터 복잡한 데이터 타입에서 필요한 데이터만 뽑아서 차트 및 데이터에 사용할 데이터 추출
     * @param dataSet
     * @return
     */
    private long dumpDataSet(DataSet dataSet) {
        Logger.i(TAG, "====================");

        DateFormat dateFormat = getDateTimeInstance();

        long dumpTotalVal = 0;   // 총 칼로리, 총 걸음수
        mYVals.add(new BarEntry(mArrIdx, 0f));
        Tr_login login = Define.getInstance().getLoginInfo();
        int sex = StringUtil.getIntVal(login.mber_sex);
        float height = StringUtil.getIntVal(login.mber_height);
        float weight = 0.0f;

        DBHelper helper = new DBHelper(mBaseFragment.getContext());
        DBHelperWeight weightDb = helper.getWeightDb();
        DBHelperWeight.WeightStaticData bottomData = weightDb.getResultStatic();
        if (!bottomData.getWeight().isEmpty()) {
            weight = StringUtil.getFloatVal(login.mber_bdwgh_app);
        } else {
            weight = StringUtil.getFloatVal(login.mber_bdwgh);
        }

        for (DataPoint dp : dataSet.getDataPoints()) {
            Logger.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(MILLISECONDS)));
            Logger.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\t Value:" + field.getName() + "=" + dp.getValue(field) + "[" + mArrIdx + "]");
                // sqlite에 저장 여부를 판단할 용도로 사용할 Map
                mGoogleFitStepMap.put(mArrIdx, (int) getFloat(dp.getValue(field).toString()));

                if (getType() == TYPE_CALORY) {

                    int value = 0;
                    float calorie = 0;
                    try {
                        value = dp.getValue(field).asInt(); // 걸음수
                        // 일반적인 계산식 (기존) - 구글피트계산방식.
                        // calorie = getMileToCalorie(value, height, weight);

                        // 녹십자 헬스케어의 계산식을 사용한 칼로리계산
                        calorie = StringUtil.getFloatVal(mBaseFragment.getCalroriTargetCalulator(sex, height, weight, value));

                        mMaxStep = mMaxStep < value ? value : mMaxStep; // 최대 걸음수
                        mTotalStep += value;                       // 걸음수 총 합
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mYVals.set(mArrIdx, new BarEntry(mArrIdx, calorie));
                    Logger.i(TAG, "calorie[" + mArrIdx + "]" + calorie);

                    dumpTotalVal += calorie;
                } else if (getType() == TYPE_STEP) {
                    int steps = 0;
                    try {
                        steps += getFloat(dp.getValue(field).toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mYVals.set(mArrIdx, new BarEntry(mArrIdx, (float) steps));
                    dumpTotalVal += steps;
                }
            }
        }
        Logger.i(TAG, "mDumpValue=" + dumpTotalVal);
        mArrIdx++;

        return dumpTotalVal;
    }

    /**
     * 마일당 칼로리 구하기
     *
     * @param stepCnt
     * @param height
     * @param weight
     * @return
     */
    private float getMileToCalorie(int stepCnt, float height, float weight) {

        float moveDistance = (height - 100) * stepCnt / 100; // (키-100 * 걸음수) / 100
        float mc = (float) (3.7103f + weight + (0.0359f * (weight * 60 * 0.0006213) * 2) * weight);
        float calorie = (float) ((moveDistance * mc) * 0.006213);
        return calorie;
    }

    /**
     * 칼로리, 걸음 여부 판단
     *
     * @return
     */
    public TypeDataSet.Type getType() {
        if (mTypeRg != null) {
            if (mTypeRg.getCheckedRadioButtonId() == R.id.radio_btn_calory) {
                return TYPE_CALORY;
            } else if (mTypeRg.getCheckedRadioButtonId() == R.id.radio_btn_step) {
                return TYPE_STEP;
            }
        }
        return TYPE_CALORY;
    }

    protected void onResume() {
        mBaseFragment.reqPermissions(permissions, new BaseFragment.IPermission() {
            @Override
            public void result(boolean isGranted) {
                if (isGranted) {
                    getData();
                } else {
                    mBaseFragment.hideProgress();
                    mBaseFragment.onBackPressed();
                }
            }
        });
    }

    public void onStop() {
        Logger.i(TAG, "onStop.mClient=" + mClient);
        if (mClient != null) {
            Logger.i(TAG, "onDetach.mClient=" + mClient + ", isConnected()=" + mClient.isConnected());
            mClient.stopAutoManage(mBaseFragment.getActivity());
            mClient.disconnect();
            mClient = null;
        }
    }

    public void onDetach() {
        Logger.i(TAG, "onDetach.mClient=" + mClient);
        if (mClient != null) {
            Logger.i(TAG, "onDetach.mClient=" + mClient + ", isConnected()=" + mClient.isConnected());
            mClient.stopAutoManage(mBaseFragment.getActivity());
            mClient.disconnect();
            mClient = null;
        }
    }

    /**
     * 칼로리,걸음 하단 화면 초기화
     */
    private void setBottomField() {
        if (getType() == TYPE_CALORY) {    // 칼로리
            tvActiveTitle.setText("활동 소모 칼로리");

            tvStepBoxTitle01.setText("목표 달성률");
            tvStepBoxTitle02.setText("총 활동 시간");
            tvStepBoxTitle03.setText("활동 소모 칼로리 비교");

            TypeDataSet.Period period = mTimeClass.getPeriodType();
            if (period == TypeDataSet.Period.PERIOD_DAY) {
                tvStepBoxTitle04.setText("최근 심박수");
            }else if (period == TypeDataSet.Period.PERIOD_WEEK) {
                tvStepBoxTitle04.setText("주간 평균 심박수");
            }else if (period == TypeDataSet.Period.PERIOD_MONTH) {
                tvStepBoxTitle04.setText("월간 평균 심박수");
            }

            tvStepBoxValue01.setText("0%");
            tvActiveValueTag.setText("Kcal");
            tvTargetValueTag.setText("Kcal");

            IvStepBoxImage02.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_st2));
            IvStepBoxImage03.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_st3));
            IvStepBoxImage04.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_st4_1));
            chartunit.setText("kcal");
        } else if (getType() == TYPE_STEP) {
            tvActiveTitle.setText("총 걸음 수");

            tvStepBoxTitle01.setText("목표 달성률");
            tvStepBoxTitle02.setText("총 이동 거리");
            tvStepBoxTitle03.setText("Save Money");
            tvStepBoxTitle04.setText("평균 보폭");
            tvStepBoxValue01.setText("0%");
            tvActiveValueTag.setText("Steps");
            tvTargetValueTag.setText("Steps");

            IvStepBoxImage02.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_st5));
            IvStepBoxImage03.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_st6));
            IvStepBoxImage04.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_st7));
            chartunit.setText("steps");
        }
    }


    /**
     * 구글 API 연결 및 계정 인증 처리
     *
     * @param callback
     */
    public void buildFitnessClientApis(final ApiData.IStep callback) {
        Logger.i(TAG, "buildFitnessClientApis mClient is null now Create");
        if (mClient != null) {
            callback.next(mClient);
            return;
        }

        mClient = new GoogleApiClient.Builder(mBaseFragment.getContext())
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "buildFitnessClientApis Connected!!! " + bundle);
                                new GoogleFitInstance(mBaseFragment).findFitnessDataSources(mClient, new ApiData.IStep() {
                                    @Override
                                    public void next(Object obj) {
                                        callback.next(mClient);
                                    }
                                });
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "buildFitnessClientApis Connection lost.  Cause: Network Lost.");
                                    CDialog.showDlg(mBaseFragment.getContext(), "Google 서비스 연결에 실패 했습니다.(NETWORK_LOST)");

                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "buildFitnessClientApis Connection lost.  Reason: Service Disconnected");
                                    CDialog.showDlg(mBaseFragment.getContext(), "Google 서비스 연결에 실패 했습니다.(Disconnected)");
                                }
                            }
                        })
                .enableAutoManage(mBaseFragment.getActivity(), 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.e(TAG, "buildFitnessClientApis connection failed. Cause: " + result.toString());
                        Log.e(TAG, "buildFitnessClientApis connection failed. getErrorCode: " + result.getErrorCode());
                        Log.e(TAG, "buildFitnessClientApis connection failed. getErrorMessage: " + result.getErrorMessage());
                        Log.e(TAG, "buildFitnessClientApis connection failed. getResolution: " + result.getResolution());
                        Log.e(TAG, "buildFitnessClientApis connection failed. Cause: " + result.isSuccess());

                        if (ConnectionResult.CANCELED == result.getErrorCode()) {
                            CDialog.showDlg(mBaseFragment.getContext(), "계정인증 후 이용 가능합니다.", new CDialog.DismissListener() {
                                @Override
                                public void onDissmiss() {
                                    SharedPref.getInstance().savePreferences(SharedPref.STEP_DATA_SOURCE_TYPE, -1);
                                    onDetach();
                                    mBaseFragment.restartMainActivity();
                                }
                            });
                        } else {
                            CDialog.showDlg(mBaseFragment.getContext(), "Google 서비스 연결에 실패 했습니다.(" + result.toString() + ")");
                        }
                    }
                })
                .build();
    }


    //    public static void printData(DataReadResult dataReadResult) {
//        int totTime = 0;
//        if (dataReadResult.getBuckets().size() > 0) {
//            Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
//            for (Bucket bucket : dataReadResult.getBuckets()) {
//                Log.i(TAG, bucket.getActivity());
//                long activeTime = bucket.getEndTime(TimeUnit.MINUTES) - bucket.getStartTime(TimeUnit.MINUTES);
//                Log.i(TAG, "Activetime " + activeTime);
//            }
//        }
//    }
//    class AsyncTaskTotal extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            getDataVal();
//            return null;
//        }
//    }

//    private void getDataVal() {
//        DataType dataType1 = DataType.TYPE_ACTIVITY_SEGMENT;
//        DataType dataType2 = DataType.AGGREGATE_ACTIVITY_SUMMARY;
//
//        DataReadRequest readRequest = new DataReadRequest.Builder()
//                .aggregate(dataType1, dataType2)
//                .bucketByTime(1, mTimeClass.getTimeUnit())
//                .setTimeRange(mTimeClass.getStartTime(), mTimeClass.getEndTime(), MILLISECONDS)
//                .build();
//
//        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mClient, readRequest)
//                .await(1, TimeUnit.MINUTES);
//
//        if (dataReadResult.getBuckets().size() > 0) {
//            int i = 0;
//            Log.i(TAG, "getDataVal.BucketSize=" + dataReadResult.getBuckets().size());
//            for (Bucket bucket : dataReadResult.getBuckets()) {
//                List<DataSet> dataSets = bucket.getDataSets();
//                for (DataSet dataSet : dataSets) {
//                    for (DataPoint dp : dataSet.getDataPoints()) {
//                        for (Field field : dp.getDataType().getFields()) {
//                            if (field.getName().contains(Field.FIELD_ACTIVITY.getName())) {
//                                Logger.i(TAG, "FIELD_ACTIVITY[" + i + "]=" + dp.getValue(field));
//                            } else if (field.getName().contains(Field.FIELD_DURATION.getName())) {
//                                Logger.i(TAG, "FIELD_DURATION[" + i + "]=" + dp.getValue(field));//StringUtil.getIntVal(dp.getValue(field).toString()) / 1000);
//                            } else if (field.getName().contains(Field.FIELD_NUM_SEGMENTS.getName())) {
//                                Logger.i(TAG, "FIELD_NUM_SEGMENTS[" + i + "]=" + dp.getValue(field));
//                            } else if (field.getName().contains(Field.FIELD_DURATION.getName())) {
//                                Logger.i(TAG, "FIELD_DURATION[" + i + "]=" + dp.getValue(field));
//                            } else if (field.getName().contains(Field.FIELD_NUM_SEGMENTS.getName())) {
//                                Logger.i(TAG, "FIELD_NUM_SEGMENTS[" + i + "]=" + dp.getValue(field));
//                            }
//
//                        }
//                        Logger.i(TAG, "---------------------------------------------------------");
//                        i++;
//                    }
//                }
//            }
//        }
//    }

//    private float dumpDataSet2(DataSet dataSet) {
//        Logger.i(TAG, "====================");
//        DateFormat dateFormat = getDateTimeInstance();
//
//        float value = 0f;
//        float totalValue = 0f;
//        for (DataPoint dp : dataSet.getDataPoints()) {
//            Logger.i(TAG, "Start: " + dateFormat.format(dp.getStartTime(MILLISECONDS)));
//            Logger.i(TAG, "End: " + dateFormat.format(dp.getEndTime(MILLISECONDS)));
//            for (Field field : dp.getDataType().getFields()) {
//                Log.i(TAG, "dumpDataSet2[" + mArrIdx + "]" + field.getName() + "=" + dp.getValue(field));
//                try {
//                    if (getType() == TYPE_CALORY) {
//                        value = StringUtil.getFloat(dp.getValue(field).toString());
//                        if (field.equals(Field.FIELD_DURATION)) {
//                            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes((long) value);
//                            Log.i(TAG, "FIELD_DURATION[" + mArrIdx + "]=" + dp.getValue(field) + ", minutes=" + minutes);
//                            totalValue += minutes;
//                        }
//                    } else if (getType() == TYPE_STEP) {
//                        if (field.equals(Field.FIELD_DISTANCE)) {
//                            totalValue += StringUtil.getFloat(dp.getValue(field).toString());
//                            Log.i(TAG, "FIELD_DISTANCE[" + mArrIdx + "]" + field.getName() + "=" + dp.getValue(field));
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        mArrIdx++;
//        return totalValue;
//    }


//    private void showNotConnectApiDlg() {
//        CDialog.showDlg(mContext, "Google 계정 인증 후 사용 가능합니다.", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mBaseFragment.restartMainActivity();
//            }
//        }, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mBaseFragment.onBackPressed();
//            }
//        });
//    }


//    public class QeuryVerifyDataTask2 extends AsyncTask<Void, Void, DataReadResult> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mBaseFragment.showProgress();
//        }
//
//        protected DataReadResult doInBackground(Void... params) {
//            if (mClient == null)
//                return null;
//
//            long startTime = mTimeClass.getStartTime();
//            long endTime = mTimeClass.getEndTime();
//
//            DateFormat dateFormat = getDateTimeInstance();
//            Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
//            Log.i(TAG, "Range End: " + dateFormat.format(endTime));
//
//            DataType dataType1;
//            DataType dataType2;
//
//            if (getType() == TYPE_CALORY) {
//                dataType1 = DataType.TYPE_ACTIVITY_SEGMENT;
//                dataType2 = DataType.AGGREGATE_ACTIVITY_SUMMARY;
//
//                DataReadRequest readRequest = new DataReadRequest.Builder()
//                        .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
//                        .bucketByTime(1, TimeUnit.DAYS)
//                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
//                        .build();
//
//                DataReadResult dataReadResult = Fitness.HistoryApi.readData(mClient, readRequest)
//                        .await(1, TimeUnit.MINUTES);
//
//                return dataReadResult;
//            } else if (getType() == TYPE_STEP) {
//                dataType1 = DataType.TYPE_DISTANCE_DELTA;
//                dataType2 = DataType.AGGREGATE_DISTANCE_DELTA;
//
//                DataReadRequest readRequest = new DataReadRequest.Builder()
//                        .aggregate(dataType1, dataType2)
//                        .bucketByTime(1, mTimeClass.getTimeUnit())
//                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
//                        .build();
//
//                DataReadResult dataReadResult = Fitness.HistoryApi.readData(mClient, readRequest)
//                        .await(1, TimeUnit.MINUTES);
//
//                return dataReadResult;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(DataReadResult dataReadResult) {
//            super.onPostExecute(dataReadResult);
//            Logger.i(TAG, "mTotalVal=" + mTotalVal);
//
//            mBaseFragment.hideProgress();
//
//            if (dataReadResult != null && dataReadResult.getBuckets().size() > 0) {
//                float totalMoveDistance = 0f;
//                Log.i(TAG, "BucketSize=" + dataReadResult.getBuckets().size());
//                mArrIdx = 0;
//                for (Bucket bucket : dataReadResult.getBuckets()) {
//                    List<DataSet> dataSets = bucket.getDataSets();
//                    for (DataSet dataSet : dataSets) {
//                        totalMoveDistance += dumpDataSet2(dataSet);
//                    }
//                }
//
//                Tr_login login = Define.getInstance().getLoginInfo();
//                TypeDataSet.Period period = mTimeClass.getPeriodType();
//                Long dayDate = 1L;
//                if (period == TypeDataSet.Period.PERIOD_DAY) {
//                    dayDate = 1L;
//                } else if (period == TypeDataSet.Period.PERIOD_WEEK) {
//                    dayDate = 7L;
//                } else if (period == TypeDataSet.Period.PERIOD_MONTH) {
//                    dayDate = (long) mTimeClass.getDayOfMonth();
//                }
//
//                if (getType() == TYPE_CALORY) {
//                    Logger.i(TAG, "totalMinute=" + totalMoveDistance);
//
//                    printData(dataReadResult);
//                } else if (getType() == TYPE_STEP) {
//                    Logger.i(TAG, "totalMoveDistance=" + totalMoveDistance + ", mTotalVal=" + mTotalVal);
//
//                    Long Goal_Step = StringUtil.getLong(login.goal_mvm_stepcnt) * dayDate;
//                    float avgStepDistance = mTotalVal / totalMoveDistance;  // 평균 보폭 계산(총걸음수/이동거리)
//                    if (mTotalVal == 0 || totalMoveDistance == 0) {
//                        avgStepDistance = 0;
//                    }
//
//                    tvActiveValue.setText(StringUtil.getFormatPrice("" + mTotalVal));
//                    tvTargetValue.setText(StringUtil.getFormatPrice("" + Goal_Step));
//                    float value = (((float) mTotalVal / Goal_Step) * 100);
//                    if (value == 0.0f) {
//                        tvStepBoxValue01.setText("0%");
//                    } else {
//                        tvStepBoxValue01.setText(String.format("%.1f", value) + "%");
//                    }
//                    tvStepBoxValue02.setText(String.format("%.2f", totalMoveDistance * 0.001) + "km");
//                    int savedMoney = calcSaveMoney((int) totalMoveDistance);
//                    tvStepBoxValue03.setText(StringUtil.getFormatPrice("" + savedMoney) + "원");
//                    tvStepBoxValue04.setText(String.format("%.1f", avgStepDistance * 10) + "cm");
//                }
//            }
//        }
//    }

//    /**
//     * Sqlite를 조회 하여 데이터가 없으면 구글 피트니스로 조회 한다.
//     */
//    private void getDbData() {
//        mYVals.clear();
//        mGoogleFitStepMap.clear();
//        mDbResultMap.clear();
//
//        DBHelper helper = new DBHelper(mBaseFragment.getContext());
//        DBHelperStep stepDB = helper.getStepDb();
//        String toDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getStartTime());
//        List<BarEntry> yVals1 = stepDB.getResultDay(toDay, mChartType);
//
//        Logger.i(TAG, "yVals1.size()=" + yVals1.size()+", "+ Arrays.toString(yVals1.toArray()));
//        int hour = 0;
//        for (BarEntry entry : yVals1) {
//            // DB에서 조회된 데이터를 Map에 저장해 놓는다.
//            // DB저장시 기존 조회 된 데이터인지 여부를 구분하기 위해
//            int step = (int) entry.getY();
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(mTimeClass.getStartTime());
//            Logger.i(TAG, "mDbResultMap.put[" +calendar.get(Calendar.DATE)+"일:"+ hour + "]step=" + step+", entry="+entry);
//            if (step > 0) {
//                mDbResultMap.put(hour, step);
//            }
//            hour++;
//        }
//
//        new QeuryVerifyDataTask().execute();
//    }

    /**
     * 오늘인지 여부
     *
     * @return
     */
    private boolean isToday() {
        // 오늘일 경우
        return mTimeClass.getCalTime() == 0;
    }

    /**
     * 현재 시간을 구한다
     * @return
     */
    private int getNowHour() {
            Calendar nowCal = Calendar.getInstance();
            nowCal.setTimeInMillis(System.currentTimeMillis());

        return nowCal.get(Calendar.HOUR_OF_DAY);
    }
}
