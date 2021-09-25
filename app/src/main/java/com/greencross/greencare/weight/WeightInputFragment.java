package com.greencross.greencare.weight;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.IBaseFragment;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.bluetooth.manager.DeviceDataUtil;
import com.greencross.greencare.bluetooth.model.WeightModel;
import com.greencross.greencare.component.CDatePicker;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.component.CFontEditText;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperWeight;
import com.greencross.greencare.main.BluetoothManager;
import com.greencross.greencare.network.tr.data.Tr_get_hedctdata;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.step.StepInputFragment;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;
import com.greencross.greencare.util.ViewUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.greencross.greencare.util.CDateUtil.getForamtyyMMddHHmmssSS;
import static com.greencross.greencare.util.CDateUtil.getForamtyyyyMMddHHmm;

/**
 * Created by insystemscompany on 2017. 2. 28..
 */

public class WeightInputFragment extends BaseFragment implements IBaseFragment {

    private static final String TAG = WeightInputFragment.class.getSimpleName();
    private TextView mDateTv, mTimeTv;

    private Button mWeightInputSavebtn;
    private CFontEditText mWeightEt;
    private CFontEditText mWeightTargetEt;
    private CFontEditText mWeightBodyRateEt;
    private CFontEditText mWeightinnerVolumeEt;
    private CFontEditText mWeightWaterVolumeEt;
    private CFontEditText mWeightMuscleVolumeEt;

    private int cal_year;
    private int cal_month;
    private int cal_day;
    private int cal_hour;
    private int cal_min;

    public static Fragment newInstance() {
        StepInputFragment fragment = new StepInputFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weight_input, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDateTv = (TextView) view.findViewById(R.id.weight_input_date_textview);
        mTimeTv = (TextView) view.findViewById(R.id.weight_input_time_textview);

        mWeightInputSavebtn = (Button) view.findViewById(R.id.weight_input_save_btn);
        mWeightEt = (CFontEditText) view.findViewById(R.id.etWeightWg);
        mWeightTargetEt = (CFontEditText) view.findViewById(R.id.etWeightTargetWg);
        mWeightBodyRateEt = (CFontEditText) view.findViewById(R.id.etWeightBodyRate);
        mWeightinnerVolumeEt = (CFontEditText) view.findViewById(R.id.etWeightInnerVolume);
        mWeightWaterVolumeEt = (CFontEditText) view.findViewById(R.id.etWeightWaterVolume);
        mWeightMuscleVolumeEt = (CFontEditText) view.findViewById(R.id.etWeightMuscleVolume);

        String now_time = getForamtyyyyMMddHHmm(new Date(System.currentTimeMillis()));
        java.util.Calendar cal = CDateUtil.getCalendar_yyyy_MM_dd_HH_mm(now_time);
        cal_year = cal.get(Calendar.YEAR);
        cal_month = cal.get(Calendar.MONTH);
        cal_day = cal.get(Calendar.DAY_OF_MONTH);
        cal_hour = cal.get(Calendar.HOUR_OF_DAY);
        cal_min = cal.get(Calendar.MINUTE);

        mDateTvSet(cal_year, cal_month, cal_day);
        mTimeTvSet(cal_hour, cal_min);

        DBHelper helper = new DBHelper(getContext());
        DBHelperWeight WeightDb = helper.getWeightDb();
        DBHelperWeight.WeightStaticData bottomData = WeightDb.getResultStatic();

        if(bottomData.getWeight().isEmpty()){
            mWeightEt.setHint("0");
        }else{
            mWeightEt.setHint(bottomData.getWeight());
        }

        Tr_login login = Define.getInstance().getLoginInfo();
        mWeightTargetEt.setEnabled(true);
        mWeightTargetEt.setHint(login.mber_bdwgh_goal);

        /** font Typeface 적용 */
        ViewUtil.setTypefaceNotoSansKRBold(getContext(), mWeightInputSavebtn);

        setTextWatcher(mWeightEt, 300f, 2);
        setTextWatcher(mWeightTargetEt, 300f, 2);
        setTextWatcher(mWeightBodyRateEt, 300f, 2);
        setTextWatcher(mWeightinnerVolumeEt, 300f, 2);
        setTextWatcher(mWeightWaterVolumeEt, 300f, 2);
        setTextWatcher(mWeightMuscleVolumeEt, 300f, 2);


        mWeightEt.addTextChangedListener(watcher);
        mWeightTargetEt.addTextChangedListener(watcher);
        mDateTv.addTextChangedListener(watcher);
        mTimeTv.addTextChangedListener(watcher);

        mWeightInputSavebtn.setOnClickListener(mClickListener);
        mDateTv.setOnTouchListener(mTouchListener);
        mTimeTv.setOnTouchListener(mTouchListener);
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle( getString(R.string.text_weight_input));
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.weight_input_save_btn) {
                validInputCheck();
            }
        }
    };

    View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int vId = v.getId();
                if (vId == R.id.weight_input_date_textview) {
                    showDatePicker(v);
                } else if (vId == R.id.weight_input_time_textview) {
                    showTimePicker();
                }
            }
            return false;
        }
    };

    private boolean DateTimeCheck(String type, int pram1, int pram2, int pram3){
        Calendar cal = Calendar.getInstance();

        if(type.equals("D")){
            cal.set(Calendar.YEAR, pram1);
            cal.set(Calendar.MONTH, pram2);
            cal.set(Calendar.DAY_OF_MONTH, pram3);
            cal.set(Calendar.HOUR_OF_DAY, cal_hour);
            cal.set(Calendar.MINUTE, cal_min);

            if(cal.getTimeInMillis() > System.currentTimeMillis()){
                CDialog.showDlg(getContext(), getString(R.string.message_nowtime_over), new CDialog.DismissListener() {
                    @Override
                    public void onDissmiss() {

                    }
                });
                return false;
            }else{
                return true;
            }
        }else{
            cal.set(Calendar.YEAR, cal_year);
            cal.set(Calendar.MONTH, cal_month);
            cal.set(Calendar.DAY_OF_MONTH, cal_day);
            cal.set(Calendar.HOUR_OF_DAY, pram1);
            cal.set(Calendar.MINUTE, pram2);

            if(cal.getTimeInMillis() > System.currentTimeMillis()){
                CDialog.showDlg(getContext(), getString(R.string.message_nowtime_over), new CDialog.DismissListener() {
                    @Override
                    public void onDissmiss() {

                    }
                });

                return false;
            }else{
                return true;
            }
        }
    }

    private void showDatePicker(View v) {
        GregorianCalendar calendar = new GregorianCalendar();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String date = mDateTv.getTag().toString();
        if (TextUtils.isEmpty(date) == false) {
            year = StringUtil.getIntVal(date.substring(0 , 4));
            month = StringUtil.getIntVal(date.substring(4 , 6))-1;
            day = StringUtil.getIntVal(date.substring(6 , 8));
        }

        new CDatePicker(getContext(), dateSetListener, year, month, day, false).show();
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if(DateTimeCheck("D",year, monthOfYear, dayOfMonth)) {
                cal_year = year;
                cal_month = monthOfYear;
                cal_day = dayOfMonth;
                mDateTvSet(year, monthOfYear, dayOfMonth);
            }
        }

    };

    private void mDateTvSet(int year, int monthOfYear, int dayOfMonth){
        String msg = String.format("%d.%d.%d", year, monthOfYear + 1, dayOfMonth);
        String tagMsg = String.format("%d%02d%02d", year, monthOfYear + 1, dayOfMonth);
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear + 1);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        mDateTv.setText(msg+" "+ CDateUtil.getDateToWeek(tagMsg)+"요일");
        mDateTv.setTag(tagMsg);
    }

    private void showTimePicker() {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        Date now = new Date();
        cal.setTime(now);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String time = mTimeTv.getTag().toString();
        if (TextUtils.isEmpty(time) == false) {
            hour = StringUtil.getIntVal(time.substring(0, 2));
            minute = StringUtil.getIntVal(time.substring(2 , 4));

            Logger.i(TAG, "hour="+hour+", minute="+minute);
        }

        TimePickerDialog dialog = new TimePickerDialog(getContext(), listener, hour, minute, false);
        dialog.show();
    }

    /**
     * 시간 피커 완료
     */
    private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        if(DateTimeCheck("S",hourOfDay, minute, 0)) {
            cal_hour = hourOfDay;
            cal_min = minute;
            mTimeTvSet(hourOfDay, minute);
        }
        }
    };

    private void mTimeTvSet(int hourOfDay, int minute){
        // 설정버튼 눌렀을 때
        String amPm = getString(R.string.text_morning);
        int hour = hourOfDay;
        if (hourOfDay >= 11) {
            amPm = getString(R.string.text_afternoon);
            if (hourOfDay >= 13)
                hour -= 12;
        }
        String tagMsg = String.format("%02d%02d", hourOfDay, minute);
        String timeStr = String.format("%02d:%02d", hour, minute);
        mTimeTv.setText(amPm + " " + timeStr);
        mTimeTv.setTag(tagMsg);
    }

    private void validInputCheck() {
        Tr_get_hedctdata.DataList data = new Tr_get_hedctdata.DataList();


        final String regDate = mDateTv.getTag().toString();
        if (TextUtils.isEmpty(regDate)) {
            CDialog.showDlg(getContext(), getString(R.string.date_input_error));
            return;
        }

        final String timeStr = mTimeTv.getTag().toString();
        if (TextUtils.isEmpty(timeStr)) {
            CDialog.showDlg(getContext(), getString(R.string.time_input_error));
            return;
        }

        final String mWeight = mWeightEt.getText().toString();
        if(!TextUtils.isEmpty(mWeight)){
            if(StringUtil.getFloat(mWeight) > 300.0f || StringUtil.getFloat(mWeight) < 20.0f){
                CDialog.showDlg(getContext(), getString(R.string.weight_range));
                return;
            }
        }

        final String mWeightBodyRate = mWeightBodyRateEt.getText().toString();
        if(!TextUtils.isEmpty(mWeightBodyRate)){
            if(StringUtil.getFloat(mWeightBodyRate) > 50.0f){
                CDialog.showDlg(getContext(), getString(R.string.weight_fat_range));
                return;
            }
        }

        final String mWeightTarget = mWeightTargetEt.getText().toString();

        final String mWeightinner = mWeightinnerVolumeEt.getText().toString();

        final String mWeightWater = mWeightWaterVolumeEt.getText().toString();

        final String mWeightMuscle = mWeightMuscleVolumeEt.getText().toString();

        CDialog.showDlg(getContext(), getString(R.string.weight_regist), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doInputWeight(regDate, timeStr, mWeight, mWeightTarget, mWeightBodyRate, mWeightinner, mWeightWater, mWeightMuscle);
            }
        }, null);
    }

    private void doInputWeight(String regDate, String timeStr, final String mWeight, final String mWeightTarget, final String mWeightBodyRate, String mWeightinner, String mWeightWater, String mWeightMuscle ) {

        regDate += timeStr;

        String weightTarget = mWeightTarget;
        if(TextUtils.isEmpty(weightTarget)){
            Tr_login login = Define.getInstance().getLoginInfo();
            weightTarget = login.mber_bdwgh_goal;
        }

        SparseArray<WeightModel> array = new SparseArray<>();
        WeightModel dataModel = new WeightModel();
        dataModel.setIdx(getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
        dataModel.setWeight(StringUtil.getFloatVal(mWeight));                 // 체중
        dataModel.setFat(StringUtil.getFloatVal(mWeightBodyRate));      // 체지방률
        dataModel.setObesity(StringUtil.getFloatVal(mWeightinner));     // 내장지방
        dataModel.setBodyWater(StringUtil.getFloatVal(mWeightWater));   // 수분
        dataModel.setMuscle(StringUtil.getFloatVal(mWeightMuscle));     // 근육
        dataModel.setBdwgh_goal(StringUtil.getFloatVal(weightTarget));     // 근육
        dataModel.setBmr(0);
        dataModel.setBone(0);
        dataModel.setHeartRate(0);
        dataModel.setRegType("U");
        dataModel.setRegDate(regDate);

        array.append(0, dataModel);

        if(StringUtil.getFloatVal(mWeight) > 0.f){
            new DeviceDataUtil().uploadWeight(WeightInputFragment.this, dataModel, new BluetoothManager.IBluetoothResult() {
                @Override
                public void onResult(boolean isSuccess) {
                    if (isSuccess) {
                        CDialog.showDlg(getContext(), getString(R.string.regist_success), new CDialog.DismissListener() {
                            @Override
                            public void onDissmiss() {

                                Tr_login login = Define.getInstance().getLoginInfo();

                                if(!mWeightTargetEt.getText().toString().isEmpty()){
                                    login.mber_bdwgh_goal = mWeightTargetEt.getText().toString();
                                    Define.getInstance().setLoginInfo(login);
                                }

                                DBHelper helper = new DBHelper(getContext());
                                DBHelperWeight weightDb = helper.getWeightDb();
                                DBHelperWeight.WeightStaticData bottomData = weightDb.getResultStatic();
                                if(!bottomData.getWeight().isEmpty()){
                                    login.mber_bdwgh_app = bottomData.getWeight();

                                    int sex             = StringUtil.getIntVal(login.mber_sex);
                                    float weight        = StringUtil.getFloatVal(login.mber_bdwgh_app);
                                    float height        = StringUtil.getFloatVal(login.mber_height);
                                    int calory          = StringUtil.getIntVal(login.goal_mvm_calory);
                                    int step = StringUtil.getIntVal(getStepTargetCalulator(sex, height, weight, calory));
                                    setStepTarget(0, step);

                                    Define.getInstance().setLoginInfo(login);
                                }

                                mDateTv.setText("");
                                mDateTv.setTag("");
                                mTimeTv.setText("");
                                mTimeTv.setTag("");
                                mWeightEt.setText("");
                                mWeightTargetEt.setText("");
                                mWeightBodyRateEt.setText("");
                                mWeightinnerVolumeEt.setText("");
                                mWeightWaterVolumeEt.setText("");
                                mWeightMuscleVolumeEt.setText("");
                                onBackPressed();
                            }
                        });
                    } else {
                        CDialog.showDlg(getContext(), getString(R.string.text_regist_fail));
                    }
                }
            });
        }else{
            new DeviceDataUtil().uploadTargetWeight(WeightInputFragment.this, dataModel, new BluetoothManager.IBluetoothResult() {
                @Override
                public void onResult(boolean isSuccess) {
                    if (isSuccess) {
                        CDialog.showDlg(getContext(), getString(R.string.regist_success), new CDialog.DismissListener() {
                            @Override
                            public void onDissmiss() {

                                Tr_login login = Define.getInstance().getLoginInfo();

                                if(!mWeightTargetEt.getText().toString().isEmpty()){
                                    login.mber_bdwgh_goal = mWeightTargetEt.getText().toString();
                                    Define.getInstance().setLoginInfo(login);
                                }

                                mDateTv.setText("");
                                mDateTv.setTag("");
                                mTimeTv.setText("");
                                mTimeTv.setTag("");
                                mWeightEt.setText("");
                                mWeightTargetEt.setText("");
                                mWeightBodyRateEt.setText("");
                                mWeightinnerVolumeEt.setText("");
                                mWeightWaterVolumeEt.setText("");
                                mWeightMuscleVolumeEt.setText("");

                                onBackPressed();
                            }
                        });
                    } else {
                        CDialog.showDlg(getContext(), getString(R.string.text_regist_fail));
                    }
                }
            });
        }


    }


    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {

            if (isValidDate() && isValidTime() && (isValidWeightEt() || isValidWeightTargetEt())) {
                mWeightInputSavebtn.setEnabled(true);
            } else {
                mWeightInputSavebtn.setEnabled(false);
            }
        }
    };

    private boolean isValidWeightTargetEt() {
        String text = mWeightTargetEt.getText().toString();
        return TextUtils.isEmpty(text) == false;
    }

    private boolean isValidWeightEt() {
        String text = mWeightEt.getText().toString();
        return TextUtils.isEmpty(text) == false;
    }

    private boolean isValidDate() {
        String text = mDateTv.getText().toString();
        return TextUtils.isEmpty(text) == false;
    }

    private boolean isValidTime() {
        String text = mTimeTv.getText().toString();
        return TextUtils.isEmpty(text) == false;
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
}