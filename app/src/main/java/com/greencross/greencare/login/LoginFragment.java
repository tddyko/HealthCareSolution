package com.greencross.greencare.login;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.bluetooth.model.MessageModel;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.component.CFontEditText;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperFoodDetail;
import com.greencross.greencare.database.DBHelperFoodMain;
import com.greencross.greencare.database.DBHelperMessage;
import com.greencross.greencare.main.MainFragment;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_get_hedctdata;
import com.greencross.greencare.network.tr.data.Tr_get_meal_input_data;
import com.greencross.greencare.network.tr.data.Tr_get_meal_input_food_data;
import com.greencross.greencare.network.tr.data.Tr_infra_message;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.DeviceUtil;
import com.greencross.greencare.util.EditTextUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.PackageUtil;
import com.greencross.greencare.util.SharedPref;
import com.greencross.greencare.util.StringUtil;
import com.greencross.greencare.util.ViewUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.greencross.greencare.login.FindPwdFragment.FIND_PWD_EMAIL;

/**
 * Created by MrsWin on 2017-02-16.
 */

public class LoginFragment extends BaseFragment {
    private final String TAG = LoginFragment.class.getSimpleName();

    private CheckBox mSaveIdCheckBox;
    private CheckBox mAutoLoginCheckBox;
    private CFontEditText mPwdEditText;
    private CFontEditText mLoginIdEditText;

    public static Fragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        return view;
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBackBtnVisible(View.INVISIBLE);
        actionBar.showActionBar(false);
        actionBar.setActionBarTitle( getString(R.string.text_login));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 테스트
            mSaveIdCheckBox = (CheckBox) view.findViewById(R.id.login_save_id_checkbox);
            mAutoLoginCheckBox = (CheckBox) view.findViewById(R.id.login_auto_login_checkbox);
            mPwdEditText = (CFontEditText) view.findViewById(R.id.login_pwd_edittext);
            mLoginIdEditText = (CFontEditText) view.findViewById(R.id.login_id_edittext);

            view.findViewById(R.id.login_find_id_textview).setOnClickListener(mOnClickListener);
            view.findViewById(R.id.login_find_pwd_textview).setOnClickListener(mOnClickListener);
            view.findViewById(R.id.login_join_button).setOnClickListener(mOnClickListener);
            view.findViewById(R.id.login_login_button).setOnClickListener(mOnClickListener);

            TextView typeTextView = (TextView)view.findViewById(R.id.login_id_edittext);
            ViewUtil.setTypefaceKelsonSansRegular(getContext(), typeTextView);

            typeTextView = (TextView)view.findViewById(R.id.login_pwd_edittext);
            ViewUtil.setTypefaceKelsonSansRegular(getContext(), typeTextView);

            CheckBox typeCheckBox = (CheckBox)view.findViewById(R.id.login_auto_login_checkbox);
            ViewUtil.setTypefaceKelsonSansRegular(getContext(), typeCheckBox);

            Button typeButton = (Button)view.findViewById(R.id.login_login_button);
            ViewUtil.setTypefaceKelsonSansRegular(getContext(), typeButton);

            typeButton = (Button)view.findViewById(R.id.login_join_button);
            ViewUtil.setTypefaceKelsonSansRegular(getContext(), typeButton);

            Boolean isSavedId = SharedPref.getInstance().getPreferences(SharedPref.IS_SAVED_LOGIN_ID, false);
        if (isSavedId) {
            String savedId = SharedPref.getInstance().getPreferences(SharedPref.SAVED_LOGIN_ID);
            mLoginIdEditText.setText(savedId);
            mSaveIdCheckBox.setChecked(true);
            EditTextUtil.hideKeyboard(getContext(), mLoginIdEditText);
        }

        if (getArguments() != null) {
            String email = getArguments().getString(FIND_PWD_EMAIL);
            if (TextUtils.isEmpty(email) == false) {
                mLoginIdEditText.setText(email);
                EditTextUtil.hideKeyboard(getContext(), mLoginIdEditText);

            }
        }
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (R.id.login_find_id_textview == vId) {
                movePage(FindIdFragment.newInstance());
            } else if (R.id.login_find_pwd_textview == vId) {
                movePage(FindPwdFragment.newInstance());
            } else if (R.id.login_join_button == vId) {
                movePage(JoinStep1Fragment.newInstance());
            } else if (R.id.login_login_button == vId) {
                    doLogin();
            }
        }
    };

    private void doLogin() {
        final String id = mLoginIdEditText.getText().toString();
        if (TextUtils.isEmpty(id)) {
            CDialog.showDlg(getContext(), getString(R.string.id_confirm));
            return;
        }
        final String pwd = mPwdEditText.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            CDialog.showDlg(getContext(), getString(R.string.pw_confirm));
            return;
        }
        showProgress();

        Tr_login.RequestData requestData = new Tr_login.RequestData();
        requestData.mber_id = mLoginIdEditText.getText().toString();
        requestData.mber_pwd = mPwdEditText.getText().toString();
        requestData.phone_model = DeviceUtil.getPhoneModelName();
        requestData.pushk = "";
        requestData.app_ver =  PackageUtil.getVersionInfo(getContext());

        getData(getContext(), Tr_login.class, requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_login) {
                    Tr_login data = (Tr_login)obj;
                    boolean isSuccess = "Y".equals(data.log_yn);
                    if (isSuccess) {
                        String savedId = SharedPref.getInstance().getPreferences(SharedPref.SAVED_LOGIN_ID);
                        Logger.i(TAG, "savedId="+savedId+", id="+id);
                        if (id.equals(savedId) == false) {
                            // 기존 사용자 아이디와 다른경우 내부 sqlite 삭제
                            DBHelper helper = new DBHelper(getContext());
                            helper.deleteAll();
                            SharedPref.getInstance().removeAllPreferences();    // sharedprefrence 초기화

                            // Sqlash 음식 초기데이터 true
                            SharedPref.getInstance().savePreferences(SharedPref.INTRO_FOOD_DB, true);

                            //건강메시지 On/Off
                            SharedPref.getInstance().savePreferences(SharedPref.HEALTH_MESSAGE_CONFIRIM, false);
                        }else{
                            SharedPref.getInstance().savePreferences(SharedPref.HEALTH_MESSAGE_CONFIRIM, true);
                        }

//                        if (mSaveIdCheckBox.isChecked()) {
                            SharedPref.getInstance().savePreferences(SharedPref.SAVED_LOGIN_PWD, pwd);
//                        }




                        SharedPref.getInstance().savePreferences(SharedPref.SAVED_LOGIN_ID, id);
                        SharedPref.getInstance().savePreferences(SharedPref.IS_SAVED_LOGIN_ID, mSaveIdCheckBox.isChecked());
                        SharedPref.getInstance().savePreferences(SharedPref.IS_LOGIN_SUCEESS, true);
                        SharedPref.getInstance().savePreferences(SharedPref.IS_AUTO_LOGIN, mAutoLoginCheckBox.isChecked());

                        data.mber_id = id;

                        if(data.mber_bdwgh_app.equals("") || data.mber_bdwgh_app.isEmpty()){
                            data.mber_bdwgh_app = data.mber_bdwgh;
                        }

                        Define.getInstance().setLoginInfo(data);

                        doFirstData();

                    } else {
                        CDialog.showDlg(getContext(), getString(R.string.login_fail));
                    }
                }
            }
        });
    }

    /**
     * 앱삭제시 저장된 3개월치 데이터 가져오기
     */
    private int m3MonthIdx = 1;
    private void doFirstData() {
        showProgress();
        if (m3MonthIdx <= 6) {
            getFirstData(""+m3MonthIdx, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (m3MonthIdx == 6) {
                        // 데이터가 6개 데이터 가져오기 되면 음식 데이터 가져오기(걸음,혈당,혈압,체중,물)

                        getFoodData(new ApiData.IStep() {
                            @Override
                            public void next(Object obj) {

                                // 건강 메시지 가져오기
                                getHealthMessageData(new ApiData.IStep() {
                                    @Override
                                    public void next(Object obj) {

                                        boolean isConfirmMsg = SharedPref.getInstance().getPreferences(SharedPref.HEALTH_MESSAGE_CONFIRIM, false);
                                        if(isConfirmMsg == false){
                                            DBHelper helper = new DBHelper(getContext());
                                            DBHelperMessage db = helper.getMessageDb();
                                            List<MessageModel> messageList = db.getResultAll(helper);
                                            if(messageList.size() > 0) {
                                                SharedPref.getInstance().savePreferences(SharedPref.HEALTH_MESSAGE, true);
                                            }
                                        }

                                        movePage(MainFragment.newInstance());
                                    }
                                });
                            }
                        });

                    } else {
                        // 3개월 데이터 가져오기 (걸음, 혈당 혈압 체중)
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                m3MonthIdx++;
                                doFirstData();
                            }
                        }, 500);
                    }
                }
            });
        }
    }

    /**
     * 3개월치 데이터 가져오기
     * @param code
     * @param iStep
     */
    private void getFirstData(final String code, final ApiData.IStep iStep) {
        // 한번 저장 완료가 되면 호출 하지 않기 위한 값
        boolean isSaved = SharedPref.getInstance().getPreferences(SharedPref.SAVED_LOGIN_ID+code , false);
        Logger.i(TAG, "getFirstData.code="+code +", isSaved="+isSaved);
        if (isSaved) {
            iStep.next(null);
            return;
        }


        Calendar cal = Calendar.getInstance(Locale.KOREA);
        Date now = new Date();
        cal.setTime(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        Tr_get_hedctdata.RequestData requestData = new Tr_get_hedctdata.RequestData();
        requestData.mber_sn = Define.getInstance().getLoginInfo().mber_sn;

        requestData.get_data_typ = code;
        requestData.end_day = CDateUtil.getToday_yyyy_MM_dd();    // 오늘 날자
        cal.set(Calendar.MONTH, -3);
        requestData.begin_day = sdf.format(cal.getTimeInMillis());   // 20170301


        getData(getContext(), Tr_get_hedctdata.class, requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_get_hedctdata) {
                    DBHelper helper = new DBHelper(getContext());

                    // 1: 운동      2:혈당    3:혈압    4:체중    5:물data (제일 Low data), 6:심박수
                    Tr_get_hedctdata data = (Tr_get_hedctdata)obj;
                    if ("1".equals(data.get_data_typ)) {
                        helper.getStepDb().insert( data, true);
                    } else if ("2".equals(data.get_data_typ)) {
                        helper.getSugarDb().insert(data, true);
                    } else if ("3".equals(data.get_data_typ)) {
                        helper.getPresureDb().insert(helper, data, true);
                    } else if ("4".equals(data.get_data_typ)) {
                        helper.getWeightDb().insert(data, true);
                    } else if ("5".equals(data.get_data_typ)) {
                        helper.getWaterDb().insert(data.data_list, true);
                    }else if ("6".equals(data.get_data_typ)) {
                        helper.getPPGDb().insert(data, true);
                    }
                    SharedPref.getInstance().savePreferences(SharedPref.SAVED_LOGIN_ID+code , true);

                    iStep.next(obj);

                } else {

                }
            }
        });
    }

    private void getFoodData(final ApiData.IStep iStep) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        final String endDate = CDateUtil.getToday_yyyyMMdd();
        Calendar cal =  CDateUtil.getCalendar_yyyyMMdd(endDate);
        cal.set(Calendar.MONTH, -3);
        final String startDate = sdf.format(cal.getTimeInMillis());

        // 식사데이터 가져와서 sqlite 저장하기
        getFirstMealData(startDate, endDate, new ApiData.IStep() {
            @Override
            public void next(Object obj) {

                // 음식 데이터 가져와서 sqlite 저장하기
                getFirstFoodData(startDate, endDate, new ApiData.IStep() {
                    @Override
                    public void next(Object obj) {
                        iStep.next(obj);
                    }
                });
            }
        });
    }


    /**
     * 데이터 가져오기(식사)
     */
    private void getFirstMealData(final String beginDate, final String endDate, final ApiData.IStep iStep) {
        final boolean isSaved = SharedPref.getInstance().getPreferences(SharedPref.IS_SAVED_MEAL_DB, false);

        if (isSaved) {
            iStep.next(isSaved);
            return;
        }

        Tr_get_meal_input_data.RequestData requestData = new Tr_get_meal_input_data.RequestData();

        Tr_login login = Define.getInstance().getLoginInfo();
        requestData.mber_sn = login.mber_sn;
        requestData.begin_day = beginDate;
        requestData.end_day = endDate;

        getData(getContext(), Tr_get_meal_input_data.class, requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_get_meal_input_data) {
                    Tr_get_meal_input_data data = (Tr_get_meal_input_data) obj;

                    DBHelper helper = new DBHelper(getContext());
                    DBHelperFoodMain db = helper.getFoodMainDb();
                    db.insert(data.data_list);

                    SharedPref.getInstance().savePreferences(SharedPref.IS_SAVED_MEAL_DB, true);

                    iStep.next(true);
                } else {
                    iStep.next(false);
                }
            }
        });
    }


    /**
     * 데이터 가져오기 (음식)
     */
    private void getFirstFoodData(String startDate, String endDate, final ApiData.IStep iStep) {
        boolean isSaved = SharedPref.getInstance().getPreferences(SharedPref.IS_SAVED_FOOD_DB, false);
        Logger.i(TAG, "getFirstFoodData, isSaved="+isSaved);
        if (isSaved) {
            iStep.next(isSaved);
            return;
        }

        final Tr_get_meal_input_food_data.RequestData requestData = new Tr_get_meal_input_food_data.RequestData();
        Tr_login login = Define.getInstance().getLoginInfo();

        requestData.mber_sn = login.mber_sn;
        requestData.begin_day = startDate;
        requestData.end_day = endDate;

        getData(getContext(), Tr_get_meal_input_food_data.class, requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_get_meal_input_food_data) {
                    Tr_get_meal_input_food_data data = (Tr_get_meal_input_food_data) obj;

                    DBHelper helper = new DBHelper(getContext());
                    DBHelperFoodDetail db = helper.getFoodDetailDb();
                    db.insert(data.data_list);

                    SharedPref.getInstance().savePreferences(SharedPref.IS_SAVED_FOOD_DB, true);

                    iStep.next(true);
                } else {
                    iStep.next(false);
                }
            }
        });
    }

    /**
     * 건강 메시지 가져와 Sqlite 저장하기
     */
    private int mHealthMsgCnt = 1;
    private void getHealthMessageData(final ApiData.IStep iStep) {

        final boolean isSaved = SharedPref.getInstance().getPreferences(SharedPref.IS_SAVED_HEALTH_MESSAGE_DB, false);

        if (isSaved) {
            iStep.next(isSaved);
            return;
        }

        Tr_infra_message.RequestData reqData = new Tr_infra_message.RequestData();
        Tr_login login = Define.getInstance().getLoginInfo();

        reqData.mber_sn = login.mber_sn;
        reqData.pageNumber = ""+mHealthMsgCnt;

        getData(getContext(), Tr_infra_message.class, reqData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_infra_message) {
                    Tr_infra_message recvData = (Tr_infra_message) obj;
                    int pageNum = StringUtil.getIntVal(recvData.pageNumber);
                    int pageMaxNum = StringUtil.getIntVal(recvData.maxpageNumber);

                    DBHelper helper = new DBHelper(getContext());
                    DBHelperMessage db = helper.getMessageDb();
                    db.insert(recvData.message_list, true);

                    if (pageNum > pageMaxNum) {
                        getHealthMessageData(iStep);
                    } else {
                        SharedPref.getInstance().savePreferences(SharedPref.IS_SAVED_HEALTH_MESSAGE_DB, true);
                        iStep.next(obj);
                    }

                    mHealthMsgCnt++;
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.finishStep();
    }

    @Override
    protected void onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
