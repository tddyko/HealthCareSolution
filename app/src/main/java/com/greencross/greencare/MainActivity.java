package com.greencross.greencare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.login.LoginFragment;
import com.greencross.greencare.main.MainFragment;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.SharedPref;

public class MainActivity extends BaseActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            boolean isLoginSuccess = SharedPref.getInstance().getPreferences(SharedPref.IS_LOGIN_SUCEESS, false);
            boolean isAutoLogin = SharedPref.getInstance().getPreferences(SharedPref.IS_AUTO_LOGIN, false);
            if (isLoginSuccess && isAutoLogin) {
                // 자동로그인 && 로그인이 완료 되었을 때
                replaceFragment(MainFragment.newInstance());
            } else {
                replaceFragment(LoginFragment.newInstance());
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (getVisibleFragment() instanceof BaseFragment) {
            int backCnt = getSupportFragmentManager().getBackStackEntryCount();
            Logger.i(TAG, "onBackPressed.backCnt=" + getSupportFragmentManager().getBackStackEntryCount() + " ,getClass()=" + getClass());
            if (getVisibleFragment() instanceof BaseFragment) {
                BaseFragment baseFragment = (BaseFragment)getVisibleFragment();
                baseFragment.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    public void superBackPressed() {
        super.onBackPressed();
    }

    public void finishStep() {
        super.finishStep();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Logger.i(TAG, TAG + ".onResume()");
        BaseFragment.newInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.i(TAG, TAG + ".onActivityResult");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (getVisibleFragment() != null) {
            getVisibleFragment().onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
