package com.greencross.greencare.bluetooth.model;

/**
 * Created by jongrakmoon on 2017. 3. 3..
 */

public class WeightModel extends BaseModel {
    private float weight;
    private float fat;
    private float bodyWater;
    private float muscle;
    private float bmr;
    private float obesity;
    private float bone;
    private int heartRate;
    private float bdwgh_goal;

    private String idx;
    private String regType;
    private String regDate;

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getBodyWater() {
        return bodyWater;
    }

    public void setBodyWater(float bodyWater) {
        this.bodyWater = bodyWater;
    }

    public float getMuscle() {
        return muscle;
    }

    public void setMuscle(float muscle) {
        this.muscle = muscle;
    }

    public float getBmr() {
        return bmr;
    }

    public void setBmr(float bmr) {
        this.bmr = bmr;
    }

    public float getObesity() {
        return obesity;
    }

    public void setObesity(float obesity) {
        this.obesity = obesity;
    }

    public float getBone() {
        return bone;
    }

    public void setBone(float bone) {
        this.bone = bone;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public float getBdwgh_goal() {
        return bdwgh_goal;
    }

    public void setBdwgh_goal(float bdwgh_goal) {
        this.bdwgh_goal = bdwgh_goal;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getRegType() {
        return regType;
    }

    public void setRegType(String regType) {
        this.regType = regType;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }
}