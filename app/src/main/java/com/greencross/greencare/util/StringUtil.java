package com.greencross.greencare.util;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Patterns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class StringUtil {

    private static final String TAG = StringUtil.class.getSimpleName();

    public static String nullTrans(String src) {
        if (src == null || src.equals("null") || src.trim().length() == 0) {
            return "";
        }
        return src;
    }

    /**
     * YYYYMMddhhmmss 형의 날짜 포맷 String 반환
     *
     * @return String format of dateTime
     */
    public static String getFormattedDateTime() {
        Time time = new Time();
        time.setToNow();
        String YYYY = time.format("%Y");
        String MM = time.format("%m");
        String dd = time.format("%d");

        String hh = time.format("%H");
        String mm = time.format("%M");
        String ss = time.format("%S");

        String appID = YYYY + MM + dd + hh + mm + ss;

        return appID;
    }

    /**
     * YYYYMMddhhmmss 형의 날짜 포맷 String 반환
     *
     * @return String format of dateTime
     */
    public static String getFormattedime() {
        Time time = new Time();
        time.setToNow();

        String hh = time.format("%H");
        String mm = time.format("%M");

        String appID = hh + "시" + mm + "분";

        return appID;
    }

    /**
     * 결제 금액을 3자리 단위로 "," 를 찍어 리턴
     *
     * @param amount String
     * @return String
     */
    public static String exchangeAmountToStringUnit(String amount) {

        if (TextUtils.isEmpty(amount)) return "";

        String rawStrAmount = "";
        String fixedStrAmount = "";
        int count = 0;
        for (int i = 0; i < amount.length(); i++) {
            fixedStrAmount = amount.substring(amount.length() - 1 - i, amount.length() - i) + fixedStrAmount;
            rawStrAmount = amount.substring(amount.length() - 1 - i, amount.length() - i) + rawStrAmount;
            count++;
            if (count == 3 && amount.length() != rawStrAmount.length()) {
                count = 0;
                fixedStrAmount = "," + fixedStrAmount;
            }
        }
        Logger.e(TAG, "result = " + fixedStrAmount);
        return fixedStrAmount;
    }

    /**
     * 날짜 형식 포맷 변경 (ex. 2013.10.10)
     *
     * @param datetime 서버로 부터 받은 date
     * @return date 가 "-", "." 중 어떤 것을 포함하고 있어도 "." 로 변환
     */
    public static String getConvertedDateTimeFormat(String datetime) {
        if (datetime == null)
            return "";

        datetime = datetime.trim();
        String strDate = datetime;

        if (datetime != null && datetime.length() == 8) {
            datetime = datetime.replace("-", "").replace(".", "").replace(" ", "");

            String tempDate1 = datetime.substring(0, 4);
            String tempDate2 = datetime.substring(4, 6);
            String tempDate3 = datetime.substring(6, 8);

            strDate = tempDate1 + "." + tempDate2 + "." + tempDate3;
        } else if (datetime != null && datetime.length() == 12) {
            datetime = datetime.replace("-", "").replace(".", "").replace(" ", "");

            String tempDate1 = datetime.substring(0, 4);
            String tempDate2 = datetime.substring(4, 6);
            String tempDate3 = datetime.substring(6, 8);
            String tempDate4 = datetime.substring(8, 10);
            String tempDate5 = datetime.substring(10, 12);

            strDate = tempDate1 + "." + tempDate2 + "." + tempDate3 + " " + tempDate4 + ":" + tempDate5;
        } else if (datetime != null && datetime.length() == 14) {
            datetime = datetime.replace("-", "").replace(".", "").replace(" ", "");

            String tempDate1 = datetime.substring(0, 4);
            String tempDate2 = datetime.substring(4, 6);
            String tempDate3 = datetime.substring(6, 8);
            String tempDate4 = datetime.substring(8, 10);
            String tempDate5 = datetime.substring(10, 12);
            String tempDate6 = datetime.substring(12, 14);

            strDate = tempDate1 + "." + tempDate2 + "." + tempDate3 + " " + tempDate4 + ":" + tempDate5 + ":" + tempDate6;

        }

        return strDate;
    }

    /**
     * 날짜 형식 포맷 변경 (ex. 2013.10.10)
     *
     * @param date 서버로 부터 받은 date
     * @return date 가 "-", "." 중 어떤 것을 포함하고 있어도 "." 로 변환
     */
    public static String getConvertedDateFormat(String date) {
        if (date == null && "".equals(date))
            return "";

        String strDate = StringUtil.getIntString(date);

        if (date != null && date.length() >= 8) {
            date = date.replace("-", "").replace(".", "").replace(" ", "");

            String tempDate1 = date.substring(0, 4);
            String tempDate2 = date.substring(4, 6);
            String tempDate3 = date.substring(6, 8);

            strDate = tempDate1 + "." + tempDate2 + "." + tempDate3;
        } else {

            if (strDate != null)
                return strDate;
            else return "";
        }

        return strDate;
    }


    public static String getConvertedDataFormatYYmm(String date) {
        String strDate = "";
        if (date != null && date.length() >= 8) {
            date = date.replace("-", "").replace(".", "").replace(" ", "");

            String tempDate1 = date.substring(0, 4);
            String tempDate2 = date.substring(4, 6);

            strDate = tempDate1 + "." + tempDate2;
        }

        return strDate;
    }

    //MMyy --> MM/yy 로 변경
    public static String getConvertedDataFormatMMyy(String date) {
        String strDate = "";
        if (date != null && date.length() == 4) {
            date = date.replace("-", "").replace(".", "").replace(" ", "");

            String tempDate1 = date.substring(0, 2);
            String tempDate2 = date.substring(2, 4);

            strDate = tempDate1 + "/" + tempDate2;
        }

        return strDate;
    }

    /**
     * 시간 형식 포맷 변경 (ex. 22:13:29 or 21:24)
     *
     * @param date String
     * @return String
     */
    public static String getConvertedTimeFormat(String date) {
        if (date == null)
            return "";

        date = date.trim().replace("-", "").replace(".", "").replace(" ", "").replace(":", "");

        if (date.length() == 6) {
            date = date.substring(0, 2) + ":" + date.substring(2, 4) + ":" + date.substring(4, 6);
        }

        if (false) {
            String tempTime1 = date.substring(8, 10);
            String tempTime2 = date.substring(10, 12);
            String tempTime3 = "";
            if (date.length() == 14) {
                tempTime3 = date.substring(12, 14);
            }

            String strTime = tempTime1 + ":" + tempTime2 + (!"".equals(tempTime3) ? ":" + tempTime3 : "");
        }
        Logger.d(TAG, "strTime = " + date);

        return date;
    }

    /**
     * 카드 목록 & 결제 카드 정보에서 보여질 카드명 : 카드명(카드닉네임)
     *
     * @param cardName String
     * @param cardNick String
     * @return String
     */
    public static String retrieveCardTitle(String cardName, String cardNick) {
        String result = "";
        if (cardName != null && cardName.equalsIgnoreCase(cardNick)) {
            result = cardName;
        } else {
            if (cardName != null && cardNick != null) {
                result = cardName + "(" + cardNick + ")";
            } else if (cardNick == null && cardName != null) {
                result = cardName;
            }
        }
        return result;
    }

    /**
     * 실행중인 화면 갯수 체크
     *
     * @param context Context
     * @return int
     */
    public static int checkRunningActivity(Context context) {
        ActivityManager actM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> Runningtasks = actM.getRunningTasks(1);
        return Runningtasks.get(0).numActivities;
    }

    /**
     * Load string from asset (text file)
     *
     * @param context  Context
     * @param filename String
     * @return String
     */
    public static String loadStringFromAsset(Context context, String filename) {
        StringBuilder ret = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(filename), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine = "";
            while ((mLine = reader.readLine()) != null) {
                //process line
                ret.append(mLine + "\n");
            }

            reader.close();
        } catch (IOException e) {
            Logger.d(TAG, e.getLocalizedMessage());
        }

        return ret.toString();
    }

    /**
     * Getting formatted Masked card number
     *
     * @param sevenDigitString String
     * @return String
     */
    public static String getFormattedMaskedCardNumberString(String sevenDigitString) {
        if (sevenDigitString == null)
            return "";
        else if (sevenDigitString.length() < 15)
            return sevenDigitString;

        String ret = sevenDigitString;

        try {
            String firstFour = sevenDigitString.substring(0, 4);
            String secondFour = "****";
            String thirdFour = "****";
            String fourthFour = null;
            if (sevenDigitString.length() == 15) {
                fourthFour = sevenDigitString.substring(12, 15);
            } else if (sevenDigitString.length() == 16) {
                fourthFour = sevenDigitString.substring(12, 16);
            }
            ret = firstFour + "-" + secondFour + "-" + thirdFour + "-" + fourthFour;

        } catch (Exception e) {
            Logger.e(TAG, e.getLocalizedMessage(), e);
        }

        return ret;
    }

    /**
     * 카드번호를 카드 포맷으로 변경
     * 1111222233334444 -> 1111-2222-3333-4444
     *
     * @param cardNumString String
     * @return String
     */
    public static String getFormattedCardNumberString(String cardNumString) {
        if (cardNumString == null)
            return "";
        else if (cardNumString.length() < 15)
            return cardNumString;

        // '-' 제거
        cardNumString = cardNumString.replace("-", "");

        StringBuffer formattedCardNum = new StringBuffer();

        try {
            int count = 1;

            for (int i = 0; i < cardNumString.length(); i++) {
                formattedCardNum.append(cardNumString.charAt(i));

                // 4i-1번째에 '-' 삽입
                if (i == 4 * count - 1) {
                    formattedCardNum.append("-");
                    count++;
                }
            }

        } catch (Exception e) {
            Logger.e(TAG, e.getLocalizedMessage(), e);
        }

        return formattedCardNum.toString();
    }

    /**
     * 카드번호 형식인 '-'를 제거한 카드번호
     * 1111-2222-3333-4444 -> 1111222233334444
     *
     * @param formattedCardNum String
     * @return String
     */
    public static String getCardNumberString(String formattedCardNum) {
        if (formattedCardNum == null)
            return "";
        else if (formattedCardNum.length() < 15)
            return formattedCardNum;


        String cardNumString = "";
        try {
            cardNumString = formattedCardNum.replace("-", "");
        } catch (Exception e) {
            Logger.e(TAG, e.getLocalizedMessage(), e);
        }

        return cardNumString;
    }

    /**
     * 010-000-0000
     * 010-0000-0000 형태로 전화번호 입력
     *
     * @param s String
     * @return String
     */
    public static String getFormattedMaskMobileNumber(String s) {
        String f = s;

        if (s == null) return "";
        else if (s.length() < 10) return s;
        else {
            f = getPhoneNumFormat(s);
            StringBuilder sb = new StringBuilder(f);
            sb.replace(sb.length() - 4, sb.length(), "****");
            return sb.toString();
        }
    }

    public static String getFormattedBarCodeNumberString(String s) {
        String f = s;

        if (s == null) return "";
        else if (s.length() < 14) return s;
        else if (s.length() == 14) {
            try {
                s = s.trim();
                f = s.substring(0, 4) + " " +
                        s.substring(4, 8) + " " +
                        s.substring(8, 12) + " " +
                        s.substring(12, 14);
            } catch (Exception e) {
                Logger.e(e);
            }
        } else if (s.length() == 16) {
            try {
                s = s.trim();
                f = s.substring(0, 4) + " " +
                        s.substring(4, 8) + " " +
                        s.substring(8, 12) + " " +
                        s.substring(12, 16);
            } catch (Exception e) {
                Logger.e(e);
            }
        }
        return f;
    }

    public static String getFormattedOTCNumberString(String s) {
        String f = s;

        if (s == null) return "";
        else if (s.length() < 20) return s;

        try {
            s = s.trim();
            f = s.substring(0, 4) + " " +
                    s.substring(4, 8) + " " +
                    s.substring(8, 12) + " " +
                    s.substring(12, 16) + " " +
                    s.substring(16, 21);
        } catch (Exception e) {
            Logger.e(e);
        }

        return f;
    }

    /**
     * 1,000,000 형태로 입력 됨
     *
     * @param str String
     * @return String
     */
    public static String getFormatPrice(String str) {
        try {
            str = str.replace(",", "");
            str = getIntString(str);
            if (TextUtils.isEmpty(str)) {
                str = "0";
            }
            str = String.format("%,d", Long.parseLong(str));
            return str;
        } catch (Exception e) {
            return "0";
        }
    }

    // 카드 설명에 "&#40;" 데이터가 들어와 치환해줌
    // 서버에서 처리되어야 하지만 임시로 특정 문자열만 처리함.
    public static String changeChar(String data) {
        if (data == null) return null;
        if (data.length() == 0) return "";

        Pattern p = compile("&#40;");
        Matcher m = p.matcher(data);
        String str = m.replaceAll("(");

        p = compile("&#41;");
        m = p.matcher(str);
        str = m.replaceAll(")");

        p = compile("&#61548;");
        m = p.matcher(str);
        str = m.replaceAll("");

        return str;
    }

    /**
     * 숫자형 문자를 Long로 변경
     *
     * @param str String
     * @return Long
     */
    public static Long getLong(String str) {
        str = getIntString(str);
        str = TextUtils.isEmpty(str) ? "0" : str;
        return Long.parseLong(str);
    }

    public static float getFloat(String str) {
        str = getFloatString(str);
        str = TextUtils.isEmpty(str) ? "0" : str;
        return Float.parseFloat(str);
    }

    public static Integer getIntger(String str) {
        str = getFloatString(str);
        str = TextUtils.isEmpty(str) ? "0" : str;
        return Integer.parseInt(str);
    }

    /**
     * 문자열에서 숫자형 문자만 추출
     *
     * @param str String
     * @return String
     */
    public static String getIntString(String str) {
//		if ("0".equals(str.toString()))
        if (str == null) str = "";
        str = str.replaceAll("[^0-9]", "");

        return str;
    }

    public static String getFloatString(String str) {
        if (str == null) str = "";
        str = str.replaceAll("[^0-9 \\.]", "");

        return str;
    }

    /**
     * 문자열에서 숫자형 문자만 추출
     *
     * @param str String
     * @return String
     */
    public static int getIntVal(String str) {
        if (str == null || TextUtils.isEmpty(str)) str = "0";
        str = getIntString(str);

        return Integer.parseInt(str);
    }

    /**
     * 문자열에서 숫자형 문자만 추출
     *
     * @param str String
     * @return String
     */
    public static float getFloatVal(String str) {
        if (str == null || TextUtils.isEmpty(str)) str = "0";
        str = getFloatString(str);

        return Float.parseFloat(str);
    }

    /**
     * 숫자만 허용하는 필터
     */
    public static InputFilter filterNumber = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern pattern = compile("[^0-9]+$");
            if (!pattern.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    /**
     * 휴대폰 번호 형태로 포멧팅
     *
     * @param phoneNum String
     * @return String
     */
    public static String getPhoneNumFormat(String phoneNum) {

        phoneNum = getIntString(phoneNum);
        if (phoneNum == null)
            phoneNum = "";

        else if (phoneNum != null && phoneNum.length() >= 3) {
            if (phoneNum.length() == 10 || phoneNum.length() == 11) {
                if (phoneNum.startsWith("82")) {
                    phoneNum = phoneNum.substring(2);
                    phoneNum = "0" + phoneNum;
                }

                if (phoneNum.startsWith("010")
                        || phoneNum.startsWith("016")
                        || phoneNum.startsWith("017")
                        || phoneNum.startsWith("018")
                        || phoneNum.startsWith("019")) {
                    if (phoneNum.length() == 10) {
                        phoneNum = phoneNum.substring(0, 3) + "-"
                                + phoneNum.substring(3, 6) + "-"
                                + phoneNum.substring(6);
                    } else if (phoneNum.length() > 8) {
                        phoneNum = phoneNum.substring(0, 3) + "-"
                                + phoneNum.substring(3, 7) + "-"
                                + phoneNum.substring(7);
                    }

                    return phoneNum;
                }
            }
        }
        return phoneNum;
    }

    public static String getPhoneNumFormat2(String phoneNum) {

        phoneNum = getIntString(phoneNum);
        if (phoneNum == null)
            phoneNum = "";

        else if (phoneNum != null && phoneNum.length() >= 3) {
            if (phoneNum.length() == 10 || phoneNum.length() == 11) {
                if (phoneNum.startsWith("82")) {
                    phoneNum = phoneNum.substring(2);
                    phoneNum = "0" + phoneNum;
                }

                if (phoneNum.startsWith("010")
                        || phoneNum.startsWith("016")
                        || phoneNum.startsWith("017")
                        || phoneNum.startsWith("018")
                        || phoneNum.startsWith("019")) {
                    if (phoneNum.length() == 10) {
                        phoneNum = phoneNum.substring(0, 3) + "-"
                                + phoneNum.substring(3, 6) + "-"
                                + phoneNum.substring(6);
                    } else if (phoneNum.length() > 8) {
                        phoneNum = phoneNum.substring(0, 3) + "-"
                                + phoneNum.substring(3, 7) + "-"
                                + phoneNum.substring(7);
                    }

                    return phoneNum;
                }
            } else if (phoneNum.length() == 8) {
                phoneNum = phoneNum.substring(0, 4) + "-"
                        + phoneNum.substring(4, 8);
            }
        }
        return phoneNum;
    }

    public static String getTelNumFormat(String telNum) {

        telNum = getIntString(telNum);
        if (telNum == null)
            telNum = "";

        else if (telNum != null && telNum.length() >= 3) {
            if (telNum.length() == 10 || telNum.length() == 11) {
                if (telNum.startsWith("82")) {
                    telNum = telNum.substring(2);
                    telNum = "0" + telNum;
                }

                if (telNum.startsWith("02")) {
                    if (telNum.length() == 9) {                 // 02-000-0000
                        telNum = telNum.substring(0, 2) + "-"
                                + telNum.substring(2, 5) + "-"
                                + telNum.substring(5, 9);
                    } else if (telNum.length() == 10) {         // 02-0000-0000
                        telNum = telNum.substring(0, 2) + "-"
                                + telNum.substring(2, 6) + "-"
                                + telNum.substring(6, 10);
                    }
                } else {
                    if (telNum.length() == 10) {                // 031-000-0000
                        telNum = telNum.substring(0, 3) + "-"
                                + telNum.substring(3, 6) + "-"
                                + telNum.substring(6, 10);
                    } else if (telNum.length() == 11) {         // 031-0000-0000
                        telNum = telNum.substring(0, 3) + "-"
                                + telNum.substring(3, 7) + "-"
                                + telNum.substring(7, 11);
                    }

                    return telNum;
                }
            } else if (telNum.length() == 8) {
                telNum = telNum.substring(0, 4) + "-"
                        + telNum.substring(4, 8);
            }
        }
        return telNum;
    }

    public static String fillValueBeforeWhiteSpace(String value, String token, int maxLength) {

        StringBuilder builder = new StringBuilder();
        if (value == null || value.length() <= 0) {
            for (int i = 0; i < maxLength; i++) {
                builder.append(token);
            }
            return builder.toString();
        }

        int gap = (maxLength - value.length());
        for (int i = 0; i < gap; i++) {
            builder.append(token);
        }
        builder.append(value);

        return builder.toString();
    }

    public static String fillValueAfterWhiteSpace(String value, String token, int maxLength) {
        StringBuilder builder = new StringBuilder();
        if (value == null || value.length() <= 0) {
            for (int i = 0; i < maxLength; i++) {
                builder.append(token);
            }
            return builder.toString();
        }

        int gap = (maxLength - value.length());
        for (int i = 0; i < gap; i++) {
            builder.append(token);
        }
        builder.insert(0, value);

        return builder.toString();
    }


    public static boolean checkHttpUrl(String url) {
        if (TextUtils.isEmpty(url))
            return false;

        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * 디바이스 클립보드에 텍스트 복사
     *
     * @param context Context
     * @param strCopy String
     */
    public static void setCopyToClipBoard(Context context, String strCopy) {

        strCopy = strCopy.replace("-", "").replace(" ", "");
        Logger.e(TAG, "복사할 계좌번호 : " + strCopy);

        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("label", strCopy);
        clipboardManager.setPrimaryClip(clipData);

    }


    /**
     * 계좌번호를 000000-00-000000 형태로 변경
     *
     * @param accountNumber
     * @return
     */
    public static String getFormattedAccountNumber(String accountNumber) {
        if (accountNumber == null)
            return "";
        else if (accountNumber.length() < 14)
            return accountNumber;

        // '-', 공백 제거
        accountNumber = accountNumber.trim().replace("-", "");

        StringBuffer formattedAccountNum = new StringBuffer();

        try {
            for (int i = 0; i < accountNumber.length(); i++) {
                formattedAccountNum.append(accountNumber.charAt(i));

                // 6번째, 8번째에 '-' 삽입
                if (i == 6 || i == 8) {
                    formattedAccountNum.append("-");
                }
            }

        } catch (Exception e) {
            Logger.e(TAG, e.getLocalizedMessage(), e);
        }

        return formattedAccountNum.toString();
    }


    /**
     * 이름 앞 1자리 외 마스킹 처리
     * ex) 홍**
     *
     * @param name
     */
    public static String getMaskName(String name) {

        if (TextUtils.isEmpty(name)) return "";

        StringBuilder sb = new StringBuilder(name);

        if (TextUtils.isEmpty(name) == false) {
            int length = name.substring(1).length();

            for (int i = 1; i <= length; i++) {
                if (i == length) {
                    sb.replace(i, length + 1, "*");
                } else {
                    sb.replace(i, length, "*");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 전화번호 가운데 자리 외 마스킹 처리
     * ex) 010-****-1234
     *
     * @param phoneNum
     */
    public static String getMaskPhoneNumFront(String phoneNum) {

        if (TextUtils.isEmpty(phoneNum)) return "";
        phoneNum = phoneNum.replace("-", "");
        Logger.d(TAG, "getMaskPhoneNumFront() - " + phoneNum);

        String regex = "(01[016789])(\\d{3,4})(\\d{4})$";
        Matcher matcher = compile(regex).matcher(phoneNum);

        if (matcher.find()) {
            String replaceTarget01 = matcher.group(1);
            String replaceTarget02 = matcher.group(2);
            char[] c = new char[replaceTarget01.length()];
            char[] c1 = new char[replaceTarget02.length()];
            Arrays.fill(c, '*');
            Arrays.fill(c1, '*');

            if (phoneNum.length() == 10 || phoneNum.length() == 11) {
                if (phoneNum.startsWith("82")) {
                    phoneNum = phoneNum.substring(2);
                    phoneNum = "0" + phoneNum;
                }

                if (phoneNum.startsWith("010")
                        || phoneNum.startsWith("016")
                        || phoneNum.startsWith("017")
                        || phoneNum.startsWith("018")
                        || phoneNum.startsWith("019")) {
                    if (phoneNum.length() == 10) {
                        phoneNum = phoneNum.substring(0, 3) + "-"
                                + phoneNum.substring(3, 6) + "-"
                                + phoneNum.substring(6);
                    } else if (phoneNum.length() > 8) {
                        phoneNum = phoneNum.substring(0, 3) + "-"
                                + phoneNum.substring(3, 7) + "-"
                                + phoneNum.substring(7);
                    }

                    return phoneNum.replace(replaceTarget02, String.valueOf(c1));
                }
            }
        }
        return phoneNum;
    }

    public static String getMaskAccountLast(String account) {
        if (TextUtils.isEmpty(account)) return "";

        account = account.trim();

        StringBuilder sb = new StringBuilder(account);
        sb.replace(sb.length() - 5, sb.length(), "*****");
        return sb.toString();

    }

    /**
     * 010-000-****
     * 010-0000-**** 형태로 뒷 4자리 외 마스킹 처리된 전화번호
     *
     * @param s String
     * @return String
     */
    public static String getFormattedMaskMobileNumberBack(String s) {
        String formattedMaskNum = s;

        if (s == null) return "";
        else {
            StringBuilder sb = new StringBuilder(s);
            sb.replace(0, sb.length() - 5, "****");
            formattedMaskNum = getPhoneNumFormat(sb.toString());

            return formattedMaskNum;
        }
    }

    /**
     * 비밀번호 유효성 검사
     *
     * @param target
     * @return
     */
    public static boolean isValidPassword(String target) {
        String pattern = "^(?=.*\\d)(?=.*[~`!@#$%\\?\\^&*()-])(?=.*[a-zA-Z0-9]).{8,15}$";
        return Pattern.matches(pattern, target);
    }

    /**
     * 이메일 유효성 검사
     *
     * @param target
     * @return
     */
    public static boolean isValidEmail(CharSequence target) {

        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * 핸든폰 번호 유효성 체크
     *
     * @param target
     * @return
     */
    public static boolean isValidPhoneNumber(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        }
        String phone = getPhoneNumFormat2(target.toString());
        phone = phone.replaceAll("[^0-9]", "");

        Logger.i(TAG, "isValidPhoneNumber=" + target);

        boolean startPhoneNum = phone.startsWith("010")
                || phone.startsWith("016")
                || phone.startsWith("017")
                || phone.startsWith("018")
                || phone.startsWith("019");

        int length = phone.length();
        if (startPhoneNum == false) {
            return false;
        }

        return length == 10 || length == 11;
    }

    public static boolean isSpecialWord(String target) {
        String pattern = "^[a-zA-Z가-힣]*$";

        return Pattern.matches(pattern, target);
    }

    /**
     * 0
     *
     * @param val
     * @return
     */
    private static String getCalValue(float val) {
        val = val < 0 ? 0 : val;

        return "" + val;
    }

    public static String getNoneZeroString(float val) {
        String result = getCalValue(val);
        if (result.lastIndexOf(".0") != -1) {
            result = result.substring(0, result.indexOf("."));
        } else {
            result = String.format("%.1f", val);
        }
        return result;
    }

    public static String getNoneZeroString2(float val) {
        String result = getCalValue(val);
        if (result.lastIndexOf(".0") != -1) {
            result = result.substring(0, result.indexOf("."));
        } else {
            result = String.format("%.2f", val);
        }
        return result;
    }
}
