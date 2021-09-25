package com.greencross.greencare.pressure;

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
import com.greencross.greencare.base.IBaseFragment;
import com.greencross.greencare.base.value.TypeDataSet;
import com.greencross.greencare.charting.data.PressureEntry;
import com.greencross.greencare.chartview.presure.PresureChartView;
import com.greencross.greencare.chartview.valueFormat.AxisValueFormatter3;
import com.greencross.greencare.component.CFontRadioButton;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperPresure;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.ChartTimeUtil;
import com.greencross.greencare.util.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by insystemscompany on 2017. 2. 28..
 */

public class PressureManageFragment extends BaseFragment implements IBaseFragment {
    private final String TAG = PressureManageFragment.class.getSimpleName();

    public ChartTimeUtil mTimeClass;
    private PresureChartView mChart;
    private TextView mDateTv;

    private ConstraintLayout layout_pressure_history;
    private ConstraintLayout layout_pressure_graph;
    private CFontRadioButton btn_pressure_graph;
    private CFontRadioButton btn_pressure_history;

    private TextView mStatTv;
    private TextView mBottomSystolicTv;
    private TextView mBottomDiastolcTv;
    private TextView mBottomMinTv;
    private TextView mBottomMaxTv;
    private ImageButton imgPre_btn;
    private ImageButton imgNext_btn;

    private PressureSwipeListView mSwipeListView;

    private AxisValueFormatter3 xFormatter;

    public static Fragment newInstance() {
        PressureManageFragment fragment = new PressureManageFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_pressure_manage, container, false);
        return view;
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.text_pressure_manage));// 액션바 타이틀
        actionBar.setActionBarMediBtn(PressureMedInputFragment.class, new Bundle());   // 투약정보
        actionBar.setActionBarWriteBtn(PressureInputFragment.class, new Bundle());     // 입력화면
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDateTv = (TextView) view.findViewById(R.id.period_date_textview);

        layout_pressure_graph  = (ConstraintLayout) view.findViewById(R.id.layout_pressure_graph);
        layout_pressure_history  = (ConstraintLayout) view.findViewById(R.id.layout_pressure_history);
        btn_pressure_graph  = (CFontRadioButton) view.findViewById(R.id.btn_pressure_graph);
        btn_pressure_history  = (CFontRadioButton) view.findViewById(R.id.btn_pressure_history);

        RadioGroup periodRg = (RadioGroup) view.findViewById(R.id.period_radio_group);
        RadioButton radioBtnDay = (RadioButton) view.findViewById(R.id.period_radio_btn_day);
        RadioButton radioBtnWeek = (RadioButton) view.findViewById(R.id.period_radio_btn_week);
        RadioButton radioBtnMonth = (RadioButton) view.findViewById(R.id.period_radio_btn_month);

        mStatTv = (TextView) view.findViewById(R.id.textView21);
        mBottomSystolicTv = (TextView) view.findViewById(R.id.bottom_avg_min_textview);
        mBottomDiastolcTv = (TextView) view.findViewById(R.id.bottom_avg_max_textview);
        mBottomMinTv = (TextView) view.findViewById(R.id.bottom_min_textview);
        mBottomMaxTv = (TextView) view.findViewById(R.id.bottom_max_textview);

        imgPre_btn                  = (ImageButton) view.findViewById(R.id.pre_btn);
        imgNext_btn                 = (ImageButton) view.findViewById(R.id.next_btn);

        view.findViewById(R.id.pre_btn).setOnClickListener(mClickListener);
        view.findViewById(R.id.next_btn).setOnClickListener(mClickListener);

        view.findViewById(R.id.btn_pressure_graph).setOnClickListener(mClickListener);
        view.findViewById(R.id.btn_pressure_history).setOnClickListener(mClickListener);
        periodRg.setOnCheckedChangeListener(mCheckedChangeListener);

        mTimeClass = new ChartTimeUtil(radioBtnDay, radioBtnWeek, radioBtnMonth);
        mChart = new PresureChartView(getContext(), view);

        getData();

        mSwipeListView = new PressureSwipeListView(view, PressureManageFragment.this);

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
            }else if (vId == R.id.btn_pressure_graph) {
                layout_pressure_history.setVisibility(View.GONE);
                layout_pressure_graph.setVisibility(View.VISIBLE);

                getData();
            }else if (vId == R.id.btn_pressure_history) {
                layout_pressure_graph.setVisibility(View.GONE);
                layout_pressure_history.setVisibility(View.VISIBLE);

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
            TypeDataSet.Period periodType = mTimeClass.getPeriodType();
            mTimeClass.clearTime();         // 날자 초기화

            if(periodType == TypeDataSet.Period.PERIOD_DAY){
                mStatTv.setText(getString(R.string.daily_statistics));
            }else if(periodType == TypeDataSet.Period.PERIOD_WEEK){
                mStatTv.setText(getString(R.string.weekly_statistics));
            }else if(periodType == TypeDataSet.Period.PERIOD_MONTH){
                mStatTv.setText(getString(R.string.monthly_statistics));
            }

            getData();   // 날자 세팅 후 조회
        }
    };

    /**
     * 날자 계산 후 조회
     */
    private void getData() {
        long startTime = mTimeClass.getStartTime();
        long endTime = mTimeClass.getEndTime();

        xFormatter = new AxisValueFormatter3(mTimeClass.getPeriodType());
        mChart.setXValueFormat(xFormatter);

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

    /**
     * 하단 데이터 세팅하기
     * @param startDate
     * @param endDate
     */
    private void getBottomDataLayout(String startDate, String endDate) {
        DBHelper helper = new DBHelper(getContext());
        DBHelperPresure db = helper.getPresureDb();
        DBHelperPresure.PressureData bottomData = db.getResultStatic(helper, startDate, endDate);

        mBottomSystolicTv.setText(""+bottomData.getSystolic());
        mBottomDiastolcTv.setText(""+bottomData.getDiastolc());
        mBottomMaxTv.setText(""+bottomData.getMaxsystolic());
        mBottomMinTv.setText(""+bottomData.getMaxdiastolc());

        new QeuryVerifyDataTask().execute();
    }

    public class QeuryVerifyDataTask extends AsyncTask<Void, Void, List<PressureEntry>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        protected List<PressureEntry> doInBackground(Void... params) {
            List<PressureEntry> yVals1 = null;
            DBHelper helper = new DBHelper(getContext());
            DBHelperPresure presureDb = helper.getPresureDb();
            TypeDataSet.Period period = mTimeClass.getPeriodType();

            mChart.setXvalMinMax(mTimeClass);

            if (period == TypeDataSet.Period.PERIOD_DAY) {
                String toDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getStartTime());
                yVals1 = presureDb.getResultDay(helper, toDay);
//                mChart.setLabelCnt(Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_WEEK));
            } else if (period == TypeDataSet.Period.PERIOD_WEEK) {
                String startDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getStartTime());
                String endDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getEndTime());

                yVals1 = presureDb.getResultWeek(helper, startDay, endDay);

//                mChart.setLabelCnt(Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_WEEK));
            } else if (period == TypeDataSet.Period.PERIOD_MONTH) {
                String startDay = CDateUtil.getFormattedString_yyyy(mTimeClass.getStartTime());
                String endDay = CDateUtil.getFormattedString_MM(mTimeClass.getStartTime());
                // 이번달 최대 일수
                Calendar cal = Calendar.getInstance(); // CDateUtil.getCalendar_yyyyMMdd(startDay);
                cal.setTime(new Date(mTimeClass.getStartTime()));
                int dayCnt = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                yVals1 = presureDb.getResultMonth(helper, startDay, endDay, dayCnt);

                xFormatter.setMonthMax(dayCnt);

                Logger.i(TAG, "dayCnt="+dayCnt+", month="+(cal.get(Calendar.MONTH)+1) ) ;
                // sqlite 조회 하여 결과 가져오기
//                mChart.setLabelCnt((dayCnt/2));
            }
            return yVals1;
        }

        @Override
        protected void onPostExecute(List<PressureEntry> yVals1) {
            super.onPostExecute(yVals1);
            hideProgress();

            Logger.i(TAG, "yVals1.size="+yVals1.size());
            mChart.setData(yVals1, mTimeClass);
            mChart.invalidate();
            setNextButtonVisible();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
        mSwipeListView.getHistoryData();
    }
}