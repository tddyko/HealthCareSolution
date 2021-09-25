package com.greencross.greencare.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.greencross.greencare.main.MainCardData;
import com.greencross.greencare.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrsohn on 2017. 3. 20..
 */

public class DBHelper2 extends SQLiteOpenHelper {
    private final String TAG = DBHelper2.class.getSimpleName();
    private static int DB_VERSION = 4;
    private static String DB_NAME = "greencare_db";

    public static String MAIN_ITEM_TABLE = "_item_table";
    public static String MAIN_ITEM_COLUMN_IDX = "_idx";
    public static String MAIN_ITEM_COLUMN_ITEM = "_item";
    public static String MAIN_ITEM_COLUMN_VISIBLE = "_visible";


    /**
     * 운동데이터
     */
    public static String TB_DATA_STEP = "tb_data_step";             // 운동데이터
    private String STEP_IDX = "idx";            //서버에 저장되는 고유번호 int M 로컬과 동일하게사용
    private String STEP_CALORIE = "calorie";        //칼로리 Str 7 M
    private String STEP_DISTANCE = "distance";    //이동거리 Str 7
    private String STEP_STEP = "step";        // 걸음수 Str 7 M
    private String STEP_HEARTRATE = "heartRate";    // 최근 심장박동수 Str 7
    private String STEP_STRESS = "stress";        // 스트레스지수 Str 7
    private String STEP_REGTYPE = "regtype";        // 등록타입 Str 1 M
    private String STEP_REGDATE = "regdate";        //등록일시 Date M

    /**
     * 혈압데이터
     */
    public static String TB_DATA_PRESSURE = "tb_data_pressure";     // 혈압데이터
    private String PRESURE_IDX = "idx";                             // 고유번호intM서버데이터 삭제를 위한 키값
    private String PRESURE_ARTERIALPRESSURE = "arterialPressure";    // 동맥압력Str7M
    private String PRESURE_DIASTOLICPRESSUR = "ediastolicPressure";    // 이완기혈압Str7M
    private String PRESURE_PULSERATE = "pulseRate";            // 맥박속도Str7
    private String PRESURE_SYSTOLICPRESSURE = "systolicPressure";    // 수축기압력Str7
    private String PRESURE_REGTYPE = "regtype";                // 등록타입Str1MD:디바이스,U:직접등록
    private String PRESURE_REGDATE = "regdate";                // 등록일시

    /**
     * 혈당데이터
     */
    public static String TB_DATA_DATASUGAR = "tb_data_datasugar";   // 혈당데이터
    private String SUGAR_IDX = "idx";        // 고유번호intM서버데이터 삭제를 위한 키값
    private String SUGAR_SUGA = "suga";        // r혈당값Str7M
    private String SUGAR_HILOW = "hiLow";        // 임시Str7이후 필요 시 사용
    private String SUGAR_BEFORE = "before";        // 식전/식후BoolM
    private String SUGAR_REGTYPE = "regtype";        // 등록타입Str1MD:디바이스,U:직접등록
    private String SUGAR_REGDATE = "regdate";        // 등록일시
    private String SUGAR_DATE = "Date";        // MyyyyMMddHHmm

    /**
     * 체중데이터
     */
    public static String TB_DATA_WEIGHT = "tb_data_weight";         // 체중데이터
    private String  WEIGHT_IDX = "idx";            // 고유번호intM서버데이터 삭제를 위한 키값
    private String WEIGHT_BMR = "bmr";            // 기초대사율Str7P
    private String WEIGHT_BODYWATE = "bodyWater";            // 체수분StrP7
    private String WEIGHT_BONE = "bone";            // 뼈StPr7
    private String WEIGHT_FAT = "fat";            // 살SPtr7
    private String WEIGHT_HEARTRAT = "heartRate";            // 심박동수PStr7
    private String WEIGHT_MUSCLE = "muscle";            // 근P육Str7
    private String WEIGHT_OBESITY = "obesity	";        // 비P만Str7M
    private String WEIGHT_WEIGHT = "weight";            // P체중Str7M
    private String WEIGHT_REGTYPE = "regtype";            // 등록타입Str1MD:디바이P스,U:직접등록
    private String WEIGHT_REGDATE = "regdate";            // 등록일시
    private String WEIGHT_DATE = "Date";			// MyyyyMMddHHmm

    /**
     * 물 데이터
     */
    public static String TB_DATA_WATER = "tb_data_water";           // 물 데이터
    private String WATER_IDX = "idx";            // 고유번호intM서버데이터 삭제를 위한 키값
    private String WATER_AMOUNT = "amount";            // 마신량Str7M
    private String WATER_REGTYPE = "regtype";            // 등록타입Str1MD:디바이스,U:직접등록
    private String WATER_REGDATE = "regdate";            // 등록일시
    private String WATER_DATE = "Date";			// MyyyyMMddHHmm

    /**
     * 기준치 데이터
     */
    public static String TB_BASIC = "tb_basic";                 // 기준치 데이터 저장 (key값으로 Value값 가져옴)
    private String DEF_KEY = "key";                             // 이메일
    private String DEF_VALUE = "value";                         // 이메일
    private String DEF_MEM_EMAIL = "def_mem_email";             // 이메일
    private String DEF_MEM_NAME = "def_mem_name";               // 이름
    private String DEF_MEM_SEX = "def_mem_sex";                 // M or F성별
    private String DEF_MEM_BIRTH = "def_mem_birth";             // 1980-12-11생일
    private String DEF_MEM_HEIGHT = "def_mem_height";            // 신장(키)
    private String DEF_MEM_WEIGHT = "def_mem_weight";            // 체중
    private String DEF_MEM_WEIGHT_TARGET = "def_mem_weight_target";            // 목표체중
    private String DEF_MEM_HIGH_PRESSURE = "def_mem_high_pressure";            // Y or N고혈압
    private String DEF_MEM_HYPERLIPIDEMI = "def_mem_hyperlipidemia";            // Y or N고지혈증
    private String DEF_MEM_SUGER_TYPE1 = "def_mem_suger_type1";            // Y or N1형 당뇨
    private String DEF_MEM_SUGER_TYPE2 = "def_mem_suger_type2";            // Y or N2형 당뇨
    private String DEF_MEM_EMPTY = "def_mem_empty";                 // Y or N없음(병없음)
    private String DEF_MEM_DRUG = "def_mem_drug";                   // Y or N복약여부
    private String DEF_MEM_SMOKE = "def_mem_smoke";                 // Y or N흡연여부
    private String DEF_TARGET_STEP = "def_target_step";             // 123걸음수 목표(하루 걸음수 목표)
    private String DEF_TARGET_WATER = "def_target_water";			// 11물 목표량

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper2(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        Logger.i(TAG, "DBHelper:onCreate");
        StringBuffer sb = new StringBuffer();
        String sql = "CREATE TABLE "+MAIN_ITEM_TABLE+" ("
                                    +MAIN_ITEM_COLUMN_IDX+" INTEGER, "
                                    +MAIN_ITEM_COLUMN_ITEM+" TEXT, "
                                    +MAIN_ITEM_COLUMN_VISIBLE+" BOOLEAN);";
        sb.append(sql);
        Logger.i(TAG, "onCreate.sql="+sql);
        sql = "CREATE TABLE "+TB_DATA_STEP+" ("
                            +STEP_IDX+" INTEGER , "
                            +STEP_CALORIE+" TEXT NOT NULL, "
                            +STEP_DISTANCE+" TEXT, "
                            +STEP_STEP+" TEXT NOT NULL, "
                            +STEP_HEARTRATE+" TEXT, "
                            +STEP_STRESS+" TEXT, "
                            +STEP_REGTYPE+" TEXT NOT NULL, "
                            +STEP_REGDATE+" DATE NOT NULL);";
        sb.append(sql);
        Logger.i(TAG, "onCreate.sql="+sql);
        sql = "CREATE TABLE "+TB_DATA_PRESSURE+" ("
                            + PRESURE_IDX+" INTEGER , "
                            + PRESURE_ARTERIALPRESSURE+" TEXT NOT NULL,"
                            + PRESURE_DIASTOLICPRESSUR+" TEXT NOT NULL,"
                            + PRESURE_PULSERATE+" TEXT NOT NULL,"
                            + PRESURE_SYSTOLICPRESSURE+" TEXT, "
                            + PRESURE_REGTYPE+" TEXT, "
                            + PRESURE_REGDATE+" TEXT  NOT NULL);";
        sb.append(sql);
        Logger.i(TAG, "onCreate.sql="+sql);
        sql = "CREATE TABLE "+TB_DATA_DATASUGAR+" ("
                                +SUGAR_IDX+" INTEGER , "
                                +SUGAR_SUGA+" TEXT NOT NULL, "
                                +SUGAR_HILOW+" TEXT, "
                                +SUGAR_BEFORE+" BOOLEAN NOT NULL, "
                                +SUGAR_REGTYPE+" TEXT NOT NULL, "
                                +SUGAR_REGDATE+" TEXT NOT NULL, "
                                +SUGAR_DATE+" DATE NOT NULL);";

        sb.append(sql);
        Logger.i(TAG, "onCreate.sql="+sql);
        sql = "CREATE TABLE "+TB_DATA_WEIGHT+" ("
                                +WEIGHT_IDX+" INTEGER , "
                                +WEIGHT_BMR+" TEXT, "
                                +WEIGHT_BODYWATE+" TEXT, "
                                +WEIGHT_BONE+" TEXT, "
                                +WEIGHT_FAT+" TEXT, "
                                +WEIGHT_HEARTRAT+" TEXT, "
                                +WEIGHT_MUSCLE+" TEXT, "
                                +WEIGHT_OBESITY+" TEXT, "
                                +WEIGHT_WEIGHT+" TEXT NOT NULL, "
                                +WEIGHT_REGTYPE+" TEXT NOT NULL, "
                                +WEIGHT_REGDATE+" TEXT NOT NULL, "
                                +WEIGHT_DATE+" DATE NOT NULL);";

        sb.append(sql);
        Logger.i(TAG, "onCreate.sql="+sql);
        sql = "CREATE TABLE "+TB_DATA_WATER+" ("
                            +WATER_IDX+" INTEGER , "
                            +WATER_AMOUNT+" TEXT, "
                            +WATER_REGTYPE+" TEXT, "
                            +WATER_REGDATE+" TEXT, "
                            +WATER_DATE+" DATE NOT NULL);";
        sb.append(sql);
        Logger.i(TAG, "onCreate.sql="+sql);
        sql = "CREATE TABLE "+TB_BASIC+" ("
                                +DEF_KEY+" TEXT, "
                                +DEF_KEY+" TEXT);";
        sb.append(sql);
        db.execSQL(sb.toString());
        Logger.i(TAG, "onCreate.sql="+sql);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            Logger.i(TAG, "DBHelper:onUpgrade.oldVersion="+oldVersion+", newVersion="+newVersion);

            try {
                db.execSQL("DROP TABLE "+MAIN_ITEM_TABLE+";");
            } catch (Exception e) {e.getMessage();}
            try {
                db.execSQL("DROP TABLE "+TB_DATA_STEP+";");
            } catch (Exception e) {e.getMessage();}
            try {
                db.execSQL("DROP TABLE "+TB_DATA_PRESSURE+";");
            } catch (Exception e) {e.getMessage();}
            try {
                db.execSQL("DROP TABLE "+TB_DATA_DATASUGAR+";");
            } catch (Exception e) {e.getMessage();}
            try {
                db.execSQL("DROP TABLE "+TB_DATA_WEIGHT+";");
            } catch (Exception e) {e.getMessage();}
            try {
                db.execSQL("DROP TABLE "+TB_DATA_WATER+";");
            } catch (Exception e) {e.getMessage();}
            try {
                db.execSQL("DROP TABLE "+TB_BASIC+";");
            } catch (Exception e) {e.getMessage();}

            onCreate(db);
        }
    }

    public void insert(int idx, String _item, boolean _visible) {
        // DB에 입력한 값으로 행 추가
        String sql = "INSERT INTO "+MAIN_ITEM_TABLE
                                +" VALUES("+idx+", '"
                                        + _item + "', '"
                                        + _visible + "');";
        Logger.i(TAG, "insert.sql="+sql);
        transactionExcute(sql);
    }

    public void updateIdx(int idx, String item) {
        String sql = "UPDATE "+MAIN_ITEM_TABLE+" SET "
                            +MAIN_ITEM_COLUMN_IDX+"=" + idx
                            + " WHERE "+MAIN_ITEM_COLUMN_ITEM+"='" + item + "';";
        Logger.i(TAG, "updateIdx.sql="+sql);
        transactionExcute(sql);
    }

    public void updateVisible(boolean _visible, String item) {
        String sql = "UPDATE "+MAIN_ITEM_TABLE+" SET "
                            +MAIN_ITEM_COLUMN_VISIBLE+"='" + _visible
                            + "' WHERE "+MAIN_ITEM_COLUMN_ITEM+"='" + item + "';";
        Logger.i(TAG, "updateVisible.sql="+sql);
        transactionExcute(sql);

    }

    public void delete(String _item) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        String sql = "DELETE FROM "+MAIN_ITEM_TABLE+" WHERE "+MAIN_ITEM_COLUMN_ITEM+"='" + _item + "';";
        Logger.i(TAG, "delete.sql="+sql);
        db.execSQL(sql);
        Logger.i(TAG, sql);
        db.close();
    }

    public List<MainCardData> getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        List<MainCardData> mainCardList = new ArrayList<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT * FROM "+MAIN_ITEM_TABLE+" ORDER BY "+MAIN_ITEM_COLUMN_IDX+" asc";
        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        int size = 0;
        while (cursor.moveToNext()) {
            int idx = cursor.getInt(0);
            String name = cursor.getString(1);
            MainCardData.CardE data = MainCardData.CardE.valueOf(name);
            boolean isVisible = cursor.getString(2).toLowerCase().equals("true");

            Logger.i(TAG, "idx["+idx+"], name="+name+", isVisible="+isVisible+"\n");
            if (isVisible) {
                MainCardData cardData = new MainCardData(data);
                cardData.setVisible(isVisible);
                mainCardList.add(cardData);
            }

            size++ ;
        }

        // 최초 사이즈가 없는 경우
        if (size == 0) {
            initData(); // 최초 메인 데이터 저장
            mainCardList.addAll(getResult());
        }

        return mainCardList;
    }


    public void initData() {
        int idx = 0;
        for (MainCardData.CardE cardE : MainCardData.CardE.values()){
            insert(idx++, cardE.name(), true);
        }
    }


    private void transactionExcute(String sql) {
        SQLiteDatabase db = getWritableDatabase();

        try{
            db.beginTransaction();
            db.execSQL(sql);

            db.setTransactionSuccessful();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        db.close();
    }
}