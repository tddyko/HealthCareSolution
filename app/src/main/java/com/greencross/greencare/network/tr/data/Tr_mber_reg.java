package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	mber_reg	회원가입

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 token		디바이스 토큰
 app_code		os 버전
 mber_id		아이디
 mber_PWD		비밀번호
 mber_hp		휴대폰번호
 mber_nm		이름
 mber_lifyea		생년(19881203)
 mber_height		키
 mber_bdwgh		몸무게
 mber_bdwgh_goal		목표체중
 pushk		푸쉬(정보)
 app_ver		앱버전
 phone_model		휴대폰 모델명
 mber_actqy		활동량  1 (주로 앉아있음) 2,(약간) 3(활동적)
 disease_nm	질환다중, 1,2,3,	질환
 medicine_yn		복용약 여부
 smkng_yn		흡연여부

 json = @"{   ""api_code"": ""mber_reg"",   ""insures_code"": ""300"", ""token"": ""deviceToken"",  ""app_code"": ""iphone"" , ""mber_id"": ""tj222honwg@gchealthcare.2com2"" , ""mber_pwd"": ""tes222tpwd"",""mber_hp"": ""010758421333"",""mber_nm"": ""닉네임6"" ,""mber_sex"": ""1"",""mber_lifyea"": ""19750221"",""mber_height"": ""182"",""mber_bdwgh"": ""79"",""mber_bdwgh_goal"": ""65""  , ""pushk"": ""ET"", ""app_ver"": ""0.28"" ,""phone_model"": ""SM890""   ,""mber_actqy"": ""1"",  ""disease_nm"": ""1,2,3,"",  ""medicine_yn"": ""Y"",  ""smkng_yn"": ""Y""  }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원키값
 reg_yn		Y


 */

public class Tr_mber_reg extends BaseData {
    private final String TAG = Tr_mber_reg.class.getSimpleName();

    public static class RequestData {
        public String mber_id; // tj222honwg@gchealthcare.2com2
        public String mber_pwd; // tes222tpwd
        public String mber_hp; // 010758421333
        public String mber_nm; // 닉네임6
        public String mber_sex; // 1
        public String mber_lifyea; // 19750221
        public String mber_height; // 182
        public String mber_bdwgh; // 79
        public String mber_bdwgh_goal; // 65
        public String pushk; // ET
        public String app_ver; // 0.28
        public String phone_model; // SM890
        public String mber_actqy; // 1
        public String disease_nm; // 1,2,3,
        public String medicine_yn; // Y
        public String smkng_yn; // Y""
        public String mber_zone; // 지역
    }

    public Tr_mber_reg() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_mber_reg.RequestData) {
            Tr_mber_reg.RequestData data = (Tr_mber_reg.RequestData) obj;
            JSONObject body = new JSONObject();
            body.put("api_code", "mber_reg" ); // "mber_reg",
            body.put("insures_code", INSURES_CODE ); // "300",
            body.put("token", DEVICE_TOKEN ); // "deviceToken",
            body.put("app_code", APP_CODE ); // "iphone",
            body.put("mber_id", data.mber_id ); // "tj222honwg@gchealthcare.2com2",
            body.put("mber_pwd", data.mber_pwd ); // "tes222tpwd",
            body.put("mber_hp", data.mber_hp ); // "010758421333",
            body.put("mber_nm", data.mber_nm ); // "닉네임6",
            body.put("mber_sex", data.mber_sex ); // "1",
            body.put("mber_lifyea", data.mber_lifyea ); // "19750221",
            body.put("mber_height", data.mber_height ); // "182",
            body.put("mber_bdwgh", data.mber_bdwgh ); // "79",
            body.put("mber_bdwgh_goal", data.mber_bdwgh_goal ); // "65",
            body.put("pushk", "ET" ); // "ET",
            body.put("app_ver", data.app_ver ); // "0.28",
            body.put("phone_model", data.phone_model ); // "SM890",
            body.put("mber_actqy", data.mber_actqy ); // "1",
            body.put("disease_nm", data.disease_nm ); // "1,2,3,",
            body.put("medicine_yn", data.medicine_yn ); // "Y",
            body.put("smkng_yn", data.smkng_yn ); // "Y"
            body.put("mber_zone", data.mber_zone );

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
