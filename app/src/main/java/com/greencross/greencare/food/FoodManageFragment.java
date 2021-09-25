package com.greencross.greencare.food;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;

/**
 * Created by mrsohn on 2017. 3. 14..
 */

public class FoodManageFragment extends BaseFragment {

    private android.widget.TextView tvFoodWriting;
    private android.widget.TextView tvFoodHistory;

    private LinearLayout liFoodWritingUnderLine;
    private LinearLayout liFoodHistoryUnderLine;

    private FoodManageWriteView mRegistView;
    private FoodManageHistoryView mHistoryView;

    public static Fragment newInstance() {
        FoodManageFragment fragment = new FoodManageFragment();
        return fragment;
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.text_fooddiary));
    }

    @Nullable
    @Override//activity_food_manage
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_food_manage, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View registView = view.findViewById(R.id.layout_food_write);
        View historyView = view.findViewById(R.id.layout_food_history);

        // 등록하기 화면
        mRegistView = new FoodManageWriteView(FoodManageFragment.this, registView);
        // 히스토리 화면
        mHistoryView = new FoodManageHistoryView(FoodManageFragment.this, historyView);

        this.tvFoodHistory = (TextView) view.findViewById(R.id.tvFoodHistory);
        this.tvFoodWriting = (TextView) view.findViewById(R.id.tvFoodWriting);
        this.liFoodWritingUnderLine = (LinearLayout) view.findViewById(R.id.liFoodWritingUnderLine);
        this.liFoodHistoryUnderLine = (LinearLayout) view.findViewById(R.id.liFoodHistoryUnderLine);


        this.tvFoodWriting = (TextView) view.findViewById(R.id.tvFoodWriting);

        view.findViewById(R.id.tvFoodHistory).setOnClickListener(mClickListener);
        view.findViewById(R.id.tvFoodWriting).setOnClickListener(mClickListener);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();

            if (vId == R.id.tvFoodHistory) {
                // 히스토리 화면
                liFoodWritingUnderLine.setVisibility(View.GONE);
                liFoodHistoryUnderLine.setVisibility(View.VISIBLE);

                mHistoryView.setVisibility(View.VISIBLE);
                mRegistView.setVisibility(View.GONE);
                tvFoodWriting.setTextColor(Color.parseColor("#969696"));
                tvFoodHistory.setTextColor(Color.parseColor("#009AC5"));

                mHistoryView.getData();
            } else if (vId == R.id.tvFoodWriting) {
                // 기록하기 화면
                liFoodWritingUnderLine.setVisibility(View.VISIBLE);
                liFoodHistoryUnderLine.setVisibility(View.GONE);

                mRegistView.setVisibility(View.VISIBLE);
                mHistoryView.setVisibility(View.GONE);
                tvFoodWriting.setTextColor(Color.parseColor("#009AC5"));
                tvFoodHistory.setTextColor(Color.parseColor("#969696"));
                mHistoryView.getData();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (mRegistView != null && mRegistView.getVisibility() == View.VISIBLE) {
            mRegistView.onResume();
        }
    }
}
