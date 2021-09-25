package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	mber_reg_check_id	아이디 중복

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 token		디바이스 토큰
 mber_id		아이디 넣기




 json = @"{   "api_code": "mber_reg_check_id",   "insures_code": "300", "token": "deviceToken",  "mber_id": "tjhong@gchealthcare.com"   }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 MBER_ID		아이디값
 MBER_ID_YN		(중복 Y) , N 일때만 (회원가입)


 */

public class Tr_mber_reg_check_id extends BaseData {
    private final String TAG = Tr_mber_reg_check_id.class.getSimpleName();

    public static class RequestData {
       public String mber_id; //		아이디 넣기
    }

    public Tr_mber_reg_check_id() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_mber_reg_check_id.RequestData) {
            JSONObject body = new JSONObject();
            Tr_mber_reg_check_id.RequestData data = (Tr_mber_reg_check_id.RequestData) obj;
            body.put("api_code", "mber_reg_check_id");
            body.put("insures_code", INSURES_CODE);
            body.put("token", DEVICE_TOKEN);
            body.put("mber_id", data.mber_id);

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
    @SerializedName("mber_id")//		아이디값
    public String mber_id;//		아이디값
    @SerializedName("mber_id_yn")//		(중복 Y) , N 일때만 (회원가입)
    public String mber_id_yn;//		(중복 Y) , N 일때만 (회원가입)

}
