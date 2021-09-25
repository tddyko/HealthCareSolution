package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 <메인화면의 날씨 정보 받아오는 API 추가함>
 json={   "api_code": "get_dust",  "insures_code": "300","dust_nm": "서울","dust_sn": "1" }

 /*Call_water_dust("1","서울");
 Call_water_dust("2", "부산");
 Call_water_dust("3", "대구");
 Call_water_dust("4", "인천");
 Call_water_dust("5", "광주");
 Call_water_dust("6", "대전");
 Call_water_dust("7", "울산");
 Call_water_dust("8", "경기");
 Call_water_dust("9", "강원");
 Call_water_dust("10", "충북");
 Call_water_dust("11", "충남");
 Call_water_dust("12", "전북");
 Call_water_dust("13", "전남");
 Call_water_dust("14", "경북");
 Call_water_dust("15", "경남");
 Call_water_dust("16", “제주”);
 */

public class Tr_get_dust extends BaseData {
	private final String TAG = Tr_get_dust.class.getSimpleName();


	public static class RequestData {
		public String dust_nm;
		public String dust_sn;

	}

	public Tr_get_dust() throws JSONException {
		super.conn_url = BaseUrl.COMMON_URL;
	}

	@Override
	public JSONObject makeJson(Object obj) throws JSONException {
		if (obj instanceof RequestData) {
			JSONObject body = new JSONObject();//getBaseJsonObj("login_id");

			RequestData data = (RequestData) obj;
            body.put("api_code", "get_dust");
            body.put("insures_code", INSURES_CODE);
            body.put("dust_nm", data.dust_nm);
            body.put("dust_sn", data.dust_sn);

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
    @SerializedName("dusn_qy")
    public String dusn_qy;

}
