package com.greencross.greencare.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.main.DustManager;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.network.tr.data.Tr_mber_edit_exe;
import com.greencross.greencare.network.tr.data.Tr_mber_reg_check_hp;
import com.greencross.greencare.network.tr.data.Tr_mber_reg_check_id;
import com.greencross.greencare.network.tr.data.Tr_mber_reg_check_nm;
import com.greencross.greencare.network.tr.data.Tr_mber_user_call;
import com.greencross.greencare.util.SharedPref;
import com.greencross.greencare.util.StringUtil;

import static com.greencross.greencare.component.CDialog.LoginshowDlg;
import static com.greencross.greencare.component.CDialog.showDlg;

/**
 * Created by MrsWin on 2017-02-16.
 */

public class JoinStep1Fragment extends BaseFragment {
    public static String JOIN_DATA = "join_data";   // 회원가입시에 사용할 데이터들

    private LinearLayout join_step1_agreegroup;
    private TextView mContractErrTv;
    private CheckBox mStep1Cb;
    private CheckBox mStep2Cb;
    private TextView mNumErrTv;
    private EditText mNameEt;
    private EditText mPhoneNumEt;
    private TextView mNameErrTv;
    private TextView mPwdErrTv;
    private EditText mPwd1Et;
    private EditText mPwd2Et;
    private TextView mIdErrTv;
    private EditText mIdEditText;

    private ImageView mIdErrIv;
    private ImageView mPwd1ErrIv1;
    private ImageView mPwd1ErrIv2;
    private ImageView mNameErrIv;
    private ImageView mNumErrIv;

    private TextView mCityTv;
    private ImageView mCityErrIv;
    private TextView mCityErrTv;
    private RelativeLayout relativeUserEmailGroup;
    private TextView tvUserEmail;

    private View mCheckedEditText;
    private JoinDataVo dataVo;
    private Button next_button;

    private boolean isEmailCheck, isPwd1Check, isPwd2Check, isNameCheck, isPhoneCheck, isCityCheck, isBoxCheck1, isBoxCheck2;


    private boolean mIsInfoEdit = false;

    public static Fragment newInstance() {
        JoinStep1Fragment fragment = new JoinStep1Fragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onCreate()");

        super.onCreate(savedInstanceState);

        // 액션바 타이틀이 있으면 환경설정에서 온것으로 판단
        if (getArguments() != null) {
            String bundleTitle = getArguments().getString(CommonActionBar.ACTION_BAR_TITLE);
            if (TextUtils.isEmpty(bundleTitle) == false) {
                mIsInfoEdit = true;
                isEmailCheck = true;
                isPwd1Check = true;
                isPwd2Check = true;
                isNameCheck = true;
                isPhoneCheck = true;
                isCityCheck = true;
                isBoxCheck1 = true;
                isBoxCheck2 = true;
            }
        }else{
            isEmailCheck = false;
            isPwd1Check = false;
            isPwd2Check = false;
            isNameCheck = false;
            isPhoneCheck = false;
            isCityCheck = false;
            isBoxCheck1 = false;
            isBoxCheck2 = false;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_step1_fragment, container, false);

        isCityCheck = false;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataVo = new JoinDataVo();

        tvUserEmail = (TextView) view.findViewById(R.id.tvUserEmail);
        relativeUserEmailGroup = (RelativeLayout) view.findViewById(R.id.relativeUserEmailGroup);

        mIdEditText = (EditText) view.findViewById(R.id.login_id_edittext);
        mIdErrIv = (ImageView) view.findViewById(R.id.login_id_imageview);
        mIdErrTv = (TextView) view.findViewById(R.id.join_step1_id_error_textview);

        mPwd1Et = (EditText) view.findViewById(R.id.join_step1_pwd_edittext1);
        mPwd2Et = (EditText) view.findViewById(R.id.join_step1_pwd_edittext2);
        mPwd1ErrIv1 = (ImageView) view.findViewById(R.id.join_step1_pwd_imageview1);
        mPwd1ErrIv2 = (ImageView) view.findViewById(R.id.join_step1_pwd_imageview2);
        mPwdErrTv = (TextView) view.findViewById(R.id.join_step1_pwd_error_textview);

        mNameEt = (EditText) view.findViewById(R.id.join_step1_name_edittext);
        mNameErrIv = (ImageView) view.findViewById(R.id.join_step1_name_imageview);
        mNameErrTv = (TextView) view.findViewById(R.id.join_step1_name_error_textview);

        mPhoneNumEt = (EditText) view.findViewById(R.id.join_step1_phone_num_edittext);
        mNumErrIv = (ImageView) view.findViewById(R.id.join_step1_phone_num_imageview);
        mNumErrTv = (TextView) view.findViewById(R.id.join_step1_phone_num_error_textview);

        mCityTv = (TextView) view.findViewById(R.id.join_step1_city_map);
        mCityErrIv = (ImageView) view.findViewById(R.id.join_step1_city_map_error);
        mCityErrTv = (TextView) view.findViewById(R.id.join_step1_city_error_textview);

        mStep1Cb = (CheckBox) view.findViewById(R.id.join_step1_checkbox1);
        mStep2Cb = (CheckBox) view.findViewById(R.id.join_step1_checkbox2);
        mContractErrTv = (TextView) view.findViewById(R.id.join_step1_contract_error_textview);
        join_step1_agreegroup = (LinearLayout) view.findViewById(R.id.join_step1_agreegroup);
        next_button = (Button) view.findViewById(R.id.next_button);

        mIdEditText.setOnFocusChangeListener(mFocusChangeListener);
        mPwd1Et.setOnFocusChangeListener(mFocusChangeListener);
        mPwd2Et.setOnFocusChangeListener(mFocusChangeListener);
        mNameEt.setOnFocusChangeListener(mFocusChangeListener);
        mPhoneNumEt.setOnFocusChangeListener(mFocusChangeListener);

        mStep1Cb.setOnClickListener(mOnClickListener);
        mStep2Cb.setOnClickListener(mOnClickListener);
        mCityTv.setOnTouchListener(mOnTouchListener);
        view.findViewById(R.id.join_step1_contract_textview).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.join_step1_personal_info_textview).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.next_button).setOnClickListener(mOnClickListener);

        // 액션바 타이틀이 있으면 환경설정에서 온것으로 판단
        if (getArguments() != null) {
            String bundleTitle = getArguments().getString(CommonActionBar.ACTION_BAR_TITLE);
            if (TextUtils.isEmpty(bundleTitle) == false) {
                next_button.setEnabled(true);
                join_step1_agreegroup.setVisibility(View.GONE);
                next_button.setText(R.string.join_step3_ecomplete);

                relativeUserEmailGroup.setVisibility(View.GONE);
                tvUserEmail.setVisibility(View.VISIBLE);
                loadPersonalInfo();
            }
        }
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.join_inform_step1));       // 액션바 타이틀
    }
    @Override
    public void onResume() {
        Log.d(this.getClass().getSimpleName(), "onResume()");
        super.onResume();

        BtnEnableCheck();
    }
    private boolean validCheck() {
        boolean isValid = true;
        if (validIdCheck() == false && !mIsInfoEdit) {
            isValid = false;
        }
        if (mPwd1Et.getText().length() != 0 || mPwd2Et.getText().length() != 0){
            if (validPwdCheck() == false) {
                isValid = false;
            }
        }
        if (validNameCheck() == false) {
            isValid = false;
        }

        if (validPhoneNumCheck() == false) {
            isValid = false;
        }

        if (validCityCheck() == false) {
            isValid = false;
        }
        // 수정모드가 아니라면 동의체크.
        if (validContractCheck() == false && !mIsInfoEdit) {
            isValid = false;
        }
        return isValid;
    }

    View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            mCheckedEditText = v;
            if (v == mIdEditText && hasFocus == false) {
                validIdCheck();
            } else if ((v == mPwd1Et || v == mPwd2Et) && hasFocus == false) {
                String pwd1 = mPwd1Et.getText().toString();
                String pwd2 = mPwd2Et.getText().toString();
                if (pwd1.length() != 0 && pwd2.length() != 0)
                    validPwdCheck();
            } else if (v == mNameEt && hasFocus == false) {
                validNameCheck();
            } else if (v == mPhoneNumEt && hasFocus == false) {
                validPhoneNumCheck();
            }
            BtnEnableCheck();
        }
    };

    boolean isValid = false;

    /**
     * 아이디체크
     *
     * @return
     */
    private boolean validIdCheck() {
        String email = mIdEditText.getText().toString();
        dataVo.setId(email);

        if (mIsInfoEdit)
            return true;

        if (StringUtil.isValidEmail(email) == false) {
            mIdErrIv.setBackgroundResource(R.drawable.icon_input_x);
            mIdErrTv.setVisibility(View.VISIBLE);
            mIdErrTv.setText(R.string.join_step1_id_error);
            isEmailCheck = false;
            BtnEnableCheck();
            return false;
        } else {
            Tr_mber_reg_check_id.RequestData requestData = new Tr_mber_reg_check_id.RequestData();
            requestData.mber_id = email;
            getData(getContext(), Tr_mber_reg_check_id.class, requestData, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (obj instanceof Tr_mber_reg_check_id) {
                        Tr_mber_reg_check_id data = (Tr_mber_reg_check_id) obj;
                        if ("N".equals(data.mber_id_yn)) {  // 중복아님 N
                            mIdErrIv.setBackgroundResource(R.drawable.icon_input_o);
                            mIdErrTv.setVisibility(View.INVISIBLE);
                            isValid = true;
                            isEmailCheck = true;
                            BtnEnableCheck();
                        } else {
                            mIdErrIv.setBackgroundResource(R.drawable.icon_input_x);
                            mIdErrTv.setVisibility(View.VISIBLE);
                            mIdErrTv.setText(R.string.join_step1_id_error2);
                            isValid = false;
                            isEmailCheck = false;
                            BtnEnableCheck();
                        }
                    }
                }
            });
        }
        return isValid;
    }

    /**
     * 패스워드 체크
     *
     * @return
     */
    private boolean validPwdCheck() {

        String pwd1 = mPwd1Et.getText().toString();
        String pwd2 = mPwd2Et.getText().toString();

        dataVo.setPwd(pwd1);

        if (!StringUtil.isValidPassword(pwd1) || !StringUtil.isValidPassword(pwd2)) {
            mPwdErrTv.setVisibility(View.VISIBLE);
            mPwdErrTv.setText(R.string.join_step1_pwd_error_leng8);
            if (StringUtil.isValidPassword(pwd1) == false) {
                mPwd1ErrIv1.setBackgroundResource(R.drawable.icon_input_x);
                isPwd1Check = false;
            }
            if (StringUtil.isValidPassword(pwd2) == false) {
                mPwd1ErrIv2.setBackgroundResource(R.drawable.icon_input_x);
                isPwd2Check = false;
            }

            BtnEnableCheck();
            return false;
        } else if (pwd1.equals(pwd2) == false) {
            mPwdErrTv.setVisibility(View.VISIBLE);
            mPwdErrTv.setText(R.string.join_step1_pwd_error);
            mPwd1ErrIv1.setBackgroundResource(R.drawable.icon_input_x);
            mPwd1ErrIv2.setBackgroundResource(R.drawable.icon_input_x);
            isPwd1Check = false;
            isPwd2Check = false;
            BtnEnableCheck();
            return false;
        } else {
            mPwd1ErrIv1.setBackgroundResource(R.drawable.icon_input_o);
            mPwd1ErrIv2.setBackgroundResource(R.drawable.icon_input_o);
            mPwdErrTv.setVisibility(View.INVISIBLE);
            isPwd1Check = true;
            isPwd2Check = true;
            BtnEnableCheck();
        }
        return true;
    }


    /**
     * 전화번호 체크
     *
     * @return
     */
    private boolean validPhoneNumCheck() {
        String phoneNum = mPhoneNumEt.getText().toString();
        dataVo.setPhoneNum(phoneNum);

        Tr_login info = Define.getInstance().getLoginInfo();
        if (mIsInfoEdit && info.mber_hp.equals(phoneNum)) {
            // 정보수정일때 기존 번호와 동일하면 true
            isPhoneCheck = true;
            return true;
        }

        if (StringUtil.isValidPhoneNumber(phoneNum) == false) {
            mNumErrIv.setBackgroundResource(R.drawable.icon_input_x);
            mNumErrTv.setText(R.string.join_step1_phone_num_error);
            mNumErrTv.setVisibility(View.VISIBLE);
            isPhoneCheck = false;
            return false;
        } else {
            Tr_mber_reg_check_hp.RequestData requestData = new Tr_mber_reg_check_hp.RequestData();
            requestData.mber_hp = phoneNum;
            getData(getContext(), Tr_mber_reg_check_hp.class, requestData, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (obj instanceof Tr_mber_reg_check_hp) {
                                Tr_mber_reg_check_hp data = (Tr_mber_reg_check_hp) obj;
                                if ("N".equals(data.mber_hp_yn)) {
                                    mNumErrIv.setVisibility(View.VISIBLE);
                                    mNumErrIv.setBackgroundResource(R.drawable.icon_input_o);
                                    mNumErrTv.setVisibility(View.INVISIBLE);
                                    isPhoneCheck = true;
                                    isValid = true;
                        } else {
                            mNumErrIv.setVisibility(View.VISIBLE);
                            mNumErrIv.setBackgroundResource(R.drawable.icon_input_x);
                            mNumErrTv.setVisibility(View.VISIBLE);
                            mNumErrTv.setText(getString(R.string.join_step1_phone_num_error2));
                            isPhoneCheck = false;
                            isValid = false;
                        }
                    }
                }
            });
        }

        BtnEnableCheck();
        return isValid;
    }

    /**
     * 이름 체크(닉네임)
     *
     * @return
     */
    private boolean validNameCheck() {
        String name = mNameEt.getText().toString();
        dataVo.setName(name);

        Tr_login info = Define.getInstance().getLoginInfo();
        if (mIsInfoEdit && info.mber_nm.equals(name)) {
            // 회원 정보 수정일때
            return true;
        }

        if ((name.length() < 2) || (name.length() > 10)) {
            mNameErrIv.setBackgroundResource(R.drawable.icon_input_x);
            mNameErrTv.setText(R.string.join_step1_name_error2);
            mNameErrTv.setVisibility(View.VISIBLE);
            // EditTextUtil.focusAndShowKeyboard(getContext(), mNameEt);
            isNameCheck = false;
            return false;
        } else if (StringUtil.isSpecialWord(name) == false) {
            mNameErrIv.setBackgroundResource(R.drawable.icon_input_x);
            mNameErrTv.setVisibility(View.VISIBLE);
            mNameErrTv.setText(R.string.join_step1_dont_special_word);
            isNameCheck = false;
            isValid = false;
        } else {
            Tr_mber_reg_check_nm.RequestData requestData = new Tr_mber_reg_check_nm.RequestData();
            requestData.mber_nm = name;
            getData(getContext(), Tr_mber_reg_check_nm.class, requestData, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (obj instanceof Tr_mber_reg_check_nm) {
                        Tr_mber_reg_check_nm data = (Tr_mber_reg_check_nm) obj;
                        if ("Y".equals(data.mber_hp_yn)) {
                            mNameErrIv.setVisibility(View.VISIBLE);
                            mNameErrIv.setBackgroundResource(R.drawable.icon_input_x);
                            mNameErrTv.setVisibility(View.VISIBLE);
                            mNameErrTv.setText(R.string.join_step1_phone_num_error2);
                            isNameCheck = false;
                            isValid = false;
                        } else {
                            mNameErrIv.setVisibility(View.VISIBLE);
                            mNameErrIv.setBackgroundResource(R.drawable.icon_input_o);
                            mNameErrTv.setVisibility(View.INVISIBLE);
                            isNameCheck = true;
                            isValid = true;
                        }
                    }
                }
            });
        }

        BtnEnableCheck();
        return isValid;
    }

    private boolean validCityCheck() {
        boolean isError = false;
        if (dataVo.getCity() == null) {
            isCityCheck = false;
            isError = false;
        } else {
            isError = true;
            isCityCheck = true;
        }
        return isError;
    }

    /**
     * 약관동의 체크 박스
     *
     * @return
     */
    private boolean validContractCheck() {
        if (!mIsInfoEdit) {
            // 회원정보 수정모드가 아니라면.
            if (mStep1Cb.isChecked() == false || mStep2Cb.isChecked() == false) {
                mContractErrTv.setVisibility(View.VISIBLE);

                return false;
            } else {
                mContractErrTv.setVisibility(View.INVISIBLE);
            }
            return true;
        } else {
            return true;
        }
    }

    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (R.id.join_step1_city_map == v.getId()) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showLocationDlg();
                }
            }
            return false;
        }
    };

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (R.id.next_button == vId) {
                if (validCheck()) {
                    if (mIsInfoEdit) {
                        // 회원정보 수정
                        doEditInfo();
                    } else {
                        // 회원가입 스텝2로 이동
                        Bundle bundle = new Bundle();
                        bundle.putBinder(JoinStep1Fragment.JOIN_DATA, dataVo);
                        movePage(JoinStep2Fragment.newInstance(), bundle);
                    }
                }
            } else if (R.id.join_step1_contract_textview == vId) {
                LoginshowDlg(getContext(), getString(R.string.join_step1_contract1_title), getString(R.string.join_step1_contract1_msg));
            } else if (R.id.join_step1_personal_info_textview == vId) {
                LoginshowDlg(getContext(), getString(R.string.join_step1_contract2_title), getString(R.string.join_step1_contract2_msg));
            } else if(R.id.join_step1_checkbox1 == vId){
                if(mStep1Cb.isChecked()){
                    isBoxCheck1 = true;
                }else{
                    isBoxCheck1 = false;
                }
                BtnEnableCheck();
            } else if(R.id.join_step1_checkbox2 == vId){
                if(mStep2Cb.isChecked()){
                    isBoxCheck2 = true
                    ;
                }else{
                    isBoxCheck2 = false;
                }
                BtnEnableCheck();
            } else if (v == mStep1Cb || v == mStep2Cb) {

                if (mCheckedEditText != null) {
                    mCheckedEditText.clearFocus();
                }
            }
        }
    };

    private void loadPersonalInfo() {
        mIdEditText.setEnabled(false);

        Tr_mber_user_call.RequestData requestData = new Tr_mber_user_call.RequestData();
        Tr_login info = Define.getInstance().getLoginInfo();
        requestData.mber_sn = info.mber_sn;
        getData(getContext(), Tr_mber_user_call.class, requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_mber_user_call) {
                    Tr_mber_user_call data = (Tr_mber_user_call)obj;

                    Tr_login login = Define.getInstance().getLoginInfo();
                    mIdEditText.setText(data.mber_id);
                    tvUserEmail.setText(data.mber_id);
                    mNameEt.setText(data.mber_nm);
                    mPhoneNumEt.setText(data.mber_hp);
                    mCityTv.setText(DustManager.getCityName(data.mber_zone));
                    dataVo.setCity(data.mber_zone);
                    isCityCheck = true;
                }
            }
        });
    }

    /**
     * Btn enable true / false
     */
    private void BtnEnableCheck(){
        // 수정모드라면.
        if (mIsInfoEdit){
            if(isPwd1Check && isPwd2Check && isNameCheck && isPhoneCheck && isCityCheck){
                next_button.setEnabled(true);
            }else{
//                next_button.setEnabled(false);
            }
        }else{
            if(isEmailCheck && isPwd1Check && isPwd2Check && isNameCheck && isPhoneCheck && isCityCheck && isBoxCheck1 && isBoxCheck2){
                next_button.setEnabled(true);
            }else{
                next_button.setEnabled(false);
            }
        }

    }

    /**
     * 회원정보수정
     */
    private void doEditInfo() {
        Tr_mber_edit_exe.RequestData requestData = new Tr_mber_edit_exe.RequestData();
        final Tr_login info = Define.getInstance().getLoginInfo();
        String email = mIdEditText.getText().toString();

        // 비밀번호 입력안해도 개인정보 변경할 수 있도록 수정.
        if (mPwd1Et.getText().length()==0){
            requestData.mber_pwd = SharedPref.getInstance().getPreferences(SharedPref.SAVED_LOGIN_PWD);
        }else{
            requestData.mber_pwd = dataVo.getPwd();
        }
        requestData.mber_sn = info.mber_sn;
        requestData.mber_hp = dataVo.getPhoneNum();
        requestData.mber_id = email;
        requestData.mber_nm = dataVo.getName();
        requestData.mber_zone = dataVo.getCity();
        getData(getContext(), Tr_mber_edit_exe.class, requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_mber_edit_exe) {

                    Tr_mber_edit_exe data = (Tr_mber_edit_exe)obj;
                    if ("Y".equals(data.reg_yn)) {
                        CDialog.showDlg(getContext(), getString(R.string.inform_modify_success), new CDialog.DismissListener() {
                            @Override
                            public void onDissmiss() {
                                info.mber_nm = dataVo.getName();
                                info.mber_hp = dataVo.getPhoneNum();
                                info.mber_zone = dataVo.getCity();
                                Define.getInstance().setLoginInfo(info);

                                boolean isAutoLogin = SharedPref.getInstance().getPreferences(SharedPref.IS_AUTO_LOGIN, false);
                                if (isAutoLogin && info != null) {
                                    Tr_login.RequestData requestData = new Tr_login.RequestData();
                                    if (mPwd1Et.getText().length() > 0){
                                        SharedPref.getInstance().savePreferences(SharedPref.SAVED_LOGIN_PWD, mPwd1Et.getText().toString());
                                    }
                                }

                                onBackPressed();
                            }
                        });
                    } else {
                        showDlg(getContext(), getString(R.string.inform_modify_fail));
                    }
                }
            }
        });
    }

    /**
     * 지역 선택
     */
    private void showLocationDlg() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_listview, null);
        ListView listView = (ListView)view.findViewById(R.id.dialog_listview);
        listView.setAdapter(new LocationAdapter());

        final CDialog dlg = CDialog.showDlg(getContext(), view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Define.LOCATION_DATA data = (Define.LOCATION_DATA) parent.getItemAtPosition(position);
                mCityTv.setText(data.getLocation().getCity());
                mCityTv.setTag(data.getLocation().getCode());
                dataVo.setCity(data.getLocation().getCode());
                dlg.dismiss();
                isCityCheck= true;
                BtnEnableCheck();
            }
        });
    }

    class LocationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Define.LOCATION_DATA.values().length;
        }

        @Override
        public Object getItem(int position) {
            return Define.LOCATION_DATA.values()[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_list_item_textview, null);
                holder.tv = (TextView)view.findViewById(R.id.list_item_textview);

                convertView = view;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            Define.LOCATION_DATA data = (Define.LOCATION_DATA) getItem(position);
            holder.tv.setText(data.getLocation().getCity());
            return convertView;
        }
    }

    class ViewHolder {
        TextView tv;
    }
}
