package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**

 FUNCTION NAME	mvm_goalqy	data 목표 수정폼 (소모칼로리, 하루 목표 걸음수)

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn
 goal_mvm_calory		목표 칼로리
 goal_mvm_stepcnt		목표 걸음수

 json = @"{   ""api_code"": ""mvm_goalqy"",  ""insures_code"": ""300"",  ""mber_sn"": ""1000"" , ""goal_mvm_calory"": ""1000""  , ""goal_mvm_stepcnt"": ""1300""   }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원 키값
 reg_yn		등록여부 (목표량 수정여부)

 */

public class Tr_mvm_goalqy extends BaseData {
    private final String TAG = Tr_mvm_goalqy.class.getSimpleName();

    public static class RequestData {
        public String mber_sn; // 1000
        public String goal_mvm_calory; // 1000
        public String goal_mvm_stepcnt; // 1300
    }

    public Tr_mvm_goalqy() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_mvm_goalqy.RequestData) {
            JSONObject body = new JSONObject();
            Tr_mvm_goalqy.RequestData data = (Tr_mvm_goalqy.RequestData) obj;
            body.put("api_code", getApiCode(TAG));
            body.put("insures_code", INSURES_CODE);
            body.put("mber_sn", data.mber_sn);
            body.put("goal_mvm_calory", data.goal_mvm_calory);
            body.put("goal_mvm_stepcnt", data.goal_mvm_stepcnt);
            return body;
        }

        return super.makeJson(obj);
    }

    /**************************************************************************************************/
    /***********************************************RECEIVE********************************************/
    /**************************************************************************************************/

    @SerializedName("api_code")
    public String api_code;
    @SerializedName("insures_code")
    public String insures_code;
    @SerializedName("mber_sn")
    public String mber_sn;
    @SerializedName("reg_yn")
    public String reg_yn;

}
