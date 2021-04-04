package bd.com.evaly.evalyshop.data.remote;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandCatResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.CategoriesItem;
import bd.com.evaly.evalyshop.rest.ApiHandler;
import bd.com.evaly.evalyshop.rest.IApiClient;

public class ApiRepository {

    private IApiClient apiService;
    private PreferenceRepository preferenceRepository;
    private ApiHandler apiHandler;

    @Inject
    public ApiRepository(IApiClient apiService,
                         PreferenceRepository preferenceRepository,
                         ApiHandler apiHandler) {
        this.apiService = apiService;
        this.preferenceRepository = preferenceRepository;
        this.apiHandler = apiHandler;
    }

    /* ------------- Brand APIs ------------- */

    public void getBrandsDetails(String brandSlug, ResponseListenerAuth<CommonDataResponse<BrandResponse>, String> listener) {
        apiHandler.createCall(apiService.getBrandDetails(brandSlug), listener);
    }

    public void getBrands(String categorySlug, String search, int page, ResponseListenerAuth<CommonDataResponse<List<BrandResponse>>, String> listener) {
        apiHandler.createCall(apiService.getBrands(categorySlug, search, page, 20), listener);
    }

    public void getCampaignCategories(String brandSlug, String campaignSlug, int page, ResponseListenerAuth<CommonDataResponse<List<CategoriesItem>>, String> listener) {
        apiHandler.createCall(apiService.getCategoriesOfBrand(brandSlug, campaignSlug, page), listener);
    }

    public void getCategories(String brandSlug, int page, ResponseListenerAuth<CommonDataResponse<BrandCatResponse>, String> listener) {
        apiHandler.createCall(apiService.getCategoriesOfBrand(brandSlug, page), listener);
    }

}
