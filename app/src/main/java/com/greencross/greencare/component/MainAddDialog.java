package com.greencross.greencare.component;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;

import com.greencross.greencare.R;
import com.greencross.greencare.main.MainCardData;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 메인화면 Add버튼에 사용하는 Dialog
 */
public class MainAddDialog extends Dialog {
    private final String TAG = MainAddDialog.class.getSimpleName();

    private List<MainCardData> mainCardList = new ArrayList<>();
    private MainItemAdapter adapter;
    private IAddDialog mIAddDialog;
    private DBHelper mDbHelper;

    private HashMap<MainCardData.CardE, MainCardData> cardHash = new HashMap<>();

    public MainAddDialog(Context context, List<MainCardData> mainCardList, IAddDialog iDismiss) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mIAddDialog = iDismiss;
        mDbHelper = new DBHelper(getContext());
        for (MainCardData data : mDbHelper.getResult()) {
            if (data.isVisible())
                cardHash.put(data.getCardE(), data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.main_add_item_dialog);
        GridView mainaddgridview = (GridView) findViewById(R.id.main_add_gridview);

        ImageView closeIv = (ImageView) findViewById(R.id.main_add_close_btn);
        adapter = new MainItemAdapter();
        mainaddgridview.setAdapter(adapter);

        prepareMainItems();

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainAddDialog.this.dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mIAddDialog != null)
            mIAddDialog.onDismiss();
    }

    private void prepareMainItems() {
        MainCardData.CardE[] data = MainCardData.CardE.values();
        for (int i = 0; i < data.length-1; i++) {
            mainCardList.add(new MainCardData(data[i]));
            Logger.i(TAG, "prepareMainItems["+i+"]"+data[i]);
        }

        adapter.notifyDataSetChanged();
    }

    class MainItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mainCardList == null ? 0 :  mainCardList.size();
        }

        @Override
        public MainCardData getItem(int position) {
            return mainCardList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder holder;
            if (convertView == null) {
                holder = new MyViewHolder();
                View view = getLayoutInflater().inflate(R.layout.main_add_dialog_item, null);
                holder.chkBox = (CheckBox)view.findViewById(R.id.main_add_item_checkbox);

                convertView = view;
                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder)convertView.getTag();
            }

            final MainCardData.CardE data = getItem(position).getCardE();

            if (data == MainCardData.CardE.FOOD) {
                holder.chkBox.setBackgroundResource(R.drawable.ico_sel01);
            } else if (data == MainCardData.CardE.WORKING) {
                holder.chkBox.setBackgroundResource(R.drawable.ico_sel02);
            } else if (data == MainCardData.CardE.WATER) {
                holder.chkBox.setBackgroundResource(R.drawable.ico_sel03);
            } else if (data == MainCardData.CardE.PRESURE) {
                holder.chkBox.setBackgroundResource(R.drawable.ico_sel04);
            } else if (data == MainCardData.CardE.SUGAR) {
                holder.chkBox.setBackgroundResource(R.drawable.ico_sel05);
            } else if (data == MainCardData.CardE.WEIGHT) {
                holder.chkBox.setBackgroundResource(R.drawable.ico_sel06);
            } else if (data == MainCardData.CardE.FAT) {
                holder.chkBox.setBackgroundResource(R.drawable.ico_sel07);
            }

            if (cardHash.get(data) != null) {
                holder.chkBox.setChecked(true);
            } else {
                holder.chkBox.setChecked(false);
            }

            holder.chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mDbHelper.updateVisible(isChecked, data.name());
                }
            });

            return convertView;
        }

        public class MyViewHolder {
            public CheckBox chkBox;
        }

    }


    public interface IAddDialog {
        void onDismiss();
    }
}