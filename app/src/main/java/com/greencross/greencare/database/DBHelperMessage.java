package com.greencross.greencare.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.greencross.greencare.bluetooth.model.MessageModel;
import com.greencross.greencare.network.tr.data.Tr_infra_message;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrsohn on 2017. 3. 20..
 */

public class DBHelperMessage {
    private final String TAG = DBHelperMessage.class.getSimpleName();

    private DBHelper mHelper;
    public DBHelperMessage(DBHelper helper) {
        mHelper = helper;
    }
    /**
     * 건강메시지
     */
    public static String TB_DATA_MESSAGE = "tb_data_message";
    private String MESSAGE_IDX = "idx";                                 // 고유번호intM서버데이터 삭제를 위한 키값
    private String MESSAGE_MESSAGE = "message";                         // 메시지
    private String IS_SERVER_REGIST = "is_server_regist";               // 서버 등록 여부
    private String MESSAGE_REGDATE = "regdate";                         // 등록일시 MyyyyMMddHHmm

    // DB를 새로 생성할 때 호출되는 함수
    public String createDb() {
        // 새로운 테이블 생성
        String sql = "CREATE TABLE if not exists "+TB_DATA_MESSAGE+" ("
                            + MESSAGE_IDX+" CHARACTER(14) PRIMARY KEY, "
                            + MESSAGE_MESSAGE+" VARCHAR(500) NULL, "
                            + IS_SERVER_REGIST+" BOOLEAN, "
                            + MESSAGE_REGDATE+" DATETIME DEFAULT CURRENT_TIMESTAMP); ";
        Logger.i(TAG, "onCreate.sql="+sql);
        return sql;
    }

    // 히스토리에서 선택된 DB로우를 삭제하는 함수
    public void DeleteDb(String idx){
        String sql = "DELETE FROM " +TB_DATA_MESSAGE + " WHERE idx == '"+idx+"' ";
        Logger.i(TAG, "onDelete.sql = "+sql);
        mHelper.transactionExcute(sql);

    }


    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    public String upgradeDb() {
        return "DROP TABLE "+TB_DATA_MESSAGE+";";
    }

    public void insert(MessageModel messageModel, boolean isServerReg) {
        // 읽고 쓰기가 가능하게 DB 열기
        String sql = "INSERT INTO "+TB_DATA_MESSAGE
                                +" VALUES('"+messageModel.getIdx()+"', '"
                                        + messageModel.getMessage() + "', '"
                                        + isServerReg + "', '"
                                        + CDateUtil.getRegDateFormat_yyyyMMddHHss(messageModel.getRegdate()) + "');";
        Logger.i(TAG, "insert.sql="+sql);
        mHelper.transactionExcute(sql);
    }

    /**
     * 3개월 데이터 가져오기 메시지 Sqlite 저장
     * @param messageList
     * @param isServerReg
     */
    public void insert(List<Tr_infra_message.MessageList> messageList, boolean isServerReg) {

        for (Tr_infra_message.MessageList data : messageList) {
            // 읽고 쓰기가 가능하게 DB 열기
            String sql = "INSERT INTO "+TB_DATA_MESSAGE
                    +" VALUES('"+data.idx+"', '"
                    + data.message_cn + "', '"
                    + isServerReg + "', '"
                    + CDateUtil.getRegDateFormat_yyyyMMddHHss(data.message_de) + "')";
            Logger.i(TAG, "insert.sql="+sql);
            mHelper.transactionExcute(sql);
        }
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
    public MessageData getResultMain(SQLiteOpenHelper helper) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = helper.getReadableDatabase();
        MessageData resultData = new MessageData();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT "+MESSAGE_IDX+",  "+MESSAGE_MESSAGE+", " + MESSAGE_REGDATE
                    + " FROM "+ TB_DATA_MESSAGE
                    + " Order by datetime("+ MESSAGE_REGDATE +") desc, cast("+ MESSAGE_IDX +" as BIGINT) DESC LIMIT 100";

        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        Logger.i(TAG, "main pressure count="+cursor.getCount());

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                try {
                    String message = cursor.getString(cursor.getColumnIndex(MESSAGE_MESSAGE));
                    String regdate = cursor.getString(cursor.getColumnIndex(MESSAGE_REGDATE));
                    Logger.i(TAG, "건강메시지 :  message=" + message + "  regdate=" + regdate);
                    resultData.setMessage(message);
                    resultData.setRegdate(regdate);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        }

        return resultData;
    }

    /*
    // 메인 통계 (수축기평균, 이완기평균 수축기최고 이완기최고)
     */
    public List<MessageModel> getResultAll(DBHelper helper) {

        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = helper.getReadableDatabase();
        List<MessageModel> data_list = new ArrayList<>();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String sql = "SELECT * FROM "+ TB_DATA_MESSAGE
                +" ORDER BY  datetime("+ MESSAGE_REGDATE +") desc, cast("+ MESSAGE_IDX +" as BIGINT) DESC "
                +" LIMIT 100;";
        Logger.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        Logger.i(TAG, "getResultAll size="+cursor.getCount());

        try {
            while (cursor.moveToNext()) {

                String idx = cursor.getString(cursor.getColumnIndex(MESSAGE_IDX));
                String message = cursor.getString(cursor.getColumnIndex(MESSAGE_MESSAGE));
                String isServerRegist = cursor.getString(cursor.getColumnIndex(IS_SERVER_REGIST));
                String regDate = cursor.getString(cursor.getColumnIndex(MESSAGE_REGDATE));

                MessageModel model = new MessageModel();
                model.setIdx(idx);
                model.setMessage(message);
                model.setRegdate(regDate);

                data_list.add(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return data_list;
    }


    public static class MessageData {
        private String message ="";
        private String regdate = "";

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getRegdate() {
            return regdate;
        }

        public void setRegdate(String regdate) {
            this.regdate = regdate;
        }

    }
}