package com.greencross.greencare.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mrsohn on 2017. 3. 6..
 */

public interface IBaseFragment {
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    void loadActionbar(CommonActionBar actionBar);
    void onViewCreated(View view, @Nullable Bundle savedInstanceState);

    void onBackPressed();
}
