package com.greencross.greencare.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.greencross.greencare.bluetooth.model.BandModel;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;

/**
 * Created by mrsohn on 2017. 3. 20..
 */

public class DBHelperStepRealtime {
    private final String TAG = DBHelperStepRealtime.class.getSimpleName();

    private DBHelper mHelper;
    public DBHelperStepRealtime(DBHelper helper) {
        mHelper = helper;
    }
    /**
     * 운동데이터
     */
    public static String TB_DATA_STEP_REALTIME = "tb_data_step_realtime";             // 운동데이터
    private String STEP_IDX = "idx";            //서버에 저장되는 고유번호 int M 로컬과 동일하게사용
    private String STEP_CALORIE = "calorie";        //칼로리 Str 7 M
    private String STEP_DISTANCE = "distance";    //이동거리 Str 7
    private String STEP_STEP = "step";        // 걸음수 Str 7 M
    private String STEP_HEARTRATE = "heartRate";    // 최근 심장박동수 Str 7
    private String STEP_STRESS = "stress";        // 스트레스지수 Str 7
    private String STEP_REGTYPE = "regtype";        // 등록타입 Str 1 M
    private String STEP_REGDATE = "regdate";        //등록일시 Date M
    private String IS_SERVER_REGIST = "is_server_regist";                // 서버 등록 여부


    // DB를 새로 생성할 때 호출되는 함수
    public String createDb() {
        // 새로운 테이블 생성
        String sql = " CREATE TABLE if not exists "+TB_DATA_STEP_REALTIME+" ("
                            +STEP_IDX+" CHARACTER(14) PRIMARY KEY, "
                            +STEP_CALORIE+" VARCHAR(15) NULL, "
                            +STEP_DISTANCE+" VARCHAR(20) NULL, "
                            +STEP_STEP+" VARCHAR(15) NULL, "
                            +STEP_HEARTRATE+" VARCHAR(15) NULL, "
                            +STEP_STRESS+" VARCHAR(15) NULL, "
                            +STEP_REGTYPE+" CHARACTER(1) NULL, "
                            +IS_SERVER_REGIST+" BOOLEAN,"
                            +STEP_REGDATE+" DATETIME DEFAULT CURRENT_TIMESTAMP); ";
        Logger.i(TAG, "onCreate.sql="+sql);
        return sql;
    }

    // 히스토리에서 선택된 DB로우를 삭제하는 함수
    public void DeleteDb(String idx){
        String sql = "DELETE FROM " +TB_DATA_STEP_REALTIME + " WHERE idx =='"+idx+"' ";
        Logger.i(TAG, "onDelete.sql = "+sql);
        mHelper.transactionExcute(sql);

    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    public String upgradeDb() {
        return "DROP TABLE "+TB_DATA_STEP_REALTIME+";";
    }


    public void insert(BandModel model, boolean isServerReg)
    {
        //오늘 이전 데이터 삭제.
        deleteTodayOther();

        String sql = "INSERT INTO "+TB_DATA_STEP_REALTIME
                +" VALUES ('"+model.getIdx()+ "', '"
                +model.getCalories()+ "', '"
                +model.getDistance()+ "', '"
                +model.getStep()+ "', '"
                +model.getHeartRate()+ "', '"
                +model.getStress()+ "', '"
                +model.getRegtype()+ "', '"
                +isServerReg+ "', '"
                +CDateUtil.getRegDateFormat_yyyyMMddHHss(model.getRegDate())+ "')";

        Logger.i(TAG, "insert.sql="+sql);
        mHelper.transactionExcute(sql);

        getResult();
    }

    public void delete(String _idx) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        String sql = "DELETE FROM "+TB_DATA_STEP_REALTIME+" WHERE "+STEP_IDX+"='" + _idx + "';";
        Logger.i(TAG, "delete.sql="+sql);
        db.execSQL(sql);
        Logger.i(TAG, sql);
        db.close();
    }

    public void deleteTodayOther() {
        // 월요일
        SQLiteDatabase db = mHelper.getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        String sql = "DELETE FROM "+TB_DATA_STEP_REALTIME+" WHERE "+STEP_REGDATE+" < '" + CDateUtil.getToday_yyyyMMdd() + "';";
        Logger.i(TAG, "delete.sql="+sql);
        db.execSQL(sql);
        Logger.i(TAG, sql);
        db.close();
    }

    public void getResult() {

        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = mHelper.getReadableDatabase();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT * FROM "+TB_DATA_STEP_REALTIME+" ORDER BY  datetime("+ STEP_REGDATE +") desc, cast("+STEP_IDX +" as BIGINT) desc LIMIT 100";
        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        int i = 0;
        while (cursor.moveToNext()) {
            String idx = cursor.getString(cursor.getColumnIndex(STEP_IDX));
            String step = cursor.getString(cursor.getColumnIndex(STEP_STEP));
            String carlori = cursor.getString(cursor.getColumnIndex(STEP_CALORIE));
            String distance = cursor.getString(cursor.getColumnIndex(STEP_DISTANCE));
            String heart = cursor.getString(cursor.getColumnIndex(STEP_HEARTRATE));
            String regDt = cursor.getString(cursor.getColumnIndex(STEP_REGDATE));
            Logger.i(TAG, "idx["+(idx )+"], step["+step+"],  carlori["+carlori+"], heart["+heart+"], distance["+distance+"], regDt=" +regDt);
        }
    }


}