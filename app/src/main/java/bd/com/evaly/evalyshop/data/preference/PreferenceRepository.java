package bd.com.evaly.evalyshop.data.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Calendar;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.models.profile.UserInfoResponse;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.util.ConstantUtils;
import bd.com.evaly.evalyshop.util.Utils;

public class PreferenceRepository {

    private SharedPreferences preference;
    private SharedPreferences.Editor preferenceEditor;
    private Gson gson;

    @Inject
    public PreferenceRepository(Context context, Gson gson) {
        this.preference = context.getSharedPreferences("EasyPreferencePref", Context.MODE_PRIVATE);
        this.preferenceEditor = preference.edit();
        this.gson = gson;
    }

    public boolean isLogged(){
        return !getToken().isEmpty();
    }

    public void saveToken(String token) {
        preferenceEditor.putString(ConstantUtils.TOKEN, token).commit();
    }

    public String getToken() {
        String token = preference.getString(ConstantUtils.TOKEN, "");
        if (token.equals(""))
            return "";
        else
            return "Bearer " + token;
    }

    public String getTokenNoBearer() {
        return preference.getString(ConstantUtils.TOKEN, "");
    }


    public void saveRefreshToken(String token) {
        preference.edit().putString(ConstantUtils.REFRESH_TOKEN, token).apply();
    }

    public String getRefreshToken() {
        return preference.getString(ConstantUtils.REFRESH_TOKEN, "");
    }

    public String getLanguage() {
        return preference.getString("language", "en");
    }

    public void setLanguage(String lang) {
        preferenceEditor.putString("language", lang).commit();
    }

    public void saveUserName(String userName) {
        preferenceEditor.putString(ConstantUtils.USERNAME, userName).commit();
    }

    public String getUserName() {
        return preference.getString(ConstantUtils.USERNAME, "");
    }

    public void savePassword(String userName) {
        preferenceEditor.putString(ConstantUtils.PASSWORD, userName).commit();
    }

    public String getPassword() {
        return preference.getString(ConstantUtils.PASSWORD, "");
    }

    public void saveUserData(UserModel userModel) {
        preferenceEditor.putString(ConstantUtils.USER_MODEL, gson.toJson(userModel)).commit();
    }

    public UserModel getUserData() {
        UserModel model =  gson.fromJson(preference.getString(ConstantUtils.USER_MODEL, ""), UserModel.class);
        if (model == null)
            return new UserModel();
        return model;
    }

    public void saveUserInfo(UserInfoResponse userModel) {
        preferenceEditor.putString(ConstantUtils.USER_INFO_MODEL, gson.toJson(userModel)).commit();
    }

    public UserInfoResponse getUserInfo() {
        return gson.fromJson(preference.getString(ConstantUtils.USER_INFO_MODEL, ""), UserInfoResponse.class);
    }

    public void saveUserRegistered(boolean registered) {
        preferenceEditor.putBoolean(ConstantUtils.REGISTERED, registered).commit();
    }

    public boolean isUserRegistered() {
        return preference.getBoolean(ConstantUtils.REGISTERED, false);
    }

    public void saveArea(String area) {
        preferenceEditor.putString("AREA", area).commit();
    }

    public String getArea() {
        return preference.getString("AREA", null);
    }

    public void saveLongitude(String value) {
        preferenceEditor.putString("LONGITUDE", value).commit();
    }

    public String getLongitude() {
        return preference.getString("LONGITUDE", null);
    }

    public void saveLatitude(String value) {
        preferenceEditor.putString("LATITUDE", value).commit();
    }

    public String getLatitude() {
        return preference.getString("LATITUDE", null);
    }

    public double getBalance() {
        return preference.getFloat("balance", 0.0f);
    }

    public void setBalance(double value) {
        preferenceEditor.putFloat("balance", (float) value).commit();
    }

    public String getBalanceString() {
        return Utils.formatPrice((double) preference.getFloat("balance", 0.0f));
    }

    public long getMessageCounterLastUpdated() {
        return preference.getLong("message_counter_last_updated", 0);
    }

    public void setMessageCounterLastUpdated() {
        Calendar calendar = Calendar.getInstance();
        preferenceEditor.putLong("message_counter_last_updated", calendar.getTimeInMillis()).commit();
    }

    public int getMessageCount() {
        return preference.getInt("message_counter", 0);
    }

    public void setMessageCount(int count) {
        preferenceEditor.putInt("message_counter", count).commit();
    }

    public boolean isDarkMode() {
        return preference.getBoolean("dark_mode", false);
    }

    public void setDarkMode(boolean is) {
        preferenceEditor.putBoolean("dark_mode", is).commit();
    }

    public void clearAll() {
        preferenceEditor.clear().commit();
    }

}
