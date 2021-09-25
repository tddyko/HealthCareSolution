package com.greencross.greencare.sugar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.base.value.TypeDataSet;
import com.greencross.greencare.charting.data.SticEntry;
import com.greencross.greencare.chartview.sugar.SugarStickChartView;
import com.greencross.greencare.chartview.valueFormat.AxisValueFormatter3;
import com.greencross.greencare.component.CFontRadioButton;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperSugar;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.ChartTimeUtil;
import com.greencross.greencare.util.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mrsohn on 2017. 3. 14..
 * 혈당관리
 */

public class SugarManageFragment extends BaseFragment {
    private final String TAG = SugarManageFragment.class.getSimpleName();

    public ChartTimeUtil mTimeClass;
    private SugarStickChartView mChart;
    private TextView mDateTv;

    private ConstraintLayout layout_sugar_history;
    private ConstraintLayout layout_sugar_graph;
    private CFontRadioButton btn_sugar_graph;
    private CFontRadioButton btn_sugar_history;

    private TextView mStatTv;
    private TextView mBottomBeforeTv;
    private TextView mBottomAfterTv;
    private TextView mBottomMinTv;
    private TextView mBottomMaxTv;

    private SugarSwipeListView mSwipeListView;
    private RadioGroup mTypeRg;

    private ImageView mImageView4;
    private ImageButton imgPre_btn;
    private ImageButton imgNext_btn;

    private AxisValueFormatter3 xFormatter;


    public static Fragment newInstance() {
        SugarManageFragment fragment = new SugarManageFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sugar_manage, container, false);
        return view;
    }

    /**
     * 액션바 세팅
     */
    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.text_bloodsugar_manage));
        actionBar.setActionBarMediBtn(SugarMedInputFragment.class, new Bundle());   // 투약정보
        actionBar.setActionBarWriteBtn(SugarInputFragment.class, new Bundle());     // 입력 화면
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDateTv                 = (TextView) view.findViewById(R.id.period_date_textview);
        layout_sugar_graph      = (ConstraintLayout) view.findViewById(R.id.layout_sugar_graph);
        layout_sugar_history    = (ConstraintLayout) view.findViewById(R.id.layout_sugar_history);
        btn_sugar_graph         = (CFontRadioButton) view.findViewById(R.id.btn_sugar_graph);
        btn_sugar_history       = (CFontRadioButton) view.findViewById(R.id.btn_sugar_history);

        mImageView4             = (ImageView) view.findViewById(R.id.imageView4);

        mTypeRg                 = (RadioGroup) view.findViewById(R.id.radiogroup_sugar_type);
        RadioButton typeAll     = (RadioButton) view.findViewById(R.id.radio_sugar_type_all);
        RadioButton typeBefore  = (RadioButton) view.findViewById(R.id.radio_sugar_type_before);
        RadioButton typeAfter   = (RadioButton) view.findViewById(R.id.radio_sugar_type_after);
        mTypeRg.setOnCheckedChangeListener(mTypeCheckedChangeListener);

        RadioGroup periodRg         = (RadioGroup) view.findViewById(R.id.period_radio_group);
        RadioButton radioBtnDay     = (RadioButton) view.findViewById(R.id.period_radio_btn_day);
        RadioButton radioBtnWeek    = (RadioButton) view.findViewById(R.id.period_radio_btn_week);
        RadioButton radioBtnMonth   = (RadioButton) view.findViewById(R.id.period_radio_btn_month);

        imgPre_btn                  = (ImageButton) view.findViewById(R.id.pre_btn);
        imgNext_btn                 = (ImageButton) view.findViewById(R.id.next_btn);

        mStatTv             = (TextView) view.findViewById(R.id.textView37);
        mBottomBeforeTv     = (TextView) view.findViewById(R.id.bottom_sugar_before_textview);
        mBottomAfterTv      = (TextView) view.findViewById(R.id.bottom_sugar_after_textview);
        mBottomMinTv        = (TextView) view.findViewById(R.id.bottom_sugar_min_textview);
        mBottomMaxTv        = (TextView) view.findViewById(R.id.bottom_sugar_max_textview);



        view.findViewById(R.id.pre_btn).setOnClickListener(mClickListener);
        view.findViewById(R.id.next_btn).setOnClickListener(mClickListener);
        view.findViewById(R.id.btn_sugar_graph).setOnClickListener(mClickListener);
        view.findViewById(R.id.btn_sugar_history).setOnClickListener(mClickListener);
        periodRg.setOnCheckedChangeListener(mCheckedChangeListener);

        mTimeClass      = new ChartTimeUtil(radioBtnDay, radioBtnWeek, radioBtnMonth, typeAll, typeBefore, typeAfter);
        mChart          = new SugarStickChartView(getContext(), view, mTimeClass);

        xFormatter = new AxisValueFormatter3(mTimeClass.getPeriodType());
        mChart.setXValueFormat(xFormatter);

        // 스와이프 리스트뷰 세팅 하기
        mSwipeListView  = new SugarSwipeListView(view, SugarManageFragment.this);

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

            } else if (vId == R.id.btn_sugar_graph) {
                layout_sugar_history.setVisibility(View.GONE);
                layout_sugar_graph.setVisibility(View.VISIBLE);

                getData();
            } else if (vId == R.id.btn_sugar_history) {
                layout_sugar_graph.setVisibility(View.GONE);
                layout_sugar_history.setVisibility(View.VISIBLE);

                // 스와이프 리스트뷰 데이터 세팅 하기
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
     * 모두, 식전, 식후
     */
    public RadioGroup.OnCheckedChangeListener mTypeCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            // 모두, 식전, 식후
            TypeDataSet.EatState EatState = mTimeClass.getEatType();
            String State    = "";
            if (EatState == TypeDataSet.EatState.TYPE_BEFORE) {
                State        = "식전";
            } else if (EatState == TypeDataSet.EatState.TYPE_AFTER) {
                State        = "식후";
            }
            // 일간, 주간, 월간
            TypeDataSet.Period periodType = mTimeClass.getPeriodType();

            if (periodType == TypeDataSet.Period.PERIOD_DAY) {
                mStatTv.setText("일간 " + State + " 통계");
            } else if (periodType == TypeDataSet.Period.PERIOD_WEEK) {
                mStatTv.setText("주간 " + State + " 통계");
            } else if (periodType == TypeDataSet.Period.PERIOD_MONTH) {
                mStatTv.setText("월간 " + State + " 통계");
            }

            getBeforeAndAfterType();
            getData();   // 날자 세팅 후 조회
        }
    };

    /**
     * 모두, 식전, 식후 여부 판단
     *
     * @return
     */
    private int getBeforeAndAfterType() {
        int beforeAntAfter = Define.SUGAR_TYPE_ALL;
        if (mTypeRg.getCheckedRadioButtonId() == R.id.radio_sugar_type_all) {
            try {
                mImageView4.setImageResource(R.drawable.graph_def);
            } catch (Exception e) {
            }
            mChart.setYAxisMinimum(60f, 243, 9);
            beforeAntAfter = Define.SUGAR_TYPE_ALL;
        } else if (mTypeRg.getCheckedRadioButtonId() == R.id.radio_sugar_type_before) {
            try {
                mImageView4.setImageResource(R.drawable.graph_def_medi);
            } catch (Exception e) {

            }
            // 식전 60~240
            mChart.setYAxisMinimum(60f, 152, 9);
            beforeAntAfter = Define.SUGAR_TYPE_BEFORE;
        } else if (mTypeRg.getCheckedRadioButtonId() == R.id.radio_sugar_type_after) {
            try {
                mImageView4.setImageResource(R.drawable.graph_def_medi);
            } catch (Exception e) {
            }
            // 식후 120~240
            mChart.setYAxisMinimum(60f, 243, 9);
            beforeAntAfter = Define.SUGAR_TYPE_AFTER;
        }
        return beforeAntAfter;
    }

    /**
     * 일간,주간,월간
     */
    public RadioGroup.OnCheckedChangeListener mCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            // 모두, 식전, 식후
            TypeDataSet.EatState EatState = mTimeClass.getEatType();
            String State    = "";
            if (EatState == TypeDataSet.EatState.TYPE_BEFORE) {
                State       = "식전";
            } else if (EatState == TypeDataSet.EatState.TYPE_AFTER) {
                State       = "식후";
            }

            // 일간, 주간, 월간
            TypeDataSet.Period periodType = mTimeClass.getPeriodType();
            mTimeClass.clearTime();         // 날자 초기화
            if (periodType == TypeDataSet.Period.PERIOD_DAY) {
                mStatTv.setText("일간 " + State + " 통계");
            } else if (periodType == TypeDataSet.Period.PERIOD_WEEK) {
                mStatTv.setText("주간 " + State + " 통계");
            } else if (periodType == TypeDataSet.Period.PERIOD_MONTH) {
                mStatTv.setText("월간 " + State + " 통계");
            }

            xFormatter = new AxisValueFormatter3(periodType);
            mChart.setXValueFormat(xFormatter);

            getData();   // 날자 세팅 후 조회
            setNextButtonVisible();
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

        TypeDataSet.Period period = mTimeClass.getPeriodType();
        if (period == TypeDataSet.Period.PERIOD_DAY) {
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


    public class QeuryVerifyDataTask extends AsyncTask<Void, Void, List<SticEntry>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        protected List<SticEntry> doInBackground(Void... params) {
            DBHelper helper = new DBHelper(getContext());
            DBHelperSugar sugarDb = helper.getSugarDb();
            TypeDataSet.Period period = mTimeClass.getPeriodType();

            // 모두, 식전, 식후 판단
            int beforeAndAfter = getBeforeAndAfterType();

            List<SticEntry> yVals1 = null;
            mChart.setXvalMinMax(mTimeClass);
            if (period == TypeDataSet.Period.PERIOD_DAY) {
                String toDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getStartTime());
                yVals1 = sugarDb.getResultDay(toDay, beforeAndAfter);
            } else if (period == TypeDataSet.Period.PERIOD_WEEK) {
                String startDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getStartTime());
                String endDay = CDateUtil.getFormattedString_yyyy_MM_dd(mTimeClass.getEndTime());

//                mChart.setLabelCnt(Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_WEEK));
                yVals1 = sugarDb.getResultWeek(startDay, endDay, beforeAndAfter);

                Log.i(TAG, "PERIOD_WEEK.size=" + yVals1.size());
            } else if (period == TypeDataSet.Period.PERIOD_MONTH) {

                String startDay = CDateUtil.getFormattedString_yyyy(mTimeClass.getStartTime());
                String endDay = CDateUtil.getFormattedString_MM(mTimeClass.getStartTime());

                // 이번달 최대 일수
                Calendar cal = Calendar.getInstance(); // CDateUtil.getCalendar_yyyyMMdd(startDay);
                cal.setTime(new Date(mTimeClass.getStartTime()));
                int dayCnt = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

                xFormatter.setMonthMax(dayCnt);

                Logger.i(TAG, "dayCnt=" + dayCnt + ", month=" + (cal.get(Calendar.MONTH) + 1));
                // sqlite 조회 하여 결과 가져오기
                yVals1 = sugarDb.getResultMonth(startDay, endDay, dayCnt, beforeAndAfter);
//                mChart.setLabelCnt((dayCnt / 2) - 2);
                Log.i(TAG, "PERIOD_MONTH.size=" + yVals1.size());
            }

            return yVals1;
        }

        @Override
        protected void onPostExecute(List<SticEntry> yVals1) {
            super.onPostExecute(yVals1);
            hideProgress();
            mChart.setData(yVals1);
            mChart.invalidate();
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
        DBHelperSugar sugarDb = helper.getSugarDb();

        TypeDataSet.EatState EatState = mTimeClass.getEatType();
        int type = 0;
        if (EatState == TypeDataSet.EatState.TYPE_ALL) {
            type = 0;
        } else if (EatState == TypeDataSet.EatState.TYPE_BEFORE) {
            type = 1;
        } else if (EatState == TypeDataSet.EatState.TYPE_AFTER) {
            type = 2;
        }

        DBHelperSugar.SugarStaticData bottomData = sugarDb.getResultStatic(startDate, endDate, type);

        mBottomBeforeTv.setText(Integer.toString(bottomData.getBefsugar()));
        mBottomAfterTv.setText(Integer.toString(bottomData.getAftsugar()));
        mBottomMaxTv.setText(Integer.toString(bottomData.getMaxsugar()));
        mBottomMinTv.setText(Integer.toString(bottomData.getMinsugar()));

        new QeuryVerifyDataTask().execute();
    }


    @Override
    public void onResume() {
        super.onResume();
        getData();  // 차트 데이터 Refresh
        mSwipeListView.getHistoryData();    // 히스토리 Refresh
    }
}
