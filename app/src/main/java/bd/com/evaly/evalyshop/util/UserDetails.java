package bd.com.evaly.evalyshop.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;

public class UserDetails {

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "UserDetails";

    public UserDetails(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void clearAll(){

        editor.clear().commit();

    }

    public String getToken() {
        String token = pref.getString("token","");

//        if(!token.equals(""))
//            token = token + "." + Utils.extraToken(this.getUserName());
//

        //Log.d("token extra", token);

        return token;

    }

    public void setToken(String token) {
        editor.putString("token", token);
        editor.commit();
    }


    public String getRefreshToken() {
        String token = pref.getString("refreshtoken","");
        return token;

    }

    public void setRefreshToken(String token) {
        editor.putString("refreshtoken", token);
        editor.commit();
    }


    public String getGroups() {
        return  pref.getString("groups","");
    }

    public void setGroup(String name) {
        editor.putString("groups", name);
        editor.commit();
    }


    public void setCreatedAt(String name) {
        editor.putString("created_at", name);
        editor.commit();
    }

    public String getCreatedAt() {
        return  pref.getString("created_at","");
    }


    // referral
    public String getRef() {
        return  pref.getString("referral","");
    }

    public void setRef(String name) {
        editor.putString("referral", name);
        editor.commit();
    }

    public String getRefMessage() {
        return  pref.getString("referral_message","Get 20 Tk. Evaly balance when someone uses your invitation code during registration on Evaly. Only new phone numbers with unique devices will be counter as valid registrations.");
    }

    public void setRefMessage(String name) {
        editor.putString("referral_message", name);
        editor.commit();
    }

    public String getRefStatistics() {
        return  pref.getString("referral_statistics","<b>0</b> people registered with your code.");
    }

    public void setRefStatistics(String name) {
        editor.putString("referral_statistics", name);
        editor.commit();
    }

    public boolean isRated() {
        return  pref.getBoolean("is_rated", false);
    }

    public void setRated(boolean name) {
        editor.putBoolean("is_rated", name);
        editor.commit();
    }

    public void setChatLoaded(boolean loaded) {
        editor.putBoolean("first_chat_loaded", loaded);
        editor.commit();
    }


    public boolean isChatLoaded() {
        return  pref.getBoolean("first_chat_loaded", false);
    }





    // user name is the phone number

    public String getUserName() {
        return  pref.getString("username","");
    }

    public void setUserName(String name) {
        editor.putString("username", name);
        editor.commit();
    }

    public String getFirstName() {
        return  pref.getString("firstname","");
    }

    public void setFirstName(String name) {
        editor.putString("firstname", name);
        editor.commit();
    }

    public String getLastName() {
        return  pref.getString("lastname","");
    }

    public void setLastName(String name) {
        editor.putString("lastname", name);
        editor.commit();
    }

    public String getEmail() {

        return  pref.getString("email","");
    }

    public void setUserID(int userid) {
        editor.putInt("userid", userid);
        editor.commit();
    }
    public int getUserID() {

        return  pref.getInt("userid",0);
    }

    public void setEmail(String name) {
        editor.putString("email", name);
        editor.commit();
    }

    // user name is the phone number

    public String getPhone() {
        return  pref.getString("phone","");
    }

    public void setPhone(String phone) {
        editor.putString("phone", phone);
        editor.commit();
    }


    public String getProfilePictureSM() {
        return  pref.getString("profile_picture_sm","");
    }

    public void setProfilePictureSM(String phone) {
        editor.putString("profile_picture_sm", phone);
        editor.commit();
    }


    public String getProfilePicture() {
        return  pref.getString("profile_picture","");
    }

    public void setProfilePicture(String phone) {
        editor.putString("profile_picture", phone);
        editor.commit();
    }



    public void setAddresses(String name) {
        editor.putString("addresses", name);
        editor.commit();
    }

    public String getAddresses() {
        return  pref.getString("addresses","");
    }

    public void setAddressID(String id){
        editor.putString("address_id", id);
        editor.commit();
    }

    public String getAddressID(){
        return  pref.getString("address_id","");
    }

    public void setJsonAddress(String address){
        editor.putString("json_addresses",address).commit();
    }

    public String getJsonAddress(){
        return pref.getString("json_addresses","");
    }

    public void setBalance(String balance){
        editor.putString("balance",balance).commit();
    }

    public String getBalance(){
        return  pref.getString("balance","0.0");
    }

}