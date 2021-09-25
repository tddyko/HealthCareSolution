package com.greencross.greencare.weight;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.base.value.TypeDataSet;
import com.greencross.greencare.charting.data.BarEntry;
import com.greencross.greencare.chartview.valueFormat.AxisValueFormatter2;
import com.greencross.greencare.chartview.weight.FatChartView;
import com.greencross.greencare.chartview.weight.WeightChartView;
import com.greencross.greencare.component.CFontRadioButton;
import com.greencross.greencare.component.CPDialog;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperWeight;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.ChartTimeUtil;
import com.greencross.greencare.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.greencross.greencare.util.CDateUtil.getForamtyyyyMMddHHmm;

/**
 * Created by insystemscompany on 2017. 2. 28..
 */

public class WeightManageFragment extends BaseFragment {
    private final String TAG = WeightManageFragment.class.getSimpleName();

    public ChartTimeUtil mTimeClass;
    private TextView mDateTv;
    private TextView mWeightTargetTv;
    private TextView mWeightTv;
    private TextView mWeightTargetWaitTv;
    private TextView mWeightDayTv;
    private TextView chartRule;

    protected WeightChartView mWeightChart;
    protected FatChartView mFatChart;

    private WeightSwipeListView mSwipeListView;

    private ConstraintLayout layout_weight_history;
    private ConstraintLayout layout_weight_graph;
    private CFontRadioButton btn_weight_graph;
    private CFontRadioButton btn_weight_history;

    private ImageButton imgPre_btn;
    private ImageButton imgNext_btn;

    private DBHelperWeight.WeightStaticData mWeightStaticData;
    private AxisValueFormatter2 xFormatter;


    public static Fragment newInstance() {
        WeightManageFragment fragment = new WeightManageFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weight_manage, container, false);

        return view;
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.text_weight_manage));       // 액션바 타이틀
        actionBar.setActionBarWriteBtn(WeightInputFragment.class, new Bundle());// 액션바 입력 버튼
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDateTv = (TextView) view.findViewById(R.id.period_date_textview);
        mWeightTargetTv = (TextView)view.findViewById(R.id.textView54);
        mWeightTv = (TextView)view.findViewById(R.id.textView52);
        mWeightTargetWaitTv = (TextView)view.findViewById(R.id.textView57);
        mWeightDayTv = (TextView)view.findViewById(R.id.textView18);
        chartRule  = (TextView)view.findViewById(R.id.chart_rule);
        layout_weight_graph  = (ConstraintLayout) view.findViewById(R.id.layout_weight_graph);
        layout_weight_history  = (ConstraintLayout) view.findViewById(R.id.layout_weight_history);
        btn_weight_graph  = (CFontRadioButton) view.findViewById(R.id.btn_weight_graph);
        btn_weight_history  = (CFontRadioButton) view.findViewById(R.id.btn_weight_history);

        imgPre_btn                  = (ImageButton) view.findViewById(R.id.pre_btn);
        imgNext_btn                 = (ImageButton) view.findViewById(R.id.next_btn);
        RadioGroup periodRg = (RadioGroup) view.findViewById(R.id.period_radio_group);
        RadioButton radioBtnDay = (RadioButton) view.findViewById(R.id.period_radio_btn_day);
        RadioButton radioBtnWeek = (RadioButton) view.findViewById(R.id.period_radio_btn_week);
        RadioButton radioBtnMonth = (RadioButton) view.findViewById(R.id.period_radio_btn_month);
        RadioButton radioBtnYear = (RadioButton) view.findViewById(R.id.period_radio_btn_year);

        view.findViewById(R.id.pre_btn).setOnClickListener(mClickListener);
        view.findViewById(R.id.next_btn).setOnClickListener(mClickListener);
        view.findViewById(R.id.btn_weight_graph).setOnClickListener(mClickListener);
        view.findViewById(R.id.btn_weight_history).setOnClickListener(mClickListener);
        view.findViewById(R.id.weight_modal_btn).setOnClickListener(mClickListener);
        periodRg.setOnCheckedChangeListener(mCheckedChangeListener);

        getBottomDataLayout(); // 하단 데이터 셋팅

        mTimeClass = new ChartTimeUtil(radioBtnDay, radioBtnWeek, radioBtnMonth, radioBtnYear);
        mWeightChart = new WeightChartView(getContext(), view);
        mFatChart = new FatChartView(getContext(), view);

        TypeDataSet.Period periodType = mTimeClass.getPeriodType();
        mTimeClass.clearTime();         // 날자 초기화

        xFormatter = new AxisValueFormatter2(periodType);
        mWeightChart.setXValueFormat(xFormatter);
        mFatChart.setXValueFormat(xFormatter);

        mSwipeListView = new WeightSwipeListView(view, WeightManageFragment.this);
        chartRule.setText("일간 : 시간별 최종데이터");
        getData();
        setNextButtonVisible();
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();

            if (vId == R.id.pre_btn) {
                mTimeClass.calTime(-1);
                getData();
            } else if (vId == R.id.next_btn) {
                // 초기값 일때 다음날 데이터는 없으므로 리턴
                if (mTimeClass.getCalTime() == 0)
                    return;

                mTimeClass.calTime(1);
                getData();
            }else if (vId == R.id.btn_weight_graph) {
                layout_weight_history.setVisibility(View.GONE);
                layout_weight_graph.setVisibility(View.VISIBLE);

                getData();
                getBottomDataLayout();
            }else if (vId == R.id.btn_weight_history) {
                layout_weight_graph.setVisibility(View.GONE);
                layout_weight_history.setVisibility(View.VISIBLE);

                mSwipeListView.getHistoryData();
            }else if (vId == R.id.weight_modal_btn){
                new showModifiDlg();
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
            TypeDataSet.Period periodType = mTimeClass.getPeriodType();
            mTimeClass.clearTime();         // 날자 초기화

            xFormatter = new AxisValueFormatter2(periodType);
            mWeightChart.setXValueFormat(xFormatter);
            mFatChart.setXValueFormat(xFormatter);

            getData();   // 날자 세팅 후 조회
        }
    };

    /**
     * 날자 계산 후 조회
     */
    private void getData() {

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

        new QeuryVerifyDataTask().execute();
    }

    public class QeuryVerifyDataTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgress();

            DBHelper helper = new DBHelper(getContext());
            DBHelperWeight weightDb = helper.getWeightDb();


            TypeDataSet.Period period = mTimeClass.getPeriodType();
            if (period == TypeDataSet.Period.PERIOD_DAY) {
                mWeightChart.setXvalMinMax(-1, 24, 24);
                mFatChart.setXvalMinMax(-1, 24, 24);

                String toDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getStartTime());
                List<BarEntry> weightYVals = weightDb.getResultDay(toDay, true);
                mWeightChart.setData(weightYVals);
                List<BarEntry> fatYVals = weightDb.getResultDay(toDay, false);
                mFatChart.setData(fatYVals);

                Toast.makeText(getContext(), "일간 : 시간별 최종데이터", Toast.LENGTH_SHORT).show();
//                chartRule.setText("일간 : 시간 별 최종 데이터");
            } else  if (period == TypeDataSet.Period.PERIOD_WEEK) {
                mWeightChart.setXvalMinMax(0, 8, 9);
                mFatChart.setXvalMinMax(0, 8, 9);

                String startDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getStartTime());
                String endDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getEndTime());
                List<BarEntry> weightYVals = weightDb.getResultWeek(startDay, endDay, true);
                mWeightChart.setData(weightYVals);
                List<BarEntry> fatYVals = weightDb.getResultWeek(startDay, endDay, false);
                mFatChart.setData(fatYVals);

                Toast.makeText(getContext(), "주간 : 요일별 평균데이터", Toast.LENGTH_SHORT).show();
//                chartRule.setText("주간 : 요일 별 평균 데이터");
            } else  if (period == TypeDataSet.Period.PERIOD_MONTH) {
                int maxX = mTimeClass.getStartTimeCal().getActualMaximum(Calendar.DAY_OF_MONTH)+1;
                xFormatter.setMonthMax(maxX);
                mWeightChart.setXvalMinMax(0, maxX, maxX);
                mFatChart.setXvalMinMax(0, maxX, maxX);

                String startDay = CDateUtil.getFormattedString_yyyy(mTimeClass.getStartTime());
                String endDay = CDateUtil.getFormattedString_MM(mTimeClass.getStartTime());
                List<BarEntry> weightYVals = weightDb.getResultMonth(startDay, endDay, true);
                mWeightChart.setData(weightYVals);
                List<BarEntry> fatYVals = weightDb.getResultMonth(startDay, endDay, false);
                mFatChart.setData(fatYVals);

                Toast.makeText(getContext(), "월간 : 일별 평균데이터", Toast.LENGTH_SHORT).show();
//                chartRule.setText("월간 : 일 별 평균 데이터");
            } else  if (period == TypeDataSet.Period.PERIOD_YEAR) {
                mWeightChart.setXvalMinMax(0, 13, 15);
                mFatChart.setXvalMinMax(0, 13, 15);

                String startDay = CDateUtil.getFormattedString_yyyy(mTimeClass.getStartTime());
                List<BarEntry> weightYVals = weightDb.getResultYear(startDay, true);
                mWeightChart.setData(weightYVals);
                List<BarEntry> fatYVals = weightDb.getResultYear(startDay, false);
                mFatChart.setData(fatYVals);

                Toast.makeText(getContext(), "년간 : 월별 평균데이터", Toast.LENGTH_SHORT).show();
//                chartRule.setText("년간 : 월 별 평균 데이터");
            }

            mWeightChart.animateY();
            mFatChart.animateY();
            setNextButtonVisible();
        }
    }

    /**
     * 하단 데이터 세팅하기
     */
    private void getBottomDataLayout() {
        Tr_login login = Define.getInstance().getLoginInfo();
        mWeightTargetTv.setText(login.mber_bdwgh_goal);

        DBHelper helper = new DBHelper(getContext());
        DBHelperWeight WeightDb = helper.getWeightDb();
        DBHelperWeight.WeightStaticData bottomData = WeightDb.getResultStatic();
        mWeightStaticData = bottomData;

        String dataWeight = "";
        if(bottomData.getWeight().isEmpty()){
            dataWeight = "0";
        }else{
            dataWeight = bottomData.getWeight();
        }

        mWeightTv.setText(StringUtil.getNoneZeroString(StringUtil.getFloatVal(dataWeight)));

        float temp = Float.parseFloat(dataWeight) - Float.parseFloat(login.mber_bdwgh_goal);
        if(bottomData.getWeight().isEmpty()) {
            mWeightTargetWaitTv.setText("--");
        }else if(temp > 0){
            mWeightTargetWaitTv.setText("+" + String.format("%.1f",temp));
        }else{
            mWeightTargetWaitTv.setText(String.format("%.1f",temp));
        }

        if(bottomData.getWeight().isEmpty()){
            mWeightDayTv.setText(getString(R.string.recent_measurements));
        }else {
            String time = getForamtyyyyMMddHHmm(new Date(System.currentTimeMillis()));
            String timeStr = bottomData.getRegdate().substring(0, 4) + bottomData.getRegdate().substring(5, 7) + bottomData.getRegdate().substring(8, 10) + bottomData.getRegdate().substring(11, 13) + bottomData.getRegdate().substring(14, 16);
            int dayTime = Integer.parseInt(time.substring(0,8));
            int dayTimeStr = Integer.parseInt(timeStr.substring(0,8));

            if(dayTime == dayTimeStr){
                mWeightDayTv.setText(getString(R.string.today_measurements));
            }else {
                if (dayTime > dayTimeStr) {

                    mWeightDayTv.setText("최근 측정("+getDateDiff(""+dayTimeStr) + "일전)");
                }else{
                    mWeightDayTv.setText("");
                }
            }
        }
    }

    private long getDateDiff(String bDate){

        int year = StringUtil.getIntVal(bDate.substring(0,4));
        int month = StringUtil.getIntVal(bDate.substring(4,6));
        int day = StringUtil.getIntVal(bDate.substring(6,8));
        Calendar thatDay = Calendar.getInstance();
        thatDay.set(Calendar.DAY_OF_MONTH,day);
        thatDay.set(Calendar.MONTH,month-1);
        thatDay.set(Calendar.YEAR, year);

        Calendar today = Calendar.getInstance();
        long diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); //result in millis
        long days = diff / (24 * 60 * 60 * 1000);
        return days;
    }

    class showModifiDlg {
        private TextView mBmrTv;
        private TextView mBmiTv;
        private TextView mObesityTv;
        private TextView mFatTv;
        private TextView mBodyWaterTv;
        private TextView mMuscleTv;
        private Button mConfirmBtn;
        /**
         * 상세정보 Dialog 띄우기
         **/
        private showModifiDlg() {
            View modifyView = LayoutInflater.from(getContext()).inflate(R.layout.activity_weight_modal, null);

            mBmrTv = (TextView) modifyView.findViewById(R.id.weight_bmr_textxview);
            mBmiTv = (TextView) modifyView.findViewById(R.id.weight_bmi_textxview);
            mObesityTv = (TextView) modifyView.findViewById(R.id.weight_obesity_textxview);
            mFatTv = (TextView) modifyView.findViewById(R.id.weight_fat_textxview);
            mBodyWaterTv = (TextView) modifyView.findViewById(R.id.weight_bodywater_textxview);
            mMuscleTv = (TextView) modifyView.findViewById(R.id.weight_muscle_textxview);

            if(mWeightStaticData.getWeight().isEmpty()){
                mBmrTv.setText("0"+" kcal");
                mBmiTv.setText("0"+" kg/m2");
                mObesityTv.setText("0"+" %");
                mFatTv.setText("0"+" %");
                mBodyWaterTv.setText("0"+" %");
                mMuscleTv.setText("0"+" %");
            } else {
                Tr_login login = Define.getInstance().getLoginInfo();
                Float rHeight = Float.parseFloat(login.mber_height);
                float rWeight = Float.parseFloat(mWeightStaticData.getWeight());
                int rSex = Integer.parseInt(login.mber_sex);
                int rAge = Integer.parseInt(login.mber_lifyea.substring(0,4));
                String nowYear = CDateUtil.getFormattedString_yyyy(System.currentTimeMillis());

                float Float_result = 0.0f;
                if(rSex == 1){
                    Float_result = (float) (66.47f + (13.75f * rWeight) + (5.0f * rHeight) - (6.76f * ((Float.parseFloat(nowYear) - rAge)+1)));
                }else{
                    Float_result = (float) (655.1f + (9.56f * rWeight) + (1.85f * rHeight) - (4.68f * ((Float.parseFloat(nowYear) - rAge)+1)));
                }

                mBmrTv.setText(Integer.toString((int) Float_result) + " kcal");
                mBmiTv.setText(String.format("%.1f", StringUtil.getFloatVal(mWeightStaticData.getWeight()) / ((Float.parseFloat(login.mber_height) / 100) * (Float.parseFloat(login.mber_height) / 100)) )+" kg/m2");
                mFatTv.setText(mWeightStaticData.getObesity()+" %");
                mObesityTv.setText(mWeightStaticData.getFat()+" %");
                mBodyWaterTv.setText(mWeightStaticData.getBodyWater()+" %");
                mMuscleTv.setText(mWeightStaticData.getMuscle()+" %");
            }

            final CPDialog dlg = CPDialog.showDlg(getContext(), modifyView);
            dlg.setTitle(getString(R.string.text_more_info));
            mConfirmBtn = (Button)dlg.findViewById(R.id.right_confirm_btn_close);
            mConfirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.dismiss();
                }
            });
        }

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();  // 차트 데이터 Refresh
        getBottomDataLayout(); // 하단 데이터 Refresh
        mSwipeListView.getHistoryData();    // 히스토리 Refresh

    }
}