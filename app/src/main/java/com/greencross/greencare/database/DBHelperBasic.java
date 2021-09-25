package com.greencross.greencare.database;

import com.greencross.greencare.util.Logger;

/**
 * Created by mrsohn on 2017. 3. 20..
 */

public class DBHelperBasic {
    private final String TAG = DBHelperBasic.class.getSimpleName();

    private DBHelper mHelper;
    public DBHelperBasic(DBHelper helper) {
        mHelper = helper;
    }
    /**
     * 기준치 데이터
     */
    public static String TB_BASIC = "tb_basic";                 // 기준치 데이터 저장 (key값으로 Value값 가져옴)
    private String DEF_KEY = "key";                             // 이메일
    private String DEF_VALUE = "value";                         // 이메일
    private String DEF_MEM_EMAIL = "def_mem_email";             // 이메일
    private String DEF_MEM_NAME = "def_mem_name";               // 이름
    private String DEF_MEM_SEX = "def_mem_sex";                 // M or F성별
    private String DEF_MEM_BIRTH = "def_mem_birth";             // 1980-12-11생일
    private String DEF_MEM_HEIGHT = "def_mem_height";            // 신장(키)
    private String DEF_MEM_WEIGHT = "def_mem_weight";            // 체중
    private String DEF_MEM_WEIGHT_TARGET = "def_mem_weight_target";            // 목표체중
    private String DEF_MEM_HIGH_PRESSURE = "def_mem_high_pressure";            // Y or N고혈압
    private String DEF_MEM_HYPERLIPIDEMI = "def_mem_hyperlipidemia";            // Y or N고지혈증
    private String DEF_MEM_SUGER_TYPE1 = "def_mem_suger_type1";            // Y or N1형 당뇨
    private String DEF_MEM_SUGER_TYPE2 = "def_mem_suger_type2";            // Y or N2형 당뇨
    private String DEF_MEM_EMPTY = "def_mem_empty";                 // Y or N없음(병없음)
    private String DEF_MEM_DRUG = "def_mem_drug";                   // Y or N복약여부
    private String DEF_MEM_SMOKE = "def_mem_smoke";                 // Y or N흡연여부
    private String DEF_TARGET_STEP = "def_target_step";             // 123걸음수 목표(하루 걸음수 목표)
    private String DEF_TARGET_WATER = "def_target_water";			// 11물 목표량

    // DB를 새로 생성할 때 호출되는 함수
    public String createDb() {
        // 새로운 테이블 생성
        String sql =  " CREATE TABLE if not exists "+TB_BASIC+" ("
            + DEF_KEY+" VARCHAR(100) NULL, "
            + DEF_VALUE +" VARCHAR(100) NULL, "
            + DEF_MEM_EMAIL +" VARCHAR(100) NULL, "
            + DEF_MEM_NAME +" VARCHAR(100) NULL, "
            + DEF_MEM_SEX +" VARCHAR(100) NULL, "
            + DEF_MEM_BIRTH +" VARCHAR(100) NULL, "
            + DEF_MEM_HEIGHT +" VARCHAR(100) NULL, "
            + DEF_MEM_WEIGHT+" VARCHAR(100) NULL, "
            + DEF_MEM_WEIGHT_TARGET+" VARCHAR(100) NULL, "
            + DEF_MEM_HIGH_PRESSURE +" VARCHAR(100) NULL, "
            + DEF_MEM_HYPERLIPIDEMI+" VARCHAR(100) NULL, "
            + DEF_MEM_SUGER_TYPE1+" VARCHAR(100) NULL, "
            + DEF_MEM_SUGER_TYPE2+" VARCHAR(100) NULL, "
            + DEF_MEM_EMPTY+" VARCHAR(100) NULL, "
            + DEF_MEM_DRUG+" VARCHAR(100) NULL, "
            + DEF_MEM_SMOKE+" VARCHAR(100) NULL, "
            + DEF_TARGET_STEP+" VARCHAR(100) NULL, "
            + DEF_TARGET_WATER+" VARCHAR(100) NULL);";
        Logger.i(TAG, "onCreate.sql="+sql);
        return sql;
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    public String upgradeDb() {
        return "DROP TABLE "+TB_BASIC+";";
    }
}
