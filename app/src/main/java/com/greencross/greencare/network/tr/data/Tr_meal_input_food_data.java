package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.database.DBHelperFoodCalorie;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 음식 등록하기
 {
 "api_code": "meal_input_food_data",
 "insures_code": "300",
 "mber_sn": "1000",
 "idx": "77777788820",
 "food_mass": [{
 "foodcode": "101",
 "forpeople": "1",
 "regdate": "201704161007"
 }, {
 "foodcode": "102",
 "forpeople": "1",
 "regdate": "201704161007"
 }]
 }
 */

public class Tr_meal_input_food_data extends BaseData {
    private final String TAG = Tr_meal_input_food_data.class.getSimpleName();

    public static class RequestData {
        public String mber_sn; // 1000
        public String idx; // 1000
        public JSONArray food_mass; // 1000
    }

    public static class RequestArrayData {
        public String foodcode; // 1000
        public String forpeople; // 1000
        public String regdate; // 1000
    }

    public static JSONArray getArray(List<DBHelperFoodCalorie.Data> datas, String regdate) {
        JSONArray array = new JSONArray();

        for (DBHelperFoodCalorie.Data data : datas) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("foodcode", data.food_code); // 102
                obj.put("forpeople", data.forpeople); // 1
                obj.put("regdate", StringUtil.getIntString(regdate)); // 201703301420""},{""idx", ""); // 4

                array.put(obj);
            } catch (JSONException e) {
                Logger.e(e);
            }
        }
        return array;
    }

    public Tr_meal_input_food_data() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_meal_input_food_data.RequestData) {
            JSONObject body = new JSONObject();
            Tr_meal_input_food_data.RequestData data = (Tr_meal_input_food_data.RequestData) obj;

            body.put("api_code", getApiCode(TAG)); //
            body.put("insures_code", INSURES_CODE);
            body.put("mber_sn", data.mber_sn); //
            body.put("idx", data.idx); //
            body.put("food_mass", data.food_mass); //
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
    @SerializedName("mber_sn")
    public String mber_sn; //
    @SerializedName("meal_sn")
    public String meal_sn; //
    @SerializedName("reg_yn")
    public String reg_yn; //

}
