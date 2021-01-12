package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
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
        data.put("refresh_token", CredentialManager.getRefreshToken());

        getiApiClient().refreshToken(CredentialManager.getToken(), data).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200 || response.code() == 201) {
                    JsonObject data = response.body().getAsJsonObject("data");
                    String token = data.get("access_token").getAsString();
                    String refresh = data.get("refresh_token").getAsString();
                    CredentialManager.saveToken(token);
                    CredentialManager.saveRefreshToken(refresh);
                    listener.onDataFetched(response.body(), response.code());
                } else if (response.code() == 401) {

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

                if (response.code() >= 200 && response.code() < 300) {
                    if (response.body() != null) {
                        dataFetchingListener.onDataFetched(response.body(), response.code());

                    } else {
                        dataFetchingListener.onFailed("Error occurred!", response.code());
                    }

                } else if (response.code() == 401) {
                    tokenRefresh(new ResponseListenerAuth<JsonObject, String>() {
                        @Override
                        public void onDataFetched(JsonObject response, int statusCode) {
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

                    if (response.body() != null) {
                        String errorMessage = response.body().toString();
                        if (response.body().toString().contains("message")) {
                            try {
                                CommonDataResponse errorResponse = new Gson().fromJson(response.body().toString(), CommonDataResponse.class);
                                errorMessage = errorResponse.getMessage();
                            } catch (Exception ignored) {

                            }
                        }
                        dataFetchingListener.onFailed(errorMessage, response.code());
                    } else {
                        if (response.errorBody() != null) {
                            String errorMessage = "Error occurred!";
                            try {
                                CommonDataResponse errorResponse = new Gson().fromJson(response.errorBody().string(), CommonDataResponse.class);
                                if (errorResponse != null && errorResponse.getMessage() != null)
                                    errorMessage = errorResponse.getMessage();
                            } catch (Exception ignored) {

                            }
                            dataFetchingListener.onFailed(errorMessage, response.code());
                        } else
                            dataFetchingListener.onFailed("Error occurred!", response.code());

                    }
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                Logger.d(t.getMessage());
                if (t instanceof IOException)
                    dataFetchingListener.onFailed(AppController.getmContext().getString(R.string.networkError), 0);
                else
                    dataFetchingListener.onFailed("Error occurred, please try again later", 0);
            }
        };
    }


}
