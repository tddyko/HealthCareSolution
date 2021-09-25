package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/* 건강메세지 삭제하기 가져오기 20170601
                case "infra_del_message":

                        try
                        {
                        json = @"{   ""api_code"": ""infra_del_message"",   ""insures_code"": ""300"",   ""app_code"": ""android"" ,""mber_sn"": ""1000"" ,""message_sn"": ""100""}";

        get_join = SK_MOBILE_CALL.SK_mobile_set.infra_del_message(json, insures_code, api_code);
        }
        catch (Exception ex)
        {
        ex.ToString();
        }

        break;
*/

public class Tr_infra_del_message extends BaseData {
    private final String TAG = Tr_infra_del_message.class.getSimpleName();

    public static class RequestData {

        public String mber_sn; //12121212",
        public String idx; //1000",
    }

    public Tr_infra_del_message() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_infra_del_message.RequestData) {
            JSONObject body = new JSONObject();
            Tr_infra_del_message.RequestData data = (Tr_infra_del_message.RequestData) obj;
            body.put("api_code", getApiCode(TAG));
            body.put("insures_code", INSURES_CODE);
            body.put("app_code", APP_CODE);

            body.put("mber_sn", data.mber_sn); // 1
            body.put("idx", data.idx); // 1000

            return body;
        }

        return super.makeJson(obj);
    }

    /**************************************************************************************************/
    /***********************************************RECEIVE********************************************/
    /**************************************************************************************************/


    @SerializedName("api_code")
    public String api_code;
    @SerializedName("data_yn")
    public String data_yn;

}
