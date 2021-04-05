package bd.com.evaly.evalyshop.util;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.Locale;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.roomdb.ProviderDatabase;
import bd.com.evaly.evalyshop.data.roomdb.userInfo.UserInfoDao;
import bd.com.evaly.evalyshop.data.roomdb.userInfo.UserInfoEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;


public class Balance {


    public static void updateUserInfo(Activity context, boolean openDashboard, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        apiRepository.getUserProfile(preferenceRepository.getToken(), new ResponseListenerAuth<CommonDataResponse<UserModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserModel> response, int statusCode) {
                preferenceRepository.saveUserData(response.getData());
                ProviderDatabase providerDatabase = ProviderDatabase.getInstance(context);
                UserInfoDao userInfoDao = providerDatabase.userInfoDao();

                Executors.newSingleThreadExecutor().execute(() -> {
                    UserInfoEntity entity = new UserInfoEntity();
                    entity.setToken(preferenceRepository.getToken());
                    entity.setRefreshToken(preferenceRepository.getRefreshToken());
                    entity.setName(response.getData().getFullName());
                    entity.setImage(response.getData().getImageSm());
                    entity.setUsername(preferenceRepository.getUserName());
                    entity.setPassword(preferenceRepository.getPassword());
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
                    updateUserInfo(context, openDashboard, apiRepository, preferenceRepository);
            }
        });

    }

    public static void update(Activity context, TextView textView, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {

        apiRepository.getUserInfoPay(preferenceRepository.getToken(), preferenceRepository.getUserName(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                response = response.getAsJsonObject("data");
                preferenceRepository.setBalance(response.get("balance").getAsDouble());

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
