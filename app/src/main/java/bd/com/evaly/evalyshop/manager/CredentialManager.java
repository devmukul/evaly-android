package bd.com.evaly.evalyshop.manager;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.preference.MyPreference;
import bd.com.evaly.evalyshop.util.ConstantUtils;

public class CredentialManager {

    public static void saveToken(String token){
        MyPreference.with(AppController.mContext).addString(ConstantUtils.TOKEN, "Bearer "+ token).save();
    }

    public static String getToken(){
        return MyPreference.with(AppController.getmContext()).getString(ConstantUtils.TOKEN, "");
    }

    public static void saveRefreshToken(String token){
        MyPreference.with(AppController.mContext).addString(ConstantUtils.REFRESH_TOKEN, "Bearer "+ token).save();
    }

    public static String getRefreshToken(){
        return MyPreference.with(AppController.getmContext()).getString(ConstantUtils.REFRESH_TOKEN, "");
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
}
