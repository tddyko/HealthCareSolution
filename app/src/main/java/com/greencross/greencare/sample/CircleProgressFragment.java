package com.greencross.greencare.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greencross.greencare.R;

/**
 * Created by MrsWin on 2017-03-01.
 */

public class CircleProgressFragment  extends Fragment {

    public static Fragment newInstance() {
        CircleProgressFragment fragment = new CircleProgressFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sample_circle_progress, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        CircleProgressBar progress = (CircleProgressBar) view.findViewById(R.id.custom_progressBar);
        progress.setStrokeWidth(40f);   // 프로그래스 두께
        progress.setColor(Color.GREEN, Color.GRAY);
        progress.setMax(100);       // 최대값
        progress.setProgress(80);   // 입력 값

    }
}
