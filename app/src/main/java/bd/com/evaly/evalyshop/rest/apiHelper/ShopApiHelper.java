package bd.com.evaly.evalyshop.rest.apiHelper;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.rest.IApiClient;
import retrofit2.Call;

public class ShopApiHelper extends BaseApiHelper{

    public static void getShopDetailsItem(String token, String url, ResponseListenerAuth<ShopDetailsModel, String> listener){

        getiApiClient().getShopDetailsItems(token, url).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getShopDetailsItem(String token, String shopSlug, int page, int limit, String categorySlug, String campaignSlug, ResponseListenerAuth<ShopDetailsModel, String> listener){

        IApiClient iApiClient = getiApiClient();
        Call<ShopDetailsModel> call;

        if (!campaignSlug.equals(""))
            call = iApiClient.getCampaignShopDetails(token, campaignSlug, shopSlug, page, limit, categorySlug);
        else
            call = iApiClient.getShopDetails(token, shopSlug, page, limit, categorySlug);

        call.enqueue(getResponseCallBackDefault(listener));

    }


}
