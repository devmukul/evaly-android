package bd.com.evaly.evalyshop.rest.apiHelper;

import android.util.Log;

import com.google.gson.JsonObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.rest.IApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseApiHelper {


    protected static Call<JsonObject> call;

    protected static IApiClient getiApiClient() {

        return ApiClient.getClient().create(IApiClient.class);
    }


    protected static void tokenRefresh(ResponseListenerAuth<JsonObject, String> listener) {
        HashMap<String, String> data = new HashMap<>();
        data.put("access", CredentialManager.getTokenNoBearer());
        data.put("refresh", CredentialManager.getRefreshToken());

        getiApiClient().refreshToken(data).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200 || response.code() == 201) {
                    String token = response.body().get("access").getAsString();
                    String refresh = response.body().get("refresh").getAsString();
                    CredentialManager.saveToken(token);
                    CredentialManager.saveRefreshToken(refresh);

                    listener.onDataFetched(response.body(), response.code());

                }else if (response.code() == 401){

                    listener.onAuthError(true);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    protected static <T> Callback<T> getResponseCallBackDefault(ResponseListenerAuth<T, String> dataFetchingListener) {

        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {

                if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {
                        dataFetchingListener.onDataFetched(response.body(), response.code());

                    } else {
                        dataFetchingListener.onFailed(response.toString(), response.code());
                    }

                } else if (response.code() == 401) {
                    tokenRefresh(new ResponseListenerAuth<JsonObject, String>() {
                        @Override
                        public void onDataFetched(JsonObject response, int statusCode) {

                            Log.d("refresh", "Refreshed and calling again");

                            dataFetchingListener.onAuthError(false);

                        }

                        @Override
                        public void onFailed(String error, int errorCode) {

                        }

                        @Override
                        public void onAuthError(boolean logout) {
                            dataFetchingListener.onAuthError(true);
                        }
                    });
                } else {
                    if (response.body() != null)
                        dataFetchingListener.onFailed(response.toString(), response.code());
                    else
                        dataFetchingListener.onFailed("error", response.code());
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                dataFetchingListener.onFailed(t.getMessage(), 0);
            }
        };
    }


}
