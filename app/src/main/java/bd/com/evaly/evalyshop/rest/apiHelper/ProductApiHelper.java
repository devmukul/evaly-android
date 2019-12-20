package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.IApiClient;
import retrofit2.Call;

public class ProductApiHelper extends ApiHelper{


    public static void getShopProducts(String shopSlug, int page, int limit, String categorySlug, String campaignSlug, ResponseListenerAuth<JsonObject, String> listener){

        if (categorySlug.equals(""))
            categorySlug = null;

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call;

        if (!campaignSlug.equals(""))
            call = iApiClient.getCampaignShopProducts(campaignSlug, shopSlug, page, limit, categorySlug);
        else
            call = iApiClient.getShopProducts(shopSlug, page, limit, categorySlug);
        call.enqueue(getResponseCallBackDefault(listener));

    }

    public static void getCategoryBrandProducts(int page, String category, String brands, ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String> listener) {

        if (category.equals("root"))
            category = null;

        getiApiClient().getCategoryBrandProducts(page, category, brands).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getSubCategories(String slug, ResponseListenerAuth<JsonArray, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonArray> call;

        if (slug.equals("root"))
            call = iApiClient.getCategories();
        else
            call = iApiClient.getCategories(slug);

        call.enqueue(getResponseCallBackDefault(listener));
    }


    public static void getBrandsOfCategories(String category, int page, int limit, ResponseListenerAuth<JsonObject, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call;

        if (category.equals("root"))
            call = iApiClient.getBrandsCategories(null,page,limit);
        else
            call = iApiClient.getBrandsCategories(category,page,limit);

        call.enqueue(getResponseCallBackDefault(listener));
    }


    public static void getShopsOfCategories(String category, int page, int limit, ResponseListenerAuth<JsonObject, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call;

        if (category.equals("root"))
            call = iApiClient.getShopsOfCategories(page,limit);
        else
            call = iApiClient.getShopsOfCategories(category,page,limit);

        call.enqueue(getResponseCallBackDefault(listener));
    }


    public static void getCategoriesOfShop(String shopSlug, String campaign, int page, ResponseListenerAuth<JsonObject, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call;

        if (campaign.equals(""))
            call = iApiClient.getCategoriesofShop(shopSlug,page);
        else
            call = iApiClient.getCategoriesOfCampaignShop(campaign,shopSlug,page);

        call.enqueue(getResponseCallBackDefault(listener));
    }




}
