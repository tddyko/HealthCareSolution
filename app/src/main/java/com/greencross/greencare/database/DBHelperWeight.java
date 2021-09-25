package com.greencross.greencare.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.greencross.greencare.charting.data.BarEntry;
import com.greencross.greencare.network.tr.data.Tr_get_hedctdata;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrsohn on 2017. 3. 20..
 */

public class DBHelperWeight {
    private final String TAG = DBHelperWeight.class.getSimpleName();

    private DBHelper mHelper;
    public DBHelperWeight(DBHelper helper) {
        mHelper = helper;
    }
    /**
     * 체중데이터
     */
    public static String TB_DATA_WEIGHT = "tb_data_weight";
    private String  WEIGHT_IDX = "idx";                         // 고유번호
    private String WEIGHT_BMR = "bmr";                          // 기초대사율
    private String WEIGHT_BODYWATE = "bodyWater";               // 체수분
    private String WEIGHT_BONE = "bone";                        // 뼈
    private String WEIGHT_FAT = "fat";                          // 살
    private String WEIGHT_HEARTRAT = "heartRate";               // 심박동수
    private String WEIGHT_MUSCLE = "muscle";                    // 근육
    private String WEIGHT_OBESITY = "obesity";                  // 비만
    private String WEIGHT_WEIGHT = "weight";                    // 체중
    private String WEIGHT_REGTYPE = "regtype";                  // 등록타입 D:디바이P스,U:직접등록
    private String WEIGHT_REGDATE = "regdate";                  // 등록일시  MyyyyMMddHHmm
    private String IS_SERVER_REGIST = "is_server_regist";       // 서버 등록 여부


    // DB를 새로 생성할 때 호출되는 함수
    public String createDb() {
        // 새로운 테이블 생성
        String sql =  " CREATE TABLE if not exists "+TB_DATA_WEIGHT+" ("
                                +WEIGHT_IDX+" CHARACTER(14) PRIMARY KEY, "
                                +WEIGHT_BMR+" VARCHAR(15) NULL, "
                                +WEIGHT_BODYWATE+" VARCHAR(15) NULL, "
                                +WEIGHT_BONE+" VARCHAR(15) NULL, "
                                +WEIGHT_FAT+" VARCHAR(15) NULL, "
                                +WEIGHT_HEARTRAT+" VARCHAR(15) NULL, "
                                +WEIGHT_MUSCLE+" VARCHAR(15) NULL, "
                                +WEIGHT_OBESITY+" VARCHAR(15) NULL, "
                                +WEIGHT_WEIGHT+" VARCHAR(15) NULL, "
                                +WEIGHT_REGTYPE+" VARCHAR(1) NULL, "
                                +IS_SERVER_REGIST+" BOOLEAN, "
                                +WEIGHT_REGDATE+" DATETIME DEFAULT CURRENT_TIMESTAMP); ";
        Logger.i(TAG, "onCreate.sql="+sql);
        return sql;

    }

    public void DeleteDb(String idx){
        String sql = "DELETE FROM " +TB_DATA_WEIGHT + " WHERE idx == '"+idx+"' ";
        Logger.i(TAG, "onDelete.sql = "+sql);
        mHelper.transactionExcute(sql);
    }

    public void UpdateDb(String idx, String weight, String bodyRate, String inner, String water, String muscle, String reg_de){
        String sql = "UPDATE " + TB_DATA_WEIGHT +
                " SET " + WEIGHT_WEIGHT + " = " + weight +
                ", " + WEIGHT_FAT + " = " + bodyRate +
                ", " + WEIGHT_OBESITY + " = " + inner +
                ", " + WEIGHT_BODYWATE + " = " + water +
                ", " + WEIGHT_MUSCLE + " = " + muscle +
                ", " + WEIGHT_REGDATE + " = '" + CDateUtil.getRegDateFormat_yyyyMMddHHss(reg_de) +"'"+
                " WHERE idx == " +idx;

        Logger.i(TAG, "onUpdate.sql = "+sql);
        mHelper.transactionExcute(sql);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    public String upgradeDb() {
        return "DROP TABLE "+TB_DATA_WEIGHT+";";
    }

    public void insert(Tr_get_hedctdata datas, boolean isServerReg) {
        insert(datas.data_list, isServerReg);
    }

    public void insert(List<Tr_get_hedctdata.DataList> datas, boolean isServerReg) {


        int cnt = 0;
        String sql = "INSERT INTO "+TB_DATA_WEIGHT
                +" VALUES";

        for (Tr_get_hedctdata.DataList data : datas) {
            StringBuffer sb = new StringBuffer();
            sb.append(sql);

            String values = "('"
                    + data.idx+ "', '" // 4",
                    + data.bmr+ "', '" // 400",
                    + data.bodywater+ "', '" // 530",
                    + data.bone+ "', '" // 3",
                    + data.fat+ "', '" // 310",
                    + data.heartrate+ "', '" // 141",
                    + data.muscle+ "', '" // 52",
                    + data.obesity+ "', '" // 3",
                    + data.weight+ "', '" // 131",
                    + data.regtype+ "', '" // D",
                    + isServerReg+ "', '" // D",
                    + CDateUtil.getRegDateFormat_yyyyMMddHHss(data.reg_de) + " ') "; // 201703301420"


            sb.append(values);

            Logger.i(TAG, "insert.sql="+sb.toString());
            mHelper.transactionExcute(sb.toString());

        }
    }

    /*
    // 일단위
     */
    public List<BarEntry> getResultDay(String nDate, boolean isWeight) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<BarEntry> yVals = new ArrayList<>();

        String fields = WEIGHT_WEIGHT;
        if (!isWeight){
            fields = WEIGHT_FAT;
        }

        Logger.i(TAG, "isWeight ="+isWeight);

        String sql = "Select "
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=0 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H0,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=1 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H1,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=2 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H2,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=3 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H3,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=4 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H4,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=5 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H5,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=6 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H6,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=7 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H7,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=8 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H8,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=9 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H9,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=10 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H10,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=11 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H11,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=12 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H12,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=13 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H13,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=14 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H14,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=15 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H15,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=16 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H16,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=17 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H17,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=18 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H18,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=19 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H19,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=20 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H20,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=21 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H21,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=22 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H22,"
                +" ROUND(MAX(CASE WHEN cast(strftime('%H', A."+WEIGHT_REGDATE+") as integer)=23 THEN cast(ifnull(A."+fields+",0) as FLOAT) End),1) as H23 "
                +" FROM "+ TB_DATA_WEIGHT + " as A "
                +" inner join ( "
                +"              SELECT MAX(idx) as idx FROM "+ TB_DATA_WEIGHT
                +"    WHERE  "+WEIGHT_REGDATE+" BETWEEN '"+ nDate +" 00:00' AND '"+ nDate +" 23:59' ";
        if (fields.equals(WEIGHT_WEIGHT)) {
            sql +=" and cast(" + WEIGHT_WEIGHT + " as FLOAT) >= 20 and cast(" + WEIGHT_WEIGHT + " as FLOAT) <= 300 ";
        }else{
            sql +=" and cast(" + WEIGHT_FAT + " as FLOAT) > 0  ";
        }
        sql +="    GROUP BY Strftime('%H', "+WEIGHT_REGDATE+") "
                +" ) as B ON A.idx = B.idx "
                +" WHERE A."+ WEIGHT_REGDATE +" BETWEEN '"+ nDate +" 00:00' and '"+nDate+" 23:59' " ;
        if (fields.equals(WEIGHT_WEIGHT)) {
            sql +=" and cast(A." + WEIGHT_WEIGHT + " as FLOAT) >= 20 and cast(A." + WEIGHT_WEIGHT + " as FLOAT) <= 300 ";
        }else{
            sql +=" and cast(A." + WEIGHT_FAT + " as FLOAT) > 0  ";
        }
        sql +=" Group by strftime('%Y', A."+WEIGHT_REGDATE+"), strftime('%m', A."+WEIGHT_REGDATE+"), strftime('%d', A."+WEIGHT_REGDATE+") " ;


        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "query ="+cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                float h0 = cursor.getFloat(cursor.getColumnIndex("H0"));
                float h1 = cursor.getFloat(cursor.getColumnIndex("H1"));
                float h2 = cursor.getFloat(cursor.getColumnIndex("H2"));
                float h3 = cursor.getFloat(cursor.getColumnIndex("H3"));
                float h4 = cursor.getFloat(cursor.getColumnIndex("H4"));
                float h5 = cursor.getFloat(cursor.getColumnIndex("H5"));
                float h6 = cursor.getFloat(cursor.getColumnIndex("H6"));
                float h7 = cursor.getFloat(cursor.getColumnIndex("H7"));
                float h8 = cursor.getFloat(cursor.getColumnIndex("H8"));
                float h9 = cursor.getFloat(cursor.getColumnIndex("H9"));
                float h10 = cursor.getFloat(cursor.getColumnIndex("H10"));
                float h11 = cursor.getFloat(cursor.getColumnIndex("H11"));
                float h12 = cursor.getFloat(cursor.getColumnIndex("H12"));
                float h13 = cursor.getFloat(cursor.getColumnIndex("H13"));
                float h14 = cursor.getFloat(cursor.getColumnIndex("H14"));
                float h15 = cursor.getFloat(cursor.getColumnIndex("H15"));
                float h16 = cursor.getFloat(cursor.getColumnIndex("H16"));
                float h17 = cursor.getFloat(cursor.getColumnIndex("H17"));
                float h18 = cursor.getFloat(cursor.getColumnIndex("H18"));
                float h19 = cursor.getFloat(cursor.getColumnIndex("H19"));
                float h20 = cursor.getFloat(cursor.getColumnIndex("H20"));
                float h21 = cursor.getFloat(cursor.getColumnIndex("H21"));
                float h22 = cursor.getFloat(cursor.getColumnIndex("H22"));
                float h23 = cursor.getFloat(cursor.getColumnIndex("H23"));

                Logger.i(TAG, "몸무게 h0:"+h0+", h1:"+h1+", h2:"+h2+", h3:" +h3+", h4:" +h4+", h5:" +h5+", h6:" +h6+", h7:" +h7);
                Logger.i(TAG, "h8:" +h8+", h9:" +h9+", h10:" +h10+", h11:" +h11+", h12:" +h12+", h13:" +h13+", h14:" +h14+", h15:" +h15);
                Logger.i(TAG, "h16:"+h16+", h17:"+h17+", h18:"+h18+", h19:"+h19+", h20:"+h20+", h21:"+h21+", h22:"+h22+", h23:"+h23);

                for (int i = 0 ; i <= cursor.getColumnCount()-1; i++) {
                    float stepVal = cursor.getFloat(cursor.getColumnIndex("H"+i));
                    float idx = i;
                    if (i==0){
                        idx = idx - 0.5f;
                    }else{
//                        idx = idx - 1.0f;
                    }

                    BarEntry entry = new BarEntry(idx,stepVal);
                    yVals.add(entry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }else{
            for (int i = 0 ; i <= cursor.getColumnCount()-1; i++) {
                BarEntry entry = new BarEntry(i-1, 0);
                yVals.add(entry);
            }
        }
        return yVals;
    }


    /*
    // 주단위
     */
    public List<BarEntry> getResultWeek(String sDate, String eDate, boolean isWeight) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<BarEntry> yVals = new ArrayList<>();

        String fields = WEIGHT_WEIGHT;
        if (!isWeight){
            fields = WEIGHT_FAT;
        }
        Logger.i(TAG, "isWeight ="+isWeight);

        String sql = "Select "
                +" ROUND(AVG(CASE cast(strftime('%w', "+WEIGHT_REGDATE+") as integer) WHEN 0 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as W1,"    //일요일부터 시작
                +" ROUND(AVG(CASE cast(strftime('%w', "+WEIGHT_REGDATE+") as integer) WHEN 1 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as W2,"
                +" ROUND(AVG(CASE cast(strftime('%w', "+WEIGHT_REGDATE+") as integer) WHEN 2 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as W3,"
                +" ROUND(AVG(CASE cast(strftime('%w', "+WEIGHT_REGDATE+") as integer) WHEN 3 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as W4,"
                +" ROUND(AVG(CASE cast(strftime('%w', "+WEIGHT_REGDATE+") as integer) WHEN 4 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as W5,"
                +" ROUND(AVG(CASE cast(strftime('%w', "+WEIGHT_REGDATE+") as integer) WHEN 5 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as W6,"
                +" ROUND(AVG(CASE cast(strftime('%w', "+WEIGHT_REGDATE+") as integer) WHEN 6 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as W7 "
                +" FROM " + TB_DATA_WEIGHT
                +" WHERE " + WEIGHT_REGDATE +" BETWEEN '"+ sDate +" 00:00' and '"+eDate+" 23:59' ";
        if (fields.equals(WEIGHT_WEIGHT)) {
            sql +=" and cast(" + WEIGHT_WEIGHT + " as FLOAT) >= 20 and cast(" + WEIGHT_WEIGHT + " as FLOAT) <= 300 ";
        }else{
            sql +=" and cast(" + WEIGHT_FAT + " as FLOAT) > 0  ";
        }


        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "count ="+cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                float w1 = cursor.getFloat(cursor.getColumnIndex("W1"));
                float w2 = cursor.getFloat(cursor.getColumnIndex("W2"));
                float w3 = cursor.getFloat(cursor.getColumnIndex("W3"));
                float w4 = cursor.getFloat(cursor.getColumnIndex("W4"));
                float w5 = cursor.getFloat(cursor.getColumnIndex("W5"));
                float w6 = cursor.getFloat(cursor.getColumnIndex("W6"));
                float w7 = cursor.getFloat(cursor.getColumnIndex("W7"));

                Logger.i(TAG, "결과 w1:"+w1+", w2:"+w2+", w3:" +w3+", w4:" +w4+", w5:" +w5+", w6:" +w6+", w7:" +w7);

                for (int i = 1 ; i <= cursor.getColumnCount(); i++) {
                    float stepVal = cursor.getFloat(cursor.getColumnIndex("W"+i));
                    int idx = i;
                    BarEntry entry = new BarEntry(idx,stepVal);
                    yVals.add(entry);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }else{
            for (int i = 1 ; i <= cursor.getColumnCount(); i++) {
                BarEntry entry = new BarEntry(i,0);
                yVals.add(entry);
            }
        }
        return yVals;
    }


    /*
    // 월단위
     */
    public List<BarEntry> getResultMonth(String nYear, String nMonth, boolean isWeight) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<BarEntry> yVals = new ArrayList<>();

        String fields = WEIGHT_WEIGHT;
        if (!isWeight){
            fields = WEIGHT_FAT;
        }

        String sql = "Select "
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 1 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D1,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 2 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D2,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 3 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D3,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 4 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D4,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 5 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D5,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 6 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D6,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 7 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D7,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 8 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D8,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 9 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D9,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 10 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D10,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 11 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D11,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 12 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D12,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 13 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D13,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 14 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D14,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 15 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D15,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 16 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D16,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 17 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D17,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 18 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D18,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 19 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D19,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 20 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D20,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 21 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D21,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 22 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D22,"
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 23 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D23, "
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 24 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D24, "
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 25 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D25, "
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 26 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D26, "
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 27 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D27, "
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 28 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D28, "
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 29 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D29, "
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 30 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D30, "
                +" ROUND(AVG(CASE cast(strftime('%d', "+WEIGHT_REGDATE+") as integer) WHEN 31 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as D31  "
                +" FROM "+ TB_DATA_WEIGHT
                +" WHERE cast(strftime('%Y',"+WEIGHT_REGDATE+") as integer)="+nYear+" and cast(strftime('%m',"+WEIGHT_REGDATE+") as integer)="+nMonth;
        if (fields.equals(WEIGHT_WEIGHT)) {
            sql +=" and cast(" + WEIGHT_WEIGHT + " as FLOAT) >= 20 and cast(" + WEIGHT_WEIGHT + " as FLOAT) <= 300 ";
        }else{
            sql +=" and cast(" + WEIGHT_FAT + " as FLOAT) > 0  ";
        }
        sql +=" Group by strftime('%Y',"+WEIGHT_REGDATE+"), strftime('%m',"+WEIGHT_REGDATE+") " ;

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "count ="+cursor.getCount());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                float d1 = cursor.getFloat(cursor.getColumnIndex("D1"));
                float d2 = cursor.getFloat(cursor.getColumnIndex("D2"));
                float d3 = cursor.getFloat(cursor.getColumnIndex("D3"));
                float d4 = cursor.getFloat(cursor.getColumnIndex("D4"));
                float d5 = cursor.getFloat(cursor.getColumnIndex("D5"));
                float d6 = cursor.getFloat(cursor.getColumnIndex("D6"));
                float d7 = cursor.getFloat(cursor.getColumnIndex("D7"));
                float d8 = cursor.getFloat(cursor.getColumnIndex("D8"));
                float d9 = cursor.getFloat(cursor.getColumnIndex("D9"));
                float d10 = cursor.getFloat(cursor.getColumnIndex("D10"));
                float d11 = cursor.getFloat(cursor.getColumnIndex("D11"));
                float d12 = cursor.getFloat(cursor.getColumnIndex("D12"));
                float d13 = cursor.getFloat(cursor.getColumnIndex("D13"));
                float d14 = cursor.getFloat(cursor.getColumnIndex("D14"));
                float d15 = cursor.getFloat(cursor.getColumnIndex("D15"));
                float d16 = cursor.getFloat(cursor.getColumnIndex("D16"));
                float d17 = cursor.getFloat(cursor.getColumnIndex("D17"));
                float d18 = cursor.getFloat(cursor.getColumnIndex("D18"));
                float d19 = cursor.getFloat(cursor.getColumnIndex("D19"));
                float d20 = cursor.getFloat(cursor.getColumnIndex("D20"));
                float d21 = cursor.getFloat(cursor.getColumnIndex("D21"));
                float d22 = cursor.getFloat(cursor.getColumnIndex("D22"));
                float d23 = cursor.getFloat(cursor.getColumnIndex("D23"));
                float d24 = cursor.getFloat(cursor.getColumnIndex("D24"));
                float d25 = cursor.getFloat(cursor.getColumnIndex("D25"));
                float d26 = cursor.getFloat(cursor.getColumnIndex("D26"));
                float d27 = cursor.getFloat(cursor.getColumnIndex("D27"));
                float d28 = cursor.getFloat(cursor.getColumnIndex("D28"));
                float d29 = cursor.getFloat(cursor.getColumnIndex("D29"));
                float d30 = cursor.getFloat(cursor.getColumnIndex("D30"));
                float d31 = cursor.getFloat(cursor.getColumnIndex("D31"));

                Logger.i(TAG, "결과 d1:"+d1+", d2:"+d2+", d3:" +d3+", d4:" +d4+", d5:" +d5+", d6:" +d6+", d7:" +d7);
                Logger.i(TAG, "d8:" +d8+", d9:" +d9+", d10:" +d10+", d11:" +d11+", d12:" +d12+", d13:" +d13+", d14:" +d14+", d15:" +d15);
                Logger.i(TAG, "d16:"+d16+", d17:"+d17+", d18:"+d18+", d19:"+d19+", d20:"+d20+", d21:"+d21+", d22:"+d22+", d23:"+d23);
                Logger.i(TAG, "d24:"+d24+", d25:"+d25+", d26:"+d26+", d27:"+d27+", d28:"+d28+", d29:"+d29+", d30:"+d30+", d31:"+d31);

                for (int i = 1 ; i < cursor.getColumnCount(); i++) {
                    float val = cursor.getFloat(cursor.getColumnIndex("D"+i));
                    BarEntry entry = new BarEntry(i, val);
                    yVals.add(entry);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }else{
            for (int i = 1 ; i <= cursor.getColumnCount(); i++) {
                BarEntry entry = new BarEntry(i, 0);
                yVals.add(entry);
            }
        }
        return yVals;
    }


    /*
    // 년단위
     */
    public List<BarEntry> getResultYear(String nYear, boolean isWeight) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<BarEntry> yVals = new ArrayList<>();

        String fields = WEIGHT_WEIGHT;
        if (!isWeight){
            fields = WEIGHT_FAT;
        }
        Logger.i(TAG, "isWeight ="+isWeight);

        String sql = "Select "
                +" ROUND(AVG(CASE cast(strftime('%m', "+WEIGHT_REGDATE+") as integer) WHEN 1 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as M1,"
                +" ROUND(AVG(CASE cast(strftime('%m', "+WEIGHT_REGDATE+") as integer) WHEN 2 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as M2,"
                +" ROUND(AVG(CASE cast(strftime('%m', "+WEIGHT_REGDATE+") as integer) WHEN 3 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as M3,"
                +" ROUND(AVG(CASE cast(strftime('%m', "+WEIGHT_REGDATE+") as integer) WHEN 4 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as M4,"
                +" ROUND(AVG(CASE cast(strftime('%m', "+WEIGHT_REGDATE+") as integer) WHEN 5 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as M5,"
                +" ROUND(AVG(CASE cast(strftime('%m', "+WEIGHT_REGDATE+") as integer) WHEN 6 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as M6,"
                +" ROUND(AVG(CASE cast(strftime('%m', "+WEIGHT_REGDATE+") as integer) WHEN 7 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as M7,"
                +" ROUND(AVG(CASE cast(strftime('%m', "+WEIGHT_REGDATE+") as integer) WHEN 8 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as M8,"
                +" ROUND(AVG(CASE cast(strftime('%m', "+WEIGHT_REGDATE+") as integer) WHEN 9 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as M9,"
                +" ROUND(AVG(CASE cast(strftime('%m', "+WEIGHT_REGDATE+") as integer) WHEN 10 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as M10,"
                +" ROUND(AVG(CASE cast(strftime('%m', "+WEIGHT_REGDATE+") as integer) WHEN 11 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as M11,"
                +" ROUND(AVG(CASE cast(strftime('%m', "+WEIGHT_REGDATE+") as integer) WHEN 12 THEN cast(ifnull("+fields+",0) as FLOAT) End),1) as M12 "
                +" FROM "+ TB_DATA_WEIGHT
                +" WHERE strftime('%Y',"+WEIGHT_REGDATE+")='"+nYear+"' ";
        if (fields.equals(WEIGHT_WEIGHT)) {
            sql +=" and cast(" + WEIGHT_WEIGHT + " as FLOAT) >= 20 and cast(" + WEIGHT_WEIGHT + " as FLOAT) <= 300 ";
        }else{
            sql +=" and cast(" + WEIGHT_FAT + " as FLOAT) > 0  ";
        }
        sql +=" Group by strftime('%Y',"+WEIGHT_REGDATE+")  " ;


        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "query ="+cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                float m1 = cursor.getFloat(cursor.getColumnIndex("M1"));
                float m2 = cursor.getFloat(cursor.getColumnIndex("M2"));
                float m3 = cursor.getFloat(cursor.getColumnIndex("M3"));
                float m4 = cursor.getFloat(cursor.getColumnIndex("M4"));
                float m5 = cursor.getFloat(cursor.getColumnIndex("M5"));
                float m6 = cursor.getFloat(cursor.getColumnIndex("M6"));
                float m7 = cursor.getFloat(cursor.getColumnIndex("M7"));
                float m8 = cursor.getFloat(cursor.getColumnIndex("M8"));
                float m9 = cursor.getFloat(cursor.getColumnIndex("M9"));
                float m10 = cursor.getFloat(cursor.getColumnIndex("M10"));
                float m11 = cursor.getFloat(cursor.getColumnIndex("M11"));
                float m12 = cursor.getFloat(cursor.getColumnIndex("M12"));

                Logger.i(TAG, "몸무게  m1:"+m1+", m2:"+m2+", m3:" +m3+", m4:" +m4+", m5:" +m5+", m6:" +m6+", m7:" +m7+", m8:" +m8+", m9:" +m9+", m10:" +m10+", m11:" +m11+", m12:" +m12);

                for (int i = 1 ; i <= cursor.getColumnCount(); i++) {
                    float val = cursor.getFloat(cursor.getColumnIndex("M"+i));
                    BarEntry entry = new BarEntry(i, val);
                    yVals.add(entry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }else{
            for (int i = 1 ; i <= cursor.getColumnCount(); i++) {
                BarEntry entry = new BarEntry(i,0);
                yVals.add(entry);
            }
        }
        return yVals;
    }

    /*
    // 메인화면 체지방
     */
    public Tr_get_hedctdata.DataList getResultMainFat() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "Select *"
                + " FROM "+ TB_DATA_WEIGHT
                + " Where cast("+WEIGHT_FAT+" as FLOAT) > 0 "
                +" Order by datetime("+ WEIGHT_REGDATE +") desc, cast("+ WEIGHT_IDX +" as BIGINT) DESC LIMIT 1";

        String weight = "0";
        String fat = "0";
        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "query ="+cursor.getCount());
        Tr_get_hedctdata.DataList datas = new Tr_get_hedctdata.DataList();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                weight = cursor.getString(cursor.getColumnIndex(WEIGHT_WEIGHT));
                fat = cursor.getString(cursor.getColumnIndex(WEIGHT_FAT));

                datas.weight = weight;
                datas.fat = fat;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }else{

        }
        return datas;
    }
    /*
    // 메인화면
     */
    public Tr_get_hedctdata.DataList getResultMain() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "Select *"
                +" FROM "+ TB_DATA_WEIGHT
                +" Order by datetime("+ WEIGHT_REGDATE +") desc, cast("+ WEIGHT_IDX +" as BIGINT) DESC LIMIT 1";

        String weight = "0";
        String fat = "0";
        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "query ="+cursor.getCount());
        Tr_get_hedctdata.DataList datas = new Tr_get_hedctdata.DataList();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                weight = cursor.getString(cursor.getColumnIndex(WEIGHT_WEIGHT));
                fat = cursor.getString(cursor.getColumnIndex(WEIGHT_FAT));

                datas.weight = weight;
                datas.fat = fat;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }else{

        }
        return datas;
    }

    public List<Tr_get_hedctdata.DataList> getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String result = "";
        List<Tr_get_hedctdata.DataList> data_list = new ArrayList<>();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT * FROM "+ TB_DATA_WEIGHT
                +" ORDER BY datetime("+ WEIGHT_REGDATE +") desc, cast("+WEIGHT_IDX+" as BIGINT) DESC";
        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        int i = 0;
        try {
            while (cursor.moveToNext()) {
                String weightIdx = cursor.getString(cursor.getColumnIndex(WEIGHT_IDX));
                String bmr = cursor.getString(cursor.getColumnIndex(WEIGHT_BMR));
                String bone = cursor.getString(cursor.getColumnIndex(WEIGHT_BONE));
                String heartrat = cursor.getString(cursor.getColumnIndex(WEIGHT_HEARTRAT));
                String weight = cursor.getString(cursor.getColumnIndex(WEIGHT_WEIGHT));
                String fat = cursor.getString(cursor.getColumnIndex(WEIGHT_FAT));
                String obesity = cursor.getString(cursor.getColumnIndex(WEIGHT_OBESITY));
                String water = cursor.getString(cursor.getColumnIndex(WEIGHT_BODYWATE));
                String muscle = cursor.getString(cursor.getColumnIndex(WEIGHT_MUSCLE));
                String regType = cursor.getString(cursor.getColumnIndex(WEIGHT_REGTYPE));
                String regDate = cursor.getString(cursor.getColumnIndex(WEIGHT_REGDATE));

                Tr_get_hedctdata.DataList data = new Tr_get_hedctdata.DataList();
                data.idx = weightIdx;
                data.bmr = bmr;
                data.bone = bone;
                data.heartrate = heartrat;
                data.weight = weight;
                data.fat = fat;
                data.obesity = obesity;
                data.bodywater = water;
                data.muscle = muscle;
                data.regtype = regType;
                data.reg_de = regDate;

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
    // 메인 바텀 최신 데이터 불러오기
     */
    public WeightStaticData getResultStatic() {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        String result = "";

        String sql = "Select * FROM " + TB_DATA_WEIGHT +
                " ORDER BY datetime("+ WEIGHT_REGDATE +") desc, cast(" + WEIGHT_IDX + " as BIGINT) DESC " +
                " LIMIT 1";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count="+cursor.getCount());

        WeightStaticData data = new WeightStaticData();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                data.setWeight(cursor.getString(cursor.getColumnIndex("weight")));
                data.setBmr(cursor.getString(cursor.getColumnIndex("bmr")));
                data.setObesity(cursor.getString(cursor.getColumnIndex("obesity")));
                data.setFat(cursor.getString(cursor.getColumnIndex("fat")));
                data.setBodyWater(cursor.getString(cursor.getColumnIndex("bodyWater")));
                data.setMuscle(cursor.getString(cursor.getColumnIndex("muscle")));
                data.setRegdate(cursor.getString(cursor.getColumnIndex("regdate")));;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }else{

        }

        return data;
    }


    public static class WeightStaticData {
        private String weight = "";
        private String bmr = "";
        private String obesity = "";
        private String fat = "";
        private String bodyWater = "";
        private String muscle = "";
        private String regdate = "";

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getBmr() {
            return bmr;
        }

        public void setBmr(String bmr) {
            this.bmr = bmr;
        }

        public String getObesity() {
            return obesity;
        }

        public void setObesity(String obesity) {
            this.obesity = obesity;
        }

        public String getFat() {
            return fat;
        }

        public void setFat(String fat) {
            this.fat = fat;
        }

        public String getBodyWater() {
            return bodyWater;
        }

        public void setBodyWater(String bodyWater) {
            this.bodyWater = bodyWater;
        }

        public String getMuscle() {
            return muscle;
        }

        public void setMuscle(String muscle) {
            this.muscle = muscle;
        }

        public String getRegdate() {
            return regdate;
        }

        public void setRegdate(String regdate) {
            this.regdate = regdate;
        }

    }
}
