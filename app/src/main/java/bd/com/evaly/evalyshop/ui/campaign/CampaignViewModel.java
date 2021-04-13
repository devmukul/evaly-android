package bd.com.evaly.evalyshop.ui.campaign;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
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
    private ApiRepository apiRepository;

    @Inject
    public CampaignViewModel(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
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
        apiRepository.getCampaignCategory(new ResponseListener<CommonDataResponse<List<CampaignCategoryResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignCategoryResponse>> response, int statusCode) {
                categoryArrayList = response.getData();
                categoryLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }


    public void loadCampaignProducts() {

        apiRepository.getCampaignAllProducts(currentPage, 20, search, new ResponseListener<CommonDataResponse<List<CampaignProductResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignProductResponse>> response, int statusCode) {
                productsArrayList.addAll(response.getData());
                productsLiveList.setValue(productsArrayList);
                currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                hideLoadingBar.setValue(true);
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
