package com.greencross.greencare.food;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.database.DBHelperFoodCalorie;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;

/**
 * Created by MrsWin on 2017-04-23.
 */

public class FoodDetailDialog {
    private final String TAG = FoodDetailDialog.class.getSimpleName();

    private float mCalVal = 1;
    private TextView qtyTv;
    private Button dlgCancelBtn;
    private Button dlgOkBtn;
    private TextView foodValTv1;
    private TextView foodValTv2;
    private TextView foodValTv3;
    private TextView foodValTv4;
    private TextView foodValTv5;
    private TextView foodValTv6;

    //    private float forPeople=1.0;
    private float mFoodGram = 0.0f;
    private float mFoodCalorie = 0.0f;
    private float mFoodCarbohydrate = 0.0f;
    private float mFoodProtein = 0.0f;
    private float mFoodFat = 0.0f;
    private float mFoodSalt = 0.0f;

    private float maxCal = 5.0f;
    private float minCal = 0.25f;


    DBHelperFoodCalorie.Data mFoodData;

    /**
     * 음식 상세 Dialog
     *
     * @param data
     */
    public FoodDetailDialog(Context context, final DBHelperFoodCalorie.Data data, final View.OnClickListener onClickListener) {

        mFoodData = data;
        mFoodData.forpeople = mFoodData.forpeople == null ? "1" : mFoodData.forpeople;


        View view = LayoutInflater.from(context).inflate(R.layout.dialog_foodsearch, null);

        qtyTv = (TextView) view.findViewById(R.id.food_qty_textview);
        dlgCancelBtn = (Button) view.findViewById(R.id.food_dlg_cancle);
        dlgOkBtn = (Button) view.findViewById(R.id.food_dlg_confirm);

        foodValTv1 = (TextView) view.findViewById(R.id.food_attr_val1);
        foodValTv2 = (TextView) view.findViewById(R.id.food_attr_val2);
        foodValTv3 = (TextView) view.findViewById(R.id.food_attr_val3);
        foodValTv4 = (TextView) view.findViewById(R.id.food_attr_val4);
        foodValTv5 = (TextView) view.findViewById(R.id.food_attr_val5);
        foodValTv6 = (TextView) view.findViewById(R.id.food_attr_val6);

//        forPeople = StringUtil.getFloatVal(mFoodData.forpeople);
        mFoodGram = StringUtil.getFloatVal(mFoodData.food_gram);
        mFoodCalorie = StringUtil.getFloatVal(mFoodData.food_calorie);
        mFoodCarbohydrate = StringUtil.getFloatVal(mFoodData.food_carbohydrate);
        mFoodProtein = StringUtil.getFloatVal(mFoodData.food_protein);
        mFoodFat = StringUtil.getFloatVal(mFoodData.food_fat);
        mFoodSalt = StringUtil.getFloatVal(mFoodData.food_salt);

        if (TextUtils.isEmpty(mFoodData.forpeople)) {
            mCalVal = 1;
            setCalDlgata(mCalVal);
        } else {
            mCalVal = StringUtil.getFloatVal(mFoodData.forpeople);
            setCalDlgata(mCalVal);
        }


        view.findViewById(R.id.next_btn).setOnClickListener(mClickListener);
        view.findViewById(R.id.pre_btn).setOnClickListener(mClickListener);

        final CDialog dlg = CDialog.showDlg(context, view);
        dlg.setTitle(data.food_name);
        dlgOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFoodData.forpeople = "" + mCalVal;

                dlg.dismiss();

                if (onClickListener != null)
                    onClickListener.onClick(v);
            }
        });
        dlgCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int vId = v.getId();
            if (vId == R.id.pre_btn) {
                setCalDlgataDo(-minCal);
            } else if (vId == R.id.next_btn) {
                setCalDlgataDo(minCal);
            }
        }
    };

    public DBHelperFoodCalorie.Data getFoodData() {
        return mFoodData;
    }


    private void setCalDlgataDo(float calVal) {

        float tmpCalVal = mCalVal + calVal;
        if (tmpCalVal < minCal || tmpCalVal > maxCal) return;

        mCalVal = mCalVal + calVal;

        setCalDlgata(mCalVal);
    }


    private void setCalDlgata(float calVal) {

        String textForpeople = StringUtil.getNoneZeroString2((mCalVal));
        String textFoodCalorie = StringUtil.getNoneZeroString((mFoodCalorie * mCalVal));
        String textFoodGram = StringUtil.getNoneZeroString((mFoodGram * mCalVal));

        String textFoodCarbohydrate = StringUtil.getNoneZeroString((mFoodCarbohydrate * mCalVal));
        String textFoodProtein = StringUtil.getNoneZeroString((mFoodProtein * mCalVal));
        String textFoodFat = StringUtil.getNoneZeroString((mFoodFat * mCalVal));
        String textFoodSalt = StringUtil.getNoneZeroString((mFoodSalt * mCalVal));
        String unit = mFoodData.food_unit;

        Logger.i(TAG, "setCalDlgata.mCalInt=" + mCalVal + ", forpeople=" + mFoodData.forpeople + ", food_calorie=" + mFoodData.food_calorie + ", foodCalVal1 * cnt=" + (mFoodSalt * mCalVal));

        qtyTv.setText(textForpeople + "인분 (" + textFoodGram + unit + ")");
        foodValTv1.setText(textFoodCalorie + "kcal");
        foodValTv2.setText(textFoodGram + "g");
        foodValTv3.setText(textFoodCarbohydrate + "g");
        foodValTv4.setText(textFoodProtein + "g");
        foodValTv5.setText(textFoodFat + "g");
        foodValTv6.setText(textFoodSalt + "mg");
    }

}
