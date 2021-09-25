package com.greencross.greencare.water;

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
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.bluetooth.manager.DeviceDataUtil;
import com.greencross.greencare.bluetooth.model.WaterModel;
import com.greencross.greencare.component.CDatePicker;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.main.BluetoothManager;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;
import com.greencross.greencare.util.ViewUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.greencross.greencare.R.id.water_input_save_btn;
import static com.greencross.greencare.util.CDateUtil.getForamtyyMMddHHmmssSS;
import static com.greencross.greencare.util.CDateUtil.getForamtyyyyMMddHHmm;

/**
 * Created by insystemscompany on 2017. 2. 28..
 */

public class WaterInputFragment extends BaseFragment {

    private static final String TAG = WaterInputFragment.class.getSimpleName();
    private TextView mDateTv, mTimeTv;

    private EditText mWaterEt, mTargetWaterEt;
    private Button submitBtn;

    private int cal_year;
    private int cal_month;
    private int cal_day;
    private int cal_hour;
    private int cal_min;

    public static Fragment newInstance() {
        WaterInputFragment fragment = new WaterInputFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_water_input, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDateTv = (TextView) view.findViewById(R.id.water_input_date_textview);
        mTimeTv = (TextView) view.findViewById(R.id.water_input_time_textview);

        mWaterEt        = (EditText) view.findViewById(R.id.water_input_edittext);
        mTargetWaterEt  = (EditText) view.findViewById(R.id.water_target_input_edittext);

        String now_time = getForamtyyyyMMddHHmm(new Date(System.currentTimeMillis()));
        java.util.Calendar cal = CDateUtil.getCalendar_yyyy_MM_dd_HH_mm(now_time);
        cal_year    = cal.get(Calendar.YEAR);
        cal_month   = cal.get(Calendar.MONTH);
        cal_day     = cal.get(Calendar.DAY_OF_MONTH);
        cal_hour    = cal.get(Calendar.HOUR_OF_DAY);
        cal_min     = cal.get(Calendar.MINUTE);

        mDateTvSet(cal_year, cal_month, cal_day);
        mTimeTvSet(cal_hour, cal_min);

        mWaterEt.setHint("");
        Tr_login login = Define.getInstance().getLoginInfo();
        if (!login.goal_water_ntkqy.toString().isEmpty()) {
            mTargetWaterEt.setHint(login.goal_water_ntkqy);
        }

        /** font Typeface 적용 */
        submitBtn = (Button) view.findViewById(water_input_save_btn);
        ViewUtil.setTypefaceNotoSansKRBold(getContext(), submitBtn);
        submitBtn.setOnClickListener(mClickListener);

        mDateTv.addTextChangedListener(watcher);
        mTimeTv.addTextChangedListener(watcher);
        mWaterEt.addTextChangedListener(watcher);
        mTargetWaterEt.addTextChangedListener(watcher);

        mDateTv.setOnTouchListener(mTouchListener);
        mTimeTv.setOnTouchListener(mTouchListener);
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.text_water));
    }

    View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int vId = v.getId();
                if (vId == R.id.water_input_date_textview) {
                    showDatePicker(v);
                } else if (vId == R.id.water_input_time_textview) {
                    showTimePicker();
                }
            }
            return false;
        }
    };

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.water_input_save_btn) {
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

                final String water = mWaterEt.getText().toString();
                if (TextUtils.isEmpty(water)) {
                    CDialog.showDlg(getContext(), getString(R.string.water_input_error));
                    return;
                }

                CDialog.showDlg(getContext(), getString(R.string.water_regist), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doInputWater(regDate, timeStr, water);
                    }
                }, null);
            }
        }
    };

    private boolean DateTimeCheck(String type, int pram1, int pram2, int pram3) {
        Calendar cal = Calendar.getInstance();

        if (type.equals("D")) {
            cal.set(Calendar.YEAR, pram1);
            cal.set(Calendar.MONTH, pram2);
            cal.set(Calendar.DAY_OF_MONTH, pram3);
            cal.set(Calendar.HOUR_OF_DAY, cal_hour);
            cal.set(Calendar.MINUTE, cal_min);

            if (cal.getTimeInMillis() > System.currentTimeMillis()) {
                CDialog.showDlg(getContext(), getString(R.string.message_nowtime_over), new CDialog.DismissListener() {
                    @Override
                    public void onDissmiss() {

                    }
                });
                return false;
            } else {
                return true;
            }
        } else {
            cal.set(Calendar.YEAR, cal_year);
            cal.set(Calendar.MONTH, cal_month);
            cal.set(Calendar.DAY_OF_MONTH, cal_day);
            cal.set(Calendar.HOUR_OF_DAY, pram1);
            cal.set(Calendar.MINUTE, pram2);

            if (cal.getTimeInMillis() > System.currentTimeMillis()) {
                CDialog.showDlg(getContext(), getString(R.string.message_nowtime_over), new CDialog.DismissListener() {
                    @Override
                    public void onDissmiss() {

                    }
                });

                return false;
            } else {
                return true;
            }
        }
    }

    private void showDatePicker(View v) {
        GregorianCalendar calendar = new GregorianCalendar();

        int year    = calendar.get(Calendar.YEAR);
        int month   = calendar.get(Calendar.MONTH);
        int day     = calendar.get(Calendar.DAY_OF_MONTH);

        String date = mDateTv.getTag().toString();
        if (TextUtils.isEmpty(date) == false) {
            year    = StringUtil.getIntVal(date.substring(0, 4));
            month   = StringUtil.getIntVal(date.substring(4, 6)) - 1;
            day     = StringUtil.getIntVal(date.substring(6, 8));
        }

        new CDatePicker(getContext(), dateSetListener, year, month, day, false).show();
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (DateTimeCheck("D", year, monthOfYear, dayOfMonth)) {
                cal_year    = year;
                cal_month   = monthOfYear;
                cal_day     = dayOfMonth;
                mDateTvSet(year, monthOfYear, dayOfMonth);
            }
        }

    };

    private void mDateTvSet(int year, int monthOfYear, int dayOfMonth) {
        String msg      = String.format("%d.%d.%d", year, monthOfYear + 1, dayOfMonth);
        String tagMsg   = String.format("%d%02d%02d", year, monthOfYear + 1, dayOfMonth);
        Calendar cal    = Calendar.getInstance();

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear + 1);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        mDateTv.setText(msg + " " + CDateUtil.getDateToWeek(tagMsg) + "요일");
        mDateTv.setTag(tagMsg);
    }

    private void showTimePicker() {
        Calendar cal    = Calendar.getInstance(Locale.KOREA);
        Date now        = new Date();
        cal.setTime(now);

        int hour    = cal.get(Calendar.HOUR_OF_DAY);
        int minute  = cal.get(Calendar.MINUTE);
        String time = mTimeTv.getTag().toString();
        if (TextUtils.isEmpty(time) == false) {
            hour    = StringUtil.getIntVal(time.substring(0, 2));
            minute  = StringUtil.getIntVal(time.substring(2, 4));

            Logger.i(TAG, "hour=" + hour + ", minute=" + minute);
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
            if (DateTimeCheck("S", hourOfDay, minute, 0)) {
                cal_hour    = hourOfDay;
                cal_min     = minute;
                mTimeTvSet(hourOfDay, minute);
            }
        }
    };

    private void mTimeTvSet(int hourOfDay, int minute) {
        // 설정버튼 눌렀을 때
        String amPm = getString(R.string.text_morning);
        int hour = hourOfDay;
        if (hourOfDay > 11) {
            amPm = getString(R.string.text_afternoon);
            if (hourOfDay >= 13)
                hour -= 12;
        } else {
            hour = hour == 0 ? 12 : hour;
        }
        String tagMsg   = String.format("%02d%02d", hourOfDay, minute);
        String timeStr  = String.format("%02d:%02d", hour, minute);
        mTimeTv.setText(amPm + " " + timeStr);
        mTimeTv.setTag(tagMsg);
    }


    private void doInputWater(String regDate, String timeStr, String amount) {

        regDate += timeStr;

        SparseArray<WaterModel> array = new SparseArray<>();
        WaterModel dataModel = new WaterModel();
        dataModel.setIndexTime(getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
        dataModel.setAmount(amount);
        dataModel.setRegtype("U");
        dataModel.setRegTime(regDate);

        array.append(0, dataModel);
        new DeviceDataUtil().uploadWaterData(WaterInputFragment.this, array, mTargetWaterEt.getText().toString(), new BluetoothManager.IBluetoothResult() {
            @Override
            public void onResult(boolean isSuccess) {
                if (isSuccess) {
                    CDialog.showDlg(getContext(), getString(R.string.regist_success), new CDialog.DismissListener() {
                        @Override
                        public void onDissmiss() {

                            if (!mTargetWaterEt.getText().toString().isEmpty()) {
                                Tr_login login = Define.getInstance().getLoginInfo();
                                login.goal_water_ntkqy = mTargetWaterEt.getText().toString();
                                Define.getInstance().setLoginInfo(login);
                            }

                            mDateTv.setText("");
                            mDateTv.setTag("");
                            mTimeTv.setText("");
                            mTimeTv.setTag("");
                            mWaterEt.setText("");
                            mTargetWaterEt.setText("");
                            onBackPressed();

                        }
                    });
                } else {
                    CDialog.showDlg(getContext(), getString(R.string.text_regist_fail));
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

            if (isValidDate() && isValidTime() && isValidWater()) {
                submitBtn.setEnabled(true);
            } else {
                submitBtn.setEnabled(false);
            }
        }
    };

    private boolean isValidWater() {
        String text = mWaterEt.getText().toString();
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
}