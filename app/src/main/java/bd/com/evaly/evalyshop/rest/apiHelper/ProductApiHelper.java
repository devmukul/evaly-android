package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopModel;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductDetailsModel;
import bd.com.evaly.evalyshop.models.shop.shopItem.ShopItem;
import bd.com.evaly.evalyshop.rest.IApiClient;
import retrofit2.Call;

public class ProductApiHelper extends BaseApiHelper {


    public static void getShopProducts(String shopSlug, int page, int limit, String categorySlug, String campaignSlug, String search, ResponseListenerAuth<JsonObject, String> listener) {

        if (categorySlug != null && categorySlug.equals(""))
            categorySlug = null;

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call;

        if (!campaignSlug.equals(""))
            call = iApiClient.getCampaignShopProducts(campaignSlug, shopSlug, page, limit, categorySlug, search);
        else
            call = iApiClient.getShopProducts(shopSlug, page, limit, categorySlug, search);
        call.enqueue(getResponseCallBackDefault(listener));

    }

    public static void getCategoryBrandProducts(int page, String category, String brands, ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String> listener) {

        if (category != null)
            if (category.equals("root"))
                category = null;

        int limit = 48;
        if (page == 1)
            limit = 21;

        getiApiClient().getCategoryBrandProducts(page, category, brands, limit).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getSubCategories(String slug, ResponseListenerAuth<JsonArray, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonArray> call;

        if (slug == null)
            slug = "root";

        if (slug.equals("root"))
            call = iApiClient.getCategories();
        else
            call = iApiClient.getCategories(slug);

        call.enqueue(getResponseCallBackDefault(listener));
    }


    public static void getBrandsOfCategories(String category, int page, int limit, ResponseListenerAuth<JsonObject, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call;

        if (category != null && category.equals("root"))
            call = iApiClient.getBrandsCategories(null, page, limit);
        else
            call = iApiClient.getBrandsCategories(category, page, limit);

        call.enqueue(getResponseCallBackDefault(listener));
    }


    public static void getShopsOfCategories(String category, int page, int limit, ResponseListenerAuth<JsonObject, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call;

        if (category != null && category.equals("root"))
            call = iApiClient.getShopsOfCategories(page, limit);
        else
            call = iApiClient.getShopsOfCategories(category, page, limit);

        call.enqueue(getResponseCallBackDefault(listener));
    }


    public static void getCategoriesOfShop(String shopSlug, String campaign, int page, ResponseListenerAuth<JsonObject, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call;

        if (campaign == null || campaign.equals(""))
            call = iApiClient.getCategoriesofShop(shopSlug, page);
        else
            call = iApiClient.getCategoriesOfCampaignShop(campaign, shopSlug, page);

        call.enqueue(getResponseCallBackDefault(listener));
    }


    public static void getProductVariants(String shopSlug, String shopItem, ResponseListenerAuth<CommonDataResponse<List<ShopItem>>, String> listener) {

        getiApiClient().getProductVariants(shopSlug, shopItem).enqueue(getResponseCallBackDefault(listener));

    }


    public static void getProductDetails(String productSlug, ResponseListenerAuth<ProductDetailsModel, String> listener) {

        getiApiClient().getProductDetails(productSlug).enqueue(getResponseCallBackDefault(listener));

    }

    public static void getAvailableShops(int variantID, ResponseListenerAuth<CommonDataResponse<List<AvailableShopModel>>, String> listener) {

        getiApiClient().getAvailableShop(variantID).enqueue(getResponseCallBackDefault(listener));

    }

    public static void getNearestAvailableShops(int variantId, double longitude, double latitude, ResponseListenerAuth<CommonDataResponse<List<AvailableShopModel>>, String> listener) {

        getiApiClient().getNearestAvailableShop(variantId, longitude, latitude).enqueue(getResponseCallBackDefault(listener));

    }


}
