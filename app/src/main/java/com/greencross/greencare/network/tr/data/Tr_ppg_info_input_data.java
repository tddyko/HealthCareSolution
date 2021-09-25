package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.bluetooth.model.BandModel;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

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

public class Tr_ppg_info_input_data extends BaseData {
	private final String TAG = Tr_bdwgh_info_input_data.class.getSimpleName();

	public static class RequestData {
		public String insures_code; // 300
		public String mber_sn; // 1000
		public JSONArray ast_mass;
	}

	public JSONArray getArray(List<BandModel> dataModel, String regType) {
		JSONArray array = new JSONArray();

		for (BandModel data : dataModel) {
			JSONObject obj = new JSONObject();
			try {
				String idx = data.getIdx() == null ? CDateUtil.getIdxFormatMMddHHmmssSS(new Date(System.currentTimeMillis()), data.getTime()) : data.getIdx();
				String regDate = data.getRegDate() == null ? CDateUtil.getForamtyyyyMMddHHmmss(new Date(System.currentTimeMillis())) : data.getRegDate();
				data.setIdx(idx);
				data.setRegDate(regDate);

				obj.put("idx", data.getIdx());
				obj.put("hrm", ""+data.getHrm());
				obj.put("regtype", regType);
				obj.put("regdate", data.getRegDate());

				array.put(obj);
			} catch (JSONException e) {
				Logger.e(e);
			}
		}
		return array;
	}

	public Tr_ppg_info_input_data() {
		super.conn_url = BaseUrl.COMMON_URL;
	}

	@Override
	public JSONObject makeJson(Object obj) throws JSONException {
		Logger.i(TAG, "makeJson.obj=" + obj);
		if (obj instanceof Tr_ppg_info_input_data.RequestData) {
			JSONObject body = new JSONObject();

			Tr_ppg_info_input_data.RequestData data = (Tr_ppg_info_input_data.RequestData) obj;
			body.put("api_code", "ppg_info_input_data"); // ppg_info_input_data
			body.put("insures_code", INSURES_CODE); // 300
			body.put("mber_sn", data.mber_sn); //  1000
			body.put("ast_mass", data.ast_mass); //
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
	@SerializedName("reg_yn")
	public String reg_yn; //

}
