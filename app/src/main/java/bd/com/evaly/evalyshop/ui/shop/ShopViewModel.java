package bd.com.evaly.evalyshop.ui.shop;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.subcampaign.SubCampaignDetailsResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopDetailsResponse;
import bd.com.evaly.evalyshop.models.reviews.ReviewSummaryModel;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ShopViewModel extends ViewModel {

    protected MutableLiveData<ShopDetailsResponse> shopDetailsLive = new MutableLiveData<>();
    private MutableLiveData<Boolean> onChatClickLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> onFollowClickLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> onResetLiveData = new MutableLiveData<>();
    private MutableLiveData<ReviewSummaryModel> ratingSummary = new MutableLiveData<>();
    private MutableLiveData<ShopDetailsModel> shopDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<TabsItem>> shopCategoryListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ItemsItem>> productListLiveData = new MutableLiveData<>();
    private SingleLiveEvent<String> buyNowLiveData = new SingleLiveEvent<>();
    private MutableLiveData<TabsItem> selectedCategoryLiveData = new MutableLiveData<>();
    private String categorySlug;
    private String campaignSlug;
    private String brandSlug;
    private String shopSlug;
    private String search = null;
    private int currentPage = 1;
    private int categoryCurrentPage = 1;
    private Integer categoryCount = null;
    private boolean isCategoryLoading = false;
    private List<TabsItem> categoryArrayList = new ArrayList<>();
    private List<ItemsItem> productArrayList = new ArrayList<>();
    private boolean isShop = true;
    protected MutableLiveData<ShopDetailsModel> shopDetailsModelLiveData = new MutableLiveData<>();
    protected MutableLiveData<SubCampaignDetailsResponse> campaignDetailsLiveData = new MutableLiveData<>();
    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;


    @Inject
    public ShopViewModel(SavedStateHandle args, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.apiRepository = apiRepository;
        this.categorySlug = null;
        this.campaignSlug = args.get("campaign_slug");
        this.shopSlug = args.get("shop_slug");
        this.brandSlug = args.get("campaign_slug");
        this.preferenceRepository = preferenceRepository;

        currentPage = 1;
        categoryCurrentPage = 1;

        loadCampaignDetails();
        loadShopDetails();
        loadShopProducts();
        loadShopCategories();
        loadRatings();
    }

    private void loadCampaignDetails() {
        if (campaignSlug == null)
            return;

        apiRepository.getSubCampaignDetails(campaignSlug, new ResponseListenerAuth<CommonDataResponse<SubCampaignDetailsResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<SubCampaignDetailsResponse> response, int statusCode) {
                campaignDetailsLiveData.setValue(response.getData());
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
        currentPage = 2;
        categoryCurrentPage = 1;
    }

    public void reload() {
        currentPage = 1;
        categoryCurrentPage = 1;
        productArrayList.clear();
        categoryArrayList.clear();
        loadShopDetails();
        loadShopProducts();
        loadShopCategories();
        loadRatings();
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public LiveData<List<ItemsItem>> getProductListLiveData() {
        return productListLiveData;
    }

    public void setProductListLiveData(MutableLiveData<List<ItemsItem>> productListLiveData) {
        this.productListLiveData = productListLiveData;
    }

    public int getCategoryCurrentPage() {
        return categoryCurrentPage;
    }

    public void setCategoryCurrentPage(int categoryCurrentPage) {
        this.categoryCurrentPage = categoryCurrentPage;
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

    public LiveData<ReviewSummaryModel> getRatingSummary() {
        return ratingSummary;
    }

    public LiveData<Boolean> getOnChatClickLiveData() {
        return onChatClickLiveData;
    }

    public void setOnChatClickLiveData(boolean action) {
        this.onChatClickLiveData.setValue(action);
    }

    public LiveData<Boolean> getOnFollowClickLiveData() {
        return onFollowClickLiveData;
    }

    public void setOnFollowClickLiveData(boolean onFollowClickLiveData) {
        this.onFollowClickLiveData.setValue(onFollowClickLiveData);
    }

    public LiveData<ShopDetailsModel> getShopDetailsLiveData() {
        return shopDetailsLiveData;
    }

    public LiveData<List<TabsItem>> getShopCategoryListLiveData() {
        return shopCategoryListLiveData;
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

    public void subscribe(boolean subscribe) {

        apiRepository.subscribeToShop(preferenceRepository.getToken(), shopSlug, subscribe, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    subscribe(subscribe);
            }
        });

    }


    public void loadRatings() {

        apiRepository.getReviewSummary(preferenceRepository.getToken(), shopSlug, new ResponseListenerAuth<CommonDataResponse<ReviewSummaryModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ReviewSummaryModel> response, int statusCode) {
                ratingSummary.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    public void loadShopDetails() {
        apiRepository.getShopDetails(shopSlug, campaignSlug, new ResponseListenerAuth<CommonDataResponse<ShopDetailsResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ShopDetailsResponse> response, int statusCode) {
                shopDetailsLive.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void loadShopProducts() {

        apiRepository.getShopDetailsItem(preferenceRepository.getToken(), shopSlug, currentPage, 21, categorySlug, campaignSlug, null, brandSlug, new ResponseListenerAuth<ShopDetailsModel, String>() {
            @Override
            public void onDataFetched(ShopDetailsModel response, int statusCode) {
                shopDetailsModelLiveData.setValue(response);
                productArrayList.addAll(response.getData().getItems());
                productListLiveData.setValue(productArrayList);
                currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    loadShopProducts();
            }
        });
    }


    public void loadShopCategories() {

        if (isCategoryLoading)
            return;

        if (categoryCount == null || (shopCategoryListLiveData.getValue() != null && categoryCurrentPage * 15 < categoryCount)) {
        } else
            return;

        isCategoryLoading = true;

        apiRepository.getCategoriesOfShop(shopSlug, campaignSlug, categoryCurrentPage, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                isCategoryLoading = false;

                List<TabsItem> itemList = new ArrayList<>();
                JsonArray jsonArray = response.getAsJsonArray("data");

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject ob = jsonArray.get(i).getAsJsonObject();
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(ob.get("category_name").getAsString());
                    tabsItem.setImage((ob.get("category_image").isJsonNull()) ? "" : ob.get("category_image").getAsString().replaceAll("\"", ""));
                    tabsItem.setSlug(ob.get("category_slug").getAsString());
                    tabsItem.setCategory(shopSlug);
                    itemList.add(tabsItem);
                }

                categoryArrayList.addAll(itemList);
                shopCategoryListLiveData.setValue(categoryArrayList);
                categoryCurrentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    public void clearProductList() {
        productArrayList.clear();
    }
}
