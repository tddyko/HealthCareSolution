package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 FUNCTION NAME	bdwgh_info_input_data	체중 data 넣는 부분 자동, 수동 값을 넣는다.

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원키값
 키
 ast_mass		1차배열 안에서( 여러개의가 돌아간다)
 idx	2차배열 loop	앱에 저장된 고유키값
 bmr	2차배열 loop	기초대사율
 bodyWater	2차배열 loop	체수분
 bone	2차배열 loop	뼈
 fat	2차배열 loop	살
 heartRate	2차배열 loop	심박동수
 muscle		근육
 obesity		비만
 weight		체중
 regtype	2차배열 loop	D:디바이스 U:직접입력
 regdate	2차배열 loop

 json = @"{   ""api_code"": ""bdwgh_info_input_data"",   ""insures_code"": ""300"",  ""mber_sn"": ""1000""   ,""ast_mass"":[{""idx"":""1"",""bmr"":""100"",""bodyWater"":""50"",""bone"":""0"",""fat"":""30"",""heartRate"":""11"",""muscle"":""50"",""obesity"":""0"",""weight"":""11"",""regtype"":""D"",""regdate"":""201703301420""  }, {""idx"":""3"",""bmr"":""200"",""bodyWater"":""150"",""bone"":""10"",""fat"":""230"",""heartRate"":""311"",""muscle"":""51"",""obesity"":""1"",""weight"":""13"",""regtype"":""D"",""regdate"":""201703301420""  }, {""idx"":""4"",""bmr"":""400"",""bodyWater"":""530"",""bone"":""3"",""fat"":""310"",""heartRate"":""141"",""muscle"":""52"",""obesity"":""3"",""weight"":""131"",""regtype"":""D"",""regdate"":""201703301420""  } ] }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 reg_yn		등록여부


 */

public class Tr_bdwgh_info_input_data extends BaseData {
    private final String TAG = Tr_bdwgh_info_input_data.class.getSimpleName();

    public static class RequestData {
        public String insures_code; // 300
        public String mber_sn; // 1000
        public JSONArray ast_mass;
    }


    public JSONArray getArray(List<Tr_get_hedctdata.DataList> datas) {
        JSONArray array = new JSONArray();
        for (Tr_get_hedctdata.DataList data : datas) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("idx", data.idx );
                obj.put("bmr", data.bmr ); 
                obj.put("bodyWater", data.bodywater );
                obj.put("bone", data.bone ); 
                obj.put("fat", data.fat ); 
                obj.put("heartRate", data.heartrate );
                obj.put("muscle", data.muscle ); 
                obj.put("obesity", data.obesity ); 
                obj.put("weight", data.weight ); 
                obj.put("regtype", data.regtype ); 
                obj.put("regdate", data.reg_de );
                obj.put("mber_bdwgh_goal", data.bdwgh_goal );

                array.put(obj);
            } catch (JSONException e) {
                Logger.e(e);
            }
        }

        return array;
    }

    public Tr_bdwgh_info_input_data() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_bdwgh_info_input_data.RequestData) {
            JSONObject body = new JSONObject();

            Tr_bdwgh_info_input_data.RequestData data = (Tr_bdwgh_info_input_data.RequestData) obj;
            body.put("api_code", "bdwgh_info_input_data"); // bdsg_info_input_data
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
