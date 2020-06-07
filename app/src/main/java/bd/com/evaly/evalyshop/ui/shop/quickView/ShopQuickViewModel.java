package bd.com.evaly.evalyshop.ui.shop.quickView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ReviewsApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ShopApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class ShopQuickViewModel extends ViewModel {
    private SingleLiveEvent<String> buyNowLiveData = new SingleLiveEvent<>();
    private MutableLiveData<TabsItem> selectedCategoryLiveData = new MutableLiveData<>();

    private MutableLiveData<ShopDetailsModel> shopDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<TabsItem>> shopCategoryListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ItemsItem>> productListLiveData = new MutableLiveData<>();

    private List<TabsItem> categoryArrayList = new ArrayList<>();
    private List<ItemsItem> productArrayList = new ArrayList<>();

    private String categorySlug;
    private String campaignSlug;
    private String shopSlug;
    private int currentPage = 1;
    private int categoryCurrentPage = 1;
    private Integer categoryCount = null;
    private boolean isCategoryLoading = false;

    public ShopQuickViewModel(String categorySlug, String campaignSlug, String shopSlug) {
        super();
        this.categorySlug = categorySlug;
        this.campaignSlug = campaignSlug;
        this.shopSlug = shopSlug;

        currentPage = 1;
        categoryCurrentPage = 1;

        if (shopSlug != null) {
            loadShopProducts();
            loadShopCategories();
        }

    }

    public void clear() {
        currentPage = 2;
        categoryCurrentPage = 1;
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

    public LiveData<List<TabsItem>> getShopCategoryListLiveData() {
        return shopCategoryListLiveData;
    }

    public SingleLiveEvent<String> getBuyNowLiveData() {
        return buyNowLiveData;
    }

    public void setBuyNowLiveData(String slug) {
        this.buyNowLiveData.setValue(slug);
    }

    public LiveData<TabsItem> getSelectedCategoryLiveData() {
        return selectedCategoryLiveData;
    }

    public LiveData<ShopDetailsModel> getShopDetailsLiveData() {
        return shopDetailsLiveData;
    }

    public void setSelectedCategoryLiveData(TabsItem selectedCategoryLiveData) {
        this.selectedCategoryLiveData.setValue(selectedCategoryLiveData);
    }

    public void loadShopProducts() {

        ShopApiHelper.getShopDetailsItem(CredentialManager.getToken(), shopSlug, currentPage, 21, categorySlug, campaignSlug, null, new ResponseListenerAuth<ShopDetailsModel, String>() {
            @Override
            public void onDataFetched(ShopDetailsModel response, int statusCode) {

                shopDetailsLiveData.setValue(response);
                productArrayList.addAll(response.getData().getItems());
                productListLiveData.setValue(productArrayList);
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

                categoryCount = response.get("count").getAsInt();

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject ob = jsonArray.get(i).getAsJsonObject();
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(ob.get("category_name").getAsString());
                    tabsItem.setImage((ob.get("category_image").isJsonNull()) ? "" : ob.get("category_image").getAsString());
                    tabsItem.setSlug(ob.get("category_slug").getAsString());
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
