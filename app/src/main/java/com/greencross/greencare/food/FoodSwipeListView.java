package com.greencross.greencare.food;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.component.swipeListview.SwipeMenu;
import com.greencross.greencare.component.swipeListview.SwipeMenuCreator;
import com.greencross.greencare.component.swipeListview.SwipeMenuItem;
import com.greencross.greencare.component.swipeListview.SwipeMenuListView;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperFoodCalorie;
import com.greencross.greencare.database.DBHelperFoodDetail;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.network.tr.data.Tr_meal_input_food_del_data;
import com.greencross.greencare.network.tr.data.Tr_meal_input_food_edit_data;
import com.greencross.greencare.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import static com.greencross.greencare.component.CDialog.showDlg;
import static com.greencross.greencare.food.FoodSearchFragment.BUNDLE_FOOD_DETAIL_INFO;

/**
 * Created by MrsWin on 2017-04-15.
 */

public class FoodSwipeListView {
    private static final String TAG = SwipeMenuListView.class.getSimpleName();
    private AppAdapter mAdapter;
    private List<DBHelperFoodCalorie.Data> mSwipeMenuDatas = new ArrayList<>();
    private BaseFragment mBaseFragment;
    private Context mContext;
    private RelativeLayout mEmptyView;
    private SwipeMenuListView listView;

    public FoodSwipeListView(View view, BaseFragment baseFragment) {
        mBaseFragment = baseFragment;
        mContext = baseFragment.getContext();
        listView = (SwipeMenuListView) view.findViewById(R.id.food_eat_listview);
        mEmptyView = (RelativeLayout) view.findViewById(R.id.food_eat_empty_view);

        mAdapter = new AppAdapter();
        listView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                createMenu1(menu);
            }

            private void createMenu1(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(mContext);
                item1.setBackground(new ColorDrawable(Color.BLACK));//new ColorDrawable(Color.rgb(0xE5, 0x18, 0x5E)));
                item1.setWidth(dp2px(60));
                item1.setIcon(R.drawable.btn_swipe_edit);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(mContext);
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
                        DBHelperFoodCalorie.Data data = (DBHelperFoodCalorie.Data) mAdapter.getItem(position);
                        showCalorieDlg(position, data);
                        break;
                    case 1:
                        String message = mContext.getString(R.string.text_alert_mesage_delete);
                        showDlg(mContext, message, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DBHelperFoodCalorie.Data data = (DBHelperFoodCalorie.Data) mAdapter.getItem(position);
                                doDeleteFoodData(data);
                                setListViewChage();
                            }
                        }, null);

                        break;
                }

                return true;
            }
        });

        setListViewChage();
    }

    /**
     * 음식 상세 Dialog
     *
     * @param data
     */
    private FoodDetailDialog mFoodDlg;

    private void showCalorieDlg(final int position, final DBHelperFoodCalorie.Data data) {
        mFoodDlg = new FoodDetailDialog(mBaseFragment.getContext(), data, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelperFoodCalorie.Data FoodData = mFoodDlg.getFoodData();
                Intent intent = new Intent();
                intent.putExtra(BUNDLE_FOOD_DETAIL_INFO, FoodData);
                if (TextUtils.isEmpty(FoodData.food_idx)) {
                    mSwipeMenuDatas.set(position, FoodData);
                    mAdapter.notifyDataSetChanged();
                } else {
                    doModifyFoodData(FoodData, "1", position);
                }

            }
        });
    }

    /**
     * 음식정보 수정하기
     *
     * @param foodData
     */
    private void doModifyFoodData(final DBHelperFoodCalorie.Data foodData, String forPeople, final int position) {
        // 수정하기
        final Tr_meal_input_food_edit_data.RequestData requestData = new Tr_meal_input_food_edit_data.RequestData();
        Tr_login login = Define.getInstance().getLoginInfo();
        requestData.idx = foodData.food_idx;
        requestData.mber_sn = login.mber_sn;
        requestData.foodcode = foodData.food_code;
        requestData.forpeople = forPeople;

        mBaseFragment.getData(mBaseFragment.getContext(), Tr_meal_input_food_edit_data.class, requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_meal_input_food_edit_data) {
                    final Tr_meal_input_food_edit_data data = (Tr_meal_input_food_edit_data) obj;
                    if ("Y".equals(data.reg_yn)) {
                        // 식사일지 등록하기
                        DBHelper helper = new DBHelper(mBaseFragment.getContext());
                        DBHelperFoodDetail db = helper.getFoodDetailDb();
                        db.update(requestData, true);

                        CDialog.showDlg(mBaseFragment.getContext(), "수정이 완료 되었습니다.", new CDialog.DismissListener() {
                            @Override
                            public void onDissmiss() {
                                mSwipeMenuDatas.set(position, foodData);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        CDialog.showDlg(mBaseFragment.getContext(), "수정에 실패 하였습니다.");
                    }
                }
            }
        });
    }

    /**
     * 음식 데이터 삭제
     *
     * @param foodData
     */
    private void doDeleteFoodData(final DBHelperFoodCalorie.Data foodData) {
        if (TextUtils.isEmpty(foodData.food_idx)) {
            // 수정하기
            mSwipeMenuDatas.remove(foodData);
            mAdapter.notifyDataSetChanged();
        } else {
            // 등록하기
            Tr_meal_input_food_del_data.RequestData requestData = new Tr_meal_input_food_del_data.RequestData();
            Tr_login login = Define.getInstance().getLoginInfo();
            requestData.mber_sn = login.mber_sn;
            requestData.idx = foodData.food_idx;
            requestData.foodcode = foodData.food_code;

            mBaseFragment.getData(mContext, Tr_meal_input_food_del_data.class, requestData, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (obj instanceof Tr_meal_input_food_del_data) {
                        Tr_meal_input_food_del_data data = (Tr_meal_input_food_del_data) obj;
                        if ("Y".equals(data.reg_yn)) {
                            mSwipeMenuDatas.remove(foodData);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            CDialog.showDlg(mContext, "삭제에 실패 하였습니다.");
                        }
                    }
                }
            });
        }
    }

    private void setListViewChage() {
        if (mSwipeMenuDatas.size() <= 0) {
            listView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                mContext.getResources().getDisplayMetrics());
    }

    public void setData(DBHelperFoodCalorie.Data data) {
        mSwipeMenuDatas.add(data);
        setListViewChage();
        mAdapter.notifyDataSetChanged();
    }

    public void setDataList(List<DBHelperFoodCalorie.Data> data) {
        mSwipeMenuDatas.addAll(data);
        setListViewChage();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 칼로리 총합을 리턴한다
     *
     * @return
     */
    public int getSumCalorieData() {
        int calorie = 0;
        for (DBHelperFoodCalorie.Data data : mSwipeMenuDatas) {
            data.food_calorie = TextUtils.isEmpty(data.food_calorie) ? "0" : data.food_calorie;
            float calorieF = Float.parseFloat(data.food_calorie);
            calorie = (int) calorieF;
        }

        return calorie;
    }

    /**
     * @return
     */
    public List<DBHelperFoodCalorie.Data> getListData() {
        return mSwipeMenuDatas;
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
                convertView = View.inflate(mContext, R.layout.swipe_food_item_view, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            DBHelperFoodCalorie.Data data = (DBHelperFoodCalorie.Data) getItem(position);

            holder.nameTv.setText(data.food_name);

            float forPeople = StringUtil.getFloatVal(data.forpeople);
            float foodGram = StringUtil.getFloat(data.food_gram);
            float calorie = StringUtil.getFloat(data.food_calorie);
            String unit = data.food_unit;
            foodGram = (foodGram * forPeople);

            String txtForPeople = StringUtil.getNoneZeroString2(forPeople);
            String txtFoodGram = StringUtil.getNoneZeroString2(foodGram);
            String txtCalorie = StringUtil.getNoneZeroString2(calorie);

            String text = txtForPeople + "회 분량(" + txtFoodGram + unit.trim() + "), " + StringUtil.getNoneZeroString2(calorie * forPeople) + "kcal";
            holder.calorieTv.setText(text);

            return convertView;
        }

        class ViewHolder {
            TextView nameTv;
            TextView calorieTv;

            public ViewHolder(View view) {
                nameTv = (TextView) view.findViewById(R.id.food_list_name_textview);
                calorieTv = (TextView) view.findViewById(R.id.food_list_calorie_textview);

                view.setTag(this);
            }
        }
    }

}
