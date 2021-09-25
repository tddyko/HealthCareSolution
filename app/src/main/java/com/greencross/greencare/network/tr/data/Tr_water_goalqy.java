package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.data;

/**
 FUNCTION NAME	water_goalqy	물 목표량 // 섭취량

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원 키값
 goal_water_goalqy		물 목표량
 goal_water_ntkqy		물 섭취량




 json = @"{   ""api_code"": ""mvm_goalqy"",  ""insures_code"": ""300"",  ""mber_sn"": ""1000"" , ""goal_water_goalqy"": ""4000""  , ""goal_water_ntkqy"": ""1000""    }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 reg_yn		등록여부

 */

public class Tr_water_goalqy extends BaseData {
    private final String TAG = Tr_water_goalqy.class.getSimpleName();

    public static class RequestData {
        public String mber_sn; // 1000
        public String insures_code; // 300
        public String goal_water_goalqy; // 4000
        public String goal_water_ntkqy; // 1000
    }

    public Tr_water_goalqy() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, ".makeJsonobj=" + obj);
        if (obj instanceof Tr_water_goalqy.RequestData) {
            JSONObject body = new JSONObject();

            Tr_water_goalqy.RequestData data = (Tr_water_goalqy.RequestData) obj;
            body.put("api_code", "water_goalqy"); // bdsg_info_input_data
            body.put("insures_code", INSURES_CODE); // 300
            body.put("mber_sn", data.mber_sn); //  1000
            body.put("goal_water_goalqy", data.goal_water_goalqy); // 300
            body.put("goal_water_ntkqy",  data.goal_water_ntkqy); //
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
