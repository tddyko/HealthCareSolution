package com.greencross.greencare.food;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.base.value.TypeDataSet;
import com.greencross.greencare.charting.data.BarEntry;
import com.greencross.greencare.charting.data.RadarEntry;
import com.greencross.greencare.chartview.food.FoodBarChartView;
import com.greencross.greencare.chartview.food.RadarChartView;
import com.greencross.greencare.chartview.valueFormat.AxisValueFormatter;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperFoodMain;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.ChartTimeUtil;
import com.greencross.greencare.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 *  히스토리화면
 * Created by mrsohn on 2017. 3. 14..
 */

public class FoodManageHistoryView {

    int recommandCalrori;   //권장칼로리.
    public ChartTimeUtil mTimeClass;

    private FoodBarChartView mChart;
    private RadarChartView mRadarChart;
    private TextView mDateTv;

    private BaseFragment mBaseFragment;
    private View mView;

    private TextView mBottomCalBreakfastTv;
    private TextView mBottomCalLunchTv;
    private TextView mBottomCalDinnerTv;
    private TextView mBottomMinuteBreakfastTv;
    private TextView mBottomMinuteLunchTv;
    private TextView mBottomMinuteDinnerTv;

    private TextView mCalTitleTv;
    private TextView mMinuteTitleTv;
    private TextView mRadarTitleTv;

    private TextView txttakecal;
    private TextView txtrecomcal;

    private ImageButton imgPre_btn;
    private ImageButton imgNext_btn;

    public FoodManageHistoryView(BaseFragment baseFragment, View view) {
        mBaseFragment = baseFragment;
        mView = view;

        mDateTv = (TextView) view.findViewById(R.id.txtCurrDate);

        RadioGroup periodRg = (RadioGroup) view.findViewById(R.id.radiogroup_period_type);
        RadioButton radioBtnMonth = (RadioButton) view.findViewById(R.id.radio_food_type_month);
        RadioButton radioBtnWeek = (RadioButton) view.findViewById(R.id.radio_food_type_week);
        RadioButton radioBtnDay = (RadioButton) view.findViewById(R.id.radio_food_type_day);

        mBottomCalBreakfastTv = (TextView) view.findViewById(R.id.food_cal_breakfast);
        mBottomCalLunchTv = (TextView) view.findViewById(R.id.food_cal_lunch);
        mBottomCalDinnerTv = (TextView) view.findViewById(R.id.food_cal_dinner);
        mBottomMinuteBreakfastTv = (TextView) view.findViewById(R.id.food_minute_blackfast);
        mBottomMinuteLunchTv = (TextView) view.findViewById(R.id.food_minute_lunch);
        mBottomMinuteDinnerTv = (TextView) view.findViewById(R.id.food_minute_dinner);

        mCalTitleTv = (TextView) view.findViewById(R.id.food_cal_title);
        mMinuteTitleTv = (TextView) view.findViewById(R.id.food_minute_title);
        mRadarTitleTv = (TextView) view.findViewById(R.id.food_radar_title);

        periodRg.setOnCheckedChangeListener(mCheckedChangeListener);

        imgPre_btn                  = (ImageButton) view.findViewById(R.id.btn_hiscalLeft);
        imgNext_btn                 = (ImageButton) view.findViewById(R.id.btn_hiscalRight);

        view.findViewById(R.id.btn_hiscalLeft).setOnClickListener(mClickListener);
        view.findViewById(R.id.btn_hiscalRight).setOnClickListener(mClickListener);


        mTimeClass = new ChartTimeUtil(radioBtnDay, radioBtnWeek, radioBtnMonth);
        mRadarChart = new RadarChartView(mBaseFragment.getContext(), view);
        mChart = new FoodBarChartView(mBaseFragment.getContext(), view);

        txttakecal = (TextView) view.findViewById(R.id.txt_takecal);    //  섭취칼로리
        txtrecomcal = (TextView) view.findViewById(R.id.txt_recomcal);  //  권장칼로리

        setRecomandCal();
        setNextButtonVisible();
    }

    private void setRecomandCal(){

        Tr_login login = Define.getInstance().getLoginInfo();                               // 로그인 정보
        String nowYear = CDateUtil.getFormattedString_yyyy(System.currentTimeMillis());     // 현재년도
        String sex = login.mber_sex;                                                        // 성별
        float height = StringUtil.getFloatVal(login.mber_height);                           // 키
        float weight = StringUtil.getFloatVal(login.mber_bdwgh);                            // 몸무게
        float bmi = StringUtil.getFloatVal(login.mber_bmi);                                 // bmi 측정치
        int rAge = Integer.parseInt(login.mber_lifyea.substring(0,4));                      // 회원 생년
        String mber_actqy  = login.mber_actqy;                                              //활동량 1,2,3

        float mWeight;   //표준체중
        float mHeight = height * 0.01f;
        mHeight = StringUtil.getFloatVal(String.format("%.2f", mHeight));

        if (sex.equals("2")){
            //여성
            mWeight = StringUtil.getFloatVal(String.format("%.1f",(mHeight*mHeight) *21));
        }else {
            //남성
            mWeight = StringUtil.getFloatVal(String.format("%.1f",(mHeight*mHeight) *22));
        }

        int bmiStep = 0;
        if(bmi < 18.5){
            bmiStep=1;
        }else if(bmi >= 18.5 && bmi <=22.9){
            bmiStep=2;
        }else { //if(bmi >= 23.0 )
            bmiStep=3;
        }
        float tmpCal = 0.0f;
        if (mber_actqy.equals("1")){    // 가벼운활동
            if (bmiStep==1){
                tmpCal = 35.0f;
            }else if (bmiStep==2){
                tmpCal = 30.0f;
            }else if (bmiStep==3){
                tmpCal = 25.0f;
            }
        }else if (mber_actqy.equals("2")) { // 보통활동
            if (bmiStep==1){
                tmpCal = 40.0f;
            }else if (bmiStep==2){
                tmpCal = 35.0f;
            }else if (bmiStep==3){
                tmpCal = 30.0f;
            }
        }else if (mber_actqy.equals("3")) {     // 힘든활동
            if (bmiStep==1){
                tmpCal = 45.0f;
            }else if (bmiStep==2){
                tmpCal = 40.0f;
            }else if (bmiStep==3){
                tmpCal = 35.0f;
            }
        }

        // 하루 권장섭취량 (표준체중 * tmpCal)
        int recomCal = (int)(mWeight * tmpCal);  // 권장섭취량

        if (mTimeClass.getPeriodType() == TypeDataSet.Period.PERIOD_WEEK) {

            Calendar cal = Calendar.getInstance();
            int nWeek = cal.get(Calendar.DAY_OF_WEEK);
            recomCal = recomCal * nWeek;
        }else if (mTimeClass.getPeriodType() == TypeDataSet.Period.PERIOD_MONTH){

            Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DAY_OF_MONTH);
            recomCal = recomCal * day;
        }
        recommandCalrori = recomCal;
        txtrecomcal.setText(StringUtil.exchangeAmountToStringUnit(""+recomCal)+" kcal");
    }

    public void setVisibility(int visibility) {
        mView.setVisibility(visibility);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.btn_hiscalLeft) {
                mTimeClass.calTime(-1);
                getData();
                setRecomandCal();
            } else if (vId == R.id.btn_hiscalRight) {
                // 초기값 일때 다음날 데이터는 없으므로 리턴
                if (mTimeClass.getCalTime() == 0)
                    return;

                mTimeClass.calTime(1);
                getData();
                setRecomandCal();
            }
            setNextButtonVisible();
        }
    };

    private void setNextButtonVisible(){
        // 초기값 일때 다음날 데이터는 없으므로 리턴
        if (mTimeClass.getCalTime() == 0) {
            imgNext_btn.setVisibility(View.INVISIBLE);
        }else{
            imgNext_btn.setVisibility(View.VISIBLE);
        }
    }
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

            getData();   // 날자 세팅 후 조회
            setRecomandCal();
        }
    };

    /**
     * 날자 계산 후 조회
     */
    public void getData() {
        long startTime = mTimeClass.getStartTime();
        long endTime = mTimeClass.getEndTime();

        String format = "yyyy.MM.dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        String startDate = sdf.format(startTime);
        String endDate = sdf.format(endTime);

        if (mTimeClass.getPeriodType() == TypeDataSet.Period.PERIOD_DAY) {
            mDateTv.setText(startDate);
        } else {
            mDateTv.setText(startDate + " ~ " + endDate);
        }

        format = "yyyy-MM-dd";
        sdf = new SimpleDateFormat(format);
        startDate = sdf.format(startTime);
        endDate = sdf.format(endTime);

        getBottomDataLayout(startDate, endDate);


    }

    /**
     * 하단 데이터 세팅하기
     *
     * @param startDate
     * @param endDate
     */
    private void getBottomDataLayout(String startDate, String endDate) {
        DBHelper helper = new DBHelper(mBaseFragment.getContext());
        DBHelperFoodMain db = helper.getFoodMainDb();
        int[] datas = db.getMealSum(startDate, endDate);
        mBottomCalBreakfastTv.setText(""+datas[0]+" kcal");
        mBottomCalLunchTv.setText(""+datas[1]+" kcal");
        mBottomCalDinnerTv.setText(""+datas[2]+" kcal");
        mBottomMinuteBreakfastTv.setText(""+datas[3]+" 분");
        mBottomMinuteLunchTv.setText(""+datas[4]+" 분");
        mBottomMinuteDinnerTv.setText(""+datas[5]+" 분");

        // 섭취칼로리
        int totTakeCal = datas[0] + datas[1] + datas[2];
        txttakecal.setText(StringUtil.exchangeAmountToStringUnit(""+totTakeCal)+" kcal");

        List<RadarEntry> entries = db.getRadial(startDate, endDate, (float)totTakeCal, (float)recommandCalrori);
        mRadarChart.setData(entries);

        new QeuryVerifyDataTask(db).execute();
    }

    public class QeuryVerifyDataTask extends AsyncTask<Void, Void, Void> {

        private DBHelperFoodMain db;
        public QeuryVerifyDataTask(DBHelperFoodMain db) {
            this.db = db;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBaseFragment.showProgress();
        }

        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mBaseFragment.hideProgress();

            TypeDataSet.Period period = mTimeClass.getPeriodType();
            if (period == TypeDataSet.Period.PERIOD_DAY) {
                String toDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getStartTime());
                List<BarEntry> yVals1 = db.getResultDay(toDay, 24);
                mChart.setData(yVals1);

                mCalTitleTv.setText("일간 영양 섭취 칼로리");
                mMinuteTitleTv.setText("일간 평균 식사 소요시간");
                mRadarTitleTv.setText("일간 영양 섭취 균형도");
            } else if (period == TypeDataSet.Period.PERIOD_WEEK) {
                String startDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getStartTime());
                String endDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getEndTime());
                List<BarEntry> yVals1 = db.getResultWeek(startDay, endDay, 7);
                mChart.setData(yVals1);

                mCalTitleTv.setText("주간 영양 섭취 칼로리");
                mMinuteTitleTv.setText("주간 평균 식사 소요시간");
                mRadarTitleTv.setText("주간 영양 섭취 균형도");
            } else if (period == TypeDataSet.Period.PERIOD_MONTH) {
                String year = CDateUtil.getFormattedString_yyyy(mTimeClass.getStartTime());
                String month = CDateUtil.getFormattedString_MM(mTimeClass.getStartTime());
                List<BarEntry> yVals1 = db.getResultMonth(year, month, mTimeClass.getDayOfMonth());
                mChart.setData(yVals1);

                mCalTitleTv.setText("월간 영양 섭취 칼로리");
                mMinuteTitleTv.setText("월간 평균 식사 소요시간");
                mRadarTitleTv.setText("월간 영양 섭취 균형도");
            }
            mChart.animateY();
            setNextButtonVisible();
        }
    }
}
