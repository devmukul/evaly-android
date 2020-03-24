package bd.com.evaly.evalyshop.ui.shop;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ReviewsApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ShopApiHelper;

public class ShopViewModel extends ViewModel {

    private MutableLiveData<Boolean> onChatClickLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> onFollowClickLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> onResetLiveData = new MutableLiveData<>();
    private MutableLiveData<JsonObject> ratingSummary = new MutableLiveData<>();
    private MutableLiveData<ShopDetailsModel> shopDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<TabsItem>> shopCategoryListLiveData = new MutableLiveData<>();
    private MutableLiveData<String> buyNowLiveData = new MutableLiveData<>();
    private MutableLiveData<TabsItem> selectedCategoryLiveData = new MutableLiveData<>();
    private String categorySlug;
    private String campaignSlug;
    private String shopSlug;
    private int currentPage = 1;
    private int categoryCurrentPage = 1;

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

    public LiveData<String> getBuyNowLiveData() {
        return buyNowLiveData;
    }

    public void setBuyNowLiveData(String slug) {
        this.buyNowLiveData.setValue(slug);
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

        ReviewsApiHelper.getShopRatings(CredentialManager.getToken(), shopSlug, new ResponseListenerAuth<JsonObject, String>() {
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

    public void loadShopProducts() {

        ShopApiHelper.getShopDetailsItem(CredentialManager.getToken(), shopSlug, currentPage, 21, categorySlug, campaignSlug, null, new ResponseListenerAuth<ShopDetailsModel, String>() {
            @Override
            public void onDataFetched(ShopDetailsModel response, int statusCode) {
                shopDetailsLiveData.setValue(response);
                if (response.getCount() > 0)
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

        ProductApiHelper.getCategoriesOfShop(shopSlug, campaignSlug, categoryCurrentPage, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                List<TabsItem> itemList = new ArrayList<>();
                JsonArray jsonArray = response.getAsJsonArray("data");

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject ob = jsonArray.get(i).getAsJsonObject();
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(ob.get("category_name").getAsString());
                    tabsItem.setImage((ob.get("category_image").isJsonNull()) ? "" : ob.get("category_image").getAsString());
                    tabsItem.setSlug(ob.get("category_slug").getAsString());
                    tabsItem.setCategory(shopSlug);
                    itemList.add(tabsItem);
                }
                shopCategoryListLiveData.setValue(itemList);
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


}
