package com.greencross.greencare.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.network.tr.data.Tr_mber_edit_add_exe;
import com.greencross.greencare.network.tr.data.Tr_mber_reg;
import com.greencross.greencare.network.tr.data.Tr_mber_user_call;
import com.greencross.greencare.util.DeviceUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.PackageUtil;
import com.greencross.greencare.util.ViewUtil;

/**
 * Created by MrsWin on 2017-02-16.
 */

public class JoinStep3Fragment extends BaseFragment {
    private final String TAG = JoinStep3Fragment.class.getSimpleName();

    private boolean mIsInfoEdit = false;
    private CheckBox mBeforeTypeCheckedChk;
    private CheckBox[] mTypeChkArr;
    private CheckBox mTypeChk1;
    private CheckBox mTypeChk2;
    private CheckBox mTypeChk3;

    private CheckBox mVirusType1;
    private CheckBox mVirusType2;
    private CheckBox mVirusType3;
    private CheckBox mVirusType4;
    private CheckBox mVirusType5;

    private RadioGroup mTakingRadioGrp;
    private RadioGroup mSmokeRadioGrp;

    private RadioButton mMedicenYesRadioBtn;
    private RadioButton mMedicenNoRadioBtn;

    private RadioButton mSmokeYesRadioBtn;
    private RadioButton mSmokeNoRadioBtn;

    private LinearLayout mMedicenLayout;
    private TextView mErrorTv;
    private Button complete_btn;

    private JoinDataVo dataVo;

    public static Fragment newInstance() {
        JoinStep3Fragment fragment = new JoinStep3Fragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_step3_fragment, container, false);

        return view;
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle("???????????? ??????(3/3)");       // ????????? ?????????
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataVo = (JoinDataVo) getArguments().getBinder(JoinStep1Fragment.JOIN_DATA);
        if (dataVo != null) {

        } else {
            dataVo = new JoinDataVo();
        }

        /** font Typeface ?????? */
        complete_btn = (Button) view.findViewById(R.id.complete_btn);
        ViewUtil.setTypefaceNotoSansKRBold(getContext(), complete_btn);

        mTypeChk1 = (CheckBox) view.findViewById(R.id.join_step3_active_type1_checkbox);
        mTypeChk2 = (CheckBox) view.findViewById(R.id.join_step3_active_type2_checkbox);
        mTypeChk3 = (CheckBox) view.findViewById(R.id.join_step3_active_type3_checkbox);
        mTypeChkArr = new CheckBox[]{mTypeChk1, mTypeChk2, mTypeChk3};
        mBeforeTypeCheckedChk = mTypeChk1;
        mTypeChk1.setOnCheckedChangeListener(mCheckedChangeListener);
        mTypeChk2.setOnCheckedChangeListener(mCheckedChangeListener);
        mTypeChk3.setOnCheckedChangeListener(mCheckedChangeListener);

        mVirusType1 = (CheckBox) view.findViewById(R.id.join_step3_virus_type1_checkbox);   // ?????????
        mVirusType2 = (CheckBox) view.findViewById(R.id.join_step3_virus_type2_checkbox);   // ??????
        mVirusType4 = (CheckBox) view.findViewById(R.id.join_step3_virus_type4_checkbox);   // ?????????
        mVirusType3 = (CheckBox) view.findViewById(R.id.join_step3_virus_type3_checkbox);   // ??????
        mVirusType5 = (CheckBox) view.findViewById(R.id.join_step3_virus_type5_checkbox);   // ??????

        mTakingRadioGrp = (RadioGroup) view.findViewById(R.id.join_step3_taking_radio_group);
        mSmokeRadioGrp = (RadioGroup) view.findViewById(R.id.join_step3_smoking_radio_group);

        mMedicenLayout = (LinearLayout)view.findViewById(R.id.join_step3_medicen_layout);

        mErrorTv = (TextView) view.findViewById(R.id.join_step3_error_textview);

        mVirusType1.setOnCheckedChangeListener(mMedicenChangeListener);
        mVirusType2.setOnCheckedChangeListener(mMedicenChangeListener);
        mVirusType3.setOnCheckedChangeListener(mMedicenChangeListener);
        mVirusType4.setOnCheckedChangeListener(mMedicenChangeListener);
        mVirusType5.setOnCheckedChangeListener(mDiseaseChangeListener);

        mMedicenYesRadioBtn = (RadioButton) view.findViewById(R.id.join_step3_taking_yes_radio_button);
        mMedicenNoRadioBtn = (RadioButton) view.findViewById(R.id.join_step3_taking_no_radio_button);

        mSmokeYesRadioBtn = (RadioButton) view.findViewById(R.id.join_step3_smoking_yes_radio_button);
        mSmokeNoRadioBtn = (RadioButton) view.findViewById(R.id.join_step3_smoking_no_radio_button);

        mMedicenYesRadioBtn.setOnCheckedChangeListener(validationListener);
        mMedicenNoRadioBtn.setOnCheckedChangeListener(validationListener);
        mSmokeYesRadioBtn.setOnCheckedChangeListener(validationListener);
        mSmokeNoRadioBtn.setOnCheckedChangeListener(validationListener);

        view.findViewById(R.id.join_step3_active_type1_layout).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.join_step3_active_type2_layout).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.join_step3_active_type3_layout).setOnClickListener(mOnClickListener);

        view.findViewById(R.id.complete_btn).setOnClickListener(mOnClickListener);

        // ????????? ???????????? ????????? ?????????????????? ???????????? ??????
        if (getArguments() != null) {
            String bundleTitle = getArguments().getString(CommonActionBar.ACTION_BAR_TITLE);
            if (TextUtils.isEmpty(bundleTitle) == false) {

                mIsInfoEdit = true;
                complete_btn.setText(R.string.join_step3_ecomplete);
                loadPersonalInfo();
            }
        }
    }

    private void BtnEnableCheck() {

        boolean vali = true;
        if (!((mVirusType1.isChecked() || mVirusType2.isChecked() || mVirusType4.isChecked())
                && (mMedicenYesRadioBtn.isChecked() || mMedicenNoRadioBtn.isChecked())
                || (mVirusType5.isChecked() || mVirusType3.isChecked()))
                ) {
            vali = false;
        }
        if (!mSmokeYesRadioBtn.isChecked() && !mSmokeNoRadioBtn.isChecked()){
            vali = false;
        }
        if (mMedicenLayout.getVisibility()!=View.GONE){
            if (!mMedicenYesRadioBtn.isChecked() && !mMedicenNoRadioBtn.isChecked()) {
                vali = false;
            }
        }

        if (vali) {
            complete_btn.setEnabled(true);
        } else {
            complete_btn.setEnabled(false);
        }
    }


    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (R.id.complete_btn == vId) {
                if (validCheck()) {
                    if (!mIsInfoEdit) {

                        Tr_mber_reg.RequestData requestData = new Tr_mber_reg.RequestData();
                        requestData.mber_id = dataVo.getId(); // 		?????????
                        requestData.mber_pwd = dataVo.getPwd(); // 		????????????
                        requestData.mber_hp = dataVo.getPhoneNum(); // 		???????????????
                        requestData.mber_nm = dataVo.getName(); // 		??????
                        requestData.mber_lifyea = dataVo.getBirth(); // 		??????(19881203)
                        requestData.mber_height = dataVo.getHeight(); // 		???
                        requestData.mber_bdwgh = dataVo.getWeight(); // 		?????????
                        requestData.mber_sex = dataVo.getSex();
                        requestData.mber_bdwgh_goal = dataVo.getTargetWeight(); // 		????????????
                        requestData.pushk = "ET"; // 		??????(??????)
                        requestData.app_ver = PackageUtil.getVersionInfo(getContext()); // 		?????????
                        requestData.phone_model = DeviceUtil.getPhoneModelName(); // 		????????? ?????????
                        requestData.mber_actqy = getActiveType(); // 		?????????  1 (?????? ????????????) 2,(??????) 3(?????????)
                        requestData.disease_nm = getVirusTypes(); // 	????????????, 1,2,3,	??????
                        requestData.medicine_yn = getMedicenType(); // 		????????? ??????
                        requestData.smkng_yn = getSmokeType(); // 		????????????
                        requestData.mber_zone = dataVo.getCity(); // 		??????

                        Logger.i(TAG, "requestData.mber_id=" + requestData.mber_id);// = dataVo.getId(); // 		?????????
                        Logger.i(TAG, "requestData.mber_PWD=" + requestData.mber_pwd);//  = dataVo.getPwd(); // 		????????????
                        Logger.i(TAG, "requestData.mber_sex=" + requestData.mber_sex);//
                        Logger.i(TAG, "requestData.mber_hp=" + requestData.mber_hp);//  = dataVo.getPhoneNum(); // 		???????????????
                        Logger.i(TAG, "requestData.mber_nm=" + requestData.mber_nm);// = dataVo.getName(); // 		??????
                        Logger.i(TAG, "requestData.mber_lifyea=" + requestData.mber_lifyea);//  = dataVo.getBirth(); // 		??????(19881203)
                        Logger.i(TAG, "requestData.mber_height=" + requestData.mber_height);//  = dataVo.getHeight(); // 		???
                        Logger.i(TAG, "requestData.mber_bdwgh=" + requestData.mber_bdwgh);//  = dataVo.getWeight(); // 		?????????
                        Logger.i(TAG, "requestData.mber_bdwgh_goal=" + requestData.mber_bdwgh_goal);// = dataVo.getTargetWeight(); // 		????????????
                        Logger.i(TAG, "requestData.pushk=" + requestData.pushk); // 		??????(??????)
                        Logger.i(TAG, "requestData.app_ver=" + requestData.app_ver);// = PackageUtil.getVersionInfo(getContext()); // 		?????????
                        Logger.i(TAG, "requestData.phone_model=" + requestData.phone_model);// = DeviceUtil.getPhoneModelName(); // 		????????? ?????????
                        Logger.i(TAG, "requestData.mber_actqy=" + requestData.mber_actqy);// = getActiveType(); // 		?????????  1 (?????? ????????????) 2,(??????) 3(?????????)
                        Logger.i(TAG, "requestData.disease_nm=" + requestData.disease_nm);// = getVirusTypes(); // 	????????????, 1,2,3,	??????
                        Logger.i(TAG, "requestData.medicine_yn=" + requestData.medicine_yn);// = dataVo.getMedicine(); // 		????????? ??????
                        Logger.i(TAG, "requestData.smkng_yn=" + requestData.smkng_yn);//= dataVo.getSmoke(); // 		????????????
                        Logger.i(TAG, "requestData.smkng_yn=" + requestData.mber_zone);


                        getData(getContext(), Tr_mber_reg.class, requestData, new ApiData.IStep() {
                            @Override
                            public void next(Object obj) {
                                if (obj instanceof Tr_mber_reg) {
                                    Tr_mber_reg data = (Tr_mber_reg) obj;
                                    if ("Y".equals(data.reg_yn)) {
                                        movePage(LoginFragment.newInstance());
                                        Toast.makeText(getContext(), getString(R.string.message_finish_member), Toast.LENGTH_SHORT).show();
                                    } else {
                                        CDialog.showDlg(getContext(), "????????? ?????? ???????????????.");
                                    }
                                }
                            }
                        });
                    }else {
                        Tr_login info = Define.getInstance().getLoginInfo();
                        // ????????????
                        Tr_mber_edit_add_exe.RequestData requestData = new Tr_mber_edit_add_exe.RequestData();
                        requestData.mber_sn =info.mber_sn;
                        requestData.mber_sex =info.mber_sex;
                        requestData.mber_sn =info.mber_sn;
                        requestData.mber_lifyea =info.mber_lifyea;
                        requestData.mber_height =info.mber_height;
                        requestData.mber_bdwgh =info.mber_bdwgh;
                        requestData.mber_bdwgh_goal =info.mber_bdwgh_goal;

                        requestData.mber_actqy =getActiveType();
                        requestData.disease_nm =getVirusTypes();
                        requestData.medicine_yn =getMedicenType();
                        requestData.smkng_yn =getSmokeType();

                                                                                                           getData(getContext(), Tr_mber_edit_add_exe.class, requestData, new ApiData.IStep() {
                            @Override
                            public void next(Object obj) {
                                if (obj instanceof Tr_mber_edit_add_exe) {
                                    Tr_mber_edit_add_exe data = (Tr_mber_edit_add_exe) obj;
                                    if ("Y".equals(data.reg_yn)) {
                                        Tr_login login = Define.getInstance().getLoginInfo();
                                        login.mber_actqy =  getActiveType();
                                        login.disease_nm =  getVirusTypes();
                                        login.smkng_yn =  getSmokeType();
                                        Define.getInstance().setLoginInfo(login);
                                        CDialog.showDlg(getContext(), "????????? ?????? ???????????????.", new CDialog.DismissListener() {
                                            @Override
                                            public void onDissmiss() {
                                                onBackPressed();
                                            }
                                        });

                                    } else {
                                        CDialog.showDlg(getContext(), "????????? ?????? ???????????????.");
                                    }
                                }
                            }
                        });
                    }
                }
                BtnEnableCheck();
            } else if (R.id.join_step3_active_type1_layout == vId) {
                mTypeChk1.setChecked(mTypeChk1.isChecked() ? false : true);
            } else if (R.id.join_step3_active_type2_layout == vId) {
                mTypeChk2.setChecked(mTypeChk2.isChecked() ? false : true);
            } else if (R.id.join_step3_active_type3_layout == vId) {
                mTypeChk3.setChecked(mTypeChk3.isChecked() ? false : true);
            }
        }
    };

    private boolean validCheck() {
        boolean isValid = true;
        mErrorTv.setVisibility(View.INVISIBLE);

        if (validType1Check() == false) {
            isValid = false;
            Logger.e(TAG, "validType1Check");
        }

        if (validDiseaseCheck() == false) {
            isValid = false;
            Logger.e(TAG, "validDiseaseCheck");
        }

        return isValid;
    }

    /**
     * ?????? ??????
     * @return
     */
    private boolean validType1Check() {
        if (mBeforeTypeCheckedChk != null) {
            return mBeforeTypeCheckedChk.isChecked();
        }
        return false;
    }

    /**
     * ???????????? ?????? ??????
     * @return
     */
    private boolean validDiseaseCheck() {
        mErrorTv.setVisibility(View.INVISIBLE);
        if (mVirusType5.isChecked()) {
            return true;
        } else if (mVirusType1.isChecked() || mVirusType4.isChecked() || mVirusType2.isChecked()) {
            // ?????????, ????????????, ?????? ?????????
           if (mTakingRadioGrp.getCheckedRadioButtonId() == -1) {
               if (mErrorTv.getVisibility() == View.INVISIBLE) {
                   mErrorTv.setVisibility(View.VISIBLE);
                   mErrorTv.setText(R.string.join_step3_error_validation2);
               }
               return false;
           } else {
               return true;
           }
        } else if (mVirusType5.isChecked() || mVirusType3.isChecked()) {
            return true;
        }

        mErrorTv.setVisibility(View.VISIBLE);
        mErrorTv.setText(R.string.join_step3_error_validation1);
        return false;
    }


    /**
     * ?????? ?????? ??????
     */
    CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (mBeforeTypeCheckedChk.isChecked() && (mBeforeTypeCheckedChk == buttonView) == false)
                    mBeforeTypeCheckedChk.setChecked(false);

                mBeforeTypeCheckedChk = (CheckBox) buttonView;
            }
        }
    };

    private String getActiveType() {
        String result = "1";
        if (mTypeChk1.isChecked()) {
            result = "1";
        } else if (mTypeChk2.isChecked()) {
            result = "2";
        } else if (mTypeChk3.isChecked()) {
            result = "3";
        }
        return result;
    }

    /**
     * ?????? ??????
     * 1.?????????, 2.????????????, 3 ?????? ???????????? ???????????? ??? ?????? ?????? ??????
     */
    CompoundButton.OnCheckedChangeListener mMedicenChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mErrorTv.setVisibility(View.INVISIBLE);
            if (mVirusType1.isChecked() || mVirusType4.isChecked()|| mVirusType2.isChecked()) {
                mMedicenLayout.setVisibility(View.VISIBLE);
            } else {
                mMedicenLayout.setVisibility(View.GONE);
            }
            BtnEnableCheck();

            mVirusType5.setChecked(false);
        }
    };
    CompoundButton.OnCheckedChangeListener validationListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            BtnEnableCheck();
        }
    };

    public String getVirusTypes() {
        StringBuffer sb = new StringBuffer();
        if (mVirusType1.isChecked()) {
            sb.append("1,");
        }
        if (mVirusType2.isChecked()) {
            sb.append("2,");
        }
        if (mVirusType3.isChecked()) {
            sb.append("3,");
        }
        if (mVirusType4.isChecked()) {
            sb.append("4,");
        }
        if (mVirusType5.isChecked()) {
            sb.append("5,");
        }
        String result = sb.toString();
        if (result.startsWith(","))
            result = result.substring(1);
        return result;
    }

    private String getMedicenType() {
        String result = "N";
        if (mMedicenYesRadioBtn.isChecked()) {
            result = "Y";
        }

        return result;
    }

    private String getSmokeType() {
        String result = "N";
        if (mSmokeYesRadioBtn.isChecked()) {
            result = "Y";
        }

        return result;
    }

    /**
     * ???????????? ?????? ?????? ?????????
     */
    CompoundButton.OnCheckedChangeListener mDiseaseChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Logger.i(TAG, "mDiseaseChangeListener="+isChecked);
            if (isChecked) {
                mVirusType1.setChecked(false);
                mVirusType2.setChecked(false);
                mVirusType3.setChecked(false);
                mVirusType4.setChecked(false);
                mVirusType5.setChecked(true);

                BtnEnableCheck();
            }
        }
    };


    /**
     * ????????? ?????? ????????????
     */
    private void loadPersonalInfo() {
        Tr_mber_user_call.RequestData requestData = new Tr_mber_user_call.RequestData();
        Tr_login info = Define.getInstance().getLoginInfo();
        requestData.mber_sn = info.mber_sn;
        getData(getContext(), Tr_mber_user_call.class, requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_mber_user_call) {
                    Tr_mber_user_call data = (Tr_mber_user_call)obj;
                    Tr_login login = Define.getInstance().getLoginInfo();

                    mTypeChk1.setChecked("1".equals(data.mber_actqy));
                    mTypeChk2.setChecked("2".equals(data.mber_actqy));
                    mTypeChk3.setChecked("3".equals(data.mber_actqy));

                    if (data.disease_nm != null) {
                        mVirusType1.setChecked(data.disease_nm.contains("1"));
                        mVirusType2.setChecked(data.disease_nm.contains("2"));
                        mVirusType3.setChecked(data.disease_nm.contains("3"));
                        mVirusType4.setChecked(data.disease_nm.contains("4"));
                        mVirusType5.setChecked(data.disease_nm.contains("5"));
                    }

                    if (data.medicine_yn != null) {
                        mMedicenYesRadioBtn.setChecked("Y".equals(data.medicine_yn));
                        mMedicenNoRadioBtn.setChecked("N".equals(data.medicine_yn));
                    }

                    if (data.smkng_yn != null) {
                        mSmokeYesRadioBtn.setChecked("Y".equals(data.smkng_yn));
                        mSmokeNoRadioBtn.setChecked("N".equals(data.smkng_yn));
                    }
                }
            }
        });
    }
}
