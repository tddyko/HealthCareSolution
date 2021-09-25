package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 걸음데이터 수정
 case "mvm_info_edit_data":
 {
 "api_code": "mvm_info_edit_data",
 "insures_code": "300",
 "mber_sn": "1000",
 "ast_mass": [
 {
 "idx": "170410173713859",
 "calorie": "1200",
 "distance": "50",
 "step": "580",
 "heartRate": "150",
 "stress": "50",
 "regtype": "D",
 "regdate": "201703301420"
 }
 ]
 }

 */

public class Tr_mvm_info_edit_data extends BaseData {
    private final String TAG = Tr_mvm_info_edit_data.class.getSimpleName();

    public static class RequestData {

         public String api_code;
         public String insures_code;
         public String mber_sn;
         public JSONArray ast_mass;

    }

    public Tr_mvm_info_edit_data() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_mvm_info_edit_data.RequestData) {
            JSONObject body = new JSONObject();
            Tr_mvm_info_edit_data.RequestData data = (Tr_mvm_info_edit_data.RequestData) obj;

            body.put("api_code", getApiCode(TAG) ); //
            body.put("insures_code", INSURES_CODE);
            body.put("mber_sn", data.mber_sn); //  1000
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
