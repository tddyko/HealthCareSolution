package com.greencross.greencare.water;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.base.value.TypeDataSet;
import com.greencross.greencare.charting.data.BarEntry;
import com.greencross.greencare.chartview.valueFormat.AxisValueFormatter;
import com.greencross.greencare.chartview.water.WaterChartView;
import com.greencross.greencare.component.CFontRadioButton;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperWater;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.ChartTimeUtil;
import com.greencross.greencare.util.StringUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by insystemscompany on 2017. 2. 28..
 */

public class WaterManageFragment extends BaseFragment {

    private Long Goal_Water;

    public ChartTimeUtil mTimeClass;
    private WaterChartView mChart;
    private TextView mDateTv;
    private TextView mWaterToDayTv;
    private TextView mWaterTargetTv;
    private TextView mWaterAttTv;

    private ImageButton imgPre_btn;
    private ImageButton imgNext_btn;

    private ConstraintLayout layout_water_history;
    private ConstraintLayout layout_water_graph;
    private ConstraintLayout layout_water_info;
    private CFontRadioButton btn_water_history;
    private CFontRadioButton btn_water_graph;

    private WaterSwipeListView mSwipeListView;


    public static Fragment newInstance() {
        WaterManageFragment fragment = new WaterManageFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_water_manage, container, false);
        return view;
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.text_water));       // 액션바 타이틀
        actionBar.setActionBarWriteBtn(WaterInputFragment.class, new Bundle());// 액션바 입력 버튼
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDateTv         = (TextView) view.findViewById(R.id.period_date_textview);
        mWaterToDayTv   = (TextView) view.findViewById(R.id.water_manage_today_textview);
        mWaterTargetTv  = (TextView) view.findViewById(R.id.textView35);
        mWaterAttTv     = (TextView) view.findViewById(R.id.textView36);

        RadioGroup periodRg         = (RadioGroup) view.findViewById(R.id.period_radio_group);
        RadioButton radioBtnMonth   = (RadioButton) view.findViewById(R.id.period_radio_btn_month);
        RadioButton radioBtnWeek    = (RadioButton) view.findViewById(R.id.period_radio_btn_week);
        RadioButton radioBtnDay     = (RadioButton) view.findViewById(R.id.period_radio_btn_day);

        layout_water_graph      = (ConstraintLayout) view.findViewById(R.id.layout_water_graph);
        layout_water_history    = (ConstraintLayout) view.findViewById(R.id.layout_water_history);
        layout_water_info       = (ConstraintLayout) view.findViewById(R.id.layout_water_info);
        btn_water_graph         = (CFontRadioButton) view.findViewById(R.id.btn_water_graph);
        btn_water_history       = (CFontRadioButton) view.findViewById(R.id.btn_water_history);

        imgPre_btn                  = (ImageButton) view.findViewById(R.id.pre_btn);
        imgNext_btn                 = (ImageButton) view.findViewById(R.id.next_btn);

        view.findViewById(R.id.btn_water_graph).setOnClickListener(mClickListener);
        view.findViewById(R.id.btn_water_history).setOnClickListener(mClickListener);
        view.findViewById(R.id.pre_btn).setOnClickListener(mClickListener);
        view.findViewById(R.id.next_btn).setOnClickListener(mClickListener);
        periodRg.setOnCheckedChangeListener(mCheckedChangeListener);

        mTimeClass      = new ChartTimeUtil(radioBtnDay, radioBtnWeek, radioBtnMonth);

        mChart          = new WaterChartView(getContext(), view);

        mSwipeListView  = new WaterSwipeListView(view, WaterManageFragment.this);

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
            } else if (vId == R.id.btn_water_graph) {
                layout_water_history.setVisibility(View.GONE);
                layout_water_graph.setVisibility(View.VISIBLE);
                layout_water_info.setVisibility(View.VISIBLE);

                getData();
            } else if (vId == R.id.btn_water_history) {
                layout_water_graph.setVisibility(View.GONE);
                layout_water_info.setVisibility(View.GONE);
                layout_water_history.setVisibility(View.VISIBLE);

                mSwipeListView.getHistoryData();
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
            TypeDataSet.Period periodType   = mTimeClass.getPeriodType();
            mTimeClass.clearTime();         // 날자 초기화

            AxisValueFormatter formatter    = new AxisValueFormatter(periodType);
            mChart.setXValueFormat(formatter);

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
        SimpleDateFormat sdf    = new SimpleDateFormat(format);

        String startDate        = sdf.format(startTime);
        String endDate          = sdf.format(endTime);
        Long dayDate            = Long.valueOf(1);

        if (mTimeClass.getPeriodType() == TypeDataSet.Period.PERIOD_DAY) {
            mDateTv.setText(startDate);
        } else if (mTimeClass.getPeriodType() == TypeDataSet.Period.PERIOD_WEEK) {
            mDateTv.setText(startDate + " ~ " + endDate);
            dayDate = Long.valueOf(7);
        } else {
            mDateTv.setText(startDate + " ~ " + endDate);
            Long month = StringUtil.getLong(endDate.substring(5, 7));
            if (month == 1) {
                dayDate = Long.valueOf(31);
            } else if (month == 2) {
                dayDate = Long.valueOf(28);
            } else if (month == 3) {
                dayDate = Long.valueOf(31);
            } else if (month == 4) {
                dayDate = Long.valueOf(30);
            } else if (month == 5) {
                dayDate = Long.valueOf(31);
            } else if (month == 6) {
                dayDate = Long.valueOf(30);
            } else if (month == 7) {
                dayDate = Long.valueOf(31);
            } else if (month == 8) {
                dayDate = Long.valueOf(31);
            } else if (month == 9) {
                dayDate = Long.valueOf(30);
            } else if (month == 10) {
                dayDate = Long.valueOf(31);
            } else if (month == 11) {
                dayDate = Long.valueOf(30);
            } else if (month == 12) {
                dayDate = Long.valueOf(31);
            }
        }

        format = "yyyy-MM-dd";
        sdf = new SimpleDateFormat(format);
        startDate = sdf.format(startTime);
        endDate = sdf.format(endTime);

        Tr_login login = Define.getInstance().getLoginInfo();
        Goal_Water = StringUtil.getLong(login.goal_water_ntkqy) * dayDate;

        getBottomDataLayout(startDate, endDate);
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

            DBHelper helper             = new DBHelper(getContext());
            DBHelperWater waterDb       = helper.getWaterDb();

            TypeDataSet.Period period   = mTimeClass.getPeriodType();
            if (period == TypeDataSet.Period.PERIOD_DAY) {
                String toDay            = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getStartTime());
                List<BarEntry> yVals1   = waterDb.getResultDay(toDay);
                mChart.setData(yVals1);
            } else if (period == TypeDataSet.Period.PERIOD_WEEK) {
                String startDay         = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getStartTime());
                String endDay           = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getEndTime());
                List<BarEntry> yVals1   = waterDb.getResultWeek(startDay, endDay);
                mChart.setData(yVals1);
            } else if (period == TypeDataSet.Period.PERIOD_MONTH) {
                String startDay         = CDateUtil.getFormattedString_yyyy(mTimeClass.getStartTime());
                String endDay           = CDateUtil.getFormattedString_MM(mTimeClass.getStartTime());
                if (endDay.substring(0, 1).equals("0")) {
                    endDay = endDay.substring(1, 2);
                }

                List<BarEntry> yVals1 = waterDb.getResultMonth(startDay, endDay);
                mChart.setData(yVals1);
            }

            setNextButtonVisible();
            mChart.animateY();
        }
    }

    /**
     * 하단 데이터 세팅하기
     *
     * @param startDate
     * @param endDate
     */
    private void getBottomDataLayout(String startDate, String endDate) {
        DBHelper helper = new DBHelper(getContext());
        DBHelperWater db = helper.getWaterDb();
        DBHelperWater.WaterStaticData bottomData = db.getResultStatic(startDate, endDate);

        mWaterToDayTv.setText(makeStringComma(Integer.toString(bottomData.getAmount()).replace(",", "")));

        mWaterTargetTv.setText(makeStringComma(Long.toString(Goal_Water).replace(",", "")));

        if (Goal_Water == 0 || (float) bottomData.getAmount() == 0) {
            mWaterAttTv.setText("0");
        } else {
            mWaterAttTv.setText(String.format("%.2f", (float) bottomData.getAmount() / (float) Goal_Water * 100));
        }

        new QeuryVerifyDataTask().execute();
    }

    /*
          문자열 포맷
     */
    public String makeStringComma(String str) {
        if (str.length() == 0)
            return "";
        long value              = Long.parseLong(str);
        DecimalFormat format    = new DecimalFormat("###,###");
        return format.format(value);
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();  // 차트 데이터 Refresh
        mSwipeListView.getHistoryData();    // 히스토리 Refresh
    }
}