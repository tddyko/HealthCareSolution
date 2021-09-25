package com.greencross.greencare.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.greencross.greencare.bluetooth.model.PressureModel;
import com.greencross.greencare.charting.data.PressureEntry;
import com.greencross.greencare.network.tr.data.Tr_bdsg_dose_medicine_input;
import com.greencross.greencare.network.tr.data.Tr_get_hedctdata;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrsohn on 2017. 3. 20..
 */

public class DBHelperPresure {
    private final String TAG = DBHelperPresure.class.getSimpleName();

    private DBHelper mHelper;
    public DBHelperPresure(DBHelper helper) {
        mHelper = helper;
    }
    /**
     * 혈압데이터
     */
    public static String TB_DATA_PRESSURE = "tb_data_pressure";         // 혈압데이터
    private String PRESURE_IDX = "idx";                                 // 고유번호intM서버데이터 삭제를 위한 키값
    private String PRESURE_ARTERIALPRESSURE = "arterialPressure";       // 동맥압력Str7M
    private String PRESURE_DIASTOLICPRESSUR = "diastolicPressure";      // 이완기혈압Str7M(저혈압값)
    private String PRESURE_PULSERATE = "pulseRate";                     // 맥박속도Str7
    private String PRESURE_SYSTOLICPRESSURE = "systolicPressure";       // 수축기압력Str7(고혈압값)
    private String PRESURE_DRUGNAME = "drugname";                       // 약이름
    private String PRESURE_REGTYPE = "regtype";                         // 등록타입Str1M D:디바이스,U:직접등록, R:투약
    private String PRESURE_REGDATE = "regdate";                         // 등록일시 MyyyyMMddHHmm
    private String IS_SERVER_REGIST = "is_server_regist";                // 서버 등록 여부

    // DB를 새로 생성할 때 호출되는 함수
    public String createDb() {
        // 새로운 테이블 생성
        String sql = "CREATE TABLE if not exists "+TB_DATA_PRESSURE+" ("
                            + PRESURE_IDX+" CHARACTER(14) PRIMARY KEY, "
                            + PRESURE_ARTERIALPRESSURE+" VARCHAR(15) NULL, "
                            + PRESURE_DIASTOLICPRESSUR+" VARCHAR(15) NULL, "
                            + PRESURE_PULSERATE+" VARCHAR(15) NULL, "
                            + PRESURE_SYSTOLICPRESSURE+" VARCHAR(15) NULL, "
                            + PRESURE_DRUGNAME+" VARCHAR(30) NULL, "
                            + PRESURE_REGTYPE+" CHARACTER(1) NULL, "
                            + IS_SERVER_REGIST+" BOOLEAN, "
                            + PRESURE_REGDATE+" DATETIME DEFAULT CURRENT_TIMESTAMP); ";
        Logger.i(TAG, "onCreate.sql="+sql);
        return sql;
    }

    // 히스토리에서 선택된 DB로우를 삭제하는 함수
    public void DeleteDb(String idx){
        String sql = "DELETE FROM " +TB_DATA_PRESSURE + " WHERE idx == '"+idx+"' ";
        Logger.i(TAG, "onDelete.sql = "+sql);
        mHelper.transactionExcute(sql);

    }

    // 특정 로우 업데이트 하는 함수
    public void UpdateDb(String idx, String systoli, String diatoli, String medicen, String reg_de){
        String sql = "UPDATE " + TB_DATA_PRESSURE +
                " SET " + PRESURE_SYSTOLICPRESSURE + " = '" + systoli + "', "
                + PRESURE_DIASTOLICPRESSUR + " = '" + diatoli +"' , "
                + PRESURE_DRUGNAME + " = '" + medicen + "' , "
                + PRESURE_REGDATE + "='" + CDateUtil.getRegDateFormat_yyyyMMddHHss(reg_de)+"'"
                +" WHERE idx == " +idx;

        Logger.i(TAG, "onUpdate.sql = "+sql);
        mHelper.transactionExcute(sql);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    public String upgradeDb() {
        return "DROP TABLE "+TB_DATA_PRESSURE+";";
    }

    public void insert(SQLiteOpenHelper helper, PressureModel pressureModel,  boolean isServerReg) {
        // 읽고 쓰기가 가능하게 DB 열기
        // DB에 입력한 값으로 행 추가
        String sql = "INSERT INTO "+TB_DATA_PRESSURE
                                +" VALUES('"+pressureModel.getIdx()+"', '"
                                        + (int)pressureModel.getArterialPressure() + "', '"
                                        + (int)pressureModel.getDiastolicPressure() + "', '"
                                        + (int)pressureModel.getPulseRate() + "', '"
                                        + (int)pressureModel.getSystolicPressure() + "', '"
                                        + pressureModel.getDrugname() + "', '"
                                        + pressureModel.getRegtype()  + "', '"
                                        + isServerReg + "', '"
                                        + CDateUtil.getRegDateFormat_yyyyMMddHHss(pressureModel.getRegdate()) + "');";
        Logger.i(TAG, "insert.sql="+sql);
        mHelper.transactionExcute(sql);

    }

    public void insert(SQLiteOpenHelper helper, Tr_get_hedctdata datas, boolean isServerReg) {

        String sql = "INSERT INTO "+TB_DATA_PRESSURE
                +" VALUES";
        for (Tr_get_hedctdata.DataList data : datas.data_list) {
            StringBuffer sb = new StringBuffer();
            sb.append(sql);

            String values = "('"+ data.idx + "', '" // 1",
                + data.arterialPressure + "', '" // 100",
                + data.diastolicPressure + "', '" // 50",
                + data.pulseRate + "', '" // 0",
                + data.systolicPressure + "', '" // 30",
                + data.drugname + "', '" // 혈압약",
                + data.regtype + "', '" // D",
                + isServerReg+ "', '" // D",
                + CDateUtil.getRegDateFormat_yyyyMMddHHss(data.reg_de) + " ') "; // 201703301420"

            sb.append(values);

            Logger.i(TAG, "insert.sql="+sb.toString());
            mHelper.transactionExcute(sb.toString());

        }

    }

    public void insert(SQLiteOpenHelper helper, Tr_bdsg_dose_medicine_input.RequestData requestData, boolean isServerReg) {
        // 읽고 쓰기가 가능하게 DB 열기
        String sql = "INSERT INTO "+TB_DATA_PRESSURE
                +" VALUES("+requestData.idx+", '"
                + "" + "', '"
                + "" + "', '"
                + "" + "', '"
                + "" + "', '"
                + requestData.medicine_nm + "', '"
                + isServerReg + "', '"
                + isServerReg + "', '"
                + requestData.medicine_typ + "');";
        Logger.i(TAG, "insert.sql="+sql);
        mHelper.transactionExcute(sql);

    }

    /**
     * @param helper
     * @return
     *
     * 메인에서 혈압값을 사용하기 위한 함수
     * 메인에서는 혈압 하나의 값만 사용
     * 혈압은 최고값, 최소값만을 사용한다.
     *
     */
    public PressureData getResultMain(SQLiteOpenHelper helper) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = helper.getReadableDatabase();
        PressureData resultData = new PressureData();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT "+PRESURE_DIASTOLICPRESSUR+" as diastolicPressure,  "+PRESURE_SYSTOLICPRESSURE+" as systolicPressure"
                    + " FROM "+ TB_DATA_PRESSURE
                    + " WHERE "+ PRESURE_DRUGNAME + " == '' and "+ PRESURE_DIASTOLICPRESSUR +" > '0' and "+ PRESURE_SYSTOLICPRESSURE +" > 0 "
                    +" Order by  datetime("+ PRESURE_REGDATE +") desc, cast("+ PRESURE_IDX +" as BIGINT) DESC LIMIT 1";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        Logger.i(TAG, "main pressure count="+cursor.getCount());

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                try {
                    int diastolicPressure = cursor.getInt(cursor.getColumnIndex(PRESURE_DIASTOLICPRESSUR));
                    int systolicPressure = cursor.getInt(cursor.getColumnIndex(PRESURE_SYSTOLICPRESSURE));
                    Logger.i(TAG, "메일혈압:  diastolicPressure=" + diastolicPressure + "  systolicPressure=" + systolicPressure);
                    resultData.setDiastolc(diastolicPressure);
                    resultData.setSystolic(systolicPressure);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        }else{
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultData;
    }


    /*
      // 투약시간
    */
    public List<PressureEntry> getResultMedicine(SQLiteOpenHelper helper, String nDate) {

        SQLiteDatabase db = helper.getReadableDatabase();
        List<PressureEntry> yVals1 = new ArrayList<>();

        String sql = "Select "
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 1 THEN 1  End),0) as H1,"     //1시
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 2 THEN 1  End),0) as H2,"     //2시
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 3 THEN 1  End),0) as H3,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 4 THEN 1  End),0) as H4,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 5 THEN 1  End),0) as H5,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 6 THEN 1  End),0) as H6,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 7 THEN 1  End),0) as H7,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 8 THEN 1  End),0) as H8,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 9 THEN 1  End),0) as H9,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 10 THEN 1 End),0) as H10,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 11 THEN 1 End),0) as H11,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 12 THEN 1 End),0) as H12,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 13 THEN 1 End),0) as H13,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 14 THEN 1 End),0) as H14,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 15 THEN 1 End),0) as H15,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 16 THEN 1 End),0) as H16,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 17 THEN 1 End),0) as H17,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 18 THEN 1 End),0) as H18,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 19 THEN 1 End),0) as H19,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 20 THEN 1 End),0) as H20,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 21 THEN 1 End),0) as H21,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 22 THEN 1 End),0) as H22,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 23 THEN 1 End),0) as H23,"
                +" ifnull(COUNT(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 24 THEN 1 End),0) as H24 "
                +" FROM "+ TB_DATA_PRESSURE
                +" WHERE "+ PRESURE_REGTYPE +" in ('R') and "+ PRESURE_REGDATE +" BETWEEN '"+ nDate +" 00:00' and '"+nDate+" 23:59' "
                +" Group by strftime('%d',"+PRESURE_REGDATE+") ";


        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count="+cursor.getCount());
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

                    Logger.i(TAG, "혈압메인 (일단위) h1:"+h1+", h2:"+h2+", h3:" +h3+", h4:" +h4+", h5:" +h5+", h6:" +h6+", h7:" +h7);
                    Logger.i(TAG, "h8:" +h8+", h9:" +h9+", h10:" +h10+", h11:" +h11+", h12:" +h12+", h13:" +h13+", h14:" +h14+", h15:" +h15);
                    Logger.i(TAG, "h16:"+h16+", h17:"+h17+", h18:"+h18+", h19:"+h19+", h20:"+h20+", h21:"+h21+", h22:"+h22+", h23:"+h23+", h24:"+h24);

                    Logger.i(TAG, "cursor.getColumnCount()="+cursor.getColumnCount());

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        } else {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return yVals1;
    }


    /*
    // 일단위
     */
    public List<PressureEntry> getResultDay(SQLiteOpenHelper helper, String nDate) {

        SQLiteDatabase db = helper.getReadableDatabase();
        List<PressureEntry> yVals1 = new ArrayList<>();

        String sql = "Select "
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 0 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H0N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 0 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H0X, "
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 1 THEN "+PRESURE_DIASTOLICPRESSUR+"  End),0) as H1N,"     //1시 MIN
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 1 THEN "+PRESURE_SYSTOLICPRESSURE+"  End),0) as H1X,"     //1시 MAX
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 2 THEN "+PRESURE_DIASTOLICPRESSUR+"  End),0) as H2N,"     //2시 MIN
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 2 THEN "+PRESURE_SYSTOLICPRESSURE+"  End),0) as H2X,"     //2시 MAX
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 3 THEN "+PRESURE_DIASTOLICPRESSUR+"  End),0) as H3N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 3 THEN "+PRESURE_SYSTOLICPRESSURE+"  End),0) as H3X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 4 THEN "+PRESURE_DIASTOLICPRESSUR+"  End),0) as H4N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 4 THEN "+PRESURE_SYSTOLICPRESSURE+"  End),0) as H4X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 5 THEN "+PRESURE_DIASTOLICPRESSUR+"  End),0) as H5N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 5 THEN "+PRESURE_SYSTOLICPRESSURE+"  End),0) as H5X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 6 THEN "+PRESURE_DIASTOLICPRESSUR+"  End),0) as H6N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 6 THEN "+PRESURE_SYSTOLICPRESSURE+"  End),0) as H6X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 7 THEN "+PRESURE_DIASTOLICPRESSUR+"  End),0) as H7N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 7 THEN "+PRESURE_SYSTOLICPRESSURE+"  End),0) as H7X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 8 THEN "+PRESURE_DIASTOLICPRESSUR+"  End),0) as H8N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 8 THEN "+PRESURE_SYSTOLICPRESSURE+"  End),0) as H8X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 9 THEN "+PRESURE_DIASTOLICPRESSUR+"  End),0) as H9N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 9 THEN "+PRESURE_SYSTOLICPRESSURE+"  End),0) as H9X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 10 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H10N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 10 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H10X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 11 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H11N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 11 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H11X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 12 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H12N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 12 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H12X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 13 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H13N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 13 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H13X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 14 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H14N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 14 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H14X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 15 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H15N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 15 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H15X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 16 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H16N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 16 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H16X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 17 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H17N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 17 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H17X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 18 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H18N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 18 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H18X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 19 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H19N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 19 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H19X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 20 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H20N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 20 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H20X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 21 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H21N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 21 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H21X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 22 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H22N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 22 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H22X,"
                +" ifnull(MIN(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 23 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as H23N,"
                +" ifnull(MAX(CASE cast(strftime('%H', "+PRESURE_REGDATE+") as integer) WHEN 23 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as H23X  "
                +" FROM "+ TB_DATA_PRESSURE
                +" WHERE "+ PRESURE_REGTYPE +" in ('D', 'U') and "+ PRESURE_REGDATE +" BETWEEN '"+ nDate +" 00:00' and '"+nDate+" 23:59' and "+PRESURE_DIASTOLICPRESSUR+" > '0' and "+PRESURE_SYSTOLICPRESSURE+ " > '0'"
                +" Group by strftime('%d',"+PRESURE_REGDATE+") " ;


        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count="+cursor.getCount());
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                try {
                    int h0n = cursor.getInt(cursor.getColumnIndex("H0N"));
                    int h1n = cursor.getInt(cursor.getColumnIndex("H1N"));    // MIN
                    int h2n = cursor.getInt(cursor.getColumnIndex("H2N"));
                    int h3n = cursor.getInt(cursor.getColumnIndex("H3N"));
                    int h4n = cursor.getInt(cursor.getColumnIndex("H4N"));
                    int h5n = cursor.getInt(cursor.getColumnIndex("H5N"));
                    int h6n = cursor.getInt(cursor.getColumnIndex("H6N"));
                    int h7n = cursor.getInt(cursor.getColumnIndex("H7N"));
                    int h8n = cursor.getInt(cursor.getColumnIndex("H8N"));
                    int h9n = cursor.getInt(cursor.getColumnIndex("H9N"));
                    int h10n = cursor.getInt(cursor.getColumnIndex("H10N"));
                    int h11n = cursor.getInt(cursor.getColumnIndex("H11N"));
                    int h12n = cursor.getInt(cursor.getColumnIndex("H12N"));
                    int h13n = cursor.getInt(cursor.getColumnIndex("H13N"));
                    int h14n = cursor.getInt(cursor.getColumnIndex("H14N"));
                    int h15n = cursor.getInt(cursor.getColumnIndex("H15N"));
                    int h16n = cursor.getInt(cursor.getColumnIndex("H16N"));
                    int h17n = cursor.getInt(cursor.getColumnIndex("H17N"));
                    int h18n = cursor.getInt(cursor.getColumnIndex("H18N"));
                    int h19n = cursor.getInt(cursor.getColumnIndex("H19N"));
                    int h20n = cursor.getInt(cursor.getColumnIndex("H20N"));
                    int h21n = cursor.getInt(cursor.getColumnIndex("H21N"));
                    int h22n = cursor.getInt(cursor.getColumnIndex("H22N"));
                    int h23n = cursor.getInt(cursor.getColumnIndex("H23N"));

                    Logger.i(TAG, "혈압메인 (일단위) h0n:"+h0n+", h1n:"+h1n+", h2n:"+h2n+", h3n:" +h3n+", h4n:" +h4n+", h5n:" +h5n+", h6n:" +h6n+", h7n:" +h7n);
                    Logger.i(TAG, "h8n:" +h8n+", h9n:" +h9n+", h10n:" +h10n+", h11n:" +h11n+", h12n:" +h12n+", h13n:" +h13n+", h14n:" +h14n+", h15n:" +h15n);
                    Logger.i(TAG, "h16n:"+h16n+", h17n:"+h17n+", h18n:"+h18n+", h19n:"+h19n+", h20n:"+h20n+", h21n:"+h21n+", h22n:"+h22n+", h23n:"+h23n);


                    int h0x = cursor.getInt(cursor.getColumnIndex("H0X"));
                    int h1x = cursor.getInt(cursor.getColumnIndex("H1X"));    // MIN/MAX (***포멧 주의바람)
                    int h2x = cursor.getInt(cursor.getColumnIndex("H2X"));
                    int h3x = cursor.getInt(cursor.getColumnIndex("H3X"));
                    int h4x = cursor.getInt(cursor.getColumnIndex("H4X"));
                    int h5x = cursor.getInt(cursor.getColumnIndex("H5X"));
                    int h6x = cursor.getInt(cursor.getColumnIndex("H6X"));
                    int h7x = cursor.getInt(cursor.getColumnIndex("H7X"));
                    int h8x = cursor.getInt(cursor.getColumnIndex("H8X"));
                    int h9x = cursor.getInt(cursor.getColumnIndex("H9X"));
                    int h10x = cursor.getInt(cursor.getColumnIndex("H10X"));
                    int h11x = cursor.getInt(cursor.getColumnIndex("H11X"));
                    int h12x = cursor.getInt(cursor.getColumnIndex("H12X"));
                    int h13x = cursor.getInt(cursor.getColumnIndex("H13X"));
                    int h14x = cursor.getInt(cursor.getColumnIndex("H14X"));
                    int h15x = cursor.getInt(cursor.getColumnIndex("H15X"));
                    int h16x = cursor.getInt(cursor.getColumnIndex("H16X"));
                    int h17x = cursor.getInt(cursor.getColumnIndex("H17X"));
                    int h18x = cursor.getInt(cursor.getColumnIndex("H18X"));
                    int h19x = cursor.getInt(cursor.getColumnIndex("H19X"));
                    int h20x = cursor.getInt(cursor.getColumnIndex("H20X"));
                    int h21x = cursor.getInt(cursor.getColumnIndex("H21X"));
                    int h22x = cursor.getInt(cursor.getColumnIndex("H22X"));
                    int h23x = cursor.getInt(cursor.getColumnIndex("H23X"));

                    Logger.i(TAG, "혈압메인 (일단위) h0x:"+h0x+", h1x:"+h1x+", h2x:"+h2x+", h3x:" +h3x+", h4x:" +h4x+", h5x:" +h5x+", h6x:" +h6x+", h7x:" +h7x);
                    Logger.i(TAG, "h8x:" +h8x+", h9x:" +h9x+", h10x:" +h10x+", h11x:" +h11x+", h12x:" +h12x+", h13x:" +h13x+", h14x:" +h14x+", h15x:" +h15x);
                    Logger.i(TAG, "h16x:"+h16x+", h17x:"+h17x+", h18x:"+h18x+", h19x:"+h19x+", h20x:"+h20x+", h21x:"+h21x+", h22x:"+h22x+", h23x:"+h23x);

                    Logger.i(TAG, "cursor.getColumnCount()="+cursor.getColumnCount());
                    for (int i = 0 ; i <= (cursor.getColumnCount() / 2)-1; i++) {
                        int valN = cursor.getInt(cursor.getColumnIndex("H"+i+"N"));
                        int valX = cursor.getInt(cursor.getColumnIndex("H"+i+"X"));

                        float mi = 0.0f;
                        if (i==0){
                            mi = 0.5f;
                        }

                        PressureEntry entry = new PressureEntry(i-mi, valN, 0, valX, 0);
                        yVals1.add(entry);
                        Logger.i(TAG, "혈압메인 (일단위 valN["+i+"]="+valN+", valX="+valX);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        } else {
            for (int i = 0 ; i <= (cursor.getColumnCount() / 2)-1; i++) {
                PressureEntry entry = new PressureEntry(i-1, 0, 0, 0, 0);
                yVals1.add(entry);
            }
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return yVals1;
    }


    /*
    // 주단위
     */
    public List<PressureEntry> getResultWeek(SQLiteOpenHelper helper, String sDate, String eDate) {

        SQLiteDatabase db = helper.getReadableDatabase();
        List<PressureEntry> yVals1 = new ArrayList<>();
        String result = "";

        String sql = "Select "
                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 0 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D1M,"    //수축기 MAX
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 0 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D1N,"    //수축기 MIN
                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 1 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D2M,"
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 1 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D2N,"
                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 2 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D3M,"
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 2 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D3N,"
                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 3 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D4M,"
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 3 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D4N,"
                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 4 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D5M,"
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 4 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D5N,"
                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 5 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D6M,"
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 5 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D6N,"
                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 6 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D7M,"
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 6 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0) as D7N,"

                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 0 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S1M,"    //이완기 MAX
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 0 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S1N,"    //이완기 MIN
                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 1 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S2M,"
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 1 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S2N,"
                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 2 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S3M,"
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 2 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S3N,"
                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 3 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S4M,"
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 3 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S4N,"
                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 4 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S5M,"
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 4 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S5N,"
                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 5 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S6M,"
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 5 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S6N,"
                +" ifnull(MAX(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 6 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S7M,"
                +" ifnull(MIN(CASE cast(strftime('%w', "+PRESURE_REGDATE+") as integer) WHEN 6 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0) as S7N"
                +" FROM " + TB_DATA_PRESSURE
                +" WHERE "+ PRESURE_REGTYPE +" in ('D', 'U') and " + PRESURE_REGDATE +" BETWEEN '"+ sDate +" 00:00' and '"+eDate+" 23:59' and "+PRESURE_DIASTOLICPRESSUR+" > '0' and "+PRESURE_SYSTOLICPRESSURE+" > '0'";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "result pressure count="+cursor.getCount());
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                try {
                    int D1M = cursor.getInt(cursor.getColumnIndex("D1M"));    //일요일 MIN/MAX (***포멧 주의바람)
                    int D2M = cursor.getInt(cursor.getColumnIndex("D2M"));
                    int D3M = cursor.getInt(cursor.getColumnIndex("D3M"));
                    int D4M = cursor.getInt(cursor.getColumnIndex("D4M"));
                    int D5M = cursor.getInt(cursor.getColumnIndex("D5M"));
                    int D6M = cursor.getInt(cursor.getColumnIndex("D6M"));
                    int D7M = cursor.getInt(cursor.getColumnIndex("D7M"));

                    Logger.i(TAG, "수축기MAX (주단위) D1M:"+D1M+", D2M:"+D2M+", D3M:" +D3M+", D4M:" +D4M+", D5M:" +D5M+", D6M:" +D6M+", D7M:" +D7M);

                    int D1N = cursor.getInt(cursor.getColumnIndex("D1N"));    //일요일 MIN/MAX (***포멧 주의바람)
                    int D2N = cursor.getInt(cursor.getColumnIndex("D2N"));
                    int D3N = cursor.getInt(cursor.getColumnIndex("D3N"));
                    int D4N = cursor.getInt(cursor.getColumnIndex("D4N"));
                    int D5N = cursor.getInt(cursor.getColumnIndex("D5N"));
                    int D6N = cursor.getInt(cursor.getColumnIndex("D6N"));
                    int D7N = cursor.getInt(cursor.getColumnIndex("D7N"));

                    Logger.i(TAG, "수축기MIN (주단위) D1N:"+D1N+", D2N:"+D2N+", D3N:" +D3N+", D4N:" +D4N+", D5N:" +D5N+", D6N:" +D6N+", D7N:" +D7N);

                    int S1M = cursor.getInt(cursor.getColumnIndex("S1M"));    //일요일 MIN/MAX (***포멧 주의바람)
                    int S2M = cursor.getInt(cursor.getColumnIndex("S2M"));
                    int S3M = cursor.getInt(cursor.getColumnIndex("S3M"));
                    int S4M = cursor.getInt(cursor.getColumnIndex("S4M"));
                    int S5M = cursor.getInt(cursor.getColumnIndex("S5M"));
                    int S6M = cursor.getInt(cursor.getColumnIndex("S6M"));
                    int S7M = cursor.getInt(cursor.getColumnIndex("S7M"));

                    Logger.i(TAG, "이완기MAX (주단위) S1M:"+S1M+", S2M:"+S2M+", S3M:" +S3M+", S4M:" +S4M+", S5M:" +S5M+", S6M =:" +S6M +", S7M:" +S7M);

                    int S1N = cursor.getInt(cursor.getColumnIndex("S1N"));    //일요일 MIN/MAX (***포멧 주의바람)
                    int S2N = cursor.getInt(cursor.getColumnIndex("S2N"));
                    int S3N = cursor.getInt(cursor.getColumnIndex("S3N"));
                    int S4N = cursor.getInt(cursor.getColumnIndex("S4N"));
                    int S5N = cursor.getInt(cursor.getColumnIndex("S5N"));
                    int S6N = cursor.getInt(cursor.getColumnIndex("S6N"));
                    int S7N = cursor.getInt(cursor.getColumnIndex("S7N"));

                    Logger.i(TAG, "이완기MIN (주단위) S1N:"+S1N+", S2N:"+S2N+", S3N:" +S3N+", S4N:" +S4N+", S5N:" +S5N+", S6N:" +S6N+", S7N:" +S7N);

                    for (int i = 1 ; i <= cursor.getColumnCount()-1 / 2; i++) {
                        int DM = cursor.getInt(cursor.getColumnIndex("D"+i+"M"));
                        int DN = cursor.getInt(cursor.getColumnIndex("D"+i+"N"));
                        int SM = cursor.getInt(cursor.getColumnIndex("S"+i+"M"));
                        int SN = cursor.getInt(cursor.getColumnIndex("S"+i+"N"));

                        PressureEntry entry = new PressureEntry(i, DM, DN, SM, SN);
                        yVals1.add(entry);
                        Logger.i(TAG, "data["+i+"] DM="+DM+" DN="+DN +" SM="+SM+" SN="+SN);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        } else {
            for (int i = 1 ; i <= cursor.getColumnCount() / 2; i++) {
                PressureEntry entry = new PressureEntry(i, 0, 0, 0, 0);
                yVals1.add(entry);
            }
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return yVals1;
    }

    /*
    // 월단위
     */
    public List<PressureEntry> getResultMonth(SQLiteOpenHelper helper, String nYear, String nMonth, int dayCnt) {

        SQLiteDatabase db = helper.getReadableDatabase();
        List<PressureEntry> yVals1 = new ArrayList<>();

        String sql = "Select "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 1 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D1M,"  // 수축기 MAX
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 1 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D1N,"  // 수축기 MIN
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 2 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D2M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 2 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D2N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 3 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D3M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 3 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D3N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 4 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D4M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 4 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D4N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 5 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D5M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 5 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D5N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 6 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D6M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 6 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D6N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 7 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D7M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 7 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D7N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 8 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D8M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 8 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D8N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 9 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D9M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 9 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D9N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 10 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D10M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 10 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D10N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 11 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D11M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 11 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D11N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 12 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D12M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 12 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D12N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 13 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D13M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 13 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D13N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 14 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D14M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 14 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D14N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 15 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D15M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 15 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D15N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 16 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D16M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 16 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D16N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 17 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D17M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 17 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D17N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 18 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D18M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 18 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D18N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 19 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D19M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 19 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D19N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 20 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D20M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 20 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D20N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 21 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D21M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 21 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D21N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 22 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D22M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 22 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D22N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 23 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D23M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 23 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D23N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 24 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D24M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 24 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D24N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 25 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D25M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 25 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D25N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 26 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D26M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 26 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D26N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 27 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D27M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 27 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D27N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 28 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D28M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 28 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D28N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 29 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D29M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 29 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D29N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 30 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D30M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 30 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D30N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 31 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D31M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 31 THEN "+PRESURE_DIASTOLICPRESSUR+" End),0)as D31N, "

                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 1 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S1M,"  // 이완기 MAX
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 1 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S1N,"  // 이완기 MIN
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 2 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S2M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 2 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S2N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 3 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S3M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 3 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S3N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 4 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S4M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 4 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S4N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 5 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S5M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 5 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S5N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 6 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S6M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 6 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S6N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 7 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S7M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 7 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S7N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 8 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S8M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 8 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S8N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 9 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S9M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 9 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S9N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 10 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S10M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 10 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S10N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 11 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S11M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 11 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S11N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 12 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S12M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 12 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S12N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 13 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S13M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 13 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S13N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 14 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S14M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 14 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S14N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 15 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S15M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 15 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S15N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 16 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S16M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 16 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S16N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 17 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S17M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 17 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S17N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 18 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S18M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 18 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S18N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 19 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S19M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 19 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S19N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 20 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S20M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 20 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S20N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 21 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S21M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 21 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S21N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 22 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S22M,"
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 22 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S22N,"
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 23 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S23M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 23 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S23N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 24 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S24M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 24 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S24N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 25 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S25M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 25 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S25N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 26 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S26M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 26 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S26N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 27 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S27M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 27 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S27N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 28 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S28M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 28 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S28N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 29 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S29M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 29 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S29N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 30 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S30M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 30 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S30N, "
                +" ifnull(MAX(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 31 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S31M, "
                +" ifnull(MIN(CASE cast(strftime('%d', "+PRESURE_REGDATE+") as integer) WHEN 31 THEN "+PRESURE_SYSTOLICPRESSURE+" End),0)as S31N"
                +" FROM "+ TB_DATA_PRESSURE
                +" WHERE "+ PRESURE_REGTYPE +" in ('D', 'U') and cast(strftime('%Y',"+PRESURE_REGDATE+") as integer)="+nYear+" and cast(strftime('%m',"+PRESURE_REGDATE+") as integer)="+nMonth+" and "+PRESURE_DIASTOLICPRESSUR+" > '0' and "+PRESURE_SYSTOLICPRESSURE+" > '0'";


        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "result pressure count="+cursor.getCount());

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                try {
                    int D1M = cursor.getInt(cursor.getColumnIndex("D1M"));
                    int D2M = cursor.getInt(cursor.getColumnIndex("D2M"));
                    int D3M = cursor.getInt(cursor.getColumnIndex("D3M"));
                    int D4M = cursor.getInt(cursor.getColumnIndex("D4M"));
                    int D5M = cursor.getInt(cursor.getColumnIndex("D5M"));
                    int D6M = cursor.getInt(cursor.getColumnIndex("D6M"));
                    int D7M = cursor.getInt(cursor.getColumnIndex("D7M"));
                    int D8M = cursor.getInt(cursor.getColumnIndex("D8M"));
                    int D9M = cursor.getInt(cursor.getColumnIndex("D9M"));
                    int D10M = cursor.getInt(cursor.getColumnIndex("D10M"));
                    int D11M = cursor.getInt(cursor.getColumnIndex("D11M"));
                    int D12M = cursor.getInt(cursor.getColumnIndex("D12M"));
                    int D13M = cursor.getInt(cursor.getColumnIndex("D13M"));
                    int D14M = cursor.getInt(cursor.getColumnIndex("D14M"));
                    int D15M = cursor.getInt(cursor.getColumnIndex("D15M"));
                    int D16M = cursor.getInt(cursor.getColumnIndex("D16M"));
                    int D17M = cursor.getInt(cursor.getColumnIndex("D17M"));
                    int D18M = cursor.getInt(cursor.getColumnIndex("D18M"));
                    int D19M = cursor.getInt(cursor.getColumnIndex("D19M"));
                    int D20M = cursor.getInt(cursor.getColumnIndex("D20M"));
                    int D21M = cursor.getInt(cursor.getColumnIndex("D21M"));
                    int D22M = cursor.getInt(cursor.getColumnIndex("D22M"));
                    int D23M = cursor.getInt(cursor.getColumnIndex("D23M"));
                    int D24M = cursor.getInt(cursor.getColumnIndex("D24M"));
                    int D25M = cursor.getInt(cursor.getColumnIndex("D25M"));
                    int D26M = cursor.getInt(cursor.getColumnIndex("D26M"));
                    int D27M = cursor.getInt(cursor.getColumnIndex("D27M"));
                    int D28M = cursor.getInt(cursor.getColumnIndex("D28M"));
                    int D29M = cursor.getInt(cursor.getColumnIndex("D29M"));
                    int D30M = cursor.getInt(cursor.getColumnIndex("D30M"));
                    int D31M = cursor.getInt(cursor.getColumnIndex("D31M"));

                    Logger.i(TAG, "수축기MAX (월단위) D1M:" + D1M + ", D2M:" + D2M + ", D3M:" + D3M + ", D4M:" + D4M + ", D5M:" + D5M + ", D6M:" + D6M + ", D7M:" + D7M);
                    Logger.i(TAG, "D8M:" + D8M + ", D9M:" + D9M + ", D10M:" + D10M + ", D11M:" + D11M + ", D12M:" + D12M + ", D13M:" + D13M + ", D14M:" + D14M + ", D15M:" + D15M);
                    Logger.i(TAG, "D16M:" + D16M + ", D17M:" + D17M + ", D18M:" + D18M + ", D19M:" + D19M + ", D20M:" + D20M + ", D21M:" + D21M + ", D22M:" + D22M + ", D23M:" + D23M);
                    Logger.i(TAG, "D24M:" + D24M + ", D25M:" + D25M + ", D26M:" + D26M + ", D27M:" + D27M + ", D28M:" + D28M + ", D29M:" + D29M + ", D30M:" + D30M + ", D31M:" + D31M);


                    int D1N = cursor.getInt(cursor.getColumnIndex("D1N"));
                    int D2N = cursor.getInt(cursor.getColumnIndex("D2N"));
                    int D3N = cursor.getInt(cursor.getColumnIndex("D3N"));
                    int D4N = cursor.getInt(cursor.getColumnIndex("D4N"));
                    int D5N = cursor.getInt(cursor.getColumnIndex("D5N"));
                    int D6N = cursor.getInt(cursor.getColumnIndex("D6N"));
                    int D7N = cursor.getInt(cursor.getColumnIndex("D7N"));
                    int D8N = cursor.getInt(cursor.getColumnIndex("D8N"));
                    int D9N = cursor.getInt(cursor.getColumnIndex("D9N"));
                    int D10N = cursor.getInt(cursor.getColumnIndex("D10N"));
                    int D11N = cursor.getInt(cursor.getColumnIndex("D11N"));
                    int D12N = cursor.getInt(cursor.getColumnIndex("D12N"));
                    int D13N = cursor.getInt(cursor.getColumnIndex("D13N"));
                    int D14N = cursor.getInt(cursor.getColumnIndex("D14N"));
                    int D15N = cursor.getInt(cursor.getColumnIndex("D15N"));
                    int D16N = cursor.getInt(cursor.getColumnIndex("D16N"));
                    int D17N = cursor.getInt(cursor.getColumnIndex("D17N"));
                    int D18N = cursor.getInt(cursor.getColumnIndex("D18N"));
                    int D19N = cursor.getInt(cursor.getColumnIndex("D19N"));
                    int D20N = cursor.getInt(cursor.getColumnIndex("D20N"));
                    int D21N = cursor.getInt(cursor.getColumnIndex("D21N"));
                    int D22N = cursor.getInt(cursor.getColumnIndex("D22N"));
                    int D23N = cursor.getInt(cursor.getColumnIndex("D23N"));
                    int D24N = cursor.getInt(cursor.getColumnIndex("D24N"));
                    int D25N = cursor.getInt(cursor.getColumnIndex("D25N"));
                    int D26N = cursor.getInt(cursor.getColumnIndex("D26N"));
                    int D27N = cursor.getInt(cursor.getColumnIndex("D27N"));
                    int D28N = cursor.getInt(cursor.getColumnIndex("D28N"));
                    int D29N = cursor.getInt(cursor.getColumnIndex("D29N"));
                    int D30N = cursor.getInt(cursor.getColumnIndex("D30N"));
                    int D31N = cursor.getInt(cursor.getColumnIndex("D31N"));

                    Logger.i(TAG, "수축기MIN (월단위) D1N:" + D1N + ", D2N:" + D2N + ", D3N:" + D3N + ", D4N:" + D4N + ", D5N:" + D5N + ", D6N:" + D6N + ", D7N:" + D7N);
                    Logger.i(TAG, "D8N:" + D8N + ", D9N:" + D9N + ", D10N:" + D10N + ", D11N:" + D11N + ", D12N:" + D12N + ", D13N:" + D13N + ", D14N:" + D14N + ", D15N:" + D15N);
                    Logger.i(TAG, "D16N:" + D16N + ", D17N:" + D17N + ", D18N:" + D18N + ", D19N:" + D19N + ", D20N:" + D20N + ", D21N:" + D21N + ", D22N:" + D22N + ", D23N:" + D23N);
                    Logger.i(TAG, "D24N:" + D24N + ", D25N:" + D25N + ", D26N:" + D26N + ", D27N:" + D27N + ", D28N:" + D28N + ", D29N:" + D29N + ", D30N:" + D30N + ", D31N:" + D31N);

                    int S1M = cursor.getInt(cursor.getColumnIndex("S1M"));
                    int S2M = cursor.getInt(cursor.getColumnIndex("S2M"));
                    int S3M = cursor.getInt(cursor.getColumnIndex("S3M"));
                    int S4M = cursor.getInt(cursor.getColumnIndex("S4M"));
                    int S5M = cursor.getInt(cursor.getColumnIndex("S5M"));
                    int S6M = cursor.getInt(cursor.getColumnIndex("S6M"));
                    int S7M = cursor.getInt(cursor.getColumnIndex("S7M"));
                    int S8M = cursor.getInt(cursor.getColumnIndex("S8M"));
                    int S9M = cursor.getInt(cursor.getColumnIndex("S9M"));
                    int S10M = cursor.getInt(cursor.getColumnIndex("S10M"));
                    int S11M = cursor.getInt(cursor.getColumnIndex("S11M"));
                    int S12M = cursor.getInt(cursor.getColumnIndex("S12M"));
                    int S13M = cursor.getInt(cursor.getColumnIndex("S13M"));
                    int S14M = cursor.getInt(cursor.getColumnIndex("S14M"));
                    int S15M = cursor.getInt(cursor.getColumnIndex("S15M"));
                    int S16M = cursor.getInt(cursor.getColumnIndex("S16M"));
                    int S17M = cursor.getInt(cursor.getColumnIndex("S17M"));
                    int S18M = cursor.getInt(cursor.getColumnIndex("S18M"));
                    int S19M = cursor.getInt(cursor.getColumnIndex("S19M"));
                    int S20M = cursor.getInt(cursor.getColumnIndex("S20M"));
                    int S21M = cursor.getInt(cursor.getColumnIndex("S21M"));
                    int S22M = cursor.getInt(cursor.getColumnIndex("S22M"));
                    int S23M = cursor.getInt(cursor.getColumnIndex("S23M"));
                    int S24M = cursor.getInt(cursor.getColumnIndex("S24M"));
                    int S25M = cursor.getInt(cursor.getColumnIndex("S25M"));
                    int S26M = cursor.getInt(cursor.getColumnIndex("S26M"));
                    int S27M = cursor.getInt(cursor.getColumnIndex("S27M"));
                    int S28M = cursor.getInt(cursor.getColumnIndex("S28M"));
                    int S29M = cursor.getInt(cursor.getColumnIndex("S29M"));
                    int S30M = cursor.getInt(cursor.getColumnIndex("S30M"));
                    int S31M = cursor.getInt(cursor.getColumnIndex("S31M"));

                    Logger.i(TAG, "이완기MAX (월단위) S1M:" + S1M + ", S2M:" + S2M + ", S3M:" + S3M + ", S4M:" + S4M + ", S5M:" + S5M + ", S6M:" + S6M + ", S7M:" + S7M);
                    Logger.i(TAG, "S8M:" + S8M + ", S9M:" + S9M + ", S10M:" + S10M + ", S11M:" + S11M + ", S12M:" + S12M + ", S13M:" + S13M + ", S14M:" + S14M + ", S15M:" + S15M);
                    Logger.i(TAG, "S16M:" + S16M + ", S17M:" + S17M + ", S18M:" + S18M + ", S19M:" + S19M + ", S20M:" + S20M + ", S21M:" + S21M + ", S22M:" + S22M + ", S23M:" + S23M);
                    Logger.i(TAG, "S24M:" + S24M + ", S25M:" + S25M + ", S26M:" + S26M + ", S27M:" + S27M + ", S28M:" + S28M + ", S29M:" + S29M + ", S30M:" + S30M + ", S31M:" + S31M);


                    int S1N = cursor.getInt(cursor.getColumnIndex("S1N"));
                    int S2N = cursor.getInt(cursor.getColumnIndex("S2N"));
                    int S3N = cursor.getInt(cursor.getColumnIndex("S3N"));
                    int S4N = cursor.getInt(cursor.getColumnIndex("S4N"));
                    int S5N = cursor.getInt(cursor.getColumnIndex("S5N"));
                    int S6N = cursor.getInt(cursor.getColumnIndex("S6N"));
                    int S7N = cursor.getInt(cursor.getColumnIndex("S7N"));
                    int S8N = cursor.getInt(cursor.getColumnIndex("S8N"));
                    int S9N = cursor.getInt(cursor.getColumnIndex("S9N"));
                    int S10N = cursor.getInt(cursor.getColumnIndex("S10N"));
                    int S11N = cursor.getInt(cursor.getColumnIndex("S11N"));
                    int S12N = cursor.getInt(cursor.getColumnIndex("S12N"));
                    int S13N = cursor.getInt(cursor.getColumnIndex("S13N"));
                    int S14N = cursor.getInt(cursor.getColumnIndex("S14N"));
                    int S15N = cursor.getInt(cursor.getColumnIndex("S15N"));
                    int S16N = cursor.getInt(cursor.getColumnIndex("S16N"));
                    int S17N = cursor.getInt(cursor.getColumnIndex("S17N"));
                    int S18N = cursor.getInt(cursor.getColumnIndex("S18N"));
                    int S19N = cursor.getInt(cursor.getColumnIndex("S19N"));
                    int S20N = cursor.getInt(cursor.getColumnIndex("S20N"));
                    int S21N = cursor.getInt(cursor.getColumnIndex("S21N"));
                    int S22N = cursor.getInt(cursor.getColumnIndex("S22N"));
                    int S23N = cursor.getInt(cursor.getColumnIndex("S23N"));
                    int S24N = cursor.getInt(cursor.getColumnIndex("S24N"));
                    int S25N = cursor.getInt(cursor.getColumnIndex("S25N"));
                    int S26N = cursor.getInt(cursor.getColumnIndex("S26N"));
                    int S27N = cursor.getInt(cursor.getColumnIndex("S27N"));
                    int S28N = cursor.getInt(cursor.getColumnIndex("S28N"));
                    int S29N = cursor.getInt(cursor.getColumnIndex("S29N"));
                    int S30N = cursor.getInt(cursor.getColumnIndex("S30N"));
                    int S31N = cursor.getInt(cursor.getColumnIndex("S31N"));

                    Logger.i(TAG, "이완기MIN (월단위) S1N:" + S1N + ", S2N:" + S2N + ", S3N:" + S3N + ", S4N:" + S4N + ", S5N:" + S5N + ", S6N:" + S6N + ", S7N:" + S7N);
                    Logger.i(TAG, "S8N:" + S8N + ", S9N:" + S9N + ", S10N:" + S10N + ", S11N:" + S11N + ", S12N:" + S12N + ", S13N:" + S13N + ", S14N:" + S14N + ", S15N:" + S15N);
                    Logger.i(TAG, "S16N:" + S16N + ", S17N:" + S17N + ", S18N:" + S18N + ", S19N:" + S19N + ", S20N:" + S20N + ", S21N:" + S21N + ", S22N:" + S22N + ", S23N:" + S23N);
                    Logger.i(TAG, "S24N:" + S24N + ", S25N:" + S25N + ", S26N:" + S26N + ", S27N:" + S27N + ", S28N:" + S28N + ", S29N:" + S29N + ", S30N:" + S30N + ", S31N:" + S31N);

                    for (int i = 1; i <= dayCnt; i++) {
                        int DM = cursor.getInt(cursor.getColumnIndex("D" + i + "M"));
                        int DN = cursor.getInt(cursor.getColumnIndex("D" + i + "N"));
                        int SM = cursor.getInt(cursor.getColumnIndex("S" + i + "M"));
                        int SN = cursor.getInt(cursor.getColumnIndex("S" + i + "N"));

                        PressureEntry entry = new PressureEntry(i, DM, DN, SM, SN);
                        yVals1.add(entry);
                        Logger.i(TAG, "data["+i+"] DM="+DM+" DN="+DN);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        } else {
            for (int i = 1; i < dayCnt; i++) {
                PressureEntry entry = new PressureEntry(i, 0, 0, 0, 0);
                yVals1.add(entry);
            }
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return yVals1;
    }

    /*
    // 메인 통계 (수축기평균, 이완기평균 수축기최고 이완기최고)
     */
    public List<Tr_get_hedctdata.DataList> getResultAll(SQLiteOpenHelper helper) {

        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String result = "";
        List<Tr_get_hedctdata.DataList> data_list = new ArrayList<>();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT * FROM "+ TB_DATA_PRESSURE
                +" Order by  datetime("+ PRESURE_REGDATE +") desc,  cast("+ PRESURE_IDX +" as BIGINT) DESC;";
        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        int i = 0;
        try {
            while (cursor.moveToNext()) {

                Tr_get_hedctdata.DataList data = new Tr_get_hedctdata.DataList();
                data.idx = cursor.getString(cursor.getColumnIndex(PRESURE_IDX)); // 2
                data.arterialPressure = cursor.getString(cursor.getColumnIndex(PRESURE_ARTERIALPRESSURE)); // 10
                data.diastolicPressure = cursor.getString(cursor.getColumnIndex(PRESURE_DIASTOLICPRESSUR)); // 50
                data.pulseRate = cursor.getString(cursor.getColumnIndex(PRESURE_PULSERATE)); // 0
                data.systolicPressure = cursor.getString(cursor.getColumnIndex(PRESURE_SYSTOLICPRESSURE)); // 30
                data.drugname = cursor.getString(cursor.getColumnIndex(PRESURE_DRUGNAME)); // 약이름
                data.reg_de = cursor.getString(cursor.getColumnIndex(PRESURE_REGDATE)); // U
                data.regtype = cursor.getString(cursor.getColumnIndex(PRESURE_REGTYPE));

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
    // 메인 통계 (수축기평균, 이완기평균 수축기최고 이완기최고)
     */
    public PressureData getResultStatic(SQLiteOpenHelper helper, String sDate, String eDate) {

        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "Select "
                +" AVG(CAST("+PRESURE_SYSTOLICPRESSURE+" as INTEGER))  as SYSTOLIC, "        //수축기평균
                +" AVG(CAST("+PRESURE_DIASTOLICPRESSUR+" as INTEGER))  as DIASTOLC, "        //이완기평균
                +" MAX(CAST("+PRESURE_SYSTOLICPRESSURE+" as INTEGER))  as MAXSYSTOLIC, "     //수축기최고
                +" MAX(CAST("+PRESURE_DIASTOLICPRESSUR+" as INTEGER))  as MAXDIASTOLC"       //이완기최고
                +" FROM " + TB_DATA_PRESSURE
                +" WHERE "+ PRESURE_REGTYPE +" in ('D', 'U') and " + PRESURE_REGDATE +" BETWEEN '"+ sDate +" 00:00' and '"+eDate+" 23:59' and "+ PRESURE_DRUGNAME + " = ''";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count="+cursor.getCount());
        PressureData pressureData = new PressureData();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                int v1 = cursor.getInt(cursor.getColumnIndex("SYSTOLIC"));  // 수축기평균
                int v2 = cursor.getInt(cursor.getColumnIndex("DIASTOLC"));  // 이완기평균
                int v3 = cursor.getInt(cursor.getColumnIndex("MAXSYSTOLIC"));  // 수축기최고
                int v4 = cursor.getInt(cursor.getColumnIndex("MAXDIASTOLC"));  // 이완기최고
                pressureData.setSystolic(v1);
                pressureData.setDiastolc(v2);
                pressureData.setMaxsystolic(v3);
                pressureData.setMaxdiastolc(v4);
                Logger.i(TAG, "결과 SYSTOLIC:"+v1+", DIASTOLC:"+v2+", MAXSYSTOLIC:" +v3+", MAXDIASTOLC:" +v4);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pressureData;
    }

    /*
  ** 혈당 메인 투약시간 그래프
 */
    public List<PressureModel> getMedicenTime(String sDate, String eDate){
        SQLiteDatabase db = mHelper.getReadableDatabase();

        List<PressureModel> arrayMedi = new ArrayList<>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT "+ PRESURE_IDX +", "+PRESURE_REGDATE+", "+ PRESURE_DRUGNAME +
                " FROM "+ TB_DATA_PRESSURE+
                " WHERE "+ PRESURE_ARTERIALPRESSURE + " <= '0' and "+PRESURE_DIASTOLICPRESSUR+" <= '0'  and "+ PRESURE_REGDATE +" BETWEEN '"+ sDate +" 00:00' and '"+eDate+" 23:59' " +
                " Order by  datetime("+ PRESURE_REGDATE +") desc, cast("+ PRESURE_IDX +" as BIGINT) ASC;";
        Logger.i(TAG, "sql="+sql);

        Cursor cursor = db.rawQuery(sql, null);

        if (!cursor.moveToFirst()) {
            cursor.close();
            Logger.e(TAG, "getResultMain !cursor.moveToFirst()");
        }

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                for (int i = 0; i <= cursor.getCount()-1; i++) {
                    String preIdx = cursor.getString(cursor.getColumnIndex(PRESURE_IDX));
                    String preReg = cursor.getString(cursor.getColumnIndex(PRESURE_REGDATE));
                    String preName = cursor.getString(cursor.getColumnIndex(PRESURE_DRUGNAME));


                    PressureModel premodel = new PressureModel();
                    premodel.setIdx(preIdx);
                    premodel.setRegdate(preReg);

                    arrayMedi.add(i, premodel);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }else {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return arrayMedi;
    }

    public static class PressureData {
        private int systolic = 0;
        private int diastolc = 0;
        private int maxsystolic = 0;
        private int maxdiastolc = 0;

        public int getSystolic() {
            return systolic;
        }

        public void setSystolic(int systolic) {
            this.systolic = systolic;
        }

        public int getDiastolc() {
            return diastolc;
        }

        public void setDiastolc(int diastolc) {
            this.diastolc = diastolc;
        }

        public int getMaxsystolic() {
            return maxsystolic;
        }

        public void setMaxsystolic(int maxsystolic) {
            this.maxsystolic = maxsystolic;
        }

        public int getMaxdiastolc() {
            return maxdiastolc;
        }

        public void setMaxdiastolc(int maxdiastolc) {
            this.maxdiastolc = maxdiastolc;
        }
    }
}