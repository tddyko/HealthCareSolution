package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 FUNCTION NAME	get_medicine_nm	혈당 및 혈압약 가져오기

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원키값
 get_medicine_typ		1:혈당약 2:혈압약
 begin_day		기간 시작일
 end_day		기간 종료일

 json = @"{   ""api_code"": ""mvm_info_input_data"",   ""insures_code"": ""300"",  ""mber_sn"": ""1000"" ,""ast_mass"":[{""idx"":""1"",""calorie"":""100"",""distance"":""50"",""step"":""580"",""heartRate"":""150"",""stress"":""50"",""regtype"":""D"",""regdate"":""201703301420""  },{""idx"":""2"",""calorie"":""100"",""distance"":""503"",""step"":""510"",""heartRate"":""150"",""stress"":""50"",""regtype"":""U"",""regdate"":""201703301420""  },{""idx"":""4"",""calorie"":""100"",""distance"":""502"",""step"":""584"",""heartRate"":""150"",""stress"":""50"",""regtype"":""D"",""regdate"":""201703301420""  }] }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원 키값
 reg_yn		등록여부



 */

public class Tr_get_medicine_nm extends BaseData {
    private final String TAG = Tr_get_medicine_nm.class.getSimpleName();

    public Tr_get_medicine_nm() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    public static class RequestData {
        public String insures_code; // 300
        public String mber_sn; // 1000
        public String ast_mass; //
    }

    public static class RequestArrData {
        public String idx; // 1
        public String calorie; // 100
        public String distance; // 50
        public String step; // 580
        public String heartRate; // 150
        public String stress; // 50
        public String regtype; // D
        public String regdate; // 201703301420
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_get_medicine_nm.RequestData) {
            JSONObject body = new JSONObject();
            Tr_get_medicine_nm.RequestData data = (Tr_get_medicine_nm.RequestData) obj;
            body.put("api_code", "get_medicine_nm" ); //
            body.put("insures_code", data.insures_code ); //
            body.put("mber_sn", data.mber_sn ); //
            body.put("ast_mass", getArray(new ArrayList<RequestArrData>()) );

            return body;
        }

        return super.makeJson(obj);
    }

    private JSONArray getArray(List<RequestArrData> arr) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject obj = new JSONObject();
            try {
                RequestArrData data = arr.get(i);
                obj.put("idx", data.idx);
                obj.put("calorie", data.calorie);
                obj.put("distance", data.distance);
                obj.put("step", data.step);
                obj.put("heartRate", data.heartRate);
                obj.put("stress", data.stress);
                obj.put("regtype", data.regtype);
                obj.put("regdate", data.regdate);

                array.put(obj);
            } catch (JSONException e) {
                Logger.e(e);
            }
        }

        return array;
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
