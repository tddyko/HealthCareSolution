package com.greencross.greencare.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.greencross.greencare.BaseActivity;
import com.greencross.greencare.R;

import java.lang.reflect.Constructor;

/**
 * Created by mrsohn on 2017. 3. 6..
 */

public class DummyActivity extends BaseActivity {
    private static final String TAG = DummyActivity.class.getSimpleName();
    private static final String FRAGMENT_NAME = "fragment_name";
    private static final String FRAGMENT_BUNDLE = "fragment_bundle";

    public static int reqCode = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment(getFragment());
    }

    public BaseFragment getFragment() {
        Intent intent = getIntent();
        BaseFragment fragment = null;
        try {
            String className = intent.getStringExtra(FRAGMENT_NAME);
            Class<?> cl = Class.forName(className);
            Constructor<?> co = cl.getConstructor();
            fragment = (BaseFragment) co.newInstance();
        } catch (Exception e) {
            Log.e(TAG, "getFragment", e);
        }

        return fragment;
    }

    public void initFragment(BaseFragment fragment) {
        replaceFragment(fragment, true, false, getBundle());
    }

    public static void startActivity(Activity activity, Class<?> cls, Bundle bundle) {
        Intent intent = getIntentData(activity, cls, bundle);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
    }

    public static void startActivityForResult(Activity activity, int reqCode, Class<?> cls, Bundle bundle) {
        Intent intent = getIntentData(activity, cls, bundle);
        activity.startActivityForResult(intent, reqCode, null);
        activity.overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
    }


    public static void startActivity(BaseFragment fragment, Class<?> cls, Bundle bundle) {
        Intent intent = getIntentData(fragment.getContext(), cls, bundle);
        fragment.startActivity(intent);
        fragment.getActivity().overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
    }


    public static void startActivityForResult(BaseFragment fragment, int reqCode, Class<?> cls, Bundle bundle) {
        Intent intent = getIntentData(fragment.getContext(), cls, bundle);
        fragment.startActivityForResult(intent, reqCode, null);
        fragment.getActivity().overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
    }

    private static Intent getIntentData(Context context, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, DummyActivity.class);
        intent.putExtra(FRAGMENT_NAME, cls.getName());
        intent.putExtra(FRAGMENT_BUNDLE, bundle);
        return intent;
    }

    private Bundle getBundle() {
        Bundle bundle = getIntent().getBundleExtra(FRAGMENT_BUNDLE);
        return bundle;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onResume() {
        super.onResume();
        BaseFragment.newInstance(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, TAG+".onStop()");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, TAG+".onDestroy()");
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fragment_pop_enter, R.anim.fragment_pop_exit);
    }
}
