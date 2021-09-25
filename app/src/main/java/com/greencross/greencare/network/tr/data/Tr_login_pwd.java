package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 FUNCTION NAME	login_pwd	비밀번호찾기

 Input
 변수명	FUNCTION NAME 	설명
 api_code	 	api 코드명 string
 insures_code	 	회원사 코드
 token	 	디바이스 토큰
 mber_id	 	아이디 넣기




 json = @"{   "api_code": "login_pwd",   "insures_code": "300", "token": "deviceToken",   "mber_id": "tjhong@gchealthcare.com" }";
 Output
 변수명	 	설명
 api_code	 	api 코드명 string
 insures_code	 	회원사 코드
 SEND_MAIL_YN	 	메일 발송(Y)

 */

public class Tr_login_pwd extends BaseData {
	private final String TAG = Tr_login_pwd.class.getSimpleName();


	public static class RequestData {
		public String mber_hp;
		public String mber_id;
	}

	public Tr_login_pwd() throws JSONException {
		super.conn_url = BaseUrl.COMMON_URL;
	}

	@Override
	public JSONObject makeJson(Object obj) throws JSONException {
		if (obj instanceof RequestData) {
			JSONObject body = new JSONObject();//getBaseJsonObj("login_id");

			RequestData data = (RequestData) obj;
            body.put("api_code", "login_pwd");
            body.put("insures_code", INSURES_CODE);
            body.put("token", DEVICE_TOKEN);
            body.put("mber_hp", data.mber_hp);
            body.put("mber_id", data.mber_id);

			return body;
		}

		return super.makeJson(obj);
	}

    /**************************************************************************************************/
    /***********************************************RECEIVE********************************************/
    /**************************************************************************************************/

	@SerializedName("api_code")        //		api 코드명 string
	public String api_code;        //		api 코드명 string
	@SerializedName("insures_code")//		회원사 코드
	public String insures_code;    //		회원사 코드
	@SerializedName("send_mail_yn")
	public String send_mail_yn;

}
