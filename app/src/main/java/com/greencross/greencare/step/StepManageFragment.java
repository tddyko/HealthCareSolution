package com.greencross.greencare.step;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.SharedPref;

/**
 * Created by insystemscompany on 2017. 2. 28..
 */

public class StepManageFragment extends BaseFragment {
    private static final String TAG = StepManageFragment.class.getSimpleName();

    private GoogleFitChartView mGoogleFitView;
    private View mView;

    public static Fragment newInstance() {
        StepManageFragment fragment = new StepManageFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_walk_manage, container, false);
        return view;
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.text_work));                        // 액션바 타이틀
        actionBar.setActionBarWriteStepBtn(StepInputFragment.class, new Bundle());  // 액션바 입력 버튼
        int dataSource = SharedPref.getInstance().getPreferences(SharedPref.STEP_DATA_SOURCE_TYPE, -1);
        if (Define.STEP_DATA_SOURCE_BAND == dataSource) {
            actionBar.setActionBarBandStepBtn(StepManageFragment.this, new Bundle());  // 액션바 입력 버튼
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;

        int dataSource = SharedPref.getInstance().getPreferences(SharedPref.STEP_DATA_SOURCE_TYPE, -1);
        if (Define.STEP_DATA_SOURCE_GOOGLE_FIT == dataSource) {
            mGoogleFitView = new GoogleFitChartView(StepManageFragment.this, view);   // 차트뷰 세팅
        } else if (Define.STEP_DATA_SOURCE_BAND == dataSource) {
            new BandChartView(StepManageFragment.this, mView);   // 차트뷰 세팅
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int dataSource = SharedPref.getInstance().getPreferences(SharedPref.STEP_DATA_SOURCE_TYPE, -1);
        Logger.i(TAG, "dataSource="+dataSource);
        if (Define.STEP_DATA_SOURCE_GOOGLE_FIT == dataSource) {
            mGoogleFitView = new GoogleFitChartView(StepManageFragment.this, mView);   // 차트뷰 세팅
        } else if (Define.STEP_DATA_SOURCE_BAND == dataSource) {
            new BandChartView(StepManageFragment.this, mView);   // 차트뷰 세팅
        }
    }

    public void onChartViewResume(){
        int dataSource = SharedPref.getInstance().getPreferences(SharedPref.STEP_DATA_SOURCE_TYPE, -1);

        if (Define.STEP_DATA_SOURCE_GOOGLE_FIT == dataSource) {
            mGoogleFitView = new GoogleFitChartView(StepManageFragment.this, mView);   // 차트뷰 세팅
        } else if (Define.STEP_DATA_SOURCE_BAND == dataSource) {
            new BandChartView(StepManageFragment.this, mView);   // 차트뷰 세팅
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleFitView != null) {
            mGoogleFitView.onResume();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleFitView != null) {
            mGoogleFitView.onStop();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.i(TAG, "onDetach");
        if (mGoogleFitView != null) {
            mGoogleFitView.onDetach();
        }
    }
}