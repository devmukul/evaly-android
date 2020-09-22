package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;
import bd.com.evaly.evalyshop.models.campaign.banner.CampaignBannerResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;

public class CampaignApiHelper extends BaseApiHelper {

    public static void getCampaignBanners(ResponseListenerAuth<CommonDataResponse<List<CampaignBannerResponse>>, String> listener) {
        getiApiClient().getCampaignBanners().enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignCategoryProducts(int page, int limit, String search, String category, ResponseListenerAuth<CommonDataResponse<List<CampaignProductResponse>>, String> listener) {
        getiApiClient().getCampaignCategoryProducts(page, limit, search, category).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignAllProducts(int page, int limit, String search, ResponseListenerAuth<CommonDataResponse<List<CampaignProductResponse>>, String> listener) {
        getiApiClient().getCampaignAllProducts(page, limit, search).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignCategory(ResponseListenerAuth<CommonDataResponse<List<CampaignCategoryResponse>>, String> listener) {
        getiApiClient().getCampaignCategory().enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaigns(int page, String search, ResponseListenerAuth<CommonDataResponse<List<CampaignItem>>, String> listener) {
        getiApiClient().getCampaigns(page, search).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignShops(String group, int page, ResponseListenerAuth<CommonDataResponse<List<CampaignShopItem>>, String> listener) {
        getiApiClient().getCampaignShops(group, page, 21).enqueue(getResponseCallBackDefault(listener));
    }

}
