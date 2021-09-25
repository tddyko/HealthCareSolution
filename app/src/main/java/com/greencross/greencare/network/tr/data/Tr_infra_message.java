package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 FUNCTION NAME	infra_message	건강메세지 가져오기

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원 키값
 pageNumber		1번째 페이지 2번재 페이지

 json = @"{   "api_code": "infra_message",   "insures_code": "300",   "app_code": "android" ,"mber_sn": "1000" ,"pageNumber": "1"}";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 message_sn		메세제 일련번호]
 idx            메시지 정보를 보낸 idx값날짜)
 message_cn		내용
 message_de		날짜

 */

public class Tr_infra_message extends BaseData {
    private final String TAG = Tr_infra_message.class.getSimpleName();

    public Tr_infra_message() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    public static class RequestData {
        public String insusres_code; // 300
        public String mber_sn; // 1000
        public String pageNumber; // 1"
    }


    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_infra_message.RequestData) {
            JSONObject body = new JSONObject();
            Tr_infra_message.RequestData data = (Tr_infra_message.RequestData) obj;
            body.put("api_code", getApiCode(TAG) ); //
            body.put("insures_code", INSURES_CODE);
            body.put("app_code", APP_CODE);
            body.put("mber_sn", data.mber_sn);
            body.put("pageNumber", data.pageNumber);

            return body;
        }

        return super.makeJson(obj);
    }


    /**************************************************************************************************/
    /***********************************************RECEIVE********************************************/
    /**************************************************************************************************/

    @SerializedName("api_code")
    public String api_code; //		api 코드명 string
    @SerializedName("pageNumber")
    public String pageNumber; //
    @SerializedName("maxpageNumber")
    public String maxpageNumber; //
    @SerializedName("message_list")
    public List<MessageList> message_list = new ArrayList<MessageList>();

   public class MessageList extends BaseData {
        @SerializedName("message_sn") // 100",
        public String message_sn; // 100",
        @SerializedName("idx") // 20170518173322",
        public String idx; // 20170518173322",
        @SerializedName("message_cn") // 안녕하세요 건강메세지",
        public String message_cn; // 안녕하세요 건강메세지",
        @SerializedName("message_de") // 201703311608"
        public String message_de; // 201703311608"
   }

}
