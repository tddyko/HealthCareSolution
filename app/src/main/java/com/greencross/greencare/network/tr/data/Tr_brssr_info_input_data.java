package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.bluetooth.model.PressureModel;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	brssr_info_input_data	혈압 data 넣는 부분 자동, 수동 값을 넣는다.

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원키값
 키
 ast_mass		1차배열 안에서( 여러개의가 돌아간다)
 idx	2차배열 loop	앱에 저장된 고유키값
 arterialPressure	2차배열 loop	동맥압력
 diastolicPressure	2차배열 loop	이완기혈압
 pulseRate	2차배열 loop	맥박속도
 systolicPressure	2차배열 loop	수축기압력
 drugname	2차배열 loop	약이름
 regtype	2차배열 loop	D:디바이스 U:직접입력
 regdate	2차배열 loop

 json = @"{   ""api_code"": ""brssr_info_input_data"",   ""insures_code"": ""300"",  ""mber_sn"": ""1000""   ,""ast_mass"":[{""idx"":""1"",""arterialPressure"":""100"",""diastolicPressure"":""50"",""pulseRate"":""0"",""systolicPressure"":""30"",""drugname"":""혈압약"",""regtype"":""D"",""regdate"":""201703301420""  } ,{""idx"":""2"",""arterialPressure"":""10"",""diastolicPressure"":""50"",""pulseRate"":""0"",""systolicPressure"":""30"",""drugname"":""약이름"",""regtype"":""U"",""regdate"":""201703301420""   } ,{""idx"":""4"",""arterialPressure"":""120"",""diastolicPressure"":""20"",""pulseRate"":""1"",""systolicPressure"":""30"",""drugname"":""약이름"",""regtype"":""D"",""regdate"":""201703301420""   }] }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 reg_yn		등록여부

 */

public class Tr_brssr_info_input_data extends BaseData {
    private final String TAG = Tr_brssr_info_input_data.class.getSimpleName();

    public static class RequestData {
        public String mber_sn; // 1000
        public JSONArray ast_mass;
    }

    public JSONArray getArray(PressureModel pressureModel) {
        JSONArray array = new JSONArray();
            JSONObject obj = new JSONObject();
            try {
                obj.put("idx", pressureModel.getIdx()); // 2
                obj.put("arterialPressure", ""+(int)pressureModel.getArterialPressure()); // 10
                obj.put("diastolicPressure", ""+(int)pressureModel.getDiastolicPressure()); // 50
                obj.put("pulseRate", ""+(int)pressureModel.getPulseRate()); // 0
                obj.put("systolicPressure", ""+(int)pressureModel.getSystolicPressure()); // 30
                obj.put("drugname", pressureModel.getDrugname()); // 약이름
                obj.put("regtype", pressureModel.getRegtype()); // U
                obj.put("regdate", pressureModel.getRegdate());
                
                array.put(obj);
            } catch (JSONException e) {
                Logger.e(e);
            }

        return array;
    }

    public Tr_brssr_info_input_data() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_brssr_info_input_data.RequestData) {
            JSONObject body = new JSONObject();
            Tr_brssr_info_input_data.RequestData data = (Tr_brssr_info_input_data.RequestData) obj;
            body.put("api_code", getApiCode(TAG)); // bdsg_info_input_data
            body.put("mber_sn", data.mber_sn); //  1000
            body.put("insures_code", INSURES_CODE);
            body.put("ast_mass",  data.ast_mass); //
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
