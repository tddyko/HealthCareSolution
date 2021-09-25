package com.greencross.greencare.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.greencross.greencare.BaseActivity;
import com.greencross.greencare.MainActivity;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperWeight;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.CConnAsyncTask;
import com.greencross.greencare.network.tr.data.Tr_get_infomation;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.DeviceUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.NetworkUtil;
import com.greencross.greencare.util.PackageUtil;
import com.greencross.greencare.util.SharedPref;
import com.greencross.greencare.util.StringUtil;

import java.io.BufferedInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrsWin on 2017-02-16.
 */

public class BaseFragment extends Fragment implements IBaseFragment {
    private final String TAG = BaseFragment.class.getSimpleName();

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 33;
    private int mRequestCode = 1111;

    private IPermission mIpermission = null;

    boolean mIsLogin = false;

    private static BaseActivity mBaseActivity;
    private CommonActionBar mActionBar;

    public static Fragment newInstance(BaseActivity activity) {
        BaseFragment fragment = new BaseFragment();
        mBaseActivity = activity;
        return fragment;
    }

    public void movePage(Fragment fragment) {
        movePage(fragment, null);
    }

    public void movePage(Fragment fragment, Bundle bundle) {
        mActionBar = mBaseActivity.getCommonActionBar();

        mBaseActivity.replaceFragment(fragment, bundle);
    }

    /**
     * 사용할 레이아웃 또는 View 지정
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        onViewCreated(view, savedInstanceState);
        return view;
    }

    /**
     * 뷰가 생성된 후 세팅
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadActionbar(getCommonActionBar());

        doAutoLogin();
    }

    @Override
    public void onBackPressed() {
        Logger.i(TAG, "BaseFragment.onBackPressed().getActivity()=" + getActivity());
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.superBackPressed();
        } else if (getActivity() instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) getActivity();
            activity.onBackPressed();
        } else {
            mBaseActivity.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadActionbar(getCommonActionBar());
        doAutoLogin();
    }

    /**
     * 자동로그인 처리
     */
    private void doAutoLogin() {
        boolean isAutoLogin = SharedPref.getInstance().getPreferences(SharedPref.IS_AUTO_LOGIN, false);
        Tr_login loginData = Define.getInstance().getLoginInfo();
        if (isAutoLogin && loginData == null) {
            Tr_login.RequestData requestData = new Tr_login.RequestData();
            requestData.mber_id = SharedPref.getInstance().getPreferences(SharedPref.SAVED_LOGIN_ID);
            requestData.mber_pwd = SharedPref.getInstance().getPreferences(SharedPref.SAVED_LOGIN_PWD);
            requestData.phone_model = DeviceUtil.getPhoneModelName();
            requestData.pushk = "";
            requestData.app_ver = PackageUtil.getVersionInfo(getContext());

            getData(getContext(), Tr_login.class, requestData, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (obj instanceof Tr_login) {
                        Tr_login data = (Tr_login) obj;
                        if ("Y".equals(data.log_yn)) {

                            if(data.mber_bdwgh_app.equals("") || data.mber_bdwgh_app.isEmpty()){
                                data.mber_bdwgh_app = data.mber_bdwgh;
                            }

                            Define.getInstance().setLoginInfo(data);
                        } else {
                            CDialog.showDlg(getContext(), "로그인에 실패 하였습니다.", new CDialog.DismissListener() {
                                @Override
                                public void onDissmiss() {
                                    getActivity().finish();
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    protected void onCreateOptionsMenu(Menu menu) {
        mBaseActivity.onCreateOptionsMenu(menu);
    }

    private ActionBar getActionBar() {
        return mBaseActivity.getSupportActionBar();
    }

    public CommonActionBar getCommonActionBar() {
        if (mActionBar == null)
            mActionBar = mBaseActivity.getCommonActionBar();

        return mActionBar;
    }

    /**
     * Back 이동시 데이터 전달
     */
    private static Bundle mBackDataBundle;

    protected static void setBackData(Bundle bundle) {
        mBackDataBundle = bundle;
    }

    public static Bundle getBackData() {
        Bundle bundle = mBackDataBundle;
        if (mBackDataBundle != null) {
            mBackDataBundle = new Bundle(); // 초기화
            return bundle;
        } else {
            return new Bundle();
        }
    }

    /**
     * 현재 보여지고 있는 Fragment
     *
     * @return
     */
    public Fragment getVisibleFragment() {
        Fragment fragment = mBaseActivity.getVisibleFragment();

        return fragment;
    }

    public void showProgress() {
        mBaseActivity.showProgress();
    }

    public void hideProgress() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBaseActivity.hideProgress();
            }
        }, 1 * 500);
    }

    /**
     * @param type           // 액션바 타입
     * @param title          // 타이틀
     * @param clickListener1 // 맨 오른쪽 버튼 처리
     * @param clickListener2 // 맨 오른쪽 옆 버튼 처리
     */
    public void setActionBar(Define.ACTION_BAR type, String title, View.OnClickListener clickListener1, View.OnClickListener clickListener2) {
        mActionBar.setActionBar(type, title, clickListener1, clickListener2);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent, null);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        super.startActivity(intent, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode, null);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }

    public void startActivityForResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {

        if (mBaseActivity != null) {
            actionBar.setActionBackBtnVisible(View.VISIBLE);
            actionBar.goneActionBarFunctionBtn();
            actionBar.showActionBar(true);            // 액션바 띄울지 여부

            actionBar.setActionBarTitle("");
            Bundle bundle = getArguments();
            if (bundle != null) {
                String title = getArguments().getString(CommonActionBar.ACTION_BAR_TITLE);
                if (TextUtils.isEmpty(title) == false)
                    actionBar.setActionBarTitle(title);

                Logger.i(TAG, "loadActionbar.title=" + title);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.i(TAG, TAG + ".onActivityResult");
    }

    /**
     * 로드벨런싱
     *
     * @param context
     * @param cls
     * @param obj
     * @param step
     */
    private void getInformation(final Context context, final Class<?> cls, final Object obj, final ApiData.IStep step) {
        Tr_get_infomation.RequestData reqData = new Tr_get_infomation.RequestData();
        reqData.insures_code = "300";
        getData(context, Tr_get_infomation.class, reqData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_get_infomation) {
                    Tr_get_infomation data = (Tr_get_infomation) obj;
                    Define.getInstance().setInformation(data);

                    getData(context, cls, obj, step);
                } else {
//                    CDialog.showDlg(context, "네트워크 연결 상태를 확인해주세요.");
                }
            }
        });
    }

    public void getData(final Context context, final Class<?> cls, final Object obj, final ApiData.IStep step) {
        getData(context, cls, obj, true, step, null);
    }

    public void getData(final Context context, final Class<?> cls, final Object obj, boolean isShowProgress, final ApiData.IStep step, final ApiData.IFailStep failStep) {

        if (NetworkUtil.getConnectivityStatus(context) == false) {
            CDialog.showDlg(context, "네트워크 연결 상태를 확인해주세요.");
            return;
        }
        String url = "http://wkd.walkie.co.kr/SK/WebService/SK_Mobile_Call.asmx/SK_mobile_Call";

        Logger.i(TAG, "LoadBalance.cls=" + cls + ", url=" + url);
        if (TextUtils.isEmpty(url) && (cls != Tr_get_infomation.class)) {
            getInformation(context, cls, obj, step);
            return;
        }

        if (isShowProgress)
            showProgress();

        CConnAsyncTask.CConnectorListener queryListener = new CConnAsyncTask.CConnectorListener() {

            @Override
            public Object run() throws Exception {

                ApiData data = new ApiData();
                return data.getData(context, cls, obj);
            }

            @Override
            public void view(CConnAsyncTask.CQueryResult result) {
                hideProgress();

                if (result.result == CConnAsyncTask.CQueryResult.SUCCESS && result.data != null) {
                    if (step != null) {
                        step.next(result.data);
                    }

                } else {
                    if (failStep != null) {
                        failStep.fail();
                    } else {
                        CDialog.showDlg(context, "데이터 수신에 실패 하였습니다.");
                        Log.e(TAG, "CConnAsyncTask error=" + result.errorStr);
                    }
                }
            }
        };

        CConnAsyncTask asyncTask = new CConnAsyncTask();
        asyncTask.execute(queryListener);
    }

    /**
     * 이미지 url에서 이미지를 가져와 ImageView에 세팅한다.
     *
     * @param imgUrl
     * @param iv
     */
    public void getImageData(final String imgUrl, final ImageView iv) {
        if (imgUrl == null) {
            Logger.d(TAG, "getIndexToImageData imgUrl is null");
            return;
        }

        CConnAsyncTask.CConnectorListener queryListener = new CConnAsyncTask.CConnectorListener() {

            @Override
            public Object run() throws Exception {
                URL url = new URL(imgUrl);
                URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

                Bitmap bm = BitmapFactory.decodeStream(bis);
                bis.close();
                return bm;
            }

            @Override
            public void view(CConnAsyncTask.CQueryResult result) {
                if (result.result == CConnAsyncTask.CQueryResult.SUCCESS && result.data != null) {
                    Bitmap bm = (Bitmap) result.data;
                    iv.setImageBitmap(bm);
                } else {
                    Logger.e(TAG, "CConnAsyncTask error");
                }
            }
        };

        CConnAsyncTask asyncTask = new CConnAsyncTask();
        asyncTask.execute(queryListener);
    }

    public void finishStep() {
        mBaseActivity.finishStep();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public BaseFragment getFragment(Class<?> cls) {
        BaseFragment fragment = null;
        try {
            Constructor<?> co = cls.getConstructor();
            fragment = (BaseFragment) co.newInstance();
        } catch (Exception e) {
            Log.e(TAG, "getFragment", e);
        }
        return fragment;
    }

    public void reqPermissions(String[] perms, IPermission iPermission) {
        mIpermission = iPermission;
        final String[] permissions = getGrandtedPermissions(perms);
        if (permissions.length > 0) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            CDialog.showDlg(getContext(), "권한 설정 후 이용 가능합니다.", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(getActivity()
                            , permissions
                            , REQUEST_PERMISSIONS_REQUEST_CODE);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } else {
            if (iPermission != null) {
                iPermission.result(true);
                mIpermission = null;
            }

        }
    }

    /**
     * 설정이 되지 않은 권한들 가져옴
     * @return
     */
    private String[] getGrandtedPermissions(String... permissions) {
        List<String> list = new ArrayList<>();
        for (String perm : permissions) {
            int isGrandted = ActivityCompat.checkSelfPermission(getContext(), perm);

            if (isGrandted != PackageManager.PERMISSION_GRANTED)
                list.add(perm);
        }

        String[] permissionArr = new String[list.size()];
        permissionArr = list.toArray(permissionArr);
        return permissionArr;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                if (mIpermission != null) {
                    mIpermission.result(true);
                    mIpermission = null;
                }
            } else if (isPermissionGransteds(grantResults)) {
                if (mIpermission != null) {
                    mIpermission.result(true);
                    mIpermission = null;
                }
            } else {
                if (mIpermission != null) {
                    mIpermission.result(false);
                    mIpermission = null;
                } else {
                    CDialog.showDlg(getContext(), "권한 설정 후 이용 가능합니다.", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reqPermissions(permissions, mIpermission);
                        }
                    }, null);
                }
            }
        }
    }

    /**
     * 운동의 목표걸음수, 목표칼로리 생성
     */
    public void setStepTarget(int _calrori, int _step) {

        Tr_login login = Define.getInstance().getLoginInfo();
        //목표칼로리, 목표걸음수 계산
        int bmiLevel = StringUtil.getIntVal(login.mber_bmi_level);
        int sex = StringUtil.getIntVal(login.mber_sex);
        DBHelper helper = new DBHelper(getContext());
        DBHelperWeight weightDb = helper.getWeightDb();
        DBHelperWeight.WeightStaticData bottomData = weightDb.getResultStatic();
        float weight        = 0.f;
        if(!bottomData.getWeight().isEmpty()) {
            weight          = StringUtil.getFloatVal(login.mber_bdwgh_app);
        }else{
            weight          = StringUtil.getFloatVal(login.mber_bdwgh);
        }
        float height = StringUtil.getFloatVal(login.mber_height);
        int actqy = StringUtil.getIntVal(login.mber_actqy);
        int targetCal = 0;  //목표소모칼로리/1일 (kcal)

        float mWeight;   //표준체중
        float mHeight = height * 0.01f;
        mHeight = StringUtil.getFloatVal(String.format("%.2f", mHeight));

        if (sex == 1) {
            //남성
            mWeight = StringUtil.getFloatVal(String.format("%.1f", (mHeight * mHeight) * 22));
        } else {
            //여성
            mWeight = StringUtil.getFloatVal(String.format("%.1f", (mHeight * mHeight) * 21));
        }
        // 체중 증량 필요
        if (bmiLevel == 0) {
            switch (sex) {
                case 1:
                    targetCal = 300;
                    break;
                case 2:
                    targetCal = 300;
                    break;
            }
        }
        // 체중 유지 필요 : BMI 기준 정상체중군(18.5≤BMI≤22.9)이지만 현재 체중≤표준 체중.
        else if (bmiLevel == 1 && weight <= mWeight) {
            switch (sex) {
                case 1:
                    targetCal = 400;
                    break;
                case 2:
                    targetCal = 300;
                    break;
            }
        }
        // 체중 감량 필요 :  BMI 기준 정상체중군(18.5≤BMI≤22.9)이지만 현재체중>표준체중
        else if ((bmiLevel == 1 && weight > mWeight) || bmiLevel > 1) {
            switch (sex) {
                case 1:
                    switch (actqy) {
                        case 1:
                            targetCal = 450;
                            break;
                        case 2:
                            targetCal = 500;
                            break;
                        case 3:
                            targetCal = 600;
                            break;
                    }
                    break;
                case 2:
                    switch (actqy) {
                        case 1:
                            targetCal = 375;
                            break;
                        case 2:
                            targetCal = 400;
                            break;
                        case 3:
                            targetCal = 500;
                            break;
                    }
                    break;
            }
        }

        String retStep ="0";
        String retCalrori;

        if (_step > 0) {
            retStep = "" + _step;
        } else {
            retStep = getStepTargetCalulator(sex, height, weight, targetCal);
        }

        if (_calrori > 0) {
            retCalrori = "" + _calrori;
        } else {
            retCalrori = getCalroriTargetCalulator(sex, height, weight, StringUtil.getIntVal(retStep));
        }

        login.goal_mvm_stepcnt = retStep;
        login.goal_mvm_calory = retCalrori;
        Define.getInstance().setLoginInfo(login);
    }

    /**
     * 목표 걸음수 계산
     */
    public String getStepTargetCalulator(int sex, float height, float weight, int calrori) {

        double stepTarget = 0.0f;
        String rtnValue;
        if (sex == 1) {
            stepTarget = ((((height / 100 / (-0.5625 * 3.5 + 4.2294)) * 3.5) / 2) * 60 / (height / 100 / (-0.5625 * 3.5 + 4.2294))) * calrori / (((3.5 + 0.1 * (height / 100 / (-0.5625 * 3.5 + 4.2294) * 3.5 / 2 * 60)) * weight) / 1000 * 5);
        } else {
            stepTarget = ((((height / 100 / (-0.5133 * 3.5 + 4.1147)) * 3.5) / 2) * 60 / (height / 100 / (-0.5133 * 3.5 + 4.1147))) * calrori / (((3.5 + 0.1 * (height / 100 / (-0.5133 * 3.5 + 4.1147) * 3.5 / 2 * 60)) * weight) / 1000 * 5);
        }
        rtnValue = String.format("%.0f", stepTarget);

        if (height==0 || weight==0){
            return "0";
        }else{
            return rtnValue;
        }
    }


    /**
     * 목표 칼로리 계산
     */
    public String getCalroriTargetCalulator(int sex, float height, float weight, int step) {

        double calroriTarget = 0.0f;
        String rtnValue;
        if (sex == 1) {
            calroriTarget = (step * (((3.5 + 0.1 * (height / 100 / (-0.5625 * 3.5 + 4.2294) * 3.5 / 2 * 60)) * weight) / 1000 * 5)) / (((((height / 100 / (-0.5625 * 3.5 + 4.2294)) * 3.5) / 2) * 60) / (height / 100 / (-0.5625 * 3.5 + 4.2294)));
        } else {
            calroriTarget = (step * (((3.5 + 0.1 * (height / 100 / (-0.5133 * 3.5 + 4.1147) * 3.5 / 2 * 60)) * weight) / 1000 * 5)) / (((((height / 100 / (-0.5133 * 3.5 + 4.1147)) * 3.5) / 2) * 60) / (height / 100 / (-0.5133 * 3.5 + 4.1147)));
        }
        rtnValue = String.format("%.0f", calroriTarget);

        if (height==0 || weight==0){
            return "0";
        }else{
            return rtnValue;
        }
    }

    /**
     * 앱 재시작
     */
    public void restartMainActivity() {
        getActivity().finish();

        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    /**
     * 권한이 정상적으로 설정 되었는지 확인
     *
     * @param grantResults
     * @return
     */
    private boolean isPermissionGransteds(int[] grantResults) {
        for (int isGranted : grantResults) {
            return isGranted == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public interface IPermission {
        void result(boolean isGranted);
    }

    // 화면 회전시 초기화 방지
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
