package com.greencross.greencare.bluetooth.device;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.greencross.greencare.bluetooth.model.BandModel;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.SharedPref;
import com.partron.wearable.band.sdk.core.BandResultCode;
import com.partron.wearable.band.sdk.core.BandUUID;
import com.partron.wearable.band.sdk.core.ConnectionState;
import com.partron.wearable.band.sdk.core.PWB_ClientManager;
import com.partron.wearable.band.sdk.core.UserProfile;
import com.partron.wearable.band.sdk.core.interfaces.BandConnectStateCallback;
import com.partron.wearable.band.sdk.core.interfaces.BandUrbanMeasureListener;
import com.partron.wearable.band.sdk.core.interfaces.OnCompleteListener;
import com.partron.wearable.band.sdk.core.interfaces.PWB_Client;
import com.partron.wearable.band.sdk.core.item.UrbanInfoItem;
import com.partron.wearable.band.sdk.core.item.UrbanInfoSyncItem;
import com.partron.wearable.band.sdk.core.item.UrbanPPGInfoSyncItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jongrakmoon on 2017. 3. 25..
 */

public class BandDevice extends BaseDevice<BandModel> {
    private final String TAG = BandDevice.class.getSimpleName();

    private PWB_ClientManager pwbClientManager;
    private PWB_Client pwbClient;
    private UserProfile userProfile;

    private OnBluetoothListener<BandModel> listener;

    public BandDevice(BluetoothDevice bluetoothDevice) {
        super(bluetoothDevice);
        this.pwbClientManager = PWB_ClientManager.getInstance();
        this.userProfile = new UserProfile();
    }

    public void setUserInfo(int age, int height, int weight, Gender gender) {

            userProfile.setAge(age);
            userProfile.setHeight(height);
            userProfile.setWeight(weight);
            userProfile.setGender(gender.getValue() ? 0 : 1);
    }

    public void setOnBluetoothListener(OnBluetoothListener<BandModel> listener) {
        this.listener = listener;
    }


    @Override
    public void connect(Context context) {

        Log.d(TAG, "???????????? ?????? ?????? ??????");

        if (listener != null) {
            listener.onStart();
        }
        pwbClient = pwbClientManager.create(context, userProfile, BandUUID.PWB_200);
        pwbClient.bandConnect(bluetoothDevice.getAddress());
        pwbClient.registerBandConnectStateCallback(new BandConnectStateCallback() {
            @Override
            public void onBandConnectState(final int state, final ConnectionState connectionState) {
                if (listener != null) {
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (state == BandResultCode.SUCCESS) {
                                switch (connectionState) {
                                    case CONNECTED:
                                        Log.d(TAG, "???????????? ?????? ?????? ??????");
                                        listener.onConnected();
                                        break;
                                    case DISCONNECTED:
                                        Log.d(TAG, "???????????? ?????? ?????? ??????");
                                        disconnect();
                                        break;
                                }
                            } else {
                                Log.d(TAG, "???????????? ?????? ?????? ??????");
                                disconnect();
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    public void disconnect() {

        if (pwbClient != null) {
            pwbClient.bandDisconnect();
        }
        pwbClient = null;

        if (listener != null) {
            listener.onDisConnected();
        }
    }

    @Override
    public boolean readData() {
        return false;
    }

    public boolean readTimeData() {

        // ????????? ?????? ??? ????????? ?????????.
        if (TextUtils.isEmpty(SharedPref.getInstance().getPreferences(SharedPref.STEP_TOTAL_SAVEDATE))){
            // ???????????????
            SharedPref.getInstance().savePreferences(SharedPref.STEP_TOTAL_SAVEDATE, CDateUtil.getToday_yyyy_MM_dd());
            SharedPref.getInstance().savePreferences(SharedPref.STEP_TOTAL_COUNT, 0);
            SharedPref.getInstance().savePreferences(SharedPref.STEP_CALRORI_TOTAL, 0);
            SharedPref.getInstance().savePreferences(SharedPref.STEP_DISTANCE_TOTAL, 0.0f);
        }else{
            // ????????? ????????? ????????? ?????? ??? ????????? ?????????.
            String stepTotDate = SharedPref.getInstance().getPreferences(SharedPref.STEP_TOTAL_SAVEDATE);
            if (!stepTotDate.equals(CDateUtil.getToday_yyyy_MM_dd())){
                SharedPref.getInstance().savePreferences(SharedPref.STEP_TOTAL_SAVEDATE, CDateUtil.getToday_yyyy_MM_dd());
                SharedPref.getInstance().savePreferences(SharedPref.STEP_TOTAL_COUNT, 0);
                SharedPref.getInstance().savePreferences(SharedPref.STEP_CALRORI_TOTAL, 0);
                SharedPref.getInstance().savePreferences(SharedPref.STEP_DISTANCE_TOTAL, 0.0f);
            }
        }

        if (pwbClient != null) {

            // ????????? ????????? ????????????
            pwbClient.getUrbanMode().getUrbanInfo(new OnCompleteListener() {
                @Override
                public void onResult(int result, Object item) {

                    Log.d(TAG, "?????? ????????? ????????? ???????????? ??????..");

                    final BandModel bandModel;
                    if (result == BandResultCode.SUCCESS) {

                        bandModel = new BandModel((UrbanInfoItem) item);

                        Log.d(TAG, "?????? ????????? ????????? ???????????? ??????...:" + bandModel.toString());

                        int totStep         = SharedPref.getInstance().getPreferences(SharedPref.STEP_TOTAL_COUNT, 0);
                        int totCalorie      = SharedPref.getInstance().getPreferences(SharedPref.STEP_CALRORI_TOTAL, 0);
                        float totDistance   = SharedPref.getInstance().getPreferences(SharedPref.STEP_DISTANCE_TOTAL, 0.0f);

                        int realStep        = bandModel.getStep() - totStep;
                        int realCalorie     = bandModel.getCalories() - totCalorie;
                        double realDistance  = bandModel.getDistance() - totDistance;
                        realDistance = Math.round(realDistance*1000d) / 1000d;

                        if (totStep < 0) totStep = 0;
                        if (realCalorie < 0) realCalorie = 0;
                        if (realDistance < 0) realDistance = 0.0f;

                        Log.d(TAG, "????????? ?????????:"+totStep +", totCalorie:"+totCalorie +", totDistance:"+totDistance);
                        Log.d(TAG, "?????? ??????:"+realStep +", realCalorie:"+realCalorie +", realDistance:"+realDistance);

                        if (realStep <= 0){
                            Log.d(TAG, "????????? ????????? ??????");
                            return;
                        }else{
                            SharedPref.getInstance().savePreferences(SharedPref.STEP_TOTAL_COUNT, bandModel.getStep());
                            SharedPref.getInstance().savePreferences(SharedPref.STEP_CALRORI_TOTAL, bandModel.getCalories());
                            SharedPref.getInstance().savePreferences(SharedPref.STEP_DISTANCE_TOTAL, bandModel.getDistance());

                            //?????? ?????????????????? ??????.
                            bandModel.setStep(realStep);
                            bandModel.setCalories(realCalorie);
                            bandModel.setDistance((float)realDistance);
                            bandModel.setHeartRate(0);
                        }
                    } else {
                        bandModel = null;
                        Log.d(TAG, "?????? ????????? ????????? ???????????? ??????...:");
                    }

                    if (listener != null) {
                        mUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onReceivedData(bandModel);
                            }
                        });
                    }
                }
            });

            // ????????? ????????? ????????????
            pwbClient.getUrbanMode().getUrbanInfoSync(new OnCompleteListener() {
                @Override
                public void onResult(int result, Object item) {

                    Log.d(TAG, "?????? ????????? ????????? ????????? ???????????? ??????.");
                    final List<BandModel> models = new ArrayList<>();
                    if (result == BandResultCode.SUCCESS) {
                        if (item != null) {
                            List<UrbanInfoSyncItem> listItem = (List<UrbanInfoSyncItem>) item;

                            for (UrbanInfoSyncItem info : listItem) {
                                Log.d(TAG, "UrbanINFO ::: Time:"+ info.getTime() +", step:"+ info.getStep()+", distance:"+ info.getDistance()+", calories:"+ info.getCalories());
                                if (info.getStep() > 0) {
                                    models.add(new BandModel(info));
                                }
                            }
                            Log.d(TAG, "?????? ????????? ????????? ????????? ???????????? ??????:" + models);
                        } else {
                            Log.d(TAG, "?????? ????????? ????????? ????????? ???????????? ??????");
                        }
                    } else {
                        Log.d(TAG, "?????? ????????? ????????? ????????? ??????.");
                    }
                    if (listener != null) {
                        mUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (models != null & models.size() > 0)
                                    listener.onReceivedTimeData(models);
                            }
                        });
                    }
                }
            });

            // ???????????? ?????????
//            pwbClient.getBandSettings().setRTC(new OnCompleteListener() {
//                @Override
//                public void onResult(int result, Object object) {
//                    if(result == BandResultCode.SUCCESS){
//                        Log.d(TAG, "???????????? ?????????:"+result);
//                    }
//                }
//            });

            return true;
        }
        return false;
    }

    public boolean readTimePPGData() {
        if (pwbClient != null) {
            if (pwbClient != null) {
                Log.d(TAG, "?????? ????????? ????????? ????????? ???????????? ??????.");
                pwbClient.getUrbanMode().getUrbanPPGInfoSync(new OnCompleteListener() {
                    @Override
                    public void onResult(int result, Object item) {
                        final List<BandModel> models = new ArrayList<>();
                        if (result == BandResultCode.SUCCESS) {
                            if (item != null) {
                                List<UrbanPPGInfoSyncItem> listItem = (List<UrbanPPGInfoSyncItem>) item;
                                for (UrbanPPGInfoSyncItem info : listItem) {
                                    Log.d(TAG, "UrbanPPGInfo ::: Hrm:"+ info.getHrm() +", Hour:"+ info.getHour()+", Minute:"+ info.getMinute()+", Stress:"+ info.getStress());
                                    if (info.getHrm()> 0) {
                                        models.add(new BandModel(info));
                                    }
                                }
                                Log.d(TAG, "?????? ????????? ????????? ????????? ???????????? ??????:" + models);

                            } else {
                                Log.d(TAG, "?????? ????????? ????????? ????????? ???????????? ??????");
                            }
                        } else {
                            Log.d(TAG, "?????? ????????? ????????? ????????? ??????.");
                        }
                        if (listener != null) {
                            mUIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (models!= null & models.size() > 0)
                                        listener.onReceivedPPGData(models);

                                }
                            });
                        }
                    }
                });
                return true;
            } else {
                Log.d(TAG, "???????????? ????????? ???????????? ?????? ????????????.");
                return false;
            }
        }
        return false;
    }

    public void measureHeartRate() {
        Log.d(TAG, "???????????? ?????? ???????????? ??????...");

        if (pwbClient != null) {
            pwbClient.getMeasureMode().ppgMeasure(new BandUrbanMeasureListener() {
                @Override
                public void onPPGResult(final int result, final int status, final int hrm, final int stress) {
                    if (listener != null) {
                        mUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (result == BandResultCode.SUCCESS) {
                                    listener.onHeartRateMeasured(hrm);
                                    Log.d(TAG, "???????????? ?????? ???????????? ??????...:" + hrm + "bps");
                                } else {
                                    listener.onHeartRateMeasured(-1);
                                    Log.d(TAG, "???????????? ?????? ???????????? ??????...:");
                                }
                            }
                        });
                    }
                }

                @Override
                public void onAlitudeResult(int result, int altitude) {
                }
            });
        } else {
            Log.d(TAG, "???????????? ????????? ???????????? ?????? ????????????.");
        }
    }

    public void mesureAltitude(float basePressure) {
        Log.d(TAG, "???????????? ?????? ???????????? ??????...");

        if (pwbClient != null) {
            pwbClient.getMeasureMode().altitudeMeasure(basePressure, new BandUrbanMeasureListener() {
                @Override
                public void onPPGResult(int i, int i1, int i2, int i3) {

                }

                @Override
                public void onAlitudeResult(final int result, final int altitude) {
                    if (listener != null) {
                        mUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (result == BandResultCode.SUCCESS) {
                                    Log.d(TAG, "???????????? ?????? ???????????? ??????...:" + altitude + "??????");
                                    listener.onAltitudeMeasured(altitude);
                                } else {
                                    Log.d(TAG, "???????????? ?????? ???????????? ??????...:");
                                    listener.onHeartRateMeasured(-1);
                                }
                            }
                        });

                    }
                }
            });
        } else {
            Log.d(TAG, "???????????? ????????? ???????????? ?????? ????????????.");
        }
    }

    public interface OnBluetoothListener<M> extends BaseDevice.OnBluetoothListener<M> {
        public void onStart();

        public void onConnected();

        public void onDisConnected();

        public void onReceivedData(M dataModel);

        public void onReceivedTimeData(List<M> dataModel);

        public void onReceivedPPGData(List<M> dataModel);

        public void onHeartRateMeasured(int heartRate);

        public void onAltitudeMeasured(int altitude);
    }

}
