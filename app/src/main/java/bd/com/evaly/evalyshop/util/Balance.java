package bd.com.evaly.evalyshop.util;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.Locale;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.data.roomdb.ProviderDatabase;
import bd.com.evaly.evalyshop.data.roomdb.userInfo.UserInfoDao;
import bd.com.evaly.evalyshop.data.roomdb.userInfo.UserInfoEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;


public class Balance {

    public static void update(Activity context, boolean openDashboard) {


        AuthApiHelper.getUserInfoPay(CredentialManager.getToken(), CredentialManager.getUserName(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                JsonObject data = response.getAsJsonObject("data");
                CredentialManager.setBalance(data.get("balance").getAsDouble());

                if (openDashboard) {
                    Intent intent = new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from", "signin");
                    context.startActivity(intent);
                    try {
                        context.finishAffinity();
                    } catch (Exception ignored) {
                    }
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


    public static void updateUserInfo(Activity context, boolean openDashboard) {
        AuthApiHelper.getUserProfile(CredentialManager.getToken(), new ResponseListenerAuth<CommonDataResponse<UserModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserModel> response, int statusCode) {
                CredentialManager.saveUserData(response.getData());
                ProviderDatabase providerDatabase = ProviderDatabase.getInstance(context);
                UserInfoDao userInfoDao = providerDatabase.userInfoDao();

                Executors.newSingleThreadExecutor().execute(() -> {
                    UserInfoEntity entity = new UserInfoEntity();
                    entity.setToken(CredentialManager.getToken());
                    entity.setRefreshToken(CredentialManager.getRefreshToken());
                    entity.setName(response.getData().getFullName());
                    entity.setImage(response.getData().getImageSm());
                    entity.setUsername(CredentialManager.getUserName());
                    entity.setPassword(CredentialManager.getPassword());
                    userInfoDao.insert(entity);
                });

                if (openDashboard) {
                    Intent intent = new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from", "signin");
                    context.startActivity(intent);
                    try {
                        context.finishAffinity();
                    } catch (Exception ignored) {
                    }
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    updateUserInfo(context, openDashboard);
            }
        });

    }

    public static void update(Activity context, TextView textView) {

        AuthApiHelper.getUserInfoPay(CredentialManager.getToken(), CredentialManager.getUserName(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                response = response.getAsJsonObject("data");
                CredentialManager.setBalance(response.get("balance").getAsDouble());

                if (context instanceof OrderDetailsActivity)
                    textView.setText(Html.fromHtml(String.format(Locale.ENGLISH, "Account: <b>৳ %s</b>", response.get("balance").getAsString())));
                else
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
