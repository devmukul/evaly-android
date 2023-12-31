package bd.com.evaly.evalyshop.ui.shop.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopDetailsResponse;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ShopSearchViewModel extends ViewModel {

    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;
    protected MutableLiveData<ShopDetailsResponse> shopDetailsLive = new MutableLiveData<>();
    private MutableLiveData<Boolean> onResetLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ItemsItem>> productListLiveData = new MutableLiveData<>();
    private SingleLiveEvent<String> buyNowLiveData = new SingleLiveEvent<>();
    private String categorySlug;
    private String campaignSlug;
    private String brandSlug;
    private String shopSlug;
    private String search = null;
    private int currentPage = 1;
    private int categoryCurrentPage = 1;
    private List<ItemsItem> productArrayList = new ArrayList<>();
    private boolean isShop = true;

    @Inject
    public ShopSearchViewModel(SavedStateHandle args, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
        this.categorySlug = null;
        this.campaignSlug = args.get("campaign_slug");
        this.shopSlug = args.get("shop_slug");
        this.brandSlug = args.get("campaign_slug");
        currentPage = 1;
        loadShopProducts();
    }

    public void clear() {
        productArrayList.clear();
        currentPage = 1;
        categoryCurrentPage = 1;
    }

    public void reload() {
        currentPage = 1;
        categoryCurrentPage = 1;
        productArrayList.clear();
        loadShopProducts();
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public LiveData<List<ItemsItem>> getProductListLiveData() {
        return productListLiveData;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public String getCampaignSlug() {
        return campaignSlug;
    }

    public void setCampaignSlug(String campaignSlug) {
        this.campaignSlug = campaignSlug;
    }

    public String getShopSlug() {
        return shopSlug;
    }

    public void setShopSlug(String shopSlug) {
        this.shopSlug = shopSlug;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public SingleLiveEvent<String> getBuyNowLiveData() {
        return buyNowLiveData;
    }

    public void setBuyNowLiveData(String slug) {
        this.buyNowLiveData.setValue(slug);
    }

    public void setBrandSlug(String brandSlug) {
        this.brandSlug = brandSlug;
    }

    public void loadShopProducts() {

        apiRepository.getShopDetailsItem(preferenceRepository.getToken(), shopSlug, currentPage, 21, categorySlug, campaignSlug, search, brandSlug, new ResponseListener<ShopDetailsModel, String>() {
            @Override
            public void onDataFetched(ShopDetailsModel response, int statusCode) {
                productArrayList.addAll(response.getData().getItems());
                productListLiveData.setValue(productArrayList);
                currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

}
