package com.greencross.greencare.step;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.DummyActivity;
import com.greencross.greencare.base.IBaseFragment;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.bluetooth.fragment.DeviceManagementFragment;
import com.greencross.greencare.bluetooth.manager.DeviceManager;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperWeight;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.network.tr.data.Tr_mvm_goalqy;
import com.greencross.greencare.util.SharedPref;
import com.greencross.greencare.util.StringUtil;
import com.greencross.greencare.util.ViewUtil;

import java.text.DecimalFormat;

/**
 * Created by insystemscompany on 2017. 2. 28..
 */

public class StepInputFragment extends BaseFragment implements IBaseFragment {

    private EditText mStepEt, mTargetStepEt;
    private Button typeButton;

    private CheckBox mGoogleFitCb;
    private CheckBox mBandCb;

    private CheckBox chkCaledit;
    private CheckBox chkStepedit;

    private View mCheckedEditText;

    private InputMethodManager imm;


    public static Fragment newInstance() {
        StepInputFragment fragment = new StepInputFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_walk_input, container, false);

        mStepEt = (EditText) view.findViewById(R.id.step_edittext);
        mTargetStepEt = (EditText) view.findViewById(R.id.step_target_edittext);

        mGoogleFitCb = (CheckBox) view.findViewById(R.id.checkbox_google);
        mBandCb = (CheckBox) view.findViewById(R.id.checkbox_urban);

        chkCaledit = (CheckBox) view.findViewById(R.id.checkbox_caledit);      // ??????????????? ????????????
        chkStepedit = (CheckBox) view.findViewById(R.id.checkbox_stepedit); // ??????????????? ????????????

        imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);

        mStepEt.setEnabled(false);
        mStepEt.setFocusableInTouchMode(false);
        mTargetStepEt.setEnabled(false);
        mTargetStepEt.setFocusableInTouchMode(false);

        mGoogleFitCb.setChecked(false);
        mBandCb.setChecked(false);

        /** font Typeface ?????? */
        typeButton = (Button)view.findViewById(R.id.walk_input_save_btn);
        ViewUtil.setTypefaceNotoSansKRBold(getContext(), typeButton);
        typeButton.setOnClickListener(mClickListener);

        Tr_login login = Define.getInstance().getLoginInfo();
        if(!login.goal_mvm_calory.toString().isEmpty()){
            mStepEt.setHint(makeStringComma(login.goal_mvm_calory.replace(",","")));
        }

        if(!login.goal_mvm_stepcnt.toString().isEmpty()){
            mTargetStepEt.setHint(makeStringComma(login.goal_mvm_stepcnt.replace(",","")));
        }

        int dataSource = SharedPref.getInstance().getPreferences(SharedPref.STEP_DATA_SOURCE_TYPE, -1);
        if (dataSource != -1) {
            if (Define.STEP_DATA_SOURCE_GOOGLE_FIT == dataSource) {
                // ?????? ???????????? ??????
                mGoogleFitCb.setChecked(true);
                mBandCb.setChecked(false);
            } else if (Define.STEP_DATA_SOURCE_BAND == dataSource) {
                // ????????? ??????
                mGoogleFitCb.setChecked(false);
                mBandCb.setChecked(true);
            }
        }

        mGoogleFitCb.setOnClickListener(mCheckClickListener);
        mBandCb.setOnClickListener(mCheckClickListener);
        chkCaledit.setOnClickListener(mCheckClickListener);
        chkStepedit.setOnClickListener(mCheckClickListener);

        mStepEt.addTextChangedListener(watcher);
        mTargetStepEt.addTextChangedListener(watcher);

        mStepEt.setOnFocusChangeListener(mFocusChangeListener);
        mTargetStepEt.setOnFocusChangeListener(mFocusChangeListener);


        mStepEt.setOnKeyListener( new View.OnKeyListener() {
              public boolean onKey(View v, int keyCode, KeyEvent event) {
                  if(event.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
                      if((StringUtil.getIntVal(mStepEt.getText().toString()) < 150 || StringUtil.getIntVal(mStepEt.getText().toString()) > 1000) && mStepEt.getText().length() > 0){
                          Toast.makeText(getContext(), getContext().getString(R.string.step_input_over), Toast.LENGTH_LONG).show();
                      }
                  }

                  return false;
              }
        });

        mTargetStepEt.setOnKeyListener( new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
                    if((StringUtil.getIntVal(mStepEt.getText().toString()) < 150 || StringUtil.getIntVal(mStepEt.getText().toString()) > 1000) && mStepEt.getText().length() > 0){
                        Toast.makeText(getContext(), getContext().getString(R.string.step_input_over), Toast.LENGTH_LONG).show();
                    }
                }

                return false;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle( getString(R.string.text_target_modify));
    }

    /*
     **????????? ??????
     */
    public String makeStringComma(String str) {
        if (str.length() == 0)
            return "";
        long value = Long.parseLong(str);
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(value);
    }

    View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            mCheckedEditText = v;
            if (v == mStepEt && hasFocus == false) {
                if((StringUtil.getIntVal(mStepEt.getText().toString()) < 150 || StringUtil.getIntVal(mStepEt.getText().toString()) > 1000) && mStepEt.getText().length() > 0){
                    Toast.makeText(getContext(), getContext().getString(R.string.step_input_over), Toast.LENGTH_LONG).show();
                }
            } else if(v == mTargetStepEt && hasFocus == false) {
                if((StringUtil.getIntVal(mStepEt.getText().toString()) < 150 || StringUtil.getIntVal(mStepEt.getText().toString()) > 1000) && mStepEt.getText().length() > 0){
                    Toast.makeText(getContext(), getContext().getString(R.string.step_input_over), Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.walk_input_save_btn) {
                final String mStep = mStepEt.getText().toString();
                if (TextUtils.isEmpty(mStep) || StringUtil.getIntVal(mStep) <= 0) {
                    CDialog.showDlg(getContext(), "?????? ?????? ???????????? ????????? ????????? ?????????.");
                    return;
                }
                if (StringUtil.getIntVal(mStepEt.getText().toString()) < 150 || StringUtil.getIntVal(mStepEt.getText().toString()) > 1000) {
                    CDialog.showDlg(getContext(), getContext().getString(R.string.step_input_over));
                    return;
                }
                final String mTargetStep = mTargetStepEt.getText().toString();
                if (TextUtils.isEmpty(mTargetStep) || (StringUtil.getIntVal(mTargetStep) <= 0 || StringUtil.getIntVal(mTargetStep) > 500000)) {
                    CDialog.showDlg(getContext(), "?????? ?????? ???????????? ????????? ????????? ?????????.");
                    return;
                }

                CDialog.showDlg(getContext(), "?????? ?????? ????????? ??? ?????? ?????? ?????? ?????? ???????????????????", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doInputStep(mStep, mTargetStep);
                    }
                }, null);
            }
        }
    };


    View.OnClickListener mCheckClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final CheckBox chkBtn = (CheckBox)v;

            if (chkBtn == chkCaledit ) {
                if (chkCaledit.isChecked() == true) {
                    chkCaledit.setChecked(true);
                    chkStepedit.setChecked(false);

                    mTargetStepEt.setEnabled(false);
                    mTargetStepEt.setFocusable(false);
                    mTargetStepEt.setFocusableInTouchMode(false);
                    mTargetStepEt.requestFocus();
                    imm.hideSoftInputFromInputMethod(mTargetStepEt.getWindowToken(), 0);

                    mStepEt.setEnabled(true);
                    mStepEt.setFocusable(true);
                    mStepEt.setFocusableInTouchMode(true);
                    mStepEt.requestFocus();
                    imm.showSoftInput(mStepEt, InputMethodManager.SHOW_IMPLICIT);

                }else {
                    chkCaledit.setChecked(false);

                    mStepEt.setEnabled(false);
                    mStepEt.setFocusable(false);
                    mStepEt.setFocusableInTouchMode(false);
                    mStepEt.requestFocus();
                    imm.hideSoftInputFromInputMethod(mStepEt.getWindowToken(), 0);
                }
            }else if (chkBtn == chkStepedit){
                if (chkStepedit.isChecked() == true) {
                    chkStepedit.setChecked(true);
                    chkCaledit.setChecked(false);

                    mStepEt.setEnabled(false);
                    mStepEt.setFocusable(false);
                    mStepEt.setFocusableInTouchMode(false);
                    mStepEt.requestFocus();
                    imm.hideSoftInputFromInputMethod(mStepEt.getWindowToken(), 0);

                    mTargetStepEt.setEnabled(true);
                    mTargetStepEt.setFocusable(true);
                    mTargetStepEt.setFocusableInTouchMode(true);
                    mTargetStepEt.requestFocus();
                    imm.showSoftInput(mTargetStepEt, InputMethodManager.SHOW_IMPLICIT);
                }else {
                    chkStepedit.setChecked(false);

                    mTargetStepEt.setEnabled(false);
                    mTargetStepEt.setFocusable(false);
                    mTargetStepEt.setFocusableInTouchMode(false);
                    mTargetStepEt.requestFocus();
                    imm.hideSoftInputFromInputMethod(mTargetStepEt.getWindowToken(), 0);
                }
            }
            else if (chkBtn == mGoogleFitCb || chkBtn== mBandCb) {

                if (chkBtn.isChecked() == false) {
                    chkBtn.setChecked(true);
                    return;
                }

                String message = "";
                if (chkBtn == mGoogleFitCb) {
                    mBandCb.setChecked(chkBtn.isChecked() ? false : true);
                    message = getString(R.string.text_googlefit) + "??? ???????????? ?????????????????? ?????????????????????????";
                } else if (chkBtn == mBandCb) {
                    mGoogleFitCb.setChecked(chkBtn.isChecked() ? false : true);
                    message = getString(R.string.text_croise) + "??? ???????????? ?????????????????? ?????????????????????????";
                }

                CDialog.showDlg(getContext(), message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int code = mGoogleFitCb.isChecked() ?
                                Define.STEP_DATA_SOURCE_GOOGLE_FIT
                                : Define.STEP_DATA_SOURCE_BAND;

                        if (Define.STEP_DATA_SOURCE_BAND == code) {
                            if (DeviceManager.isRegDevice(getContext(), DeviceManager.FLAG_BLE_DEVICE_BAND)) {
                                SharedPref.getInstance().savePreferences(SharedPref.STEP_DATA_SOURCE_TYPE, Define.STEP_DATA_SOURCE_BAND);
                                onBackPressed();
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(DeviceManagementFragment.IS_STEP_DEVICE_REGIST, true);
                                DummyActivity.startActivityForResult(StepInputFragment.this, Define.STEP_DATA_SOURCE_BAND, DeviceManagementFragment.class, bundle);
                            }
                        } else {
                            SharedPref.getInstance().savePreferences(SharedPref.STEP_DATA_SOURCE_TYPE, code);
                            onBackPressed();
                        }
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (chkBtn == mGoogleFitCb) {
                            mGoogleFitCb.setChecked(false);
                            mBandCb.setChecked(true);
                        } else if (chkBtn == mBandCb) {
                            mGoogleFitCb.setChecked(true);
                            mBandCb.setChecked(false);
                        }
                    }
                });

            }
        }
    };

    private void doInputStep(String mStep , String mTargetStep) {

        Tr_login login = Define.getInstance().getLoginInfo();

        Tr_mvm_goalqy.RequestData requestData = new Tr_mvm_goalqy.RequestData();
        requestData.mber_sn = login.mber_sn;
        requestData.goal_mvm_calory = mStep;
        requestData.goal_mvm_stepcnt = mTargetStep;

        getData(getContext(), Tr_mvm_goalqy.class, requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_mvm_goalqy) {
                    Tr_mvm_goalqy data = (Tr_mvm_goalqy)obj;
                    if ("Y".equals(data.reg_yn)) {
                        CDialog.showDlg(getContext(), "????????? ?????? ????????????.", new CDialog.DismissListener() {
                            @Override
                            public void onDissmiss() {
                                Tr_login login = Define.getInstance().getLoginInfo();
                                login.goal_mvm_calory = mStepEt.getText().toString();
                                login.goal_mvm_stepcnt = mTargetStepEt.getText().toString();
                                Define.getInstance().setLoginInfo(login);

                                mStepEt.setText("");
                                mTargetStepEt.setText("");
                                onBackPressed();
                            }
                        });
                    } else {
                        CDialog.showDlg(getContext(), "????????? ?????? ????????????.");
                    }

                }
            }
        });
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(s.toString().length() > 0){

                Tr_login login      = Define.getInstance().getLoginInfo();
                int sex             = StringUtil.getIntVal(login.mber_sex);
                DBHelper helper = new DBHelper(getContext());
                DBHelperWeight weightDb = helper.getWeightDb();
                DBHelperWeight.WeightStaticData bottomData = weightDb.getResultStatic();
                float weight        = 0.f;
                if(!bottomData.getWeight().isEmpty()) {
                    weight          = StringUtil.getFloatVal(login.mber_bdwgh_app);
                }else{
                    weight          = StringUtil.getFloatVal(login.mber_bdwgh);
                }
                float height        = StringUtil.getFloatVal(login.mber_height);
                int sInt            = Integer.parseInt(s.toString());

                if(chkCaledit.isChecked() == true){
                    if(mStepEt.getText().toString().length() > 0){
                        mTargetStepEt.removeTextChangedListener(this);
                        mTargetStepEt.setText(""+getStepTargetCalulator(sex, height, weight, sInt));
                        mTargetStepEt.addTextChangedListener(this);
                    }

                }else{
                    if(mTargetStepEt.getText().toString().length() > 0) {
                        mStepEt.removeTextChangedListener(this);
                        mStepEt.setText("" + getCalroriTargetCalulator(sex, height, weight, sInt));
                        mStepEt.addTextChangedListener(this);
                    }
                }
            }else{
                if(chkCaledit.isChecked() == true){
                    mTargetStepEt.removeTextChangedListener(this);
                    mTargetStepEt.setText("");
                    mTargetStepEt.addTextChangedListener(this);
                }else{
                    mStepEt.removeTextChangedListener(this);
                    mStepEt.setText("");
                    mStepEt.addTextChangedListener(this);
                }
            }

            if (isValidStep() && isValidTargetStep()) {
                typeButton.setEnabled(true);
            } else {
                typeButton.setEnabled(false);
            }
        }
    };

    private boolean isValidStep() {
        String text = mStepEt.getText().toString();
        return TextUtils.isEmpty(text) == false;
    }

    private boolean isValidTargetStep() {
        String text = mTargetStepEt.getText().toString();
        return TextUtils.isEmpty(text) == false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Define.STEP_DATA_SOURCE_BAND) {
            int typeSource = SharedPref.getInstance().getPreferences(SharedPref.STEP_DATA_SOURCE_TYPE, -1);
            if (Define.STEP_DATA_SOURCE_BAND == typeSource) {
                // ?????? ?????? ????????? ??????
                mGoogleFitCb.setChecked(false);
                mBandCb.setChecked(true);
                restartMainActivity();
            } else {
                mGoogleFitCb.setChecked(true);
                mBandCb.setChecked(false);
            }
        }
    }
}