package com.greencross.greencare.bluetooth.model;

import java.util.Date;

/**
 * Created by jongrakmoon on 2017. 3. 3..
 */

public class PressureModel extends BaseModel {

    private Date time;
    private float systolicPressure;
    private float diastolicPressure;
    private float arterialPressure;
    private float pulseRate;
    private String drugname = "";

    // 데이터 베이스 저장용 필드
    private String idx = "";
    private String regtype = "";
    private String regdate = "";

    public float getSystolicPressure() {
        return systolicPressure;
    }

    public void setSystolicPressure(float systolicPressure) {
        this.systolicPressure = systolicPressure;
    }

    public float getDiastolicPressure() {
        return diastolicPressure;
    }

    public void setDiastolicPressure(float diastolicPressure) {
        this.diastolicPressure = diastolicPressure;
    }

    public float getArterialPressure() {
        return arterialPressure;
    }

    public void setArterialPressure(float arterialPressure) {
        this.arterialPressure = arterialPressure;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public float getPulseRate() {
        return pulseRate;
    }

    public void setPulseRate(float pulseRate) {
        this.pulseRate = pulseRate;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getDrugname() {
        return drugname;
    }

    public void setDrugname(String drugname) {
        this.drugname = drugname;
    }

    public String getRegtype() {
        return regtype;
    }

    public void setRegtype(String regtype) {
        this.regtype = regtype;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

}