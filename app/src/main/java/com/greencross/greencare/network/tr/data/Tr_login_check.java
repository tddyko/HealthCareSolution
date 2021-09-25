package com.greencross.greencare.network.tr.data;

import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 FUNCTION NAME	logincheck	로그인인증 키값 가져가기

 Input
 변수명	FUNCTION NAME 	설명
 api_code	 	api 코드명 string
 app_code	 	iOS, android
 insures_code	 	회원사 코드
 token	 	디바이스 토큰

 json = @"{   "api_code": "logincheck",  "insures_code": "300", "token": "deviceToken", "app_code": "android4.1" }";
 Output
 변수명	 	설명
 api_code	 	api 코드명 string
 maxpageNumber	 	카운트
 insures_code	 	회원사 코드
 mber_sn	 	회원 키값
 mber_nm	 	회원이름
 */

public class Tr_login_check extends BaseData {
	private final String TAG = Tr_login_check.class.getSimpleName();


	public static class RequestData {
		public String api_code;	// 	api 코드명 string
		public String insures_code;	// 	회원사 코드
		public String token; //	디바이스 토큰
	}

	public Tr_login_check() throws JSONException {
		super.conn_url = BaseUrl.COMMON_URL;
	}

	@Override
	public JSONObject makeJson(Object obj) throws JSONException {
		if (obj instanceof RequestData) {
			JSONObject body = new JSONObject();
			body.put("app_code", "android");

			RequestData data = (RequestData) obj;
			body.put("api_code", data.api_code);
			body.put("insures_code", data.insures_code);
			body.put("token", data.token);

			return body;
		}

		return super.makeJson(obj);
	}

    /**************************************************************************************************/
    /***********************************************RECEIVE********************************************/
    /**************************************************************************************************/

}
