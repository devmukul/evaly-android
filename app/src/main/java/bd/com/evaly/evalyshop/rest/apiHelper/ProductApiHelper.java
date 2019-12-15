package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.IApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductApiHelper extends ApiHelper{

    public static void getCategoryBrandProducts(int page, String category, String brands, ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String> listener) {

        if (category.equals("root"))
            category = null;

        getiApiClient().getCategoryBrandProducts(page, category, brands).enqueue(getResponseCallBackResult(listener));
    }


    public static void getSubCategories(String slug, ResponseListenerAuth<Response<JSONArray>, String> listener) {
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


    public static void getBrandsOfCategories(String category, int page, int limit, ResponseListenerAuth<Response<JsonObject>, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call;

        if (category.equals("root"))
            call = iApiClient.getBrandsCategories(null,page,limit);
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

    public static void getShopsOfCategories(String category, int page, int limit, ResponseListenerAuth<Response<JsonObject>, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call;

        if (category.equals("root"))
            call = iApiClient.getShopsOfCategories(page,limit);
        else
            call = iApiClient.getShopsOfCategories(category,page,limit);

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
