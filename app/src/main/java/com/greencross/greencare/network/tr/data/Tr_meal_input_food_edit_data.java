package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * 음식 수정하기
 *
 {
 "api_code": "meal_input_food_edit_data",
 "insures_code": "300",
 "mber_sn": "1000",
 "idx": "7777778888",
 "foodcode": "102",
 "forpeople": "3"
 }

 */

public class Tr_meal_input_food_edit_data extends BaseData {
	private final String TAG = Tr_meal_input_food_edit_data.class.getSimpleName();


	public static class RequestData {
          public String mber_sn; // 1000",
          public String idx; // 77777788812",
          public String foodcode; // 10",
          public String forpeople; // b",
	}

	public Tr_meal_input_food_edit_data() throws JSONException {
		super.conn_url = BaseUrl.COMMON_URL;
	}

	@Override
	public JSONObject makeJson(Object obj) throws JSONException {
		if (obj instanceof RequestData) {
			JSONObject body = new JSONObject();//getBaseJsonObj("login_id");

			RequestData data = (RequestData) obj;
            body.put("api_code", getApiCode(TAG));
            body.put("insures_code", INSURES_CODE);
            body.put("mber_sn", data.mber_sn ); // 1000",
            body.put("idx", data.idx); // 77777788812",
            body.put("foodcode", data.foodcode ); //
            body.put("forpeople", data.forpeople ); //

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
    @SerializedName("mber_sn")
    public String mber_sn;
    @SerializedName("reg_yn")
    public String reg_yn;

}
