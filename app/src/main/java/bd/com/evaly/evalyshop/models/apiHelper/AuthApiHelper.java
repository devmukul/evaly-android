package bd.com.evaly.evalyshop.models.apiHelper;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.listener.DataFetchingListener;
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

    private static IApiClient getiApiClient() {
        return ApiClient.getClient().create(IApiClient.class);
    }
}
