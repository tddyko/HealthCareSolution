package com.greencross.greencare.water;

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
import com.greencross.greencare.database.DBHelperWater;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_get_hedctdata;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.network.tr.data.Tr_water_info_del_data;
import com.greencross.greencare.network.tr.data.Tr_water_info_edit_data;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.greencross.greencare.component.CDialog.showDlg;

/**
 * Created by bong_e on 2017. 4. 17..
 */

public class WaterSwipeListView {
    private static final String TAG = WaterSwipeListView.class.getSimpleName();
    private WaterSwipeListView.AppAdapter mAdapter;
    private List<Tr_get_hedctdata.DataList> mSwipeMenuDatas = new ArrayList<>();
    private BaseFragment mBaseFragment;

    public WaterSwipeListView(View view, BaseFragment baseFragment) {
        mBaseFragment               = baseFragment;
        SwipeMenuListView listView  = (SwipeMenuListView) view.findViewById(R.id.water_history_listview);

        mAdapter                    = new WaterSwipeListView.AppAdapter();
        listView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator    = new SwipeMenuCreator() {
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
                        new WaterSwipeListView.showModifiDlg(data);
                        break;
                    case 1:
                        // delete
                        String message                          = mBaseFragment.getContext().getString(R.string.text_alert_mesage_delete);
                        showDlg(mBaseFragment.getContext(), message, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Tr_get_hedctdata.DataList data  = (Tr_get_hedctdata.DataList) mAdapter.getItem(position);

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

        DBHelper helper         = new DBHelper(mBaseFragment.getContext());
        DBHelperWater waterDb   = helper.getWaterDb();
        mSwipeMenuDatas.addAll(waterDb.getResult());
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

        public String makeStringComma(String str) {
            if (str.length() == 0)
                return "";
            long value              = Long.parseLong(str);
            DecimalFormat format    = new DecimalFormat("###,###");
            return format.format(value);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mBaseFragment.getContext(), R.layout.swipe_menu_history_item_view, null);
                new WaterSwipeListView.AppAdapter.ViewHolder(convertView);
            }
            WaterSwipeListView.AppAdapter.ViewHolder holder = (WaterSwipeListView.AppAdapter.ViewHolder) convertView.getTag();
            Tr_get_hedctdata.DataList data = (Tr_get_hedctdata.DataList) getItem(position);

            Calendar cal        = CDateUtil.getCalendar_yyyy_MM_dd_HH_mm(data.reg_de);
            String yyyyMMddhhss = CDateUtil.getRegDateFormat_yyyyMMdd_HHss(data.reg_de, ".", ":");

            holder.typeTv.setText(mBaseFragment.getContext().getString(R.string.text_direct_enter));
            holder.amounTv.setText(makeStringComma(data.amount.replace(",", "")));
            holder.beforeAfterTv.setVisibility(View.GONE);
            holder.unitTv.setText(mBaseFragment.getContext().getString(R.string.ml));
            holder.timeTv.setText(yyyyMMddhhss);


            return convertView;
        }

        class ViewHolder {
            TextView timeTv;
            TextView beforeAfterTv;
            TextView amounTv;
            TextView typeTv;
            TextView unitTv;

            public ViewHolder(View view) {
                timeTv          = (TextView) view.findViewById(R.id.sugar_history_item_time_textview);
                beforeAfterTv   = (TextView) view.findViewById(R.id.sugar_history_item_before_after_textview);
                amounTv         = (TextView) view.findViewById(R.id.sugar_history_item_sugar_textview);
                typeTv          = (TextView) view.findViewById(R.id.sugar_history_item_type_textview);
                unitTv          = (TextView) view.findViewById(R.id.sugar_history_item_mldl_textview);

                view.setTag(this);
            }
        }
    }

    /**
     * 물 삭제하기
     *
     * @param dataList
     */
    private void doDeleteData(final int position, final Tr_get_hedctdata.DataList dataList) {

        Tr_water_info_del_data inputData                = new Tr_water_info_del_data();
        Tr_water_info_del_data.RequestData requestData  = new Tr_water_info_del_data.RequestData();
        Tr_login login                                  = Define.getInstance().getLoginInfo();

        requestData.mber_sn = login.mber_sn;
        requestData.ast_mass = inputData.getArray(dataList);

        mBaseFragment.getData(mBaseFragment.getContext(), inputData.getClass(), requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_water_info_del_data) {
                    Tr_water_info_del_data data = (Tr_water_info_del_data) obj;
                    if ("Y".equals(data.reg_yn)) {

                        DBHelper helper         = new DBHelper(mBaseFragment.getContext());
                        DBHelperWater waterDb   = helper.getWaterDb();
                        waterDb.DeleteDb(dataList.idx);

                        mSwipeMenuDatas.remove(position);
                        mAdapter.notifyDataSetChanged();

                        CDialog.showDlg(mBaseFragment.getContext(), mBaseFragment.getContext().getString(R.string.delete_success));
                    }
                }
            }
        });
    }

    /**
     * 물 수정하기
     *
     * @param dataList
     */
    private void doModifyData(final Tr_get_hedctdata.DataList dataList) {
        Tr_water_info_edit_data inputData               = new Tr_water_info_edit_data();
        Tr_water_info_edit_data.RequestData requestData = new Tr_water_info_edit_data.RequestData();
        Tr_login login = Define.getInstance().getLoginInfo();

        requestData.mber_sn     = login.mber_sn;
        requestData.ast_mass    = inputData.getArray(dataList);

        mBaseFragment.getData(mBaseFragment.getContext(), inputData.getClass(), requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_water_info_edit_data) {
                    Tr_water_info_edit_data data = (Tr_water_info_edit_data) obj;
                    if ("Y".equals(data.reg_yn)) {

                        DBHelper helper         = new DBHelper(mBaseFragment.getContext());
                        DBHelperWater waterDb   = helper.getWaterDb();
                        waterDb.UpdateDb(dataList.idx, dataList.amount, dataList.reg_de);
                        getHistoryData();

                        CDialog.showDlg(mBaseFragment.getContext(), mBaseFragment.getContext().getString(R.string.modify_success));
                    }
                }
            }
        });
    }

    class showModifiDlg {
        private EditText mWaterInputEt;
        private EditText mWaterTargetEt;
        private TextView mDateTv;
        private TextView mTimeTv;
        private Button mSaveBtn;
        private String dataIdx;

        /**
         * 수정 Dialog 띄우기
         */
        private showModifiDlg(final Tr_get_hedctdata.DataList data) {
            View modifyView     = LayoutInflater.from(mBaseFragment.getContext()).inflate(R.layout.activity_water_input, null);
            mWaterInputEt       = (EditText) modifyView.findViewById(R.id.water_input_edittext);
            mWaterTargetEt      = (EditText) modifyView.findViewById(R.id.water_target_input_edittext);
            mDateTv             = (TextView) modifyView.findViewById(R.id.water_input_date_textview);
            mTimeTv             = (TextView) modifyView.findViewById(R.id.water_input_time_textview);
            mDateTv.setEnabled(false);
            mTimeTv.setEnabled(true);
            dataIdx = data.idx;

            mTimeTv.setOnTouchListener(mTouchListener);

            Tr_login login = Define.getInstance().getLoginInfo();
            mWaterTargetEt.setHint(login.goal_water_ntkqy);

            Calendar cal        = CDateUtil.getCalendar_yyyy_MM_dd_HH_mm(data.reg_de);
            String yyyy_mm_dd   = CDateUtil.getFormattedString_yyyy_MM_dd(cal.getTimeInMillis());
            String hh_mm        = CDateUtil.getFormattedString_hh_mm(cal.getTimeInMillis());
            // 2017-01-01 금요일 형태로 표시
            mDateTv.setText(yyyy_mm_dd + " " + CDateUtil.getDateToWeek(cal) + "요일");
            mDateTv.setTag(StringUtil.getIntString(yyyy_mm_dd));   // yyyyMMdd
            // 오후 1:00 형태로 표시
            mTimeTv.setText(CDateUtil.getAmPmString(cal.get(Calendar.HOUR_OF_DAY)) + " " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE));
            mTimeTv.setTag(StringUtil.getIntString(hh_mm));   // HHmm

            mWaterInputEt.setText("" + StringUtil.getIntVal(data.amount));

            final CDialog dlg = CDialog.showDlg(mBaseFragment.getContext(), modifyView);
            dlg.setTitle("물수정");
            mSaveBtn = (Button) modifyView.findViewById(R.id.water_input_save_btn);
            mSaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!mWaterTargetEt.getText().toString().isEmpty()) {
                        Tr_login login          = Define.getInstance().getLoginInfo();
                        login.goal_water_ntkqy  = mWaterTargetEt.getText().toString();
                        Define.getInstance().setLoginInfo(login);
                    }

                    dlg.dismiss();
                    data.amount     = mWaterInputEt.getText().toString();
                    String regDate  = mDateTv.getTag().toString();
                    String timeStr  = mTimeTv.getTag().toString();
                    regDate += timeStr;
                    data.reg_de     = regDate;
                    doModifyData(data);
                }
            });

            mTimeTv.addTextChangedListener(watcher);
            mDateTv.addTextChangedListener(watcher);
            mWaterInputEt.addTextChangedListener(watcher);
            if (isValidDate() && isValidTime() && isValidDrug()) {
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
                    if (vId == R.id.water_input_time_textview) {
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
            Calendar cal    = Calendar.getInstance(Locale.KOREA);
            Date now        = new Date();
            cal.setTime(now);

            int hour    = cal.get(Calendar.HOUR_OF_DAY);
            int minute  = cal.get(Calendar.MINUTE);
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
            int hour    = hourOfDay;
            if (hourOfDay > 11) {
                amPm = mBaseFragment.getContext().getString(R.string.text_afternoon);
                if (hourOfDay >= 13)
                    hour -= 12;
            } else {
                hour    = hour == 0 ? 12 : hour;
            }
            String tagMsg   = String.format("%02d%02d", hourOfDay, minute);
            String timeStr  = String.format("%02d:%02d", hour, minute);
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
                if (isValidDate() && isValidTime() && isValidDrug()) {
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

        private boolean isValidDrug() {
            String text = mWaterInputEt.getText().toString();
            return TextUtils.isEmpty(text) == false;
        }
    }
}
