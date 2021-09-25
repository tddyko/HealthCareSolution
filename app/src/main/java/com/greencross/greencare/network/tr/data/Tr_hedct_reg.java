package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	hedct_reg	장치등록 hedct_reg

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn
 hedct_hist_sn		hedct_uniqueid 에 등록된 유일한 키값으로 저장시킨다.
 hedct_ver		장비버전
 mac_addr
 hedct_nm
 ostype

 json = @"{   ""api_code"": ""hedct_reg"",   ""insures_code"": ""300"",  ""mber_sn"": ""1000"" ,""hedct_hist_sn"": ""1000001"",""hedct_ver"": ""1.1"" , ""mac_addr"": ""fe00dsf52sdfasdf52sdfs"",""hedct_nm"": ""체중계""  ,""ostype"": ""android""  }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원 키값
 REG_YN		등록여부
 hedct_sn		장비의 고유키값(등록된 키값)
 ostype		안드로이드

 */

public class Tr_hedct_reg extends BaseData {
    private final String TAG = Tr_hedct_reg.class.getSimpleName();

    public static class RequestData {
        public String mber_sn; // 1000
        public String hedct_hist_sn; // 1000001
        public String hedct_ver; // 1.1
        public String mac_addr; // fe00dsf52sdfasdf52sdfs
        public String hedct_nm; // 체중계
        public String ostype; // android
    }

    public Tr_hedct_reg() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_hedct_reg.RequestData) {

            JSONObject body = getBaseJsonObj("hedct_reg");

            Tr_hedct_reg.RequestData data = (Tr_hedct_reg.RequestData) obj;
            body.put("mber_sn", data.mber_sn); //
            body.put("hedct_hist_sn", data.hedct_hist_sn); //
            body.put("hedct_ver", data.hedct_ver); //
            body.put("mac_addr", data.mac_addr); //
            body.put("hedct_nm", data.hedct_nm); //
            body.put("ostype", data.ostype); //

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
    @SerializedName("REG_YN")
    public String REG_YN; //
    @SerializedName("hedct_sn")
    public String hedct_sn; //
    @SerializedName("ostype")
    public String ostype; //

}
