package com.greencross.greencare.bluetooth.manager;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.bluetooth.model.BandModel;
import com.greencross.greencare.bluetooth.model.BloodModel;
import com.greencross.greencare.bluetooth.model.MessageModel;
import com.greencross.greencare.bluetooth.model.PressureModel;
import com.greencross.greencare.bluetooth.model.WaterModel;
import com.greencross.greencare.bluetooth.model.WeightModel;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperMessage;
import com.greencross.greencare.database.DBHelperPPG;
import com.greencross.greencare.database.DBHelperPresure;
import com.greencross.greencare.database.DBHelperStep;
import com.greencross.greencare.database.DBHelperStepRealtime;
import com.greencross.greencare.database.DBHelperSugar;
import com.greencross.greencare.database.DBHelperWater;
import com.greencross.greencare.database.DBHelperWeight;
import com.greencross.greencare.main.BluetoothManager;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.data.Tr_bdsg_dose_medicine_input;
import com.greencross.greencare.network.tr.data.Tr_bdsg_info_input_data;
import com.greencross.greencare.network.tr.data.Tr_bdwgh_goal_input;
import com.greencross.greencare.network.tr.data.Tr_bdwgh_info_input_data;
import com.greencross.greencare.network.tr.data.Tr_brssr_info_input_data;
import com.greencross.greencare.network.tr.data.Tr_get_hedctdata;
import com.greencross.greencare.network.tr.data.Tr_infra_message_write;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.network.tr.data.Tr_mvm_info_input_data;
import com.greencross.greencare.network.tr.data.Tr_ppg_info_input_data;
import com.greencross.greencare.network.tr.data.Tr_water_goalqy;
import com.greencross.greencare.network.tr.data.Tr_water_info_input_data;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.SharedPref;
import com.greencross.greencare.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.greencross.greencare.util.CDateUtil.getForamtyyMMddHHmmssSS;

/**
 * Created by MrsWin on 2017-04-09.
 */

public class DeviceDataUtil {

    Handler mHandler = new Handler();
    /**
     * 걸음 데이터 서버 및 sqlite에 저장
     * @param dataModel
     */
    public void uploadStepData(final BaseFragment baseFragment, final List<BandModel> dataModel) {
        uploadStepData(baseFragment, dataModel, null);
    }

    /**
     * 심박수 데이터 서버 및 sqlite에 저장
     * @param dataModel
     */
    public void uploadPPGData(final BaseFragment baseFragment, final List<BandModel> dataModel) {
        uploadPPGData(baseFragment, dataModel, null);
    }
    /**
     * 서버에 데이터 등록
     * @param baseFragment
     * @param dataModel
     * @param iBluetoothResult
     */
    public void uploadStepData(final BaseFragment baseFragment, final List<BandModel> dataModel, final BluetoothManager.IBluetoothResult iBluetoothResult) {

        Tr_mvm_info_input_data inputData                = new Tr_mvm_info_input_data();
        Tr_login login                                  = Define.getInstance().getLoginInfo();

        Tr_mvm_info_input_data.RequestData requestData  = new Tr_mvm_info_input_data.RequestData();
        requestData.mber_sn     = login.mber_sn;
        requestData.ast_mass    = inputData.getArray(dataModel, "D");

        baseFragment.getData(baseFragment.getContext(), inputData.getClass(), requestData, true, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_mvm_info_input_data) {
                    Tr_mvm_info_input_data data = (Tr_mvm_info_input_data) obj;
                    if ("Y".equals(data.reg_yn)) {
                        registStepDB(baseFragment, dataModel, true);

                        if (iBluetoothResult != null)
                            iBluetoothResult.onResult(true);
                    } else {
                        CDialog.showDlg(baseFragment.getContext(), baseFragment.getContext().getString(R.string.text_regist_fail));
                    }
                }
            }
        }, new ApiData.IFailStep() {
            @Override
            public void fail() {
                registStepDB(baseFragment, dataModel, false);
                if (iBluetoothResult != null)
                    iBluetoothResult.onResult(false);
            }
        });
    }
    /**
     * 서버에 데이터 등록
     * @param baseFragment
     * @param dataModel
     * @param iBluetoothResult
     */
    public void uploadStepRealTimeData(final BaseFragment baseFragment, final BandModel dataModel, final BluetoothManager.IBluetoothResult iBluetoothResult) {

        registStepDB(baseFragment, dataModel, true);
    }
    /**
     * 걸음 실시간 데이터 Sqlite에 저장하기
     * @param baseFragment
     * @param model
     * @param isServerRegist
     */
    private void registStepDB(BaseFragment baseFragment, BandModel model, boolean isServerRegist) {

        DBHelper helper     = new DBHelper(baseFragment.getContext());
        DBHelperStepRealtime db     = helper.getmStepRtimeDb();
        db.insert(model, isServerRegist);
    }

    /**
     * 걸음 데이터 Sqlite에 저장하기
     * @param baseFragment
     * @param dataModel
     * @param isServerRegist
     */
    private void registStepDB(BaseFragment baseFragment, List<BandModel> dataModel, boolean isServerRegist) {
        DBHelper helper     = new DBHelper(baseFragment.getContext());
        DBHelperStep db     = helper.getStepDb();
        db.insert(dataModel, isServerRegist);
    }

    /**
     * 심박수 데이터 Sqlite에 저장하기
     * @param baseFragment
     * @param dataModel
     * @param isServerRegist
     */
    private void registPPGDB(BaseFragment baseFragment, List<BandModel> dataModel, boolean isServerRegist) {
        DBHelper helper     = new DBHelper(baseFragment.getContext());
        DBHelperPPG db     = helper.getPPGDb();
        db.insert(dataModel, isServerRegist);
    }
    /**
     * 혈압데이터 서버 및 sqlite에 저장
     *
     * @param pressureModel
     */
    public void uploadPresure(final BaseFragment baseFragment, final PressureModel pressureModel, final BluetoothManager.IBluetoothResult iBluetoothResult) {
        Tr_brssr_info_input_data inputData                  = new Tr_brssr_info_input_data();
        Tr_login login                                      = Define.getInstance().getLoginInfo();

        Tr_brssr_info_input_data.RequestData requestData    = new Tr_brssr_info_input_data.RequestData();
        requestData.mber_sn     = login.mber_sn;
        requestData.ast_mass    = inputData.getArray(pressureModel);

        baseFragment.getData(baseFragment.getContext(), inputData.getClass(), requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_brssr_info_input_data) {
                    Tr_brssr_info_input_data data = (Tr_brssr_info_input_data) obj;
                    if ("Y".equals(data.reg_yn)) {
                        if(pressureModel.getDiastolicPressure() > 0.0f && pressureModel.getSystolicPressure() > 0.0f){
                            insertPressureMessage(baseFragment, pressureModel);
                        }
                        registPresureDB(baseFragment, pressureModel, true);
                        iBluetoothResult.onResult(true);
                    } else {
                        CDialog.showDlg(baseFragment.getContext(), baseFragment.getContext().getString(R.string.text_regist_fail));
                    }

                } else {
                    if(pressureModel.getDiastolicPressure() > 0.0f && pressureModel.getSystolicPressure() > 0.0f){
                        insertPressureMessage(baseFragment, pressureModel);
                    }
                    registPresureDB(baseFragment, pressureModel, false);
                    iBluetoothResult.onResult(false);
                }
            }
        });
    }

    private void registPresureDB(BaseFragment baseFragment, PressureModel pressureModel, boolean isServerRegist) {
        DBHelper helper     = new DBHelper(baseFragment.getContext());
        DBHelperPresure db  = helper.getPresureDb();
        db.insert(helper, pressureModel, isServerRegist);
    }

    /**
     * 혈압에 대한 투약정보 넣기
     *
     * @param baseFragment
     * @param iBluetoothResult
     */
    public void uploadPresureDrug(final BaseFragment baseFragment, final Tr_bdsg_dose_medicine_input.RequestData requestData, final BluetoothManager.IBluetoothResult iBluetoothResult) {
        Tr_bdsg_dose_medicine_input inputData   = new Tr_bdsg_dose_medicine_input();
        Tr_login login                          = Define.getInstance().getLoginInfo();

        baseFragment.getData(baseFragment.getContext(), inputData.getClass(), requestData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_bdsg_dose_medicine_input) {
                    Tr_bdsg_dose_medicine_input data = (Tr_bdsg_dose_medicine_input) obj;
                    if ("Y".equals(data.reg_yn)) {

                        iBluetoothResult.onResult(true);
                    } else {
                        CDialog.showDlg(baseFragment.getContext(), baseFragment.getContext().getString(R.string.text_regist_fail));
                    }
                } else {
                    registPresureDB(baseFragment, requestData, false);
                    iBluetoothResult.onResult(false);
                }
            }
        });
    }

    private void registPresureDB(BaseFragment baseFragment, Tr_bdsg_dose_medicine_input.RequestData requestData, boolean isServerRegist) {
        DBHelper helper     = new DBHelper(baseFragment.getContext());
        DBHelperPresure db  = helper.getPresureDb();
        db.insert(helper, requestData, true);
    }

    /**
     * 혈당 데이터 서버 및 sqlite에 저장
     *
     * @param dataModel
     */
    public void uploadSugarData(final BaseFragment baseFragment, final SparseArray<BloodModel> dataModel, final BluetoothManager.IBluetoothResult iBluetoothResult) {

        Tr_bdsg_info_input_data inputData                   = new Tr_bdsg_info_input_data();
        Tr_login login                                      = Define.getInstance().getLoginInfo();

        Tr_bdsg_info_input_data.RequestData requestData     = new Tr_bdsg_info_input_data.RequestData();
        requestData.mber_sn     = login.mber_sn;
        requestData.ast_mass    = inputData.getArray(dataModel);

        baseFragment.getData(baseFragment.getContext(), inputData.getClass(), requestData, true, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_bdsg_info_input_data) {
                    Tr_bdsg_info_input_data data = (Tr_bdsg_info_input_data) obj;
                    boolean isServerReg = "Y".equals(data.reg_yn);
                    if (isServerReg) {
                        registSugarDB(baseFragment, dataModel, true);

                        if (dataModel.size() > 0) {
                            BloodModel model = dataModel.get(dataModel.keyAt(dataModel.size() - 1));
                            if(model.getSugar() > 0.0f){
                                insertSugarMessage(baseFragment, dataModel);
                            }
                        }

                        if (iBluetoothResult != null)
                            iBluetoothResult.onResult(true);
                    } else {
                        CDialog.showDlg(baseFragment.getContext(), baseFragment.getContext().getString(R.string.text_regist_fail));
                    }
                }

            }
        }, new ApiData.IFailStep() {
            @Override
            public void fail() {
                registSugarDB(baseFragment, dataModel, false);

                if (dataModel.size() > 0) {
                    BloodModel model = dataModel.get(dataModel.keyAt(dataModel.size() - 1));
                    if(model.getSugar() > 0.0f){
                        insertSugarMessage(baseFragment, dataModel);
                    }
                }

                if (iBluetoothResult != null)
                    iBluetoothResult.onResult(false);
            }
        });
    }

    /**
     * 건강메시지 sqlite 등록하기(혈당)
     * @param baseFragment
     * @param dataModel
     */
    private void insertSugarMessage(BaseFragment baseFragment, SparseArray<BloodModel> dataModel) {
        // 메시지 DB 등록하기
        if (dataModel.size() > 0) {
            BloodModel model        = dataModel.get(dataModel.keyAt(dataModel.size() - 1));
            String message          = getSugarMessage(model, model.getBefore());
            if (TextUtils.isEmpty(message) == false) {
                SharedPref.getInstance().savePreferences(SharedPref.HEALTH_MESSAGE, true);
                MessageModel messageModel = new MessageModel();
                messageModel.setIdx(getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
                messageModel.setSugar("" + model.getSugar());
                messageModel.setRegdate("" + model.getRegTime());
                messageModel.setMessage(message);

                insertMesageDb(baseFragment, messageModel);
            }
        }
    }

    /**
     * 건강메시지 sqlite 등록하기(혈압)
     * @param baseFragment
     * @param model
     **/
    private void insertPressureMessage(BaseFragment baseFragment, PressureModel model) {
        // 메시지 DB 등록하기
        String message = getPressureMessage(baseFragment, model.getSystolicPressure(), model.getDiastolicPressure());
        if (TextUtils.isEmpty(message) == false) {
            SharedPref.getInstance().savePreferences(SharedPref.HEALTH_MESSAGE, true);

            MessageModel messageModel = new MessageModel();
            messageModel.setIdx(getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
            try {

                Thread.sleep(100);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            messageModel.setHeightpressure("" + model.getDiastolicPressure());
            messageModel.setLowpressure("" + model.getSystolicPressure());
            messageModel.setRegdate(model.getRegdate());
            messageModel.setMessage(message);
            insertMesageDb(baseFragment, messageModel);
        }
    }

    /**
     * 건강메시지 sqlite 등록하기(체지방)
     * @param baseFragment
     **/
    private void insertWeightMessage(BaseFragment baseFragment,  String weight, String reg, String fat) {
        // 메시지 DB 등록하기

        String message = getWeightMessage(baseFragment, weight, fat);
        if (TextUtils.isEmpty(message) == false) {
            SharedPref.getInstance().savePreferences(SharedPref.HEALTH_MESSAGE, true);
            MessageModel messageModel = new MessageModel();
            messageModel.setIdx(getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
            try {

                Thread.sleep(100);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            messageModel.setWeight("" + weight);
            messageModel.setRegdate(reg);
            messageModel.setMessage(message);
            insertMesageDb(baseFragment, messageModel);
        }
    }

    /**
     * 건강메시지 서버 전송 및 sqlite 저장
     *
     * @param baseFragment
     * @param model
     */
    private void insertMesageDb(final BaseFragment baseFragment, final MessageModel model) {
        Tr_infra_message_write.RequestData reqData = new Tr_infra_message_write.RequestData();
        Tr_login login          = Define.getInstance().getLoginInfo();
        reqData.idx             = model.getIdx();
        reqData.mber_sn         = login.mber_sn;
        reqData.infra_message   = model.getMessage();

        baseFragment.getData(baseFragment.getContext(), Tr_infra_message_write.class, reqData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {

                DBHelper helper     = new DBHelper(baseFragment.getContext());
                DBHelperMessage db  = helper.getMessageDb();
                if (obj instanceof Tr_infra_message_write) {
                    Tr_infra_message_write data = (Tr_infra_message_write) obj;
                    db.insert(model, "Y".equals(data.reg_yn));
                } else {
                    db.insert(model, false);
                }
            }
        });
    }

    /**
     * 혈당 sqlite 저장하기
     *
     * @param baseFragment
     * @param dataModel
     * @param isServerRegist
     */
    private void registSugarDB(BaseFragment baseFragment, SparseArray<BloodModel> dataModel, boolean isServerRegist) {
        DBHelper helper = new DBHelper(baseFragment.getContext());
        DBHelperSugar db = helper.getSugarDb();
        db.insert(dataModel, isServerRegist);
    }


    /**
     * 물 데이터 업로드 및 Sqlite저장
     *
     * @param baseFragment
     * @param dataModel
     * @param iBluetoothResult
     */
    public void uploadWaterData(final BaseFragment baseFragment, final SparseArray<WaterModel> dataModel, final String TargetAmount, final BluetoothManager.IBluetoothResult iBluetoothResult) {

        Tr_water_info_input_data inputData  = new Tr_water_info_input_data();
        Tr_login login                      = Define.getInstance().getLoginInfo();

        Tr_water_info_input_data.RequestData requestData    = new Tr_water_info_input_data.RequestData();
        requestData.mber_sn                                 = login.mber_sn;
        requestData.ast_mass                                = inputData.getArray(dataModel);

        //섭취량 등록
        baseFragment.getData(baseFragment.getContext(), inputData.getClass(), requestData, true, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_water_info_input_data) {
                    Tr_water_info_input_data data = (Tr_water_info_input_data) obj;
                    if ("Y".equals(data.reg_yn)) {

                        // 등록이 성공후에 목표 등록
                        if (!TargetAmount.toString().isEmpty()) {
                            Tr_login login                                  = Define.getInstance().getLoginInfo();
                            Tr_water_goalqy goalinputData                   = new Tr_water_goalqy();
                            Tr_water_goalqy.RequestData goalRequestData     = new Tr_water_goalqy.RequestData();
                            goalRequestData.mber_sn                         = login.mber_sn;
                            goalRequestData.goal_water_ntkqy                = TargetAmount;
                            goalRequestData.goal_water_goalqy               = "";

                            baseFragment.getData(baseFragment.getContext(), goalinputData.getClass(), goalRequestData, true, new ApiData.IStep() {
                                @Override
                                public void next(Object obj) {
                                    if (obj instanceof Tr_water_goalqy) {
                                        Tr_water_goalqy data = (Tr_water_goalqy) obj;
                                        if ("Y".equals(data.reg_yn)) {
                                            registWaterDB(baseFragment, dataModel, true);

                                            if (iBluetoothResult != null)
                                                iBluetoothResult.onResult(true);
                                        } else {
                                        }
                                    }
                                }
                            }, new ApiData.IFailStep() {
                                @Override
                                public void fail() {
                                }
                            });
                        } else {
                            registWaterDB(baseFragment, dataModel, true);

                            if (iBluetoothResult != null)
                                iBluetoothResult.onResult(true);
                        }
                    } else {
                        CDialog.showDlg(baseFragment.getContext(), baseFragment.getContext().getString(R.string.text_regist_fail));
                    }
                }
            }
        }, new ApiData.IFailStep() {
            @Override
            public void fail() {
                registWaterDB(baseFragment, dataModel, false);
                if (iBluetoothResult != null)
                    iBluetoothResult.onResult(false);
            }
        });
    }

    private void registWaterDB(BaseFragment baseFragment, SparseArray<WaterModel> dataModel, boolean isServerRegist) {
        DBHelper helper     = new DBHelper(baseFragment.getContext());
        DBHelperWater db    = helper.getWaterDb();
        db.insert(dataModel, isServerRegist);
    }

    /**
     * 체중 데이터 입력
     * @param baseFragment
     * @param weightModel
     * @param iBluetoothResult
     */
    public void uploadWeight(final BaseFragment baseFragment, final WeightModel weightModel, final BluetoothManager.IBluetoothResult iBluetoothResult) {

        Tr_get_hedctdata.DataList data = new Tr_get_hedctdata.DataList();
        data.bmr        = "" + weightModel.getBmr();
        data.bodywater  = "" + weightModel.getBodyWater();
        data.bone       = "" + weightModel.getBone();
        data.fat        = "" + weightModel.getFat();
        data.heartrate  = "" + weightModel.getHeartRate();
        data.muscle     = "" + weightModel.getMuscle();
        data.obesity    = "" + weightModel.getObesity();
        data.weight     = "" + weightModel.getWeight();
        data.bdwgh_goal = "" + weightModel.getBdwgh_goal();

        data.idx        = weightModel.getIdx();
        data.regtype    = weightModel.getRegType();
        data.reg_de     = weightModel.getRegDate();

        List<Tr_get_hedctdata.DataList> datas = new ArrayList<>();
        datas.add(data);
        new DeviceDataUtil().uploadWeight(baseFragment, datas, iBluetoothResult);
    }

    /**
     * 목표체중 데이터 입력
     * @param baseFragment
     * @param weightModel
     * @param iBluetoothResult
     */
    public void uploadTargetWeight(final BaseFragment baseFragment, final WeightModel weightModel, final BluetoothManager.IBluetoothResult iBluetoothResult) {

        Tr_bdwgh_goal_input inputData       = new Tr_bdwgh_goal_input();
        final Tr_login login                = Define.getInstance().getLoginInfo();

        Tr_bdwgh_goal_input.RequestData requestData         = new Tr_bdwgh_goal_input.RequestData();
        requestData.mber_sn                                 = login.mber_sn;
        requestData.mber_bdwgh_goal                         = Float.toString(weightModel.getBdwgh_goal());

        baseFragment.getData(baseFragment.getContext(), inputData.getClass(), requestData, true, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_bdwgh_goal_input) {
                    Tr_bdwgh_goal_input data = (Tr_bdwgh_goal_input) obj;
                    if ("Y".equals(data.reg_yn)) {

                        if (iBluetoothResult != null)
                            iBluetoothResult.onResult(true);

                    } else {
                        CDialog.showDlg(baseFragment.getContext(), baseFragment.getContext().getString(R.string.text_regist_fail));
                    }
                }
            }
        }, new ApiData.IFailStep() {
            @Override
            public void fail() {

                if (iBluetoothResult != null)
                    iBluetoothResult.onResult(false);
            }
        });
    }

    /**
     * 심박수 데이터 등록
     * @param baseFragment
     * @param dataModel
     * @param iBluetoothResult
     */
    public void uploadPPGData(final BaseFragment baseFragment, final List<BandModel> dataModel, final BluetoothManager.IBluetoothResult iBluetoothResult) {

        Tr_ppg_info_input_data inputData                = new Tr_ppg_info_input_data();
        Tr_login login                                  = Define.getInstance().getLoginInfo();

        Tr_ppg_info_input_data.RequestData requestData  = new Tr_ppg_info_input_data.RequestData();
        requestData.mber_sn     = login.mber_sn;
        requestData.ast_mass    = inputData.getArray(dataModel, "D");

        baseFragment.getData(baseFragment.getContext(), inputData.getClass(), requestData, true, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_ppg_info_input_data) {
                    Tr_ppg_info_input_data data = (Tr_ppg_info_input_data) obj;
                    if ("Y".equals(data.reg_yn)) {
                        registPPGDB(baseFragment, dataModel, true);

                        if (iBluetoothResult != null)
                            iBluetoothResult.onResult(true);
                    } else {
                        CDialog.showDlg(baseFragment.getContext(), baseFragment.getContext().getString(R.string.text_regist_fail));
                    }
                }
            }
        }, new ApiData.IFailStep() {
            @Override
            public void fail() {
                registPPGDB(baseFragment, dataModel, false);
                if (iBluetoothResult != null)
                    iBluetoothResult.onResult(false);
            }
        });
    }

    /**
     * 체중데이터 업로드 및 Sqlite 저장
     * @param baseFragment
     * @param datas
     * @param iBluetoothResult
     */
    public void uploadWeight(final BaseFragment baseFragment, final List<Tr_get_hedctdata.DataList> datas, final BluetoothManager.IBluetoothResult iBluetoothResult) {

        Tr_bdwgh_info_input_data inputData  = new Tr_bdwgh_info_input_data();
        final Tr_login login                = Define.getInstance().getLoginInfo();

        Tr_bdwgh_info_input_data.RequestData requestData    = new Tr_bdwgh_info_input_data.RequestData();
        requestData.mber_sn                                 = login.mber_sn;
        requestData.ast_mass                                = inputData.getArray(datas);

        baseFragment.getData(baseFragment.getContext(), inputData.getClass(), requestData, true, new ApiData.IStep() {
            @Override
            public void next(Object obj) {
                if (obj instanceof Tr_bdwgh_info_input_data) {
                    Tr_bdwgh_info_input_data data = (Tr_bdwgh_info_input_data) obj;
                    if ("Y".equals(data.reg_yn)) {
                        for (Tr_get_hedctdata.DataList listdata : datas) {
                            if(StringUtil.getFloatVal(listdata.weight) > 0.0f || listdata.regtype.equals("D")){
                                insertWeightMessage(baseFragment, listdata.weight, listdata.reg_de, listdata.fat);
                                registWeightDB(baseFragment, datas, true);
                            }

                        }

                        if (iBluetoothResult != null)
                            iBluetoothResult.onResult(true);
                    } else {
                        CDialog.showDlg(baseFragment.getContext(), baseFragment.getContext().getString(R.string.text_regist_fail));
                    }
                }
            }
        }, new ApiData.IFailStep() {
            @Override
            public void fail() {
                for (Tr_get_hedctdata.DataList data : datas) {
                    if(StringUtil.getFloatVal(data.weight) > 0.0f || data.regtype.equals("D")){
                        insertWeightMessage(baseFragment, data.weight, data.reg_de, data.fat);
                        registWeightDB(baseFragment, datas, true);
                    }
                }

                if (iBluetoothResult != null)
                    iBluetoothResult.onResult(false);
            }
        });
    }

    private void registWeightDB(BaseFragment baseFragment, List<Tr_get_hedctdata.DataList> datas, boolean isServerRegist) {
        DBHelper helper     = new DBHelper(baseFragment.getContext());
        DBHelperWeight db   = helper.getWeightDb();
        db.insert(datas, isServerRegist);
    }

    /**
     * 건강메시지 체지방메시지 만들기
     */

    private String getRatingMsg(String fat) {
        String ratingMsg = "";
        int rating = getRating(fat);

        if (rating == 1) {
            ratingMsg = "체지방률 " + fat + "%로서 평균보다 상당히 적은 상태입니다.";
        } else if (rating == 2) {
            ratingMsg = "체지방률 " + fat + "%로서 평균보다 적은 상태입니다.";
        } else if (rating == 3) {
            ratingMsg = "체지방률 " + fat + "%로서 평균적인 상태입니다.";
        } else if (rating == 4) {
            ratingMsg = "체지방률 " + fat + "%로서 평균보다 많은 상태입니다.";
        } else if (rating == 5) {
            ratingMsg = "체지방률 " + fat + "%로서 평균보다 상당히 많은 상태입니다.";
        }
        return ratingMsg;
    }

    /**
     * 건강메시지 체지방 등급 만들기
     */

    private int getRating(String fat) {
        int rating = 0;                                                                                 // 체지방 등급
        Tr_login login = Define.getInstance().getLoginInfo();                                           // 회원 정보
        int sex         = StringUtil.getIntVal(login.mber_sex);                                         // 회원 성별
        String nowYear  = CDateUtil.getFormattedString_yyyy(System.currentTimeMillis());                // 현재 년도
        int rBirth      = StringUtil.getIntVal(login.mber_lifyea.substring(0, 4));                      // 회원 생년
        int rAge        = (StringUtil.getIntVal(nowYear) - rBirth);                                     // 회원 나이
        float bdfat     = StringUtil.getFloatVal(fat);                                                  // 회원 체지방률

        // 남자
        if (sex == 1) {
            if (((rAge >= 19 && rAge <= 24) && (bdfat <= 8.0))                            // 1등급군에 해당
                    || ((rAge >= 25 && rAge <= 29) && (bdfat <= 9.4))
                    || ((rAge >= 30 && rAge <= 34) && (bdfat <= 10.6))
                    || ((rAge >= 35 && rAge <= 39) && (bdfat <= 12.9))
                    || ((rAge >= 40 && rAge <= 44) && (bdfat <= 12.8))
                    || ((rAge >= 45 && rAge <= 49) && (bdfat <= 13.2))
                    || ((rAge >= 50 && rAge <= 54) && (bdfat <= 14.3))
                    || ((rAge >= 55 && rAge <= 59) && (bdfat <= 14.4))
                    || ((rAge >= 60 && rAge <= 64) && (bdfat <= 16.1))) {
                rating = 1;
            } else if (((rAge >= 19 && rAge <= 24) && (bdfat >= 8.1 && bdfat <= 11.7))      // 2등급군에 해당
                    || ((rAge >= 25 && rAge <= 29) && (bdfat >= 9.5 && bdfat <= 13.7))
                    || ((rAge >= 30 && rAge <= 34) && (bdfat >= 10.7 && bdfat <= 14.5))
                    || ((rAge >= 35 && rAge <= 39) && (bdfat >= 13.0 && bdfat <= 16.7))
                    || ((rAge >= 40 && rAge <= 44) && (bdfat >= 12.9 && bdfat <= 15.6))
                    || ((rAge >= 45 && rAge <= 49) && (bdfat >= 13.3 && bdfat <= 16.5))
                    || ((rAge >= 50 && rAge <= 54) && (bdfat >= 14.4 && bdfat <= 17.7))
                    || ((rAge >= 55 && rAge <= 59) && (bdfat >= 14.5 && bdfat <= 18.0))
                    || ((rAge >= 60 && rAge <= 64) && (bdfat >= 16.2 && bdfat <= 17.8))) {
                rating = 2;
            } else if (((rAge >= 19 && rAge <= 24) && (bdfat >= 11.8 && bdfat <= 16.6))     // 3등급군에 해당
                    || ((rAge >= 25 && rAge <= 29) && (bdfat >= 13.8 && bdfat <= 18.3))
                    || ((rAge >= 30 && rAge <= 34) && (bdfat >= 14.6 && bdfat <= 18.8))
                    || ((rAge >= 35 && rAge <= 39) && (bdfat >= 16.8 && bdfat <= 21.1))
                    || ((rAge >= 40 && rAge <= 44) && (bdfat >= 15.7 && bdfat <= 20.0))
                    || ((rAge >= 45 && rAge <= 49) && (bdfat >= 16.6 && bdfat <= 20.3))
                    || ((rAge >= 50 && rAge <= 54) && (bdfat >= 17.8 && bdfat <= 21.8))
                    || ((rAge >= 55 && rAge <= 59) && (bdfat >= 18.1 && bdfat <= 21.5))
                    || ((rAge >= 60 && rAge <= 64) && (bdfat >= 17.9 && bdfat <= 22.5))) {
                rating = 3;
            } else if (((rAge >= 19 && rAge <= 24) && (bdfat >= 16.7 && bdfat <= 22.8))     // 4등급군에 해당
                    || ((rAge >= 25 && rAge <= 29) && (bdfat >= 18.4 && bdfat <= 24.4))
                    || ((rAge >= 30 && rAge <= 34) && (bdfat >= 18.9 && bdfat <= 23.0))
                    || ((rAge >= 35 && rAge <= 39) && (bdfat >= 21.2 && bdfat <= 25.1))
                    || ((rAge >= 40 && rAge <= 44) && (bdfat >= 20.1 && bdfat <= 24.0))
                    || ((rAge >= 45 && rAge <= 49) && (bdfat >= 20.4 && bdfat <= 24.8))
                    || ((rAge >= 50 && rAge <= 54) && (bdfat >= 21.9 && bdfat <= 25.9))
                    || ((rAge >= 55 && rAge <= 59) && (bdfat >= 21.6 && bdfat <= 25.1))
                    || ((rAge >= 60 && rAge <= 64) && (bdfat >= 22.6 && bdfat <= 27.5))) {
                rating = 4;
            } else if (((rAge >= 19 && rAge <= 24) && (bdfat >= 22.9))                    // 5등급군에 해당
                    || ((rAge >= 25 && rAge <= 29) && (bdfat >= 24.5))
                    || ((rAge >= 30 && rAge <= 34) && (bdfat >= 23.1))
                    || ((rAge >= 35 && rAge <= 39) && (bdfat >= 25.2))
                    || ((rAge >= 40 && rAge <= 44) && (bdfat >= 24.1))
                    || ((rAge >= 45 && rAge <= 49) && (bdfat >= 24.9))
                    || ((rAge >= 50 && rAge <= 54) && (bdfat >= 26.0))
                    || ((rAge >= 55 && rAge <= 59) && (bdfat >= 25.2))
                    || ((rAge >= 60 && rAge <= 64) && (bdfat >= 27.6))) {
                rating = 5;
            }
            return rating;
        }
        // 여자
        if (sex == 2) {
            if (((rAge >= 19 && rAge <= 24) && (bdfat <= 19.0))                           // 1등급군에 해당
                    || ((rAge >= 25 && rAge <= 29) && (bdfat <= 18.6))
                    || ((rAge >= 30 && rAge <= 34) && (bdfat <= 18.9))
                    || ((rAge >= 35 && rAge <= 39) && (bdfat <= 19.2))
                    || ((rAge >= 40 && rAge <= 44) && (bdfat <= 19.8))
                    || ((rAge >= 45 && rAge <= 49) && (bdfat <= 19.4))
                    || ((rAge >= 50 && rAge <= 54) && (bdfat <= 19.7))
                    || ((rAge >= 55 && rAge <= 59) && (bdfat <= 20.6))
                    || ((rAge >= 60 && rAge <= 64) && (bdfat <= 21.2))) {
                rating = 1;
            } else if (((rAge >= 19 && rAge <= 24) && (bdfat >= 19.1 && bdfat <= 22.3))     // 2등급군에 해당
                    || ((rAge >= 25 && rAge <= 29) && (bdfat >= 18.7 && bdfat <= 21.3))
                    || ((rAge >= 30 && rAge <= 34) && (bdfat >= 19.0 && bdfat <= 22.1))
                    || ((rAge >= 35 && rAge <= 39) && (bdfat >= 19.3 && bdfat <= 23.0))
                    || ((rAge >= 40 && rAge <= 44) && (bdfat >= 19.9 && bdfat <= 23.1))
                    || ((rAge >= 45 && rAge <= 49) && (bdfat >= 19.5 && bdfat <= 22.9))
                    || ((rAge >= 50 && rAge <= 54) && (bdfat >= 19.8 && bdfat <= 23.9))
                    || ((rAge >= 55 && rAge <= 59) && (bdfat >= 20.7 && bdfat <= 24.3))
                    || ((rAge >= 60 && rAge <= 64) && (bdfat >= 21.3 && bdfat <= 24.8))) {
                rating = 2;
            } else if (((rAge >= 19 && rAge <= 24) && (bdfat >= 22.4 && bdfat <= 25.3))     // 3등급군에 해당
                    || ((rAge >= 25 && rAge <= 29) && (bdfat >= 21.4 && bdfat <= 24.9))
                    || ((rAge >= 30 && rAge <= 34) && (bdfat >= 22.2 && bdfat <= 24.8))
                    || ((rAge >= 35 && rAge <= 39) && (bdfat >= 23.1 && bdfat <= 27.0))
                    || ((rAge >= 40 && rAge <= 44) && (bdfat >= 23.2 && bdfat <= 28.0))
                    || ((rAge >= 45 && rAge <= 49) && (bdfat >= 23.0 && bdfat <= 27.7))
                    || ((rAge >= 50 && rAge <= 54) && (bdfat >= 24.4 && bdfat <= 27.8))
                    || ((rAge >= 55 && rAge <= 59) && (bdfat >= 24.4 && bdfat <= 28.9))
                    || ((rAge >= 60 && rAge <= 64) && (bdfat >= 24.9 && bdfat <= 29.2))) {
                rating = 3;
            } else if (((rAge >= 19 && rAge <= 24) && (bdfat >= 25.4 && bdfat <= 29.6))     // 4등급군에 해당
                    || ((rAge >= 25 && rAge <= 29) && (bdfat >= 25.0 && bdfat <= 29.6))
                    || ((rAge >= 30 && rAge <= 34) && (bdfat >= 24.9 && bdfat <= 28.6))
                    || ((rAge >= 35 && rAge <= 39) && (bdfat >= 27.1 && bdfat <= 32.8))
                    || ((rAge >= 40 && rAge <= 44) && (bdfat >= 28.1 && bdfat <= 33.1))
                    || ((rAge >= 45 && rAge <= 49) && (bdfat >= 27.8 && bdfat <= 31.4))
                    || ((rAge >= 50 && rAge <= 54) && (bdfat >= 27.9 && bdfat <= 34.6))
                    || ((rAge >= 55 && rAge <= 59) && (bdfat >= 29.0 && bdfat <= 36.0))
                    || ((rAge >= 60 && rAge <= 64) && (bdfat >= 29.3 && bdfat <= 34.7))) {
                rating = 4;
            } else if (((rAge >= 19 && rAge <= 24) && (bdfat >= 29.7))                    // 5등급군에 해당
                    || ((rAge >= 25 && rAge <= 29) && (bdfat >= 29.7))
                    || ((rAge >= 30 && rAge <= 34) && (bdfat >= 28.7))
                    || ((rAge >= 35 && rAge <= 39) && (bdfat >= 32.9))
                    || ((rAge >= 40 && rAge <= 44) && (bdfat >= 33.2))
                    || ((rAge >= 45 && rAge <= 49) && (bdfat >= 31.5))
                    || ((rAge >= 50 && rAge <= 54) && (bdfat >= 34.7))
                    || ((rAge >= 55 && rAge <= 59) && (bdfat >= 36.1))
                    || ((rAge >= 60 && rAge <= 64) && (bdfat >= 34.8))) {
                rating = 5;
            }
            return rating;
        }
        return rating;
    }

    /**
     * 건강메시지 체중 메시지 만들기
     * @return
     */

    private String getWeightMessage(BaseFragment basefragment, String weight, String fat){
        Tr_login login  = Define.getInstance().getLoginInfo();                                          // 회원 정보
        String rWeight   = String.format("%.1f", StringUtil.getFloatVal(weight));  // 회원 체중
        float rHeight   = StringUtil.getFloat(login.mber_height) * 0.01f;                               // 회원 키
        float bmi       = StringUtil.getFloatVal(String.format("%.1f", StringUtil.getFloatVal(weight) / (rHeight * rHeight))); // 회원 BMI
        String lavelstr = "";
        if(bmi < 18.5) {
            lavelstr = "저체중";
        }else if(bmi >= 18.5 && bmi <= 22.9){
            lavelstr = "정상체중";
        }else if(bmi > 22.9 && bmi < 25.0){
            lavelstr = "과체중";
        }else if(bmi >= 25.0){
            lavelstr = "비만";
        }

        String message  = "측정된 체중 " + rWeight + "kg으로 계산된 BMI(체질량지수)는 " + bmi + "으로 " + lavelstr + "군에 해당합니다.";

        float bdfat = StringUtil.getFloatVal(fat);

        if (bdfat > 0) {
            if (message != "")
                message += "\n\n";
            message += getRatingMsg(fat);
        }

        // 저체중군
        if(bmi < 18.5) {
            // 추가메시지는 ||로 구분하여 넣는다.
            if (message != "")
                message += "\n\n";
            message     += "적절한 운동과 균형 잡힌 음식섭취를 통해 정상체중을 회복 할 수 있도록 노력이 필요합니다. \n" +
                           "체중증가가 지방만이 아닌 제지방의 증가까지 병행하여 목표 활동량 달성 노력과 함께 근력운동을 추가하여 근육의 양과 크기를 증가시키는 것이 중요합니다. \n" +
                           "점진적으로 목표를 수정하여 활동량과 식사량을 늘려주세요.";
            return message;
        }
        // 정상체중군
        if(bmi >= 18.5 && bmi <= 22.9) {
            // 추가메시지는 ||로 구분하여 넣는다.
            if (message != "")
                message += "\n\n";
            message     += "적절한 운동과 식사조절을 통해 건강한 체중을 유지하는 것이 중요합니다. \n" +
                           "점진적으로 목표를 수정하여 활동량을 늘려주세요.";
            return message;
        }
        // 비만군
        if(bmi >= 25.0) {
            // 추가메시지는 ||로 구분하여 넣는다.
            if (message != "")
                message += "\n\n";
            message     += "적절한 체중 감량에는 시간이 걸립니다. 가능한 매일 활동 목표 달성을 위해 노력해야 합니다. \n" +
                           "추천되는 목표활동량은 최소한입니다. 점진적으로 목표를 수정하여 활동량을 늘려가야 합니다. \n" +
                           "체중 감량을 위해 평소보다 하루 500~1,000kcal정도의 에너지 섭취량을 줄이세요.";
            return message;
        }
        return message;
    }

    /**
     * 건강메시지 혈당 만들기
     *
     * @param
     * @param eatType
     * @return
     */
    private String getSugarMessage(BloodModel model, String eatType) {
        String message = "";
        String tString = CDateUtil.HH_MM(model.getTime());
        String nowTime = StringUtil.getFormattedDateTime();


        float sugar = model.getSugar();
        // 식전
        if (eatType.equals("0")|| eatType.equals("1")|| eatType.equals("3")) {
            if (sugar <= 99) {
                //정상
                message = tString + " 식전 혈당은 " + String.format("%.0f", sugar) + "mg/dL로 정상범위 안에 있습니다.\n";
                SharedPref.getInstance().savePreferences(SharedPref.SUGAR_OVER_CHECK, "N");
            } else if (sugar >= 100 && sugar <= 125) {
                //당뇨 전단계
                message = tString + " 식전 혈당은 " + String.format("%.0f", sugar) + "mg/dL로 정상범위에서 벗어납니다. 정기적으로 혈당을 체크하고, 관리가 필요합니다.\n";
                SharedPref.getInstance().savePreferences(SharedPref.SUGAR_OVER_CHECK, "N");
            } else if (sugar >= 126) {
                //당뇨병
                message = tString + " 식전 혈당은 " + String.format("%.0f", sugar) + "mg/dL로 정상범위에서 벗어납니다. 정기적으로 혈당을 체크하고, 관리가 필요합니다.\n";
                SharedPref.getInstance().savePreferences(SharedPref.SUGAR_OVER_CHECK, "Y");
                SharedPref.getInstance().savePreferences(SharedPref.SUAGR_OVER_TIME, nowTime);
            }
        }
        // 식후
        else {
            if (sugar <= 139) {
                //정상
                message = tString + "식후 혈당은 " + String.format("%.0f", sugar) + "mg/dL로 정상범위 안에 있습니다.\n";
                SharedPref.getInstance().savePreferences(SharedPref.SUGAR_OVER_CHECK, "N");
            } else if (sugar >= 140 && sugar <= 199) {
                //당뇨 전단계
                message = tString + " 식후 혈당은 " + String.format("%.0f", sugar) + "mg/dL로 정상범위에서 벗어납니다. 정기적으로 혈당을 체크하고, 관리가 필요합니다.\n";
                SharedPref.getInstance().savePreferences(SharedPref.SUGAR_OVER_CHECK, "N");
            } else if (sugar >= 200) {
                //당뇨병
                message = tString + " 식후 혈당은 " + String.format("%.0f", sugar) + "mg/dL로 정상범위에서 벗어납니다. 정기적으로 혈당을 체크하고, 관리가 필요합니다.\n";
                SharedPref.getInstance().savePreferences(SharedPref.SUGAR_OVER_CHECK, "Y");
                SharedPref.getInstance().savePreferences(SharedPref.SUAGR_OVER_TIME, nowTime);
            }
        }
        Logger.i(TAG, "getSugarMessage=" + message);
        return message;
    }


    private String getPresureGroup(BaseFragment baseFragment, float systolic, float diastolic) {
        String presureGroup = "";

        if(systolic < 120 && diastolic < 80) {
            presureGroup = "정상혈압";
        }
        if((systolic >= 120 && systolic <= 139) || (diastolic >= 80 && diastolic <= 89)) {
            presureGroup = "고혈압 전단계";
        }
        if((systolic >= 140 && systolic <= 159) || (diastolic >= 90 && diastolic <= 99)) {
            presureGroup = "고혈압 1기";
        }
        if((systolic >= 160) || (diastolic >= 100)) {
            presureGroup = "고혈압 2기";
        }
        return presureGroup;
    }

    /**
     * 건강메시지 혈압 만들기
     * @param baseFragment
     * @param systolic
     * @param diastolic
     * @return
     */
    private String getPressureMessage(BaseFragment baseFragment, float systolic, float diastolic) {

        String presureGroup = getPresureGroup(baseFragment, systolic, diastolic);
        String currString   = StringUtil.getFormattedDateTime();
        String message      = "측정된 혈압 " + (int)systolic + "/" + (int)diastolic + "mmHg은 " + presureGroup + "에 해당합니다.";

        // 고혈압 전단계
        String afterTime = SharedPref.getInstance().getPreferences(SharedPref.PRESURE_AFTER_TIME);
        int afterCount = SharedPref.getInstance().getPreferences(SharedPref.PRESURE_AFTER_COUNT, 0);
        // 고혈압 1기
        String oneStepTime = SharedPref.getInstance().getPreferences(SharedPref.PRESURE_ONESTEP_TIME);
        int oneStepCount = SharedPref.getInstance().getPreferences(SharedPref.PRESURE_ONESTEP_COUNT, 0);
        // 고혈압 2기
        String twoStepTime = SharedPref.getInstance().getPreferences(SharedPref.PRESURE_TWOSTEP_TIME);
        int twoStepCount = SharedPref.getInstance().getPreferences(SharedPref.PRESURE_TWOSTEP_COUNT, 0);
        // 고혈압 마지막 저장 시간저장
        String lastTimeSave = SharedPref.getInstance().getPreferences(SharedPref.PRESURE_LAST_SAVE_TIME);

        // 전단계 시간 초기화
        if (TextUtils.isEmpty(afterTime)) {
            afterTime = currString;
            SharedPref.getInstance().savePreferences(SharedPref.PRESURE_AFTER_TIME, afterTime);
        }
        // 1기 시간 초기화
        if (TextUtils.isEmpty(oneStepTime)) {
            oneStepTime = currString;
            SharedPref.getInstance().savePreferences(SharedPref.PRESURE_ONESTEP_TIME, oneStepTime);
        }
        // 2기 시간 초기화
        if (TextUtils.isEmpty(twoStepTime)) {
            twoStepTime = currString;
            SharedPref.getInstance().savePreferences(SharedPref.PRESURE_TWOSTEP_TIME, twoStepTime);
        }
        if(TextUtils.isEmpty(lastTimeSave)) {
            lastTimeSave = currString;
            SharedPref.getInstance().savePreferences(SharedPref.PRESURE_LAST_SAVE_TIME, lastTimeSave);
        }

        long currtime       = CDateUtil.getTime("yyyyMMddHHmmss", currString);
        long after10time    = CDateUtil.getTime("yyyyMMddHHmmss", afterTime);
        long one10Time      = CDateUtil.getTime("yyyyMMddHHmmss", oneStepTime);
        long two10Time      = CDateUtil.getTime("yyyyMMddHHmmss", twoStepTime);
        long lastTime       = CDateUtil.getTime("yyyyMMddHHmmss", lastTimeSave);

        long tenTime        = 1000 * 60 * 10;
        long dayOnehourTime = 1000 * 60 * 60 * 25;

        boolean isAfter10Minit;          // 전단계 10분 확인
        if (currtime - after10time <= tenTime) {
            isAfter10Minit = true;
        } else {
            isAfter10Minit = false;
        }
        boolean isOneStep10Minit;               // 1기 10분 확인
        if (currtime - one10Time <= tenTime) {
            isOneStep10Minit = true;
        } else {
            isOneStep10Minit = false;
        }
        boolean isTwoStep10Minit;               // 2기 10분 확인
        if (currtime - two10Time <= tenTime) {
            isTwoStep10Minit = true;
        } else {
            isTwoStep10Minit = false;
        }
        boolean isAfterLastOnedayOneHourTime;
        if( currtime - lastTime >= dayOnehourTime) {
            isAfterLastOnedayOneHourTime = true;
        } else {
            isAfterLastOnedayOneHourTime = false;
        }

        if(isAfterLastOnedayOneHourTime) {
            message     += "혈압은 시간마다, 활동 상태에 따라 다르므로 아침 9~10시 사이와 저녁 9~10시 사이에 각각 2~3회 측정한 혈압의 평균이 비교적 정확한 자기 혈압입니다.\n";
            if (message != "")
                message += "\n\n";
        }

        Tr_login login  = Define.getInstance().getLoginInfo();                             // 로그인 정보
        float bmi       = StringUtil.getFloatVal(login.mber_bmi);                          // bmi 측정치
        String nowYear  = CDateUtil.getFormattedString_yyyy(System.currentTimeMillis());   // 현재년도
        int rAge = Integer.parseInt(login.mber_lifyea.substring(0, 4));                    // 회원 생년
        //+----------------------------------------------
        // 정상
        //+----------------------------------------------
        if (systolic < 120 && diastolic < 80) {

            if (message != "")
                message += "\n\n";
            message += baseFragment.getContext().getString(R.string.presure_normal);
            return message;
        }
        //+----------------------------------------------
        //고혈압 전단계
        //+----------------------------------------------
        if (((systolic >= 120 && systolic <= 139) || (diastolic >= 80 && diastolic <= 89)) && systolic < 140) {
            if (afterCount == 0) {

                if (message != "")
                    message += "\n\n";
                message += baseFragment.getContext().getString(R.string.presure_after_one_and_two_step);
                isAfter10Minit = true;
                afterCount = 1;
            }
            if (afterCount > 1 && isAfter10Minit) {

                if (message != "")
                    message += "\n\n";
                message +=  "일반적으로 가정혈압의 고혈압 기준은 135/85 mmHg로서 진료실혈압보다 약 5 mmHg 정도 낮습니다.\n" +
                        "고혈압 예방을 위한 적극적인 관리가 필요합니다.\n" +
                        "비만(특히 복부 비만), 고염분 섭취, 운동부족, 흡연, 과음을 조절하는 생활습관을 갖도록 노력하세요.";
            } else if(!isAfter10Minit) {
                SharedPref.getInstance().savePreferences(SharedPref.PRESURE_AFTER_COUNT, 0);
                message = getPressureMessage(baseFragment, systolic, diastolic);
            }
            SharedPref.getInstance().savePreferences(SharedPref.PRESURE_AFTER_TIME, currString);
            SharedPref.getInstance().savePreferences(SharedPref.PRESURE_AFTER_COUNT, afterCount + 1);
            return message;
        }

        if (((systolic >= 140 && systolic <= 159) || (diastolic >= 90 && diastolic <= 99)) && systolic < 160) {
            //+----------------------------------------------
            //고혈압 1기 (첫번째)
            //+----------------------------------------------
            if(oneStepCount == 0) {
                if (message != "")
                    message += "\n\n";
                message += baseFragment.getContext().getString(R.string.presure_onestep_and_twostep);
                isOneStep10Minit = true;
                oneStepCount = 1;
            }
            //+----------------------------------------------
            //고혈압 1기 (두번째)
            //+----------------------------------------------
            if (oneStepCount > 1 && isOneStep10Minit) {
                // 고혈압 1기 두번째 (80세이상, 당뇨일 경우 추가 메시지)
                if (((systolic >= 140 && systolic <= 159) || (diastolic >= 90 && diastolic <= 90)) && isOneStep10Minit) {
                    String systolicStr  = "140";
                    String diastolicStr = "90";
                    // 80세 이상일 경우
                    if ((Integer.parseInt(nowYear) - rAge) >= 80) {
                        systolicStr     = "150";
                        diastolicStr    = "90";
                    }
                    // 당뇨일 경우
                    if (login.disease_nm.contains("2")) {
                        systolicStr     = "130";
                        diastolicStr    = "80";
                    }
                    if (message != "")
                        message += "\n\n";
                    message += "고혈압 치료의 목표 혈압은 수축기혈압 " + systolicStr + "mmHg 미만, 이완기혈압 " + diastolicStr + "mmHg 미만입니다. 심혈관질환 예방을 위해 적극적인 생활습관 개선이 필요합니다.";
                }
                // 고혈압 1기 두번째 (bmi가 25 이상일 경우 (비만일 경우) 추가 메시지)
                if (bmi >= 25) {
                    String sex  = "90";      //남성
                    if (login.mber_sex.equals("2")) {
                        sex     = "85";      //여성
                    }

                    if (message != "")
                        message += "\n\n";
                    message += "[체중감량]\n" +
                            "① 효과 : 체중 1kg 감량 시 수축기혈압은 -1.1mmHg, 이완기혈압은 -0.9mmHg 감소효과가 있습니다.\n" +
                            "② 목표 : 체질량지수(BMI) 25kg/m2 미만 및 허리둘레 " + sex + "cm 미만 유지를 목표로 합니다. \n" +
                            "③ 식사조절과 함께 신체활동량을 늘려주세요.";
                }
                // 고혈압 1기 두번째 (흡연중일 경우 추가 메시지)
                if (login.smkng_yn.equals("Y")) {

                    if (message != "")
                        message += "\n\n";
                    message += "[금연]\n" +
                            "담배에 함유된 니코틴에 의해 일시적으로 혈압과 맥박이 상승됩니다. 흡연은 고혈압과 마찬가지로 심혈관질환의 강력한 위험인자이므로 완전한 금연이 필요합니다.";
                }
                // 고혈압 1기 두번째 (활동량 1,2번 선택인 경우 추가 메시지)
                if ((login.mber_actqy.equals("1")) || (login.mber_actqy.equals("2"))) {

                    if (message != "")
                        message += "\n\n";
                    message += "[운동]\n" +
                            "① 효과 : 하루 30~50분, 일주일에 5일 이상 운동 시 수축기혈압은 -4.9mmHg, 이완기혈압은 -3.7mmHg 감소효과가 있습니다.\n" +
                            "② 방법 : 유산소 운동을 기본으로 하되 무산소 운동을 일주일에 2~3회 정도 병행하면 좋습니다. \n" +
                            "③ 주의 : 혈압이 조절되지 않는 경우 무거운 것을 들어올리는 것과 같은 무산소 운동은 피하세요.";
                }
            } else if(!isOneStep10Minit) {
                SharedPref.getInstance().savePreferences(SharedPref.PRESURE_ONESTEP_COUNT, 0);
                message = getPressureMessage(baseFragment, systolic, diastolic);
            }
            SharedPref.getInstance().savePreferences(SharedPref.PRESURE_ONESTEP_TIME, currString);
            SharedPref.getInstance().savePreferences(SharedPref.PRESURE_ONESTEP_COUNT, oneStepCount + 1);
            return message;
        }
        if (((systolic >= 160) || (diastolic >= 100))) {
            //+----------------------------------------------
            // 고혈압 2기 (첫번째)
            //+----------------------------------------------
            if(twoStepCount == 0) {
                if (message != "")
                    message += "\n\n";
                message += baseFragment.getContext().getString(R.string.presure_onestep_and_twostep);
                isTwoStep10Minit = true;
                twoStepCount = 1;
            }
            //+----------------------------------------------
            // 고혈압 2기 (두번째)
            //+----------------------------------------------
            if (twoStepCount > 1 && isTwoStep10Minit) {
                // 고혈압 2기 두번째 (80세 이상, 당뇨일 경우 추가 메시지)
                if (((systolic >= 160) || (diastolic >= 100)) && isTwoStep10Minit) {
                    String systolicStr  = "140";
                    String diastolicStr = "90";
                    // 80세 이상일 경우
                    if ((Integer.parseInt(nowYear) - rAge) >= 80) {
                        systolicStr     = "150";
                        diastolicStr    = "90";
                    }
                    // 당뇨일 경우
                    if (login.disease_nm.contains("2")) {
                        systolicStr     = "140";
                        diastolicStr    = "80";
                    }

                    if (message != "")
                        message += "\n\n";
                    message += "고혈압 치료의 목표 혈압은 수축기혈압 " + systolicStr + "mmHg 미만, 이완기혈압 " + diastolicStr + "mmHg 미만입니다. 심혈관질환 예방을 위해 적극적인 생활습관 개선이 필요합니다.";
                }
                // 고혈압 2기 두번째 (bmi가 25 이상인 경우(비만인 경우) 추가 메시지)
                if (bmi >= 25) {
                    String sex = "90";      //남성
                    if (login.mber_sex.equals("2")) {
                        sex = "85";         //여성
                    }

                    if (message != "")
                        message += "\n\n";
                    message += "[체중감량]\n" +
                            "① 효과 : 체중 1kg 감량 시 수축기혈압은 -1.1mmHg, 이완기혈압은 -0.9mmHg 감소효과가 있습니다.\n" +
                            "② 목표 : 체질량지수(BMI) 25kg/m2 미만 및 허리둘레 " + sex + "cm 미만 유지를 목표로 합니다. \n" +
                            "③ 식사조절과 함께 신체활동량을 늘려주세요.";
                }
                // 고혈압 2기 두번째 (흡연중일 경우 추가 메시지)
                if (login.smkng_yn.equals("Y")) {

                    if (message != "")
                        message += "\n\n";
                    message += "[금연]\n담배에 함유된 니코틴에 의해 일시적으로 혈압과 맥박이 상승됩니다. 흡연은 고혈압과 마찬가지로 심혈관질환의 강력한 위험인자이므로 완전한 금연이 필요합니다.";
                }
                // 고혈압 2기 두번째 (활동량 1,2번 선택인 경우 추가 메시지)
                if ((login.mber_actqy.equals("1")) || (login.mber_actqy.equals("2"))) {

                    if (message != "")
                        message += "\n\n";
                    message += "[운동]\n" +
                            "① 효과 : 하루 30~50분, 일주일에 5일 이상 운동 시 수축기혈압은 -4.9mmHg, 이완기혈압은 -3.7mmHg 감소효과가 있습니다.\n" +
                            "② 방법 : 유산소 운동을 기본으로 하되 무산소 운동을 일주일에 2~3회 정도 병행하면 좋습니다. \n" +
                            "③ 주의 : 혈압이 조절되지 않는 경우 무거운 것을 들어올리는 것과 같은 무산소 운동은 피하세요.";

                }
                // 고혈압 2기 두번째 (180/120 mmHg 이상인 경우 추가 메시지)
                if ((systolic >= 180) && (diastolic >= 120)) {

                    if (message != "")
                        message += "\n\n";
                    message +=  "[고혈압성 응급 주의]\n" +
                            "고혈 압성 응급이란 혈압이 급격하게 상승하면서 장기에 손상이 동반되는 응급상황입니다. 하지만 장기의 손상이 없을 경우 고혈압성 응급에 해당하지 않을 수도 있습니다. \n" +
                            "① 증상 : 망막 출혈 및 부종, 심한 두통, 호흡곤란, 코피, 불안증 등의 증상을 보일 수 있습니다. \n" +
                            "② 대처 : 고혈압성 응급의 경우 해당 전문의의 치료가 필요합니다. 반드시 병원에 방문하세요.";
                }
                // 고혈압 2기 두번째 (200/110 mmHg 이상인 경우 추가 메시지)
                if ((systolic >= 200) && (diastolic >= 110)) {

                    if (message != "")
                        message += "\n\n";
                    message += "[운동 시 주의사항]\n" +
                            "① 운동 전 : 수축기혈압 200mmHg 이상, 이완기혈압 110mmHg 이상이면 운동을 하지 않는 것이 좋습니다. \n" +
                            "② 운동 중 : 수축기혈압 250mmHg 이상, 이완기혈압 115mmHg 이상일 경우 운동을 중지해야 합니다.";
                }
            } else if(!isTwoStep10Minit) {
                SharedPref.getInstance().savePreferences(SharedPref.PRESURE_TWOSTEP_COUNT, 0);
                message = getPressureMessage(baseFragment, systolic, diastolic);
            }
            SharedPref.getInstance().savePreferences(SharedPref.PRESURE_TWOSTEP_TIME, currString);
            SharedPref.getInstance().savePreferences(SharedPref.PRESURE_TWOSTEP_COUNT, twoStepCount + 1);
            return message;
        }
        SharedPref.getInstance().savePreferences(SharedPref.PRESURE_LAST_SAVE_TIME, currString);
        Logger.i(TAG, "getPressureMessage=" + message);
        return message;
    }
}