package com.greencross.greencare.sample;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.greencross.greencare.R;
import com.greencross.greencare.alram.BootReceiver;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.DummyActivity;
import com.greencross.greencare.bluetooth.fragment.BluetoothMainFragment;
import com.greencross.greencare.chartview.food.RadarChartFragment;
import com.greencross.greencare.component.CDatePicker;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.component.swipeListview.SwipteMenuFragment;
import com.greencross.greencare.database.util.DBBackupManager;
import com.greencross.greencare.food.FoodManageFragment;
import com.greencross.greencare.main.MainFragment;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_get_infomation;
import com.greencross.greencare.pressure.PressureInputFragment;
import com.greencross.greencare.pressure.PressureManageFragment;
import com.greencross.greencare.pressure.PressureMedInputFragment;
import com.greencross.greencare.setting.SettingFragment;
import com.greencross.greencare.step.StepInputFragment;
import com.greencross.greencare.step.StepManageFragment;
import com.greencross.greencare.sugar.SugarInputFragment;
import com.greencross.greencare.sugar.SugarManageFragment;
import com.greencross.greencare.sugar.SugarMedInputFragment;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.ViewUtil;
import com.greencross.greencare.water.WaterInputFragment;
import com.greencross.greencare.water.WaterManageFragment;
import com.greencross.greencare.weight.WeightInputFragment;
import com.greencross.greencare.weight.WeightManageFragment;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static java.text.DateFormat.getDateTimeInstance;

/**
 * Created by MrsWin on 2017-03-01.
 */

public class SampleFragment extends BaseFragment {
    public static String SAMPLE_BACK_DATA = "SAMPLE_BACK_DATA";

    public static Fragment newInstance() {
        SampleFragment fragment = new SampleFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sample_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.sample_time_picker).setOnClickListener(mClickListener);
        view.findViewById(R.id.sample_date_picker).setOnClickListener(mClickListener);
        view.findViewById(R.id.sample_main_card_view).setOnClickListener(mClickListener);
        view.findViewById(R.id.sample_dummy_activity).setOnClickListener(mClickListener);
        view.findViewById(R.id.sample_dummy_fragment).setOnClickListener(mClickListener);
        view.findViewById(R.id.sample_draw_view).setOnClickListener(mClickListener);
        view.findViewById(R.id.sample_draw_view2).setOnClickListener(mClickListener);

        view.findViewById(R.id.sample_radar_chart).setOnClickListener(mClickListener);
        view.findViewById(R.id.sample_swipe_list_view).setOnClickListener(mClickListener);
        view.findViewById(R.id.sample_camera_exam).setOnClickListener(mClickListener);
        view.findViewById(R.id.sample_sugar_manage).setOnClickListener(mClickListener);
        view.findViewById(R.id.sample_alert).setOnClickListener(mClickListener);
        view.findViewById(R.id.sample_test_connection).setOnClickListener(mClickListener);

        view.findViewById(R.id.input_walk).setOnClickListener(mClickListener);
        view.findViewById(R.id.input_sugar).setOnClickListener(mClickListener);
        view.findViewById(R.id.input_sugar_medi).setOnClickListener(mClickListener);
        view.findViewById(R.id.input_pressure).setOnClickListener(mClickListener);
        view.findViewById(R.id.input_pressure_medi).setOnClickListener(mClickListener);
        view.findViewById(R.id.input_weight).setOnClickListener(mClickListener);
        view.findViewById(R.id.input_water).setOnClickListener(mClickListener);
        view.findViewById(R.id.btn_bluetooth).setOnClickListener(mClickListener);

        view.findViewById(R.id.main_walk).setOnClickListener(mClickListener);
        view.findViewById(R.id.main_sugar).setOnClickListener(mClickListener);
        view.findViewById(R.id.main_pressure).setOnClickListener(mClickListener);
        view.findViewById(R.id.main_weight).setOnClickListener(mClickListener);
        view.findViewById(R.id.main_water).setOnClickListener(mClickListener);
        view.findViewById(R.id.main_food).setOnClickListener(mClickListener);
        view.findViewById(R.id.main_setting).setOnClickListener(mClickListener);

        view.findViewById(R.id.db_export_button).setOnClickListener(mClickListener);
        view.findViewById(R.id.db_import_button).setOnClickListener(mClickListener);
        view.findViewById(R.id.test_alram_button).setOnClickListener(mClickListener);

        /** font Typeface 적용 */
        TextView typeTextView = (TextView)view.findViewById(R.id.sample_type_textview);
        EditText typeEditText = (EditText)view.findViewById(R.id.sample_type_edittext);
        ViewUtil.setTypefaceNotoSansKRBold(getContext(), typeTextView);
        ViewUtil.setTypefaceNotoSansKRBold(getContext(), typeEditText);

    }

    /**
     * 액션바 세팅
     */
    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.text_login));
    }


    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();

            if (vId == R.id.sample_time_picker) {
                showTimePicker();
            } else if (vId == R.id.sample_date_picker) {
                showDatePicker(v);
            } else if (vId == R.id.sample_dummy_activity) {
                Bundle bundle = new Bundle();
                DummyActivity.startActivity(getActivity(), SugarInputFragment.class, bundle);
            } else if (vId == R.id.sample_dummy_fragment) {
                movePage(SugarInputFragment.newInstance());
            } else if (vId == R.id.sample_draw_view) {
                movePage(PathEffectsFragment.newInstance());
            } else if (vId == R.id.sample_draw_view2) {
                movePage(DrawPathFragment.newInstance());
            } else if (vId == R.id.sample_radar_chart) {
                movePage(RadarChartFragment.newInstance());
            } else if (vId == R.id.sample_swipe_list_view) {
                movePage(SwipteMenuFragment.newInstance());
            } else if (vId == R.id.sample_camera_exam) {
                movePage(CameraExamFragment.newInstance());
            } else if (vId == R.id.sample_sugar_manage) {
                movePage(SugarManageFragment.newInstance());
            } else if (vId == R.id.sample_alert) {
                // 클릭 리스너 연결
                CDialog.showDlg(getContext(), "메시지", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "makeText", Toast.LENGTH_SHORT).show();
                    }
                });
            }
             else if (vId == R.id.sample_test_connection) {
                Tr_get_infomation.RequestData data = new Tr_get_infomation.RequestData();
                data.insures_code = "300";

                getData(getContext(), Tr_get_infomation.class, data, new ApiData.IStep() {
                    @Override
                    public void next(Object obj) {

                        if (obj instanceof Tr_get_infomation) {
                            Tr_get_infomation data = (Tr_get_infomation)obj;

                            CDialog.showDlg(getContext(), obj.toString());
                        }

                    }
                });


            } else if (vId == R.id.db_import_button) {
                Toast.makeText(getContext(), "db_import_button", Toast.LENGTH_SHORT).show();
                new DBBackupManager().importDB(getContext());
            } else if (vId == R.id.db_export_button) {
                Toast.makeText(getContext(), "db_export_button", Toast.LENGTH_SHORT).show();
                new DBBackupManager().exportDB(getContext());
            } else if (vId == R.id.input_walk) {
                //등록
                Bundle bundle = new Bundle();
                DummyActivity.startActivityForResult(SampleFragment.this, 999, StepInputFragment.class, bundle);
            } else if (vId == R.id.input_sugar) {
                Bundle bundle = new Bundle();
                DummyActivity.startActivity(getActivity(), SugarInputFragment.class, bundle);
            } else if (vId == R.id.input_sugar_medi) {
                Bundle bundle = new Bundle();
                DummyActivity.startActivity(getActivity(), SugarMedInputFragment.class, bundle);
            } else if (vId == R.id.input_pressure) {
                Bundle bundle = new Bundle();
                DummyActivity.startActivity(getActivity(), PressureInputFragment.class, bundle);
            } else if (vId == R.id.input_pressure_medi) {
                Bundle bundle = new Bundle();
                DummyActivity.startActivity(getActivity(), PressureMedInputFragment.class, bundle);
            } else if (vId == R.id.input_weight) {
                Bundle bundle = new Bundle();
                DummyActivity.startActivity(getActivity(), WeightInputFragment.class, bundle);
            } else if (vId == R.id.input_water) {
                Bundle bundle = new Bundle();
                DummyActivity.startActivity(getActivity(), WaterInputFragment.class, bundle);
            } else if (vId == R.id.btn_bluetooth) {
                Intent intent = new Intent(getActivity(), BluetoothMainFragment.class);
                startActivity(intent);
            //메인이동
            } else if (vId == R.id.sample_main_card_view) {
                movePage(MainFragment.newInstance());
            } else if (vId == R.id.main_walk) {

                movePage(StepManageFragment.newInstance());
            }else if (vId == R.id.main_sugar) {

                movePage(SugarManageFragment.newInstance());
            }else if (vId == R.id.main_pressure) {

                movePage(PressureManageFragment.newInstance());
            }else if (vId == R.id.main_weight) {

                movePage(WeightManageFragment.newInstance());
            }else if (vId == R.id.main_water) {

                movePage(WaterManageFragment.newInstance());
            }else if (vId == R.id.main_food) {

                movePage(FoodManageFragment.newInstance());
            }else if(vId == R.id.main_setting) {

                movePage(SettingFragment.newInstance());
            } else if (vId == R.id.test_alram_button) {
                int A_DAY = 1000 * 60 * 60 * 24 * 1;
                PendingIntent[] sender = new PendingIntent[2];
                for(int i = 0 ; i < 2 ; i++){
                    Intent BootIntent = new Intent(getActivity(), BootReceiver.class);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    if(i == 0){
                        calendar.set(calendar.get(Calendar.YEAR)
                                , calendar.get(Calendar.MONTH)
                                , calendar.get(Calendar.DATE), 0, 0, 0);
                    }else if(i == 1){
                        calendar.set(calendar.get(Calendar.YEAR)
                                , calendar.get(Calendar.MONTH)
                                , calendar.get(Calendar.DATE), 9, 0, 0);
                    }

                    BootIntent.setType(Integer.toString(i));

                    sender[i] = PendingIntent.getBroadcast(getActivity(), 0, BootIntent, 0);
                    AlarmManager mManager;
                    mManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);

                    mManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), A_DAY, sender[i]);

                    DateFormat dateFormat = getDateTimeInstance();
                    Toast.makeText(getContext(), "알람시작:"+dateFormat.format(calendar.getTimeInMillis())+" intent type:"+BootIntent.getType(), Toast.LENGTH_SHORT).show();

                    System.out.println("intent type : "+BootIntent.getType()+" calendar.getTimeInMillis() : "+calendar.getTimeInMillis() +" 알람시작 : "+dateFormat.format(calendar.getTimeInMillis()));
                    calendar.add(Calendar.DATE, 1);
                    System.out.println("intent type : "+BootIntent.getType()+" calendar.getTimeInMillis() : "+calendar.getTimeInMillis() +" 알람시작 : "+dateFormat.format(calendar.getTimeInMillis()));
                }

            }
        }
    };


    /**
     * 시간 Picer 표시
     */
    private void showTimePicker() {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        Date now = new Date();
        cal.setTime(now);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(getContext(), listener, hour, minute, false);
        dialog.show();
    }

    /**
     * 시간 피커 완료
     */
    private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // 설정버튼 눌렀을 때
            Toast.makeText(getContext(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();
        }
    };

    private void showDatePicker(View v) {
        GregorianCalendar calendar = new GregorianCalendar();
        String birth = "2017";//mBrithEt.getText().toString().trim();
        String[] birthSpl = birth.split("\\.");

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (birthSpl.length == 3) {
            year = Integer.parseInt("".equals(birthSpl[0]) ? "0" : birthSpl[0].trim());
            month = Integer.parseInt("".equals(birthSpl[1]) ? "0" : birthSpl[1].trim()) - 1;
            day = Integer.parseInt("".equals(birthSpl[2]) ? "0" : birthSpl[2].trim());
        }

        new CDatePicker(getContext(), dateSetListener, year, month, day).show();
    }
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String msg = String.format("%d. %d. %d", year, monthOfYear + 1, dayOfMonth);

            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }

    };


    @Override
    public void onResume() {
        super.onResume();
        // 이전 플래그먼트에서 데이터 받기
        Bundle bundle = getBackData();
        String backString = bundle.getString(SAMPLE_BACK_DATA);
        Logger.i("", "backString="+backString);
    }

    @Override
    public void onBackPressed() {
        super.finishStep();
    }
}
