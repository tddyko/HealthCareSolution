package com.greencross.greencare.bluetooth.model;

import com.partron.wearable.band.sdk.core.item.UrbanInfoItem;
import com.partron.wearable.band.sdk.core.item.UrbanInfoSyncItem;
import com.partron.wearable.band.sdk.core.item.UrbanPPGInfoSyncItem;

/**
 * Created by jongrakmoon on 2017. 3. 25..
 */

public class BandModel extends BaseModel {
    private int calories;
    private float distance;
    private int step;
    private int heartRate;
    private int stress;
    private int hour;
    private int minute;
    private int hrm;

    private String idx;
    private String regtype; // U:사용자 직접입력, D:디바이스, G:구글피트니스
    private String regDate;
    
    private int time;
    private long latestTimeMs;

    private String serialVersionUID; //  7888357950147579467,

    public BandModel() {
    }

    public BandModel(UrbanInfoItem info) {
        this();
        this.calories = info.getCalories();
        this.distance = info.getDistance();
        this.step = info.getStep();
        this.heartRate = info.getHrm();
        this.stress = info.getStress();
    }
    
    public BandModel(UrbanInfoSyncItem info) {
        this();
        this.calories = info.getCalories();
        this.distance = info.getDistance();
        this.step = info.getStep();
        this.time = info.getTime();

//        this.latestTimeMs = info.getLatestTimeMs();   // (PWB-250 밴드 지원)
    }

    public BandModel(UrbanPPGInfoSyncItem info) {
        this();
        this.hour = info.getHour();
        this.minute = info.getMinute();
        this.hrm = info.getHrm();
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getStress() {
        return stress;
    }

    public void setStress(int stress) {
        this.stress = stress;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getRegtype() {
        return regtype;
    }

    public void setRegtype(String regtype) {
        this.regtype = regtype;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getLatestTimeMs() {
        return latestTimeMs;
    }

    public void setLatestTimeMs(long latestTimeMs) {
        this.latestTimeMs = latestTimeMs;
    }

    public String getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setSerialVersionUID(String serialVersionUID) {
        this.serialVersionUID = serialVersionUID;
    }

    public int getHrm() {
        return hrm;
    }

    public void setHrm(int hrm) {
        this.hrm = hrm;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
