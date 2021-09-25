package com.greencross.greencare.pressure;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.component.swipeListview.SwipeMenu;
import com.greencross.greencare.component.swipeListview.SwipeMenuCreator;
import com.greencross.greencare.component.swipeListview.SwipeMenuItem;
import com.greencross.greencare.component.swipeListview.SwipeMenuListView;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperPresure;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_brssr_info_del_data;
import com.greencross.greencare.network.tr.data.Tr_brssr_info_edit_data;
import com.greencross.greencare.network.tr.data.Tr_get_hedctdata;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.greencross.greencare.component.CDialog.showDlg;

/**
 * Created by MrsWin on 2017-04-15.
 */

public class PressureSwipeListView {
    private static final String TAG = PressureSwipeListView.class.getSimpleName();
    private PressureSwipeListView.AppAdapter mAdapter;
    private List<Tr_get_hedctdata.DataList> mSwipeMenuDatas = new ArrayList<>();
    private BaseFragment mBaseFragment;

    public PressureSwipeListView(View view, BaseFragment baseFragment) {
        mBaseFragment = baseFragment;
        SwipeMenuListView listView = (SwipeMenuListView) view.findViewById(R.id.history_listview);

        mAdapter = new AppAdapter();
        listView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                createMenu1(menu);
            }

            private void createMenu1(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(mBaseFragment.getContext());
                item1.setBackground(new ColorDrawable(Color.BLACK));//new ColorDrawable(Color.rgb(0xE5, 0x18, 0x5E)));
                item1.setWidth(dp2px(60));
                item1.setIcon(R.drawable.btn_swipe_edit);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(mBaseFragment.getContext());
                item2.setBackground((new ColorDrawable(Color.RED)));//(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                item2.setWidth(dp2px(60));
                item2.setIcon(R.drawable.btn_swipe_del);
                menu.addMenuItem(item2);
            }

        };
        // set creator
        listView.setMenuCreator(creator);

        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:

                        Tr_get_hedctdata.DataList data = (Tr_get_hedctdata.DataList) mAdapter.getItem(position);
                        if (data.regtype.equals("U")) {
                            // 직정입력한 데이터만 수정할 수 있음.
                            new PressureSwipeListView.showModifiDlg(data);
                        } else {
                            // 장치에서 측정된 데이터는 수정할 수 없음.
                            String message = mBaseFragment.getContext().getString(R.string.text_alert_mesage_disable_edit);
                            CDialog.showDlg(mBaseFragment.getContext(), message);
                        }

                        break;
                    case 1:
                        // delete
                        String message = mBaseFragment.getContext().getString(R.string.text_alert_mesage_delete);
                        showDlg(mBaseFragment.getContext(), message, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Tr_get_hedctdata.DataList data = (Tr_get_hedctdata.DataList) mAdapter.getItem(position);

                                doDeleteData(position, data);
                            }
                        }, null);

                        break;
                }

                // false:Swipe 닫힘, true:Swipe안닫힘
                return true;
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                mBaseFragment.getContext().getResources().getDisplayMetrics());
    }

    public void getHistoryData() {
        mSwipeMenuDatas.clear();

        DBHelper helper = new DBHelper(mBaseFragment.getContext());
        DBHelperPresure db = helper.getPresureDb();
        //혈압 스와이프 데이터 가져오기 오류
        mSwipeMenuDatas.addAll(db.getResultAll(helper));
        mAdapter.notifyDataSetChanged();
    }

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSwipeMenuDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mSwipeMenuDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type
            return position % 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mBaseFragment.getContext(), R.layout.swipe_menu_history_item_view, null);
                new ViewHolder(convertView);
            }
            PressureSwipeListView.AppAdapter.ViewHolder holder = (PressureSwipeListView.AppAdapter.ViewHolder) convertView.getTag();
            Tr_get_hedctdata.DataList data = (Tr_get_hedctdata.DataList) getItem(position);

            String yyyyMMddhhss = CDateUtil.getRegDateFormat_yyyyMMdd_HHss(data.reg_de, ".", ":");

            if (StringUtil.getFloat(data.systolicPressure) == 0f && StringUtil.getFloat(data.diastolicPressure) == 0f) {
                holder.typeTv.setVisibility(View.GONE);
                holder.valueTv.setText("투약 (" + data.drugname + ")");
                holder.beforeAfterTv.setText("");
                holder.timeTv.setText(yyyyMMddhhss);
                holder.mldlTv.setVisibility(View.GONE);
            } else {
                holder.typeTv.setVisibility(View.VISIBLE);
                String value = data.systolicPressure + "/" + data.diastolicPressure;
                holder.typeTv.setText("D".equals(data.regtype) ? mBaseFragment.getContext().getString(R.string.text_device) : mBaseFragment.getContext().getString(R.string.text_direct_enter));
                holder.valueTv.setText(value);
                holder.beforeAfterTv.setText("");//"0".equals(data.before) ? mContext.getString(R.string.text_foodbefore) : mContext.getString(R.string.text_foodafter));
                holder.timeTv.setText(yyyyMMddhhss);
                holder.mldlTv.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        class ViewHolder {
            TextView timeTv;
            TextView beforeAfterTv;
            TextView valueTv;
            TextView typeTv;
            TextView mldlTv;

            public ViewHolder(View view) {
                timeTv = (TextView) view.findViewById(R.id.sugar_history_item_time_textview);
                beforeAfterTv = (TextView) view.findViewById(R.id.sugar_history_item_before_after_textview);
                valueTv = (TextView) view.findViewById(R.id.sugar_history_item_sugar_textview);
                typeTv = (TextView) view.findViewById(R.id.sugar_history_item_type_textview);
                mldlTv = (TextView) view.findViewById(R.id.sugar_history_item_mldl_textview);

                view.setTag(this);
            }
        }
    }

    /**
     * 혈압 삭제하기
     *
     * @param dataList
     */
    private void doDeleteData(final int position, final Tr_get_hedctdata.DataList dataList) {

        Tr_brssr_info_del_data inputData = new Tr_brssr_info_del_data();
        Tr_brssr_info_del_data.RequestData requestData = new Tr_brssr_info_del_data.RequestData();
        Tr_login login = Define.getInstance().getLoginInfo();

        requestData.mber_sn = login.mber_sn;
        requestData.ast_mass = inputData.getArray(dataList);

        mBaseFragment.getData(mBaseFragment.getContext(), inputData.getClass(), requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_brssr_info_del_data) {
                    Tr_brssr_info_del_data data = (Tr_brssr_info_del_data) obj;
                    if ("Y".equals(data.reg_yn)) {

                        DBHelper helper = new DBHelper(mBaseFragment.getContext());
                        DBHelperPresure presureDb = helper.getPresureDb();
                        presureDb.DeleteDb(dataList.idx);

                        mSwipeMenuDatas.remove(position);
                        mAdapter.notifyDataSetChanged();

                        CDialog.showDlg(mBaseFragment.getContext(), mBaseFragment.getContext().getString(R.string.delete_success));
                    }
                }
            }
        });
    }

    class showModifiDlg {
        private EditText mPressureSystolicEt;
        private EditText mPressureDiastolicEt;
        private TextView mDateTv;
        private TextView mTimeTv;
        private EditText mMedicenInputEt;
        private Button mSaveBtn;
        private String dataIdx;
        private LinearLayout mPressureLayout;
        private LinearLayout mMedicenLayout;

        private boolean mIsMedicenMode; // 투약정보 수정인지 여부

        /**
         * 수정 Dialog 띄우기
         */
        private showModifiDlg(final Tr_get_hedctdata.DataList data) {
            View modifyView = LayoutInflater.from(mBaseFragment.getContext()).inflate(R.layout.activity_pressure_input, null);
            mPressureSystolicEt = (EditText) modifyView.findViewById(R.id.systolic_pressure);
            mPressureDiastolicEt = (EditText) modifyView.findViewById(R.id.diastolic_pressure_edittext);
            mMedicenInputEt = (EditText) modifyView.findViewById(R.id.pressure_medicen_editext);
            mDateTv = (TextView) modifyView.findViewById(R.id.pressure_input_date_textview);
            mTimeTv = (TextView) modifyView.findViewById(R.id.pressure_input_time_textview);
            mPressureLayout = (LinearLayout) modifyView.findViewById(R.id.pressure_input_pressure_layout);
            mMedicenLayout = (LinearLayout) modifyView.findViewById(R.id.pressure_input_medicen_layout);

            mDateTv.setEnabled(false);
            mTimeTv.setEnabled(true);
            dataIdx = data.idx;

            mTimeTv.setOnTouchListener(mTouchListener);

            Calendar cal = CDateUtil.getCalendar_yyyy_MM_dd_HH_mm(data.reg_de);

            // 2017-01-01 금요일 형태로 표시
            String yyyy_mm_dd = CDateUtil.getFormattedString_yyyy_MM_dd(cal.getTimeInMillis());
            String hh_mm = CDateUtil.getFormattedString_hh_mm(cal.getTimeInMillis());
            // 2017-01-01 금요일 형태로 표시
            mDateTv.setText(yyyy_mm_dd + " " + CDateUtil.getDateToWeek(cal) + "요일");
            mDateTv.setTag(StringUtil.getIntString(yyyy_mm_dd));   // yyyyMMdd
            // 오후 1:00 형태로 표시
            mTimeTv.setText(CDateUtil.getAmPmString(cal.get(Calendar.HOUR_OF_DAY)) + " " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE));
            mTimeTv.setTag(StringUtil.getIntString(hh_mm));   // HHmm

            mIsMedicenMode = StringUtil.getIntVal(data.systolicPressure) <= 0 && StringUtil.getIntVal(data.diastolicPressure) <= 0;
            if (mIsMedicenMode) {

                if (data.drugname.toString().equals(" ")) {
                    data.drugname = "";
                }

                mPressureLayout.setVisibility(View.GONE);
                mMedicenLayout.setVisibility(View.VISIBLE);
                mMedicenInputEt.setText(data.drugname);

                if (data.drugname.toString().equals("")) {
                    data.drugname = " ";
                }
            } else {
                mPressureLayout.setVisibility(View.VISIBLE);
                mMedicenLayout.setVisibility(View.GONE);
                mPressureSystolicEt.setText("" + StringUtil.getIntVal(data.systolicPressure));
                mPressureDiastolicEt.setText("" + StringUtil.getIntVal(data.diastolicPressure));
            }

            final CDialog dlg = CDialog.showDlg(mBaseFragment.getContext(), modifyView);
            if (mIsMedicenMode)
                dlg.setTitle(mBaseFragment.getContext().getString(R.string.medi_modify));
            else
                dlg.setTitle(mBaseFragment.getContext().getString(R.string.presure_modify));
            mSaveBtn = (Button) modifyView.findViewById(R.id.pressure_input_save_btn);
            mSaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dlg.dismiss();

                    boolean isDrugMode = StringUtil.getFloat(data.sugar) == 0f && TextUtils.isEmpty(data.drugname) == false;

                    if (isDrugMode) {

                        if (mMedicenInputEt.getText().toString().isEmpty()) {
                            mMedicenInputEt.setText(" ");
                        }

                        data.drugname = mMedicenInputEt.getText().toString();
                    } else {
                        data.systolicPressure = mPressureSystolicEt.getText().toString();
                        data.diastolicPressure = mPressureDiastolicEt.getText().toString();
                        data.drugname = "";
                    }

                    String regDate = mDateTv.getTag().toString();
                    String timeStr = mTimeTv.getTag().toString();
                    regDate += timeStr;
                    data.reg_de = regDate;

                    doModifyData(data);
                }
            });

            mTimeTv.addTextChangedListener(watcher);
            mDateTv.addTextChangedListener(watcher);
            mPressureSystolicEt.addTextChangedListener(watcher);
            mPressureDiastolicEt.addTextChangedListener(watcher);
            if (isValidDate() && isValidTime() && isValidSystolic()) {
                mSaveBtn.setEnabled(true);
            } else {
                mSaveBtn.setEnabled(false);
            }
        }

        View.OnTouchListener mTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int vId = v.getId();
                    if (vId == R.id.pressure_input_time_textview) {
                        showTimePicker();
                    }
                }
                return false;
            }
        };

        /**
         * 시간 피커 완료
         */
        private void showTimePicker() {
            Calendar cal = Calendar.getInstance(Locale.KOREA);
            Date now = new Date();
            cal.setTime(now);

            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            String time = mTimeTv.getTag().toString();
            if (TextUtils.isEmpty(time) == false) {
                hour = StringUtil.getIntVal(time.substring(0, 2));
                minute = StringUtil.getIntVal(time.substring(2, 4));

                Logger.i(TAG, "hour=" + hour + ", minute=" + minute);
            }

            TimePickerDialog dialog = new TimePickerDialog(mBaseFragment.getContext(), listener, hour, minute, false);
            dialog.show();
        }

        private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mTimeTvSet(hourOfDay, minute);
            }
        };

        private void mTimeTvSet(int hourOfDay, int minute) {
            // 설정버튼 눌렀을 때
            String amPm = mBaseFragment.getContext().getString(R.string.text_morning);
            int hour = hourOfDay;
            if (hourOfDay > 11) {
                amPm = mBaseFragment.getContext().getString(R.string.text_afternoon);
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

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isValidDate() && isValidTime() && isValidSystolic()) {
                    mSaveBtn.setEnabled(true);
                } else {
                    mSaveBtn.setEnabled(false);
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

        private boolean isValidSystolic() {
            if (mIsMedicenMode) {
                return true;

            } else {
                String text = mPressureSystolicEt.getText().toString();
                String ttext = mPressureDiastolicEt.getText().toString();
                return TextUtils.isEmpty(text) == false && TextUtils.isEmpty(ttext) == false;
            }
        }


        /**
         * 혈압 수정하기
         *
         * @param dataList
         */
        private void doModifyData(final Tr_get_hedctdata.DataList dataList) {
            Tr_brssr_info_edit_data inputData = new Tr_brssr_info_edit_data();
            Tr_brssr_info_edit_data.RequestData requestData = new Tr_brssr_info_edit_data.RequestData();
            Tr_login login = Define.getInstance().getLoginInfo();

            requestData.mber_sn = login.mber_sn;
            requestData.ast_mass = inputData.getArray(dataList);

            mBaseFragment.getData(mBaseFragment.getContext(), inputData.getClass(), requestData, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (obj instanceof Tr_brssr_info_edit_data) {
                        Tr_brssr_info_edit_data data = (Tr_brssr_info_edit_data) obj;
                        if ("Y".equals(data.reg_yn)) {

                            DBHelper helper = new DBHelper(mBaseFragment.getContext());
                            DBHelperPresure presureDb = helper.getPresureDb();
                            presureDb.UpdateDb(dataIdx, mPressureSystolicEt.getText().toString(), mPressureDiastolicEt.getText().toString(), mMedicenInputEt.getText().toString(), dataList.reg_de);

                            getHistoryData();

                            CDialog.showDlg(mBaseFragment.getContext(), mBaseFragment.getContext().getString(R.string.modify_success));
                        }
                    }
                }
            });
        }
    }
}