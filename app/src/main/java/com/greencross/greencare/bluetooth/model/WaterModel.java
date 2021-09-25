package com.greencross.greencare.bluetooth.model;

/**
 * Created by bong_e on 2017. 4. 17..
 */

public class WaterModel extends BaseModel {

    private int sequenceNumber;
    private String indexTime = "";
    private String amount = "";
    private String targetAmount = "";
    private String regtype = "";
    private String regTime = "";

    public String getIndexTime() {
        return indexTime;
    }

    public void setIndexTime(String indexTime) {
        this.indexTime = indexTime;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getAmonut() {
        return amount;
    }

    public void setAmount(String amount) { this.amount = amount; }

    public String getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(String targetAmount) { this.targetAmount = targetAmount; }

    public String getRegtype() { return regtype; }

    public void setRegtype(String regtype) { this.regtype = regtype; }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }
}
