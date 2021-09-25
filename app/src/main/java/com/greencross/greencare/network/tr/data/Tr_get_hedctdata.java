package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 FUNCTION NAME	get_hedctdata	앱 삭제시 관련된 로우  data 가져오기

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원키값
 get_data_typ		타입 get_data_typ 1: 운동      2:혈당    3:혈압    4:체중    5:물data (제일 Low data)
 begin_day		기간 시작일
 end_day		기간 종료일
 json = @"{   ""api_code"": ""get_hedctdata"" ,  ""get_data_typ"": ""5""  ,  ""insures_code"": ""300"",""mber_sn"": ""1000"",""begin_day"": ""20170301"",""end_day"": ""20170330"" }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원 키값

 */

public class Tr_get_hedctdata extends BaseData {
    private final String TAG = Tr_get_hedctdata.class.getSimpleName();

    public static class RequestData {
        public String get_data_typ; // 5
        public String insures_code; // 300
        public String mber_sn; // 1000
        public String begin_day; // 20170301
        public String end_day; // 20170330
    }

    public Tr_get_hedctdata() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_get_hedctdata.RequestData) {
            JSONObject body = new JSONObject();
            Tr_get_hedctdata.RequestData data = (Tr_get_hedctdata.RequestData) obj;
            body.put("api_code", getApiCode(TAG) ); //
            body.put("insures_code", INSURES_CODE ); //
            body.put("mber_sn", data.mber_sn ); //
            body.put("get_data_typ", data.get_data_typ ); //
            body.put("begin_day", data.begin_day ); //
            body.put("end_day", data.end_day ); //

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
    @SerializedName("get_data_typ")
    public String get_data_typ; //
    @SerializedName("data_list")
    public List<DataList> data_list; //


    public static class DataList {
        @SerializedName("idx") // 2",
        public String idx; // 2",
        @SerializedName("distance") // 503",
        public String distance; // 503",
        @SerializedName("regtype") // U",
        public String regtype; // U",
        @SerializedName("reg_de") // 201703301420"
        public String reg_de; // 201703301420"

        @SerializedName("drugname") // 약이름",
        public String drugname;

        /** 1 **/
        @SerializedName("calory") // 100",
        public String calory; // 100",
        @SerializedName("step") // 510",
        public String step; // 510",
        @SerializedName("heartrate") // 150",
        public String heartrate; // 150",
        @SerializedName("stress") // 50",
        public String stress; // 50",

        /** 2 **/
        @SerializedName("sugar") // 100",
        public String sugar; // 100",
        @SerializedName("hilow") // 50",
        public String hilow; // 50",
        @SerializedName("before") // 0",
        public String before; // 0",

        /** 3 혈압 **/
        @SerializedName("arterialPressure") // 10",
        public String arterialPressure;
        @SerializedName("diastolicPressure") // 50",
        public String diastolicPressure;
        @SerializedName("pulseRate") // 0",
        public String pulseRate;
        @SerializedName("systolicPressure") // 30",
        public String systolicPressure;

        /** 4 체지방 **/
        @SerializedName("bmr") // 100",
        public String bmr;
        @SerializedName("bodywater") // 50",
        public String bodywater;
        @SerializedName("bone") // 0",
        public String bone;
        @SerializedName("fat") // 30",
        public String fat;
        @SerializedName("muscle") // 50",
        public String muscle;
        @SerializedName("obesity") // 0",
        public String obesity;
        @SerializedName("weight") // 11",
        public String weight;
        @SerializedName("bdwgh_goal") // 11",
        public String bdwgh_goal;

        /** 5 **/
        @SerializedName("amount") // "100",
        public String amount;

        /** 6 **/
        @SerializedName("hrm") // "100",
        public String hrm;
        /** 7 **/
    }

}
