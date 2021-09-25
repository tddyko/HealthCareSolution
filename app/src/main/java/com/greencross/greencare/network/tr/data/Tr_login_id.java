package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 FUNCTION NAME	login_id	아이디 찾기

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 app_code		iOS, android
 insures_code		회원사 코드
 token		디바이스 토큰
 mber_hp		휴대폰 번호




 json = @"{   "api_code": "login_id",   "insures_code": "300", "token": "deviceToken",   "mber_hp": "01085842255" }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_id		회원 아이디



 */

public class Tr_login_id extends BaseData {
	private final String TAG = Tr_login_id.class.getSimpleName();


	public static class RequestData {
		public String mber_hp;
	}

	public Tr_login_id() throws JSONException {
		super.conn_url = BaseUrl.COMMON_URL;
	}

	@Override
	public JSONObject makeJson(Object obj) throws JSONException {
		if (obj instanceof RequestData) {
			JSONObject body = new JSONObject();//getBaseJsonObj("login_id");
			RequestData data = (RequestData) obj;

			body.put("api_code", "login_id");
			body.put("insures_code", INSURES_CODE);
			body.put("token", DEVICE_TOKEN);
			body.put("mber_hp", data.mber_hp);

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
	@SerializedName("mber_id")
	public String mber_id;

}
