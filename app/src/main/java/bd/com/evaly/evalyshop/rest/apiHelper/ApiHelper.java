package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.CommonSuccessResponse;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.rest.IApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiHelper {

    protected static Call<JsonObject> call;

    protected static IApiClient getiApiClient() {
        return ApiClient.getClient().create(IApiClient.class);
    }


    protected static <T> Callback<CommonSuccessResponse<T>> getResponseCallBackData(ResponseListenerAuth<CommonSuccessResponse<T>, String> dataFetchingListener) {

        return new Callback<CommonSuccessResponse<T>>() {
            @Override
            public void onResponse(Call<CommonSuccessResponse<T>> call, Response<CommonSuccessResponse<T>> response) {
                if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {
                        dataFetchingListener.onDataFetched(response.body(), response.code());

                    } else {
                        dataFetchingListener.onFailed("error", response.code());
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
                    if (response.body() != null)
                        dataFetchingListener.onFailed(response.body().getMessage(), response.code());
                    else
                        dataFetchingListener.onFailed("error", response.code());
                }
            }

            @Override
            public void onFailure(Call<CommonSuccessResponse<T>> call, Throwable t) {
                dataFetchingListener.onFailed(t.getMessage(), 0);
            }
        };
    }




    protected static <T> Callback<CommonResultResponse<T>> getResponseCallBackResult(ResponseListenerAuth<CommonResultResponse<T>, String> dataFetchingListener) {

        return new Callback<CommonResultResponse<T>>() {
            @Override
            public void onResponse(Call<CommonResultResponse<T>> call, Response<CommonResultResponse<T>> response) {

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
            public void onFailure(Call<CommonResultResponse<T>> call, Throwable t) {
                dataFetchingListener.onFailed(t.getMessage(), 0);
            }
        };
    }



    protected static void tokenRefresh(ResponseListenerAuth<JsonObject, String> listener) {
        HashMap<String, String> data = new HashMap<>();
        data.put("access", CredentialManager.getToken());
        data.put("refresh", CredentialManager.getRefreshToken());

        getiApiClient().refreshToken(data).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200 || response.code() == 201) {
                    String token = response.body().get("access").getAsString();
                    String refresh = response.body().get("refresh").getAsString();
                    CredentialManager.saveToken(token);
                    CredentialManager.saveRefreshToken(refresh);


                }else if (response.code() == 401){

                    listener.onAuthError(true);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }




    protected static <T> Callback<CommonResultResponse<T>> getResponseCallBackDefault(ResponseListenerAuth<CommonResultResponse<T>, String> dataFetchingListener) {

        return new Callback<CommonResultResponse<T>>() {
            @Override
            public void onResponse(Call<CommonResultResponse<T>> call, Response<CommonResultResponse<T>> response) {

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
            public void onFailure(Call<CommonResultResponse<T>> call, Throwable t) {
                dataFetchingListener.onFailed(t.getMessage(), 0);
            }
        };
    }


}
