package com.greencross.greencare;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.login.LoginFragment;
import com.greencross.greencare.main.MainFragment;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.SharedPref;


public class BaseActivity extends AppCompatActivity {
    private final String TAG = BaseActivity.class.getSimpleName();

    protected LinearLayout mProgressView;

    protected CommonActionBar mCommonActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // 커스텀 폰트 일괄 적용
        SharedPref.getInstance().initContext(BaseActivity.this);
        mProgressView = (LinearLayout) findViewById(R.id.main_progress);

        // 공통 액션바
        mCommonActionBar = new CommonActionBar(this);
    }

    public CommonActionBar getCommonActionBar() {
        if (mCommonActionBar == null)
            mCommonActionBar = new CommonActionBar(this);

        return mCommonActionBar;
    }


    /**
     * 커스텀 폰트 일괄적용
     * @param view
     */
    private void setGlobalFont(View view) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), getString(R.string.KelsonSansRegular));
        if (view != null) {
            if(view instanceof ViewGroup){
                ViewGroup vg = (ViewGroup)view;
                int vgCnt = vg.getChildCount();
                for(int i = 0; i < vgCnt; i++){
                    View v = vg.getChildAt(i);
                    if(v instanceof TextView){
                        ((TextView) v).setTypeface(typeface);
                    }
                    setGlobalFont(v);
                }
            }
        }
    }


    public void showProgress() {
        mProgressView.setVisibility(View.VISIBLE);
        mCommonActionBar.showBlockLayout(true);
    }

    public void hideProgress() {
        mProgressView.setVisibility(View.GONE);
        mCommonActionBar.showBlockLayout(false);
    }

    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, null);
    }

    public void replaceFragment(Fragment fragment, Bundle bundle) {
        boolean isReplace = (fragment instanceof LoginFragment) || (fragment instanceof MainFragment);
        replaceFragment(fragment, isReplace, true, bundle);
    }

    public void replaceFragment(final Fragment fragment, final boolean isReplace, boolean isAnim, Bundle bundle) {
        BaseFragment.newInstance(this);
        if (isReplace)
            removeAllFragment();

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (isAnim)
            transaction.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_pop_enter, R.anim.fragment_pop_exit);
        if (bundle != null)
            fragment.setArguments(bundle);

        transaction.replace(R.id.content_layout, fragment, fragment.getClass().getSimpleName());
        if (!isFinishing() && !isDestroyed()) {
            if (isReplace == false)
                transaction.addToBackStack(null);

            transaction.commit();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isReplace == false)
                        transaction.addToBackStack(null);

                    transaction.commitAllowingStateLoss();
                }
            }, 100);
        }


        printFragmentLog();
    }

    public Fragment getVisibleFragment() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null && fragment.isVisible()) {
                return fragment;
            }
        }
        return null;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public void removeAllFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStackImmediate();
    }


    private void printFragmentLog() {
        if (getSupportFragmentManager().getFragments() != null) {
            Logger.i(TAG, "replaceFragment.size=" + getSupportFragmentManager().getFragments().size());

            for (Fragment fg : getSupportFragmentManager().getFragments()) {
                if (fg != null)
                    Logger.i(TAG, "replaceFragment.name=" + fg.toString());
            }
        }
    }

    View.OnClickListener actionBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (R.id.action_bar_back_button == vId) {
                onBackPressed();
            }
        }
    };


    public void finishStep() {
        if (mIsFinishing) {
            mIsFinishing = false;
            super.onBackPressed();
        } else {
            if (getClass().getName().equals(MainActivity.class.getName())) {
                Toast.makeText(getApplicationContext(), getString(R.string.message_finish_message), Toast.LENGTH_SHORT).show();
                mIsFinishing = true;
                mMasterHandler.sendEmptyMessageDelayed(HANDLE_FINISHING_EXPIRE, 3000L);
            } else {
                super.onBackPressed();
            }
        }
    }

    protected boolean mIsFinishing = false;
    public final int HANDLE_FINISHING_EXPIRE = -999;
    protected Handler mMasterHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_FINISHING_EXPIRE:
                    mIsFinishing = false;
                    break;
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        Logger.i(TAG, TAG + ".onResume()");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseFragment baseFragment = (BaseFragment) getVisibleFragment();
        if (baseFragment != null) {
        }
    }
}
