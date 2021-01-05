package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;

public class BrandApiHelper extends BaseApiHelper{

    public static void getBrandsDetails(String brandSlug, ResponseListenerAuth<CommonDataResponse<BrandResponse>, String> listener) {
        getiApiClient().getBrandDetails(brandSlug).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getBrands(String categorySlug, String search, int page, ResponseListenerAuth<CommonDataResponse<List<BrandResponse>>, String> listener) {
        getiApiClient().getBrands(categorySlug, search, page, 20).enqueue(getResponseCallBackDefault(listener));
    }

}
