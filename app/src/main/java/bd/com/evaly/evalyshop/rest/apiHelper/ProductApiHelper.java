package bd.com.evaly.evalyshop.rest.apiHelper;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.rest.IApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductApiHelper {


    public static Call<JsonObject> call;

    private static IApiClient getiApiClient() {
        return ApiClient.getClient().create(IApiClient.class);
    }

    public static void getSubCategories(String slug, ResponseListener<Response<JSONArray>> listener) {
        IApiClient iApiClient = getiApiClient();
        Call<JSONArray> call;

        if (slug.equals("root"))
            call = iApiClient.getCategories();
        else
            call = iApiClient.getCategories(slug);

        call.enqueue(new Callback<JSONArray>() {
            @Override
            public void onResponse(Call<JSONArray> call, Response<JSONArray> response) {
                listener.onDataFetched(response, response.code());
            }

            @Override
            public void onFailure(Call<JSONArray> call, Throwable t) {
                listener.onFailed(0);
            }
        });
    }


    public static void getBrandsOfCategories(String category, int page, int limit, ResponseListener<Response<JsonObject>> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call;

        if (category.equals("root"))
            call = iApiClient.getBrandsCategories(page,limit);
        else
            call = iApiClient.getBrandsCategories(category,page,limit);


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Log.d("jsonz", response.body().toString());

                listener.onDataFetched(response, response.code());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onFailed(0);
            }
        });
    }



}
