package com.greencross.greencare.bluetooth.model;

import java.util.Date;

/**
 * Created by jongrakmoon on 2017. 3. 3..
 */

public class BloodModel extends BaseModel {

    public static final int FLAG_SUGAR_HIGH = 1;
    public static final int FLAG_SUGAR_LOW = -1;

    public static final String INPUT_TYPE_DEVICE = "D";
    public static final String INPUT_TYPE_U = "U";

    private int sequenceNumber;
    private Date time;
    private String idx = "";
    private float sugar;
    private int hiLow;
    private String medicenName = "";
    private int eatType;
    private String before = "";
    private String regtype = "";
    private String regTime = "";

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public float getSugar() {
        return sugar;
    }

    public void setSugar(float sugar) {
        this.sugar = sugar;
    }

    public boolean isSugarHigh() {
        return hiLow == FLAG_SUGAR_HIGH;
    }

    public boolean isSugarLow() {
        return hiLow == FLAG_SUGAR_LOW;
    }

    public void setHiLow(int hiLow) {
        this.hiLow = hiLow;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getEatType() {
        return eatType;
    }

    public void setEatType(int eatType) {
        this.eatType = eatType;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getRegtype() {
        return regtype;
    }

    public void setRegtype(String regtype) {
        this.regtype = regtype;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public int getHiLow() {
        return hiLow;
    }

    public String getMedicenName() {
        return medicenName;
    }

    public void setMedicenName(String medicenName) {
        this.medicenName = medicenName;
    }

}
