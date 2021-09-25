package com.greencross.greencare.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.greencross.greencare.MainActivity;
import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.IBaseFragment;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.bluetooth.model.MessageModel;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.component.MultiDirectionSlidingDrawer;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperMessage;
import com.greencross.greencare.database.DBHelperWeight;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_get_dust;
import com.greencross.greencare.network.tr.data.Tr_infra_del_message;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.sample.SampleFragment;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.SharedPref;
import com.greencross.greencare.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.greencross.greencare.component.CDialog.showDlg;


/**
 * Created by MrsWin on 2017-02-16.
 */

public class MainFragment extends BaseFragment implements IBaseFragment {
    private final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView mSlideRecyclerView;
    private MainAdapter mMainRecyclerAdapter;
    private List<MainCardData> mainCardList = new ArrayList<>();

    private SlideAdapter mSlideAdapter;

    private Button mCloseButton;
    private Button mOpenButton;
    private View mSlideViewBtn;
    private ImageView mSlideImgBtn;
    private MultiDirectionSlidingDrawer mSlideDrawer;
    private View mBlockView;
    private TextView mSlideNewIcon;
    private TextView txtCityName;
    private TextView mDustStusTv;
    private TextView mTempTv;
    private TextView mWeatherTv;
    private TextView mBmiResultTv;
    private TextView tvBasicCalory;
    private TextView tvWeight;
    private TextView tvHeight;
    private TextView tvBmi;

    private BluetoothManager mBluetoothManager;

    public static BaseFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        return view;
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBackBtnVisible(View.INVISIBLE);
        actionBar.setActionBarTitle(getString(R.string.text_main));
        actionBar.setActionBarDeviceBtn(MainFragment.this);   // 블루투스 기기 버튼
        actionBar.setActionBarSettingBtn(MainFragment.this);  // 설정 버튼
    }
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // XXX 백으로 데이터 보내기
        Bundle bundle = new Bundle();
        bundle.putString(SampleFragment.SAMPLE_BACK_DATA, TAG+" BackData!!!");
        setBackData(bundle);

        recyclerView = (RecyclerView) view.findViewById(R.id.main_recycler_view);
        mSlideRecyclerView = (RecyclerView) view.findViewById(R.id.main_slide_recycler_view);

        mSlideNewIcon = (TextView)view.findViewById(R.id.main_slide_new_imageview);

        mSlideViewBtn = view.findViewById( R.id.main_slide_view_btn );
        mSlideImgBtn = (ImageView) view.findViewById( R.id.main_slide_imageview );

        mSlideDrawer = (MultiDirectionSlidingDrawer) view.findViewById( R.id.main_slide_drawer);
        mBlockView = view.findViewById(R.id.slide_block_layout);
        mSlideDrawer.setBlockView(mBlockView);
        mSlideDrawer.setControlImgBtn(mSlideImgBtn);
        mSlideDrawer.setMainFragment(MainFragment.this);
        mMainRecyclerAdapter = new MainAdapter(MainFragment.this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mMainRecyclerAdapter);

        mSlideAdapter = new SlideAdapter();
        mSlideRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSlideRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSlideRecyclerView.setAdapter(mSlideAdapter);

        // RecyclerView Line
        mSlideRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        // Swipe 세팅
        ItemTouchHelper.Callback callback = new MainAdapterTouchHelper(mMainRecyclerAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

        mSlideViewBtn.setOnClickListener(mOnClickListener);
        mSlideImgBtn.setOnClickListener(mOnClickListener);
        mBlockView.setOnClickListener(mOnClickListener);

        Tr_login login = Define.getInstance().getLoginInfo();
        tvWeight = (TextView)view.findViewById(R.id.tvWeight);
        tvHeight = (TextView)view.findViewById(R.id.main_height_tv);
        tvBmi = (TextView)view.findViewById(R.id.main_bmi_tv);
        tvBasicCalory = (TextView)view.findViewById(R.id.main_basic_calory_tv);
        txtCityName = (TextView)view.findViewById(R.id.txtCityName);
        mDustStusTv = (TextView)view.findViewById(R.id.main_dust_status_tv);
        mTempTv = (TextView)view.findViewById(R.id.main_temp_tv);
        mWeatherTv = (TextView)view.findViewById(R.id.txtWeather);
        mBmiResultTv = (TextView)view.findViewById(R.id.main_bmi_result_tv);



        DBHelper mhelper = new DBHelper(getContext());
        DBHelperWeight WeightDb = mhelper.getWeightDb();
        DBHelperWeight.WeightStaticData bottomData = WeightDb.getResultStatic();

        if(login.mber_bdwgh_app.equals("") || login.mber_bdwgh_app.isEmpty() || bottomData.getWeight().isEmpty()){
            login.mber_bdwgh_app = login.mber_bdwgh;
            Define.getInstance().setLoginInfo(login);
        }

        getDustInfo(true);
    }

    private String getBasicCalori(){

        Tr_login login = Define.getInstance().getLoginInfo();
        int rHeight = Integer.parseInt(login.mber_height);
        float rWeight = 0.f;

        DBHelper helper = new DBHelper(getContext());
        DBHelperWeight WeightDb = helper.getWeightDb();
        DBHelperWeight.WeightStaticData bottomData = WeightDb.getResultStatic();
        if(!bottomData.getWeight().isEmpty()) {
            rWeight = Float.parseFloat(bottomData.getWeight());
        }else if(bottomData.getWeight().isEmpty()){
            rWeight = Float.parseFloat(login.mber_bdwgh);
        }else{
            rWeight = Float.parseFloat(login.mber_bdwgh_app);
        }

        int rSex = Integer.parseInt(login.mber_sex);
        int rAge = Integer.parseInt(login.mber_lifyea.substring(0,4));
        String result = "";
        String nowYear = CDateUtil.getFormattedString_yyyy(System.currentTimeMillis());

        float Float_result = 0.0f;
        if(rSex == 1){
            Float_result = (float) (66.47f + (13.75f * rWeight) + (5.0f * rHeight) - (6.76f * ((Float.parseFloat(nowYear) - rAge)+1)));
        }else{
            Float_result = (float) (655.1f + (9.56f * rWeight) + (1.85f * rHeight) - (4.68f * ((Float.parseFloat(nowYear) - rAge)+1)));
        }

        result = Integer.toString((int)Float_result);
        return result;
    }

    private void doDust(String zoneName) {
        String zonCode = DustManager.getDustLocation(zoneName);

        Tr_get_dust.RequestData requestData = new Tr_get_dust.RequestData();
        requestData.dust_sn = zonCode;
        getData(getContext(), Tr_get_dust.class, requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_get_dust) {
                    Tr_get_dust data = (Tr_get_dust)obj;
                    Define.getInstance().setDustData(data);
                    mDustStusTv.setText(DustManager.getDustStatusStr(data));
                }
            }
        });
    }


    private void setInit() {
        int healthMsgCnt = -1;
        Define.getInstance().setHealthMessageCnt(healthMsgCnt);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.main_slide_view_btn || vId == R.id.slide_block_layout || vId == R.id.main_slide_imageview) {
                if( mSlideDrawer.isOpened())
                    mSlideDrawer.animateClose();
                else
                    mSlideDrawer.animateOpen();
            }
        }
    };

    public void prepareMainItems() {
        DBHelper dbHelper = new DBHelper(getContext());
        List<MainCardData> data = dbHelper.getResult();

        if (mainCardList.size() != 0)
            mainCardList.clear();

        mainCardList.addAll(data);
        Log.i(TAG, "mainCardList.size="+mainCardList.size());
        mMainRecyclerAdapter.setData(mainCardList);
    }

    @Override
    public void onBackPressed() {
        Logger.i(TAG, "onBackPressed.mSlideDrawer.isOpened()="+mSlideDrawer.isOpened());
        if (mSlideDrawer.isOpened()) {
            mSlideDrawer.animateClose();
        } else {
            finishStep();
        }
    }

    public void slideNewIconInvisible() {
        if (mSlideAdapter != null)
            mSlideAdapter.clearNewIcon();
    }

    class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideViewHolder> {
        List<MessageModel> mMessage_list = new ArrayList<>();

        @Override
        public SlideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_slide_list_adapter, parent, false);
            return new SlideViewHolder(itemView);
        }

        public void setData(List<MessageModel> message_list) {
            mMessage_list.clear();
            mMessage_list.addAll(message_list);

            notifyDataSetChanged();
        }

        public void clearNewIcon() {
            mSlideNewIcon.setVisibility(View.INVISIBLE);
            SharedPref.getInstance().savePreferences(SharedPref.HEALTH_MESSAGE, false);
        }

        @Override
        public void onBindViewHolder(SlideViewHolder holder, final int position) {
            MessageModel msg = mMessage_list.get(position);

            String str = msg.getMessage();

            if (str.contains("[고혈압성 응급 주의]")) {
                int int1 = str.indexOf("[고혈압성 응급 주의]");
                int int2 = str.indexOf("고혈압성 응급의 경우 해당 전문의의 치료가 필요합니다. 반드시 병원에 방문하세요.");
                String str1 = str.substring(0, int1 - 1);
                String str2 = str.substring(int1, int1 + "[고혈압성 응급 주의]".length());
                String str3 = str.substring(int1 + "[고혈압성 응급 주의]".length() + 1, int2 - 1);
                String str4 = str.substring(int2, int2 + "고혈압성 응급의 경우 해당 전문의의 치료가 필요합니다. 반드시 병원에 방문하세요.".length());
                String str5 = "";

                if(int2 + "고혈압성 응급의 경우 해당 전문의의 치료가 필요합니다. 반드시 병원에 방문하세요.".length() < str.length()) {
                    str5 = str.substring(int2 + "고혈압성 응급의 경우 해당 전문의의 치료가 필요합니다. 반드시 병원에 방문하세요.".length());
                }

                holder.contentTextView.setText(Html.fromHtml(str1.replaceAll("\n", "<br>")
                        + "<br><font color=\"#EE0029\">"
                        + str2
                        + "</font><br>"
                        + str3.replaceAll("\n", "<br>")
                        + "<font color=\"#EE0029\">"
                        + str4
                        + "</font>"
                        + str5.replaceAll("\n", "<br>")
                ));
            } else {
                holder.contentTextView.setText(msg.getMessage());
            }

            msg.getRegdate().substring(5,16);

            holder.dateTextView.setText(msg.getRegdate().substring(5, 16));
            holder.deleteButton.setOnClickListener(new MsgOnClickListener(position) {
                @Override
                public void onClick(View v) {
                    showDlg(getContext(), "삭제하시겠습니까?", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MessageModel msg = mMessage_list.get(position);
                            doDeleteData(position, msg);
                        }
                    }, null);
                }
            });

        }

        public abstract class MsgOnClickListener implements View.OnClickListener {

            protected int index;

            public MsgOnClickListener(int index) {
                this.index = index;
            }
        }

        /**
         * 건강메세지 삭제하기
         *
         * @param model
         */
        private void doDeleteData(final int position, final MessageModel model) {

            Tr_infra_del_message.RequestData requestData    = new Tr_infra_del_message.RequestData();
            Tr_login login                                  = Define.getInstance().getLoginInfo();

            requestData.mber_sn = login.mber_sn;
            requestData.idx = model.getIdx();

            getData(getContext(), Tr_infra_del_message.class, requestData, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (obj instanceof Tr_infra_del_message) {
                        Tr_infra_del_message data = (Tr_infra_del_message) obj;
                        if ("Y".equals(data.data_yn)) {

                            DBHelper helper         = new DBHelper(getContext());
                            DBHelperMessage msgDb   = helper.getMessageDb();
                            msgDb.DeleteDb(model.getIdx());

                            mMessage_list.remove(position);
                            notifyDataSetChanged();

                            CDialog.showDlg(getContext(), getContext().getString(R.string.delete_success));
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mMessage_list == null ? 0 : mMessage_list.size();
        }

        class SlideViewHolder extends RecyclerView.ViewHolder {
            public TextView contentTextView;
            public TextView dateTextView;
            public ImageButton  deleteButton;
            public SlideViewHolder(View itemView) {
                super(itemView);

                contentTextView = (TextView) itemView.findViewById(R.id.main_slide_adapter_content_textview);
                dateTextView = (TextView) itemView.findViewById(R.id.main_slide_adapter_date_textview);
                deleteButton = (ImageButton) itemView.findViewById(R.id.main_slide_adapter_delete_button);
            }
        }
    }

    private void setBmiResult(float weight, float height) {
        String rtStr = "";
        float h = height * 0.01f;
        float re = weight/(h*h);
        String bmiLevel ="";
        if(re < 18.5){
            rtStr="저체중";
            bmiLevel = "0";
        }else if(re >= 18.5 && re <=22.9){
            rtStr="정상";
            bmiLevel = "1";
        }else if(re >= 23.0 && re <=24.9){
            rtStr="과체중";
            bmiLevel = "2";
        }else if(re >= 25.5 && re <=29.9){
            rtStr="비만";
            bmiLevel = "3";
        }else if(re >= 30.0){
            rtStr="고도비만";
            bmiLevel = "4";
        }

        Tr_login login = Define.getInstance().getLoginInfo();
        login.mber_bmi = Float.toString(re);
        login.mber_bmi_level = bmiLevel;
        Define.getInstance().setLoginInfo(login);

        mBmiResultTv.setText("("+rtStr+")");
        tvBmi.setText(String.format("%.1f", re));
    }


    /**
     * 미세먼지, 날씨 데이터 가져오기
     */
    private void getDustInfo(final boolean isInit) {
        Tr_login loginData = Define.getInstance().getLoginInfo();
        String zoneName = DustManager.getCityName(loginData.mber_zone);//loginData.mber_zone
        txtCityName.setText(zoneName);

        int savedCal = Define.getInstance().getWeatherRequestedTime(); // 조회 했던 시간
        final Calendar nowCal = Calendar.getInstance();             //

        if (savedCal != -1) {
            int nowHour = nowCal.get(Calendar.HOUR_OF_DAY);

            Logger.i(TAG, "getDustInfo.savedHour="+savedCal+", nowHour="+nowHour);
            // 조회 했던 시간과 현재 시간이 동일하면 리턴
            if (savedCal == nowHour) {
                startBluetoothListener(isInit);

                Tr_get_dust dust = Define.getInstance().getDustData();
                mDustStusTv.setText(DustManager.getDustStatusStr(dust));

                DustManager.MsnWeatherData weather = Define.getInstance().getWeatherData();
                String watherStr ="-";
                String tempStr ="-";
                // 날씨정보 못가져오는 경우 "-" 처리.
                if (weather.weather != null && !weather.weather.equals("null")) watherStr = weather.weather;
                if (weather.temp != null && !weather.temp.equals("null")) tempStr = weather.temp;

                mWeatherTv.setText(watherStr);
                mTempTv.setText(tempStr+"°");

                return;
            }
        }

        //미세먼지
        doDust(zoneName);

        new DustManager().getWeather(MainFragment.this, zoneName, new DustManager.IResult() {
            @Override
            public void result(DustManager.MsnWeatherData weather) {
                Logger.d(TAG, "MsnWeatherData weather:"+ weather.weather+", temp="+weather.temp);
                if (weather != null) {
                    mWeatherTv.setText(weather.weather);
                    mTempTv.setText(weather.temp+"°");

                    Define.getInstance().setWeatherData(weather);
                }

                Define.getInstance().setWeatherRequestedTime(nowCal.get(Calendar.HOUR_OF_DAY));

                startBluetoothListener(isInit);
            }
        });
    }

    /**
     * 블루투스 리스너 실행
     * @param isInit
     */
    private void startBluetoothListener(boolean isInit) {
        // 블루투스 실행
        if (mBluetoothManager == null) {
            mBluetoothManager = new BluetoothManager(MainFragment.this);
        }

        setHealthMessageSqlite();
    }


    /**
     * 건강 메시지 세팅하기
     */
    public void setHealthMessageSqlite() {
        DBHelper helper = new DBHelper(getContext());
        DBHelperMessage db = helper.getMessageDb();
        List<MessageModel> messageList = db.getResultAll(helper);

        boolean isMsgCnt = SharedPref.getInstance().getPreferences(SharedPref.HEALTH_MESSAGE, false);

        if (isMsgCnt == false) {
            mSlideNewIcon.setVisibility(View.INVISIBLE);
        } else if (isMsgCnt == true && messageList.size() > 0) {
            mSlideNewIcon.setVisibility(View.VISIBLE);
        }
        mSlideAdapter.setData(messageList);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBluetoothManager != null)
            mBluetoothManager.onResume();

        Tr_login login = Define.getInstance().getLoginInfo();

        DBHelper helper = new DBHelper(getContext());
        DBHelperWeight WeightDb = helper.getWeightDb();
        DBHelperWeight.WeightStaticData bottomData = WeightDb.getResultStatic();

        if(bottomData.getWeight().isEmpty()){
            tvWeight.setText(login.mber_bdwgh);
        }else{
            tvWeight.setText(login.mber_bdwgh_app);
        }
        tvHeight.setText(login.mber_height);

        tvBasicCalory.setText(getBasicCalori());


        prepareMainItems();
        getDustInfo(true);

        // BMI 체중에대한 표준여부.
        float rHeight = Float.parseFloat(login.mber_height);
        float rWeight =  Float.parseFloat(tvWeight.getText().toString());
        setBmiResult(rWeight, rHeight);

        // 운동의 목표칼로리, 목표걸음수 초기화.
        if(login.goal_mvm_calory.toString().isEmpty()
                || login.goal_mvm_stepcnt.toString().isEmpty()
                || StringUtil.getIntVal(login.goal_mvm_calory) < 150
                || StringUtil.getIntVal(login.goal_mvm_calory) > 1000)
        {
            setStepTarget(0, 0);    //신규로 계산할때는 0,0을 준다
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBluetoothManager != null)
            mBluetoothManager.onPause();
    }

    public void notifyAdapter() {
        if (mMainRecyclerAdapter != null)
            mMainRecyclerAdapter.notifyDataSetChanged();

        setHealthMessageSqlite();
    }


    @Override
    public void onDetach() {
        super.onDetach();

        setInit();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mMainRecyclerAdapter != null)
            mMainRecyclerAdapter.onDetach();
    }
}