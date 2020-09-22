package bd.com.evaly.evalyshop.ui.campaign;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.models.campaign.banner.CampaignBannerResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;

public class CampaignViewModel extends ViewModel {

    private CampaignNavigator navigator;
    private int currentPage = 1;

    private MutableLiveData<List<CampaignCategoryResponse>> categoryLiveList = new MutableLiveData<>();
    private List<CampaignCategoryResponse> categoryArrayList = new ArrayList<>();
    private MutableLiveData<List<CampaignItem>> liveList = new MutableLiveData<>();
    private MutableLiveData<List<CampaignBannerResponse>> bannerLiveList = new MutableLiveData<>();
    private MutableLiveData<List<CampaignProductResponse>> productsLiveList = new MutableLiveData<>();
    private List<CampaignItem> list = new ArrayList<>();
    private String search = null;
    private int page = 1;

    public CampaignViewModel() {
        currentPage = 1;
        loadCampaigns();
        loadCampaignCategory();
        loadCampaignProducts();
        loadCampaignBanners();
    }

    public void setNavigator(CampaignNavigator navigator) {
        this.navigator = navigator;
    }

    public void loadCampaigns() {

        CampaignApiHelper.getCampaigns(currentPage, search, new ResponseListenerAuth<CommonDataResponse<List<CampaignItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignItem>> response, int statusCode) {
                list.addAll(response.getData());
                liveList.setValue(list);
                if (response.getCount() > list.size())
                    currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                navigator.onListFailed(errorBody, errorCode);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    public void loadCampaignCategory() {
        CampaignApiHelper.getCampaignCategory(new ResponseListenerAuth<CommonDataResponse<List<CampaignCategoryResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignCategoryResponse>> response, int statusCode) {
                categoryArrayList = response.getData();
                categoryLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void loadCampaignBanners() {

        CampaignApiHelper.getCampaignBanners(new ResponseListenerAuth<CommonDataResponse<List<CampaignBannerResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignBannerResponse>> response, int statusCode) {
                bannerLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    public void loadCampaignProducts() {
        CampaignApiHelper.getCampaignAllProducts(page, 20, search, new ResponseListenerAuth<CommonDataResponse<List<CampaignProductResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignProductResponse>> response, int statusCode) {
                productsLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void clear() {
        list.clear();
        currentPage = 1;
    }

    public List<CampaignCategoryResponse> getCategoryArrayList() {
        return categoryArrayList;
    }

    public LiveData<List<CampaignCategoryResponse>> getCategoryLiveList() {
        return categoryLiveList;
    }

    public LiveData<List<CampaignBannerResponse>> getBannerLiveList() {
        return bannerLiveList;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public LiveData<List<CampaignItem>> getLiveList() {
        return liveList;
    }

}
