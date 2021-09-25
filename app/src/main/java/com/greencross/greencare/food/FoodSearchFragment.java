package com.greencross.greencare.food;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperFoodCalorie;
import com.greencross.greencare.util.Logger;

/**
 * Created by mrsohn on 2017. 3. 14..
 * 혈당관리
 */

public class FoodSearchFragment extends BaseFragment {
    private final String TAG = FoodSearchFragment.class.getSimpleName();

    public static String BUNDLE_FOOD_DETAIL_INFO = "food_detail_info";

    private ListView mListView;
    private ListAdapter mAdapter;

    public static Fragment newInstance() {
        FoodSearchFragment fragment = new FoodSearchFragment();
        return fragment;
    }

    /**
     * 액션바 세팅
     */
    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_food_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText mSearchEditText = (EditText) view.findViewById(R.id.food_search_edittext);
        mListView = (ListView) view.findViewById(R.id.food_search_listview);

        view.findViewById(R.id.food_search_button).setOnClickListener(mClickListener);

        DBHelper helper = new DBHelper(getContext());
        DBHelperFoodCalorie db = helper.getFoodCalorieDb();
        Cursor cursor = db.getResult("");

        mAdapter = new ListAdapter(getContext(), cursor, true);
        mListView.setAdapter(mAdapter);

        mSearchEditText.addTextChangedListener(mWatcher);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.food_search_button) {

            }
        }
    };

    class ListAdapter extends CursorAdapter {

        public ListAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        public ListAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_food_search_item, null);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final DBHelperFoodCalorie.Data data = getDataCursor(cursor);

            TextView foodTextView = (TextView) view.findViewById(R.id.food_qty_textview);
            foodTextView.setText(data.food_name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCalorieDlg(data);
                }
            });
        }
    }

    TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (TextUtils.isEmpty(str) == false) {
                DBHelper helper = new DBHelper(getContext());
                DBHelperFoodCalorie db = helper.getFoodCalorieDb();
                Cursor cursor = db.getResult(s.toString());

                if (mAdapter != null)
                    mAdapter.swapCursor(cursor);
            }
        }
    };

    /**
     * 음식 상세 Dialog
     * @param data
     */
    private FoodDetailDialog mFoodDlg;
    private void showCalorieDlg(final DBHelperFoodCalorie.Data data) {
        mFoodDlg = new FoodDetailDialog(getContext(), data, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelperFoodCalorie.Data foodData = mFoodDlg.getFoodData();
                Intent intent = new Intent();
                intent.putExtra(BUNDLE_FOOD_DETAIL_INFO, foodData);

                Logger.i(TAG, "data.forpeople="+foodData.forpeople);

                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
    }

    private DBHelperFoodCalorie.Data getDataCursor(Cursor cursor) {
        DBHelperFoodCalorie.Field tb = new DBHelperFoodCalorie.Field();
        DBHelperFoodCalorie.Data data = new DBHelperFoodCalorie.Data();
        data.food_code = cursor.getString(cursor.getColumnIndex(tb.FOOD_CODE));
        data.food_kind = cursor.getString(cursor.getColumnIndex(tb.FOOD_KIND));
        data.food_name = cursor.getString(cursor.getColumnIndex(tb.FOOD_NAME));
        data.food_gram = cursor.getString(cursor.getColumnIndex(tb.FOOD_GRAM));
        data.food_unit = cursor.getString(cursor.getColumnIndex(tb.FOOD_UNIT));
        data.food_calorie = cursor.getString(cursor.getColumnIndex(tb.FOOD_CALORIE));
        data.food_carbohydrate = cursor.getString(cursor.getColumnIndex(tb.FOOD_CARBOHYDRATE));
        data.food_fat = cursor.getString(cursor.getColumnIndex(tb.FOOD_FAT));
        data.food_protein = cursor.getString(cursor.getColumnIndex(tb.FOOD_PROTEIN));
        data.food_sugars = cursor.getString(cursor.getColumnIndex(tb.FOOD_SUGARS));
        data.food_salt = cursor.getString(cursor.getColumnIndex(tb.FOOD_SALT));
        data.food_cholesterol = cursor.getString(cursor.getColumnIndex(tb.FOOD_CHOLESTEROL));
        data.food_saturated = cursor.getString(cursor.getColumnIndex(tb.FOOD_SATURATED));
        data.food_transquanti = cursor.getString(cursor.getColumnIndex(tb.FOOD_TRANSQUANTI));

        return data;
    }
}
