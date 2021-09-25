package com.greencross.greencare.step;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.base.value.TypeDataSet;
import com.greencross.greencare.charting.data.BarEntry;
import com.greencross.greencare.chartview.valueFormat.AxisValueFormatter;
import com.greencross.greencare.chartview.walk.BarChartView;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperPPG;
import com.greencross.greencare.database.DBHelperStep;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.ChartTimeUtil;
import com.greencross.greencare.util.StringUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.greencross.greencare.R.id.chart_unit;
import static com.greencross.greencare.base.value.TypeDataSet.Type.TYPE_CALORY;
import static com.greencross.greencare.base.value.TypeDataSet.Type.TYPE_STEP;

/**
 * Created by mrsohn on 2017. 3. 8..
 */

public class BandChartView {
    private final String TAG = BandChartView.class.getSimpleName();

    private Long Goal_Step;
    private Long Goal_Cal;

    private BaseFragment mBaseFragment;
    private Context mContext;
    private boolean mChartType;

    public RadioGroup mTypeRg;

    private int mArrIdx = 0;
    public ChartTimeUtil mTimeClass;

    protected BarChartView mChart;

    protected ImageButton mNextbtn;
    protected ImageButton mPrebtn;
    private TextView mDateTv;
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

    protected List<BarEntry> mYVals = new ArrayList<>();

    public BandChartView(BaseFragment baseFragment, View view) {
        mContext = baseFragment.getContext();
        mBaseFragment= baseFragment;
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
        tvStepBoxTitle01 = (TextView) view.findViewById(R.id.tvStepBoxTitle01);   //목표 달성율
        tvStepBoxTitle02 = (TextView) view.findViewById(R.id.tvStepBoxTitle02);   //총이동거리
        tvStepBoxTitle03 = (TextView) view.findViewById(R.id.tvStepBoxTitle03);   //칼로리비교
        tvStepBoxTitle04  = (TextView) view.findViewById(R.id.tvStepBoxTitle04);   //심박수
        tvStepBoxValue01  = (TextView) view.findViewById(R.id.tvStepBoxValue01);   // 타이틀
        tvStepBoxValue02  = (TextView) view.findViewById(R.id.tvStepBoxValue02);
        tvStepBoxValue03  = (TextView) view.findViewById(R.id.tvStepBoxValue03);
        tvStepBoxValue04  = (TextView) view.findViewById(R.id.tvStepBoxValue04);

        IvStepBoxImage02 = (ImageView) view.findViewById(R.id.IvStepBoxImage02);
        IvStepBoxImage03 = (ImageView) view.findViewById(R.id.IvStepBoxImage03);
        IvStepBoxImage04 = (ImageView) view.findViewById(R.id.IvStepBoxImage04);

        tvActiveTitle  = (TextView) view.findViewById(R.id.tvActiveTitle);
        tvActiveValue  = (TextView) view.findViewById(R.id.tvActiveValue);
        tvActiveValueTag = (TextView) view.findViewById(R.id.tvActiveValueaTag);
        tvTargetValue  = (TextView) view.findViewById(R.id.tvTargetValue);
        tvTargetValueTag  = (TextView) view.findViewById(R.id.tvTargetValueTag);
        chartunit = (TextView) view.findViewById(chart_unit);


        mChart = new BarChartView(mContext, view);

        mPrebtn.setOnClickListener(mClickListener);
        mNextbtn.setOnClickListener(mClickListener);

        mTypeRg.setOnCheckedChangeListener(mTypeCheckedChangeListener);

        RadioGroup periodRg = (RadioGroup) view.findViewById(R.id.period_radio_group);
        RadioButton radioBtnDay = (RadioButton) view.findViewById(R.id.period_radio_btn_day);
        RadioButton radioBtnWeek = (RadioButton) view.findViewById(R.id.period_radio_btn_week);
        RadioButton radioBtnMonth = (RadioButton) view.findViewById(R.id.period_radio_btn_month);

        view.findViewById(R.id.radio_btn_calory).setOnClickListener(mClickListener);
        view.findViewById(R.id.radio_btn_step).setOnClickListener(mClickListener);

        periodRg.setOnCheckedChangeListener(mCheckedChangeListener);

        mTimeClass = new ChartTimeUtil(radioBtnDay, radioBtnWeek, radioBtnMonth);

        getData();
        setNextButtonVisible();
    }

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
            } else if(vId == R.id.radio_btn_calory) {
                mChartType = false;
            }  else if(vId == R.id.radio_btn_step) {
                mChartType = true;
            }

            getData();
            setNextButtonVisible();

            if (mYVals != null)
                mYVals.clear();

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
    /**
     * 칼로리, 걸음수
     */
    public RadioGroup.OnCheckedChangeListener mTypeCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            mTimeClass.getTime();

            if (mYVals != null)
                mYVals.clear();

            new QeuryVerifyDataTask().execute();
        }
    };

    /**
     * 일간,주간,월간
     */
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

            getData();   // 날자 세팅 후 조회
        }
    };

    /**
     * 날자 계산 후 조회
     */
    private void getData() {

        // 활동소모 초기화
        mFoodCalories = new ArrayList<FoodCalories>();
        String[] name =  mContext.getResources().getStringArray(R.array.life_food);
        String[] unit = mContext.getResources().getStringArray(R.array.life_food_unit);
        String[] value = mContext.getResources().getStringArray(R.array.life_food_calorie);

        for(int i = 0; i < name.length; i++)
            mFoodCalories.add(new FoodCalories(name[i], unit[i], Integer.valueOf(value[i])));

        long startTime = mTimeClass.getStartTime();
        long endTime = mTimeClass.getEndTime();

        String format = "yyyy.MM.dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        String startDate = sdf.format(startTime);
        String endDate = sdf.format(endTime);

        if (mTimeClass.getPeriodType() == TypeDataSet.Period.PERIOD_DAY) {
            mDateTv.setText(startDate);
        } else {
            mDateTv.setText(startDate +" ~ "+endDate);
        }

        format = "yyyy-MM-dd";
        sdf = new SimpleDateFormat(format);
        startDate = sdf.format(startTime);
        endDate = sdf.format(endTime);
        getBottomDataLayout(startDate, endDate);


    }

    public class QeuryVerifyDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setBottomField();
            mBaseFragment.showProgress();
        }

        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mBaseFragment.hideProgress();

            DBHelper helper = new DBHelper(mBaseFragment.getContext());
            DBHelperStep stepDB = helper.getStepDb();
            DBHelperPPG ppgDB = helper.getPPGDb();

            Long dayDate = 1L;

            TypeDataSet.Period period = mTimeClass.getPeriodType();
            if (period == TypeDataSet.Period.PERIOD_DAY) {
                String toDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getStartTime());
                List<BarEntry> yVals1 = stepDB.getResultDay(toDay, mChartType);
                mChart.setData(yVals1, mTimeClass);
            } else  if (period == TypeDataSet.Period.PERIOD_WEEK) {
                String startDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getStartTime());
                String endDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getEndTime());
                List<BarEntry> yVals1 = stepDB.getResultWeek(startDay, endDay, mChartType);
                mChart.setData(yVals1, mTimeClass);
                dayDate = 7L;
            } else  if (period == TypeDataSet.Period.PERIOD_MONTH) {
                String year = CDateUtil.getFormattedString_yyyy(mTimeClass.getStartTime());
                String month = CDateUtil.getFormattedString_MM(mTimeClass.getStartTime());
                List<BarEntry> yVals1 = stepDB.getResultMonth(year, month, mChartType);
                mChart.setData(yVals1, mTimeClass);
                dayDate = (long)mTimeClass.getDayOfMonth();
            }

            mChart.animateY();

            long startTime = mTimeClass.getStartTime();
            long endTime = mTimeClass.getEndTime();

            String format = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(format);

            String startDate = sdf.format(startTime);
            String endDate = sdf.format(endTime);

            DBHelperStep.StepValue stepvalue = stepDB.getResultStatic(startDate, endDate, mChartType);

            Log.d(TAG, "totcalorie:"+stepvalue.totcalorie);
            Log.d(TAG, "totstep:"+stepvalue.totstep);
            Log.d(TAG, "distance:"+stepvalue.distance);
            Log.d(TAG, "stride:"+stepvalue.stride);

            Tr_login login = Define.getInstance().getLoginInfo();

            Goal_Cal = StringUtil.getLong(login.goal_mvm_calory) * dayDate;
            Goal_Step = StringUtil.getLong(login.goal_mvm_stepcnt) * dayDate;
            float avgStepDistance = (Float.parseFloat(login.mber_height) - 100f); // 평균보폭

            if (getType() == TYPE_CALORY) {    // 칼로리
                tvActiveValue.setText(StringUtil.getFormatPrice(stepvalue.totcalorie));
                tvTargetValue.setText(StringUtil.getFormatPrice(""+Goal_Cal));
                if(Goal_Cal == 0){
                    tvStepBoxValue01.setText("-");
                }else{
                    tvStepBoxValue01.setText( String.format("%.1f", (Float.parseFloat(stepvalue.totcalorie)  / (float)Goal_Cal) * 100) +"%");
                }
                float value = ((StringUtil.getFloatVal(stepvalue.totstep))/60f);
                tvStepBoxValue02.setText(String.format("%,d", (int)value)+"분");
                tvStepBoxValue03.setText(calcClaorieCompare(Integer.parseInt(stepvalue.totcalorie)));

                DBHelperPPG.PPGValue ppgvalue = null;
                if (period == TypeDataSet.Period.PERIOD_DAY) {  // 최근
                    ppgvalue = ppgDB.getResultFastPPG(startDate, endDate);
                }else{                                          // 주간, 월간
                    ppgvalue = ppgDB.getResultPPG(startDate, endDate);
                }

                if (ppgvalue.hrm.equals("0")){
                    tvStepBoxValue04.setText("-");
                }else{
                    tvStepBoxValue04.setText(StringUtil.getIntVal(ppgvalue.hrm) + "회/분");   // 심박수 (구 최장연속활동시간)
                }

            } else if (getType() == TYPE_STEP) {
                float totalMoveDistance = (avgStepDistance * StringUtil.getFloatVal(stepvalue.totstep))*0.1f;
                tvActiveValue.setText(StringUtil.getFormatPrice(stepvalue.totstep));
                tvTargetValue.setText(StringUtil.getFormatPrice(""+Goal_Step));
                if(Goal_Step == 0){
                    tvStepBoxValue01.setText("-");
                }else {
                    tvStepBoxValue01.setText(String.format("%.1f", (Float.parseFloat(stepvalue.totstep) / (float) Goal_Step) * 100) + "%");
                }
                float dispTotalDistance = totalMoveDistance/1000f;
                float value = dispTotalDistance * 0.1f;

                tvStepBoxValue02.setText(String.format("%.2f", value)+"km");
                float kmcm = StringUtil.getFloatVal(String.format("%.2f", value)) * 100000;
//                float savedMoney = calcSaveMoney(Float.parseFloat(stepvalue.distance));
                float savedMoney = calcSaveMoney(totalMoveDistance/10);
                tvStepBoxValue03.setText(StringUtil.getFormatPrice(""+(int)savedMoney)+"원");
                if(kmcm == 0.0f){
                    tvStepBoxValue04.setText("0cm");
                }else{
                    tvStepBoxValue04.setText( String.format("%.1f", (kmcm   / Float.parseFloat(stepvalue.totstep)))+"cm");
                }

            }
            setNextButtonVisible();
        }
    }

    /**
     * 마일당 칼로리 구하기
     * @param stepCnt
     * @param height
     * @param weight
     * @return
     */
    private float getMileToCalorie(int stepCnt, float height, float weight) {
        float moveDistance = (height -100) * stepCnt / 100; // (키-100 * 걸음수) / 100
        float mc = (float) (3.7103f + weight + (0.0359f * (weight * 60 * 0.0006213) * 2) * weight);
        float calorie = (float) ((moveDistance * mc) * 0.006213);
        return calorie;
    }

    /*
     **문자열 포맷
     */
    public String makeStringComma(String str) {
        if (str.length() == 0)
            return "";
        long value = Long.parseLong(str);
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(value);
    }

    /**
     * 하단 데이터 세팅하기
     * @param startDate
     * @param endDate
     */
    private void getBottomDataLayout(String startDate, String endDate) {
        DBHelper helper = new DBHelper(mBaseFragment.getContext());
        DBHelperStep db = helper.getStepDb();

        new QeuryVerifyDataTask().execute();
    }

    /*
        Save Money 계산 함수
     */
    private float calcSaveMoney(float distance) {
        if(distance == 0)
            return 0.0f;

//        distance = distance * 1000;

        final int initDist = 2000;
        final int initFee = 3000;
        final int unitDist = 142;
        final int unitDistFee = 100;

        if(distance <= initDist)
            return initFee;

        distance -= initDist;
        float tmp=(initFee + (distance / unitDist + 1) * unitDistFee);
        return tmp;
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
     * 칼로리, 걸음 여부 판단
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
            tvActiveValueTag.setText("Steps");
            tvTargetValueTag.setText("Steps");


            IvStepBoxImage02.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_st5));
            IvStepBoxImage03.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_st6));
            IvStepBoxImage04.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_st7));
            chartunit.setText("steps");
        }
    }

    // 활동소모 칼로리비교 가져오기 : 삼계탕 0.0인분
    private String calcClaorieCompare(int calories) {
        if(calories == 0)
            return "-";

        Random random = new Random();
        int r = random.nextInt(mFoodCalories.size());

        FoodCalories target = mFoodCalories.get(r);

        String value = String.format("%.1f", calories / (float)target.calories);

        if(value.endsWith(".0"))
            value = value.substring(0, value.length() - 2);

        String result = target.name + " " + value + " " + target.unit;
        return result;
    }
}
