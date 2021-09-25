package com.greencross.greencare;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.greencross.greencare.alram.BootReceiver;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperFoodCalorie;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.CConnAsyncTask;
import com.greencross.greencare.network.tr.data.Tr_get_infomation;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.DeviceUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.NetworkUtil;
import com.greencross.greencare.util.PackageUtil;
import com.greencross.greencare.util.SharedPref;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class SplashActivity extends AppCompatActivity {
    private final String TAG = SplashActivity.class.getSimpleName();

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 33;

    DBHelper _helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setTheme(android.R.style.Theme_NoTitleBar);
        Logger.initLogger(this);
        SharedPref.getInstance().initContext(SplashActivity.this);

        // 날씨 정보 조회 시간 초기 화
        Define.getInstance().setWeatherRequestedTime(-1);

        _helper = new DBHelper(getApplicationContext());
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (NetworkUtil.getConnectivityStatus(this) == false) {
            CDialog.showDlg(this, getString(R.string.text_network_error), new CDialog.DismissListener() {
                @Override
                public void onDissmiss() {
                    finish();
                }
            });
        } else {
            Handler hd = new Handler();
            hd.postDelayed(new Splashhandler(), 100); // 1초 후에 hd Handler 실행
        }
    }

    public void getData(final Class<?> cls, final Object obj, final ApiData.IStep step) {

        if (NetworkUtil.getConnectivityStatus(SplashActivity.this) == false) {
            CDialog.showDlg(this, getString(R.string.text_network_error), new CDialog.DismissListener() {
                @Override
                public void onDissmiss() {
                    finish();
                }
            });
            return;
        }

        CConnAsyncTask.CConnectorListener queryListener = new CConnAsyncTask.CConnectorListener() {
            @Override
            public Object run() {
                ApiData data = new ApiData();
                return data.getData(getApplicationContext(), cls, obj);
            }

            @Override
            public void view(CConnAsyncTask.CQueryResult result) {
                Logger.i(TAG, "result.result=" + result.result + ", result.data=" + result.data);
                if (result.result == CConnAsyncTask.CQueryResult.SUCCESS && result.data != null) {
                    if (step != null) {
                        step.next(result.data);
                    }

                } else {
                    Logger.e(TAG, getString(R.string.text_network_data_rec_fail));
                    Logger.e(TAG, "CConnAsyncTask error=" + result.errorStr);
                    try {
                        CDialog.showDlg(getApplicationContext(), getString(R.string.text_network_data_rec_fail), new CDialog.DismissListener() {
                            @Override
                            public void onDissmiss() {
                                finish();
                            }
                        });
                    } catch (Exception e) {

                        try {
                            CDialog.showDlg(SplashActivity.this, getString(R.string.text_network_data_rec_fail), new CDialog.DismissListener() {
                                @Override
                                public void onDissmiss() {
                                    finish();
                                }
                            });
                        } catch (Exception e2) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                            builder.setMessage(getString(R.string.text_network_data_rec_fail));
                            builder.setPositiveButton(getString(R.string.text_confirm), null);
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                }
                            });
                            AlertDialog dlg = builder.create();
                            dlg.show();
                        }
                    }
                }
            }
        };

        CConnAsyncTask asyncTask = new CConnAsyncTask();
        asyncTask.execute(queryListener);
    }

    /**
     * 권한 설정
     * @return
     */
    private String[] getGrandtedPermissions() {
        List<String> permissions = new ArrayList<>();

        int isGrandted = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (isGrandted != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        isGrandted = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (isGrandted != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        isGrandted = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (isGrandted != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        String[] permissionArr = new String[permissions.size()];
        permissionArr = permissions.toArray(permissionArr);
        return permissionArr;

    }

    private void reqPermissions() {
        final String[] permissions = getGrandtedPermissions();
        if (permissions.length > 0) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            ActivityCompat.requestPermissions(SplashActivity.this
                    , permissions
                    , REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            loadFoodData();
        }
    }

    /**
     * 식품 칼로리 Sqlite에 저장
     */
    private void loadFoodData() {

        SQLiteDatabase db = _helper.getReadableDatabase();
        int version = db.getVersion();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPref pref = SharedPref.getInstance();
                boolean isSaveFoodDb = pref.getPreferences(SharedPref.INTRO_FOOD_DB, false);
                //음식디비가 저장되어 있지 않거나, 최신버전이 아닌앱은 새로 받음.
                if (isSaveFoodDb == false || _helper.isNewFood) {
                    new InsertFoodCalorieDb().execute();
                } else {
                    getInformation();
                }
            }
        }, 500);
    }

    /**
     * GCM 등록
     */
    class InsertFoodCalorieDb extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog searchDialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchDialog = ProgressDialog.show(SplashActivity.this, null, getString(R.string.text_network_init_data_conf), true, false, null);
            searchDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isSuccess = true;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(getAssets().open("food_db.csv")));

                DBHelper helper = new DBHelper(getApplicationContext());
                DBHelperFoodCalorie db = helper.getFoodCalorieDb();
                db.initFoodCalorieDb(reader);
            } catch (Exception e) {
                e.printStackTrace();
                isSuccess = false;
            }
            return isSuccess;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);

            if (searchDialog != null)
                searchDialog.dismiss();

            getInformation();
        }
    }


    private class Splashhandler implements Runnable {
        public void run() {
            reqPermissions();
        }
    }

    private void getInformation() {
        Logger.i(TAG, "getInformation()");
        Tr_get_infomation.RequestData reqData = new Tr_get_infomation.RequestData();
        getData(Tr_get_infomation.class, reqData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {

                if (obj instanceof Tr_get_infomation) {
                    Tr_get_infomation data = (Tr_get_infomation) obj;

                    Define.getInstance().setInformation(data);
                    doAutoLogin();
                } else {

                    CDialog dlg = new CDialog(SplashActivity.this);
                    dlg.setMessage(getString(R.string.text_network_error));
                    dlg.setOkButton(getString(R.string.text_confirm), null);
                    dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                }
            }
        });
    }

    /**
     * 자동로그인 처리
     * 자동 로그인이 아니면 바로 메인화면으로
     */
    private void doAutoLogin() {

//        boolean isAutoLogin = SharedPref.getInstance().getPreferences(SharedPref.IS_AUTO_LOGIN, false);
//        Tr_login loginData = Define.getInstance().getLoginInfo();
//        if (isAutoLogin && loginData == null) {
            Tr_login.RequestData requestData = new Tr_login.RequestData();
            requestData.mber_id = SharedPref.getInstance().getPreferences(SharedPref.SAVED_LOGIN_ID);
            requestData.mber_pwd = SharedPref.getInstance().getPreferences(SharedPref.SAVED_LOGIN_PWD);
            if (TextUtils.isEmpty(requestData.mber_id)) {
                SharedPref.getInstance().savePreferences(SharedPref.IS_AUTO_LOGIN, false);
                startMainActivity();
                return;
            }
            requestData.phone_model = DeviceUtil.getPhoneModelName();
            requestData.pushk = "";
            requestData.app_ver = PackageUtil.getVersionInfo(SplashActivity.this);

            getData(Tr_login.class, requestData, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (obj instanceof Tr_login) {
                        Tr_login data = (Tr_login) obj;
                        if ("Y".equals(data.log_yn)) {
                            Define.getInstance().setLoginInfo(data);
                            startMainActivity();
                        } else {
                            CDialog.showDlg(SplashActivity.this, getString(R.string.login_fail), new CDialog.DismissListener() {
                                @Override
                                public void onDissmiss() {
                                    SharedPref.getInstance().savePreferences(SharedPref.IS_LOGIN_SUCEESS, false);
                                    SharedPref.getInstance().savePreferences(SharedPref.IS_AUTO_LOGIN, false);
                                    startMainActivity();
                                }
                            });
                        }
                    }
                }
            });

//        } else {
//            startMainActivity();
//        }
    }

    /**
     * 메인화면 진입
     */
    private void startMainActivity() {
        SharedPref.getInstance().savePreferences(SharedPref.INTRO_FOOD_DB, true);
        boolean login = SharedPref.getInstance().getPreferences(SharedPref.IS_LOGIN_SUCEESS, false);
        if(!login) {
            BootAlarms();
        }
        startActivity(new Intent(getApplication(), MainActivity.class)); // 로딩이 끝난후 이동할 Activity
        SplashActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
                loadFoodData();
            } else if (isPermissionGransteds(grantResults)) {
                // Permission was granted.
                loadFoodData();

            } else {
                // Permission denied.

                CDialog.showDlg(SplashActivity.this, getString(R.string.permission_setting_access), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reqPermissions();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        }
    }

    /**
     * 권한이 정상적으로 설정 되었는지 확인
     * @param grantResults
     * @return
     */
    private boolean isPermissionGransteds(int[] grantResults) {
        for (int isGranted : grantResults) {
            return isGranted == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }


    /*
    * 혈당 건강메세지 알람
    *
    **/
    private void BootAlarms(){
        int A_DAY = 1000 * 60 * 60 * 24;
        PendingIntent[] sender = new PendingIntent[2];
        for (int i = 0; i < 2; i++) {
            Intent BootIntent = new Intent(getApplication(), BootReceiver.class);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            if (i == 0) {
                calendar.set(calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DATE), 0, 0, 0);
            } else if (i == 1) {
                calendar.set(calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DATE), 9, 0, 0);
            }

            BootIntent.setType(Integer.toString(i));

            sender[i] = PendingIntent.getBroadcast(getApplication(), 0, BootIntent, 0);
            AlarmManager mManager;
            mManager = (AlarmManager) getApplication().getBaseContext().getSystemService(Context.ALARM_SERVICE);

            mManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), A_DAY, sender[i]);
        }
    }
}