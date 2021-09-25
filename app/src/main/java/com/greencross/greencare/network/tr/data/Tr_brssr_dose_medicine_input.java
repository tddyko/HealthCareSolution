package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	brssr_dose_medicine_input	혈압에 대한 투약 정보 넣기

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원 키값
 idx		앱고유키값
 medicine_nm		약품명
 reg_day		등록일자
 medicine_typ		2: 혈압


 json = @"{   ""api_code"": ""bdsg_dose_medicine_input"",  ""insures_code"": ""300"",  ""mber_sn"": ""1000"" , ""reg_day"": ""201703271420""  , ""medicine_nm"": ""혈압약품""  , ""medicine_typ"": ""2""   }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 reg_yn		등록여부



 */

public class Tr_brssr_dose_medicine_input extends BaseData {
    private final String TAG = Tr_brssr_dose_medicine_input.class.getSimpleName();

    public static class RequestData {
        public String mber_sn; // 1000      // 회원 키값
        public String reg_day; // 201703271420      // 앱고유키값
        public String medicine_nm; // 혈압약품      // 약품명
        public String medicine_typ; // 2        // 등록일자

    }


    public Tr_brssr_dose_medicine_input() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_brssr_dose_medicine_input.RequestData) {

            JSONObject body = getBaseJsonObj("bdsg_dose_medicine_input");

            Tr_brssr_dose_medicine_input.RequestData data = (Tr_brssr_dose_medicine_input.RequestData) obj;
            body.put("mber_sn", data.mber_sn); //  1000
            body.put("reg_day",  data.reg_day); //
            body.put("medicine_nm",  data.medicine_nm); //
            body.put("medicine_typ",  data.medicine_typ); //

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
    @SerializedName("reg_yn")
    public String reg_yn; //

}
