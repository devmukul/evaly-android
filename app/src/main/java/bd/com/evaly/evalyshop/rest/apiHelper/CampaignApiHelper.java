package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;
import bd.com.evaly.evalyshop.models.campaign.banner.CampaignBannerResponse;
import bd.com.evaly.evalyshop.models.campaign.brand.CampaignBrandResponse;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.models.campaign.carousel.CampaignCarouselResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignProductCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.models.campaign.shop.CampaignShopResponse;
import bd.com.evaly.evalyshop.models.campaign.subcampaign.SubCampaignDetailsResponse;
import bd.com.evaly.evalyshop.models.campaign.topProducts.CampaignTopProductResponse;

public class CampaignApiHelper extends BaseApiHelper {

    public static void getCampaignProductCategories(String category, String campaignSlug, String search, int page, ResponseListenerAuth<CommonDataResponse<List<CampaignProductCategoryResponse>>, String> listener) {
        getiApiClient().getCampaignProductCategories(category, campaignSlug, search, page, 30).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getSubCampaignDetails(String campaignSlug, ResponseListenerAuth<CommonDataResponse<SubCampaignDetailsResponse>, String> listener) {
        getiApiClient().getSubCampaignDetails(campaignSlug).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignCarousel(String context, ResponseListenerAuth<CommonDataResponse<List<CampaignCarouselResponse>>, String> listener) {
        getiApiClient().getCampaignCarousel(context).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignBanners(ResponseListenerAuth<CommonDataResponse<List<CampaignBannerResponse>>, String> listener) {
        getiApiClient().getCampaignBanners().enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignCategoryCampaigns(int page, int limit, String search, String category, ResponseListenerAuth<CommonDataResponse<List<SubCampaignResponse>>, String> listener) {
        getiApiClient().getCampaignCategoryCampaigns(page, limit, search, category).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignCategoryProducts(int page, int limit, String search, String category, String campaign, String productCategory, String priceSort, ResponseListenerAuth<CommonDataResponse<List<CampaignProductResponse>>, String> listener) {
        getiApiClient().getCampaignCategoryProducts(page, limit, search, category, campaign, productCategory, priceSort).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignCategoryBrands(int page, int limit, String search, String category, String campaign, ResponseListenerAuth<CommonDataResponse<List<CampaignBrandResponse>>, String> listener) {
        getiApiClient().getCampaignCategoryBrands(page, limit, search, category, campaign).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignCategoryShops(int page, int limit, String search, String category, String campaign, ResponseListenerAuth<CommonDataResponse<List<CampaignShopResponse>>, String> listener) {
        getiApiClient().getCampaignCategoryShops(page, limit, search, category, campaign).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignAllProducts(int page, int limit, String search, ResponseListenerAuth<CommonDataResponse<List<CampaignProductResponse>>, String> listener) {
        getiApiClient().getCampaignAllProducts(page, limit, search).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignCategory(ResponseListenerAuth<CommonDataResponse<List<CampaignCategoryResponse>>, String> listener) {
        getiApiClient().getCampaignCategory().enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignCategoryTopProducts(ResponseListenerAuth<CommonDataResponse<List<CampaignTopProductResponse>>, String> listener) {
        getiApiClient().getCampaignCategoryTopProducts().enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaigns(int page, String search, ResponseListenerAuth<CommonDataResponse<List<CampaignItem>>, String> listener) {
        getiApiClient().getCampaigns(page, search).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignShops(String group, int page, ResponseListenerAuth<CommonDataResponse<List<CampaignShopItem>>, String> listener) {
        getiApiClient().getCampaignShops(group, page, 21).enqueue(getResponseCallBackDefault(listener));
    }

}
