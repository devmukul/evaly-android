package bd.com.evaly.evalyshop.rest.apiHelper;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;

public class BrandApiHelper extends BaseApiHelper{

    public static void getBrandsDetails(String brandSlug, ResponseListenerAuth<CommonDataResponse<BrandResponse>, String> listener) {
        getiApiClient().getBrandDetails(brandSlug).enqueue(getResponseCallBackDefault(listener));
    }

}
