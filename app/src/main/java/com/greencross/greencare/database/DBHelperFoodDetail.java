package com.greencross.greencare.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.greencross.greencare.network.tr.data.Tr_get_meal_input_food_data;
import com.greencross.greencare.network.tr.data.Tr_meal_input_food_edit_data;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrsohn on 2017. 3. 20..
 */

public class DBHelperFoodDetail {
    private final String TAG = DBHelperFoodDetail.class.getSimpleName();

    public static String TB_DATA_FOOD_DETAIL = "tb_data_food_detail";         //
    public static String FOOD_IDX = "idx"; // 고유번호 Str 14 M yyMMddHHmmssff,
    public static String FOOD_FOODCODE = "foodcode"; // 음식코드 Int M
    public static String FOOD_FORPEOPLE = "forpeople"; // 인분 int M 몇인분영문필드명
    public static String FOOD_REGDATE = "regdate";

    private DBHelper mHelper;

    public DBHelperFoodDetail(DBHelper helper) {
        mHelper = helper;
    }

    // DB를 새로 생성할 때 호출되는 함수
    public String createDb() {
        // 새로운 테이블 생성
        String sql = " CREATE TABLE if not exists " + TB_DATA_FOOD_DETAIL + " ("
                + FOOD_IDX + " INTEGER , "
                + FOOD_FOODCODE + " INT, "
                + FOOD_FORPEOPLE + " VARCHAR(5) NULL, "
                + FOOD_REGDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP); ";

        Logger.i(TAG, "onCreate.sql=" + sql);
        return sql;

    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    public String upgradeDb() {
        return "DROP TABLE " + TB_DATA_FOOD_DETAIL + ";";
    }

    public void insert(List<Tr_get_meal_input_food_data.ReceiveDatas> datas) {
        // 읽고 쓰기가 가능하게 DB 열기
        String sql = "INSERT INTO " + TB_DATA_FOOD_DETAIL
                + " VALUES ";

        for (Tr_get_meal_input_food_data.ReceiveDatas data : datas) {
            StringBuffer sb = new StringBuffer();
            sb.append(sql);
            String values = "('"
                    + data.idx + "', '" // 4",
                    + data.foodcode + "', '" // 400",
                    + data.forpeople + "', '" // 400",
                    + CDateUtil.getRegDateFormat_yyyyMMddHHss(data.regdate) + "') "; // 201703301420"


            sb.append(values);

            Logger.i(TAG, "insert.sql=" + sb.toString());
            mHelper.transactionExcute(sb.toString());
        }
    }

    public void insert(List<DBHelperFoodCalorie.Data> datas, String idx, String regDate) {
        // 읽고 쓰기가 가능하게 DB 열기
        // SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        StringBuffer sb = new StringBuffer();
        int cnt = 0;
        String sql = "INSERT INTO " + TB_DATA_FOOD_DETAIL
                + " VALUES ";
        sb.append(sql);

        for (DBHelperFoodCalorie.Data data : datas) {
            String values = "('"
                    + idx + "', '" // 4",
                    + data.food_code + "', '" // 400",
                    + data.forpeople + "', '" // 400",
                    + regDate + "') "; // 201703301420"


            sb.append(values);
            if (cnt != (datas.size() - 1)) {
                sb.append(",");
            }
            cnt++;
        }

        Logger.i(TAG, "insert.sql=" + sb.toString());
        mHelper.transactionExcute(sb.toString());

    }

    /**
     * 일지 수정
     *
     * @param isServerReg
     */
    public void update(Tr_meal_input_food_edit_data.RequestData data, boolean isServerReg) {
        StringBuffer sb = new StringBuffer();
        String sql = "UPDATE " + TB_DATA_FOOD_DETAIL;
        sb.append(sql);

        sql = "SET "
                + FOOD_FOODCODE + "='" + data.foodcode + "', "
                + FOOD_FORPEOPLE + "='" + data.forpeople + "' ";
        sb.append(sql);

        sql = "WHERE " + FOOD_IDX + "='" + data.idx + "'";
        sb.append(sql);


        Logger.i(TAG, "insert.sql=" + sb.toString());
        mHelper.transactionExcute(sb.toString());
    }


    public void getResult() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "Select * "
                + " FROM " + TB_DATA_FOOD_DETAIL;

        String weight = "0";
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
   // 먹은 음식리스트
    */
    public List<DBHelperFoodCalorie.Data> getFoodList(String idx) {

        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql = "Select "
                + " code, kind, name, forpeople, gram, unit, calorie, carbohydrate, protein, fat, sugars, salt, cholesterol, saturated, ctransquantic "
                + " FROM tb_data_food_calorie "
                + "        INNER JOIN tb_data_food_detail ON tb_data_food_detail.foodcode = tb_data_food_calorie.code "
                + " WHERE tb_data_food_detail.idx ='" + idx + "' ";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);

        Logger.i(TAG, "count =" + cursor.getCount());

        List<DBHelperFoodCalorie.Data> array = new ArrayList<>();
        if (cursor.getCount() > 0) {
            try {
                while (cursor.moveToNext()) {

                    String code = cursor.getString(cursor.getColumnIndex("code"));                    // 푸드코드
                    String kind = cursor.getString(cursor.getColumnIndex("kind"));                    // 분류( 면류, 당류..)
                    String name = cursor.getString(cursor.getColumnIndex("name"));                    // 음식이름(김밥 등..)
                    String forpeople = cursor.getString(cursor.getColumnIndex("forpeople"));          // 인분(몇인분)
                    String gram = cursor.getString(cursor.getColumnIndex("gram"));                    // 그램
                    String unit = cursor.getString(cursor.getColumnIndex("unit"));                    // 단위
                    String calorie = cursor.getString(cursor.getColumnIndex("calorie"));              // 칼로리(열량)
                    String carbohydrate = cursor.getString(cursor.getColumnIndex("carbohydrate"));    // 탄수화물
                    String protein = cursor.getString(cursor.getColumnIndex("protein"));              // 단백질
                    String fat = cursor.getString(cursor.getColumnIndex("fat"));                      // 지방
                    String sugars = cursor.getString(cursor.getColumnIndex("sugars"));                // 당류
                    String salt = cursor.getString(cursor.getColumnIndex("salt"));                    // 나트륨
                    String cholesterol = cursor.getString(cursor.getColumnIndex("cholesterol"));      // 콜레스테롤
                    String saturated = cursor.getString(cursor.getColumnIndex("saturated"));          // 포화지방산
                    String ctransquantic = cursor.getString(cursor.getColumnIndex("ctransquantic"));  // 트랜스지방산

                    Logger.i(TAG, "먹은 음식리스트:::::  code:" + code + ", kind:" + kind + ", name:" + name + ", forpeople:" + forpeople + ", gram:" + gram + ", calorie:" + calorie + ", gram:" + gram + ", " +
                            "carbohydrate:" + carbohydrate + ", protein:" + protein + ", fat:" + fat + ", sugars:" + sugars + ", salt:" + salt + ", cholesterol:" + cholesterol + ", saturated:" + saturated + ", ctransquantic:" + ctransquantic);
                    DBHelperFoodCalorie.Data data = new DBHelperFoodCalorie.Data();
                    data.food_code = code;
                    data.food_kind = kind;
                    data.food_name = name;
                    data.forpeople = forpeople;
                    data.food_gram = gram;
                    data.food_unit = unit;
                    data.food_calorie = calorie;
                    data.food_carbohydrate = carbohydrate;
                    data.food_protein = protein;
                    data.food_fat = fat;
                    data.food_sugars = sugars;
                    data.food_salt = salt;
                    data.food_cholesterol = cholesterol;
                    data.food_saturated = saturated;
                    data.food_ctransquantic = ctransquantic;

                    array.add(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {

        }
        return array;
    }
}