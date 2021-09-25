package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	mber_reg_check_hp	휴대폰 중복 확인

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 token		디바이스 토큰
 mber_hp		휴대폰번호




 json = @"{   "api_code": "mber_reg_check_hp",   "insures_code": "300", "token": "deviceToken",   "mber_hp": "01085842542"  }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 MBER_HP		휴대폰번호
 MBER_HP_YN		(중복 Y) N 일때만 (회원가입

 */

public class Tr_mber_reg_check_hp extends BaseData {
    private final String TAG = Tr_mber_reg_check_hp.class.getSimpleName();

    public static class RequestData {
       public String mber_hp; //		휴대폰번호 넣기
    }

    public Tr_mber_reg_check_hp() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_mber_reg_check_hp.RequestData) {
            Tr_mber_reg_check_hp.RequestData data = (Tr_mber_reg_check_hp.RequestData) obj;
            JSONObject body = new JSONObject();
            body.put("api_code", getApiCode(TAG));
            body.put("insures_code", INSURES_CODE);
            body.put("token", DEVICE_TOKEN);
            body.put("mber_hp", data.mber_hp);

            return body;
        }

        return super.makeJson(obj);
    }

    /**************************************************************************************************/
    /***********************************************RECEIVE********************************************/
    /**************************************************************************************************/

    @SerializedName("api_code")//		api 코드명 string
    public String api_code;//		api 코드명 string
    @SerializedName("insures_code")//		회원사 코드
    public String insures_code;//		회원사 코드
    @SerializedName("mber_hp")//		아이디값
    public String mber_hp;//		아이디값
    @SerializedName("mber_hp_yn")//		휴대폰번호
    public String mber_hp_yn;//		(중복 Y) , N 일때만 (회원가입)

}
