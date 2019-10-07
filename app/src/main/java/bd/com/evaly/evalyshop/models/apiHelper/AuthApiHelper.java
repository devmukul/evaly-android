package bd.com.evaly.evalyshop.models.apiHelper;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.SetPasswordModel;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.rest.IApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthApiHelper {
    public static void setPassword(HashMap<String, String> model, DataFetchingListener<Response<JsonObject>> listener){

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call = iApiClient.setPassword(model);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onFailed(0);
            }
        });

    }

    public static void register(HashMap<String, String> data, DataFetchingListener<Response<JsonObject>> listener){

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call = iApiClient.register(data);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onFailed(0);
            }
        });

    }

    public static void changeXmppPassword(HashMap<String, String> data, DataFetchingListener<Response<JsonPrimitive>> listener){

        IApiClient iApiClient = ApiClient.getXmppClient().create(IApiClient.class);
        Call<JsonPrimitive> call = iApiClient.changeXmppPassword(data);
        call.enqueue(new Callback<JsonPrimitive>() {
            @Override
            public void onResponse(Call<JsonPrimitive> call, Response<JsonPrimitive> response) {
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonPrimitive> call, Throwable t) {
                Logger.d(t.getMessage());
                listener.onFailed(0);
            }
        });

    }

    public static void addRoster(HashMap<String, String> data, DataFetchingListener<Response<JsonPrimitive>> listener){

        IApiClient iApiClient = ApiClient.getXmppClient().create(IApiClient.class);
        Call<JsonPrimitive> call = iApiClient.addRoster(data);
        call.enqueue(new Callback<JsonPrimitive>() {
            @Override
            public void onResponse(Call<JsonPrimitive> call, Response<JsonPrimitive> response) {
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonPrimitive> call, Throwable t) {
                Logger.d(t.getMessage());
                listener.onFailed(0);
            }
        });

    }
    public static void sendCustomMessage(HashMap<String, String> data, DataFetchingListener<Response<JsonObject>> listener){

        IApiClient iApiClient = ApiClient.getClient().create(IApiClient.class);
        Call<JsonObject> call = iApiClient.sendCustomMessage(CredentialManager.getToken(), data);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Logger.d(t.getMessage());
                listener.onFailed(0);
            }
        });

    }

    private static IApiClient getiApiClient() {
        return ApiClient.getClient().create(IApiClient.class);
    }
}
