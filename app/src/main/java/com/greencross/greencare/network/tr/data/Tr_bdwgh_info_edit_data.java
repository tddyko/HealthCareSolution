package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 // 혈당 data DATA 수정하기

 case "bdsg_info_edit_data":
 //sugar 혈당값 Str 7
 hiLow 임시 Str 7 이후
 before 0:식전 1:식후
 {
 "api_code": "bdsg_info_edit_data",
 "insures_code": "300",
 "mber_sn": "1000",
 "ast_mass": [
 {
 "idx": "1",
 "sugar": "13300",
 "hiLow": "50",
 "before": "0",
 "drugname": "약이름",
 "regtype": "D",
 "regdate": "201703301420"
 }
 ]
 }
 */

public class Tr_bdwgh_info_edit_data extends BaseData {
    private final String TAG = Tr_bdwgh_info_edit_data.class.getSimpleName();

    public static class RequestData {

        public String mber_sn;
        public JSONArray ast_mass;

    }


    public Tr_bdwgh_info_edit_data() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_bdwgh_info_edit_data.RequestData) {
            JSONObject body = new JSONObject();
            Tr_bdwgh_info_edit_data.RequestData data = (Tr_bdwgh_info_edit_data.RequestData) obj;

            body.put("api_code", getApiCode(TAG) ); //
            body.put("insures_code", INSURES_CODE);
            body.put("mber_sn", data.mber_sn); //  1000
            body.put("ast_mass",  data.ast_mass); //


            return body;
        }

        return super.makeJson(obj);
    }

    public JSONArray getArray(Tr_get_hedctdata.DataList dataList) {
        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();
        try {
            obj.put("idx" , dataList.idx ); //170410173713859",
            obj.put("bmr", dataList.bmr );
            obj.put("bone", dataList.bone );
            obj.put("heartRate", dataList.heartrate );
            obj.put("weight" , dataList.weight ); //체중",
            obj.put("fat" , dataList.fat ); //살",
            obj.put("muscle" , dataList.muscle ); //근육",
            obj.put("obesity" , dataList.obesity ); // 비만
            obj.put("bodyWater" , dataList.bodywater ); // 수분량
            obj.put("regtype" , dataList.regtype ); //D"
            obj.put("regdate" , dataList.reg_de ); //201703301420"

            array.put(obj);
        } catch (JSONException e) {
            Logger.e(e);
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
