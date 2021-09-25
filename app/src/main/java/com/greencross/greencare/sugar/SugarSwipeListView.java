package com.greencross.greencare.sugar;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.greencross.greencare.database.DBHelperSugar;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_bdsg_info_del_data;
import com.greencross.greencare.network.tr.data.Tr_bdsg_info_edit_data;
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

public class SugarSwipeListView {
    private static final String TAG = SwipeMenuListView.class.getSimpleName();
    private AppAdapter mAdapter;
    private List<Tr_get_hedctdata.DataList> mSwipeMenuDatas = new ArrayList<>();
    private BaseFragment mBaseFragment;

    public SugarSwipeListView(View view, BaseFragment baseFragment) {
        mBaseFragment = baseFragment;
        SwipeMenuListView listView = (SwipeMenuListView) view.findViewById(R.id.sugar_history_listview);

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
                            // 직접입력한 데이터만 수정할 수 있음.
                            new showModifiDlg(data);
                        } else {
                            // 장치에서 측정된 데이터는 수정할 수 없음.
                            String message = mBaseFragment.getContext().getString(R.string.text_alert_mesage_disable_edit);
                            CDialog.showDlg(mBaseFragment.getContext(), message);
                        }

                        break;
                    case 1:
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
                return false;
            }
        });


        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
                Tr_get_hedctdata.DataList data = (Tr_get_hedctdata.DataList) mAdapter.getItem(position);
                if (data.regtype.equals("U")) {

                }
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
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
        DBHelperSugar sugarDb = helper.getSugarDb();
        mSwipeMenuDatas.addAll(sugarDb.getResult());
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
            ViewHolder holder = (ViewHolder) convertView.getTag();
            Tr_get_hedctdata.DataList data = (Tr_get_hedctdata.DataList) getItem(position);

            Calendar cal = CDateUtil.getCalendar_yyyy_MM_dd_HH_mm(data.reg_de);
            String yyyyMMddhhss = CDateUtil.getRegDateFormat_yyyyMMdd_HHss(data.reg_de, ".", ":");

            Logger.i(TAG, "data.sugar=" + data.sugar + ", data.drugname=" + data.drugname);
            if (StringUtil.getFloat(data.sugar) <= 0f) {
                holder.typeTv.setVisibility(View.GONE);
                holder.sugarTv.setText("투약 (" + data.drugname + ")");
                holder.beforeAfterTv.setText("");
                holder.mldlTv.setVisibility(View.GONE);
            } else {
                holder.typeTv.setVisibility(View.VISIBLE);
                holder.typeTv.setText("D".equals(data.regtype) ? mBaseFragment.getContext().getString(R.string.text_device) : mBaseFragment.getContext().getString(R.string.text_direct_enter));
                holder.sugarTv.setText(data.sugar);
                Log.d("data.before","data.before:"+data.before);
                holder.beforeAfterTv.setText("2".equals(data.before) ? mBaseFragment.getContext().getString(R.string.text_foodafter) : mBaseFragment.getContext().getString(R.string.text_foodbefore));
                holder.mldlTv.setVisibility(View.VISIBLE);
                holder.mldlTv.setText("mg/dL");
            }

            holder.timeTv.setText(yyyyMMddhhss);

            return convertView;
        }

        class ViewHolder {
            TextView timeTv;
            TextView beforeAfterTv;
            TextView sugarTv;
            TextView typeTv;
            TextView mldlTv;

            public ViewHolder(View view) {
                timeTv = (TextView) view.findViewById(R.id.sugar_history_item_time_textview);
                beforeAfterTv = (TextView) view.findViewById(R.id.sugar_history_item_before_after_textview);
                sugarTv = (TextView) view.findViewById(R.id.sugar_history_item_sugar_textview);
                typeTv = (TextView) view.findViewById(R.id.sugar_history_item_type_textview);
                mldlTv = (TextView) view.findViewById(R.id.sugar_history_item_mldl_textview);

                view.setTag(this);
            }
        }
    }

    /**
     * 혈당 수정하기
     *
     * @param dataList
     */
    private void doModifyData(final Tr_get_hedctdata.DataList dataList) {
        Tr_bdsg_info_edit_data inputData = new Tr_bdsg_info_edit_data();
        Tr_bdsg_info_edit_data.RequestData requestData = new Tr_bdsg_info_edit_data.RequestData();
        Tr_login login = Define.getInstance().getLoginInfo();

        requestData.mber_sn = login.mber_sn;
        requestData.ast_mass = inputData.getArray(dataList);

        mBaseFragment.getData(mBaseFragment.getContext(), inputData.getClass(), requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_bdsg_info_edit_data) {
                    Tr_bdsg_info_edit_data data = (Tr_bdsg_info_edit_data) obj;
                    if ("Y".equals(data.reg_yn)) {

                        updateSugarDb(dataList);
                        CDialog.showDlg(mBaseFragment.getContext(), "수정이 완료되었습니다.");
                    }
                }
            }
        });
    }

    /**
     * 혈당 DB에서 해당
     *
     * @param dataList
     */
    private void updateSugarDb(Tr_get_hedctdata.DataList dataList) {
        DBHelper helper = new DBHelper(mBaseFragment.getContext());
        DBHelperSugar sugarDb = helper.getSugarDb();
        sugarDb.updateDbSugar(dataList.idx, dataList.sugar, dataList.drugname, dataList.before, dataList.reg_de);

        getHistoryData();
    }

    /**
     * 혈당 삭제하기
     *
     * @param dataList
     */
    private void doDeleteData(final int position, final Tr_get_hedctdata.DataList dataList) {

        Tr_bdsg_info_del_data inputData = new Tr_bdsg_info_del_data();
        Tr_bdsg_info_del_data.RequestData requestData = new Tr_bdsg_info_del_data.RequestData();
        Tr_login login = Define.getInstance().getLoginInfo();

        requestData.mber_sn = login.mber_sn;
        requestData.ast_mass = inputData.getArray(dataList);

        mBaseFragment.getData(mBaseFragment.getContext(), inputData.getClass(), requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_bdsg_info_del_data) {
                    Tr_bdsg_info_del_data data = (Tr_bdsg_info_del_data) obj;
                    if ("Y".equals(data.reg_yn)) {

                        deleteSugarDb(dataList);

                        mSwipeMenuDatas.remove(position);
                        mAdapter.notifyDataSetChanged();

                        CDialog.showDlg(mBaseFragment.getContext(), "삭제 되었습니다.");
                    }
                }
            }
        });
    }

    /**
     * 혈당 DB에서  해당 idx의 데이터를 삭제
     *
     * @param dataList
     */
    private void deleteSugarDb(Tr_get_hedctdata.DataList dataList) {
        DBHelper helper = new DBHelper(mBaseFragment.getContext());
        DBHelperSugar sugarDb = helper.getSugarDb();
        sugarDb.deleteDb(dataList.idx);
    }

    class showModifiDlg {
        private EditText mSugarInputEt;
        private EditText mMedicenInputEt;
        private TextView mDateTv;
        private TextView mTimeTv;
        private Button mSaveBtn;
        private String dataIdx;
        private CheckBox mCheckBox;
        private LinearLayout mSugarLayout;
        private LinearLayout mMedicenLayout;

        private boolean mIsMedicenMode; // 투약정보 수정인지 여부

        /**
         * 수정 Dialog 띄우기
         */
        private showModifiDlg(final Tr_get_hedctdata.DataList data) {
            View modifyView = LayoutInflater.from(mBaseFragment.getContext()).inflate(R.layout.activity_sugar_input, null);
            mCheckBox = (CheckBox) modifyView.findViewById(R.id.suger_medicen_input_checkbox);
            mSugarInputEt = (EditText) modifyView.findViewById(R.id.suger_input_edittext);
            mMedicenInputEt = (EditText) modifyView.findViewById(R.id.sugar_medicen_editext);
            mDateTv = (TextView) modifyView.findViewById(R.id.sugar_input_date_textview);
            mTimeTv = (TextView) modifyView.findViewById(R.id.sugar_input_time_textview);
            mSugarLayout = (LinearLayout) modifyView.findViewById(R.id.sugar_input_sugar_layout);
            mMedicenLayout = (LinearLayout) modifyView.findViewById(R.id.sugar_input_medicen_layout);

            mDateTv.setEnabled(false);
            mTimeTv.setEnabled(true);

            mTimeTv.setOnTouchListener(mTouchListener);

            dataIdx = data.idx;

            // 식전:1, 식후2 아무것도 선택안하면 0
            mCheckBox.setChecked(!("2".equals(data.before)));
            Logger.i(TAG, "before=" + data.before);

            Calendar cal = CDateUtil.getCalendar_yyyy_MM_dd_HH_mm(data.reg_de);
            String yyyy_mm_dd = CDateUtil.getFormattedString_yyyy_MM_dd(cal.getTimeInMillis());
            String hh_mm = CDateUtil.getFormattedString_hh_mm(cal.getTimeInMillis());
            // 2017-01-01 금요일 형태로 표시
            mDateTv.setText(yyyy_mm_dd + " " + CDateUtil.getDateToWeek(cal) + "요일");
            mDateTv.setTag(StringUtil.getIntString(yyyy_mm_dd));   // yyyyMMdd
            // 오후 1:00 형태로 표시
            mTimeTv.setText(CDateUtil.getAmPmString(cal.get(Calendar.HOUR_OF_DAY)) + " " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE));
            mTimeTv.setTag(StringUtil.getIntString(hh_mm));   // HHmm

            mIsMedicenMode = StringUtil.getFloat(data.sugar) == 0f;
            if (mIsMedicenMode) {

                if (data.drugname.toString().equals(" ")) {
                    data.drugname = "";
                }

                mSugarLayout.setVisibility(View.GONE);
                mMedicenLayout.setVisibility(View.VISIBLE);
                mMedicenInputEt.setText(data.drugname);

                if (data.drugname.toString().equals("")) {
                    data.drugname = " ";
                }
            } else {
                mSugarLayout.setVisibility(View.VISIBLE);
                mMedicenLayout.setVisibility(View.GONE);
                mSugarInputEt.setText(data.sugar);
            }

            final CDialog dlg = CDialog.showDlg(mBaseFragment.getContext(), modifyView);
            if (mIsMedicenMode)
                dlg.setTitle(mBaseFragment.getContext().getString(R.string.medi_modify));
            else
                dlg.setTitle(mBaseFragment.getContext().getString(R.string.sugar_modify));
            mSaveBtn = (Button) modifyView.findViewById(R.id.sugar_input_save_btn);
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
                        data.before = mCheckBox.isChecked() ? "1" : "2";
                        data.sugar = mSugarInputEt.getText().toString();
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
            mSugarInputEt.addTextChangedListener(watcher);
            if (isValidDate() && isValidTime() && isValidSugarAndSugar()) {
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
                    if (vId == R.id.sugar_input_time_textview) {
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
                if (isValidDate() && isValidTime() && isValidSugarAndSugar()) {
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

        private boolean isValidSugarAndSugar() {
            if (mIsMedicenMode) {
                return true;
            } else {
                String text = mSugarInputEt.getText().toString();
                return TextUtils.isEmpty(text) == false;
            }
        }

    }
}
