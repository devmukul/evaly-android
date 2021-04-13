package bd.com.evaly.evalyshop.ui.brand;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandCatResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.CategoriesItem;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BrandViewModel extends ViewModel {

    protected MutableLiveData<List<ProductItem>> liveList = new MutableLiveData<>();
    protected MutableLiveData<BrandCatResponse> detailsLive = new MutableLiveData<>();
    protected MutableLiveData<List<TabsItem>> categoryListLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> onResetLiveData = new MutableLiveData<>();
    private List<ProductItem> arrayList = new ArrayList<>();
    private String slug;
    private String categorySlug = null;
    private String campaignSlug = null;
    private int currentPage = 1;
    private boolean isCategoryLoading;
    private List<TabsItem> categoryArrayList = new ArrayList<>();
    private MutableLiveData<TabsItem> selectedCategoryLiveData = new MutableLiveData<>();
    private int categoryCurrentPage = 1;
    private ApiRepository apiRepository;

    @Inject
    public BrandViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
        this.slug = savedStateHandle.get("brand_slug");
        if (savedStateHandle.contains("category_slug"))
            this.categorySlug = savedStateHandle.get("category_slug");
        if (savedStateHandle.contains("campaign_slug"))
            this.campaignSlug = savedStateHandle.get("campaign_slug");
        getProducts();
        loadCategories();
    }

    public String getCampaignSlug() {
        return campaignSlug;
    }

    public void clearProductList() {
        arrayList.clear();
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public String getSlug() {
        return slug;
    }

    public LiveData<TabsItem> getSelectedCategoryLiveData() {
        return selectedCategoryLiveData;
    }

    public void setSelectedCategoryLiveData(TabsItem selectedCategoryLiveData) {
        this.selectedCategoryLiveData.setValue(selectedCategoryLiveData);
    }

    public MutableLiveData<Boolean> getOnResetLiveData() {
        return onResetLiveData;
    }

    public void setOnResetLiveData(boolean onResetLiveData) {
        this.onResetLiveData.setValue(onResetLiveData);
    }

    public void getProducts() {

        apiRepository.getCampaignBrandProducts(currentPage, categorySlug, slug, campaignSlug, new ResponseListener<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveList.setValue(arrayList);
                currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {


            }

        });

    }

    public void loadCampaignCategories() {
        if (campaignSlug == null)
            return;
        apiRepository.getCampaignCategories(slug, campaignSlug, categoryCurrentPage, new ResponseListener<CommonDataResponse<List<CategoriesItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CategoriesItem>> response, int statusCode) {
                isCategoryLoading = false;
                for (CategoriesItem item : response.getData()) {
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(item.getName());
                    tabsItem.setImage(item.getImageUrl());
                    tabsItem.setSlug(item.getSlug());
                    categoryArrayList.add(tabsItem);
                }
                categoryListLiveData.setValue(categoryArrayList);
                categoryCurrentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void loadCategories() {
        isCategoryLoading = true;
        loadCampaignCategories();
        apiRepository.getCategories(slug, categoryCurrentPage, new ResponseListener<CommonDataResponse<BrandCatResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<BrandCatResponse> response, int statusCode) {
                detailsLive.setValue(response.getData());
                if (campaignSlug == null) {
                    isCategoryLoading = false;
                    for (CategoriesItem item : response.getData().getCategories()) {
                        TabsItem tabsItem = new TabsItem();
                        tabsItem.setTitle(item.getName());
                        tabsItem.setImage(item.getImageUrl());
                        tabsItem.setSlug(item.getSlug());
                        categoryArrayList.add(tabsItem);
                    }
                    categoryListLiveData.setValue(categoryArrayList);
                    categoryCurrentPage++;
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }


}
