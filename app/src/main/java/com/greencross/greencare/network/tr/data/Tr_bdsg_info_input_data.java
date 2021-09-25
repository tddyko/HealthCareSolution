package com.greencross.greencare.network.tr.data;

import android.util.SparseArray;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.bluetooth.model.BloodModel;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 FUNCTION NAME	bdsg_info_input_data	혈당 data 넣는 부분 자동, 수동 값을 넣는다.

 Input
 변수명	FUNCTION NAME 	설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 mber_sn		회원키값
 키
     ast_mass		1차배열 안에서( 여러개의가 돌아간다)
         idx	2차배열 loop	앱에 저장된 고유키값
         sugar	2차배열 loop	혈당
         hiLow	2차배열 loop	임시
         before	2차배열 loop	식전: 0 식후:1
         regtype	2차배열 loop	D:디바이스 U:직접입력
         regdate	2차배열 loop



 json = @"{   ""api_code"": ""bdsg_info_input_data"",   ""insures_code"": ""300"",  ""mber_sn"": ""1000""   ,""ast_mass"":[     {""idx"":""1"",""sugar"":""100"",""hiLow"":""50"",""before"":""0"",""drugname"":""약이름"",""regtype"":""D"",""regdate"":""201703301420""  },{""idx"":""2"",""sugar"":""10"",""hiLow"":""50"",""before"":""0"",""drugname"":""약이름"",""regtype"":""U"",""regdate"":""201703301420""   },{""idx"":""4"",""sugar"":""120"",""hiLow"":""20"",""before"":""1"",""drugname"":""약이름"",""regtype"":""D"",""regdate"":""201703301420""   }] }";
 Output
 변수명		설명
 api_code		api 코드명 string
 insures_code		회원사 코드
 reg_yn		등록여부


 */

public class Tr_bdsg_info_input_data extends BaseData {
    private final String TAG = Tr_bdsg_info_input_data.class.getSimpleName();

    public static class RequestData {
        public String api_code; //bdsg_info_input_data",
        public String insures_code; //300",
        public String mber_sn; //1000",
        public String drugname; //1000",
        public JSONArray ast_mass;
    }
    

    public Tr_bdsg_info_input_data() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_bdsg_info_input_data.RequestData) {
            JSONObject body = new JSONObject();
            Tr_bdsg_info_input_data.RequestData data = (Tr_bdsg_info_input_data.RequestData) obj;

            body.put("api_code", TAG.replace("Tr_","") ); //
            body.put("insures_code", INSURES_CODE);
            body.put("mber_sn", data.mber_sn); //  1000
            body.put("ast_mass",  data.ast_mass); //

            return body;
        }

        return super.makeJson(obj);
    }

    public JSONArray getArray(SparseArray<BloodModel> dataModel) {
        JSONArray array = new JSONArray();
        if (dataModel.size() > 0) {
            BloodModel data = dataModel.get(dataModel.keyAt(dataModel.size() - 1));
            JSONObject obj = new JSONObject();
            String medicenName = data.getMedicenName();

            try {
                obj.put("idx" , data.getIdx() ); //  앱에 저장된 고유키값
                obj.put("sugar" , ""+data.getSugar() ); //  "혈당
                obj.put("hiLow" , "" ); //  임시
                obj.put("drugname", medicenName); //
                obj.put("before" , data.getBefore() ); // 식전: 0 식후:1
                obj.put("regtype" , data.getRegtype() ); //
                obj.put("regdate" , data.getRegTime()); //  "임시

                array.put(obj);
            } catch (JSONException e) {
                Logger.e(e);
            }
        }

        return array;
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
