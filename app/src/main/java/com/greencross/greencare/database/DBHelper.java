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

public class DBHelper extends SQLiteOpenHelper {
    private final String TAG                    = DBHelper.class.getSimpleName();

    // + ------------------------------------------------------------
    // 15 : 7/12 - 음식테이블레 unit 추가. (14->15업그래이드시 오류발생)
    // 16 : 7/13 - 음식테이블 DB_VERSION이 다르면 테이블 Drop하고 새로 만들도록 수정.
    // + ------------------------------------------------------------
    private static int DB_VERSION               = 16;
    public boolean isNewFood                    = false;        //새로운 음식디비 있음.
    public static String DB_NAME                = "greencare_db";

    public String MAIN_ITEM_TABLE               = "_item_table";
    public String MAIN_ITEM_COLUMN_IDX          = "_idx";
    public String MAIN_ITEM_COLUMN_ITEM         = "_item";
    public String MAIN_ITEM_COLUMN_VISIBLE      = "_visible";

    private DBHelperSugar mSugarDb              = new DBHelperSugar(DBHelper.this);
    private DBHelperPresure mPresureDb          = new DBHelperPresure(DBHelper.this);
    private DBHelperStep mStepDb                = new DBHelperStep(DBHelper.this);
    private DBHelperStepRealtime mStepRtimeDb   = new DBHelperStepRealtime(DBHelper.this);
    private DBHelperWater mWaterDb              = new DBHelperWater(DBHelper.this);
    private DBHelperWeight mWeightDb            = new DBHelperWeight(DBHelper.this);
    private DBHelperBasic mBasicDb              = new DBHelperBasic(DBHelper.this);
    private DBHelperMessage mMessageDb          = new DBHelperMessage(DBHelper.this);
    private DBHelperPPG mPpgDb                  = new DBHelperPPG(DBHelper.this);

    private DBHelperFoodMain mFoodMainDb        = new DBHelperFoodMain(DBHelper.this);
    private DBHelperFoodDetail mFoodDetailDb    = new DBHelperFoodDetail(DBHelper.this);
    private DBHelperFoodCalorie mFoodCalorieDb  = new DBHelperFoodCalorie(DBHelper.this);

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        Logger.i(TAG, "DBHelper:onCreate");
        String sql = "CREATE TABLE if not exists "  + MAIN_ITEM_TABLE + " ("
                                                    + MAIN_ITEM_COLUMN_IDX + " INTEGER, "
                                                    + MAIN_ITEM_COLUMN_ITEM + " TEXT, "
                                                    + MAIN_ITEM_COLUMN_VISIBLE + " BOOLEAN);";
        db.execSQL(sql);
        Logger.i(TAG, "onCreate.sql=" + sql);
        db.execSQL(mSugarDb.createDb());
        db.execSQL(mPresureDb.createDb());
        db.execSQL(mStepDb.createDb());
        db.execSQL(mStepRtimeDb.createDb());
        db.execSQL(mWaterDb.createDb());
        db.execSQL(mWeightDb.createDb());
        db.execSQL(mBasicDb.createDb());
        db.execSQL(mMessageDb.createDb());
        db.execSQL(mPpgDb.createDb());

        db.execSQL(mFoodMainDb.createDb());
        db.execSQL(mFoodDetailDb.createDb());
        db.execSQL(mFoodCalorieDb.createDb());
    }


    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Logger.i(TAG, "DBHelper:onUpgrade");

        if (oldVersion != newVersion) {
            Logger.i(TAG, "DBHelper:onUpgrade.oldVersion=" + oldVersion + ", newVersion=" + newVersion);

            isNewFood = true;
            // 지우고 테이블 다시 만듦.
            db.execSQL(mFoodCalorieDb.dropTable());

            onCreate(db);
        }
    }

    public void insert(int idx, String _item, boolean _visible) {
        // DB에 입력한 값으로 행 추가
        String sql = "INSERT INTO " + MAIN_ITEM_TABLE
                                    + " VALUES(" + idx + ", '"
                                    + _item + "', '"
                                    + _visible + "');";
        Logger.i(TAG, "insert.sql=" + sql);
        transactionExcute(sql);
    }

    public void updateIdx(int idx, String item) {
        String sql = "UPDATE "  + MAIN_ITEM_TABLE + " SET "
                                + MAIN_ITEM_COLUMN_IDX + "=" + idx
                                + " WHERE " + MAIN_ITEM_COLUMN_ITEM + "='" + item + "';";
        Logger.i(TAG, "updateIdx.sql=" + sql);
        transactionExcute(sql);
    }

    public void updateVisible(boolean _visible, String item) {
        String sql = "UPDATE "  + MAIN_ITEM_TABLE + " SET "
                                + MAIN_ITEM_COLUMN_VISIBLE + "='" + _visible
                                + "' WHERE " + MAIN_ITEM_COLUMN_ITEM + "='" + item + "';";
        Logger.i(TAG, "updateVisible.sql=" + sql);
        transactionExcute(sql);

    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();

        String sql = "DELETE FROM " + DBHelperSugar.TB_DATA_SUGAR;
        Logger.i(TAG, "delete.sql=" + sql);
        db.execSQL(sql);
        Logger.i(TAG, sql);

        String sql2 = "DELETE FROM " + DBHelperWeight.TB_DATA_WEIGHT;
        Logger.i(TAG, "delete.sql=" + sql2);
        db.execSQL(sql2);
        Logger.i(TAG, sql2);

        String sql3 = "DELETE FROM " + DBHelperFoodDetail.TB_DATA_FOOD_DETAIL;
        Logger.i(TAG, "delete.sql=" + sql3);
        db.execSQL(sql3);
        Logger.i(TAG, sql3);

        String sql4 = "DELETE FROM " + DBHelperFoodMain.TB_DATA_FOOD_MAIN;
        Logger.i(TAG, "delete.sql=" + sql4);
        db.execSQL(sql4);
        Logger.i(TAG, sql4);

        String sql5 = "DELETE FROM " + DBHelperMessage.TB_DATA_MESSAGE;
        Logger.i(TAG, "delete.sql=" + sql5);
        db.execSQL(sql5);
        Logger.i(TAG, sql5);

        String sql6 = "DELETE FROM " + DBHelperPresure.TB_DATA_PRESSURE;
        Logger.i(TAG, "delete.sql=" + sql6);
        db.execSQL(sql6);
        Logger.i(TAG, sql6);

        String sql7 = "DELETE FROM " + DBHelperStep.TB_DATA_STEP;
        Logger.i(TAG, "delete.sql=" + sql7);
        db.execSQL(sql7);
        Logger.i(TAG, sql7);

        String sql8 = "DELETE FROM " + DBHelperWater.TB_DATA_WATER;
        Logger.i(TAG, "delete.sql=" + sql8);
        db.execSQL(sql8);
        Logger.i(TAG, sql8);

        String sql9 = "DELETE FROM " + DBHelperPPG.TB_DATA_PPG;
        Logger.i(TAG, "delete.sql=" + sql9);
        db.execSQL(sql9);
        Logger.i(TAG, sql9);

        String sql10 = "DELETE FROM " + DBHelperStepRealtime.TB_DATA_STEP_REALTIME;
        Logger.i(TAG, "delete.sql=" + sql10);
        db.execSQL(sql10);
        Logger.i(TAG, sql10);

        db.close();
    }


    public void delete(String _item) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        String sql = "DELETE FROM " + MAIN_ITEM_TABLE + " WHERE " + MAIN_ITEM_COLUMN_ITEM + "='" + _item + "';";
        Logger.i(TAG, "delete.sql=" + sql);
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
        String sql = "SELECT * FROM " + MAIN_ITEM_TABLE + " ORDER BY " + MAIN_ITEM_COLUMN_IDX + " asc";
        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        int size = 0;
        while (cursor.moveToNext()) {
            int idx = cursor.getInt(0);
            String name = cursor.getString(1);
            MainCardData.CardE data = MainCardData.CardE.valueOf(name);
            boolean isVisible = cursor.getString(2).toLowerCase().equals("true");

            Logger.i(TAG, "idx[" + idx + "], name=" + name + ", isVisible=" + isVisible + "\n");
            if (isVisible) {
                MainCardData cardData = new MainCardData(data);
                cardData.setVisible(isVisible);
                mainCardList.add(cardData);
            }

            size++;
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
        for (MainCardData.CardE cardE : MainCardData.CardE.values()) {

            if (cardE.getCardName().equals("물") || cardE.getCardName().equals("체지방률")) {
                insert(idx++, cardE.name(), false);
            } else {
                insert(idx++, cardE.name(), true);
            }

        }
    }


    public void transactionExcute(String sql) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.execSQL(sql);

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        db.close();
    }


    public DBHelperSugar getSugarDb() {
        return mSugarDb;
    }

    public DBHelperPresure getPresureDb() {
        return mPresureDb;
    }

    public DBHelperStep getStepDb() {
        return mStepDb;
    }

    public DBHelperStepRealtime getmStepRtimeDb() {
        return mStepRtimeDb;
    }

    public DBHelperPPG getPPGDb() {
        return mPpgDb;
    }

    public DBHelperWater getWaterDb() {
        return mWaterDb;
    }

    public DBHelperWeight getWeightDb() {
        return mWeightDb;
    }

    public DBHelperBasic getBasicDb() {
        return mBasicDb;
    }

    public DBHelperMessage getMessageDb() {
        return mMessageDb;
    }

    public DBHelperFoodMain getFoodMainDb() {
        return mFoodMainDb;
    }

    public DBHelperFoodDetail getFoodDetailDb() {
        return mFoodDetailDb;
    }

    public DBHelperFoodCalorie getFoodCalorieDb() {
        return mFoodCalorieDb;
    }

}