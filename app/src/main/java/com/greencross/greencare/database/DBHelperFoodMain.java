package com.greencross.greencare.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.charting.data.BarEntry;
import com.greencross.greencare.charting.data.RadarEntry;
import com.greencross.greencare.network.tr.data.Tr_get_meal_input_data;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.network.tr.data.Tr_meal_input_data;
import com.greencross.greencare.network.tr.data.Tr_meal_input_edit_data;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import static com.greencross.greencare.database.DBHelperFoodDetail.FOOD_FOODCODE;
import static com.greencross.greencare.database.DBHelperFoodDetail.TB_DATA_FOOD_DETAIL;

/**
 * Created by mrsohn on 2017. 3. 20..
 */

public class DBHelperFoodMain {
    private final String TAG = DBHelperFoodMain.class.getSimpleName();

    public static String TB_DATA_FOOD_MAIN = "tb_data_food_main";         //
    private String FOOD_IDX = "idx"; // 고유번호 Str 14 M yyMMddHHmmssff,
    private String FOOD_AMOUNTTIME = "amounttime"; // 식사소요시간 Str 7 M
    private String FOOD_MEALTYPE = "mealtype"; // 식사타입 Str 1 M 아침:a,
    private String FOOD_CALORIE = "calorie"; // 칼로리 Str 15 M
    private String FOOD_PICTURE = "picture"; // 사진이미지 Str 200 사진경로
    private String FOOD_REGDATE = "regdate"; // 식사시간 Date M yyyyMMddHHmm

    private DBHelper mHelper;

    public DBHelperFoodMain(DBHelper helper) {
        mHelper = helper;
    }

    // / DB를 새로 생성할 때 호출되는 함수
    public String createDb() {
        // 새로운 테이블 생성
        String sql = " CREATE TABLE if not exists " + TB_DATA_FOOD_MAIN + " ("
                + FOOD_IDX + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FOOD_AMOUNTTIME + " TEXT, "
                + FOOD_MEALTYPE + " VARCHAR(1) NULL, "
                + FOOD_CALORIE + " VARCHAR(10) NULL, "
                + FOOD_PICTURE + " VARCHAR(200) NULL, "
                + FOOD_REGDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP); ";
        Logger.i(TAG, "onCreate.sql=" + sql);
        return sql;

    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    public String upgradeDb() {
        return "DROP TABLE " + TB_DATA_FOOD_MAIN + ";";
    }

    public void insert(Tr_meal_input_data.RequestData data, boolean isServerReg) {
        List<Tr_meal_input_data.RequestData> list = new ArrayList<>();
        list.add(data);
        insert(list, isServerReg);
    }

    public void insert(List<Tr_meal_input_data.RequestData> datas, boolean isServerReg) {
        // DB에 입력한 값으로 행 추가
        StringBuffer sb = new StringBuffer();
        int cnt = 0;
        String sql = "INSERT INTO " + TB_DATA_FOOD_MAIN
                + " VALUES";
        sb.append(sql);

        for (Tr_meal_input_data.RequestData data : datas) {
            String values = "('"
                    + data.idx + "', '" // 4",
                    + data.amounttime + "', '" // 400",
                    + data.mealtype + "', '" // 530",
                    + data.calorie + "', '" // 3",
                    + data.picture + "', '" // 310",
                    + CDateUtil.getRegDateFormat_yyyyMMddHHss(data.regdate) + "') "; // 201703301420"


            sb.append(values);
            if (cnt != (datas.size() - 1)) {
                sb.append(",");
            }
            cnt++;
        }

        Logger.i(TAG, "insert.sql=" + sb.toString());
        mHelper.transactionExcute(sb.toString());
    }

    public void insert(List<Tr_get_meal_input_data.ReceiveDatas> datas) {
        String sql = "INSERT INTO " + TB_DATA_FOOD_MAIN
                + " VALUES";

        for (Tr_get_meal_input_data.ReceiveDatas data : datas) {
            StringBuffer sb = new StringBuffer();
            sb.append(sql);
            String values = "('"
                    + data.idx + "', '" // 4",
                    + data.amounttime + "', '" // 400",
                    + data.mealtype + "', '" // 530",
                    + data.calorie + "', '" // 3",
                    + data.picture + "', '" // 310",
                    + CDateUtil.getRegDateFormat_yyyyMMddHHss(data.regdate) + " ') "; // 201703301420"


            sb.append(values);

            Logger.i(TAG, "insert.sql=" + sb.toString());
            mHelper.transactionExcute(sb.toString());
        }

    }

    /**
     * 일지 수정
     *
     * @param isServerReg
     */
    public void update(Tr_meal_input_edit_data.RequestData mainData, List<DBHelperFoodCalorie.Data> foodArray, boolean isServerReg) {

        //칼로리 계산
        float totCal = 0.0f;
        for (DBHelperFoodCalorie.Data data : foodArray) {
            totCal += StringUtil.getFloatVal(data.forpeople);
        }

        // 메인테이블 업데이트
        StringBuffer sb = new StringBuffer();
        String sql = "UPDATE " + TB_DATA_FOOD_MAIN;
        sb.append(sql);

        sql = " SET "
                + FOOD_AMOUNTTIME + "='" + mainData.amounttime + "', "
                + FOOD_CALORIE + "='" + totCal + "',  "   //전체칼로리.;
                + FOOD_REGDATE + "='" + CDateUtil.getRegDateFormat_yyyyMMddHHss(mainData.regdate) + "' ";
        sb.append(sql);

        sql = " WHERE " + FOOD_IDX + "='" + mainData.idx + "';";
        sb.append(sql);
        Logger.i(TAG, "update.sql=" + sb.toString());
        mHelper.transactionExcute(sb.toString());


        //서브테이블 삭제.
        sql = "DELETE From " + TB_DATA_FOOD_DETAIL
                + " Where idx='" + mainData.idx+"';";
        Logger.i(TAG, "delete.sql=" + sql);
        mHelper.transactionExcute(sql);

        sb = new StringBuffer();

        //서브테이블 등록
        int cnt = 0;
        sql = "INSERT INTO " + TB_DATA_FOOD_DETAIL
                + " VALUES ";
        sb.append(sql);

        for (DBHelperFoodCalorie.Data data : foodArray) {
            String values = "('"
                    + mainData.idx + "', '" // 4",
                    + data.food_code + "', '" // 400",
                    + data.forpeople + "', '" // 400",
                    + mainData.regdate.trim() + "') "; // 201703301420"


            sb.append(values);
            if (cnt != (foodArray.size() - 1)) {
                sb.append(",");
            }
            cnt++;
        }
        sb.append(";");

        Logger.i(TAG, "insert.sql=" + sb.toString());
        mHelper.transactionExcute(sb.toString());

    }

    /*
    // 일단위 호출(칼로리 및 소요시간을 리턴함)
     */
    public Tr_get_meal_input_data getResultDay(String nDate) {

        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql = "Select "
                + TB_DATA_FOOD_MAIN +"."+ FOOD_IDX + ", "
                + FOOD_MEALTYPE + ", "
                + FOOD_AMOUNTTIME + ",  "
                + FOOD_PICTURE + ",  "
                + TB_DATA_FOOD_MAIN +"."+ FOOD_REGDATE + ",  "
                + " SUM( ifnull(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE+"."+DBHelperFoodCalorie.Field.FOOD_CALORIE +",0) * ifnull("+ DBHelperFoodDetail.FOOD_FORPEOPLE+",0)) as "+DBHelperFoodCalorie.Field.FOOD_CALORIE
                + " FROM " + TB_DATA_FOOD_MAIN
                + " LEFT JOIN " + TB_DATA_FOOD_DETAIL + " ON "
                + TB_DATA_FOOD_MAIN+"."+FOOD_IDX + "=" + TB_DATA_FOOD_DETAIL +"."+DBHelperFoodDetail.FOOD_IDX
                + " LEFT JOIN " + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + " ON "
                + TB_DATA_FOOD_DETAIL+"."+ FOOD_FOODCODE + "=" +DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE +"."+ DBHelperFoodCalorie.Field.FOOD_CODE
                + " WHERE " + TB_DATA_FOOD_MAIN +"."+ FOOD_REGDATE + " BETWEEN '" + nDate + " 00:00' and '" + nDate + " 23:59' "
                + " Group by " + TB_DATA_FOOD_MAIN +"."+ FOOD_MEALTYPE;

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "query =" + cursor.getCount());
        Tr_get_meal_input_data input_data = new Tr_get_meal_input_data();
        try {
            while (cursor.moveToNext()) {

                String foodIdx = cursor.getString(cursor.getColumnIndex(FOOD_IDX));
                String foodMealType = cursor.getString(cursor.getColumnIndex(FOOD_MEALTYPE));
                String foodCalorie = cursor.getString(cursor.getColumnIndex(FOOD_CALORIE));
                String foodAmounttime = cursor.getString(cursor.getColumnIndex(FOOD_AMOUNTTIME));
                String picture = cursor.getString(cursor.getColumnIndex(FOOD_PICTURE));
                String regdate = cursor.getString(cursor.getColumnIndex(FOOD_REGDATE));

                Logger.i(TAG, "음식전체칼로리 regdate:"+regdate+" foodIdx:" + foodIdx + ", foodMealType:" + foodMealType + ", foodCalorie:" + foodCalorie + ", foodAmounttime:" + foodAmounttime+ ", picture:" + picture);
                Tr_get_meal_input_data.ReceiveDatas receiveDatas = new Tr_get_meal_input_data.ReceiveDatas();
                receiveDatas.idx = foodIdx;
                receiveDatas.mealtype = foodMealType;
                receiveDatas.calorie = ""+(int)StringUtil.getFloat(foodCalorie);
                receiveDatas.amounttime = foodAmounttime;
                receiveDatas.picture = picture;
                receiveDatas.regdate = regdate;
                input_data.data_list.add(receiveDatas);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return input_data;
    }


    public void getResult() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "Select * "
                + " FROM " + TB_DATA_FOOD_MAIN;

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "query =" + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
        }
    }


    /*
    // 일단위
     */
    public List<BarEntry> getResultDay(String nDate, int defalutCount) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<BarEntry> yVals1 = new ArrayList<>();

        String sql = "Select "
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=1  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H1,"                        //1시(식전) **데이터확인주의
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=2  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H2,"                        //2시(식전) **데이터확인주의
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=3  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H3,"                        //2시(식전) **데이터확인주의
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=4  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H4,"                        //3시(식전) **데이터확인주의
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=5  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H5,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=6  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H6,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=7  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H7,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=8  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H8,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=9  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H9,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=10  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H10,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=11  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H11,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=12  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H12,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=13  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H13,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=14  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H14,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=15  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H15,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=16  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H16,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=17  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H17,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=18  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H18,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=19  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H19,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=20  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H20,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=21  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H21,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=22  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H22,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=23  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H23,"
                + " SUM(CASE WHEN cast(strftime('%H', " + TB_DATA_FOOD_MAIN+"."+FOOD_REGDATE + ") as integer)=24  THEN CAST(" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + "."+DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)  End) as H24 "
                + " FROM " + TB_DATA_FOOD_MAIN
                + " INNER JOIN " + TB_DATA_FOOD_DETAIL +" ON "
                +  TB_DATA_FOOD_MAIN +"."+ FOOD_IDX +"="+ DBHelperFoodDetail.TB_DATA_FOOD_DETAIL +"."+ DBHelperFoodDetail.FOOD_IDX
                + " INNER JOIN " +DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE +" ON "
                +  DBHelperFoodDetail.TB_DATA_FOOD_DETAIL + "." + DBHelperFoodDetail.FOOD_FOODCODE +"=" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE +"."+DBHelperFoodCalorie.Field.FOOD_CODE
                + " WHERE " +TB_DATA_FOOD_MAIN+"."+ FOOD_REGDATE + " BETWEEN '" + nDate + " 00:00' and '" + nDate + " 23:59' "
                + " Group by cast(strftime('%d'," +TB_DATA_FOOD_MAIN+"."+ FOOD_REGDATE + ") as integer) ";


        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "count =" + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                for (int i = 1; i <= cursor.getColumnCount(); i++) {
                    float calorie = cursor.getInt(cursor.getColumnIndex("H" + i + ""));
                    Logger.i(TAG, "calorie[" + i + "].calorie=" + calorie);

                    BarEntry entry = new BarEntry(i, (int) calorie);
                    yVals1.add(entry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            for (int i = 1; i <= defalutCount; i++) {
                BarEntry entry = new BarEntry(i, 0);
                yVals1.add(entry);
            }
        }
        return yVals1;
    }


    /*
    // 주단위
     */
    public List<BarEntry> getResultWeek(String sDate, String eDate, int defalutCount) {
        List<BarEntry> yVals1 = new ArrayList<BarEntry>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        StringBuffer sb = new StringBuffer();

        String sql = "Select ";
        sb.append(sql);
        Logger.i(TAG, sql);
        int cnt = 6;
        for (int i = 0; i <= cnt; i++) {
            sql = (" ifnull(SUM(CASE  WHEN cast(strftime('%w', " +DBHelperFoodDetail.TB_DATA_FOOD_DETAIL+"."+ DBHelperFoodDetail.FOOD_REGDATE + ") as integer)=" + i + " THEN"
                    + " CAST(" + DBHelperFoodCalorie.Field.FOOD_CALORIE + " as FLOAT) * CAST(" + DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT) End),0) as W" + (i + 1) + " ");

            if (i != cnt)
                sql += ",";

            sb.append(sql);
            Logger.i(TAG, sql);
        }

        sql = " FROM " + TB_DATA_FOOD_DETAIL
                + " INNER JOIN " + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE +" ON "
                +  DBHelperFoodDetail.TB_DATA_FOOD_DETAIL + "." + DBHelperFoodDetail.FOOD_FOODCODE +"=" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE +"."+DBHelperFoodCalorie.Field.FOOD_CODE
                + " WHERE " +DBHelperFoodDetail.TB_DATA_FOOD_DETAIL +"."+ DBHelperFoodDetail.FOOD_REGDATE + " BETWEEN '" + sDate + " 00:00' and '" + eDate + " 23:59' ";
        sb.append(sql);
        Logger.i(TAG, sql);

        sql = sb.toString();


        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count=" + cursor.getCount());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                int w1 = cursor.getInt(cursor.getColumnIndex("W1"));
                int w2 = cursor.getInt(cursor.getColumnIndex("W2"));
                int w3 = cursor.getInt(cursor.getColumnIndex("W3"));
                int w4 = cursor.getInt(cursor.getColumnIndex("W4"));
                int w5 = cursor.getInt(cursor.getColumnIndex("W5"));
                int w6 = cursor.getInt(cursor.getColumnIndex("W6"));
                int w7 = cursor.getInt(cursor.getColumnIndex("W7"));

                Logger.i(TAG, "결과 w1:" + w1 + ", w2:" + w2 + ", w3:" + w3 + ", w4:" + w4 + ", w5:" + w5 + ", w6:" + w6 + ", w7:" + w7);

                Logger.i(TAG, "cursor.getColumnCount()=" + cursor.getColumnCount());
                for (int i = 1; i <= cursor.getColumnCount(); i++) {
                    float calorie = cursor.getInt(cursor.getColumnIndex("W" + i + ""));
                    Logger.i(TAG, "calorie[" + i + "].calorie=" + calorie);

                    BarEntry entry = new BarEntry(i - 1, (int) calorie);
                    yVals1.add(entry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            for (int i = 1; i <= defalutCount; i++) {
                BarEntry entry = new BarEntry(i - 1, 0);
                yVals1.add(entry);
            }
        }

        return yVals1;
    }


    /*
    // 월단위
     */
    public List<BarEntry> getResultMonth(String nYear, String nMonth, int dayCnt) {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<BarEntry> yVals1 = new ArrayList<>();

        StringBuffer sb = new StringBuffer();
        Logger.d(TAG, "######################################### 혈당 월간 쿼리 조회 #########################################");
        String sql = "SELECT ";
        sb.append(sql);
        Logger.i(TAG, sql);
        for (int i = 0; i <= dayCnt; i++) {
            sql = (" ifnull(SUM(CASE  WHEN cast(strftime('%d'," +TB_DATA_FOOD_DETAIL+"."+ FOOD_REGDATE + ") as integer)=" + i + "  THEN " + FOOD_CALORIE + "*" + DBHelperFoodDetail.FOOD_FORPEOPLE +" End), 0) as D" + i + " ");

            if (i != dayCnt)
                sql += ",";

            sb.append(sql);
            Logger.i(TAG, sql);
        }
        sql = " FROM " + TB_DATA_FOOD_DETAIL
                + " INNER JOIN " + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE +" ON "
                +  TB_DATA_FOOD_DETAIL + "." + DBHelperFoodDetail.FOOD_FOODCODE +"=" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE +"."+DBHelperFoodCalorie.Field.FOOD_CODE
                + " WHERE cast(strftime('%Y'," +TB_DATA_FOOD_DETAIL+"."+ DBHelperFoodDetail.FOOD_REGDATE + ") as integer)=" + nYear + " and cast(strftime('%m'," +TB_DATA_FOOD_DETAIL+"."+ DBHelperFoodDetail.FOOD_REGDATE + ") as integer)=" + nMonth;
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

                Logger.i(TAG, "월단위 d1:" + d1 + ", d2:" + d2 + ", d3:" + d3 + ", d4:" + d4 + ", d5:" + d5 + ", d6:" + d6 + ", d7:" + d7);
                Logger.i(TAG, "d8:" + d8 + ", d9:" + d9 + ", d10:" + d10 + ", d11:" + d11 + ", d12:" + d12 + ", d13:" + d13 + ", d14:" + d14 + ", d15:" + d15);
                Logger.i(TAG, "d16:" + d16 + ", d17:" + d17 + ", d18:" + d18 + ", d19:" + d19 + ", d20:" + d20 + ", d21:" + d21 + ", d22:" + d22 + ", d23:" + d23);
                Logger.i(TAG, "d24:" + d24 + ", d25:" + d25 + ", d26:" + d26 + ", d27:" + d27 + ", d28:" + d28);    //+", d29:"+d29+", d30:"+d30+", d31:"+d31a
                for (int i = 1; i <= cursor.getColumnCount() - 1; i++) {
                    float calorie = cursor.getInt(cursor.getColumnIndex("D" + i + ""));
                    Logger.i(TAG, "calorie[" + i + "].calorie=" + calorie);

                    BarEntry entry = new BarEntry(i - 1, (int) calorie);
                    yVals1.add(entry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            for (int i = 1; i <= dayCnt; i++) {
                BarEntry entry = new BarEntry(i - 1, 0);
                yVals1.add(entry);
            }
        }
        return yVals1;
    }


    /*
   // 아침,점심,저녁 (칼로리, 시간)
    */
    public int[] getMealSum(String sDate, String eDate) {

        SQLiteDatabase db = mHelper.getReadableDatabase();

        int[] datas = new int[6];

        String sql = "Select "
                + " ifnull(SUM(CASE WHEN " +TB_DATA_FOOD_MAIN+"."+ FOOD_MEALTYPE + " in ('a', 'd') THEN "
                + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE +"."+DBHelperFoodCalorie.Field.FOOD_CALORIE + "*" + DBHelperFoodDetail.FOOD_FORPEOPLE +" End),0) as breakfast,"
                + " ifnull(SUM(CASE WHEN " +TB_DATA_FOOD_MAIN+"."+ FOOD_MEALTYPE + " in ('b', 'e') THEN "
                + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE +"."+ DBHelperFoodCalorie.Field.FOOD_CALORIE + "*" + DBHelperFoodDetail.FOOD_FORPEOPLE +" End),0) as lunch,"
                + " ifnull(SUM(CASE WHEN " +TB_DATA_FOOD_MAIN+"."+ FOOD_MEALTYPE + " in ('c', 'f') THEN "
                + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE +"."+ DBHelperFoodCalorie.Field.FOOD_CALORIE + "*" + DBHelperFoodDetail.FOOD_FORPEOPLE +" End),0) as dinner,"

                + " ifnull(AVG(CASE WHEN " +TB_DATA_FOOD_MAIN+"."+ FOOD_MEALTYPE + " in ('a') THEN " + FOOD_AMOUNTTIME + " End),0) as break_time,"
                + " ifnull(AVG(CASE WHEN " +TB_DATA_FOOD_MAIN+"."+ FOOD_MEALTYPE + " in ('b') THEN " + FOOD_AMOUNTTIME + " End),0) as lunch_time,"
                + " ifnull(AVG(CASE WHEN " +TB_DATA_FOOD_MAIN+"."+ FOOD_MEALTYPE + " in ('c') THEN " + FOOD_AMOUNTTIME + " End),0) as dinner_time "
                + " FROM " + TB_DATA_FOOD_MAIN
                + " INNER JOIN " + TB_DATA_FOOD_DETAIL +" ON "
                + TB_DATA_FOOD_DETAIL + "."+ DBHelperFoodDetail.FOOD_IDX +"="+TB_DATA_FOOD_MAIN+"."+FOOD_IDX
                + " INNER JOIN " + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE +" ON "
                + TB_DATA_FOOD_DETAIL + "." + DBHelperFoodDetail.FOOD_FOODCODE +"=" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE +"."+DBHelperFoodCalorie.Field.FOOD_CODE
                + " WHERE " + TB_DATA_FOOD_DETAIL+"."+ DBHelperFoodDetail.FOOD_REGDATE + " BETWEEN '" + sDate + " 00:00' and '" + eDate + " 23:59' ";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "count =" + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                datas[0] = cursor.getInt(cursor.getColumnIndex("breakfast"));
                datas[1] = cursor.getInt(cursor.getColumnIndex("lunch"));
                datas[2] = cursor.getInt(cursor.getColumnIndex("dinner"));
                datas[3] = cursor.getInt(cursor.getColumnIndex("break_time"));
                datas[4] = cursor.getInt(cursor.getColumnIndex("lunch_time"));
                datas[5] = cursor.getInt(cursor.getColumnIndex("dinner_time"));
                Logger.i(TAG, "아침,점심,저녁 (칼로리) breakfast:" + datas[0] + ", lunch:" + datas[1] + ", dinner:" + datas[3]);
                Logger.i(TAG, "아침,점심,저녁 (시간) breaktime:" + datas[4] + ", lunchtime:" + datas[4] + ", dinnertime:" + datas[5]);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
        }
        return datas;
    }


    /*
   // 방사형그래프
    */
    public List<RadarEntry> getRadial(String sDate, String eDate, float totTakeCal, float recomCal) {

        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql = "Select "
                + " ifnull(SUM(CAST(tb_data_food_calorie.calorie as FLOAT) * CAST(tb_data_food_detail.forpeople as FLOAT)),0) as calorie,"
                + " ifnull(SUM(CAST(tb_data_food_calorie.protein as FLOAT) * CAST(tb_data_food_detail.forpeople as FLOAT)),0) as protein,"
                + " ifnull(SUM(CAST(tb_data_food_calorie.salt as FLOAT) * CAST(tb_data_food_detail.forpeople as FLOAT)),0) as salt,"
                + " ifnull(SUM(CAST(tb_data_food_calorie.fat as FLOAT) * CAST(tb_data_food_detail.forpeople as FLOAT)),0) as fat,"
                + " ifnull(SUM(CAST(tb_data_food_calorie.carbohydrate as FLOAT) * CAST(tb_data_food_detail.forpeople as FLOAT)),0) as carbohydrate "
                + " FROM tb_data_food_detail"
                + " INNER JOIN tb_data_food_calorie ON tb_data_food_detail.foodcode = tb_data_food_calorie.code"
                + " WHERE tb_data_food_detail.regdate between '" + sDate + " 00:00' and '" + eDate + " 23:59'";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "count =" + cursor.getCount());
        List<RadarEntry> entries = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                float protein = cursor.getInt(cursor.getColumnIndex("protein"));  // 단백질
                float fat = cursor.getInt(cursor.getColumnIndex("fat"));          // 지방
                float carbohydrate = cursor.getInt(cursor.getColumnIndex("carbohydrate"));    // 탄수화물
                float salt = cursor.getInt(cursor.getColumnIndex("salt"));        // 나트륨


                float gCalorie = 0.0f;           //그래프 칼로리수치
                float gCarbohydrate = 0.0f;      //그래프 탄수화물수치
                float gProtein = 0.0f;           //그래프 단백질수치
                float gFat     = 0.0f;           //그래프 지방 수치
                float gSalt    = 0.0f;           //그래프 나트륨수치

                // + --------------------------
                //칼로리
                // + --------------------------
                float tmpCal= (totTakeCal / recomCal) * 100;
                if (tmpCal > 110){
                    gCalorie = 100.0f; // 과잉
                }else if (tmpCal >= 90 && tmpCal <=110){
                    gCalorie = 66.6f;
                }else if (tmpCal < 90) {
                    gCalorie = 33.3f;
                }

                // + --------------------------
                //단백질
                // + --------------------------
                float tmpProtein = ((protein * 4)/totTakeCal) * 100;
                if (tmpProtein > 20){
                    gProtein = 100.0f; // 과잉
                }else if (tmpProtein >= 7 && tmpProtein <=20){
                    gProtein = 66.6f;
                }else if (tmpProtein < 7) {
                    gProtein = 33.3f;
                }

                // + --------------------------
                // 탄수화물
                // + --------------------------
                float tmpCarbohydrate = ((carbohydrate * 4)/totTakeCal) * 100;
                if (tmpCarbohydrate > 65){
                    gCarbohydrate = 100.0f; // 과잉
                }else if (tmpCarbohydrate >= 55 && tmpCarbohydrate <=65){
                    gCarbohydrate = 66.6f;
                }else if (tmpCarbohydrate < 55) {
                    gCarbohydrate = 33.3f;
                }

                // + --------------------------
                // 지방
                // + --------------------------
                float tmpFat = ((fat * 9)/totTakeCal) * 100;
                if (tmpFat > 30){
                    gFat = 100.0f; // 과잉
                }else if (tmpFat >= 15 && tmpFat <=30){
                    gFat = 66.6f;
                }else if (tmpFat < 15){
                    gFat = 33.3f;
                }

                // + --------------------------
                // 나트륨
                // + --------------------------
                Tr_login login = Define.getInstance().getLoginInfo();
                String sex = login.mber_sex;
                int rBirth = Integer.parseInt(login.mber_lifyea.substring(0,4));                                   // 출생년도
                int nowYear = Integer.parseInt(CDateUtil.getFormattedString_yyyy(System.currentTimeMillis()));     // 현재년도
                int nAge = (nowYear - rBirth + 1);                                                                 // 나이

                int tmpSalt = 0;        //충분섭취량
                if (sex.equals("2")){   //여성
                    if(nAge >= 19 && nAge <=29){
                        tmpSalt = 1500;
                    }else if(nAge >= 30 && nAge <=49){
                        tmpSalt = 1500;
                    }else if(nAge >= 50 && nAge <=64){
                        tmpSalt = 1500;
                    }else if(nAge >= 65 && nAge <=74){
                        tmpSalt = 1300;
                    }else if(nAge >= 75) {
                        tmpSalt = 1100;
                    }
                }else{
                    if(nAge >= 19 && nAge <=29){
                        tmpSalt = 1500;
                    }else if(nAge >= 30 && nAge <=49){
                        tmpSalt = 1500;
                    }else if(nAge >= 50 && nAge <=64){
                        tmpSalt = 1500;
                    }else if(nAge >= 65 && nAge <=74){
                        tmpSalt = 1300;
                    }else if(nAge >= 75) {
                        tmpSalt = 1100;
                    }
                }
                if (salt >= 2000){
                    gSalt = 100.0f; // 과잉
                }else if (salt >= tmpSalt && salt <= 2000){
                    gSalt = 66.6f;
                }else if(salt < tmpSalt){
                    gSalt = 33.6f;
                }

                Logger.i(TAG, "방사형 그래프 :::::  열량(칼로리)totTakeCal:" + totTakeCal + ", 단백질(protein):" + protein + ", 나트륨(salt):" + salt + ", 지방(fat):" + fat + ", 탄수화물(carbohydrate):" + carbohydrate);
                entries.add(new RadarEntry(gCalorie));
                entries.add(new RadarEntry(gProtein));
                entries.add(new RadarEntry(gSalt));
                entries.add(new RadarEntry(gFat));
                entries.add(new RadarEntry(gCarbohydrate));

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            entries.add(new RadarEntry(0));
            entries.add(new RadarEntry(0));
            entries.add(new RadarEntry(0));
            entries.add(new RadarEntry(0));
            entries.add(new RadarEntry(0));
        }
        return entries;
    }


    public String getResultMain() {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String nowDate = CDateUtil.getToday_yyyyMMdd();

        String sql = "Select "
                + " SUM(CAST(" +DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE+"."+FOOD_CALORIE +" as FLOAT) * CAST("+ TB_DATA_FOOD_DETAIL+"."+DBHelperFoodDetail.FOOD_FORPEOPLE + " as FLOAT)) as " + FOOD_CALORIE
                + " FROM " + TB_DATA_FOOD_MAIN
                + " INNER JOIN " + TB_DATA_FOOD_DETAIL +" ON "
                +  TB_DATA_FOOD_MAIN + "." + FOOD_IDX +"=" + TB_DATA_FOOD_DETAIL +"."+DBHelperFoodDetail.FOOD_IDX
                + " INNER JOIN " + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE + " ON "
                +  TB_DATA_FOOD_DETAIL + "." + FOOD_FOODCODE +"=" + DBHelperFoodCalorie.Field.TB_DATA_FOOD_CALORIE +"."+DBHelperFoodCalorie.Field.FOOD_CODE
                + " WHERE " +TB_DATA_FOOD_MAIN+"."+ FOOD_REGDATE + " BETWEEN '" + nowDate + " 00:00' and '" + nowDate + " 23:59' ";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, " count=" + cursor.getCount());

        String calorie = "";

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                calorie = cursor.getString(cursor.getColumnIndex(FOOD_CALORIE));

                Logger.i(TAG, "결과 [getResultMain] calorie:" + calorie);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {

        }

        return calorie;
    }
}