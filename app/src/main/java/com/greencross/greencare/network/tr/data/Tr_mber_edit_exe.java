package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	mber_edit_exe	회원정보 수정

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원키값
 token		토큰
 app_code		안드로이드 버전
 mber_id		아이디
 mber_pwd		비밀번호
 mber_hp		휴대폰
 mber_nm		이름


 json = @"{   ""api_code"": ""mber_edit_exe"",   ""insures_code"": ""300"",""mber_sn"": ""1000"", ""token"": ""deviceToken"",  ""app_code"": ""android4.2"" , ""mber_id"": ""tjhong@gchealthcare.com"" , ""mber_pwd"": """",""mber_hp"": ""01085842344"",""mber_nm"": ""닉네임_지구"" }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원키값
 reg_yn		Y

 */

public class Tr_mber_edit_exe extends BaseData {
    private final String TAG = Tr_mber_edit_exe.class.getSimpleName();

    public static class RequestData {
        public String mber_sn;       //  회원키값
        public String mber_id;       //  아이디
        public String mber_pwd;      //  비밀번호
        public String mber_hp;       //  휴대폰
        public String mber_nm;       //  이름
        public String mber_zone;       //  지역
    }

    public Tr_mber_edit_exe() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_mber_edit_exe.RequestData) {
            JSONObject body = new JSONObject();
            Tr_mber_edit_exe.RequestData data = (Tr_mber_edit_exe.RequestData) obj;

            body.put("api_code", getApiCode(TAG) );
            body.put("insures_code", INSURES_CODE);
            body.put("token", DEVICE_TOKEN);
            body.put("app_code", APP_CODE);

            body.put("mber_sn", data.mber_sn );

            body.put("mber_id", data.mber_id );
            body.put("mber_pwd", data.mber_pwd );
            body.put("mber_hp", data.mber_hp );
            body.put("mber_nm", data.mber_nm );
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
