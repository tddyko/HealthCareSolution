package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * FUNCTION NAME	getInformation	환경설정 가져오기
 * <p>
 * Input
 * 변수명	FUNCTION NAME 	설명
 * api_code	 	api 코드명 string
 * app_code	 	iOS, android
 * insures_code	 	회원사 코드
 * <p>
 * json = @"{ "api_code": "getInformation"  , "app_code": "android"  ,"insures_code": "300"  }";
 * json = @"{ "api_code": "getInformation"  , "app_code": "ios"  ,"insures_code": "300"  }";
 * <p>
 * Output
 * 변수명	 	설명
 * api_code	 	api 코드명 string
 * insures_code	 	회원사코드
 * CMPNY_NM	 	회원사명
 * CMPNY_ARS	 	콜센터 번호
 * apiURL	 	JSON 호출 URL
 * appVersion	 	현재 버전
 * updateURL	 	업데이트 URL
 */

public class Tr_get_infomation extends BaseData {
    private final String TAG = Tr_get_infomation.class.getSimpleName();

    public static class RequestData {
        public String insures_code;    // 	회원사 코드
        public String token; //	디바이스 토큰
    }

    public Tr_get_infomation() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_get_infomation.RequestData) {
            JSONObject body = new JSONObject();
            body.put("app_code", APP_CODE);
            body.put("api_code", "getInformation");


            Tr_get_infomation.RequestData data = (Tr_get_infomation.RequestData) obj;
            body.put("insures_code", INSURES_CODE);
            body.put("token", data.token);

            return body;
        }

        return super.makeJson(obj);
    }

    private JSONArray getArray(String... arr) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < arr.length; i++) {
            JSONObject arrObj = new JSONObject();
            try {
                arrObj.put("mssnName", "");    // 미션명
                arrObj.put("mssnPoint", "");     // 미션코드
                array.put(arrObj);
            } catch (JSONException e) {
                Logger.e(e);
            }
        }

        return array;
    }

    /**************************************************************************************************/
    /***********************************************RECEIVE********************************************/
    /**************************************************************************************************/

    @SerializedName("api_code") // : "android",
    public String api_code;//: "android",
    @SerializedName("insures_code") // : "300",
    public String insures_code; // ": "300",
    @SerializedName("CMPNY_NM") //: "SK 기반서비스",
    public String CMPNY_NM; // ": "SK 기반서비스",
    @SerializedName("CMPNY_ARS") //: "15887524",
    public String CMPNY_ARS; // ": "15887524",
    @SerializedName("CMPNY_IMAGE") //: "",
    public String CMPNY_IMAGE; // ": "",
    @SerializedName("loginURL") //: "",
    public String loginURL; // ": "",
    @SerializedName("apiURL") //"http://m.shealthcare.co.kr/SK/WebService/SK_Mobile_Call.asmx",
    public String apiURL; // ": "http://m.shealthcare.co.kr/SK/WebService/SK_Mobile_Call.asmx",
    @SerializedName("NOTICE_YN") //: "N",
    public String NOTICE_YN; // ": "N",
    @SerializedName("appVersion") //: "1.13",
    public String appVersion; // ": "1.13",
    @SerializedName("updateURL") //: "market://details?id=com.greencross.watercle"
    public String updateURL; // ": "market://details?id=com.greencross.watercle"

}
