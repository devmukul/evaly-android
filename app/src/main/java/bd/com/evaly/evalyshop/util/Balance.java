package bd.com.evaly.evalyshop.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Locale;

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

                    JsonObject data = response.getAsJsonObject("data");

                    userDetails.setBalance(data.get("balance").getAsString());

                    JsonObject ob = data.getAsJsonObject("user");

                    if (ob.has("groups"))
                        userDetails.setGroup(ob.getAsJsonArray("groups").toString());

                    userDetails.setUserName(ob.get("username").getAsString());

                    if (!ob.get("first_name").isJsonNull())
                        userDetails.setFirstName(ob.get("first_name").getAsString());

                    if (!ob.get("last_name").isJsonNull())
                        userDetails.setLastName(ob.get("last_name").getAsString());

                    if (!ob.get("email").isJsonNull())
                        userDetails.setEmail(ob.get("email").getAsString());

                    if (!ob.get("contact").isJsonNull())
                        userDetails.setPhone(ob.get("contact").getAsString());

                    if (!ob.get("address").isJsonNull())
                        userDetails.setJsonAddress(ob.get("address").getAsString());

                    if (!ob.get("profile_pic_url").isJsonNull())
                        userDetails.setProfilePicture(ob.get("profile_pic_url").getAsString());

                    if (!ob.get("image_sm").isJsonNull())
                        userDetails.setProfilePictureSM(ob.get("image_sm").getAsString());

                    UserModel userModel = new Gson().fromJson(ob.toString(), UserModel.class);

                    if (ob.get("first_name").isJsonNull())
                        userModel.setFirst_name("");

                    if (ob.get("last_name").isJsonNull())
                        userModel.setLast_name("");

                    CredentialManager.saveUserData(userModel);

                    if (openDashboard) {
                       // Toast.makeText(context, "Successfully signed in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, UserDashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("from", "signin");
                        context.startActivity(intent);
                        context.finishAffinity();
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
                    response = response.getAsJsonObject("data");
                    userDetails.setBalance(response.get("balance").getAsString());
                    textView.setText(String.format(Locale.ENGLISH, "৳ %s", response.get("balance").getAsString()));
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
