package bd.com.evaly.evalyshop.ui.shop;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopDetailsResponse;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ReviewsApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ShopApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class ShopViewModel extends ViewModel {

    protected MutableLiveData<ShopDetailsResponse> shopDetailsLive = new MutableLiveData<>();
    private MutableLiveData<Boolean> onChatClickLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> onFollowClickLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> onResetLiveData = new MutableLiveData<>();
    private MutableLiveData<JsonObject> ratingSummary = new MutableLiveData<>();
    private MutableLiveData<ShopDetailsModel> shopDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<TabsItem>> shopCategoryListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ItemsItem>> productListLiveData = new MutableLiveData<>();
    private SingleLiveEvent<String> buyNowLiveData = new SingleLiveEvent<>();
    private MutableLiveData<TabsItem> selectedCategoryLiveData = new MutableLiveData<>();
    private String categorySlug;
    private String campaignSlug;
    private String brandSlug;
    private String shopSlug;
    private int currentPage = 1;
    private int categoryCurrentPage = 1;
    private Integer categoryCount = null;
    private boolean isCategoryLoading = false;
    private List<TabsItem> categoryArrayList = new ArrayList<>();
    private List<ItemsItem> productArrayList = new ArrayList<>();
    private boolean isShop = true;


    @ViewModelInject
    public ShopViewModel(@Assisted SavedStateHandle args) {

        this.categorySlug = null;
        this.campaignSlug = args.get("campaign_slug");
        this.shopSlug = args.get("shop_slug");
        this.brandSlug = args.get("campaign_slug");

        currentPage = 1;
        categoryCurrentPage = 1;

        loadShopDetails();
        loadShopProducts();
        loadShopCategories();
    }

    public void clear() {
        currentPage = 2;
        categoryCurrentPage = 1;
    }

    public void reload() {
        currentPage = 1;
        categoryCurrentPage = 1;
        productArrayList.clear();
        loadShopDetails();
        loadShopProducts();
        loadShopCategories();
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

    public LiveData<JsonObject> getRatingSummary() {
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

        GeneralApiHelper.subscribeToShop(CredentialManager.getToken(), shopSlug, subscribe, new ResponseListenerAuth<JsonObject, String>() {
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

        ReviewsApiHelper.getReviewSummary(CredentialManager.getToken(), shopSlug, isShop, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                ratingSummary.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    loadRatings();

            }
        });
    }

    public void loadShopDetails() {
        ShopApiHelper.getShopDetails(shopSlug, new ResponseListenerAuth<CommonDataResponse<ShopDetailsResponse>, String>() {
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

        ShopApiHelper.getShopDetailsItem(CredentialManager.getToken(), shopSlug, currentPage, 21, categorySlug, campaignSlug, null, brandSlug, new ResponseListenerAuth<ShopDetailsModel, String>() {
            @Override
            public void onDataFetched(ShopDetailsModel response, int statusCode) {
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

        ProductApiHelper.getCategoriesOfShop(shopSlug, campaignSlug, categoryCurrentPage, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                isCategoryLoading = false;

                List<TabsItem> itemList = new ArrayList<>();
                JsonArray jsonArray = response.getAsJsonArray("data");

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject ob = jsonArray.get(i).getAsJsonObject();
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(ob.get("category_name").getAsString().replaceAll("\"", ""));
                    tabsItem.setImage((ob.get("category_image").isJsonNull()) ? "" : ob.get("category_image").getAsString().replaceAll("\"", ""));
                    tabsItem.setSlug(ob.get("category_slug").getAsString().replaceAll("\"", ""));
                    tabsItem.setCategory(shopSlug);
                    itemList.add(tabsItem);
                }

                categoryArrayList.addAll(itemList);
                shopCategoryListLiveData.setValue(categoryArrayList);
                // shopCategoryListLiveData.setValue(itemList);
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
