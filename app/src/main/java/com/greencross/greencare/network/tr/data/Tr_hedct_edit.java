package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	hedct_edit	장치닉네임 및 장치삭제건에 대하여

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원키값
 hedct_sn		키
 hedct_nm		이름
 hedct_yn		등록여부


 json = @"{   ""api_code"": ""hedct_edit"",   ""insures_code"": ""300"",  ""mber_sn"": ""1000"" ,""hedct_sn"": ""1000002"",""hedct_nm"": ""장치이름"",""hedct_yn"": ""Y""}";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 edit_yn		수정여부


 */

public class Tr_hedct_edit extends BaseData {
    private final String TAG = Tr_hedct_edit.class.getSimpleName();

    public static class RequestData {
        public String mber_sn; // 1000
        public String hedct_sn; // 1000002
        public String hedct_nm; // 장치이름
        public String hedct_yn; // Y

    }

    public Tr_hedct_edit() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_hedct_edit.RequestData) {

            JSONObject body = getBaseJsonObj("hedct_edit");

            Tr_hedct_edit.RequestData data = (Tr_hedct_edit.RequestData) obj;
            body.put("mber_sn", data.mber_sn); //
            body.put("hedct_sn", data.hedct_sn); //
            body.put("hedct_nm", data.hedct_nm); //
            body.put("hedct_yn", data.hedct_yn); //

            return body;
        }

        return super.makeJson(obj);
    }

    /**************************************************************************************************/
    /***********************************************RECEIVE********************************************/
    /**************************************************************************************************/

    @SerializedName("api_code")
    public String api_code; //
    @SerializedName("insures_code")
    public String insures_code; //
    @SerializedName("edit_yn")
    public String edit_yn; //

}
