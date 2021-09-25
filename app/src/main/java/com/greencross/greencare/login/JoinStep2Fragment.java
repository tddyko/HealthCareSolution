package com.greencross.greencare.login;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.component.CDatePicker;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperWeight;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.network.tr.data.Tr_mber_user_call;
import com.greencross.greencare.network.tr.data.Tr_mber_user_edit_exe;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.ClearEditTextViewWatcher;
import com.greencross.greencare.util.EditTextUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;
import com.greencross.greencare.util.ViewUtil;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by MrsWin on 2017-02-16.
 */

public class JoinStep2Fragment extends BaseFragment {
    private final String TAG = JoinStep2Fragment.class.getSimpleName();

    private RadioGroup mSexRg;
    private RadioButton mSexManRb;
    private RadioButton mSexWomanRb;
    private EditText mBrithEt;
    private TextView mBirthErrTv;
    private EditText mHeightEt;
    private TextView mHeightErrTv;
    private EditText mWeightEt;
    private TextView mWeightErrTv;
    private EditText mTargetWeightEt;
    private TextView mTargetWeightErrTv;
    private Button next_button;
    private JoinDataVo dataVo;

    private boolean mIsInfoEdit = false;
    private boolean isValiBirth, isValiHeight, isValiWeight, isValiTarWeight;

    public static Fragment newInstance() {
        JoinStep2Fragment fragment = new JoinStep2Fragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onCreate()");

        super.onCreate(savedInstanceState);

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_step2_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataVo = (JoinDataVo) getArguments().getBinder(JoinStep1Fragment.JOIN_DATA);
        if (dataVo != null) {
            Logger.i(TAG, "onViewCreated.id="+dataVo.getId());
            Logger.i(TAG, "onViewCreated.pwd="+dataVo.getPwd());
            Logger.i(TAG, "onViewCreated.name="+dataVo.getName());
            Logger.i(TAG, "onViewCreated.phoneNum="+dataVo.getPhoneNum());
            Logger.i(TAG, "onViewCreated.sex="+dataVo.getSex());
            Logger.i(TAG, "onViewCreated.birth="+dataVo.getBirth());
        } else {
            dataVo = new JoinDataVo();
        }


        mSexRg = (RadioGroup) view.findViewById(R.id.join_step2_sex_radio_group);
        mSexManRb = (RadioButton) view.findViewById(R.id.join_step2_man_radio_button);
        mSexWomanRb = (RadioButton) view.findViewById(R.id.join_step2_woman_radio_button);

        mBrithEt = (EditText) view.findViewById(R.id.join_step2_birth_edittext);
        mBirthErrTv = (TextView) view.findViewById(R.id.join_step2_birth_error_textview);

        mHeightEt = (EditText) view.findViewById(R.id.join_step2_height_edittext);
        mHeightErrTv = (TextView) view.findViewById(R.id.join_step2_height_error_textview);

        mWeightEt = (EditText) view.findViewById(R.id.join_step2_weight_edittext);
        mWeightErrTv = (TextView) view.findViewById(R.id.join_step2_weight_error_textview);

        mTargetWeightEt = (EditText) view.findViewById(R.id.join_step2_target_weight_edittext);
        mTargetWeightErrTv = (TextView) view.findViewById(R.id.join_step2_target_weight_error_textview);


        new ClearEditTextViewWatcher(mHeightEt, 300L);
        setTextWatcher(mWeightEt, 300f, 2);
        setTextWatcher(mTargetWeightEt, 300f, 2);

        view.findViewById(R.id.next_button).setOnClickListener(mOnClickListener);
        mBrithEt.setOnClickListener(mOnClickListener);
        mBrithEt.setInputType(0);


        //키보드 가리기
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mBrithEt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        mBrithEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker(v);
                }
            }
        });

        mBrithEt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validCheck();
                BtnEnableCheck();
            }
        });

        mHeightEt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validCheck();
                BtnEnableCheck();
            }
        });
        mWeightEt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validCheck();
                BtnEnableCheck();
            }
        });
        mTargetWeightEt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validCheck();
                BtnEnableCheck();
            }
        });


        /** font Typeface 적용 */
        RadioButton typeRadioButton = (RadioButton)view.findViewById(R.id.join_step2_man_radio_button);
        ViewUtil.setTypefaceKelsonSansRegular(getContext(), typeRadioButton);

        typeRadioButton = (RadioButton)view.findViewById(R.id.join_step2_woman_radio_button);
        ViewUtil.setTypefaceKelsonSansRegular(getContext(), typeRadioButton);

        TextView typeTextView = (TextView)view.findViewById(R.id.join_step2_birth_edittext);
        ViewUtil.setTypefaceKelsonSansRegular(getContext(), typeTextView);

        typeTextView = (TextView)view.findViewById(R.id.join_step2_height_edittext);
        ViewUtil.setTypefaceKelsonSansRegular(getContext(), typeTextView);

        typeTextView = (TextView)view.findViewById(R.id.join_step2_weight_edittext);
        ViewUtil.setTypefaceKelsonSansRegular(getContext(), typeTextView);

        typeTextView = (TextView)view.findViewById(R.id.join_step2_target_weight_edittext);
        ViewUtil.setTypefaceKelsonSansRegular(getContext(), typeTextView);

        next_button = (Button)view.findViewById(R.id.next_button);
        ViewUtil.setTypefaceNotoSansKRBold(getContext(), next_button);



        // 액션바 타이틀이 있으면 환경설정에서 온것으로 판단
        if (getArguments() != null) {
            String bundleTitle = getArguments().getString(CommonActionBar.ACTION_BAR_TITLE);
            if (TextUtils.isEmpty(bundleTitle) == false) {
                Tr_login login = Define.getInstance().getLoginInfo();
                mIsInfoEdit = true;
                next_button.setText(R.string.join_step3_ecomplete);
                mWeightEt.setEnabled(false);

                loadPersonalInfo();
            }
        }
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle("가입정보 입력(2/3)");       // 액션바 타이틀
    }

    @Override
    public void onResume() {
        Log.d(this.getClass().getSimpleName(), "onResume()");
        super.onResume();

    }

    private void BtnEnableCheck(){
        if(isValiBirth && isValiHeight && isValiWeight && isValiTarWeight){
            next_button.setEnabled(true);
        }else{
            next_button.setEnabled(false);
        }
    }
    private boolean validCheck() {
        boolean isValid = true;
        dataVo.setSex(mSexManRb.isChecked() ? "1" : "2");

        if (validBirthCheck() == false) {
            isValid = false;
        }

        if (validHeightCheck() == false) {
            isValid = false;
        }

        if (validWeightCheck() == false) {
            isValid = false;
        }
        if (validTargetWeightCheck() == false) {
            isValid = false;
        }

        return isValid;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (R.id.next_button == vId) {
                if (validCheck()) {
                    if (mIsInfoEdit) {
                        // 회원정보수정
                        doEditInfo();
                    } else {
                        // 회원가입
                        Bundle bundle = new Bundle();
                        bundle.putBinder(JoinStep1Fragment.JOIN_DATA, dataVo);
                        movePage(JoinStep3Fragment.newInstance(), bundle);
                    }
                }
            } else if (vId == R.id.join_step2_birth_edittext) {
                showDatePicker(v);
            }
        }
    };

    private void showDatePicker(View v) {
        GregorianCalendar calendar = new GregorianCalendar();
        String birth = mBrithEt.getText().toString().trim();
        String[] birthSpl = birth.split("\\.");

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (birthSpl.length == 3) {
            year = Integer.parseInt("".equals(birthSpl[0]) ? "0" : birthSpl[0].trim());
            month = Integer.parseInt("".equals(birthSpl[1]) ? "0" : birthSpl[1].trim()) - 1;
            day = Integer.parseInt("".equals(birthSpl[2]) ? "0" : birthSpl[2].trim());
        }else {
            year = 1970;
            month = 1;
            day = 1;
        }

        EditTextUtil.hideKeyboard(getContext(), mBrithEt);
        new CDatePicker(getContext(), dateSetListener, year, month, day).show();
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear + 1;
            String msg = String.format("%d. %d. %d", year, monthOfYear, dayOfMonth);

            StringBuffer sb = new StringBuffer();
            sb.append(year);
            DecimalFormat df = new DecimalFormat("00");
            sb.append(df.format(monthOfYear));
            sb.append(df.format(dayOfMonth));

            mBrithEt.setText(msg);
            mBrithEt.setTag(sb.toString());
            validBirthCheck();
            Logger.i(TAG, "birth.tag="+mBrithEt.getTag());

        }

    };

    /**
     * 생년월일 체크
     * @return
     */
    private boolean validBirthCheck() {

        String birth = mBrithEt.getTag() != null ? mBrithEt.getTag().toString() : "";//mBrithEt.getText().toString();
        dataVo.setBirth(birth);
        if (birth.length() == 0) {
            mBirthErrTv.setVisibility(View.VISIBLE);
            isValiBirth = false;
            return false;
        } else {
            mBirthErrTv.setVisibility(View.INVISIBLE);
            isValiBirth = true;
        }
        return true;
    }

    /**
     * 키 체크
     * @return
     */
    private boolean validHeightCheck() {
        String heightStr = mHeightEt.getText().toString();
        int height = StringUtil.getIntVal(heightStr);
        dataVo.setHeight(heightStr);
        if (height < 80 && height > 300) {
            mHeightErrTv.setVisibility(View.VISIBLE);
            isValiHeight = false;
            return false;
        } else {
            mHeightErrTv.setVisibility(View.INVISIBLE);
            isValiHeight = true;
        }
        return true;
    }

    /**
     * 몸무게 체크
     * @return
     */
    private boolean validWeightCheck() {
        String weightStr = mWeightEt.getText().toString();
        float weight = StringUtil.getFloat(weightStr);
        dataVo.setWeight(weightStr);

        if(mIsInfoEdit) {
            DBHelper helper = new DBHelper(getContext());
            DBHelperWeight weightDb = helper.getWeightDb();
            DBHelperWeight.WeightStaticData bottomData = weightDb.getResultStatic();
            if(!bottomData.getWeight().isEmpty()) {
                mWeightErrTv.setText("(체중 관리에서 마지막으로 입력한 값입니다.)");
                mWeightErrTv.setVisibility(View.VISIBLE);
            }else{
                mWeightErrTv.setVisibility(View.INVISIBLE);
            }

            isValiWeight = true;
        }else{

            Logger.i(TAG, "str="+ "".equals(weightStr)+", weight="+weight+", weight="+(weight > 20f && weight < 300f));
            if ("".equals(weightStr) || (weight > 20f && weight < 300f) == false) {
                mWeightErrTv.setVisibility(View.VISIBLE);
                isValiWeight = false;
                return false;
            } else {
                mWeightErrTv.setVisibility(View.INVISIBLE);
                isValiWeight = true;
            }
            return true;
        }
        return true;
    }

    private boolean validTargetWeightCheck() {
        String weightStr = mTargetWeightEt.getText().toString();
        float weight = StringUtil.getFloat(weightStr);
            dataVo.setTargetWeight(weightStr);

        if ("".equals(weightStr) || (weight > 20f && weight < 300f) == false) {
            mTargetWeightErrTv.setVisibility(View.VISIBLE);
            isValiTarWeight = false;
            return false;
        } else {
            mTargetWeightErrTv.setVisibility(View.INVISIBLE);
            isValiTarWeight = true;
        }
        return true;
    }

    private String beforeText = "";
    private void setTextWatcher(final EditText editText, final float maxVal, final int dotAfterCnt) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String str = s.toString();
                if (beforeText.equals(str)) {
                    if (str.equals("") == false)
                        editText.setSelection(str.length());
                    return;
                }

                if (str.length() != 0) {
                    float val = StringUtil.getFloat(s.toString());

                    if (val == 0 || val > maxVal) {
                        str = str.substring(0, str.length()-1);
                        beforeText = str;
                        editText.setText(str);
                    }

                    String[] dotAfter = str.split("\\.");
                    if (dotAfter.length >= 2 && (dotAfter[1].length() > dotAfterCnt)) {
                        str = str.substring(0, str.length()-1);
                        beforeText = str;
                        editText.setText(str);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editText.addTextChangedListener(textWatcher);
    }

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

                    String birth = CDateUtil.getFormatYYYYMMDD(data.mber_lifyea);
                    mBrithEt.setText(birth);
                    mBrithEt.setTag(data.mber_lifyea);
                    mHeightEt.setText(login.mber_height);
                    mSexWomanRb.setChecked("2".equals(data.mber_sex));

                    DBHelper helper = new DBHelper(getContext());
                    DBHelperWeight WeightDb = helper.getWeightDb();
                    DBHelperWeight.WeightStaticData bottomData = WeightDb.getResultStatic();
                    if(bottomData.getWeight().isEmpty()){
                        mWeightEt.setText(data.mber_bdwgh);
                    }else{
                        mWeightEt.setText(bottomData.getWeight());
                    }

                    mTargetWeightEt.setText(login.mber_bdwgh_goal);
                }
            }
        });
    }

    /**
     * 회원정보수정
     */
    private void doEditInfo() {
        Tr_mber_user_edit_exe.RequestData requestData = new Tr_mber_user_edit_exe.RequestData();
        Tr_login info = Define.getInstance().getLoginInfo();
        requestData.mber_sn = info.mber_sn;
        requestData.mber_bdwgh = dataVo.getWeight();
        requestData.mber_bdwgh_goal = dataVo.getTargetWeight();
        requestData.mber_height = dataVo.getHeight();
        requestData.mber_sex = dataVo.getSex();
        requestData.mber_lifyea = dataVo.getBirth();

        getData(getContext(), Tr_mber_user_edit_exe.class, requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_mber_user_edit_exe) {
                    Tr_mber_user_edit_exe data = (Tr_mber_user_edit_exe)obj;

                        if ("Y".equals(data.reg_yn)) {
                            CDialog.showDlg(getContext(), "정보가 수정 되었습니다.", new CDialog.DismissListener() {
                                @Override
                                public void onDissmiss() {
                                    Tr_login login = Define.getInstance().getLoginInfo();
                                    login.mber_bdwgh = dataVo.getWeight();
                                    login.mber_height = dataVo.getHeight();
                                    login.mber_sex = dataVo.getSex();
                                    login.mber_lifyea = dataVo.getBirth();
                                    login.mber_bdwgh_goal = dataVo.getTargetWeight();
                                    Define.getInstance().setLoginInfo(login);

                                    int sex             = StringUtil.getIntVal(login.mber_sex);
                                    float weight        = StringUtil.getFloatVal(login.mber_bdwgh);
                                    float height        = StringUtil.getFloatVal(login.mber_height);
                                    int calory          = StringUtil.getIntVal(login.goal_mvm_calory);
                                    int step = StringUtil.getIntVal(getStepTargetCalulator(sex, height, weight, calory));
                                    setStepTarget(0, step);

                                    onBackPressed();
                                }
                            });
                        } else {
                            CDialog.showDlg(getContext(), "정보 변경에 실패 하였습니다.");
                        }
                }
            }
        });

    }
}