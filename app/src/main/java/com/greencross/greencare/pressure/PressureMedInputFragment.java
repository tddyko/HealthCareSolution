package com.greencross.greencare.pressure;

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
import com.greencross.greencare.bluetooth.manager.DeviceDataUtil;
import com.greencross.greencare.bluetooth.model.PressureModel;
import com.greencross.greencare.component.CDatePicker;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.main.BluetoothManager;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;
import com.greencross.greencare.util.ViewUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.greencross.greencare.util.CDateUtil.getForamtyyyyMMddHHmm;

/**
 * Created by insystemscompany on 2017. 2. 28..
 */

public class PressureMedInputFragment extends BaseFragment implements IBaseFragment {

    private static final String TAG = PressureMedInputFragment.class.getSimpleName();

    private TextView mDateTv, mTimeTv;
    private EditText mDrugEt;
    private Button saveBtn;

    public static Fragment newInstance() {
        PressureMedInputFragment fragment = new PressureMedInputFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_pressure_med_input, container, false);

        /** font Typeface 적용 */
        saveBtn = (Button)view.findViewById(R.id.pressure_med_input_save_btn);
        ViewUtil.setTypefaceNotoSansKRBold(getContext(), saveBtn);

        mDateTv = (TextView) view.findViewById(R.id.pressure_med_input_date_textview);
        mTimeTv = (TextView) view.findViewById(R.id.pressure_med_input_time_textview);

        mDrugEt = (EditText) view.findViewById(R.id.pressure_drug_input_edittext);

        String now_time = getForamtyyyyMMddHHmm(new Date(System.currentTimeMillis()));
        java.util.Calendar cal = CDateUtil.getCalendar_yyyy_MM_dd_HH_mm(now_time);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        mDateTvSet(year, month, day);
        mTimeTvSet(hour, min);

        saveBtn.setEnabled(true);

        mDateTv.addTextChangedListener(watcher);
        mTimeTv.addTextChangedListener(watcher);

        saveBtn.setOnClickListener(mClickListener);
        mDateTv.setOnTouchListener(mTouchListener);
        mTimeTv.setOnTouchListener(mTouchListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle( getString(R.string.text_medi_info_input));
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.pressure_med_input_save_btn) {
                final  String regDate = mDateTv.getTag().toString();
                if (TextUtils.isEmpty(regDate)) {
                    CDialog.showDlg(getContext(), getString(R.string.date_input_error));
                    return;
                }

                final String timeStr = mTimeTv.getTag().toString();
                if (TextUtils.isEmpty(timeStr)) {
                    CDialog.showDlg(getContext(), getString(R.string.time_input_error));
                    return;
                }

                CDialog.showDlg(getContext(), getString(R.string.medi_info_regist), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doInputPressureDrug(regDate, timeStr);
                    }
                }, null);
            }
        }
    };

    View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int vId = v.getId();
                if (vId == R.id.pressure_med_input_date_textview) {
                    showDatePicker(v);
                } else if (vId == R.id.pressure_med_input_time_textview) {
                    showTimePicker();
                }
            }
            return false;
        }
    };

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

            mDateTvSet(year, monthOfYear, dayOfMonth);
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

            mTimeTvSet(hourOfDay, minute);
        }
    };

    private void mTimeTvSet(int hourOfDay, int minute){
        // 설정버튼 눌렀을 때
        String amPm = "오전";
        int hour = hourOfDay;
        if (hourOfDay > 11) {
            amPm = "오후";
            if (hourOfDay >= 13)
                hour -= 12;
        } else {
            hour = hour == 0 ? 12 : hour;
        }
        String tagMsg = String.format("%02d%02d", hourOfDay, minute);
        String timeStr = String.format("%02d:%02d", hour, minute);
        mTimeTv.setText(amPm + " " + timeStr);
        mTimeTv.setTag(tagMsg);
    }


    /**
     * 혈압 데이터 올리기
     * @param regDate
     * @param timeStr
     */
    private void doInputPressureDrug(String regDate, String timeStr) {

        regDate += timeStr;

        String medicen = mDrugEt.getText().toString();
        if (TextUtils.isEmpty(medicen)) {
            medicen = " ";
        }

        SparseArray<PressureModel> array = new SparseArray<>();
        PressureModel dataModel = new PressureModel();
        dataModel.setIdx(CDateUtil.getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
        dataModel.setArterialPressure(0);    // 동맥압력
        dataModel.setDiastolicPressure(0);   //이완기혈압
        dataModel.setPulseRate(0);           // 맥박속도
        dataModel.setSystolicPressure(0);    // 수축기압력
        dataModel.setDrugname(medicen); // 약이름
        dataModel.setRegtype("U"); // D:디바이스 U:직접입력
        dataModel.setRegdate(regDate);

        array.append(0, dataModel);
        new DeviceDataUtil().uploadPresure(PressureMedInputFragment.this, dataModel, new BluetoothManager.IBluetoothResult() {
            @Override
            public void onResult(boolean isSuccess) {
                if (isSuccess) {
                    CDialog.showDlg(getContext(), getString(R.string.text_regist_success), new CDialog.DismissListener() {
                        @Override
                        public void onDissmiss() {
                            mDateTv.setText("");
                            mDateTv.setTag("");
                            mTimeTv.setText("");
                            mTimeTv.setTag("");
                            mDrugEt.setText("");
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
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {

            if (isValidDate() && isValidTime()) {
                saveBtn.setEnabled(true);
            } else {
                saveBtn.setEnabled(false);
            }
        }
    };

    private boolean isValidDate() {
        String text = mDateTv.getText().toString();
        return TextUtils.isEmpty(text) == false;
    }

    private boolean isValidTime() {
        String text = mTimeTv.getText().toString();
        return TextUtils.isEmpty(text) == false;
    }
}