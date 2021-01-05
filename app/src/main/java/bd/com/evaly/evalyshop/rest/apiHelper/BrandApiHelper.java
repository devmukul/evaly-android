package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandCatResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;
import bd.com.evaly.evalyshop.util.UrlUtils;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class BrandApiHelper extends BaseApiHelper{

    public static void getBrandsDetails(String brandSlug, ResponseListenerAuth<CommonDataResponse<BrandResponse>, String> listener) {
        getiApiClient().getBrandDetails(brandSlug).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getBrands(String categorySlug, String search, int page, ResponseListenerAuth<CommonDataResponse<List<BrandResponse>>, String> listener) {
        getiApiClient().getBrands(categorySlug, search, page, 20).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getCategories(String slug,  ResponseListenerAuth<CommonDataResponse<BrandCatResponse>, String> listener) {
        getiApiClient().getCategoriesOfBrand(slug).enqueue(getResponseCallBackDefault(listener));
    }

}
