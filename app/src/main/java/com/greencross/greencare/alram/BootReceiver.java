package com.greencross.greencare.alram;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.greencross.greencare.MainActivity;
import com.greencross.greencare.R;
import com.greencross.greencare.SplashActivity;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.bluetooth.model.MessageModel;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperMessage;
import com.greencross.greencare.database.DBHelperSugar;
import com.greencross.greencare.network.tr.ApiData;
import com.greencross.greencare.network.tr.CConnAsyncTask;
import com.greencross.greencare.network.tr.data.Tr_infra_message_write;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.NetworkUtil;
import com.greencross.greencare.util.SharedPref;
import com.greencross.greencare.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.greencross.greencare.util.CDateUtil.getForamtyyMMddHHmmssSS;
import static com.greencross.greencare.util.CDateUtil.getFormatmmddhhmm;

/**
 * Created by godaewon on 2017. 5. 16..
 */

public class BootReceiver extends BroadcastReceiver {

    private final String TAG = BootReceiver.class.getSimpleName();
    private Context mcontext;
    private BaseFragment mBaseFragment;

    @Override
    public void onReceive(Context context, Intent intent) {
        mBaseFragment = new BaseFragment();
        mcontext = context;

        Calendar calendar = Calendar.getInstance();

        calendar.set(calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DATE)
                , calendar.get(Calendar.HOUR)
                , calendar.get(Calendar.MINUTE)
                , calendar.get(Calendar.SECOND)); // 오늘날짜

        String dayRedate = getForamtyyyyMMddHHmmssSS(new Date(System.currentTimeMillis()));
        Logger.i(TAG, "BootReceiver start dayRedate : " + dayRedate);
        int week_day = calendar.get(Calendar.DAY_OF_WEEK);
        int day = calendar.get(Calendar.DATE);
        int month_week = calendar.get(Calendar.WEEK_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);

        calendar.set(calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , 1
                , calendar.get(Calendar.HOUR)
                , calendar.get(Calendar.MINUTE)
                , calendar.get(Calendar.SECOND)); // 오늘날짜

        int month_week_day = calendar.get(Calendar.DAY_OF_WEEK);

        if (month_week_day == 5 || month_week_day == 6 || month_week_day == 0) {
            month_week = month_week - 1;
        }

        DBHelper helper = new DBHelper(context);
        DBHelperSugar db = helper.getSugarDb();
        String msg = "";

        String overCheck = SharedPref.getInstance().getPreferences(SharedPref.SUGAR_OVER_CHECK);
        String oldTime = SharedPref.getInstance().getPreferences(SharedPref.SUAGR_OVER_TIME);
        if (TextUtils.isEmpty(overCheck)) {
            overCheck = "0";
        }
        if (TextUtils.isEmpty(oldTime)) {
            oldTime = "0";
        }
        int twoWeek = 1000 * 60 * 24 * 14;
        String nowTime = StringUtil.getFormattedDateTime();

        Logger.i("intent", "intent get type" + intent.getType());
        if (intent.getType().equals("0")) {
            //자정에 울리는 알람
            if (week_day == 1) {
                // 3/4 번 울리게하기
                DBHelperSugar.SugarStaticData weekBeforeData = db.getBeforeMaxSugar("w");
                DBHelperSugar.SugarStaticData weekAfterData = db.getAfterMaxSugar("w");

                if (weekBeforeData.getRedate() != "") {
                    MessageModel messageModel = new MessageModel();
                    messageModel.setIdx(getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
                    messageModel.setSugar("" + weekBeforeData.getMaxsugar());
                    messageModel.setRegdate("" + dayRedate);
                    messageModel.setMessage("지난 주 식전 혈당이 가장 높았던 날은 " + getFormatmmddhhmm(weekBeforeData.getRedate()) + " " + String.format("%d", weekBeforeData.getMaxsugar()) + " mg/dL 입니다.");

                    insertMesageDb(mBaseFragment, messageModel);
                }

                if (weekAfterData.getRedate() != "") {
                    MessageModel messageModel = new MessageModel();
                    messageModel.setIdx(getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
                    messageModel.setSugar("" + weekAfterData.getMaxsugar());
                    messageModel.setRegdate("" + dayRedate);
                    messageModel.setMessage("지난 주 식후 혈당이 가장 높았던 날은 " + getFormatmmddhhmm(weekAfterData.getRedate()) + " " + String.format("%d", weekAfterData.getMaxsugar()) + " mg/dL 입니다.");

                    insertMesageDb(mBaseFragment, messageModel);
                }
            }

            if (day == 1) {
                // 5/6번 울리게 하기
                DBHelperSugar.SugarStaticData monthBeforeData = db.getBeforeMaxSugar("m");
                DBHelperSugar.SugarStaticData monthAfterData = db.getAfterMaxSugar("m");

                if (monthBeforeData.getRedate() != "") {
                    MessageModel messageModel = new MessageModel();
                    messageModel.setIdx(getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
                    messageModel.setSugar("" + monthBeforeData.getMaxsugar());
                    messageModel.setRegdate("" + dayRedate);
                    messageModel.setMessage("지난 달 식전 혈당이 가장 높았던 날은 " + getFormatmmddhhmm(monthBeforeData.getRedate()) + " " + String.format("%d", monthBeforeData.getMaxsugar()) + " mg/dL 입니다.");

                    insertMesageDb(mBaseFragment, messageModel);
                }

                if (monthAfterData.getRedate() != "") {
                    MessageModel messageModel = new MessageModel();
                    messageModel.setIdx(getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
                    messageModel.setSugar("" + monthAfterData.getMaxsugar());
                    messageModel.setRegdate("" + dayRedate);
                    messageModel.setMessage("지난 달 식후 혈당이 가장 높았던 날은 " + getFormatmmddhhmm(monthBeforeData.getRedate()) + " " + String.format("%d", monthBeforeData.getMaxsugar()) + " mg/dL 입니다.");

                    insertMesageDb(mBaseFragment, messageModel);
                }
            }

            if (overCheck.equals("Y") && (StringUtil.getLong(nowTime) - StringUtil.getLong(oldTime)) >= twoWeek) {
                SetNotication(context, "2주 동안 혈당 결과가 없습니다. 혈당 관리 잘하고 계시나요? \n" + "정기적으로 혈당을 체크하고, 기록하는 것은 당뇨관리에 큰 도움이 됩니다. 오늘부터 실천하세요.");
            }

        } else if (intent.getType().equals("1")) {
            // 09시에 울리는 알람

            if (day == 25) {
                // 8번 울리게 하기
                String dataEmpty = db.getSugarDataCheck();
                if (dataEmpty.equals("N")) {
                    SetNotication(context, "혈당을 체크하고 기록해보세요.\n치료에 대한 반응이나 치료 후 목표 혈당에 도달했는지 알수있는 당뇨의 중요한 관리 수단 입니다.");

                    MessageModel messageModel = new MessageModel();
                    messageModel.setIdx(getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
                    messageModel.setSugar("" + "0");
                    messageModel.setRegdate("" + dayRedate);
                    messageModel.setMessage("혈당을 체크하고 기록해보세요.\n치료에 대한 반응이나 치료 후 목표 혈당에 도달했는지 알수있는 당뇨의 중요한 관리 수단 입니다.");

                    insertMesageDb(mBaseFragment, messageModel);
                }
            }

            if (week_day == 1) {
                // 9 번 울리게하기
                String SugarOver = db.getHightSugarCheck();
                if (SugarOver.equals("Y")) {
                    String message = "";
                    if (month_week == 2 || month_week == 3 || month_week == 4) {
                        message = getSugarMonthWeekMessage(month_week, month);
                        SetNotication(context, message);

                        MessageModel messageModel = new MessageModel();
                        messageModel.setIdx(getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
                        messageModel.setSugar("" + "0");
                        messageModel.setRegdate("" + dayRedate);
                        messageModel.setMessage(message);

                        insertMesageDb(mBaseFragment, messageModel);
                    }
                }
            }
        }
        Logger.i(TAG, "BootReceiver=" + intent);
    }


    public void SetNotication(Context context, String msg) {

        // DB 연결 조회
        DBHelper helper = new DBHelper(context);
        DBHelperMessage db = helper.getMessageDb();
        List<MessageModel> models = db.getResultAll(helper);
        String message = models.size() > 0 ? models.get(0).getMessage() : mcontext.getString(R.string.text_actionbar_one_line_message);

        Resources res = context.getResources();
        Intent notificationIntent = new Intent(context, BootReceiver.class);
        notificationIntent.putExtra("notificationId", 9999); //전달할 값

        PendingIntent pendingIntent = null;
        Tr_login loginInfo = Define.getInstance().getLoginInfo();
        if (loginInfo != null) {
            pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentTitle(mcontext.getString(R.string.text_alert))
                .setContentText(msg)
                .setTicker(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);

        Log.i("HelloAlarmActivitypen", Long.toString(System.currentTimeMillis()));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1234, builder.build());
    }


    /**
     * 건강메시지 서버 전송 및 sqlite 저장
     *
     * @param baseFragment
     * @param model
     */
    private void insertMesageDb(final BaseFragment baseFragment, final MessageModel model) {
        Tr_infra_message_write.RequestData reqData = new Tr_infra_message_write.RequestData();
        Tr_login login = Define.getInstance().getLoginInfo();
        reqData.idx = model.getIdx();
        reqData.mber_sn = login.mber_sn;
        reqData.infra_message = model.getMessage();

        getData(Tr_infra_message_write.class, reqData, new ApiData.IStep() {
            @Override
            public void next(Object obj) {

                DBHelper helper = new DBHelper(mcontext);
                DBHelperMessage db = helper.getMessageDb();
                if (obj instanceof Tr_infra_message_write) {
                    Tr_infra_message_write data = (Tr_infra_message_write) obj;
                    SharedPref.getInstance().savePreferences(SharedPref.HEALTH_MESSAGE, true);
                    db.insert(model, "Y".equals(data.reg_yn));
                } else {
                    db.insert(model, false);
                }
            }
        });
    }


    public void getData(final Class<?> cls, final Object obj, final ApiData.IStep step) {
        CConnAsyncTask.CConnectorListener queryListener = new CConnAsyncTask.CConnectorListener() {
            @Override
            public Object run() {
                ApiData data = new ApiData();
                return data.getData(mcontext, cls, obj);
            }


            @Override
            public void view(CConnAsyncTask.CQueryResult result) {
                Logger.i(TAG, "result.result=" + result.result + ", result.data=" + result.data);
                if (result.result == CConnAsyncTask.CQueryResult.SUCCESS && result.data != null) {
                    if (step != null) {
                        step.next(result.data);
                    }
                } else {
                    Logger.e(TAG, mcontext.getString(R.string.text_network_data_rec_fail));
                    Logger.e(TAG, "CConnAsyncTask error=" + result.errorStr);
                    try {
                        CDialog.showDlg(mcontext, mcontext.getString(R.string.text_network_data_rec_fail), new CDialog.DismissListener() {
                            @Override
                            public void onDissmiss() {
                            }
                        });
                    } catch (Exception e) {
                        try {
                            CDialog.showDlg(mcontext, mcontext.getString(R.string.text_network_data_rec_fail), new CDialog.DismissListener() {
                                @Override
                                public void onDissmiss() {
                                }
                            });
                        } catch (Exception e2) {
                        }
                    }
                }
            }
        };

        CConnAsyncTask asyncTask = new CConnAsyncTask();
        asyncTask.execute(queryListener);
    }


    public String getSugarMonthWeekMessage(int month_week, int month) {
        String message = "";
        if (month_week == 2) {
            if (month == 1) {
                message = "[혈당 조절 목표] 대한당뇨병 학회에서는 식전혈당 80~130mg/dL, 식후혈당 180 mg/dL 아래로, 당화혈색소는 6.5% 아래로 조절하는 것을 목표로 합니다. \n" +
                        "(단, 당뇨병을 앓은 기간, 나이, 기대여명, 합병증 유무 등 환자 개개인의 상태에 따라 혈당조절 목표를 개별화할 수 있습니다.)";
            } else if (month == 2) {
                message = "[자가혈당측정 시 유의사항]\n" +
                        "- 검사 전에 손을 비누로 씻고, 완전히 말린 후에 체크하세요.\n" +
                        "- 검사지는 가능한 공기에 닿지 않도록 뚜껑을 닫아 보관하고, 직사광선을 피하세요.\n" +
                        "- 손가락 끝을 짜거나 자극을 주는 것은 검사의 정확성을 떨어뜨릴 수 있습니다. \n" +
                        "- 채혈침을 재사용하지 말고, 유효기간이 지난 검사지는 사용하지 말아야 합니다.";
            } else if (month == 2) {
                message = "[자가혈당측정 시 유의사항]\n" +
                        "- 검사 전에 손을 비누로 씻고, 완전히 말린 후에 체크하세요.\n" +
                        "- 검사지는 가능한 공기에 닿지 않도록 뚜껑을 닫아 보관하고, 직사광선을 피하세요.\n" +
                        "- 손가락 끝을 짜거나 자극을 주는 것은 검사의 정확성을 떨어뜨릴 수 있습니다. \n" +
                        "- 채혈침을 재사용하지 말고, 유효기간이 지난 검사지는 사용하지 말아야 합니다.";
            } else if (month == 3) {
                message = "[혈당과 약물] 혈당이 올랐을 때 혈당을 올릴만한 원인 약물을 투여하고 있는지 확인해보세요.\n" +
                        "천식, 류마티스관절염, 전신홍반루푸스, 염증성 장질환 등 다양한 질환에서 사용하는 스테로이드 제제가 대표적입니다. \n" +
                        "먹는 약 뿐만 아니라 주사제나 피부에 바르는 스테로이드 제제도 혈당을 올릴 수 있습니다. \n" +
                        "인터페론, 면역억제제 등의 약물도 혈당을 올릴 수 있습니다. \n" +
                        "혈당을 올릴 수 있다는 이유로 무조건 그 약물을 피하는 것은 곤란합니다.\n" +
                        " 의사와 상의해 투약하면서 혈당 변화에 유의해야 합니다.";
            } else if (month == 4) {
                message = "[자가혈당측정] 자가혈당측정은 치료에 대한 반응이나 치료 후 목표 혈당에 도달했는지 알 수 있는 중요한 관리 수단입니다. \n" +
                        "-고혈당이나 저혈당을 신속히 확인, 관리 가능\n" +
                        "-생활습관에 따른 혈당 반응을 이해하고 관리 가능\n" +
                        "-혈당을 참조해 당뇨병 치료제의 용량 조정 가능\n" +
                        "-의료진의 진료와 상담의 기초자료로 활용 가능";
            } else if (month == 5) {
                message = "[당화혈색소] 당화혈색소는 최근 3개월 동안 혈당 조절 정도를 가늠할 수 있을 뿐만 아니라 환자가 측정한 자가혈당측정치의 정확성을 판단하는데 도움이 됩니다.\n" +
                        "당화혈색소는 3개월마다 측정을 고려하지만 혈당 조절 정도, 환자 상황, 사용 중인 경구혈당강하제의 종류 등을 고려해 3개월보다 짧거나 더 긴 간격으로 조정해 측정할 수 있습니다. \n" +
                        "혈당 변화가 심할 때, 치료 약물을 변경했을 때, 임신과 같이 좀 더 철저한 혈당 조절이 필요할 때 당화혈색소를 더 자주 측정할 수 있습니다.";
            } else if (month == 6) {
                message = "[혈당과 스트레스] 여러 질병, 외상, 입원 등 스트레스 상황에서 혈당을 올리는 호르몬이 나오게 됩니다. \n" +
                        "이것은 인슐린과 반대되는 작용을 하기 때문에 스트레스를 받으면 혈당이 올라갈 수 있습니다. \n" +
                        "스트레스 유발성 고혈당에서도 정도에 따라 단기간 인슐린 주사 등 약물치료가 필요할 수 있습니다.";
            } else if (month == 7) {
                message = "[저혈당 증상]\n" +
                        "1)자율신경 항진 : 빈맥, 식은땀, 불안감, 배고픔, 오심, 손떨림, 창백한 얼굴\n" +
                        "2)신경당결핍 : 집중이 안됨, 의식혼미, 기력약화, 어지럼, 시력변화, 말하기 힘듦, 두통\n" +
                        "위와 같은 증상이 나타날 경우 주스나 사탕, 설탕물 등의 당질을 섭취하세요. ";
            } else if (month == 8) {
                message = "[저혈당 대처] \n" +
                        "1)의식이 있는 경우 : 15~20g의 당질을 섭취하세요.\n" +
                        "주스나 청량음료 3/4컵(175mL)=사탕 3~4개=설탕 또는 꿀 한 숟가락=요구르트 1.5개\n" +
                        "(단, 지방이 포함된 초콜릿, 아이스크림 등은 혈당을 올리는 작용이 지연될 수 있으므로 저혈당 치료에 부적합)\n" +
                        "2)의식이 없는 경우 : 질식의 우려가 있으므로 입안에 음식물을 넣지 말고 병원으로 응급 이송하세요.";
            } else if (month == 9) {
                message = "[혈당 변동성과 합병증] 혈당이 낮을 때와 높을 때의 차이가 클 경우 활성산소종의 발생이 증가하여 산화스트레스가 커지고 이로 인해 미세혈관∙대혈관 합병증이 증가할 수 있습니다.";
            } else if (month == 10) {
                message = "[당뇨병 눈관리] 당뇨병성 망막병증 초기에는 아무런 증상이 없을 수 있으므로 모르는 사이에 망막병증이 진행되면서 악화될 수 있습니다. \n" +
                        "따라서, 시력저하 등 증상이 없더라도 당뇨병성 망막병증의 조기 발견을 위해 정기적으로 안과 검진을 받아야 합니다.";
            } else if (month == 11) {
                message = "[당뇨병 발관리] \n" +
                        "당뇨병 발 합병증 예방을 위해 매일 거울을 이용해 발 상태를 확인해야 합니다. \n" +
                        "발을 깨끗이 씻고 물기를 잘 닫은 후 건조해지지 않도록 합니다.\n" +
                        "맨발로 다니지 말고 편안한 양말과 신발을 신습니다. 발톱은 너무 바짝 깎지 말고 담배는 꼭 끊도록 합니다.";
            } else if (month == 12) {
                message = "[당뇨병과 가족의 역할] 당뇨병 관리는 식사 조절과 운동요법 등 생활전반의 태도나 습관이 모두 연결되어 있습니다. \n" +
                        "가족이 함께 노력하면, 관리효과가 좋아질 뿐만 아니라 가족 전체의 건강을 지키는데도 큰 도움이 됩니다. ";
            }


        } else if (month_week == 3) {
            if (month == 1) {
                message = "[당뇨식=건강식] 당뇨의 식사관리는 무조건 어떤 음식을 줄이거나 제한하는 것이 아니라 건강한 식사를 계획하고 실천하는 것입니다. 특정 식품 제한은 과학적 근거가 있는 경우에만 하고, 먹는 즐거움을 유지 할 수 있도록 해야합니다.";
            } else if (month == 2) {
                message = "[혈당과 식사시간] 식사시간에 맞추어 제때에 20분 이상 천천히 식사를 하세요. 불규칙한 식사는 과식이나 폭식으로 이어지기 쉽고, 끼니를 자주 거르면 기초대사량이 낮아져 쉽게 살이 찌며, 체지방 증가로 인해 인슐린의 작용이 방해를 받아 혈당 관리가 어려워 질 수 있습니다.";
            } else if (month == 3) {
                message = "[균형잡힌 식생활] 어느 한 영양소라도 부족하거나 과다 섭취하면 영양의 균형이 깨지게 됩니다. 다양한 식품을 통해 여러가지 영양소를 골고루 섭취하도록 하세요.";
            } else if (month == 4) {
                message = "[기름진 음식 섭취 주의] 육류 섭취시 기름이 많은 갈비, 삼겹살 대신 살코기 위주로 선택하시고, 튀김보다는 구이, 찜 등 기름을 적게 사용하는 조리법을 선택하세요.";
            } else if (month == 5) {
                message = "[당분이 많은 식품 섭취 주의] 밥, 떡, 빵, 면류 등 곡류군은 당질함량이 많으므로 1일 식사량을 엄수하고, 설탕, 물엿, 시럽, 사탕 등 당분이 많은 식품 과잉섭취를 피하세요.";
            } else if (month == 6) {
                message = "[혈당과 섬유소] 잡곡, 채소, 해조류 등 섬유소가 많은 식품 섭취는 급격한 혈당상승을 억제하는데 도움이 됩니다.";
            } else if (month == 7) {
                message = "[혈당과 당분섭취] 당분이 높은 과일의 섭취는 혈당을 올릴 수 있으므로 과잉섭취하지 않도록 주의하고, 쥬스형태 보다는 생으로 드시는 것이 좋습니다.";
            } else if (month == 8) {
                message = "[당뇨병 약물치료와 음식섭취] 약물치료 중일 경우 식사를 거르면 저혈당이 발생할 수 있으므로 주의하시고, 매일 일정한 시간에 알맞은 양의 음식을 규칙적으로 섭취하세요.";
            } else if (month == 9) {
                message = "[당지수란?] 식후 당질의 흡수 속도를 반영하여 당질의 질을 비교할 수 있도록 수치화 한 것입니다. \n" +
                        "당지수가 낮은 음식은 높은 음식에 비해 혈당을 천천히 상승시키는 효과가 있습니다. \n" +
                        "당지수가 55 이하인 경우를 저당지수 식품, 70 이상인 경우를 고당지수 식품으로 분류합니다.";
            } else if (month == 10) {
                message = "[당뇨병과 아침식사] 아침식사를 거르게 되면 신체는 에너지가 부족하여 집중력과 업무능력이 떨어지고 저혈당의 위험이 높아집니다. 간식섭취가 많아지거나 점심식사를 과식하게 되어 혈당관리가 더 어렵게 될 수 있으니 아침을 거르지 않고, 간단하게라도 식사를 하는 것이 좋습니다.";
            } else if (month == 11) {
                message = "[부엌에서 당뇨병 잡기, 생활 속 조리법 Tips]\n" +
                        "1) 어육류는 눈에 보이는 지방과 껍질은 제거하고 섭취하세요.\n" +
                        "2) 튀김보다는 찜, 구이, 삶기 등의 방법으로 조리하세요.\n" +
                        "3) 채소 섭취 시 소스나 기름 사용량에 주의하세요.\n" +
                        "4) 조리 시에는 고체성 기름(버터, 마가린 등)보다 식물성 기름(참기름, 들기름, 올리브유 등)을 사용하세요.\n" +
                        "5) 동일한 식품 중에서 영양성분표를 확인하고, 상대적으로 당, 지방 함량이 적은 식품을 선택하세요.";
            } else if (month == 12) {
                message = "[외식 시 주의사항] 외식도 가급적이면 제때에 식사하도록 하고, 외식 전에 식사를 거르지 않도록 합니다. 식사계획에 잘 맞추어 곡류, 어육류, 채소, 지방이 골고루 배합된 음식을 선택하는 것이 중요합니다.";
            }

        } else if (month_week == 4) {
            if (month == 1) {
                message = "[당뇨병과 운동] 혈당 조절에 도움이 되는 유산소 운동으로는 빨리 걷기, 자전거타기, 수영, 에어로빅 등이 있고, 자신의 체중이나 중력을 이용한 스쿼트, 런지 등과 같은 근력 운동을 병행하는 것이 좋습니다. \n" +
                        "※관절이상, 심뇌혈관질환, 망막질환, 신경합병증을 가진 경우 고강도 근력운동은 피하세요.";
            } else if (month == 2) {
                message = "[혈당조절과 운동강도] 운동을 할 때는 등에 약간 땀이 나거나 숨이 찰 정도의 강도로 하는 것이 효과적입니다. 중등도 강도의 운동을 30분 이상, 가능한 한 1주일 내내 실시하는 것이 이상적입니다. 매일 유산소 운동을 하기 어려운 경우에는 회당 운동시간을 늘리는 것도 방법이 됩니다. ";
            } else if (month == 3) {
                message = "[운동과 저혈당] 운동 중 저혈당이 발생하면 공복감, 비정상적인 식은땀, 현기증, 흥분, 불안정, 가슴의 두근거림, 떨림, 두통, 피로감, 발작, 혼수상태 등의 증상이 나타날 수 있습니다. 운동 중 저혈당 증상이 나타나면 운동을 중단하고 추가적인 탄수화물을 섭취할 것을 권장합니다.";
            } else if (month == 4) {
                message = "[정리운동의 중요성] 정리운동은 상승된 체온을 낮추고 근육에 쌓인 젖산이라는 피로물질을 제거하여 운동 후 근육통증과 피로를 예방하는 효과가 있습니다. 본 운동 전후에 준비운동과 정리운동을 실시하는 것은 운동 시 상해도 예방하고, 운동 후 피로와 근육통을 방지 할 수 있는 방법이므로 운동 전 후에 5~10분씩 실시하도록 합니다. ";
            } else if (month == 5) {
                message = "[효과적인 유산소 운동법] 유산소운동시 최대심박수의 50~70% 정도로 약간 숨이 찰 정도로 하는 것이 좋고, 근력운동 시에는 8~15회 반복 가능한 세트로 1~3세트 정도 하는 것이 좋습니다.\n" +
                        "※운동 중 어지럽거나 심장에 통증이 느껴지는 등 이상증세가 나타날 경우 운동을 멈추고, 의사와 상담하세요. ";
            } else if (month == 6) {
                message = "[운동 전 혈당체크] 운동을 시작하기 전 케톤증의 증상이 있고, 혈당이 250mg/dL 이상일 때는 운동을 시작하지 말아야 합니다. 케톤증의 증상이 없더라도 혈당이 300mg/dL 이상일 때 운동을 하는 것은 주의가 필요합니다. ";
            } else if (month == 7) {
                message = "[당뇨병 약물치료와 운동] 인슐린이나 인슐린분비촉진제를 사용하는 환자일 경우 운동 중이나 운동 후 생길 수 있는 저혈당을 예방하기 위해 탄수화물 보충이 필요하거나 용량 조절이 필요합니다.";
            } else if (month == 8) {
                message = "[당뇨병 신경합병증과 운동] 당뇨병으로 신경합병증이 생겼을 경우 수영, 자전거 타기, 팔 운동 등과 같이 체중이 많이 실리지 않는 운동을 하는 것이 바람직합니다. 당뇨병성 자율신경병증이 있는 환자는 운동 전 심장에 관한 정밀검사를 받는 것이 좋습니다.";
            } else if (month == 9) {
                message = "[혈당조절과 유산소운동] 규칙적인 유산소운동은 지방대사를 증가시켜 체중과 체지방률 감소를 유도하며, 이러한 신체구성의 개선효과는 혈당과 당화혈색소를 감소를 가져오게 됩니다.";
            } else if (month == 10) {
                message = "[제2형 당뇨병의 운동법] 제2형 당뇨병의 경우 중강도의 유산소운동 주당 210분 이상, 고강도의 유산소운동 주당 125분 이상 실천할 것을 권고합니다. 운동은 주당 최소 3회 이상 실시해야 하며, 운동하지 않는 날이 2일 이상 연속되지 않도록 하는 것이 좋습니다.";
            } else if (month == 11) {
                message = "[근육량과 혈당관리] 근육량의 증가는 혈당관리와 감소에 효과적입니다. 근육운동은 8~10개의 대근육을 사용하는 운동을 주당 60분 이상 혹은 최소 2세트 이상 실시하도록 권고합니다. ";
            } else if (month == 12) {
                message = "[유연성운동 시 주의사항]\n" +
                        "1) 심장에서 먼곳부터 스트레칭 합니다. \n" +
                        "2) 소근육에서 대근육으로 스트레칭 합니다. \n" +
                        "3) 작은동작에서 큰 동작 순으로 실시합니다. \n" +
                        "4) 스트레칭 동작시 반동을 주지 않습니다.";
            }
        }

        return message;
    }


    /**
     * 시간 yyyy-mm-dd hh:mm을 mm월dd일(월) 오전 00시00분 변환
     */
    public static String getFormatmmddhhmm(String timeStr) {
        Logger.i("alaram","getFormatmmddhhmm = "+timeStr);
        Calendar cal    = Calendar.getInstance();
        cal.set(Integer.parseInt(timeStr.substring(0, 4)), Integer.parseInt(timeStr.substring(5, 7)), Integer.parseInt(timeStr.substring(8, 10)), Integer.parseInt(timeStr.substring(11, 13)), Integer.parseInt(timeStr.substring(14, 16)));
        timeStr         = StringUtil.getIntString(timeStr);
        if (timeStr.length() >= 12) {
            timeStr     = timeStr.substring(4, 6) + "월" + timeStr.substring(6, 8) + "일" + "(" + getDateToWeek(cal) + ")" + getAmPmString(timeStr.substring(8, 10)) + timeStr.substring(8, 10) + "시" + timeStr.substring(10, 12) + "분";
        }
        return timeStr;
    }

    /**
     * 특정 날짜에 대하여 요일을 구함(일 ~ 토)
     *
     * @return
     * @throws Exception
     */
    public static String getDateToWeek(Calendar cal) {
        String convertedString  = "";

        try {
            int dayNum = cal.get(Calendar.DAY_OF_WEEK) - 1;   // 요일을 구해온다.

            switch (dayNum) {
                case 1:
                    convertedString = "일";
                    break;
                case 2:
                    convertedString = "월";
                    break;
                case 3:
                    convertedString = "화";
                    break;
                case 4:
                    convertedString = "수";
                    break;
                case 5:
                    convertedString = "목";
                    break;
                case 6:
                    convertedString = "금";
                    break;
                case 0:
                    convertedString = "토";
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertedString;
    }

    /**
     * 오전 오후 String 로 변환해준다
     *
     * @param hourOfDay
     * @return
     */

    public static String getAmPmString(String hourOfDay) {
        if (hourOfDay.length() > 2)
            hourOfDay = StringUtil.getIntString(hourOfDay).substring(0, 2);

        String amPm = "오전";
        int hour = StringUtil.getIntVal(hourOfDay);
        if (hour > 11) {
            amPm = "오후";
        }
        return amPm;
    }

    public static String getForamtyyyyMMddHHmmssSS(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(date);
    }

}