package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
혈압에 대한 투약정보 넣기

 json={
 "api_code": "bdsg_dose_medicine_input"  ,
 "insures_code": "300"          ,
 "mber_sn": "1012"             ,
 "idx":"1702031256",
 "reg_day": "201703271420"         ,
 "medicine_nm": "혈압약품"          ,
 "medicine_typ": "2"
 }

 */

public class Tr_bdsg_dose_medicine_input extends BaseData {
    private final String TAG = Tr_bdsg_dose_medicine_input.class.getSimpleName();

    public static class RequestData {
        public String api_code; // bdsg_info_input_data
        public String insures_code; // 300
        public String mber_sn; // 1000
        public String ast_mass;
        
        public String idx;
        public String medicine_nm; // 혈압약품"          ,
        public String medicine_typ; // 2"
        public String reg_day; // "201703271420"         ,

    }


    public Tr_bdsg_dose_medicine_input() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_bdsg_dose_medicine_input.RequestData) {
            JSONObject body = new JSONObject();
            Tr_bdsg_dose_medicine_input.RequestData data = (Tr_bdsg_dose_medicine_input.RequestData) obj;

            body.put("api_code", getApiCode(TAG) );
            body.put("insures_code", INSURES_CODE);
            body.put("mber_sn", data.mber_sn);
            body.put("idx", data.idx); //
            body.put("medicine_nm",  data.medicine_nm); // "혈압약품"          ,
            body.put("medicine_typ",  data.medicine_typ); // "2"
            body.put("reg_day",  data.reg_day); // "201703271420"

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
    @SerializedName("mber_sn")
    public String mber_sn; //
    @SerializedName("reg_yn")
    public String reg_yn; //

}
