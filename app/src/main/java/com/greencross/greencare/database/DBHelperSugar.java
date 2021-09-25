package com.greencross.greencare.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;


import com.greencross.greencare.bluetooth.model.BloodModel;
import com.greencross.greencare.charting.data.PressureEntry;
import com.greencross.greencare.charting.data.SticEntry;
import com.greencross.greencare.network.tr.data.Tr_get_hedctdata;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mrsohn on 2017. 3. 20..
 */

public class DBHelperSugar {
    private final String TAG = DBHelperSugar.class.getSimpleName();
    private DBHelper mHelper;

    public DBHelperSugar(DBHelper helper) {
        mHelper = helper;
    }

    /**
     * 혈당데이터
     */
    public static String TB_DATA_SUGAR = "tb_data_sugar";   // 혈당데이터
    private String SUGAR_IDX = "idx";        // 고유번호intM서버데이터 삭제를 위한 키값
    private String SUGAR_SUGAR = "sugar";        // r혈당값Str7M
    private String SUGAR_HILOW = "hiLow";        // 임시Str7이후 필요 시 사용
    private String SUGAR_BEFORE = "before";        // 식전/식후BoolM
    private String SUGAR_DRUGNAME = "drugname";        // 약이름
    private String SUGAR_REGTYPE = "regtype";        // 등록타입Str1M D:디바이스,U:직접등록, R:투약
    private String SUGAR_REGDATE = "regdate";        // 등록일시
    private String IS_SERVER_REGIST = "is_server_regist";                // 서버 등록 여부


    // DB를 새로 생성할 때 호출되는 함수
    public String createDb() {
        // 새로운 테이블 생성
        String sql = " CREATE TABLE if not exists " + TB_DATA_SUGAR + " ("
                + SUGAR_IDX + " CHARACTER(14) PRIMARY KEY, "
                + SUGAR_SUGAR + " VARCHAR(15) NULL, "
                + SUGAR_HILOW + " CHARACTER(1) NULL, "
                + SUGAR_BEFORE + " VARCHAR(1) NULL, "
                + SUGAR_DRUGNAME + " VARCHAR(30) NULL, "
                + SUGAR_REGTYPE + " CHARACTER(1) NULL, "
                + IS_SERVER_REGIST + " BOOLEAN,"
                + SUGAR_REGDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP); ";
        Logger.i(TAG, "onCreate.sql=" + sql);
        return sql;
    }

    // 히스토리에서 선택된 DB로우를 삭제하는 함수
    public void deleteDb(String idx) {
        String sql = "DELETE FROM " + TB_DATA_SUGAR + " WHERE idx == '" + idx + "' ";

        Logger.i(TAG, "onDelete.sql = " + sql);
        mHelper.transactionExcute(sql);
    }

    // 특정 로우 업데이트 하는 함수
    public void updateDbSugar(String idx, String sugar, String medicen, String before, String reg_de) {
        String sql = "UPDATE " + TB_DATA_SUGAR +
                " SET "
                + SUGAR_SUGAR + "='" + sugar + "', "
                + SUGAR_DRUGNAME + "='" + medicen + "',"
                + SUGAR_BEFORE + "='" + before + "',"
                + SUGAR_REGDATE + "='" + CDateUtil.getRegDateFormat_yyyyMMddHHss(reg_de) + "'"
                + " WHERE idx = '" + idx + "'";

        Logger.i(TAG, "onUpdate.sql = " + sql);
        mHelper.transactionExcute(sql);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    public String upgradeDb() {
        return "DROP TABLE " + TB_DATA_SUGAR + ";";
    }

    /**
     * 블루투스 디바이스에서 데이터를 받았을 때
     *
     * @param dataModel
     * @param isServerReg
     */
    public void insert(SparseArray<BloodModel> dataModel, boolean isServerReg) {
        // DB에 입력한 값으로 행 추가
        StringBuffer sb = new StringBuffer();
        if (dataModel.size() > 0) {
            BloodModel data = dataModel.get(dataModel.keyAt(dataModel.size() - 1));
            String sql = "INSERT INTO " + TB_DATA_SUGAR
                    + " VALUES('" + data.getIdx() + "', '"
                    + data.getSugar() + "', '"
                    + " " + "', '"
                    + data.getBefore() + "', '"
                    + data.getMedicenName() + "', '"
                    + data.getRegtype() + "', '"
                    + isServerReg + "', '"
                    + CDateUtil.getRegDateFormat_yyyyMMddHHss(data.getRegTime()) + "');";
            sb.append(sql);
        }


        Logger.i(TAG, "insert.sql=" + sb.toString());
        mHelper.transactionExcute(sb.toString());

        getResult();
    }

    /**
     * 직접입력시
     *
     * @param datas
     * @param isServerReg
     */
    public void insert(Tr_get_hedctdata datas, boolean isServerReg) {

        String sql = "INSERT INTO " + TB_DATA_SUGAR
                + " VALUES";

        for (Tr_get_hedctdata.DataList data : datas.data_list) {
            StringBuffer sb = new StringBuffer();
            sb.append(sql);

            String values = "('" + data.idx + "', '"
                    + data.sugar + "', '"
                    + data.hilow + "', '"
                    + data.before + "', '"
                    + data.drugname + "', '"
                    + data.regtype + "', '"
                    + isServerReg + "', '"
                    + CDateUtil.getRegDateFormat_yyyyMMddHHss(data.reg_de) + "')";

            sb.append(values);
            Logger.i(TAG, "insert.sql=" + sb.toString());
            mHelper.transactionExcute(sb.toString());
        }

    }

    public void updateIdx(int idx, String item) {
    }

    public void updateVisible(boolean _visible, String item) {
    }

    public void delete(String _item) {
    }


    public List<Tr_get_hedctdata.DataList> getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String result = "";
        List<Tr_get_hedctdata.DataList> data_list = new ArrayList<>();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT * FROM " + TB_DATA_SUGAR
                + " ORDER BY datetime("+ SUGAR_REGDATE +") desc, cast(" + SUGAR_IDX + " as BIGINT) DESC";
        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        int i = 0;
        while (cursor.moveToNext()) {
            String sugarIdx = cursor.getString(cursor.getColumnIndex(SUGAR_IDX));
            String sugar = cursor.getString(cursor.getColumnIndex(SUGAR_SUGAR));
            String hiLow = cursor.getString(cursor.getColumnIndex(SUGAR_HILOW));
            String before = cursor.getString(cursor.getColumnIndex(SUGAR_BEFORE));
            String drugName = cursor.getString(cursor.getColumnIndex(SUGAR_DRUGNAME));
            String regType = cursor.getString(cursor.getColumnIndex(SUGAR_REGTYPE));
            String regDate = cursor.getString(cursor.getColumnIndex(SUGAR_REGDATE));

            Tr_get_hedctdata.DataList data = new Tr_get_hedctdata.DataList();
            data.idx = sugarIdx;
            data.sugar = sugar;
            data.hilow = hiLow;
            data.before = before;
            data.drugname = drugName;
            data.regtype = regType;
            data.reg_de = regDate;

            Logger.i(TAG, "result i=[" + (i++) + "] sugarIdx[" + sugarIdx + "], sugar=" + sugar + ", drugName=" + drugName + ", regType=" + regType + ", regDate=" + regDate + ", before=" + before);
            data_list.add(data);
        }
        cursor.close();

        return data_list;
    }

    /**
     * 가장 최근 저장된 값
     *
     * @param helper
     * @return 메인에서 혈당값을 사용하기 위한 함수
     * 메인에서는 혈당 하나의 값만 사용
     * 혈당은 최종값만을 사용한다.
     */
    public String getResultMain(SQLiteOpenHelper helper) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = helper.getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT " + SUGAR_IDX + ", " + SUGAR_SUGAR + ", " + SUGAR_REGDATE +
                " FROM " + TB_DATA_SUGAR +
                " WHERE " + SUGAR_DRUGNAME + " == ''" +
                " Order by datetime("+ SUGAR_REGDATE +") desc, cast(" + SUGAR_IDX + " as BIGINT) DESC LIMIT 1;";
        Logger.i(TAG, "sql=" + sql);

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor == null) {
            Logger.e(TAG, "getResultMain cursor == null");
            return "";
        }

        if (!cursor.moveToFirst()) {
            cursor.close();
            Logger.e(TAG, "getResultMain !cursor.moveToFirst()");
            return "";
        }
        Logger.i(TAG, "getResultRecent cursor.moveToNext():" + cursor.getCount());
        if (cursor.getCount() > 0) {
            try {
                String sugarIdx = cursor.getString(cursor.getColumnIndex(SUGAR_IDX));
                String regdate = cursor.getString(cursor.getColumnIndex(SUGAR_REGDATE));
                String sugar = cursor.getString(cursor.getColumnIndex(SUGAR_SUGAR));
                Logger.i(TAG, "result SUGAR_REGDATE[" + regdate + "], sugar=" + sugar);

                result = sugar;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }

        return result;
    }

    /*
      // 투약시간
    */
    public List<PressureEntry> getResultMedicine(SQLiteOpenHelper helper, String nDate) {

        SQLiteDatabase db = helper.getReadableDatabase();
        List<PressureEntry> yVals1 = new ArrayList<>();

        String sql = "Select "
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 1 THEN 1  End),0) as H1,"     //1시
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 2 THEN 1  End),0) as H2,"     //2시
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 3 THEN 1  End),0) as H3,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 4 THEN 1  End),0) as H4,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 5 THEN 1  End),0) as H5,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 6 THEN 1  End),0) as H6,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 7 THEN 1  End),0) as H7,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 8 THEN 1  End),0) as H8,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 9 THEN 1  End),0) as H9,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 10 THEN 1 End),0) as H10,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 11 THEN 1 End),0) as H11,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 12 THEN 1 End),0) as H12,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 13 THEN 1 End),0) as H13,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 14 THEN 1 End),0) as H14,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 15 THEN 1 End),0) as H15,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 16 THEN 1 End),0) as H16,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 17 THEN 1 End),0) as H17,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 18 THEN 1 End),0) as H18,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 19 THEN 1 End),0) as H19,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 20 THEN 1 End),0) as H20,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 21 THEN 1 End),0) as H21,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 22 THEN 1 End),0) as H22,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 23 THEN 1 End),0) as H23,"
                + " ifnull(COUNT(CASE cast(strftime('%H', " + SUGAR_REGDATE + ") as integer) WHEN 24 THEN 1 End),0) as H24 "
                + " FROM " + TB_DATA_SUGAR
                + " WHERE " + SUGAR_REGTYPE + " in ('R') and " + SUGAR_REGDATE + " BETWEEN '" + nDate + " 00:00' and '" + nDate + " 23:59' "
                + " Group by strftime('%d'," + SUGAR_REGDATE + ") ";


        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count=" + cursor.getCount());
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                try {
                    int h1 = cursor.getInt(cursor.getColumnIndex("H1"));    // MIN
                    int h2 = cursor.getInt(cursor.getColumnIndex("H2"));
                    int h3 = cursor.getInt(cursor.getColumnIndex("H3"));
                    int h4 = cursor.getInt(cursor.getColumnIndex("H4"));
                    int h5 = cursor.getInt(cursor.getColumnIndex("H5"));
                    int h6 = cursor.getInt(cursor.getColumnIndex("H6"));
                    int h7 = cursor.getInt(cursor.getColumnIndex("H7"));
                    int h8 = cursor.getInt(cursor.getColumnIndex("H8"));
                    int h9 = cursor.getInt(cursor.getColumnIndex("H9"));
                    int h10 = cursor.getInt(cursor.getColumnIndex("H10"));
                    int h11 = cursor.getInt(cursor.getColumnIndex("H11"));
                    int h12 = cursor.getInt(cursor.getColumnIndex("H12"));
                    int h13 = cursor.getInt(cursor.getColumnIndex("H13"));
                    int h14 = cursor.getInt(cursor.getColumnIndex("H14"));
                    int h15 = cursor.getInt(cursor.getColumnIndex("H15"));
                    int h16 = cursor.getInt(cursor.getColumnIndex("H16"));
                    int h17 = cursor.getInt(cursor.getColumnIndex("H17"));
                    int h18 = cursor.getInt(cursor.getColumnIndex("H18"));
                    int h19 = cursor.getInt(cursor.getColumnIndex("H19"));
                    int h20 = cursor.getInt(cursor.getColumnIndex("H20"));
                    int h21 = cursor.getInt(cursor.getColumnIndex("H21"));
                    int h22 = cursor.getInt(cursor.getColumnIndex("H22"));
                    int h23 = cursor.getInt(cursor.getColumnIndex("H23"));
                    int h24 = cursor.getInt(cursor.getColumnIndex("H24"));

                    Logger.i(TAG, "혈압메인 (일단위) h1:" + h1 + ", h2:" + h2 + ", h3:" + h3 + ", h4:" + h4 + ", h5:" + h5 + ", h6:" + h6 + ", h7:" + h7);
                    Logger.i(TAG, "h8:" + h8 + ", h9:" + h9 + ", h10:" + h10 + ", h11:" + h11 + ", h12:" + h12 + ", h13:" + h13 + ", h14:" + h14 + ", h15:" + h15);
                    Logger.i(TAG, "h16:" + h16 + ", h17:" + h17 + ", h18:" + h18 + ", h19:" + h19 + ", h20:" + h20 + ", h21:" + h21 + ", h22:" + h22 + ", h23:" + h23 + ", h24:" + h24);

                    Logger.i(TAG, "cursor.getColumnCount()=" + cursor.getColumnCount());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        } else {
            cursor.close();
        }
        return yVals1;
    }


    /*
    // 일단위
     */
    public List<SticEntry> getResultDay(String nDate, int beforeAndAfterType) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<SticEntry> yVals1 = new ArrayList<>();

        String sql = "Select "
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=0 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H0B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=0 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H0A, "
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=1 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H1B,"  //1시(식전) **데이터확인주의
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=1 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H1A,"  //1시(식후) **데이터확인주의
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=2 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H2B,"                        //2시(식전) **데이터확인주의
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=2 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H2A,"  //2시(식후) **데이터확인주의
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=3 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H3B,"                        //2시(식전) **데이터확인주의
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=3 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H3A,"  //3시(식후) **데이터확인주의
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=4 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H4B,"                        //3시(식전) **데이터확인주의
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=4 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H4A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=5 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H5B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=5 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H5A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=6 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H6B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=6 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H6A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=7 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H7B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=7 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H7A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=8 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H8B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=8 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H8A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=9 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H9B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=9 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H9A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=10 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H10B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=10 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H10A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=11 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H11B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=11 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H11A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=12 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H12B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=12 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H12A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=13 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H13B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=13 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H13A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=14 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H14B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=14 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H14A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=15 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H15B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=15 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H15A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=16 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H16B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=16 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H16A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=17 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H17B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=17 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H17A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=18 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H18B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=18 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H18A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=19 and (" + SUGAR_BEFORE + "!='2') THEN " + SUGAR_SUGAR + " End) as H19B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=19 and (" + SUGAR_BEFORE + "='2' ) THEN " + SUGAR_SUGAR + " End) as H19A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=20 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H20B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=20 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H20A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=21 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H21B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=21 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H21A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=22 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H22B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=22 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H22A,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=23 and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End) as H23B,"
                + " AVG(CASE WHEN cast(strftime('%H', " + SUGAR_REGDATE + ") as integer)=23 and (" + SUGAR_BEFORE + "= '2') THEN " + SUGAR_SUGAR + " End) as H23A "
                + " FROM " + TB_DATA_SUGAR
                + " WHERE " + SUGAR_REGTYPE + " in ('D', 'U') and " + SUGAR_REGDATE + " BETWEEN '" + nDate + " 00:00' and '" + nDate + " 23:59' and " + SUGAR_DRUGNAME + " = '' and cast(" + SUGAR_SUGAR + " as integer) > 0"
                + " Group by cast(strftime('%d'," + SUGAR_REGDATE + ") as integer) ";


        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "count =" + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                int h0b = cursor.getInt(cursor.getColumnIndex("H0B"));
                int h0a = cursor.getInt(cursor.getColumnIndex("H0A"));
                int h1b = cursor.getInt(cursor.getColumnIndex("H1B"));
                int h1a = cursor.getInt(cursor.getColumnIndex("H1A"));
                int h2b = cursor.getInt(cursor.getColumnIndex("H2B"));
                int h2a = cursor.getInt(cursor.getColumnIndex("H2A"));
                int h3b = cursor.getInt(cursor.getColumnIndex("H3B"));
                int h3a = cursor.getInt(cursor.getColumnIndex("H3A"));
                int h4b = cursor.getInt(cursor.getColumnIndex("H4B"));
                int h4a = cursor.getInt(cursor.getColumnIndex("H4A"));
                int h5b = cursor.getInt(cursor.getColumnIndex("H5B"));
                int h5a = cursor.getInt(cursor.getColumnIndex("H5A"));
                int h6b = cursor.getInt(cursor.getColumnIndex("H6B"));
                int h6a = cursor.getInt(cursor.getColumnIndex("H6A"));
                int h7b = cursor.getInt(cursor.getColumnIndex("H7B"));
                int h7a = cursor.getInt(cursor.getColumnIndex("H7A"));
                int h8b = cursor.getInt(cursor.getColumnIndex("H8B"));
                int h8a = cursor.getInt(cursor.getColumnIndex("H8A"));
                int h9b = cursor.getInt(cursor.getColumnIndex("H9B"));
                int h9a = cursor.getInt(cursor.getColumnIndex("H9A"));
                int h10b = cursor.getInt(cursor.getColumnIndex("H10B"));
                int h10a = cursor.getInt(cursor.getColumnIndex("H10A"));
                int h11b = cursor.getInt(cursor.getColumnIndex("H11B"));
                int h11a = cursor.getInt(cursor.getColumnIndex("H11A"));
                int h12b = cursor.getInt(cursor.getColumnIndex("H12B"));
                int h12a = cursor.getInt(cursor.getColumnIndex("H12A"));
                int h13b = cursor.getInt(cursor.getColumnIndex("H13B"));
                int h13a = cursor.getInt(cursor.getColumnIndex("H13A"));
                int h14b = cursor.getInt(cursor.getColumnIndex("H14B"));
                int h14a = cursor.getInt(cursor.getColumnIndex("H14A"));
                int h15b = cursor.getInt(cursor.getColumnIndex("H15B"));
                int h15a = cursor.getInt(cursor.getColumnIndex("H15A"));
                int h16b = cursor.getInt(cursor.getColumnIndex("H16B"));
                int h16a = cursor.getInt(cursor.getColumnIndex("H16A"));
                int h17b = cursor.getInt(cursor.getColumnIndex("H17B"));
                int h17a = cursor.getInt(cursor.getColumnIndex("H17A"));
                int h18b = cursor.getInt(cursor.getColumnIndex("H18B"));
                int h18a = cursor.getInt(cursor.getColumnIndex("H18A"));
                int h19b = cursor.getInt(cursor.getColumnIndex("H19B"));
                int h19a = cursor.getInt(cursor.getColumnIndex("H19A"));
                int h20b = cursor.getInt(cursor.getColumnIndex("H20B"));
                int h20a = cursor.getInt(cursor.getColumnIndex("H20A"));
                int h21b = cursor.getInt(cursor.getColumnIndex("H21B"));
                int h21a = cursor.getInt(cursor.getColumnIndex("H21A"));
                int h22b = cursor.getInt(cursor.getColumnIndex("H22B"));
                int h22a = cursor.getInt(cursor.getColumnIndex("H22A"));
                int h23b = cursor.getInt(cursor.getColumnIndex("H23B"));
                int h23a = cursor.getInt(cursor.getColumnIndex("H23A"));

                Logger.i(TAG, "혈당 (일단위) h0a:"+ h0a +", h1a:" + h1a + ", h2a:" + h2a + ", h3a:" + h3a + ", h4a:" + h4a + ", h5a:" + h5a + ", h6:" + h6a + ", h7a:" + h7a);
                Logger.i(TAG, "h8a:" + h8a + ", h9a:" + h9a + ", h10a:" + h10a + ", h11:" + h11a + ", h12:" + h12a + ", h13:" + h13a + ", h14:" + h14a + ", h15:" + h15a);
                Logger.i(TAG, "h16a:" + h16a + ", h17a:" + h17a + ", h18a:" + h18a + ", h19a:" + h19a + ", h20a:" + h20a + ", h21a:" + h21a + ", h22a:" + h22a + ", h23a:" + h23a );
                Logger.i(TAG, "----");
                Logger.i(TAG, "h0b:"+ h0b + ", h1b:" + h1b + ", h2b:" + h2b + ", h3b:" + h3b + ", h4b:" + h4b + ", h5b:" + h5b + ", h6:" + h6b + ", h7b:" + h7b);
                Logger.i(TAG, "h8b:" + h8b + ", h9b:" + h9b + ", h10b:" + h10b + ", h11:" + h11b + ", h12:" + h12b + ", h13:" + h13b + ", h14:" + h14b + ", h15:" + h15b);
                Logger.i(TAG, "h16b:" + h16b + ", h17b:" + h17b + ", h18b:" + h18b + ", h19b:" + h19b + ", h20b:" + h20b + ", h21b:" + h21b + ", h22b:" + h22b + ", h23b:" + h23b );


                for (int i = 0; i <= (cursor.getColumnCount() / 2)-1; i++) {
                    float before = cursor.getInt(cursor.getColumnIndex("H" + i + "B"));
                    float after = cursor.getInt(cursor.getColumnIndex("H" + i + "A"));

                    before = replaceVal(before, beforeAndAfterType);
                    after = replaceVal(after, beforeAndAfterType);
                    Logger.i(TAG, "result[" + i + "].before=" + before + ", after=" + after);

                    yVals1.add(new SticEntry(i - 1
                            , after
                            , before
                            , beforeAndAfterType
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            for (int i = 0; i <= (cursor.getColumnCount() / 2)-1; i++) {
                yVals1.add(new SticEntry(i - 1
                        , 0
                        , 0
                        , beforeAndAfterType
                ));
            }
            cursor.close();
        }
        return yVals1;
    }

    /*
    // 주단위
     */
    public List<SticEntry> getResultWeek(String sDate, String eDate, int beforeAndAfterType) {
        List<SticEntry> yVals1 = new ArrayList<SticEntry>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        StringBuffer sb = new StringBuffer();

        String sql = "Select ";
        sb.append(sql);
        Logger.i(TAG, sql);
        int cnt = 6;
        for (int i = 0; i <= cnt; i++) {
            sql = " ifnull(MIN(CASE  WHEN cast(strftime('%w', " + SUGAR_REGDATE + ") as integer)=" + i + " and (" + SUGAR_BEFORE + " != 2 ) THEN " + SUGAR_SUGAR + " End),0) as W" + (i + 1) + "B,"         //식전
                    + " ifnull(MAX(CASE  WHEN cast(strftime('%w', " + SUGAR_REGDATE + ") as integer)=" + i + " and (" + SUGAR_BEFORE + " = '2' ) THEN " + SUGAR_SUGAR + " End),0) as W" + (i + 1) + "A";     //식후

            if (i != cnt)
                sql += ",";

            sb.append(sql);
            Logger.i(TAG, sql);
        }

        sql = " FROM " + TB_DATA_SUGAR
                + " WHERE " + SUGAR_REGTYPE + " in ('D', 'U') and " + SUGAR_REGDATE + " BETWEEN '" + sDate + " 00:00' and '" + eDate + " 23:59' and " + SUGAR_DRUGNAME + " = '' and cast(" + SUGAR_SUGAR + " as integer) > 0";
        sb.append(sql);
        Logger.i(TAG, sql);

        sql = sb.toString();


        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count=" + cursor.getCount());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                Logger.i(TAG, "cursor.getColumnCount()=" + cursor.getColumnCount());
                for (int i = 1; i <= cursor.getColumnCount() / 2; i++) {
                    float before = cursor.getInt(cursor.getColumnIndex("W" + i + "B"));
                    float after = cursor.getInt(cursor.getColumnIndex("W" + i + "A"));
                    before = replaceVal(before, beforeAndAfterType);
                    after = replaceVal(after, beforeAndAfterType);

                    yVals1.add(new SticEntry(i - 1, after, before, beforeAndAfterType));
                    Logger.i(TAG, "getResultWeek[" + i + "] before=" + before + ", after=" + after);
                }
                return yVals1;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            for (int i = 1; i <= cursor.getColumnCount() / 2; i++) {
                yVals1.add(new SticEntry(i - 1, 0, 0, beforeAndAfterType));
            }
            cursor.close();
            return yVals1;
        }

        return yVals1;
    }

    /**
     * 60미만은 60으로 표시
     * 240이상은 240으로 표시
     *
     * @param val
     * @return
     */
    private float replaceVal(float val, int beforeAndAfterType) {
        int max = beforeAndAfterType == 1 ? 150 : 240;
        if (val == 0)
            return 0f;  // 빈값으로 간주

        if (60 > val) {
            val = 60;
        } else if (max < val) {
            val = max;
        }

        return val;
    }

    /*
    // 월단위
     */
    public List<SticEntry> getResultMonth(String nYear, String nMonth, int dayCnt, int beforeAndAfterType) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<SticEntry> yVals1 = new ArrayList<>();

        StringBuffer sb = new StringBuffer();
        Logger.d(TAG, "######################################### 혈당 월간 쿼리 조회 #########################################");
        String sql = "SELECT ";
        sb.append(sql);
        Logger.i(TAG, sql);
        for (int i = 0; i <= dayCnt; i++) {
            sql = (" ifnull(MIN(CASE  WHEN cast(strftime('%d', " + SUGAR_REGDATE + ") as integer)=" + i + " and (" + SUGAR_BEFORE + "!= '2') THEN " + SUGAR_SUGAR + " End), 0) as D" + i + "B,"                   //식전
                    + " ifnull(MAX(CASE  WHEN cast(strftime('%d', " + SUGAR_REGDATE + ") as integer)=" + i + " and (" + SUGAR_BEFORE + "= '2' ) THEN " + SUGAR_SUGAR + " End), 0) as D" + i + "A");    //식후

            if (i != dayCnt)
                sql += ",";

            sb.append(sql);
            Logger.i(TAG, sql);
        }
        sql = " FROM " + TB_DATA_SUGAR
                + " WHERE  " + SUGAR_REGTYPE + " in ('D', 'U') and cast(strftime('%Y'," + SUGAR_REGDATE + ") as integer)=" + nYear + " and cast(strftime('%m'," + SUGAR_REGDATE + ") as integer)=" + nMonth + " and " + SUGAR_DRUGNAME + " = '' and cast(" + SUGAR_SUGAR + " as integer) > 0"
                + " Group by strftime('%Y'," + SUGAR_REGDATE + "), strftime('%m'," + SUGAR_REGDATE + ") ";
        Logger.i(TAG, sql);
        sb.append(sql);
        sql = sb.toString();
        Logger.d(TAG, "######################################### 혈당 월간 쿼리 조회 #########################################");

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count=" + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                int d1b = cursor.getInt(cursor.getColumnIndex("D1B"));
                int d1a = cursor.getInt(cursor.getColumnIndex("D1A"));
                int d2b = cursor.getInt(cursor.getColumnIndex("D2B"));
                int d2a = cursor.getInt(cursor.getColumnIndex("D2A"));
                int d3b = cursor.getInt(cursor.getColumnIndex("D3B"));
                int d3a = cursor.getInt(cursor.getColumnIndex("D3A"));
                int d4b = cursor.getInt(cursor.getColumnIndex("D4B"));
                int d4a = cursor.getInt(cursor.getColumnIndex("D4A"));
                int d5b = cursor.getInt(cursor.getColumnIndex("D5B"));
                int d5a = cursor.getInt(cursor.getColumnIndex("D5A"));
                int d6b = cursor.getInt(cursor.getColumnIndex("D6B"));
                int d6a = cursor.getInt(cursor.getColumnIndex("D6A"));
                int d7b = cursor.getInt(cursor.getColumnIndex("D7B"));
                int d7a = cursor.getInt(cursor.getColumnIndex("D7A"));
                int d8b = cursor.getInt(cursor.getColumnIndex("D8B"));
                int d8a = cursor.getInt(cursor.getColumnIndex("D8A"));
                int d9b = cursor.getInt(cursor.getColumnIndex("D9B"));
                int d9a = cursor.getInt(cursor.getColumnIndex("D9A"));
                int d10b = cursor.getInt(cursor.getColumnIndex("D10B"));
                int d10a = cursor.getInt(cursor.getColumnIndex("D10A"));
                int d11b = cursor.getInt(cursor.getColumnIndex("D11B"));
                int d11a = cursor.getInt(cursor.getColumnIndex("D11A"));
                int d12b = cursor.getInt(cursor.getColumnIndex("D12B"));
                int d12a = cursor.getInt(cursor.getColumnIndex("D12A"));
                int d13b = cursor.getInt(cursor.getColumnIndex("D13B"));
                int d13a = cursor.getInt(cursor.getColumnIndex("D13A"));
                int d14b = cursor.getInt(cursor.getColumnIndex("D14B"));
                int d14a = cursor.getInt(cursor.getColumnIndex("D14A"));
                int d15b = cursor.getInt(cursor.getColumnIndex("D15B"));
                int d15a = cursor.getInt(cursor.getColumnIndex("D15A"));
                int d16b = cursor.getInt(cursor.getColumnIndex("D16B"));
                int d16a = cursor.getInt(cursor.getColumnIndex("D16A"));
                int d17b = cursor.getInt(cursor.getColumnIndex("D17B"));
                int d17a = cursor.getInt(cursor.getColumnIndex("D17A"));
                int d18b = cursor.getInt(cursor.getColumnIndex("D18B"));
                int d18a = cursor.getInt(cursor.getColumnIndex("D18A"));
                int d19b = cursor.getInt(cursor.getColumnIndex("D19B"));
                int d19a = cursor.getInt(cursor.getColumnIndex("D19A"));
                int d20b = cursor.getInt(cursor.getColumnIndex("D20B"));
                int d20a = cursor.getInt(cursor.getColumnIndex("D20A"));
                int d21b = cursor.getInt(cursor.getColumnIndex("D21B"));
                int d21a = cursor.getInt(cursor.getColumnIndex("D21A"));
                int d22b = cursor.getInt(cursor.getColumnIndex("D22B"));
                int d22a = cursor.getInt(cursor.getColumnIndex("D22A"));
                int d23b = cursor.getInt(cursor.getColumnIndex("D23B"));
                int d23a = cursor.getInt(cursor.getColumnIndex("D23A"));
                int d24b = cursor.getInt(cursor.getColumnIndex("D24B"));
                int d24a = cursor.getInt(cursor.getColumnIndex("D24A"));
                int d25b = cursor.getInt(cursor.getColumnIndex("D25B"));
                int d25a = cursor.getInt(cursor.getColumnIndex("D25A"));
                int d26b = cursor.getInt(cursor.getColumnIndex("D26B"));
                int d26a = cursor.getInt(cursor.getColumnIndex("D26A"));
                int d27b = cursor.getInt(cursor.getColumnIndex("D27B"));
                int d27a = cursor.getInt(cursor.getColumnIndex("D27A"));
                int d28b = cursor.getInt(cursor.getColumnIndex("D28B"));
                int d28a = cursor.getInt(cursor.getColumnIndex("D28A"));

                Logger.i(TAG, "결과 d1a:" + d1a + ", d2a:" + d2a + ", d3a:" + d3a + ", d4a:" + d4a + ", d5a:" + d5a + ", d6a:" + d6a + ", d7a:" + d7a);
                Logger.i(TAG, "d8a:" + d8a + ", d9a:" + d9a + ", d10a:" + d10a + ", d11a:" + d11a + ", d12a:" + d12a + ", d13a:" + d13a + ", d14a:" + d14a + ", d15a:" + d15a);
                Logger.i(TAG, "d16a:" + d16a + ", d17a:" + d17a + ", d18a:" + d18a + ", d19a:" + d19a + ", d20a:" + d20a + ", d21a:" + d21a + ", d22a:" + d22a + ", d23a:" + d23a);
                Logger.i(TAG, "d24a:" + d24a + ", d25a:" + d25a + ", d26a:" + d26a + ", d27a:" + d27a + ", d28a:" + d28a);    //+", d29a:"+d29a+", d30a:"+d30a+", d31a:"+d31a
                Logger.i(TAG, "----- ");
                Logger.i(TAG, "d1b:" + d1b + ", d2b:" + d2b + ", d3b:" + d3b + ", d4b:" + d4b + ", d5b:" + d5b + ", d6b:" + d6b + ", d7b:" + d7b);
                Logger.i(TAG, "d8b:" + d8b + ", d9b:" + d9b + ", d10b:" + d10b + ", d11b:" + d11b + ", d12b:" + d12b + ", d13b:" + d13b + ", d14b:" + d14b + ", d15:" + d15b);
                Logger.i(TAG, "d16b:" + d16b + ", d17b:" + d17b + ", d18b:" + d18b + ", d19b:" + d19b + ", d20b:" + d20b + ", d21b:" + d21b + ", d22b:" + d22b + ", d23b:" + d23b);
                Logger.i(TAG, "d24b:" + d24b + ", d25b:" + d25b + ", d26b:" + d26b + ", d27b:" + d27b + ", d28b:" + d28b);    //+", d29b:"+d29b+", d30b:"+d30b+", d31b:"+d31b

                for (int i = 1; i <= dayCnt; i++) {
                    float before = cursor.getInt(cursor.getColumnIndex("D" + i + "B"));
                    float after = cursor.getInt(cursor.getColumnIndex("D" + i + "A"));

                    before = replaceVal(before, beforeAndAfterType);
                    after = replaceVal(after, beforeAndAfterType);
                    yVals1.add(new SticEntry(i - 1
                            , after
                            , before
                            , beforeAndAfterType
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            for (int i = 1; i <= dayCnt; i++) {
                yVals1.add(new SticEntry(i - 1
                        , 0
                        , 0
                        , beforeAndAfterType
                ));
            }
            cursor.close();
        }
        return yVals1;
    }

    /*
     ** 혈당 메인 투약시간 그래프
    */
    public List<MedicenData> getMedicenTime(String sDate, String eDate) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        List<MedicenData> arrayMedi = new ArrayList<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT " + SUGAR_IDX + ", " + SUGAR_SUGAR + ", " + SUGAR_REGDATE + ", " + SUGAR_DRUGNAME +
                " FROM " + TB_DATA_SUGAR +
                " WHERE  cast(" + SUGAR_SUGAR + " as integer) <= '0'  and " + SUGAR_REGDATE + " BETWEEN '" + sDate + " 00:00' and '" + eDate + " 23:59' " +
                " Order by datetime("+ SUGAR_REGDATE +") desc, cast(" + SUGAR_IDX +  " as BIGINT) ASC;";
        Logger.i(TAG, "sql=" + sql);

        Cursor cursor = db.rawQuery(sql, null);


        Logger.i(TAG, "getResultRecent cursor.moveToNext():" + cursor.getCount());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                for (int i = 0; i <= cursor.getCount() - 1; i++) {
                    String sugarIdx = cursor.getString(cursor.getColumnIndex(SUGAR_IDX));
                    String regdate = cursor.getString(cursor.getColumnIndex(SUGAR_REGDATE));
                    String sugar = cursor.getString(cursor.getColumnIndex(SUGAR_SUGAR));
                    String name = cursor.getString(cursor.getColumnIndex(SUGAR_DRUGNAME));

                    MedicenData mediData = new MedicenData();
                    mediData.setMedi_idx(sugarIdx);
                    mediData.setMedi_regdate(regdate);

                    arrayMedi.add(i, mediData);
                    Logger.i(TAG, "getMedicenTime sugarIdx : " + sugarIdx + " regdate " + regdate + " name : " + name + " arrayMedi : " + arrayMedi);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }

        Logger.i(TAG, "getMedicenTime arrayMedi : " + arrayMedi);
        return arrayMedi;
    }

    /*
    // 메인 통계 (식전평균, 식후평균, 최고값, 최저값)
     */
    public SugarStaticData getResultStatic(String sDate, String eDate, int eatState) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<SticEntry> yVals1 = new ArrayList<>();
        String result = "";
        String Eat = "";
        if (eatState == 0) { //ALL
            Eat = "'0','1','2','3',''";
        } else if (eatState == 1) { //BEFORE
            Eat = "'0','1','3',''";
        } else if (eatState == 2) { //AFTER
            Eat = "'2'";
        }

        String sql = "Select "
                + " AVG(CASE  WHEN (" + SUGAR_BEFORE + " != '2') THEN cast(" + SUGAR_SUGAR + " as integer) End) as BEFSUBAR, "        //식전 평균
                + " AVG(CASE  WHEN (" + SUGAR_BEFORE + " = '2') THEN cast(" + SUGAR_SUGAR + " as integer) End) as AFTSUGAR, "    //식후 평균
                + " MAX(CASE WHEN (" + SUGAR_BEFORE + " in (" + Eat + ") ) THEN cast(" + SUGAR_SUGAR + " as integer) End) as MAXSUGAR, "     //최고값
                + " MIN(CASE WHEN (" + SUGAR_BEFORE + " in (" + Eat + ") ) THEN cast(" + SUGAR_SUGAR + " as integer) End) as MINSUGAR "     //최저값
                + " FROM " + TB_DATA_SUGAR
                + " WHERE " + SUGAR_REGDATE + " BETWEEN '" + sDate + " 00:00' and '" + eDate + " 23:59' and " + SUGAR_DRUGNAME + " = '' and cast(" + SUGAR_SUGAR + " as integer) > 0";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count=" + cursor.getCount());
        SugarStaticData data = new SugarStaticData();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                int v1 = cursor.getInt(cursor.getColumnIndex("BEFSUBAR"));  // 식전평균
                int v2 = cursor.getInt(cursor.getColumnIndex("AFTSUGAR"));  // 식후평균
                int v3 = cursor.getInt(cursor.getColumnIndex("MAXSUGAR"));  // 최고값
                int v4 = cursor.getInt(cursor.getColumnIndex("MINSUGAR"));  // 최저값 

                Logger.i(TAG, "결과 BEFSUBAR:" + v1 + ", AFTSUGAR:" + v2 + ", MAXSUGAR:" + v3 + ", MINSUGAR:" + v4);
                data.setBefsugar(v1);
                data.setAftsugar(v2);
                data.setMaxsugar(v3);
                data.setMinsugar(v4);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            cursor.close();
        }
        return data;
    }

    public static class MedicenData {
        private String medi_idx = "";
        private String medi_regdate = "";

        public String getMedi_idx() {
            return medi_idx;
        }

        public void setMedi_idx(String medi_idx) {
            this.medi_idx = medi_idx;
        }

        public String getMedi_regdate() {
            return medi_regdate;
        }

        public void setMedi_regdate(String medi_regdate) {
            this.medi_regdate = medi_regdate;
        }

    }

    public static class SugarStaticData {
        private int befsugar = 0;
        private int aftsugar = 0;
        private int maxsugar = 0;
        private int minsugar = 0;
        private String redate = "";

        public int getBefsugar() {
            return befsugar;
        }

        public void setBefsugar(int befsugar) {
            this.befsugar = befsugar;
        }

        public int getAftsugar() {
            return aftsugar;
        }

        public void setAftsugar(int aftsugar) {
            this.aftsugar = aftsugar;
        }

        public int getMaxsugar() {
            return maxsugar;
        }

        public void setMaxsugar(int maxsugar) {
            this.maxsugar = maxsugar;
        }

        public int getMinsugar() {
            return minsugar;
        }

        public void setMinsugar(int minsugar) {
            this.minsugar = minsugar;
        }


        public String getRedate() {
            return redate;
        }

        public void setRedate(String redate) {
            this.redate = redate;
        }
    }


    //////건강 메세지 쿼리

    // 주간 / 월간 식전 혈당 최고 수치가 100이상일때
    public SugarStaticData getBeforeMaxSugar(String dateType) {

        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sDate = "";
        String eDate = "";

        if (dateType.equals("m")) {
            // 이번달 최대 일수
            Calendar cal = Calendar.getInstance(); // CDateUtil.getCalendar_yyyyMMdd(startDay);
            cal.setTime(new Date(System.currentTimeMillis() - (60000 * 60 * 24 * 1)));
            int dayCnt = cal.getActualMaximum(Calendar.DATE);

            sDate = CDateUtil.getFormattedString_yyyy_MM_dd(System.currentTimeMillis() - (60000l * 60 * 24 * dayCnt));
            eDate = CDateUtil.getFormattedString_yyyy_MM_dd(System.currentTimeMillis() - (60000 * 60 * 24 * 1));

        } else {
            sDate = CDateUtil.getFormattedString_yyyy_MM_dd(System.currentTimeMillis() - (60000 * 60 * 24 * 7));
            eDate = CDateUtil.getFormattedString_yyyy_MM_dd(System.currentTimeMillis() - (60000 * 60 * 24 * 1));
        }

        String sql = "Select "
                + SUGAR_SUGAR + " as SUAGR , "  //SUGAR >= 100 , MAX(SUGAR)
                + SUGAR_REGDATE + " as REDATE" //등록일
                + " FROM " + TB_DATA_SUGAR
                + " WHERE " + SUGAR_REGDATE + " BETWEEN '" + sDate + " 00:00' and '" + eDate + " 23:59' and " + SUGAR_BEFORE + " != '2' and cast(" + SUGAR_SUGAR + " as integer) >= 100"
                + " ORDER BY datetime("+ SUGAR_REGDATE +") desc, cast(" + SUGAR_IDX + " as BIGINT) DESC LIMIT 1";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count=" + cursor.getCount());
        SugarStaticData data = new SugarStaticData();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                int v1 = cursor.getInt(cursor.getColumnIndex("SUAGR"));  // SUGAR
                String v2 = cursor.getString(cursor.getColumnIndex("REDATE"));  // 등록일

                Logger.i(TAG, "결과 SUAGR:" + v1 + ", REDATE:" + v2);

                data.setMaxsugar(v1);
                data.setRedate(v2);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            cursor.close();
        }
        return data;
    }

    // 주간 / 월간 식후 혈당 최고 수치가 140이상일때
    public SugarStaticData getAfterMaxSugar(String dateType) {

        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sDate = "";
        String eDate = "";

        if (dateType.equals("m")) {
            // 이번달 최대 일수
            Calendar cal = Calendar.getInstance(); // CDateUtil.getCalendar_yyyyMMdd(startDay);
            cal.setTime(new Date(System.currentTimeMillis() - (60000 * 60 * 24 * 1)));
            int dayCnt = cal.getActualMaximum(Calendar.DATE);

            sDate = CDateUtil.getFormattedString_yyyy_MM_dd(System.currentTimeMillis() - (60000l * 60 * 24 * dayCnt));
            eDate = CDateUtil.getFormattedString_yyyy_MM_dd(System.currentTimeMillis() - (60000 * 60 * 24 * 1));

        } else {
            sDate = CDateUtil.getFormattedString_yyyy_MM_dd(System.currentTimeMillis() - (60000 * 60 * 24 * 7));
            eDate = CDateUtil.getFormattedString_yyyy_MM_dd(System.currentTimeMillis() - (60000 * 60 * 24 * 1));
        }

        String sql = "Select "
                + SUGAR_SUGAR + " as SUAGR , "  //SUGAR >= 100 , MAX(SUGAR)
                + SUGAR_REGDATE + " as REDATE" //등록일
                + " FROM " + TB_DATA_SUGAR
                + " WHERE " + SUGAR_REGDATE + " BETWEEN '" + sDate + " 00:00' and '" + eDate + " 23:59' and " + SUGAR_BEFORE + " in  ('2') and cast(" + SUGAR_SUGAR + " as integer) >= 140"
                + " ORDER BY datetime("+ SUGAR_REGDATE +") desc, cast(" + SUGAR_IDX + " as BIGINT) DESC LIMIT 1";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count=" + cursor.getCount());
        SugarStaticData data = new SugarStaticData();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                int v1 = cursor.getInt(cursor.getColumnIndex("SUAGR"));  // SUGAR
                String v2 = cursor.getString(cursor.getColumnIndex("REDATE"));  // 등록일

                Logger.i(TAG, "결과 SUAGR:" + v1 + ", REDATE:" + v2);

                data.setMaxsugar(v1);
                data.setRedate(v2);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            cursor.close();
        }
        return data;
    }

    // 혈당수치를 입력했는지 여부
    public String getSugarDataCheck() {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql = "Select "
                + SUGAR_SUGAR + " as SUAGR , "  // SUGAR
                + SUGAR_REGDATE + " as REDATE" // 등록일
                + " FROM " + TB_DATA_SUGAR
                + " WHERE cast(" + SUGAR_SUGAR + " as integer) > 0"
                + " ORDER BY datetime("+ SUGAR_REGDATE +") desc, cast(" + SUGAR_IDX + " as BIGINT) DESC LIMIT 1";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count=" + cursor.getCount());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                return "Y";
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            cursor.close();

            return "N";
        }

        return "N";
    }

    //  2,3,4 주간 가장 높은 식전 또는 식후 혈당이 정상범위에서 벗어날 때
    public String getHightSugarCheck() {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sDate = CDateUtil.getFormattedString_yyyy_MM_dd(System.currentTimeMillis() - (60000 * 60 * 24 * 7));
        String eDate = CDateUtil.getFormattedString_yyyy_MM_dd(System.currentTimeMillis() - (60000 * 60 * 24 * 1));

        String sql = "Select "
                + SUGAR_SUGAR + " as SUAGR , "  //SUGAR >= 100 , MAX(SUGAR)
                + SUGAR_REGDATE + " as REDATE" //등록일
                + " FROM " + TB_DATA_SUGAR
                + " WHERE " + SUGAR_REGDATE + " BETWEEN '" + sDate + " 00:00' and '" + eDate + " 23:59' and ((" + SUGAR_BEFORE + " in  ('0','1','2','3','') and cast(" + SUGAR_SUGAR + " as integer) >= 140) or (" + SUGAR_BEFORE + " = '0' and cast(" + SUGAR_SUGAR + " as integer) >= 100))"
                + " ORDER BY datetime("+ SUGAR_REGDATE +") desc, cast(" + SUGAR_IDX + " as BIGINT) DESC LIMIT 1";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count=" + cursor.getCount());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                return "Y";
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            cursor.close();
            return "N";
        }

        return "N";

    }
}
