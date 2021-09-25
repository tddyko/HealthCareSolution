package com.greencross.greencare.food;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.DummyActivity;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.base.value.TypeDataSet;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperFoodMain;
import com.greencross.greencare.network.tr.data.Tr_get_meal_input_data;
import com.greencross.greencare.network.tr.data.Tr_get_meal_input_food_data;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.ChartTimeUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.ViewUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.greencross.greencare.food.FoodInputFragment.BUNDLE_FOOD_INPUT_DATE;

/**
 * 등록하기 화면
 * Created by mrsohn on 2017. 3. 14..
 */

public class FoodManageWriteView {
    private static final String TAG = FoodManageWriteView.class.getSimpleName();

    private final int FOOD_INPUT_REQ = 8888;

    public ChartTimeUtil mTimeClass;

    private RelativeLayout mBreafastInputLayout;
    private RelativeLayout mBreakfastSnackInputLayout;
    private RelativeLayout mLunchInputLayout;
    private RelativeLayout mLunchSnackInputLayout;
    private RelativeLayout mDinnerInputLayout;
    private RelativeLayout mDinnerSnackInputLayout;

    private ImageView mFoodIv01;
    private ImageView mFoodIv02;
    private ImageView mFoodIv03;
    private ImageView mFoodIv04;
    private ImageView mFoodIv05;
    private ImageView mFoodIv06;

    private TextView mKcalTv01;
    private TextView mKcalTv02;
    private TextView mKcalTv03;
    private TextView mKcalTv04;
    private TextView mKcalTv05;
    private TextView mKcalTv06;

    private TextView mTimeTv01;
    private TextView mTimeTv02;
    private TextView mTimeTv03;


    private TextView mDateTv;
    private BaseFragment mBaseFragment;
    private View mView;

    private Tr_get_meal_input_data.ReceiveDatas mMealDataA;     // 아침
    private Tr_get_meal_input_data.ReceiveDatas mMealDataB;     // 점심
    private Tr_get_meal_input_data.ReceiveDatas mMealDataC;     // 저녁
    private Tr_get_meal_input_data.ReceiveDatas mMealDataD;     // 아침간식
    private Tr_get_meal_input_data.ReceiveDatas mMealDataE;     // 점심간식
    private Tr_get_meal_input_data.ReceiveDatas mMealDataF;     // 저녁간식

    private List<Tr_get_meal_input_food_data.ReceiveDatas> mFoodListA = new ArrayList<>();  // 아침
    private List<Tr_get_meal_input_food_data.ReceiveDatas> mFoodListB = new ArrayList<>();  // 점심
    private List<Tr_get_meal_input_food_data.ReceiveDatas> mFoodListC = new ArrayList<>();  // 저녁
    private List<Tr_get_meal_input_food_data.ReceiveDatas> mFoodListD = new ArrayList<>();  // 아침간식
    private List<Tr_get_meal_input_food_data.ReceiveDatas> mFoodListE = new ArrayList<>();  // 점심간식
    private List<Tr_get_meal_input_food_data.ReceiveDatas> mFoodListF = new ArrayList<>();  // 저녁간식

    private ImageView foodbreakfestimage;
    private ImageView foodbreakfestsnackimage;
    private ImageView foodlunchimage;
    private ImageView foodlunchsnackimage;
    private ImageView fooddinnerimage;
    private ImageView fooddinnersnackimage;

    private ImageButton imgPre_btn;
    private ImageButton imgNext_btn;


    public FoodManageWriteView(BaseFragment baseFragment, View view) {
        mBaseFragment = baseFragment;
        mView = view;

        mDateTv = (TextView) view.findViewById(R.id.txtCurrDate);

        mFoodIv01 = (ImageView) view.findViewById(R.id.ivImage01);
        mFoodIv02 = (ImageView) view.findViewById(R.id.ivImage02);
        mFoodIv03 = (ImageView) view.findViewById(R.id.ivImage03);
        mFoodIv04 = (ImageView) view.findViewById(R.id.ivImage04);
        mFoodIv05 = (ImageView) view.findViewById(R.id.ivImage05);
        mFoodIv06 = (ImageView) view.findViewById(R.id.ivImage06);

        mKcalTv01 = (TextView) view.findViewById(R.id.tvKcal1);
        mKcalTv02 = (TextView) view.findViewById(R.id.tvKcal2);
        mKcalTv03 = (TextView) view.findViewById(R.id.tvKcal3);
        mKcalTv04 = (TextView) view.findViewById(R.id.tvKcal4);
        mKcalTv05 = (TextView) view.findViewById(R.id.tvKcal5);
        mKcalTv06 = (TextView) view.findViewById(R.id.tvKcal6);

        foodbreakfestimage = (ImageView) view.findViewById(R.id.food_breakfest_image);             // 아침
        foodbreakfestsnackimage = (ImageView) view.findViewById(R.id.food_breakfest_snack_image);  // 아침간식
        foodlunchimage = (ImageView) view.findViewById(R.id.food_lunch_image);                     // 점심
        foodlunchsnackimage = (ImageView) view.findViewById(R.id.food_lunch_snack_image);          // 점심간식
        fooddinnerimage = (ImageView) view.findViewById(R.id.food_dinner_image);                   // 저녁
        fooddinnersnackimage = (ImageView) view.findViewById(R.id.food_dinner_snack_image);        // 저녁간식

        mTimeTv01 = (TextView) view.findViewById(R.id.tvTime01);
        mTimeTv02 = (TextView) view.findViewById(R.id.tvTime02);
        mTimeTv03 = (TextView) view.findViewById(R.id.tvTime03);
        imgPre_btn                  = (ImageButton) view.findViewById(R.id.btn_calLeft);
        imgNext_btn                 = (ImageButton) view.findViewById(R.id.btn_calRight);

        mBreafastInputLayout = (RelativeLayout) view.findViewById(R.id.food_breakfest_input_layout);
        mBreakfastSnackInputLayout = (RelativeLayout) view.findViewById(R.id.food_breakfast_snack_input_layout);
        mLunchInputLayout = (RelativeLayout) view.findViewById(R.id.food_lunch_input_layout);
        mLunchSnackInputLayout = (RelativeLayout) view.findViewById(R.id.food_lunch_snack_input_layout);
        mDinnerInputLayout = (RelativeLayout) view.findViewById(R.id.food_dinner_input_layout);
        mDinnerSnackInputLayout = (RelativeLayout) view.findViewById(R.id.food_dinner_snack_input_layout);

        mBreafastInputLayout.setOnClickListener(mClickListener);
        mBreakfastSnackInputLayout.setOnClickListener(mClickListener);
        mLunchInputLayout.setOnClickListener(mClickListener);
        mLunchSnackInputLayout.setOnClickListener(mClickListener);
        mDinnerInputLayout.setOnClickListener(mClickListener);
        mDinnerSnackInputLayout.setOnClickListener(mClickListener);

        view.findViewById(R.id.btn_calLeft).setOnClickListener(mClickListener);
        view.findViewById(R.id.btn_calRight).setOnClickListener(mClickListener);

        mTimeClass = new ChartTimeUtil(TypeDataSet.Period.PERIOD_DAY);

        // 음식 검색후 재 진입시 날자 데이터
        Bundle backBundle = mBaseFragment.getBackData();
        String date = backBundle.getString(BUNDLE_FOOD_INPUT_DATE);
        Logger.i(TAG, "backBundle.date=" + date);
        if (backBundle != null && TextUtils.isEmpty(date) == false) {
            Calendar cal = CDateUtil.getCalendar_yyyy_MM_dd_HH_mm(date);
            mTimeClass.setStartTime(cal.getTimeInMillis());
        }

        getData();
        setNextButtonVisible();
    }

    public void setVisibility(int visibility) {
        mView.setVisibility(visibility);
    }

    public int getVisibility() {
        if (mView == null)
            return View.GONE;
        else
            return mView.getVisibility();
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();

            if (vId == R.id.btn_calLeft) {
                mTimeClass.calTime(-1);
                getData();
            } else if (vId == R.id.btn_calRight) {
                // 초기값 일때 다음날 데이터는 없으므로 리턴
                if (mTimeClass.getCalTime() == 0)
                    return;

                mTimeClass.calTime(1);
                getData();
            } else if (vId == R.id.food_breakfest_input_layout
                    || vId == R.id.food_breakfast_snack_input_layout
                    || vId == R.id.food_lunch_input_layout
                    || vId == R.id.food_lunch_snack_input_layout
                    || vId == R.id.food_dinner_input_layout
                    || vId == R.id.food_dinner_snack_input_layout) {

                FoodInputFragment fragment = (FoodInputFragment) FoodInputFragment.newInstance();
                String mealType = v.getTag().toString();
                Bundle bundle = getBundleData(mealType);
                fragment.setArguments(bundle);
                DummyActivity.startActivityForResult(mBaseFragment, FOOD_INPUT_REQ, fragment.getClass(), bundle);
            }
            setNextButtonVisible();
        }
    };

    private void setNextButtonVisible(){
        // 초기값 일때 다음날 데이터는 없으므로 리턴
        if (mTimeClass.getCalTime() == 0) {
            imgNext_btn.setVisibility(View.INVISIBLE);
        }else{
            imgNext_btn.setVisibility(View.VISIBLE);
        }
    }
    /**
     * 날자 계산 후 조회
     */
    public void getData() {
        mBaseFragment.showProgress();
        long startTime = mTimeClass.getStartTime();

        mMealDataA = null;     // 아침
        mMealDataB = null;     // 점심
        mMealDataC = null;     // 저녁
        mMealDataD = null;     // 아침간식
        mMealDataE = null;     // 점심간식
        mMealDataF = null;     // 저녁간식

        mFoodListA.clear();  // 아침
        mFoodListB.clear();  // 점심
        mFoodListC.clear();  // 저녁
        mFoodListD.clear();  // 아침간식
        mFoodListE.clear();  // 점심간식
        mFoodListF.clear();  // 저녁간식

        String format = "yyyy.MM.dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        String startDate = sdf.format(startTime);
        mDateTv.setText(startDate);

        format = "yyyy-MM-dd";
        sdf = new SimpleDateFormat(format);
        startDate = sdf.format(startTime);

        getMealData(startDate);
        mBaseFragment.hideProgress();
    }

    /**
     * 데이터 가져오기(식사)
     */
    private void getMealData(final String reqDate) {
        DBHelper helper = new DBHelper(mBaseFragment.getContext());
        DBHelperFoodMain db = helper.getFoodMainDb();
        Tr_get_meal_input_data foodData = db.getResultDay(reqDate);
        setMealData(foodData);

    }

    /**
     * 식사데이터
     *
     * @return
     */
    private void setMealData(Tr_get_meal_input_data data) {
        mFoodIv01.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_food_after));
        mFoodIv02.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_food_after));
        mFoodIv03.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_food_after));
        mFoodIv04.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_food_after));
        mFoodIv05.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_food_after));
        mFoodIv06.setImageDrawable(ContextCompat.getDrawable(mBaseFragment.getContext(), R.drawable.icon_food_after));

        foodbreakfestimage.setImageBitmap(null);
        foodbreakfestsnackimage.setImageBitmap(null);
        foodlunchimage.setImageBitmap(null);
        foodlunchsnackimage.setImageBitmap(null);
        fooddinnerimage.setImageBitmap(null);
        fooddinnersnackimage.setImageBitmap(null);

        mKcalTv01.setText("0kcal");
        mTimeTv01.setText("   ");
        mTimeTv01.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_time, 0, 0, 0);
        mKcalTv02.setText("0kcal");
        mTimeTv02.setText("   ");
        mTimeTv02.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_time, 0, 0, 0);
        mKcalTv03.setText("0kcal");
        mTimeTv03.setText("   ");
        mTimeTv03.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_time, 0, 0, 0);

        mKcalTv04.setText("0kcal");
        mKcalTv05.setText("0kcal");
        mKcalTv06.setText("0kcal");

        for (Tr_get_meal_input_data.ReceiveDatas recv : data.data_list) {

            String mealType = recv.mealtype;
            String idx = recv.idx;

            Logger.i(TAG, "mealType=" + mealType + ", idx=" + idx + ", photoPath=" + Define.getFoodPhotoPath(idx) + ", amounttime=" + recv.amounttime);
            if (mBaseFragment.getContext().getString(R.string.text_breakfast_code).equals(mealType)) {
                mMealDataA = recv;
                mKcalTv01.setText(recv.calorie + "kcal");

                if (TextUtils.isEmpty(recv.calorie) == false) {

                    if (TextUtils.isEmpty(recv.amounttime)==true)
                        mTimeTv01.setText("");
                    else
                        mTimeTv01.setText(recv.amounttime + "분");

                    mTimeTv01.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                }
                ViewUtil.getIndexToImageData(idx, foodbreakfestimage);

            } else if (mBaseFragment.getContext().getString(R.string.text_lunch_code).equals(mealType)) {
                mMealDataB = recv;
                mKcalTv03.setText(recv.calorie + "kcal");

                if (TextUtils.isEmpty(recv.calorie) == false) {

                    if (TextUtils.isEmpty(recv.amounttime)==true)
                        mTimeTv02.setText("");
                    else
                        mTimeTv02.setText(recv.amounttime + "분");

                    mTimeTv02.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                }
                ViewUtil.getIndexToImageData(idx, foodlunchimage);

            } else if (mBaseFragment.getContext().getString(R.string.text_dinner_code).equals(mealType)) {
                mMealDataC = recv;
                mKcalTv05.setText(recv.calorie + "kcal");

                if (TextUtils.isEmpty(recv.calorie) == false) {

                    if (TextUtils.isEmpty(recv.amounttime)==true)
                        mTimeTv03.setText("");
                    else
                        mTimeTv03.setText(recv.amounttime + "분");

                    mTimeTv03.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                }
                ViewUtil.getIndexToImageData(idx, fooddinnerimage);

            } else if (mBaseFragment.getContext().getString(R.string.text_breakfast_snack_code).equals(mealType)) {
                mMealDataD = recv;
                mKcalTv02.setText(recv.calorie + "kcal");
                ViewUtil.getIndexToImageData(idx, foodbreakfestsnackimage);

            } else if (mBaseFragment.getContext().getString(R.string.text_lunch_snack_code).equals(mealType)) {
                mMealDataE = recv;
                mKcalTv04.setText(recv.calorie + "kcal");
                ViewUtil.getIndexToImageData(idx, foodlunchsnackimage);

            } else if (mBaseFragment.getContext().getString(R.string.text_dinner_snack_code).equals(mealType)) {
                mMealDataF = recv;

                mKcalTv06.setText(recv.calorie + "kcal");
                ViewUtil.getIndexToImageData(idx, fooddinnersnackimage);
            }
        }
    }

    /**
     * 음식 데이터
     *
     * @return
     */
    private void setFoodListData(Tr_get_meal_input_food_data data) {
        mFoodListA.clear();
        mFoodListB.clear();
        mFoodListC.clear();
        mFoodListD.clear();
        mFoodListE.clear();
        mFoodListF.clear();
        for (Tr_get_meal_input_food_data.ReceiveDatas recv : data.data_list) {
            String mealType = recv.forpeople;
            if (mBaseFragment.getContext().getString(R.string.text_breakfast_code).equals(mealType)) {
                mFoodListA.add(recv);
            } else if (mBaseFragment.getContext().getString(R.string.text_lunch_code).equals(mealType)) {
                mFoodListB.add(recv);
            } else if (mBaseFragment.getContext().getString(R.string.text_dinner_code).equals(mealType)) {
                mFoodListC.add(recv);
            } else if (mBaseFragment.getContext().getString(R.string.text_breakfast_snack_code).equals(mealType)) {
                mFoodListD.add(recv);
            } else if (mBaseFragment.getContext().getString(R.string.text_lunch_snack_code).equals(mealType)) {
                mFoodListE.add(recv);
            } else if (mBaseFragment.getContext().getString(R.string.text_dinner_snack_code).equals(mealType)) {
                mFoodListF.add(recv);
            }
        }
    }

    /**
     * 음식검색에서 사용될 BundleData
     *
     * @param mealType
     * @return
     */
    private Bundle getBundleData(String mealType) {
        Tr_get_meal_input_data.ReceiveDatas mealData = null;
        List<Tr_get_meal_input_food_data.ReceiveDatas> foodList = new ArrayList<>();
        String title = "";
        if (mBaseFragment.getContext().getString(R.string.text_breakfast_code).equals(mealType)) {
            title = mBaseFragment.getContext().getString(R.string.text_breakfast);
            mealData = mMealDataA;
            foodList.addAll(mFoodListA);
        } else if (mBaseFragment.getContext().getString(R.string.text_lunch_code).equals(mealType)) {
            title = mBaseFragment.getContext().getString(R.string.text_lunch);
            mealData = mMealDataB;
            foodList.addAll(mFoodListB);
        } else if (mBaseFragment.getContext().getString(R.string.text_dinner_code).equals(mealType)) {
            title = mBaseFragment.getContext().getString(R.string.text_dinner);
            mealData = mMealDataC;
            foodList.addAll(mFoodListC);
        } else if (mBaseFragment.getContext().getString(R.string.text_breakfast_snack_code).equals(mealType)) {
            title = mBaseFragment.getContext().getString(R.string.text_breakfast_snack);
            mealData = mMealDataD;
            foodList.addAll(mFoodListD);
        } else if (mBaseFragment.getContext().getString(R.string.text_lunch_snack_code).equals(mealType)) {
            title = mBaseFragment.getContext().getString(R.string.text_lunch_snack);
            mealData = mMealDataE;
            foodList.addAll(mFoodListE);
        } else if (mBaseFragment.getContext().getString(R.string.text_dinner_snack_code).equals(mealType)) {
            title = mBaseFragment.getContext().getString(R.string.text_dinner_snack);
            mealData = mMealDataF;
            foodList.addAll(mFoodListF);
        }

        Bundle bundle = new Bundle();
        String date = mDateTv.getText().toString();
        bundle.putString(BUNDLE_FOOD_INPUT_DATE, date);
        bundle.putString(FoodInputFragment.BUNDLE_FOOD_MEAL_TYPE, mealType);

        bundle.putParcelable(FoodInputFragment.BUNDLE_MEAL_DATA, mealData);
        bundle.putParcelableArrayList(FoodInputFragment.BUNDLE_FOOD_DATA, (ArrayList<? extends Parcelable>) foodList);
        Logger.i(TAG, "Bundle.foodList=" + foodList.size());

        bundle.putString(CommonActionBar.ACTION_BAR_TITLE, title);  // 액션바 타이틀

        return bundle;
    }

    public void onResume() {
        getData();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}