package com.greencross.greencare.network.tr.data;

import com.google.gson.annotations.SerializedName;
import com.greencross.greencare.bluetooth.model.BandModel;
import com.greencross.greencare.network.tr.BaseData;
import com.greencross.greencare.network.tr.BaseUrl;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * FUNCTION NAME	mvm_info_input_data	운동 data 넣는 부분 자동, 수동 값을 넣는다.
 * <p>
 * Input
 * 변수명	FUNCTION NAME 	설명
 * api_code		api 코드명 string
 * insures_code		회원사 코드
 * mber_sn
 * <p>
 * ast_mass		1차배열 안에서( 여러개의가 돌아간다)
 * idx	2차배열 loop	앱에 저장된 고유키값
 * calorie	2차배열 loop	칼로리
 * distance	2차배열 loop	이동거리
 * step	2차배열 loop	걸음수
 * heartRate	2차배열 loop	심장박동
 * stress	2차배열 loop	스트레스
 * regtype	2차배열 loop	D:디바이스 U:직접입력
 * regdate	2차배열 loop
 * <p>
 * json = @"{   ""api_code"": ""mvm_info_input_data"",   ""insures_code"": ""300"",  ""mber_sn"": ""1000"" ,""ast_mass"":[{""idx"":""1"",""calorie"":""100"",""distance"":""50"",""step"":""580"",""heartRate"":""150"",""stress"":""50"",""regtype"":""D"",""regdate"":""201703301420""  },{""idx"":""2"",""calorie"":""100"",""distance"":""503"",""step"":""510"",""heartRate"":""150"",""stress"":""50"",""regtype"":""U"",""regdate"":""201703301420""  },{""idx"":""4"",""calorie"":""100"",""distance"":""502"",""step"":""584"",""heartRate"":""150"",""stress"":""50"",""regtype"":""D"",""regdate"":""201703301420""  }] }";
 * Output
 * 변수명		설명
 * api_code		api 코드명 string
 * insures_code		회원사 코드
 * mber_sn		회원 키값
 * reg_yn		등록여부
 */

public class Tr_mvm_info_input_data extends BaseData {
    private final String TAG = Tr_mvm_info_input_data.class.getSimpleName();

    public static class RequestData {
        public String mber_sn; // 1000
        public JSONArray ast_mass;
    }

    public JSONArray getArray(List<BandModel> dataModel, String regType) {
        JSONArray array = new JSONArray();

        //현재시간.
        SimpleDateFormat sNow = new SimpleDateFormat("HH");
        int nHour = StringUtil.getIntVal(sNow.format(new Date(System.currentTimeMillis())));


//        for (int i = 0; i < dataModel.size(); i++) {
        int i = 0;
        for (BandModel data : dataModel) {
            JSONObject obj = new JSONObject();
            try {
//                BandModel data = dataModel.get(i);
                String idx = data.getIdx() == null ? CDateUtil.getIdxFormatMMddHHmmssSS(new Date(System.currentTimeMillis()), data.getTime()) : data.getIdx();

                int diff = 0;
                if (data.getTime() > nHour) {
                    diff = -1;
                }else if (data.getTime() == nHour){
                    if(dataModel.size() >= 23){
                        if (i == 0)
                            diff = -1;
                    }
                }

//              String regDate = data.getRegDate() == null ? CDateUtil.getForamtyyyyMMddHHmmss(new Date(System.currentTimeMillis())) : data.getRegDate();
                String regDate = data.getRegDate() == null ? CDateUtil.getForamtyyyyMMddHOUR0000(data.getTime(), diff) : data.getRegDate();

                data.setIdx(idx);
                data.setRegDate(regDate);

                obj.put("idx", data.getIdx());//dataModel.getIdx()); // 2
                obj.put("calorie", ""+data.getCalories()); // 100
                obj.put("distance", ""+data.getDistance()); // 503
                obj.put("step", ""+data.getStep()); // 510
                obj.put("heartRate", ""+data.getHeartRate()); // 150
                obj.put("stress", ""+data.getStress()); // 50
                obj.put("regtype", regType); // D:디바이스 U:직접입력
                obj.put("regdate", data.getRegDate()); // 201703301420""},{""idx", ""); // 4

                array.put(obj);
            } catch (JSONException e) {
                Logger.e(e);
            }
            i++;
        }

        return array;
    }

    public Tr_mvm_info_input_data() {
        super.conn_url = BaseUrl.COMMON_URL;
    }

    @Override
    public JSONObject makeJson(Object obj) throws JSONException {
        Logger.i(TAG, "makeJson.obj=" + obj);
        if (obj instanceof Tr_mvm_info_input_data.RequestData) {
            JSONObject body = new JSONObject();
            Tr_mvm_info_input_data.RequestData data = (Tr_mvm_info_input_data.RequestData) obj;

            body.put("api_code", TAG.replace("Tr_","") ); //
            body.put("insures_code", INSURES_CODE);
            body.put("mber_sn", data.mber_sn); //
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
    @SerializedName("mber_sn")
    public String mber_sn; //
    @SerializedName("reg_yn")
    public String reg_yn; //

}
