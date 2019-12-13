package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import org.json.JSONArray;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.rest.IApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductApiHelper extends ApiHelper{

    public static void getSubCategories(String slug, ResponseListener<Response<JSONArray>, String> listener) {
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
                listener.onFailed(t.getMessage(),0);
            }
        });
    }


    public static void getBrandsOfCategories(String category, int page, int limit, ResponseListener<Response<JsonObject>, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call;

        if (category.equals("root"))
            call = iApiClient.getBrandsCategories(page,limit);
        else
            call = iApiClient.getBrandsCategories(category,page,limit);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onDataFetched(response, response.code());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onFailed(t.getMessage(),0);
            }
        });
    }



}
