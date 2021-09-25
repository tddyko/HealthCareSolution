package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 FUNCTION NAME	mber_user_call	개인정보 불러오기

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 token		디바이스 토큰
 mber_hp		휴대폰번호


 json = @"{   ""api_code"": ""mber_user_call"",   ""insures_code"": ""300"", ""mber_sn"": ""1000""  }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_nm		이름
 mber_hp		휴대폰
 mber_id		아이디
 mber_lifyea		생년월일(yymmdd)
 mber_sex		성별
 mber_height		키
 mber_bdwgh		몸무게

 */

public class Tr_mber_user_call extends BaseData {
	private final String TAG = Tr_mber_user_call.class.getSimpleName();


	public static class RequestData {
        public String mber_sn; // 1000""
	}

	public Tr_mber_user_call() throws JSONException {
		super.conn_url = BaseUrl.COMMON_URL;
	}

	@Override
	public JSONObject makeJson(Object obj) throws JSONException {
		if (obj instanceof RequestData) {
			JSONObject body = getBaseJsonObj("mber_user_call");

			RequestData data = (RequestData) obj;
			body.put("mber_sn", data.mber_sn);

			return body;
		}

		return super.makeJson(obj);
	}

    /**************************************************************************************************/
    /***********************************************RECEIVE********************************************/
    /**************************************************************************************************/



    @SerializedName("api_code") // mber_user_call",
    public String api_code; // mber_user_call",
    @SerializedName("insures_code") // 300",
    public String insures_code; // 300",
    @SerializedName("mber_nm") // jstjtskgxj",
    public String mber_nm; // jstjtskgxj",
    @SerializedName("mber_hp") // 01033333333",
    public String mber_hp; // 01033333333",
    @SerializedName("mber_id") // cc@cc.com",
    public String mber_id; // cc@cc.com",
    @SerializedName("mber_lifyea") // 970821",
    public String mber_lifyea; // 970821",
    @SerializedName("mber_sex") // 2",
    public String mber_sex; // 2",
    @SerializedName("mber_height") // 170",
    public String mber_height; // 170",
    @SerializedName("mber_bdwgh") // 55.55",
    public String mber_bdwgh; // 55.55",
    @SerializedName("mber_bdwgh_goal") // 44.44",
    public String mber_bdwgh_goal; // 44.44",
    @SerializedName("mber_actqy") // 2",
    public String mber_actqy; // 2",
    @SerializedName("disease_nm") // 2,4,",
    public String disease_nm; // 2,4,",
    @SerializedName("medicine_yn") // Y",
    public String medicine_yn; // Y",
    @SerializedName("smkng_yn") // N",
    public String smkng_yn; // N",
    @SerializedName("mber_zone") // Y"
    public String mber_zone; // Y"
    @SerializedName("data_yn") // Y"
    public String data_yn; // Y"

}
