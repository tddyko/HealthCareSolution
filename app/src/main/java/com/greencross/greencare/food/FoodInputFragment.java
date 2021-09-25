package com.greencross.greencare.food;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.DummyActivity;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.component.CDatePicker;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperFoodCalorie;
import com.greencross.greencare.database.DBHelperFoodDetail;
import com.greencross.greencare.database.DBHelperFoodMain;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_get_meal_input_data;
import com.greencross.greencare.network.tr.data.Tr_get_meal_input_food_data;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.network.tr.data.Tr_meal_input_data;
import com.greencross.greencare.network.tr.data.Tr_meal_input_edit_data;
import com.greencross.greencare.network.tr.data.Tr_meal_input_food_data;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;
import com.greencross.greencare.util.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.greencross.greencare.R.id.food_search_button;
import static com.greencross.greencare.food.FoodSearchFragment.BUNDLE_FOOD_DETAIL_INFO;

/**
 * Created by mrsohn on 2017. 3. 14..
 */

public class FoodInputFragment extends BaseFragment {

    private final String TAG = FoodInputFragment.class.getSimpleName();

    public static final String BUNDLE_FOOD_INPUT_DATE       = "food_input_date";  // 식사일기에서 현재 날자
    public static final String BUNDLE_FOOD_MEAL_TYPE        = "bundle_food_meal_type";  // mealtype

    public static final String BUNDLE_MEAL_DATA             = "bundle_meal_data";  // 식사일기에서 조회된 식사 데이터(단일)
    public static final String BUNDLE_FOOD_DATA             = "bundle_food_data";  // 식사일기에서 조회된 음식 데이터(배열)

    private final int REQUEST_IMAGE_CAPTURE                 = 111;      // 카메라 실행
    private final int REQUEST_IMAGE_ALBUM                   = 222;        // 앨범 실행
    private final int REQUEST_IMAGE_CROP                    = 333;         // 이미지 잘라내기
    private final int REQUEST_FOOD_SEARCH                   = 4444;       // 음식 검색

    private Uri contentUri;                                 // 앨범,카메라에서 받아온 경로 위치
    private ImageView mCameraBtn;//, mPictureIv;
    private TextView mDateTv, mTimeTv;
    private EditText mEatTimeEt;
    private String mMealType = "";
    private ImageView mFoodPicture; // 음식사진이미지.

    private FoodSwipeListView mSwipeListView;

    private String mIdx = CDateUtil.getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis()));

    private Tr_get_meal_input_data.ReceiveDatas mMealData               = null;
    private List<Tr_get_meal_input_food_data.ReceiveDatas> mFoodList    = new ArrayList<>();  // 아침

    public static Fragment newInstance() {
        FoodInputFragment fragment = new FoodInputFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_food_input, container, false);
        Bundle bundle = getArguments();
        // 아침:a,점심   b ,저녁 c , 아침간식 d , 점심간식 e , 저녁간식  f
        mMealType = bundle.getString(BUNDLE_FOOD_MEAL_TYPE);
        mMealData = bundle.getParcelable(BUNDLE_MEAL_DATA); // 식사 데이터
        mFoodList = bundle.getParcelableArrayList(BUNDLE_FOOD_DATA);    // 음식 데이터

        Logger.i(TAG, "mMealData=" + mMealData);
        Logger.i(TAG, "mFoodList=" + mFoodList.size());
        if (mMealData != null) {
            mIdx = mMealData.idx;
        }

        return view;
    }

    /**
     * 액션바 세팅
     */
    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        Button saveBtn = actionBar.setActionBarSaveBtn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeUploadData();
            }
        });
        // 식사 데이터가 있으면 수정으로 판단
        if (mMealData == null) {
            saveBtn.setText(getString(R.string.text_regist));
        } else {
            saveBtn.setText(getString(R.string.text_modify));
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCameraBtn      = (ImageView) view.findViewById(R.id.food_input_camera_btn);
//        mPictureIv      = (ImageView) view.findViewById(R.id.food_input_picture_imageview);
        mDateTv         = (TextView) view.findViewById(R.id.food_input_date_textview);
        mTimeTv         = (TextView) view.findViewById(R.id.food_input_time_textview);
        mEatTimeEt      = (EditText) view.findViewById(R.id.food_eat_time_tv);
        mFoodPicture    = (ImageView) view.findViewById(R.id.food_picture);     // 음식사진이미지.

        // 스와이프 리스트뷰 세팅 하기
        mSwipeListView  = new FoodSwipeListView(view, FoodInputFragment.this);

        mCameraBtn.setOnClickListener(mClickListener);
//        mPictureIv.setOnClickListener(mClickListener);
        mDateTv.setOnTouchListener(mTouchListener);
        mTimeTv.setOnTouchListener(mTouchListener);

        Bundle bundle   = getArguments();
        String date     = bundle.getString(BUNDLE_FOOD_INPUT_DATE);
        Calendar cal    = CDateUtil.getCalendar_yyyy_MM_dd_HH_mm(date);

        mDateTv.setText(date);
        mDateTv.setTag(date);

        // 백으로 데이터 보내기
        Bundle backBundle = new Bundle();
        backBundle.putString(BUNDLE_FOOD_INPUT_DATE, date);
        setBackData(backBundle);

        view.findViewById(food_search_button).setOnClickListener(mClickListener);

        if (mMealData != null) { // 식사 데이터
            String amounttime = mMealData.amounttime;
            amounttime = TextUtils.isEmpty(amounttime) ? "" : amounttime;
            mEatTimeEt.setText(amounttime);
            if (TextUtils.isEmpty(mMealData.regdate) == false)
                cal         = CDateUtil.getCalendar_yyyy_MM_dd_HH_mm(mMealData.regdate);
            int year        = cal.get(Calendar.YEAR);
            int month       = cal.get(Calendar.MONTH);
            int hourOfDay   = cal.get(Calendar.HOUR_OF_DAY);
            int minute      = cal.get(Calendar.MINUTE);
            setTimeTv(hourOfDay, minute);

            if (TextUtils.isEmpty(mMealData.picture) == false){
                getImageData(mMealData.picture, mFoodPicture);              // 서버에 이미지가 있다면 서버이미지 우선.
            }else{
                ViewUtil.getIndexToImageData(mMealData.idx, mFoodPicture);   // 이미지 세팅
            }

            getFoodListData(mMealData.idx);
        } else {
            if (getString(R.string.text_breakfast_code).equals(mMealType)) {
                setTimeTv(8, 00);   // 아침
            } else if (getString(R.string.text_lunch_code).equals(mMealType)) {
                setTimeTv(12, 00);  // 점심
            } else if (getString(R.string.text_dinner_code).equals(mMealType)) {
                setTimeTv(18, 00);  // 저녁
            } else if (getString(R.string.text_breakfast_snack_code).equals(mMealType)) {
                setTimeTv(9, 00);   // 아침간식
            } else if (getString(R.string.text_lunch_snack_code).equals(mMealType)) {
                setTimeTv(14, 00);  // 점심간식
            } else if (getString(R.string.text_dinner_snack_code).equals(mMealType)) {
                setTimeTv(20, 00);  // 저녁간식
            }
        }
    }

    /**
     * 데이터 가져오기 (음식)
     */
    private void getFoodListData(String idx) {
        DBHelper helper = new DBHelper(getContext());
        DBHelperFoodDetail db = helper.getFoodDetailDb();

        List<DBHelperFoodCalorie.Data> foodList = db.getFoodList(idx);
        mSwipeListView.setDataList(foodList);
    }

    /**
     * 음식 데이터
     *
     * @return
     */
    private void setFoodListData(Tr_get_meal_input_food_data data) {
        mFoodList.clear();
        for (Tr_get_meal_input_food_data.ReceiveDatas recv : data.data_list) {
            mFoodList.add(recv);
        }

        DBHelper helper = new DBHelper(getContext());
        DBHelperFoodCalorie db = helper.getFoodCalorieDb();
        if (mFoodList.size() > 0) {
            List<DBHelperFoodCalorie.Data> foodList = db.getResult(mFoodList);
            mSwipeListView.setDataList(foodList);
        }
    }

    /**
     * 식사, 음식정보 서버 전송전 체크 및 값 세팅
     */
    private void beforeUploadData() {
        String dayStr = mDateTv.getTag().toString();
        String timeStr = mTimeTv.getTag().toString();
        if (TextUtils.isEmpty(timeStr)) {
            CDialog.showDlg(getContext(), getString(R.string.food_meal_time_input));
            return;
        }

        // 식사이미지만으로도 등록 할 수 있도록 수정.
//        List<DBHelperFoodCalorie.Data> dataList = mSwipeListView.getListData();
//        if (dataList.size() <= 0) {
//            CDialog.showDlg(getContext(), getString(R.string.food_add));
//            return;
//        }

        // 식사시간(선택 사항)
        final String amountTime = StringUtil.getIntString(mEatTimeEt.getText().toString());
        final String regDate = CDateUtil.getRegDateFormat_yyyyMMddHHss(dayStr + timeStr);

        Logger.i(TAG, "amountTime=" + amountTime + ", regDate=" + regDate);
        final Tr_login login = Define.getInstance().getLoginInfo();

        uploadMealData(mIdx, amountTime, regDate, login.mber_sn);

        /*
        if (contentUri != null && new File(contentUri.getPath()).isFile()) {
            doUploadPicture(mIdx, contentUri.getPath(), new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    try {
                        String result = new JSONObject(obj.toString()).getString("data_yn");
                        if ("Y".equals(result)) {
                            uploadMealData(mIdx, amountTime, regDate, login.mber_sn);
                        } else {
                            CDialog.showDlg(getContext(), getString(R.string.text_network_data_send_fail));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            uploadMealData(mIdx, amountTime, regDate, login.mber_sn);
        }*/
    }


    /**
     * 식사일지 등록하기/수정하기
     *
     * @param idx
     * @param amountTime
     * @param regDate
     */
    private void uploadMealData(final String idx, final String amountTime, final String regDate, final String mber_sn) {

        if (mMealData != null) {
            // 수정하기
            final Tr_meal_input_edit_data.RequestData requestData = new Tr_meal_input_edit_data.RequestData();
            requestData.idx         = mMealData.idx;
            requestData.mber_sn     = mber_sn;
            requestData.amounttime  = amountTime;
            requestData.mealtype    = mMealType;
            requestData.calorie     = "" + mSwipeListView.getSumCalorieData();
            requestData.regdate     = regDate;
            requestData.picture     = "";

            getData(getContext(), Tr_meal_input_edit_data.class, requestData, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (obj instanceof Tr_meal_input_edit_data) {
                        Tr_meal_input_edit_data data = (Tr_meal_input_edit_data) obj;
                        if ("Y".equals(data.reg_yn)) {

                            DBHelper helper = new DBHelper(getContext());
                            DBHelperFoodMain db = helper.getFoodMainDb();

                            db.update(requestData, mSwipeListView.getListData(), true);

                            CDialog.showDlg(getContext(), getString(R.string.modify_success), new CDialog.DismissListener() {
                                @Override
                                public void onDissmiss() {
                                    onBackPressed();
                                }
                            });
                        } else {
                            CDialog.showDlg(getContext(), getString(R.string.text_regist_fail));
                        }
                    }
                }
            });
        } else {

            // 등록하기
            final Tr_meal_input_data.RequestData requestData = new Tr_meal_input_data.RequestData();

            requestData.idx         = CDateUtil.getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis()));
            requestData.mber_sn     = mber_sn;
            requestData.idx         = idx;
            requestData.amounttime  = amountTime;
            requestData.mealtype    = mMealType;
            requestData.calorie     = "" + mSwipeListView.getSumCalorieData();
            requestData.regdate     = regDate;
            requestData.picture     = "";
            getData(getContext(), Tr_meal_input_data.class, requestData, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (obj instanceof Tr_meal_input_data) {
                        Tr_meal_input_data data = (Tr_meal_input_data) obj;

                        if ("Y".equals(data.reg_yn)) {

                            DBHelper helper = new DBHelper(getContext());
                            DBHelperFoodMain db = helper.getFoodMainDb();
                            db.insert(requestData, true);

                            uploadFoodEatData(idx, amountTime, regDate, mber_sn);
                        } else {
                            CDialog.showDlg(getContext(), getString(R.string.text_regist_fail));
                        }
                    }
                }
            });
        }
    }

    /**
     * 음식 등록하기
     */
    private void uploadFoodEatData(final String idx, final String amountTime, final String regDate, String mber_sn) {
        final List<DBHelperFoodCalorie.Data> dataList = mSwipeListView.getListData();
//        if (dataList.size() <= 0) {
//            CDialog.showDlg(getContext(), getString(R.string.food_add));
//            return;
//        }

        // 음식데이터가 없다면.
        if (dataList.size() <= 0) {

            uploadPicture();

            // 음식데이터가 있다면.
        }else {

            // 등록하기
            final Tr_meal_input_food_data.RequestData requestData = new Tr_meal_input_food_data.RequestData();

            requestData.idx = idx;
            requestData.mber_sn = mber_sn;
            requestData.food_mass = Tr_meal_input_food_data.getArray(dataList, regDate);
            getData(getContext(), Tr_meal_input_food_data.class, requestData, new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    if (obj instanceof Tr_meal_input_food_data) {
                        Tr_meal_input_food_data data = (Tr_meal_input_food_data) obj;

                        if ("Y".equals(data.reg_yn)) {
                            // 음식 등록하기
                            DBHelper helper = new DBHelper(getContext());
                            DBHelperFoodDetail db = helper.getFoodDetailDb();
                            db.insert(dataList, mIdx, regDate);

                            // 사진업로드.
                            uploadPicture();
                        } else {
                            CDialog.showDlg(getContext(), getString(R.string.text_regist_fail));
                        }
                    }
                }
            });

        }
    }

    // 사진업로드.
    private void uploadPicture(){

        if (contentUri != null && new File(contentUri.getPath()).isFile()) {
            doUploadPicture(mIdx, contentUri.getPath(), new ApiData.IStep() {
                @Override
                public void next(Object obj) {
                    try {
                        String result = new JSONObject(obj.toString()).getString("data_yn");
                        if ("Y".equals(result)) {

                            CDialog.showDlg(getContext(), getString(R.string.text_regist_success), new CDialog.DismissListener() {
                                @Override
                                public void onDissmiss() {
                                    onBackPressed();
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            CDialog.showDlg(getContext(), getString(R.string.text_regist_success), new CDialog.DismissListener() {
                @Override
                public void onDissmiss() {
                    onBackPressed();
                }
            });
        }
    }

    // 음식사진 업로드.
    private void doUploadPicture(String idx, String path, final ApiData.IStep iStep) {
        String param        = idx;          // 업로드 페이지에서 추가할 param 값
        String filePath     = path;         // contentUri.getPath();
        Logger.i(TAG, "doUploadPicture.mIdx=" + idx + ", filePath=" + filePath);
        HttpAsyncFileTask33 rssTask = new HttpAsyncFileTask33(new HttpAsyncTaskInterface() {
            @Override
            public void onPreExecute() {
            }
            @Override
            public void onPostExecute(String data) {
            }
            @Override
            public void onError() {
                CDialog.showDlg(getContext(), getString(R.string.text_network_data_send_fail));
            }
            @Override
            public void onFileUploaded(String result) {
                iStep.next(result);
            }
        });

        rssTask.setParam(param, path);
        rssTask.execute();
    }

    View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int vId = v.getId();
                if (vId == R.id.food_input_date_textview) {
                    showDatePicker(v);
                } else if (vId == R.id.food_input_time_textview) {
                    showTimePicker();
                }
            }
            return false;
        }
    };

    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.greencross.greencare.fileprovider";

    private File createTempFile() {
        String fileName = mIdx + ".png";
        File file       = null;
        try {
            file        = new File(Define.IMAGE_SAVE_PATH);
            if (!file.exists()) {
                file.mkdirs();
                Logger.i(TAG, "createTempFile.mkdirs=" + file.getPath());
            }

            file        = new File(file.getPath(), fileName);
            Logger.i(TAG, "createTempFile.getapth=" + file.getPath());
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();


            if (vId == R.id.food_input_camera_btn) {
                showSelectGalleryCamera();
            } else if (vId == food_search_button) {
                String title = getArguments().getString(CommonActionBar.ACTION_BAR_TITLE);
                Logger.i(TAG, "food_search_button.title=" + title);
                Bundle bundle = new Bundle();
                bundle.putString(CommonActionBar.ACTION_BAR_TITLE, title);
                DummyActivity.startActivityForResult(FoodInputFragment.this, REQUEST_FOOD_SEARCH, FoodSearchFragment.class, bundle);
            }
        }
    };

    private void selectGallery() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.CAMERA};
        reqPermissions(permissions, new IPermission() {
            @Override
            public void result(boolean isGranted) {
                if (isGranted)
                    selectImage();
            }
        });
    }

    private void selectCamera(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.CAMERA};
        reqPermissions(permissions, new IPermission() {
            @Override
            public void result(boolean isGranted) {
                if (isGranted) {
                    String idx = CDateUtil.getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis()));
                    try {
                        contentUri = Uri.fromFile(createTempFile());

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            // Error while creating file
        }
        return file;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void showDatePicker(View v) {
        GregorianCalendar calendar = new GregorianCalendar();

        int year    = calendar.get(Calendar.YEAR);
        int month   = calendar.get(Calendar.MONTH);
        int day     = calendar.get(Calendar.DAY_OF_MONTH);

        String date = mDateTv.getTag().toString();
        if (TextUtils.isEmpty(date) == false) {
            year    = StringUtil.getIntVal(date.substring(0, 4));
            month   = StringUtil.getIntVal(date.substring(4, 6)) - 1;
            day     = StringUtil.getIntVal(date.substring(6, 8));
        }

        new CDatePicker(getContext(), dateSetListener, year, month, day, false).show();
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String msg      = String.format("%d.%d.%d", year, monthOfYear + 1, dayOfMonth);
            String tagMsg   = String.format("%d%02d%02d", year, monthOfYear + 1, dayOfMonth);
            Calendar cal    = Calendar.getInstance();

            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear + 1);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            mDateTv.setText(msg);
            mDateTv.setTag(tagMsg);
        }

    };

    private CDialog mStepDlg;
    private void showSelectGalleryCamera() {
        View.OnClickListener dlgClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int vId = v.getId();

                if (mStepDlg != null) {
                    mStepDlg.dismiss();
                }
                if (R.id.select_gallery_btn == vId) {
                    selectGallery();
                } else if (R.id.select_camera_btn == vId) {
                    selectCamera();
                }
            }
        };

        /**
         * 데이터소스 선택
         */
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_listview_gallery_camera, null);
        view.findViewById(R.id.select_gallery_btn).setOnClickListener(dlgClickListener);
        view.findViewById(R.id.select_camera_btn).setOnClickListener(dlgClickListener);

        mStepDlg = CDialog.showDlg(getContext(), view);
        mStepDlg.setTitle("사진 선택");
    }

    private void showTimePicker() {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        Date now = new Date();
        cal.setTime(now);

        int hour    = cal.get(Calendar.HOUR_OF_DAY);
        int minute  = cal.get(Calendar.MINUTE);
        String time = mTimeTv.getTag().toString();
        if (TextUtils.isEmpty(time) == false) {
            hour = StringUtil.getIntVal(time.substring(0, 2));
            minute = StringUtil.getIntVal(time.substring(2, 4));

            Logger.i(TAG, "hour=" + hour + ", minute=" + minute);
        }

        TimePickerDialog dialog = new TimePickerDialog(getContext(), listener, hour, minute, false);
        dialog.show();
    }

    /**
     * 시간 피커 완료
     */
    private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setTimeTv(hourOfDay, minute);
        }
    };

    private void setTimeTv(int hourOfDay, int minute) {
        String amPm = "오전";
        int hour = hourOfDay;
        if (hourOfDay > 11) {
            amPm = "오후";
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

    public void selectImage() {
        //버튼 클릭시 처리로직
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_ALBUM);
    }

    /**
     * EXIF정보를 회전각도로 변환하는 메서드
     *
     * @param exifOrientation EXIF 회전각
     * @return 실제 각도
     */
    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    /**
     * 이미지를 회전시킵니다.
     *
     * @param bitmap 비트맵 이미지
     * @return 회전된 이미지
     */
    public Bitmap rotate(Bitmap bitmap, String path) {
        Logger.i(TAG, "rotate.path=" + path);
        // 이미지를 상황에 맞게 회전시킨다
        try {
            ExifInterface exif  = new ExifInterface(path);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degrees         = exifOrientationToDegrees(exifOrientation);

            Bitmap retBitmap    = bitmap;

            if (degrees != 0 && bitmap != null) {
                Matrix m = new Matrix();
                m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

                try {
                    Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                    if (bitmap != converted) {
                        retBitmap   = converted;
                        bitmap.recycle();
                        bitmap      = null;
                    }
                } catch (OutOfMemoryError ex) {
                    // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
                }
            }
            return retBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public String saveBitmapToFile(Bitmap bitmap, String path) {

        File tempFile = new File(path);
        try {

            FileOutputStream out = new FileOutputStream(tempFile);
            if (out != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // 넘거 받은 bitmap을 png로 저장해줌
                out.close(); // 마무리로 닫아줍니다.
            }
            Logger.i(TAG, "saveBitmapToFile.path=" + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.i(TAG, "saveBitmapToFile=" + tempFile.getAbsolutePath());
        return tempFile.getAbsolutePath(); // 임시파일 저장경로를 리턴해주면 끝!
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_ALBUM) {
            if (data != null) {
                contentUri = data.getData();
                try {
                    Bitmap bitmap   = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentUri);
                    contentUri      = Uri.fromFile(createTempFile());
                    saveBitmapToFile(bitmap, contentUri.getPath());
                    rotate(bitmap, contentUri.getPath());
                    cropImage(contentUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Logger.i(TAG, "contentUri=" + contentUri.toString());
                Bitmap bitmap   = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentUri);
                bitmap          = rotate(bitmap, contentUri.getPath());
                saveBitmapToFile(bitmap, contentUri.getPath());
                cropImage(contentUri);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CROP) {
            if (data != null) {
                Bundle extras       = data.getExtras();
                if (extras != null) {
                    Bitmap bitmap   = (Bitmap) extras.get("data");
                    Logger.i(TAG, "REQUEST_IMAGE_CROP.contentUri");
                    mFoodPicture.setImageBitmap(bitmap);
                    saveBitmapToFile(bitmap, Define.getFoodPhotoPath(mIdx));
                }
            }
        } else if (requestCode == REQUEST_FOOD_SEARCH && resultCode == Activity.RESULT_OK) {
            DBHelperFoodCalorie.Data info = data.getParcelableExtra(BUNDLE_FOOD_DETAIL_INFO);
            Logger.i(TAG, "REQUEST_FOOD_SEARCH=" + info.food_name);
            mSwipeListView.setData(info);
        }
    }

    private void cropImage(Uri contentUri) {
        if (contentUri != null) {
            File file = new File(contentUri.toString());
            Logger.i(TAG, "cropImage.exists=" + file.exists());
            Logger.i(TAG, "cropImage.isFile=" + file.isFile());
            Logger.i(TAG, "cropImage.length=" + file.length());
        }
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        // indicate image type and Uri of image
        cropIntent.setDataAndType(contentUri, "image/*");
        // set crop properties
        cropIntent.putExtra("crop", "true");
        // indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        // indicate output X and Y
        cropIntent.putExtra("outputX", 250);
        cropIntent.putExtra("outputY", 250);
        // retrieve data on return
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }
}

