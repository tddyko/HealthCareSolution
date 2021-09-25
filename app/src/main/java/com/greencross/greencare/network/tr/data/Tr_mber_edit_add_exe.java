package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	mber_edit_add_exe	문진관련 수정하기

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원키값
 mber_sex		토큰
 mber_lifyea		생년 (19851203)
 mber_height		키
 mber_bdwgh		몸무게
 mber_bdwgh_goal		목표몸무게
 mber_actqy		활동 (1,2,3)
 disease_nm		질환명
 medicine_yn		복용약
 smkng_yn		흡연


 json = @"{   ""api_code"": ""mber_edit_add_exe"",   ""insures_code"": ""300"", ""token"": ""deviceToken"",  ""mber_sn"": ""1000"" ,""mber_sex"": ""1"",""mber_lifyea"": ""19750221"",""mber_height"": ""182"",""mber_bdwgh"": ""79"" ,""mber_bdwgh_goal"": ""65""   ,""mber_actqy"": ""1"",  ""disease_nm"": ""1,2,3,"",  ""medicine_yn"": ""Y"",  ""smkng_yn"": ""Y"" }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원키값
 reg_yn		Y



 */

public class Tr_mber_edit_add_exe extends BaseData {
    private final String TAG = Tr_mber_edit_add_exe.class.getSimpleName();

    public static class RequestData {
        public String mber_sn; // 1000
        public String mber_sex; // 1
        public String mber_lifyea; // 19750221
        public String mber_height; // 182
        public String mber_bdwgh; // 79
        public String mber_bdwgh_goal; // 65
        public String mber_actqy; // 1
        public String disease_nm; // 1,2,3,
        public String medicine_yn; // Y
        public String smkng_yn; // Y""}";

    }

    public Tr_mber_edit_add_exe() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_mber_edit_add_exe.RequestData) {

            JSONObject body = getBaseJsonObj("mber_edit_add_exe");

            Tr_mber_edit_add_exe.RequestData data = (Tr_mber_edit_add_exe.RequestData) obj;
            body.put("mber_sn", data.mber_sn);
            body.put("mber_sex", data.mber_sex);
            body.put("mber_lifyea", data.mber_lifyea);
            body.put("mber_height", data.mber_height);
            body.put("mber_bdwgh", data.mber_bdwgh);
            body.put("mber_bdwgh_goal", data.mber_bdwgh_goal);
            body.put("mber_actqy", data.mber_actqy);
            body.put("disease_nm", data.disease_nm);
            body.put("medicine_yn", data.medicine_yn);
            body.put("smkng_yn", data.smkng_yn);

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
