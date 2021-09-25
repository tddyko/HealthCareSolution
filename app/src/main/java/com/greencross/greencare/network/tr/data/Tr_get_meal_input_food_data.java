package com.greencross.greencare.network.tr.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 데이터 가져오기 (음식)
 {
 json={
 "api_code": "get_meal_input_food_data",
 "insures_code": "300",
 "mber_sn": "1000",
 "begin_day": "20170301",
 "end_day": "20170430"
 }

 //////receive
 <?xml version="1.0" encoding="utf-8"?>
 <string xmlns="http://tempuri.org/">{
 "api_code": "get_meal_input_food_data",
 "insures_code": "300",
 "mber_sn": "1000",
 "data_list": [
 {
 "idx": "77777788820",
 "foodcode": "101",
 "forpeople": "1",
 "regdate": "201704161007"
 },
 {
 "idx": "77777788820",
 "foodcode": "102",
 "forpeople": "1",
 "regdate": "201704161007"
 }
 ]
 }</string>
 */

public class Tr_get_meal_input_food_data extends BaseData {
	private final String TAG = Tr_get_meal_input_food_data.class.getSimpleName();


	public static class RequestData {
          public String mber_sn; // 1000",
          public String idx; // 77777788812",
          public String begin_day; // 10",
          public String end_day; // 10",

	}

	public Tr_get_meal_input_food_data() throws JSONException {
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
            body.put("begin_day", data.begin_day ); //
            body.put("end_day", data.end_day ); //

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
    @SerializedName("data_list")
    public List<ReceiveDatas> data_list = new ArrayList<>();

    public class ReceiveDatas implements Parcelable {
        @SerializedName("idx")
        public String idx; // 77777788820",
        @SerializedName("foodcode")
        public String foodcode; // 10",
        @SerializedName("forpeople")
        public String forpeople; // a",
        @SerializedName("regdate")
        public String regdate; // 201704161007"

        protected ReceiveDatas(Parcel in) {
            idx = in.readString();
            foodcode = in.readString();
            forpeople = in.readString();
            regdate = in.readString();
        }

        public  final Creator<ReceiveDatas> CREATOR = new Creator<ReceiveDatas>() {
            @Override
            public ReceiveDatas createFromParcel(Parcel in) {
                return new ReceiveDatas(in);
            }

            @Override
            public ReceiveDatas[] newArray(int size) {
                return new ReceiveDatas[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(idx);
            dest.writeString(foodcode);
            dest.writeString(forpeople);
            dest.writeString(regdate);
        }
    }
}
