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
    private MutableLiveData<Boolean> onFollowCliclLiveData = new MutableLiveData<>();
    private MutableLiveData<JsonObject> ratingSummary = new MutableLiveData<>();
    private MutableLiveData<ShopDetailsModel> shopDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<TabsItem>> shopCategoryListLiveData = new MutableLiveData<>();

    private MutableLiveData<String> buyNowLiveData = new MutableLiveData<>();


    public LiveData<Boolean> getOnChatClickLiveData() {
        return onChatClickLiveData;
    }

    public void setOnChatClickLiveData(boolean action) {
        this.onChatClickLiveData.setValue(action);
    }

    public void subscribe(String slug, boolean subscribe) {


        GeneralApiHelper.subscribeToShop(CredentialManager.getToken(), slug, subscribe, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

                if (!logout)
                    subscribe(slug, subscribe);

            }
        });

    }


    public void getRating(final String sku) {

        ReviewsApiHelper.getShopRatings(CredentialManager.getToken(), sku, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                ratingSummary.setValue(response);

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void loadShopProdructs(String slug, int currentPage, String categorySlug, String campaignSlug) {

        ShopApiHelper.getShopDetailsItem(CredentialManager.getToken(), slug, currentPage, 21, categorySlug, campaignSlug, new ResponseListenerAuth<ShopDetailsModel, String>() {
            @Override
            public void onDataFetched(ShopDetailsModel response, int statusCode) {
                shopDetailsLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {


            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    public void loadShopCategories(String slug, int currentPage, String campaign_slug) {

        ProductApiHelper.getCategoriesOfShop(slug, campaign_slug, currentPage, new ResponseListenerAuth<JsonObject, String>() {
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
                    tabsItem.setCategory(slug);
                    itemList.add(tabsItem);
                }

                shopCategoryListLiveData.setValue(itemList);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    public MutableLiveData<Boolean> getOnFollowCliclLiveData() {
        return onFollowCliclLiveData;
    }

    public void setOnFollowCliclLiveData(boolean onFollowCliclLiveData) {
        this.onFollowCliclLiveData.setValue(onFollowCliclLiveData);
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
}
