package com.greencross.greencare.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.DummyActivity;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.bluetooth.fragment.DeviceManagementFragment;
import com.greencross.greencare.bluetooth.manager.DeviceManager;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.component.MainAddDialog;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperFoodMain;
import com.greencross.greencare.database.DBHelperPresure;
import com.greencross.greencare.database.DBHelperStep;
import com.greencross.greencare.database.DBHelperSugar;
import com.greencross.greencare.database.DBHelperWater;
import com.greencross.greencare.database.DBHelperWeight;
import com.greencross.greencare.food.FoodManageFragment;
import com.greencross.greencare.googleFitness.GoogleFitInstance;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_get_hedctdata;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.pressure.PressureManageFragment;
import com.greencross.greencare.sample.CircleProgressBar;
import com.greencross.greencare.step.StepManageFragment;
import com.greencross.greencare.sugar.SugarManageFragment;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.SharedPref;
import com.greencross.greencare.util.StringUtil;
import com.greencross.greencare.water.WaterManageFragment;
import com.greencross.greencare.weight.WeightManageFragment;

import java.text.DecimalFormat;
import java.util.List;

import static com.greencross.greencare.bluetooth.manager.DeviceManager.isRegDevice;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private final String TAG = MainAdapter.class.getSimpleName();

    private BaseFragment mBaseFragment;
    private List<MainCardData> mainCardList;
    private LinearLayout mCardTintLayout;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout progressLayout;
        public ImageView addItemIv;
        public CardView cardview;
        public CircleProgressBar progress;
        public TextView valueTv;
        public TextView titleTv;

        private LinearLayout foregroundLayout;

        public MyViewHolder(View view) {
            super(view);

            progressLayout = (FrameLayout) view.findViewById(R.id.main_progress_layout);
            addItemIv = (ImageView) view.findViewById(R.id.main_add_btn);

            foregroundLayout = (LinearLayout) view.findViewById(R.id.cardview_tint_layout);
            cardview = (CardView) view.findViewById(R.id.card_view);
            progress = (CircleProgressBar) itemView.findViewById(R.id.card_progressBar);
            valueTv = (TextView) itemView.findViewById(R.id.main_progress_value);
            titleTv = (TextView) itemView.findViewById(R.id.main_progress_title);

            foregroundLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mCardTintLayout = foregroundLayout;
                    }
                    return false;
                }
            });

        }
    }

    private void setBackTint(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mCardTintLayout = (LinearLayout) v.findViewById(R.id.cardview_tint_layout);
                return false;
            }
        });
    }


    public void setDragState(boolean isDrag) {
        if (mCardTintLayout != null) {
            if (isDrag)
                mCardTintLayout.setBackgroundResource(R.drawable.draw_circle_44444444);
            else
                mCardTintLayout.setBackgroundColor(Color.TRANSPARENT);
        }

        if (isDrag == false) {
            // 변경된 순서 Sqlite 저장
            DBHelper dbHelper = new DBHelper(mBaseFragment.getContext());
            int idx = 0;
            for (MainCardData data : mainCardList) {
                if (mainCardList != null) {
                    dbHelper.updateIdx(idx++, data.getCardE().name());
                }
            }

            if (Logger.mUseLogSetting)
                dbHelper.getResult();
        }
    }

    public MainAdapter(BaseFragment basefragment) {
        this.mBaseFragment = basefragment;
    }

    public void setData(List<MainCardData> mainCardList) {
        this.mainCardList = mainCardList;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_card_view, parent, false);
        itemView.setBackgroundColor(Color.TRANSPARENT);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        MainCardData mainCard = mainCardList.get(position);
        final MainCardData.CardE data = mainCard.getCardE();

        if (data == MainCardData.CardE.ADD) {
            // 추가버튼 세팅
            holder.progressLayout.setVisibility(View.INVISIBLE);
            holder.addItemIv.setVisibility(View.VISIBLE);   // 추가버튼 보이기
            holder.foregroundLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MainAddDialog(mBaseFragment.getContext(), mainCardList, new MainAddDialog.IAddDialog() {
                        @Override
                        public void onDismiss() {
                            ((MainFragment) mBaseFragment).prepareMainItems();
                        }
                    }).show();
                }
            });
            return;
        } else {
            holder.progressLayout.setVisibility(View.VISIBLE);
            holder.addItemIv.setVisibility(View.GONE);
        }

        holder.titleTv.setText(String.format("%,d", mainCard.getValue()));
        holder.titleTv.setTextColor(mainCard.getCardColor());
        holder.titleTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, mainCard.getCardImage(), 0, 0);

        holder.valueTv.setText(data.getCardName());
        if (data == MainCardData.CardE.WORKING || data == MainCardData.CardE.FOOD || data == MainCardData.CardE.WATER) {
        }

        holder.progress.setStrokeWidth(20f);   // 프로그래스 두께
        holder.progress.setColor(mainCard.getCardColor());

        holder.progress.setMax(mainCard.getMaxValue());         // 최대값
        holder.progress.setProgress(mainCard.getValue());       // 입력 값

        holder.foregroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveMenu(data);
            }
        });

        if (data == MainCardData.CardE.SUGAR) {
            setProgressSugar(holder.progress, holder.titleTv);
            holder.valueTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (data == MainCardData.CardE.PRESURE) {
            setProgressPressure(holder.progress, holder.titleTv);
            holder.valueTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (data == MainCardData.CardE.WORKING) {
            setProgressStep(holder.progress, holder.titleTv, holder);
        }

        if (data == MainCardData.CardE.WEIGHT) {
            setProgressWeight(holder.progress, holder.titleTv, holder);
        }

        if (data == MainCardData.CardE.FAT) {
            setProgressFat(holder.progress, holder.titleTv);
            holder.valueTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (data == MainCardData.CardE.WATER) {
            setProgressWater(holder.progress, holder.titleTv, holder);
        }

        if (data == MainCardData.CardE.FOOD) {
            setProgressFood(holder.progress, holder.titleTv, holder);
        }

        // 3개월치 데이터 넣기
        int reqDataType = position + 1;
    }

    private void moveMenu(MainCardData.CardE data) {
        Bundle bundle = new Bundle();
        if (data == MainCardData.CardE.WORKING) {
            showSelectStepSource();
//            SharedPref.getInstance().savePreferences(SharedPref.STEP_DATA_SOURCE_TYPE, Define.STEP_DATA_SOURCE_BAND);
//            mBaseFragment.movePage(StepManageFragment.newInstance());
        } else if (data == MainCardData.CardE.SUGAR) {
            mBaseFragment.movePage(SugarManageFragment.newInstance());
        } else if (data == MainCardData.CardE.PRESURE) {
            mBaseFragment.movePage(PressureManageFragment.newInstance());
        } else if (data == MainCardData.CardE.WEIGHT) {
            mBaseFragment.movePage(WeightManageFragment.newInstance());
        } else if (data == MainCardData.CardE.WATER) {
            mBaseFragment.movePage(WaterManageFragment.newInstance());
        } else if (data == MainCardData.CardE.FOOD) {
            mBaseFragment.movePage(FoodManageFragment.newInstance());
        } else if (data == MainCardData.CardE.FAT) {
            mBaseFragment.movePage(WeightManageFragment.newInstance());
        }
    }


    public void swap(int firstPosition, int secondPosition) {

        int maxPosition = firstPosition > secondPosition ? firstPosition : secondPosition;
        // 마지막 ADD 버튼에 영향 받지 않도록 처리
        if (maxPosition != mainCardList.size() - 1) {
            notifyItemMoved(firstPosition, secondPosition);

            // 데이터 정렬
            MainCardData firstData = mainCardList.get(firstPosition);
            MainCardData secondData = mainCardList.get(secondPosition);

            Logger.i(TAG, "before swap.firstPosition[" + firstPosition + "]=" + firstData.getCardE().name()
                    + ", secondPosition[" + secondPosition + "]=" + secondData.getCardE().name());

            mainCardList.remove(firstPosition);
            mainCardList.add(secondPosition, firstData);
        }

    }

    public void remove(int position) {
        mainCardList.remove(position);
        notifyItemRemoved(position);

        Logger.i(TAG, "remove.position=" + position);
        for (MainCardData data : mainCardList) {
            Logger.i(TAG, "remove=" + data.getCardE().name() + "\n");
        }
    }

    @Override
    public int getItemCount() {
        return mainCardList.size();
    }


    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            return false;
        }
    }


    /**
     * 혈당 프로그래스를 세팅
     *
     * @param tv
     */
    private void setProgressSugar(CircleProgressBar progress, TextView tv) {

        DBHelper helper = new DBHelper(mBaseFragment.getContext());
        DBHelperSugar sugarDb = helper.getSugarDb();
        String sugar = sugarDb.getResultMain(helper);
        sugar = TextUtils.isEmpty(sugar) ? "0" : sugar;
        float val = Float.parseFloat(sugar);
        tv.setText((int) val + "mg/dl");

        progress.setProgress(100);   // 프로그래스 현재값 세팅
        progress.setMax(100);   // 프로그래스 최대치 세팅
    }

    /**
     * 혈압 프로그래스를 세팅
     *
     * @param tv
     */
    private void setProgressPressure(CircleProgressBar progress, TextView tv) {
        DBHelper helper = new DBHelper(mBaseFragment.getContext());
        DBHelperPresure db = helper.getPresureDb();
        DBHelperPresure.PressureData data = db.getResultMain(helper);
        String result = String.format("%,d", data.getSystolic()) + "/" + String.format("%,d", data.getDiastolc());
        tv.setText(result);

        progress.setProgress(100);   // 프로그래스 현재값 세팅
        progress.setMax(100);   // 프로그래스 최대치 세팅
    }

    /**
     * 걸음 데이터 조회(구글피트니스, 밴드)
     *
     * @param tv
     */
    private void setProgressStep(final CircleProgressBar progress, final TextView tv, final MyViewHolder holder) {
        int sourceType = SharedPref.getInstance().getPreferences(SharedPref.STEP_DATA_SOURCE_TYPE, -1);

        if (Define.STEP_DATA_SOURCE_GOOGLE_FIT == sourceType) {
            // 구글 피트니스로 조회
            Logger.i(TAG, "STEP_DATA_SOURCE_GOOGLE_FIT");
            buildFitnessClientApis(new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    mBaseFragment.hideProgress();
                    Long step = (Long) obj;
                    setStepField("" + step, progress, tv, holder);
                }
            });
        } else if (Define.STEP_DATA_SOURCE_BAND == sourceType) {
            // 스마트 밴드로 조회
            DBHelper helper = new DBHelper(mBaseFragment.getContext());
            DBHelperStep stepDb = helper.getStepDb();
            String step = stepDb.getResultMain(helper);
            setStepField(step, progress, tv, holder);
        }
    }

    public GoogleApiClient mClient = null;

    public void buildFitnessClientApis(final ApiData.IStep callBack) {
        mBaseFragment.showProgress();
        if (mClient != null) {
            new GoogleFitInstance(mBaseFragment).totalValDataTask(mClient, DataType.TYPE_STEP_COUNT_DELTA, callBack);
            return;
        }
        Logger.i(TAG, "buildFitnessClientApis mClient is null now Create");

        mClient = new GoogleApiClient.Builder(mBaseFragment.getContext())
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                mBaseFragment.hideProgress();
                                Logger.i(TAG, "buildFitnessClientApis Connected!!! ");
                                new GoogleFitInstance(mBaseFragment).findFitnessDataSources(mClient, new ApiData.IStep() {
                                    @Override
                                    public void next(Object obj) {
                                        new GoogleFitInstance(mBaseFragment).totalValDataTask(mClient, DataType.TYPE_STEP_COUNT_DELTA, callBack);
                                    }
                                });
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Logger.i(TAG, "buildFitnessClientApis Connection lost.  Cause: Network Lost.");
                                    CDialog.showDlg(mBaseFragment.getContext(), "Google 서비스 연결에 실패 했습니다.(NETWORK_LOST)");

                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Logger.i(TAG, "buildFitnessClientApis Connection lost.  Reason: Service Disconnected");
                                    CDialog.showDlg(mBaseFragment.getContext(), "Google 서비스 연결에 실패 했습니다.(Disconnected)");
                                }
                            }
                        })
                .enableAutoManage(mBaseFragment.getActivity(), 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Logger.e(TAG, "buildFitnessClientApis connection failed. Cause: " + result.toString());
                        Logger.e(TAG, "buildFitnessClientApis connection failed. getErrorCode: " + result.getErrorCode());
                        Logger.e(TAG, "buildFitnessClientApis connection failed. getErrorMessage: " + result.getErrorMessage());
                        Logger.e(TAG, "buildFitnessClientApis connection failed. getResolution: " + result.getResolution());
                        Logger.e(TAG, "buildFitnessClientApis connection failed. Cause: " + result.isSuccess());
                        mBaseFragment.hideProgress();

                        if (ConnectionResult.CANCELED == result.getErrorCode()) {
                            CDialog.showDlg(mBaseFragment.getContext(), "계정인증 후 이용 가능합니다.", new CDialog.DismissListener() {
                                @Override
                                public void onDissmiss() {
                                    SharedPref.getInstance().savePreferences(SharedPref.STEP_DATA_SOURCE_TYPE, -1);
                                    mBaseFragment.restartMainActivity();
                                }
                            });
                        } else {
                            CDialog.showDlg(mBaseFragment.getContext(), "Google 서비스 연결에 실패 했습니다.(" + result.toString() + ")");
                        }
                    }
                })
                .build();
    }

    public void onDetach() {
        Logger.i(TAG, "onDetach.mClient=" + mClient);
        if (mClient != null) {
            Logger.i(TAG, "onDetach.mClient=" + mClient + ", isConnected()=" + mClient.isConnected());
            mClient.stopAutoManage(mBaseFragment.getActivity());
            mClient.disconnect();
            mClient = null;
        }
    }

    /**
     * 구글피트니스 또는 밴드에서 조회 된 데이터
     * 프로그래스 화면에 세팅
     *
     * @param step
     * @param progress
     * @param tv
     * @param holder
     */
    private void setStepField(String step, CircleProgressBar progress, TextView tv, MyViewHolder holder) {
        Tr_login login = Define.getInstance().getLoginInfo();

        float mvmStep = StringUtil.getFloatVal(login.goal_mvm_stepcnt);
        float stepVal = StringUtil.getFloatVal(step);

        tv.setText(makeStringComma(Integer.toString((int) stepVal).replace(",", "")));
        holder.valueTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.mian_frag, 0, 0, 0);
        holder.valueTv.setText(" " + makeStringComma(Integer.toString((int) mvmStep).replace(",", "")) + "걸음");

        if (mvmStep == 0.f) {
            stepVal = 0;
        }

        progress.setProgress(stepVal);   // 프로그래스 현재값 세팅
        progress.setMax((int) mvmStep);
    }


    /**
     * 물
     *
     * @param tv
     */
    private void setProgressWater(CircleProgressBar progress, TextView tv, MyViewHolder holder) {
        DBHelper helper = new DBHelper(mBaseFragment.getContext());
        DBHelperWater waterDb = helper.getWaterDb();
        String water = waterDb.getResultMain();

        Tr_login login = Define.getInstance().getLoginInfo();

        float waterVal = StringUtil.getFloatVal(water);
        float water_goal = StringUtil.getFloatVal(login.goal_water_ntkqy);

        tv.setText(makeStringComma(Integer.toString((int) waterVal).replace(",", "")));
        holder.valueTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.mian_frag, 0, 0, 0);
        holder.valueTv.setText(" " + makeStringComma(Integer.toString((int) water_goal).replace(",", "")) + "ml");

        if ((int) water_goal == 0) {
            waterVal = 0.f;
        }

        progress.setProgress(waterVal);   // 프로그래스 현재값 세팅
        progress.setMax((int) water_goal);
    }

    public String makeStringComma(String str) {
        if (str.length() == 0)
            return "";
        long value = Long.parseLong(str);
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(value);
    }

    /**
     * 식사
     *
     * @param tv
     */
    private void setProgressFood(CircleProgressBar progress, TextView tv, MyViewHolder holder) {
        DBHelper helper = new DBHelper(mBaseFragment.getContext());
        DBHelperFoodMain foodDb = helper.getFoodMainDb();
        String caloria = foodDb.getResultMain();

        Tr_login login = Define.getInstance().getLoginInfo();

        float cal_val = StringUtil.getFloatVal(caloria);
        float cal_goal = StringUtil.getFloatVal(login.goal_mvm_calory);
        int rActqy = Integer.parseInt(login.mber_actqy);
        int rSex = Integer.parseInt(login.mber_sex);
        float rWeight = 0.0f;
        DBHelperWeight WeightDb = helper.getWeightDb();
        DBHelperWeight.WeightStaticData bottomData = WeightDb.getResultStatic();

        if(bottomData.getWeight().isEmpty()){
            rWeight = Float.parseFloat(login.mber_bdwgh);
        }else{
            rWeight = Float.parseFloat(login.mber_bdwgh_app);
        }

        float rHeight = Float.parseFloat(login.mber_height);
        rHeight = rHeight * 0.01f;

        float mWeight;   //표준체중
        float mHeight = rHeight;
        mHeight = StringUtil.getFloatVal(String.format("%.2f", mHeight));

        if (rSex == 2){
            //여성
            mWeight = StringUtil.getFloatVal(String.format("%.1f",(mHeight*mHeight) *21));
        }else {
            //남성
            mWeight = StringUtil.getFloatVal(String.format("%.1f",(mHeight*mHeight) *22));
        }

        float re = rWeight/(rHeight * rHeight);
        float fat = 0.f;
        if(re < 18.5){
            //"저체중"
            if(rActqy == 1){
                fat = 35.f;
            }else if(rActqy == 2){
                fat = 40.f;
            }else if(rActqy == 3){
                fat = 45.f;
            }
        }else if(re >= 18.5 && re <=22.9){
            //"정상";
            if(rActqy == 1){
                fat = 30.f;
            }else if(rActqy == 2){
                fat = 35.f;
            }else if(rActqy == 3){
                fat = 40.f;
            }
        }else if(re >= 23.0) {
            //"과체중";
            if (rActqy == 1) {
                fat = 25.f;
            } else if (rActqy == 2) {
                fat = 30.f;
            } else if (rActqy == 3) {
                fat = 35.f;
            }
        }

        int recomCal = (int)(mWeight * fat);  // 권장섭취량

        tv.setText(makeStringComma(Integer.toString((int) cal_val).replace(",", "")));
        holder.valueTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.mian_frag, 0, 0, 0);
        holder.valueTv.setText(" " + makeStringComma(Integer.toString(recomCal).replace(",", "")) + "Kcal");

        if (recomCal == 0) {
            cal_val = 0.f;
        }

        progress.setProgress(cal_val);   // 프로그래스 현재값 세팅
        progress.setMax(recomCal);
    }

    /**
     * 체중 프로그래스를 세팅
     *
     * @param tv
     */
    private void setProgressWeight(CircleProgressBar progress, TextView tv, MyViewHolder holder) {
        DBHelper helper = new DBHelper(mBaseFragment.getContext());
        DBHelperWeight db = helper.getWeightDb();
        Tr_get_hedctdata.DataList dataList = db.getResultMain();
        Tr_login login = Define.getInstance().getLoginInfo();

        float weight = StringUtil.getFloatVal(dataList.weight);
        float weight_goal = StringUtil.getFloatVal(login.mber_bdwgh_goal);

        tv.setText(StringUtil.getNoneZeroString(weight) + "kg");
        holder.valueTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.mian_frag, 0, 0, 0);
        holder.valueTv.setText(" " + Integer.toString((int) weight_goal) + "Kg");

        if ((int) weight == 0.f || (int) weight_goal == 0) {
            progress.setProgress(0);
            progress.setMax(0);
        } else if ((int) weight_goal > (int) weight) {
            progress.setProgress(weight);
            progress.setMax((int) weight_goal);
        } else {
            progress.setProgress(weight_goal);
            progress.setMax((int) weight);
        }
    }

    /**
     * 체중 프로그래스를 세팅
     *
     * @param tv
     */
    private void setProgressFat(CircleProgressBar progress, TextView tv) {
        DBHelper helper = new DBHelper(mBaseFragment.getContext());
        DBHelperWeight db = helper.getWeightDb();
        Tr_get_hedctdata.DataList dataList = db.getResultMainFat();

        float fat = StringUtil.getFloatVal(dataList.fat);
        tv.setText(StringUtil.getFloatVal(String.format("%.1f", fat)) + "%");

        progress.setProgress(100);   // 프로그래스 현재값 세팅
        progress.setMax(100);   // 프로그래스 최대치 세팅
    }

    /**
     * 걸음 데이터 소스 선택
     */
    private CDialog mStepDlg;

    private void showSelectStepSource() {
        int dataSource = SharedPref.getInstance().getPreferences(SharedPref.STEP_DATA_SOURCE_TYPE, -1);
        if (dataSource != -1) {
            if (Define.STEP_DATA_SOURCE_BAND == dataSource) {
                boolean isRegist = DeviceManager.isRegDevice(mBaseFragment.getContext(), DeviceManager.FLAG_BLE_DEVICE_BAND);
                if (isRegist) {
                    mBaseFragment.movePage(StepManageFragment.newInstance());
                } else {
                    SharedPref.getInstance().savePreferences(SharedPref.STEP_DATA_SOURCE_TYPE, -1);
                    showSelectStepSource();
                }
            } else {
                mBaseFragment.movePage(StepManageFragment.newInstance());
            }

            return;
        }

        View.OnClickListener dlgClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int vId = v.getId();

                if (mStepDlg != null) {
                    mStepDlg.dismiss();
                }
                if (R.id.step_source_google_btn == vId) {
                    buildFitnessClientApis(new ApiData.IStep() {
                        @Override
                        public void next(Object obj) {
                            SharedPref.getInstance().savePreferences(SharedPref.STEP_DATA_SOURCE_TYPE, Define.STEP_DATA_SOURCE_GOOGLE_FIT);
                            mBaseFragment.movePage(StepManageFragment.newInstance());
                        }
                    });

                } else if (R.id.step_source_band_btn == vId) {
                    if (isRegDevice(mBaseFragment.getContext(), DeviceManager.FLAG_BLE_DEVICE_BAND)) {
                        SharedPref.getInstance().savePreferences(SharedPref.STEP_DATA_SOURCE_TYPE, Define.STEP_DATA_SOURCE_BAND);
                        mBaseFragment.movePage(StepManageFragment.newInstance());
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(DeviceManagementFragment.IS_STEP_DEVICE_REGIST, true);
                        DummyActivity.startActivity(mBaseFragment, DeviceManagementFragment.class, bundle);
                    }

                }
            }
        };

        /**
         * 데이터소스 선택
         */
        final View view = LayoutInflater.from(mBaseFragment.getContext()).inflate(R.layout.dialog_listview_step, null);
        view.findViewById(R.id.step_source_band_btn).setOnClickListener(dlgClickListener);
        view.findViewById(R.id.step_source_google_btn).setOnClickListener(dlgClickListener);

        mStepDlg = CDialog.showDlg(mBaseFragment.getContext(), view);
        mStepDlg.setTitle("걸음 소스 선택");
    }
}
