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
 FUNCTION NAME	bdwgh_goal_input	체중값이 0일때 목표체중 값 변경


 case "bdwgh_goal_input":

 try
 {
 json = @"{   ""api_code"": ""bdwgh_goal_input"",   ""insures_code"": ""300"",   ""app_code"": ""android"" ,""mber_sn"": ""1000"" ,""mber_bdwgh_goal"": ""88.66""}";
 get_join = SK_MOBILE_CALL.SK_INFRA_BDWGH.Call_input_goal_data(json, insures_code, api_code);
 }
 catch (Exception ex)
 {
 ex.ToString();
 }

 break;


 */

public class Tr_bdwgh_goal_input extends BaseData {
    private final String TAG = Tr_bdwgh_goal_input.class.getSimpleName();

    public static class RequestData {
        public String insures_code; // 300
        public String mber_sn; // 1000
        public String mber_bdwgh_goal; // 1000
    }


    public JSONArray getArray(Tr_get_hedctdata.DataList dataList) {
        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();
        try {
            obj.put("idx" , dataList.idx ); //170410173713859",
            array.put(obj);
        } catch (JSONException e) {
            Logger.e(e);
        }
//        }

        return array;
    }

    public Tr_bdwgh_goal_input() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_bdwgh_goal_input.RequestData) {
            JSONObject body = new JSONObject();

            Tr_bdwgh_goal_input.RequestData data = (Tr_bdwgh_goal_input.RequestData) obj;
            body.put("api_code", "bdwgh_goal_input"); // Tr_bdwgh_goal_input
            body.put("insures_code", INSURES_CODE); // 300
            body.put("app_code", "android");
            body.put("mber_sn", data.mber_sn); //  회원고유키값
            body.put("mber_bdwgh_goal", data.mber_bdwgh_goal); //  목표체중
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
