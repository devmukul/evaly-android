package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopDetailsResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopListResponse;
import bd.com.evaly.evalyshop.models.shop.GroupShopModel;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.rest.IApiClient;
import retrofit2.Call;

public class ShopApiHelper extends BaseApiHelper {

    public static void getShopDetailsItem(String token, String url, ResponseListenerAuth<ShopDetailsModel, String> listener) {

        getiApiClient().getShopDetailsItems(token, url).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getShopDetailsItem(String token, String shopSlug, int page, int limit, String categorySlug, String campaignSlug, String search, String brandSlug, ResponseListenerAuth<ShopDetailsModel, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<ShopDetailsModel> call;

        if (campaignSlug != null && !campaignSlug.equals(""))
            call = iApiClient.getCampaignShopDetails(token, campaignSlug, shopSlug, page, limit, categorySlug, search, brandSlug);
        else
            call = iApiClient.getShopDetails(token, shopSlug, page, limit, categorySlug, search);

        call.enqueue(getResponseCallBackDefault(listener));

    }

    public static void getShops(String categorySlug, String search, int page, ResponseListenerAuth<CommonDataResponse<List<ShopListResponse>>, String> listener) {
        getiApiClient().getShops(categorySlug, search, page, 20).enqueue(getResponseCallBackDefault(listener));
    }



    public  static void getShopDetails(String slug, ResponseListenerAuth<CommonDataResponse<ShopDetailsResponse>, String> listener){
        getiApiClient().getShopDetails(CredentialManager.getToken(), slug).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getFollowedShop(String token, ResponseListenerAuth<JsonObject, String> listener) {

        getiApiClient().getFollowedShops(token).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getShopsByGroup(String group, int page, int limit, String area, String search, ResponseListenerAuth<CommonDataResponse<List<GroupShopModel>>, String> listener) {

        getiApiClient().getShopByGroup(group, page, limit, area, search).enqueue(getResponseCallBackDefault(listener));
    }


}
