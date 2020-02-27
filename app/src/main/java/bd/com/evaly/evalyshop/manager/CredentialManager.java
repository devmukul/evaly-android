package bd.com.evaly.evalyshop.manager;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.util.preference.MyPreference;
import bd.com.evaly.evalyshop.util.ConstantUtils;

public class CredentialManager {

    public static void saveToken(String token){
        MyPreference.with(AppController.mContext).addString(ConstantUtils.TOKEN, token).save();
    }

    public static String getToken(){
        String token =  MyPreference.with(AppController.getmContext()).getString(ConstantUtils.TOKEN, "");
        if (token.equals(""))
            return "";
        else
            return "Bearer "+token;
    }


    public static String getTokenNoBearer(){
        return MyPreference.with(AppController.getmContext()).getString(ConstantUtils.TOKEN, "");
    }


    public static void saveRefreshToken(String token){
        MyPreference.with(AppController.mContext).addString(ConstantUtils.REFRESH_TOKEN, token).save();
    }

    public static String getRefreshToken(){
        return MyPreference.with(AppController.getmContext()).getString(ConstantUtils.REFRESH_TOKEN, "");
    }

    public static void setLanguage(String lang){
        MyPreference.with(AppController.mContext).addString("language", lang).save();
    }

    public static String getLanguage(){
        return MyPreference.with(AppController.getmContext()).getString("language", "en");
    }

    public static void saveUserName(String userName){
        MyPreference.with(AppController.mContext).addString(ConstantUtils.USERNAME, userName).save();
    }

    public static String getUserName(){
        return MyPreference.with(AppController.getmContext()).getString(ConstantUtils.USERNAME, "");
    }

    public static void savePassword(String userName){
        MyPreference.with(AppController.mContext).addString(ConstantUtils.PASSWORD, userName).save();
    }

    public static String getPassword(){
        return MyPreference.with(AppController.getmContext()).getString(ConstantUtils.PASSWORD, "");
    }

    public static void saveUserData(UserModel userModel){
        MyPreference.with(AppController.mContext).addObject(ConstantUtils.USER_MODEL, userModel).save();
    }

    public static void saveUserRegistered(boolean registered){
        MyPreference.with(AppController.mContext).addBoolean(ConstantUtils.REGISTERED, registered).save();
    }

    public static UserModel getUserData(){
        return MyPreference.with(AppController.getmContext()).getObject(ConstantUtils.USER_MODEL, UserModel.class);
    }

    public static void saveShopList(String shopList){
        MyPreference.with(AppController.mContext).addString(ConstantUtils.SHOP_LIST, shopList).save();
    }

    public static String getShopList(){
        return MyPreference.with(AppController.getmContext()).getString(ConstantUtils.SHOP_LIST,"");
    }

    public static void saveTodaysDate(String date){
        MyPreference.with(AppController.mContext).addString(ConstantUtils.DATE, date).save();
    }

    public static String getTodaysDate(){
        return MyPreference.with(AppController.mContext).getString(ConstantUtils.DATE, "");
    }

    public static boolean isUserRegistered() {
        return MyPreference.with(AppController.mContext).getBoolean(ConstantUtils.REGISTERED, false);
    }
}
