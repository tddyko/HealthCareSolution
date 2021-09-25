package com.greencross.greencare.bluetooth.model;

/**
 * Created by jongrakmoon on 2017. 3. 3..
 */

public class MessageModel extends BaseModel {

    private String sugar;
    private String lowpressure;
    private String heightpressure;
    private String weight;

    private String message;

    // 데이터 베이스 저장용 필드
    private String idx = "";

    private String regtype = "";
    private String regdate = "";
    public String getsugar() {
        return sugar;
    }

    public void setSugar(String sugar) {
        this.sugar = sugar;
    }

    public String getLowpressure() {
        return lowpressure;
    }

    public void setLowpressure(String lowpressure) {
        this.lowpressure = lowpressure;
    }

    public String getHeightpressure() {
        return heightpressure;
    }

    public void setHeightpressure(String heightpressure) {
        this.heightpressure = heightpressure;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
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
