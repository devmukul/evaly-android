package bd.com.evaly.evalyshop.manager;

import java.util.Calendar;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.util.ConstantUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.preference.MyPreference;

public class CredentialManager {

    public static void saveToken(String token) {
        MyPreference.with(AppController.mContext).addString(ConstantUtils.TOKEN, token).save();
    }

    public static String getToken() {
        String token = MyPreference.with(AppController.getmContext()).getString(ConstantUtils.TOKEN, "");
        if (token.equals(""))
            return "";
        else
            return "Bearer " + token;
    }


    public static String getTokenNoBearer() {
        return MyPreference.with(AppController.getmContext()).getString(ConstantUtils.TOKEN, "");
    }


    public static void saveRefreshToken(String token) {
        MyPreference.with(AppController.mContext).addString(ConstantUtils.REFRESH_TOKEN, token).save();
    }

    public static String getRefreshToken() {
        return MyPreference.with(AppController.getmContext()).getString(ConstantUtils.REFRESH_TOKEN, "");
    }

    public static String getLanguage() {
        return MyPreference.with(AppController.getmContext()).getString("language", "en");
    }

    public static void setLanguage(String lang) {
        MyPreference.with(AppController.mContext).addString("language", lang).save();
    }

    public static void saveUserName(String userName) {
        MyPreference.with(AppController.mContext).addString(ConstantUtils.USERNAME, userName).save();
    }

    public static String getUserName() {
        return MyPreference.with(AppController.getmContext()).getString(ConstantUtils.USERNAME, "");
    }

    public static void savePassword(String userName) {
        MyPreference.with(AppController.mContext).addString(ConstantUtils.PASSWORD, userName).save();
    }

    public static String getPassword() {
        return MyPreference.with(AppController.getmContext()).getString(ConstantUtils.PASSWORD, "");
    }

    public static void saveUserData(UserModel userModel) {
        MyPreference.with(AppController.mContext).addObject(ConstantUtils.USER_MODEL, userModel).save();
    }

    public static void saveUserRegistered(boolean registered) {
        MyPreference.with(AppController.mContext).addBoolean(ConstantUtils.REGISTERED, registered).save();
    }

    public static UserModel getUserData() {
        return MyPreference.with(AppController.getmContext()).getObject(ConstantUtils.USER_MODEL, UserModel.class);
    }

    public static boolean isUserRegistered() {
        return MyPreference.with(AppController.mContext).getBoolean(ConstantUtils.REGISTERED, false);
    }

    public static void saveArea(String area) {
        MyPreference.with(AppController.mContext).addString("AREA", area).save();
    }

    public static String getArea() {
        return MyPreference.with(AppController.mContext).getString("AREA", null);
    }

    public static void saveLongitude(String value) {
        MyPreference.with(AppController.mContext).addString("LONGITUDE", value).save();
    }

    public static String getLongitude() {
        return MyPreference.with(AppController.mContext).getString("LONGITUDE", null);
    }

    public static void saveLatitude(String value) {
        MyPreference.with(AppController.mContext).addString("LATITUDE", value).save();
    }

    public static String getLatitude() {
        return MyPreference.with(AppController.mContext).getString("LATITUDE", null);
    }

    public static double getBalance() {
        return MyPreference.with(AppController.mContext).getFloat("balance", 0.0f);
    }

    public static void setBalance(double value) {
        MyPreference.with(AppController.mContext).addFloat("balance", (float) value).save();
    }

    public static String getBalanceString() {
        return Utils.formatPrice((double) MyPreference.with(AppController.mContext).getFloat("balance", 0.0f));
    }

    public static long getMessageCounterLastUpdated() {
        return MyPreference.with(AppController.mContext).getLong("message_counter_last_updated", 0);
    }

    public static void setMessageCounterLastUpdated() {
        Calendar calendar = Calendar.getInstance();
        MyPreference.with(AppController.mContext).addLong("message_counter_last_updated", calendar.getTimeInMillis()).save();
    }

    public static int getMessageCount() {
        return MyPreference.with(AppController.mContext).getInt("message_counter", 0);
    }

    public static void setMessageCount(int count) {
        MyPreference.with(AppController.mContext).addInt("message_counter", count).save();
    }

}
