package com.greencross.greencare.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.greencross.greencare.base.value.TypeDataSet;
import com.greencross.greencare.bluetooth.model.BandModel;
import com.greencross.greencare.charting.data.BarEntry;
import com.greencross.greencare.network.tr.data.Tr_get_hedctdata;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mrsohn on 2017. 3. 20..
 */

public class DBHelperPPG {
    private final String TAG = DBHelperPPG.class.getSimpleName();

    private DBHelper mHelper;
    public DBHelperPPG(DBHelper helper) {
        mHelper = helper;
    }
    /**
     * 운동데이터
     */
    public static String TB_DATA_PPG = "tb_data_ppg";             // 심박수데이터
    private String PPG_IDX = "idx";
    private String PPG_HRM = "hrm";
    private String PPG_REGTYPE = "regtype";        // 등록타입 Str 1 M
    private String PPG_REGDATE = "regdate";        //등록일시 Date M
    private String IS_SERVER_REGIST = "is_server_regist";                // 서버 등록 여부


    // DB를 새로 생성할 때 호출되는 함수
    public String createDb() {
        // 새로운 테이블 생성
        String sql = " CREATE TABLE if not exists "+TB_DATA_PPG+" ("
                            +PPG_IDX+" CHARACTER(14) PRIMARY KEY, "
                            +PPG_HRM+" VARCHAR(15) NULL, "
                            +PPG_REGTYPE+" CHARACTER(1) NULL, "
                            +IS_SERVER_REGIST+" BOOLEAN, "
                            +PPG_REGDATE+" DATETIME DEFAULT CURRENT_TIMESTAMP); ";
        Logger.i(TAG, "onCreate.sql="+sql);
        return sql;
    }

    // 히스토리에서 선택된 DB로우를 삭제하는 함수
    public void DeleteDb(String idx){
        String sql = "DELETE FROM " +TB_DATA_PPG + " WHERE idx =='"+idx+"' ";
        Logger.i(TAG, "onDelete.sql = "+sql);
        mHelper.transactionExcute(sql);

    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    public String upgradeDb() {
        return "DROP TABLE "+TB_DATA_PPG+";";
    }

    public void insert(List<BandModel> dataModel, boolean isServerReg) {

        SimpleDateFormat sNow = new SimpleDateFormat("HH");
        int nHour = StringUtil.getIntVal(sNow.format(new Date(System.currentTimeMillis())));


        String sql = "INSERT INTO "+TB_DATA_PPG
                +" VALUES";

        for (BandModel data : dataModel) {

            if (data.getHrm()==0)
                    continue;

            int diff = 0;
            if (data.getHour() >= nHour){
                diff = -1;
            }

            Logger.d(TAG, "data.getHour:"+data.getHour());
            String yyymmddhhmmss = CDateUtil.getForamtyyyyMMddHOUR0000(data.getHour(), diff);
            Log.d(TAG, "yyyyymmddhhmmssymmddhhmmss:"+yyymmddhhmmss);

            StringBuffer sb = new StringBuffer();
            sb.append(sql);
            String values = "('"+data.getIdx()+ "', '"
                    +data.getHrm()+ "', '"
                    +data.getRegtype()+ "', '"
                    +isServerReg+ "', '"
                    +CDateUtil.getRegDateFormat_yyyyMMddHHss(yyymmddhhmmss)+ "') ";

            sb.append(values);
            Logger.i(TAG, "insert.sql="+sb.toString());
            mHelper.transactionExcute(sb.toString());
        }

        getResult();
    }

    public void insert(Tr_get_hedctdata datas, boolean isServerReg) {
        // DB에 입력한 값으로 행 추가
        String sql = "INSERT INTO "+TB_DATA_PPG
                +" VALUES";

        for (Tr_get_hedctdata.DataList data : datas.data_list) {

            if (data.hrm.equals("0"))
                continue;

            StringBuffer sb = new StringBuffer();
            sb.append(sql);
            String values = "("+data.idx+ ", '"
                                +data.hrm+ "', '"
                                +data.regtype+ "', '"
                                +isServerReg+ "', '"
                                +CDateUtil.getRegDateFormat_yyyyMMddHHss(data.reg_de)+ "')";

            sb.append(values);

            Logger.i(TAG, "insert.sql="+sb.toString());
            mHelper.transactionExcute( sb.toString());
        }
    }


    public void delete(String _idx) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        String sql = "DELETE FROM "+TB_DATA_PPG+" WHERE "+PPG_IDX+"='" + _idx + "';";
        Logger.i(TAG, "delete.sql="+sql);
        db.execSQL(sql);
        Logger.i(TAG, sql);
        db.close();
    }

    public void getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = mHelper.getReadableDatabase();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT * FROM "+TB_DATA_PPG+" ORDER BY  datetime("+ PPG_REGDATE +") desc, cast("+PPG_IDX +" as BIGINT) asc";
        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        int i = 0;
        while (cursor.moveToNext()) {
            String idx = cursor.getString(cursor.getColumnIndex(PPG_IDX));
            String hrm = cursor.getString(cursor.getColumnIndex(PPG_HRM));
            String regDt = cursor.getString(cursor.getColumnIndex(PPG_REGDATE));

            Logger.i(TAG, "PPG["+(i++)+"] idx["+idx+"], hrm="+hrm+", regDt=" +regDt);
        }

    }

    public void getResult(String startTime, String endTime, TypeDataSet.Period period) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = mHelper.getReadableDatabase();
        StringBuffer sb = new StringBuffer();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT * FROM "+TB_DATA_PPG+" "
                +" ORDER BY  datetime("+ PPG_REGDATE +") desc, cast("+PPG_IDX +" as BIGINT) asc";
        Logger.i(TAG, "getResult.sql="+sql);

        Cursor cursor = db.rawQuery(sql, null);
        int i = 0;
        Logger.i(TAG, "getResult.size="+cursor.getCount());
        while (cursor.moveToNext()) {
            String idx = cursor.getString(cursor.getColumnIndex(PPG_IDX));
            String hrm = cursor.getString(cursor.getColumnIndex(PPG_HRM));
            String regDate = cursor.getString(cursor.getColumnIndex(PPG_REGDATE));

            Logger.i(TAG, "step["+i+"] idx["+idx+"], regDate["+regDate+"], hrm="+hrm);
        }

    }
    /*
    // 금일 데이터만 걸음수 SUM하여 가져온다.
     */
    public String getResultMain(SQLiteOpenHelper helper) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = helper.getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String nowDate = CDateUtil.getToday_yyyyMMdd();
        String sql = "SELECT SUM("+PPG_HRM+") as "+PPG_HRM
                +" FROM "+ TB_DATA_PPG
                +" WHERE "+ PPG_REGDATE +" BETWEEN '"+ nowDate +" 00:00:00' and '"+nowDate+" 23:59:59' ";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "result walk count="+cursor.getCount());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                int hrm = cursor.getInt(cursor.getColumnIndex(PPG_HRM));

                Logger.i(TAG, "심박수 hrm=" + hrm);
                result = String.format("%,d", hrm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 일단위
    public List<BarEntry> getResultDay(String nDate, boolean isStep) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<BarEntry> yVals1 = new ArrayList<>();

        String sql = "Select "
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 1 THEN "+PPG_HRM+" End),0) as H1,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 2 THEN "+PPG_HRM+" End),0) as H2,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 3 THEN "+PPG_HRM+" End),0) as H3,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 4 THEN "+PPG_HRM+" End),0) as H4,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 5 THEN "+PPG_HRM+" End),0) as H5,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 6 THEN "+PPG_HRM+" End),0) as H6,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 7 THEN "+PPG_HRM+" End),0) as H7,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 8 THEN "+PPG_HRM+" End),0) as H8,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 9 THEN "+PPG_HRM+" End),0) as H9,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 10 THEN "+PPG_HRM+" End),0) as H10,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 11 THEN "+PPG_HRM+" End),0) as H11,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 12 THEN "+PPG_HRM+" End),0) as H12,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 13 THEN "+PPG_HRM+" End),0) as H13,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 14 THEN "+PPG_HRM+" End),0) as H14,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 15 THEN "+PPG_HRM+" End),0) as H15,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 16 THEN "+PPG_HRM+" End),0) as H16,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 17 THEN "+PPG_HRM+" End),0) as H17,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 18 THEN "+PPG_HRM+" End),0) as H18,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 19 THEN "+PPG_HRM+" End),0) as H19,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 20 THEN "+PPG_HRM+" End),0) as H20,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 21 THEN "+PPG_HRM+" End),0) as H21,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 22 THEN "+PPG_HRM+" End),0) as H22,"
                +" ifnull(SUM(CASE cast(strftime('%H', "+PPG_REGDATE+") as integer) WHEN 23 THEN "+PPG_HRM+" End),0) as H23,"
                +" ifnull(SUM(CASE WHEN cast(strftime('%H', "+PPG_REGDATE+") as integer) in (24,0) THEN "+PPG_HRM+" End),0) as H24 "
                +" FROM "+ TB_DATA_PPG
                +" WHERE "+ PPG_REGDATE +" BETWEEN '"+ nDate +" 00:00:00' and '"+nDate+" 23:59:59' "
                +" Group by strftime('%Y',"+PPG_REGDATE+"), strftime('%m',"+PPG_REGDATE+"), strftime('%d',"+PPG_REGDATE+")" ;

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "count ="+cursor.getCount());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
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
                int h24 = cursor.getInt(cursor.getColumnIndex("H24"));

                Logger.i(TAG, "결과 h1:"+h1+", h2:"+h2+", h3:" +h3+", h4:" +h4+", h5:" +h5+", h6:" +h6+", h7:" +h7);
                Logger.i(TAG, "h8:" +h8+", h9:" +h9+", h10:" +h10+", h11:" +h11+", h12:" +h12+", h13:" +h13+", h14:" +h14+", h15:" +h15);
                Logger.i(TAG, "h16:"+h16+", h17:"+h17+", h18:"+h18+", h19:"+h19+", h20:"+h20+", h21:"+h21+", h22:"+h22+", h23:"+h23+", h24:"+h24);

                for (int i = 1 ; i <= cursor.getColumnCount(); i++) {
                    float stepVal = cursor.getInt(cursor.getColumnIndex("H"+i));
                    int idx = i;
                    BarEntry entry = new BarEntry(idx, stepVal, (int)stepVal);
                    Logger.i(TAG, "BarEntry["+i+"]="+stepVal+", entry="+entry);
                    yVals1.add(entry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            for (int i = 1 ; i <= cursor.getColumnCount(); i++) {
                BarEntry entry = new BarEntry(i,0);
                yVals1.add(entry);
            }
        }

        return yVals1;
    }

    /**
     * DB에 데이터가 있는지 여부 확인
     * @param models
     */
    public List<BandModel> getResultRegistData(List<BandModel> models) {
        String sql = "SELECT "+PPG_IDX;
        sql += " FROM "+ TB_DATA_PPG;
        sql += " WHERE ";
//        for (BandModel model : models) {
        for (int i =0; i <= models.size()-1; i++) {
            BandModel model = models.get(i);
            sql += PPG_IDX +"='"+ model.getIdx()+"' ";
            if (i < models.size()-1) {
                sql += " OR ";
            }
        }
        Logger.i(TAG, sql);

        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        int i = 0;
        List<String> idxArr = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String idx = cursor.getString(cursor.getColumnIndex(PPG_IDX));
                idxArr.add(idx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        List<BandModel> newModels = new ArrayList<>();
        newModels.addAll(models);
        for (i = 0; i <= idxArr.size()-1; i++) {
            for (BandModel model : models) {
                String idx = idxArr.get(i);
                if (model.getIdx().equals(idx)) {
                    newModels.remove(model);
                    Logger.i(TAG, "alReady registered idx["+idx+"]");
                }
            }
        }

        return newModels;
    }

    /*
    // 주단위
     */
    public List<BarEntry> getResultWeek(String sDate, String eDate, boolean isStep) {

        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<BarEntry> yVals1 = new ArrayList<>();
        String sql = "Select "
                +" ifnull(SUM(CASE cast(strftime('%w', "+PPG_REGDATE+") as integer) WHEN 0 THEN "+PPG_HRM+" End),0) as W1a,"    //일요일부터 시작
                +" ifnull(SUM(CASE cast(strftime('%w', "+PPG_REGDATE+") as integer) WHEN 1 THEN "+PPG_HRM+" End),0) as W2a,"
                +" ifnull(SUM(CASE cast(strftime('%w', "+PPG_REGDATE+") as integer) WHEN 2 THEN "+PPG_HRM+" End),0) as W3a,"
                +" ifnull(SUM(CASE cast(strftime('%w', "+PPG_REGDATE+") as integer) WHEN 3 THEN "+PPG_HRM+" End),0) as W4a,"
                +" ifnull(SUM(CASE cast(strftime('%w', "+PPG_REGDATE+") as integer) WHEN 4 THEN "+PPG_HRM+" End),0) as W5a,"
                +" ifnull(SUM(CASE cast(strftime('%w', "+PPG_REGDATE+") as integer) WHEN 5 THEN "+PPG_HRM+" End),0) as W6a,"
                +" ifnull(SUM(CASE cast(strftime('%w', "+PPG_REGDATE+") as integer) WHEN 6 THEN "+PPG_HRM+" End),0) as W7a "
                +" FROM " + TB_DATA_PPG
                +" WHERE " + PPG_REGDATE +" BETWEEN '"+ sDate +" 00:00:00' and '"+eDate+" 23:59:59' "
                +" Group by strftime('%w', "+PPG_REGDATE+"); ";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "count ="+cursor.getCount());
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

                Logger.i(TAG, "결과 w1:"+w1+", w2:"+w2+", w3:" +w3+", w4:" +w4+", w5:" +w5+", w6:" +w6+", w7:" +w7);

                for (int i = 1 ; i <= cursor.getColumnCount(); i++) {
                    float stepVal = cursor.getInt(cursor.getColumnIndex("W"+i+"a"));
                    int idx = i-1;
                    BarEntry entry = new BarEntry(idx,stepVal);
                    yVals1.add(entry);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.close();
            }
        }else{
            for (int i = 1 ; i <= cursor.getColumnCount(); i++) {
                BarEntry entry = new BarEntry(i-1,0);
                yVals1.add(entry);
            }
        }
        return yVals1;
    }

    /*
    // 월단위
     */
    public List<BarEntry> getResultMonth(String nYear, String nMonth, boolean isStep) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<BarEntry> yVals1 = new ArrayList<>();

        String sql = "Select "
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 1 THEN "+PPG_HRM+" End), 0) as D1,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 2 THEN "+PPG_HRM+" End), 0) as D2,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 3 THEN "+PPG_HRM+" End), 0) as D3,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 4 THEN "+PPG_HRM+" End), 0) as D4,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 5 THEN "+PPG_HRM+" End), 0) as D5,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 6 THEN "+PPG_HRM+" End), 0) as D6,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 7 THEN "+PPG_HRM+" End), 0) as D7,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 8 THEN "+PPG_HRM+" End), 0) as D8,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 9 THEN "+PPG_HRM+" End), 0) as D9,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 10 THEN "+PPG_HRM+" End), 0) as D10,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 11 THEN "+PPG_HRM+" End), 0) as D11,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 12 THEN "+PPG_HRM+" End), 0) as D12,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 13 THEN "+PPG_HRM+" End), 0) as D13,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 14 THEN "+PPG_HRM+" End), 0) as D14,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 15 THEN "+PPG_HRM+" End), 0) as D15,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 16 THEN "+PPG_HRM+" End), 0) as D16,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 17 THEN "+PPG_HRM+" End), 0) as D17,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 18 THEN "+PPG_HRM+" End), 0) as D18,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 19 THEN "+PPG_HRM+" End), 0) as D19,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 20 THEN "+PPG_HRM+" End), 0) as D20,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 21 THEN "+PPG_HRM+" End), 0) as D21,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 22 THEN "+PPG_HRM+" End), 0) as D22,"
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 23 THEN "+PPG_HRM+" End), 0) as D23, "
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 24 THEN "+PPG_HRM+" End), 0) as D24, "
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 25 THEN "+PPG_HRM+" End), 0) as D25, "
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 26 THEN "+PPG_HRM+" End), 0) as D26, "
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 27 THEN "+PPG_HRM+" End), 0) as D27, "
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 28 THEN "+PPG_HRM+" End), 0) as D28, "
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 29 THEN "+PPG_HRM+" End), 0) as D29, "
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 30 THEN "+PPG_HRM+" End), 0) as D30, "
                +" ifnull(SUM(CASE cast(strftime('%d', "+PPG_REGDATE+") as integer) WHEN 31 THEN "+PPG_HRM+" End), 0) as D31  "
                +" FROM "+ TB_DATA_PPG
                +" WHERE cast(strftime('%Y',"+PPG_REGDATE+") as integer)="+nYear+" and cast(strftime('%m',"+PPG_REGDATE+") as integer)="+nMonth
                +" Group by strftime('%Y',"+PPG_REGDATE+"), strftime('%m',"+PPG_REGDATE+") " ;

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "count ="+cursor.getCount());
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

                Logger.i(TAG, "결과 d1:"+d1+", d2:"+d2+", d3:" +d3+", d4:" +d4+", d5:" +d5+", d6:" +d6+", d7:" +d7);
                Logger.i(TAG, "d8:" +d8+", d9:" +d9+", d10:" +d10+", d11:" +d11+", d12:" +d12+", d13:" +d13+", d14:" +d14+", d15:" +d15);
                Logger.i(TAG, "d16:"+d16+", d17:"+d17+", d18:"+d18+", d19:"+d19+", d20:"+d20+", d21:"+d21+", d22:"+d22+", d23:"+d23);
                Logger.i(TAG, "d24:"+d24+", d25:"+d25+", d26:"+d26+", d27:"+d27+", d28:"+d28+", d29:"+d29+", d30:"+d30+", d31:"+d31);

                for (int i = 1 ; i < cursor.getColumnCount(); i++) {
                    float stepVal = cursor.getInt(cursor.getColumnIndex("D"+i));
                    int idx = i-1;
                    BarEntry entry = new BarEntry(idx,stepVal);
                    yVals1.add(entry);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.close();
            }
        }else{
            for (int i = 1 ; i <= cursor.getColumnCount(); i++) {
                BarEntry entry = new BarEntry(i-1,0);
                yVals1.add(entry);
            }
        }
        return yVals1;
    }

    /*
    // 메인 통계
     */
    public PPGValue getResultPPG(String sDate, String eDate) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "Select "
                +" ifnull(AVG("+ PPG_HRM +"), 0) as "+PPG_HRM  // 총칼로리 또는 총걸음수
                +" FROM "+ TB_DATA_PPG
                +" WHERE "+ PPG_REGDATE +" BETWEEN '"+ sDate +" 00:00:00' and '"+eDate+" 23:59:59' and cast("+PPG_HRM+" as integer) > 0 ";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "count ="+cursor.getCount());

        PPGValue rtnValue = new PPGValue();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                rtnValue.hrm            =  ""+cursor.getInt(cursor.getColumnIndex(PPG_HRM));

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return rtnValue;
    }

    /*
    // 최근 심박수
     */
    public PPGValue getResultFastPPG(String sDate, String eDate) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "Select  "+ PPG_HRM +" "
                +" FROM "+ TB_DATA_PPG
                +" ORDER BY datetime("+PPG_REGDATE +") desc,  cast("+PPG_IDX +" as BIGINT) desc LIMIT 1";
//                +" WHERE "+ PPG_REGDATE +" BETWEEN '"+ sDate +" 00:00:00' and '"+eDate+" 23:59:59' and cast("+PPG_HRM+" as integer) > 0 ";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "count ="+cursor.getCount());

        PPGValue rtnValue = new PPGValue();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                rtnValue.hrm            =  ""+cursor.getInt(cursor.getColumnIndex(PPG_HRM));

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }else{
            rtnValue.hrm = "0";
        }
        return rtnValue;
    }

    public static class PPGValue {
        public String idx;
        public String hrm;      // 심박수
        public String regtype;
        public String regdate;

    }
}