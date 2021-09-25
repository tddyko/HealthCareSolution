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
 // 혈당 data 삭제하기

 {
 "api_code": "bdsg_info_del_data",
 "insures_code": "300",
 "mber_sn": "1000",
 "ast_mass": [
 {
 "idx": "1"
 }
 ]
 }
 */

public class Tr_brssr_info_del_data extends BaseData {
    private final String TAG = Tr_brssr_info_del_data.class.getSimpleName();

    public static class RequestData {

        public String mber_sn;
        public JSONArray ast_mass;

    }

    public Tr_brssr_info_del_data() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_brssr_info_del_data.RequestData) {
            JSONObject body = new JSONObject();
            Tr_brssr_info_del_data.RequestData data = (Tr_brssr_info_del_data.RequestData) obj;

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
