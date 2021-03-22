package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandCatResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.CategoriesItem;

public class BrandApiHelper extends BaseApiHelper {

    public static void getBrandsDetails(String brandSlug, ResponseListenerAuth<CommonDataResponse<BrandResponse>, String> listener) {
        getiApiClient().getBrandDetails(brandSlug).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getBrands(String categorySlug, String search, int page, ResponseListenerAuth<CommonDataResponse<List<BrandResponse>>, String> listener) {
        getiApiClient().getBrands(categorySlug, search, page, 20).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCampaignCategories(String brandSlug, String campaignSlug, int page, ResponseListenerAuth<CommonDataResponse<List<CategoriesItem>>, String> listener) {
            getiApiClient().getCategoriesOfBrand(brandSlug, campaignSlug, page).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCategories(String brandSlug, int page, ResponseListenerAuth<CommonDataResponse<BrandCatResponse>, String> listener) {
            getiApiClient().getCategoriesOfBrand(brandSlug, page).enqueue(getResponseCallBackDefault(listener));
    }

}
