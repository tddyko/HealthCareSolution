package com.greencross.greencare.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_login_id;
import com.greencross.greencare.network.tr.data.Tr_mber_reg_check_hp;
import com.greencross.greencare.util.EditTextUtil;
import com.greencross.greencare.util.StringUtil;
import com.greencross.greencare.util.ViewUtil;

/**
 * Created by MrsWin on 2017-02-16.
 * 아이디 찾기
 */

public class FindIdFragment extends BaseFragment {

    private EditText mPhoneNumTv1;
    private EditText mPhoneNumTv2;
    private EditText mPhoneNumTv3;

    private TextView mEmailTv;
    private TextView mEmailErrTv;

    public static Fragment newInstance() {
        FindIdFragment fragment = new FindIdFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_id_fragment, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        EditTextUtil.hideKeyboard(getContext(), mPhoneNumTv1);
        EditTextUtil.hideKeyboard(getContext(), mPhoneNumTv2);
        EditTextUtil.hideKeyboard(getContext(), mPhoneNumTv3);
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.find_id));       // 액션바 타이틀
    }

    private void initView(View view) {
        mPhoneNumTv1 = (EditText)view.findViewById(R.id.find_id_phone_num1_edittext);
        mPhoneNumTv2 = (EditText)view.findViewById(R.id.find_id_phone_num2_edittext);
        mPhoneNumTv3 = (EditText)view.findViewById(R.id.find_id_phone_num3_edittext);

        mEmailTv = (TextView)view.findViewById(R.id.find_id_email_edittext);
        mEmailErrTv = (TextView)view.findViewById(R.id.join_step1_id_error_textview);

        view.findViewById(R.id.find_id_phone_num_confrim_button).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.find_id_complete_btn).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.find_pwd_button).setOnClickListener(mOnClickListener);

        /** font Typeface 적용 */
        Button typeButton = (Button)view.findViewById(R.id.find_id_complete_btn);
        ViewUtil.setTypefaceNotoSansKRBold(getContext(), typeButton);

        typeButton = (Button)view.findViewById(R.id.find_pwd_button);
        ViewUtil.setTypefaceNotoSansKRBold(getContext(), typeButton);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();

            String email = mEmailTv.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString(FindPwdFragment.FIND_PWD_EMAIL, email);

            if (R.id.find_id_complete_btn == vId) {
                movePage(LoginFragment.newInstance(), bundle);
            } else if (R.id.find_id_phone_num_confrim_button == vId) {
                validPhoneNumCheck();
            } else if (R.id.find_pwd_button == vId) {
                movePage(FindPwdFragment.newInstance(), bundle);
            }
        }
    };

    /**
     * 전화번호 체크
     * @return
     */
    private void validPhoneNumCheck() {
        mEmailErrTv.setVisibility(View.INVISIBLE);

        String phoneNum = "";
        phoneNum += mPhoneNumTv1.getText().toString();
        phoneNum += mPhoneNumTv2.getText().toString();
        phoneNum += mPhoneNumTv3.getText().toString();

        final String numVal = phoneNum;
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
                        if ("N".equals(data.mber_hp_yn)) {
                            mEmailErrTv.setVisibility(View.VISIBLE);
                        } else {
                            doLoginId(numVal);
                        }
                    }
                }
            });
        }
    }

    /**
     * 아이디 찾기
     * @param phoneNum
     */
    private void doLoginId(String phoneNum) {

        Tr_login_id.RequestData requestData = new Tr_login_id.RequestData();
        requestData.mber_hp = phoneNum;
        getData(getContext(), Tr_login_id.class, requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_login_id) {
                    Tr_login_id data = (Tr_login_id)obj;
                    mEmailTv.setText(data.mber_id);
                }

            }
        });
    }

}
