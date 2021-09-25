package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 식사일지 수정하기
 * <p>
 * // 식사일지 (아침,점심,간식,아침간식,점심간식,저녁간식) 등록하기
 * //아침:a,점심 b ,저녁 c , 아침간식 d , 점심간식 e , 저녁간식 f
 * // 식사 수정하기
 * "api_code": "meal_input_edit_data",
 * "insures_code": "300",
 * "mber_sn": "1000",
 * "idx": "77777788812",
 * "amounttime": "10",
 * "mealtype": "b",
 * "calorie": "33111",
 * "regdate": "201704161007"
 * }
 */

public class Tr_meal_input_edit_data extends BaseData {
    private final String TAG = Tr_meal_input_edit_data.class.getSimpleName();

    public static class RequestData {
        public String mber_sn; // 1000",
        public String idx; // 77777788812",
        public String amounttime; // 10",
        public String mealtype; // b",
        public String calorie; // 33111",
        public String regdate; // 201704161007"
        public String picture; // db저장을 위한 값 "
    }

    public Tr_meal_input_edit_data() throws JSONException {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        if (obj instanceof RequestData) {
            JSONObject body = new JSONObject();//getBaseJsonObj("login_id");

            RequestData data = (RequestData) obj;
            body.put("api_code", getApiCode(TAG));
            body.put("insures_code", INSURES_CODE);
            body.put("mber_sn", data.mber_sn); // 1000",
            body.put("idx", data.idx); // 77777788812",
            body.put("amounttime", data.amounttime); // 10",
            body.put("mealtype", data.mealtype); // a",
            body.put("calorie", data.calorie); // 111",
            body.put("regdate", StringUtil.getIntString(data.regdate)); // 201704161007"

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
    @SerializedName("reg_yn")
    public String reg_yn;

}
