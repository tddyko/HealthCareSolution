package com.greencross.greencare.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import com.greencross.greencare.bluetooth.model.WaterModel;
import com.greencross.greencare.charting.data.BarEntry;
import com.greencross.greencare.network.tr.data.Tr_get_hedctdata;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrsohn on 2017. 3. 20..
 */

public class DBHelperWater {
    private final String TAG = DBHelperWater.class.getSimpleName();

    private DBHelper mHelper;

    public DBHelperWater(DBHelper helper) {
        mHelper = helper;
    }

    /**
     * 물 데이터
     */
    public static String TB_DATA_WATER = "tb_data_water";           // 물 데이터
    private String WATER_IDX = "idx";            // 고유번호intM서버데이터 삭제를 위한 키값
    private String WATER_AMOUNT = "amount";            // 마신량Str7M
    private String WATER_REGTYPE = "regtype";            // 등록타입Str1MD:디바이스,U:직접등록
    private String WATER_REGDATE = "regdate";            // 등록일시
    private String IS_SERVER_REGIST = "is_server_regist";                // 서버 등록 여부


    // DB를 새로 생성할 때 호출되는 함수
    public String createDb() {
        // 새로운 테이블 생성
        String sql = " CREATE TABLE if not exists " + TB_DATA_WATER + " ("
                + WATER_IDX + " INTEGER , "
                + WATER_AMOUNT + " VARCHAR(10) NULL, "
                + WATER_REGTYPE + " VARCHAR(1) NULL, "
                + IS_SERVER_REGIST + " BOOLEAN, "
                + WATER_REGDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP);";
        Logger.i(TAG, "onCreate.sql=" + sql);
        return sql;
    }

    // 히스토리에서 선택된 DB로우를 삭제하는 함수
    public void DeleteDb(String idx) {
        String sql = "DELETE FROM " + TB_DATA_WATER + " WHERE idx = '" + idx + "' ";
        Logger.i(TAG, "onDelete.sql = " + sql);
        mHelper.transactionExcute(sql);

    }

    // 특정 로우 업데이트 하는 함수
    public void UpdateDb(String idx, String amount, String reg_de) {
        String sql = "UPDATE " + TB_DATA_WATER +
                " SET " + WATER_AMOUNT + " = " + amount + ", " +
                WATER_REGDATE + "='" + CDateUtil.getRegDateFormat_yyyyMMddHHss(reg_de) + "'" +
                " WHERE idx = " + idx;

        Logger.i(TAG, "onUpdate.sql = " + sql);
        mHelper.transactionExcute(sql);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    public String upgradeDb() {
        return "DROP TABLE " + TB_DATA_WATER + ";";
    }

    /**
     * 블루투스 디바이스에서 데이터를 받았을 때
     *
     * @param dataModel
     * @param isServerReg
     */
    public void insert(SparseArray<WaterModel> dataModel, boolean isServerReg) {

        StringBuffer sb = new StringBuffer();
        if (dataModel.size() > 0) {
            WaterModel data = dataModel.get(dataModel.keyAt(dataModel.size() - 1));
            String sql = "INSERT INTO " + TB_DATA_WATER
                    + " VALUES('" + data.getIndexTime() + "', '"
                    + data.getAmonut() + "', '"
                    + data.getRegtype() + "', '"
                    + isServerReg + "', '"
                    + CDateUtil.getRegDateFormat_yyyyMMddHHss(data.getRegTime()) + "');";
            sb.append(sql);
        }


        Logger.i(TAG, "insert.sql=" + sb.toString());
        mHelper.transactionExcute(sb.toString());

        getResult();
    }

    public void insert(List<Tr_get_hedctdata.DataList> datas, boolean isServerReg) {

        String sql = "INSERT INTO " + TB_DATA_WATER
                + " VALUES ";

        for (Tr_get_hedctdata.DataList data : datas) {
            StringBuffer sb = new StringBuffer();
            sb.append(sql);

            String values = "('"
                    + data.idx + "', '" //3",
                    + data.amount + "', '" //100",
                    + data.regtype + "', '" //D",
                    + isServerReg + "', '"
                    + CDateUtil.getRegDateFormat_yyyyMMddHHss(data.reg_de) + "')";

            sb.append(values);

            Logger.i(TAG, "insert.sql=" + sb.toString());
            mHelper.transactionExcute(sb.toString());

        }

    }


    public List<Tr_get_hedctdata.DataList> getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String result = "";
        List<Tr_get_hedctdata.DataList> data_list = new ArrayList<>();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT * FROM " + TB_DATA_WATER
                + " ORDER BY datetime("+ WATER_REGDATE +") desc, cast(" + WATER_IDX + " as BIGINT) DESC";
        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        int i = 0;
        try {
            while (cursor.moveToNext()) {
                String waterIdx = cursor.getString(cursor.getColumnIndex(WATER_IDX));
                String amount = cursor.getString(cursor.getColumnIndex(WATER_AMOUNT));
                String regType = cursor.getString(cursor.getColumnIndex(WATER_REGTYPE));
                String regDate = cursor.getString(cursor.getColumnIndex(WATER_REGDATE));

                Tr_get_hedctdata.DataList data = new Tr_get_hedctdata.DataList();
                data.idx = waterIdx;
                data.amount = amount;
                data.regtype = regType;
                data.reg_de = regDate;

                Logger.i(TAG, "result i=[" + (i++) + "] sugarIdx[" + waterIdx + "], amount=" + amount + ", regType=" + regType + ", regDate=" + regDate);
                data_list.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return data_list;
    }

    /*
    // 일단위
     */
    public List<BarEntry> getResultDay(String nDate) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<BarEntry> yVals1 = new ArrayList<>();

        String sql = "Select "
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 0 THEN " + WATER_AMOUNT + " End),0) as H0, "
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 1 THEN " + WATER_AMOUNT + " End),0) as H1,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 2 THEN " + WATER_AMOUNT + " End),0) as H2,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 3 THEN " + WATER_AMOUNT + " End),0) as H3,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 4 THEN " + WATER_AMOUNT + " End),0) as H4,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 5 THEN " + WATER_AMOUNT + " End),0) as H5,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 6 THEN " + WATER_AMOUNT + " End),0) as H6,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 7 THEN " + WATER_AMOUNT + " End),0) as H7,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 8 THEN " + WATER_AMOUNT + " End),0) as H8,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 9 THEN " + WATER_AMOUNT + " End),0) as H9,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 10 THEN " + WATER_AMOUNT + " End),0) as H10,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 11 THEN " + WATER_AMOUNT + " End),0) as H11,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 12 THEN " + WATER_AMOUNT + " End),0) as H12,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 13 THEN " + WATER_AMOUNT + " End),0) as H13,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 14 THEN " + WATER_AMOUNT + " End),0) as H14,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 15 THEN " + WATER_AMOUNT + " End),0) as H15,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 16 THEN " + WATER_AMOUNT + " End),0) as H16,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 17 THEN " + WATER_AMOUNT + " End),0) as H17,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 18 THEN " + WATER_AMOUNT + " End),0) as H18,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 19 THEN " + WATER_AMOUNT + " End),0) as H19,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 20 THEN " + WATER_AMOUNT + " End),0) as H20,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 21 THEN " + WATER_AMOUNT + " End),0) as H21,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 22 THEN " + WATER_AMOUNT + " End),0) as H22,"
                + " ifnull(SUM(CASE cast(strftime('%H', " + WATER_REGDATE + ") as integer) WHEN 23 THEN " + WATER_AMOUNT + " End),0) as H23 "
                + " FROM " + TB_DATA_WATER
                + " WHERE " + WATER_REGDATE + " BETWEEN '" + nDate + " 00:00' and '" + nDate + " 23:59' "
                + " Group by strftime('%Y'," + WATER_REGDATE + "), strftime('%m'," + WATER_REGDATE + "), strftime('%d'," + WATER_REGDATE + ") ";


        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "query =" + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                int h0 = cursor.getInt(cursor.getColumnIndex("H0"));
                int h1 = cursor.getInt(cursor.getColumnIndex("H1"));
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

                Logger.i(TAG, "물 (일단위) h0:" + h0 + ",h1:" + h1 + ", h2:" + h2 + ", h3:" + h3 + ", h4:" + h4 + ", h5:" + h5 + ", h6:" + h6 + ", h7:" + h7);
                Logger.i(TAG, "h8:" + h8 + ", h9:" + h9 + ", h10:" + h10 + ", h11:" + h11 + ", h12:" + h12 + ", h13:" + h13 + ", h14:" + h14 + ", h15:" + h15);
                Logger.i(TAG, "h16:" + h16 + ", h17:" + h17 + ", h18:" + h18 + ", h19:" + h19 + ", h20:" + h20 + ", h21:" + h21 + ", h22:" + h22 + ", h23:" + h23 );

                for (int i = 0; i <= cursor.getColumnCount(); i++) {
                    float waterVal = cursor.getInt(cursor.getColumnIndex("H" + i));
                    BarEntry entry = new BarEntry(i, waterVal);
                    yVals1.add(entry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (int i = 0; i <= cursor.getColumnCount(); i++) {
                BarEntry entry = new BarEntry(i, 0);
                yVals1.add(entry);
            }
        }
        return yVals1;
    }


    /*
    // 주단위
     */
    public List<BarEntry> getResultWeek(String sDate, String eDate) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<BarEntry> yVals1 = new ArrayList<>();
        String result = "";

        String sql = "Select "
                + " ifnull(SUM(CASE cast(strftime('%w', " + WATER_REGDATE + ") as integer) WHEN 0 THEN " + WATER_AMOUNT + " End),0) as W1a,"    //일요일부터 시작
                + " ifnull(SUM(CASE cast(strftime('%w', " + WATER_REGDATE + ") as integer) WHEN 1 THEN " + WATER_AMOUNT + " End),0) as W2a,"
                + " ifnull(SUM(CASE cast(strftime('%w', " + WATER_REGDATE + ") as integer) WHEN 2 THEN " + WATER_AMOUNT + " End),0) as W3a,"
                + " ifnull(SUM(CASE cast(strftime('%w', " + WATER_REGDATE + ") as integer) WHEN 3 THEN " + WATER_AMOUNT + " End),0) as W4a,"
                + " ifnull(SUM(CASE cast(strftime('%w', " + WATER_REGDATE + ") as integer) WHEN 4 THEN " + WATER_AMOUNT + " End),0) as W5a,"
                + " ifnull(SUM(CASE cast(strftime('%w', " + WATER_REGDATE + ") as integer) WHEN 5 THEN " + WATER_AMOUNT + " End),0) as W6a,"
                + " ifnull(SUM(CASE cast(strftime('%w', " + WATER_REGDATE + ") as integer) WHEN 6 THEN " + WATER_AMOUNT + " End),0) as W7a "
                + " FROM " + TB_DATA_WATER
                + " WHERE " + WATER_REGDATE + " BETWEEN '" + sDate + " 00:00' and '" + eDate + " 23:59' ";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "물 count=" + cursor.getCount());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                int w1 = cursor.getInt(cursor.getColumnIndex("W1a"));
                int w2 = cursor.getInt(cursor.getColumnIndex("W2a"));
                int w3 = cursor.getInt(cursor.getColumnIndex("W3a"));
                int w4 = cursor.getInt(cursor.getColumnIndex("W4a"));
                int w5 = cursor.getInt(cursor.getColumnIndex("W5a"));
                int w6 = cursor.getInt(cursor.getColumnIndex("W6a"));
                int w7 = cursor.getInt(cursor.getColumnIndex("W7a"));

                Logger.i(TAG, "물메인 (주단위) w1:" + w1 + ", w2:" + w2 + ", w3:" + w3 + ", w4:" + w4 + ", w5:" + w5 + ", w6:" + w6 + ", w7:" + w7);

                int idx = 0;
                for (int i = 1; i <= cursor.getColumnCount(); i++) {
                    float stepVal = cursor.getInt(cursor.getColumnIndex("W" + i + "a"));
                    BarEntry entry = new BarEntry(idx++, stepVal);
                    yVals1.add(entry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (int i = 1; i <= cursor.getColumnCount(); i++) {
                BarEntry entry = new BarEntry(i - 1, 0);
                yVals1.add(entry);
            }
        }
        return yVals1;
    }


    /*
    // 월단위
     */
    public List<BarEntry> getResultMonth(String nYear, String nMonth) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<BarEntry> yVals1 = new ArrayList<>();

        String sql = "Select "
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 1 THEN " + WATER_AMOUNT + " End),0) as D1,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 2 THEN " + WATER_AMOUNT + " End),0) as D2,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 3 THEN " + WATER_AMOUNT + " End),0) as D3,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 4 THEN " + WATER_AMOUNT + " End),0) as D4,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 5 THEN " + WATER_AMOUNT + " End),0) as D5,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 6 THEN " + WATER_AMOUNT + " End),0) as D6,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 7 THEN " + WATER_AMOUNT + " End),0) as D7,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 8 THEN " + WATER_AMOUNT + " End),0) as D8,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 9 THEN " + WATER_AMOUNT + " End),0) as D9,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 10 THEN " + WATER_AMOUNT + " End),0) as D10,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 11 THEN " + WATER_AMOUNT + " End),0) as D11,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 12 THEN " + WATER_AMOUNT + " End),0) as D12,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 13 THEN " + WATER_AMOUNT + " End),0) as D13,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 14 THEN " + WATER_AMOUNT + " End),0) as D14,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 15 THEN " + WATER_AMOUNT + " End),0) as D15,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 16 THEN " + WATER_AMOUNT + " End),0) as D16,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 17 THEN " + WATER_AMOUNT + " End),0) as D17,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 18 THEN " + WATER_AMOUNT + " End),0) as D18,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 19 THEN " + WATER_AMOUNT + " End),0) as D19,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 20 THEN " + WATER_AMOUNT + " End),0) as D20,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 21 THEN " + WATER_AMOUNT + " End),0) as D21,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 22 THEN " + WATER_AMOUNT + " End),0) as D22,"
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 23 THEN " + WATER_AMOUNT + " End),0) as D23, "
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 24 THEN " + WATER_AMOUNT + " End),0) as D24, "
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 25 THEN " + WATER_AMOUNT + " End),0) as D25, "
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 26 THEN " + WATER_AMOUNT + " End),0) as D26, "
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 27 THEN " + WATER_AMOUNT + " End),0) as D27, "
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 28 THEN " + WATER_AMOUNT + " End),0) as D28, "
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 29 THEN " + WATER_AMOUNT + " End),0) as D29, "
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 30 THEN " + WATER_AMOUNT + " End),0) as D30, "
                + " ifnull(SUM(CASE cast(strftime('%d', " + WATER_REGDATE + ") as integer) WHEN 31 THEN " + WATER_AMOUNT + " End),0) as D31  "
                + " FROM " + TB_DATA_WATER
                + " WHERE cast(strftime('%Y'," + WATER_REGDATE + ") as integer)=" + nYear + " and cast(strftime('%m'," + WATER_REGDATE + ") as integer)=" + nMonth
                + " Group by strftime('%Y'," + WATER_REGDATE + "), strftime('%m'," + WATER_REGDATE + ") ";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "물 월 count=" + cursor.getCount());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                int d1 = cursor.getInt(cursor.getColumnIndex("D1"));
                int d2 = cursor.getInt(cursor.getColumnIndex("D2"));
                int d3 = cursor.getInt(cursor.getColumnIndex("D3"));
                int d4 = cursor.getInt(cursor.getColumnIndex("D4"));
                int d5 = cursor.getInt(cursor.getColumnIndex("D5"));
                int d6 = cursor.getInt(cursor.getColumnIndex("D6"));
                int d7 = cursor.getInt(cursor.getColumnIndex("D7"));
                int d8 = cursor.getInt(cursor.getColumnIndex("D8"));
                int d9 = cursor.getInt(cursor.getColumnIndex("D9"));
                int d10 = cursor.getInt(cursor.getColumnIndex("D10"));
                int d11 = cursor.getInt(cursor.getColumnIndex("D11"));
                int d12 = cursor.getInt(cursor.getColumnIndex("D12"));
                int d13 = cursor.getInt(cursor.getColumnIndex("D13"));
                int d14 = cursor.getInt(cursor.getColumnIndex("D14"));
                int d15 = cursor.getInt(cursor.getColumnIndex("D15"));
                int d16 = cursor.getInt(cursor.getColumnIndex("D16"));
                int d17 = cursor.getInt(cursor.getColumnIndex("D17"));
                int d18 = cursor.getInt(cursor.getColumnIndex("D18"));
                int d19 = cursor.getInt(cursor.getColumnIndex("D19"));
                int d20 = cursor.getInt(cursor.getColumnIndex("D20"));
                int d21 = cursor.getInt(cursor.getColumnIndex("D21"));
                int d22 = cursor.getInt(cursor.getColumnIndex("D22"));
                int d23 = cursor.getInt(cursor.getColumnIndex("D23"));
                int d24 = cursor.getInt(cursor.getColumnIndex("D24"));
                int d25 = cursor.getInt(cursor.getColumnIndex("D25"));
                int d26 = cursor.getInt(cursor.getColumnIndex("D26"));
                int d27 = cursor.getInt(cursor.getColumnIndex("D27"));
                int d28 = cursor.getInt(cursor.getColumnIndex("D28"));
                int d29 = cursor.getInt(cursor.getColumnIndex("D29"));
                int d30 = cursor.getInt(cursor.getColumnIndex("D30"));
                int d31 = cursor.getInt(cursor.getColumnIndex("D31"));

                Logger.i(TAG, "물메인 (월단위) d1:" + d1 + ", d2:" + d2 + ", d3:" + d3 + ", d4:" + d4 + ", d5:" + d5 + ", d6:" + d6 + ", d7:" + d7);
                Logger.i(TAG, "d8:" + d8 + ", d9:" + d9 + ", d10:" + d10 + ", d11:" + d11 + ", d12:" + d12 + ", d13:" + d13 + ", d14:" + d14 + ", d15:" + d15);
                Logger.i(TAG, "d16:" + d16 + ", d17:" + d17 + ", d18:" + d18 + ", d19:" + d19 + ", d20:" + d20 + ", d21:" + d21 + ", d22:" + d22 + ", d23:" + d23);
                Logger.i(TAG, "d24:" + d24 + ", d25:" + d25 + ", d26:" + d26 + ", d27:" + d27 + ", d28:" + d28 + ", d29:" + d29 + ", d30:" + d30 + ", d31:" + d31);

                int idx = 0;
                for (int i = 1; i <= cursor.getColumnCount(); i++) {
                    float stepVal = cursor.getInt(cursor.getColumnIndex("D" + i));
                    BarEntry entry = new BarEntry(idx++, stepVal);
                    yVals1.add(entry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (int i = 1; i <= cursor.getColumnCount(); i++) {
                BarEntry entry = new BarEntry(i - 1, 0);
                yVals1.add(entry);
            }
        }
        return yVals1;
    }

    /*
    // 바텀
     */
    public WaterStaticData getResultStatic(String sDate, String eDate) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        String result = "";

        String sql = "Select "
                + " SUM(" + WATER_AMOUNT + ") as " + WATER_AMOUNT + " "
                + " FROM " + TB_DATA_WATER
                + " WHERE " + WATER_REGDATE + " BETWEEN '" + sDate + " 00:00' and '" + eDate + " 23:59'";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count=" + cursor.getCount());

        WaterStaticData data = new WaterStaticData();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                Integer amount = cursor.getInt(cursor.getColumnIndex(WATER_AMOUNT));

                Logger.i(TAG, "결과 AMOUNT:" + amount);
                data.setAmount(amount);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {

        }

        return data;
    }


    public String getResultMain() {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String nowDate = CDateUtil.getToday_yyyyMMdd();

        String sql = "Select "
                + " SUM(" + WATER_AMOUNT + ") as " + WATER_AMOUNT
                + " FROM " + TB_DATA_WATER
                + " WHERE " + WATER_REGDATE + " BETWEEN '" + nowDate + " 00:00' and '" + nowDate + " 23:59' ";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count=" + cursor.getCount());

        String amount = "";

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                amount = cursor.getString(cursor.getColumnIndex(WATER_AMOUNT));

                Logger.i(TAG, "결과 [getResultMain] AMOUNT:" + amount);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
        }

        return amount;
    }

    public static class WaterStaticData {
        private int amount = 0;

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

    }
}
