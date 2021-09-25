package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 // 건강메세지 등록하기 
 case "infra_message_write":

 try
 {
 json = @"{   ""api_code"; //"infra_message_write"",   ""insures_code"; //"300"",   ""idx"; //"12121212"" ,""mber_sn"; //"1000"" ,""infra_message"; //"메세지 내용입니다.""}";

 get_join = SK_MOBILE_CALL.SK_mobile_set.infra_message_write(json, insures_code, api_code);
 }
 catch (Exception ex)
 {
 ex.ToString();
 }

 break;
 */

public class Tr_infra_message_write extends BaseData {
    private final String TAG = Tr_infra_message_write.class.getSimpleName();

    public static class RequestData {

        public String idx; //12121212",
        public String mber_sn; //1000",
        public String infra_message; //메세지 내용입니다."
    }

    public Tr_infra_message_write() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_infra_message_write.RequestData) {
            JSONObject body = new JSONObject();
            Tr_infra_message_write.RequestData data = (Tr_infra_message_write.RequestData) obj;
            body.put("api_code", getApiCode(TAG));
            body.put("insures_code", INSURES_CODE);

            body.put("idx", data.idx); // 1
            body.put("mber_sn", data.mber_sn); // 1000
            body.put("infra_message", data.infra_message); // 19750223

            return body;
        }

        return super.makeJson(obj);
    }

    /**************************************************************************************************/
    /***********************************************RECEIVE********************************************/
    /**************************************************************************************************/


    @SerializedName("api_code")
    public String api_code;
    @SerializedName("insures_code")
    public String insures_code;
    @SerializedName("mber_sn")
    public String mber_sn;
    @SerializedName("reg_yn")
    public String reg_yn;



}
