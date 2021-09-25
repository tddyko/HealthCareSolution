package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	hedct_uniqueid	활동계, 체중계, 체지방계, 혈압계, 혈당계 등 해당 고유 키값을 가져오기

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn
 mac_addr		장치에 대한 고유키값


 json = @"{   ""api_code"": ""hedct_uniqueid"",   ""insures_code"": ""300"",  ""mber_sn"": ""1000"" ,""mac_addr"": ""fe00dsf52sdfasdf52sdfs""  }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원 키값
 reg_yn		등록여부
 hedct_hist_sn		고유키값(장비고유키값) 유일한값

 */

public class Tr_hedct_unique_id extends BaseData {
    private final String TAG = Tr_hedct_unique_id.class.getSimpleName();

    public static class RequestData {
        public String mber_sn; // 1000
        public String mac_addr; // fe00dsf52sdfasdf52sdfs""}";

    }

    public Tr_hedct_unique_id() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_hedct_unique_id.RequestData) {

            JSONObject body = getBaseJsonObj("hedct_uniqueid");

            Tr_hedct_unique_id.RequestData data = (Tr_hedct_unique_id.RequestData) obj;
            body.put("mber_sn", data.mber_sn);//
            body.put("mac_addr", data.mac_addr);//

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
    @SerializedName("mber_sn")
    public String mber_sn; //
    @SerializedName("reg_yn")
    public String reg_yn; //
    @SerializedName("hedct_hist_sn")
    public String hedct_hist_sn; //

}
