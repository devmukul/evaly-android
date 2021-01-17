package bd.com.evaly.evalyshop.ui.campaign;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class CampaignViewModel extends ViewModel {

    private SingleLiveEvent<CampaignProductResponse> buyNowClick = new SingleLiveEvent<>();
    private MutableLiveData<List<CampaignCategoryResponse>> categoryLiveList = new MutableLiveData<>();
    private List<CampaignCategoryResponse> categoryArrayList = new ArrayList<>();
    private MutableLiveData<List<CampaignItem>> liveList = new MutableLiveData<>();
    private MutableLiveData<List<CampaignProductResponse>> productsLiveList = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> hideLoadingBar = new SingleLiveEvent<>();
    private List<CampaignProductResponse> productsArrayList = new ArrayList<>();
    private List<CampaignItem> list = new ArrayList<>();
    private String search = null;
    private int currentPage = 1;
    private int totalCount = 0;

    public CampaignViewModel() {
        currentPage = 1;
        loadCampaignCategory();
        loadCampaignProducts();
    }

    public SingleLiveEvent<CampaignProductResponse> getBuyNowClick() {
        return buyNowClick;
    }

    public void setBuyNowClick(CampaignProductResponse model) {
        this.buyNowClick.setValue(model);
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


    public void loadCampaignProducts() {
        if (currentPage > 1 && totalCount <= productsArrayList.size()) {
            hideLoadingBar.setValue(true);
            return;
        }

        CampaignApiHelper.getCampaignAllProducts(currentPage, 20, search, new ResponseListenerAuth<CommonDataResponse<List<CampaignProductResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignProductResponse>> response, int statusCode) {
                productsArrayList.addAll(response.getData());
                productsLiveList.setValue(productsArrayList);
                currentPage++;
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
        productsArrayList.clear();
        currentPage = 1;
        totalCount = 0;
    }


    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public SingleLiveEvent<Boolean> getHideLoadingBar() {
        return hideLoadingBar;
    }

    public List<CampaignCategoryResponse> getCategoryArrayList() {
        return categoryArrayList;
    }

    public LiveData<List<CampaignCategoryResponse>> getCategoryLiveList() {
        return categoryLiveList;
    }

    public LiveData<List<CampaignProductResponse>> getProductsLiveList() {
        return productsLiveList;
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
