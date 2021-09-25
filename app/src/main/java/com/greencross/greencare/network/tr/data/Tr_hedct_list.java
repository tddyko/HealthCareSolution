package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	hedct_list	장치현황리스트

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원 키값


 json = @"{   ""api_code"": ""hedct_list"",   ""insures_code"": ""300"",  ""mber_sn"": ""1000""}";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 hedct_sn		장비고유등록고유키값
 hedct_ver		장비버전
 hedct_nm		장비명
 hedct_mac		맥어드레스

 */

public class Tr_hedct_list extends BaseData {
    private final String TAG = Tr_hedct_list.class.getSimpleName();

    public static class RequestData {
        public String mber_sn; //1000

    }

    public Tr_hedct_list() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_hedct_list.RequestData) {

            JSONObject body = getBaseJsonObj("hedct_reg");

            Tr_hedct_list.RequestData data = (Tr_hedct_list.RequestData) obj;
            body.put("mber_sn", data.mber_sn); //

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
    @SerializedName("hedct_sn")
    public String hedct_sn; //
    @SerializedName("hedct_ver")
    public String hedct_ver; //
    @SerializedName("hedct_nm")
    public String hedct_nm; //
    @SerializedName("hedct_mac")
    public String hedct_mac; //

}
