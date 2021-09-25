package com.greencross.greencare.network.tr.data;

import android.util.SparseArray;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.bluetooth.model.WaterModel;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	water_info_input_data	물 data 넣는 부분 자동, 수동 값을 넣는다.

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원키값
 키
 ast_mass		1차배열 안에서( 여러개의가 돌아간다)
 idx	2차배열 loop	앱에 저장된 고유키값
 amount	2차배열 loop	물 음용량
 regtype	2차배열 loop	D:디바이스 U:직접입력
 regdate	2차배열 loop

 json = @"{   ""api_code"": ""water_info_input_data"",   ""insures_code"": ""300"",  ""mber_sn"": ""1000""   ,""ast_mass"":[{""idx"":""1"",""amount"":""100"" ,""regtype"":""D"",""regdate"":""201703301420""  } ,{ ""idx"":""3"",""amount"":""100"" ,""regtype"":""D"",""regdate"":""201703301420""   } ,{""idx"":""4"",""amount"":""100"" ,""regtype"":""U"",""regdate"":""201703301420""    }] }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 reg_yn		등록여부


 */

public class Tr_water_info_input_data extends BaseData {
    private final String TAG = Tr_water_info_input_data.class.getSimpleName();

    public static class RequestData {
        public String insures_code; // 300
        public String mber_sn; // 1000
        public JSONArray ast_mass; //
    }

    public static class RequestArrData {
        public String idx; // 1
        public String amount; // 100
        public String regtype; // D
        public String regdate; // 201703301420
    }


    public JSONArray getArray(SparseArray<WaterModel> dataModel) {
        JSONArray array = new JSONArray();
        if (dataModel.size() > 0) {
            WaterModel data = dataModel.get(dataModel.keyAt(dataModel.size() - 1));
            JSONObject obj = new JSONObject();
            try {
                obj.put("idx" , data.getIndexTime() ); //  앱에 저장된 고유키값
                obj.put("amount" , data.getAmonut() ); //  앱에 저장된 고유키값
                obj.put("regtype" , data.getRegtype() ); // 저장 타입
                obj.put("regdate" , data.getRegTime() ); //  "임시

                array.put(obj);
            } catch (JSONException e) {
                Logger.e(e);
            }
        }

        return array;
    }

    public Tr_water_info_input_data() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_water_info_input_data.RequestData) {
            JSONObject body = new JSONObject();

            Tr_water_info_input_data.RequestData data = (Tr_water_info_input_data.RequestData) obj;
            body.put("api_code", "water_info_input_data"); // bdsg_info_input_data
            body.put("insures_code", INSURES_CODE); // 300
            body.put("mber_sn", data.mber_sn); //  1000
            body.put("ast_mass", data.ast_mass); //
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
