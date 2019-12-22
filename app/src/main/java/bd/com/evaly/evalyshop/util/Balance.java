package bd.com.evaly.evalyshop.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import bd.com.evaly.evalyshop.activity.UserDashboardActivity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;


public class Balance {

    public static void update(Activity context, boolean openDashboard) {


        UserDetails userDetails = new UserDetails(context);

        AuthApiHelper.getUserInfoPay(CredentialManager.getToken(), CredentialManager.getUserName(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {


                try {
                    JsonObject data = response.getAsJsonObject("data");

                    userDetails.setBalance(data.get("balance").getAsString());

                    JsonObject ob = data.getAsJsonObject("user");

                    if (ob.has("groups"))
                        userDetails.setGroup(ob.getAsJsonArray("groups").toString());

                    userDetails.setUserName(ob.get("username").getAsString());
                    userDetails.setFirstName(ob.get("first_name").getAsString());
                    userDetails.setLastName(ob.get("last_name").getAsString());
                    userDetails.setEmail(ob.get("email").getAsString());
                    userDetails.setPhone(ob.get("contact").getAsString());
                    if (ob.get("address") != null){
                        userDetails.setJsonAddress(ob.get("address").getAsString());
                    }
                    userDetails.setProfilePicture(ob.get("profile_pic_url").getAsString());
                    userDetails.setProfilePictureSM(ob.get("image_sm").getAsString());

                    UserModel userModel = new Gson().fromJson(ob.toString(), UserModel.class);
                    CredentialManager.saveUserData(userModel);

                    if (openDashboard) {
                        Toast.makeText(context, "Successfully signed in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, UserDashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("from", "signin");
                        context.startActivity(intent);
                        context.finishAffinity();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });


    }


    public static void update(Activity context, TextView textView) {

        UserDetails userDetails = new UserDetails(context);

        AuthApiHelper.getUserInfoPay(CredentialManager.getToken(), CredentialManager.getUserName(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {


                try {

                    response = response.getAsJsonObject("data");
                    userDetails.setBalance(response.get("balance").getAsString());

                    textView.setText("৳ " + response.get("balance").getAsString());

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


}
