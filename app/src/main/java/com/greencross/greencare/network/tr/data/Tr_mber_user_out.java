package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	mber_user_out	개인정보 탈퇴하기

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원키값


 json = @"{   ""api_code"": ""mber_user_call"",   ""insures_code"": ""300"", ""mber_sn"": ""1000""  }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 del_yn		del 여부

 */

public class Tr_mber_user_out extends BaseData {
    private final String TAG = Tr_mber_user_out.class.getSimpleName();

    public static class RequestData {
       public String mber_sn; //		아이디 넣기
    }

    public Tr_mber_user_out() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_mber_user_out.RequestData) {

            JSONObject body = getBaseJsonObj("mber_user_out");

            Tr_mber_user_out.RequestData data = (Tr_mber_user_out.RequestData) obj;
            body.put("mber_id", data.mber_sn);//		아이디 넣기

            return body;
        }

        return super.makeJson(obj);
    }

    /**************************************************************************************************/
    /***********************************************RECEIVE********************************************/
    /**************************************************************************************************/

    @SerializedName("api_code")
    public String api_code; // api 코드명 string
    @SerializedName("insures_code")
    public String insures_code; //  회원사 코드
    @SerializedName("del_yn")
    public String del_yn;   // del 여부

}
