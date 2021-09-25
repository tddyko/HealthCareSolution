package com.greencross.greencare.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.component.CFontButton;
import com.greencross.greencare.component.CFontTextView;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_login_pwd;
import com.greencross.greencare.network.tr.data.Tr_mber_reg_check_hp;
import com.greencross.greencare.network.tr.data.Tr_mber_reg_check_id;
import com.greencross.greencare.util.EditTextUtil;
import com.greencross.greencare.util.StringUtil;
import com.greencross.greencare.util.ViewUtil;

/**
 * Created by MrsWin on 2017-02-16.
 * 아이디 찾기
 */

public class FindPwdFragment extends BaseFragment {

    public static String FIND_PWD_EMAIL = "find_pwd_email";

    private EditText mEmailET;

    private EditText mPhoneNumTv1;
    private EditText mPhoneNumTv2;
    private EditText mPhoneNumTv3;

    private CFontTextView mErrTv1;
    private CFontTextView mErrTv2;

    public static Fragment newInstance() {
        FindPwdFragment fragment = new FindPwdFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_pwd_fragment, container, false);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        EditTextUtil.hideKeyboard(getContext(), mEmailET);
        EditTextUtil.hideKeyboard(getContext(), mPhoneNumTv1);
        EditTextUtil.hideKeyboard(getContext(), mPhoneNumTv2);
        EditTextUtil.hideKeyboard(getContext(), mPhoneNumTv3);
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.find_password));       // 액션바 타이틀
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmailET = (EditText) view.findViewById(R.id.find_pwd_email_edittext);

        mPhoneNumTv1 = (EditText)view.findViewById(R.id.find_pwd_phone_num1_edittext);
        mPhoneNumTv2 = (EditText)view.findViewById(R.id.find_pwd_phone_num2_edittext);
        mPhoneNumTv3 = (EditText)view.findViewById(R.id.find_pwd_phone_num3_edittext);

        mErrTv1 = (CFontTextView) view.findViewById(R.id.find_pwd_error_text1);
        mErrTv2 = (CFontTextView) view.findViewById(R.id.find_pwd_error_text2);

        view.findViewById(R.id.confrim_btn).setOnClickListener(mOnClickListener);

        if (getArguments() != null) {
            String email = getArguments().getString(FIND_PWD_EMAIL);
            if (TextUtils.isEmpty(email) == false) {
                mEmailET.setText(email);
                EditTextUtil.hideKeyboard(getContext(), mEmailET);
            }
        }

        /** font Typeface 적용 */
        Button typeButton = (Button)view.findViewById(R.id.confrim_btn);
        ViewUtil.setTypefaceNotoSansKRBold(getContext(), typeButton);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (R.id.confrim_btn == vId) {

                String email = mEmailET.getText().toString();
                String phoneNum = "";
                phoneNum += mPhoneNumTv1.getText().toString();
                phoneNum += mPhoneNumTv2.getText().toString();
                phoneNum += mPhoneNumTv3.getText().toString();

                if (StringUtil.isValidEmail(email) == false) {
                    CDialog.showDlg(getContext(), getString(R.string.join_step1_id_error));
                    return;
                }

                if (StringUtil.isValidPhoneNumber(phoneNum) == false) {
                    CDialog.showDlg(getContext(), getString(R.string.join_step1_phone_num_error));
                    return;
                }

                validIdCheck(email, phoneNum);
            }
        }
    };

    /**
     * 아이디체크
     * @return
     */
    private void validIdCheck(final String email, final String phoneNum) {
        mErrTv1.setVisibility(View.INVISIBLE);
        mErrTv2.setVisibility(View.INVISIBLE);


        if (StringUtil.isValidEmail(email) == false) {
            CDialog.showDlg(getContext(), getString(R.string.join_step1_id_error));
        } else {
            Tr_mber_reg_check_id.RequestData requestData = new Tr_mber_reg_check_id.RequestData();
            requestData.mber_id = email;
            getData(getContext(), Tr_mber_reg_check_id.class, requestData, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (obj instanceof Tr_mber_reg_check_id) {
                        Tr_mber_reg_check_id data = (Tr_mber_reg_check_id)obj;
                        if ("Y".equals(data.mber_id_yn)) {  // 중복아님 N
                            mErrTv1.setVisibility(View.INVISIBLE);
                            validPhoneNumCheck(phoneNum, data.mber_id);
                        } else {
                            mErrTv1.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }


    /**
     * 전화번호 체크
     * @return
     */
    private void validPhoneNumCheck(String phoneNum, final String email) {

        if (StringUtil.isValidPhoneNumber(phoneNum) == false) {
            CDialog.showDlg(getContext(), getString(R.string.join_step1_phone_num_error));
            return;
        } else {
            Tr_mber_reg_check_hp.RequestData requestData = new Tr_mber_reg_check_hp.RequestData();
            requestData.mber_hp = phoneNum;
            getData(getContext(), Tr_mber_reg_check_hp.class, requestData, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (obj instanceof Tr_mber_reg_check_hp) {
                        Tr_mber_reg_check_hp data = (Tr_mber_reg_check_hp)obj;
                        if ("Y".equals(data.mber_hp_yn)) { // 중복아님 N
                            mErrTv1.setVisibility(View.INVISIBLE);
                            doFindPwd(email, data.mber_hp);
                        } else {
                            mErrTv2.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }

    /**
     * 비밀번호 찾기
     */
    private void doFindPwd(String email, String phoneNum) {

        Tr_login_pwd.RequestData requestData = new Tr_login_pwd.RequestData();
        requestData.mber_hp = phoneNum;
        requestData.mber_id = email;
        getData(getContext(), Tr_login_pwd.class, requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_login_pwd) {
                    Tr_login_pwd data = (Tr_login_pwd)obj;
                    if ("Y".equals(data.send_mail_yn)) {
                        CDialog.showDlg(getContext(), getString(R.string.find_pwd_result_message), new CDialog.DismissListener() {
                            @Override
                            public void onDissmiss() {
                                movePage(LoginFragment.newInstance());
                            }
                        });
                    } else {

                        CDialog.showDlg(getContext(), getString(R.string.pw_send_fail));
                    }
                }
            }
        });
    }

}
