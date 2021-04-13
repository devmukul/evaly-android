package bd.com.evaly.evalyshop.rest;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.inject.Singleton;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

@Singleton
public class ApiHandler {

    @SuppressWarnings("unchecked")
    public <T, V> void createCall(Call<T> call, ResponseListener<T, V> responseListener) {
        if (!Utils.isNetworkAvailable(AppController.getContext())) {
            responseListener.onFailed((V) AppController.getContext().getString(R.string.networkError), 500);
            return;
        }

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response != null && responseListener != null)
                    successProcessor(call, response, responseListener);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                responseListener.onFailed((V) getCustomErrorMessage(t), 0);
            }
        });
    }

    private String getCustomErrorMessage(Throwable error) {
        if (error instanceof SocketTimeoutException) {
            return AppController.getContext().getString(R.string.requestTimeOutError);
        } else if (error instanceof MalformedJsonException) {
            return AppController.getContext().getString(R.string.responseMalformedJson);
        } else if (error instanceof IOException) {
            return AppController.getContext().getString(R.string.networkError);
        } else if (error instanceof HttpException) {
            return (((HttpException) error).response().message());
        } else {
            return AppController.getContext().getString(R.string.unknownError);
        }
    }

    @SuppressWarnings("unchecked")
    private <T, V> void successProcessor(Call<T> call, Response<T> response, ResponseListener<T, V> responseListener) {
        int statusCode = response.code();
        if (statusCode >= 200 && statusCode < 400) {
            if (response.body() != null) {
                if (response.body().getClass().isAssignableFrom(CommonDataResponse.class)) {
                    CommonDataResponse<T> commonSuccessResponse = (CommonDataResponse<T>) response.body();
                    if (commonSuccessResponse != null) {
                        responseListener.onDataFetched(response.body(), response.code());
                    } else {
                        responseListener.onFailed((V) ((CommonDataResponse<T>) response.body()).getMessage(), statusCode);
                    }
                } else {
                    responseListener.onDataFetched(response.body(), statusCode);
                }
            } else {
                responseListener.onDataFetched(response.body(), statusCode);
            }
        } else {
            try {
                Gson gson = new Gson();
                if (response.errorBody() != null) {
                    try {
                        String error = null;
                        CommonDataResponse loginResponse = gson.fromJson(response.errorBody().string(), CommonDataResponse.class);
                        if (loginResponse != null) {
                            // if (call.request().url().toString().contains("auth/api/login/")) {}
                            error = loginResponse.getMessage();
                        }
                        if (error != null) {
                            responseListener.onFailed((V) error, statusCode);
                        } else {
                            responseListener.onFailed((V) AppController.getContext().getString(R.string.unknownError), statusCode);
                        }
                    } catch (JsonSyntaxException ex) {
                        responseListener.onFailed((V) AppController.getContext().getString(R.string.unknownError), statusCode);
                        ex.printStackTrace();
                    }
                }
            } catch (IOException e) {
                responseListener.onFailed((V) AppController.getContext().getString(R.string.unknownError), statusCode);
                e.printStackTrace();
            }
        }
    }
}
