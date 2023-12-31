package bd.com.evaly.evalyshop.ui.shop.quickView;

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
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ShopQuickViewModel extends ViewModel {
    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;
    private SingleLiveEvent<String> buyNowLiveData = new SingleLiveEvent<>();
    private MutableLiveData<TabsItem> selectedCategoryLiveData = new MutableLiveData<>();
    private MutableLiveData<ShopDetailsModel> shopDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<TabsItem>> shopCategoryListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ItemsItem>> productListLiveData = new MutableLiveData<>();
    private List<TabsItem> categoryArrayList = new ArrayList<>();
    private List<ItemsItem> productArrayList = new ArrayList<>();
    private String categorySlug;
    private String campaignSlug;
    private String brandSlug;
    private String shopSlug;
    private int currentPage = 1;
    private int categoryCurrentPage = 1;
    private Integer categoryCount = null;
    private boolean isCategoryLoading = false;

    @Inject
    public ShopQuickViewModel(SavedStateHandle args, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.categorySlug = null;
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
        this.campaignSlug = args.get("campaign_slug");
        this.shopSlug = args.get("shop_slug");
        this.brandSlug = args.get("campaign_slug");

        currentPage = 1;
        categoryCurrentPage = 1;

        loadShopProducts();
        loadShopCategories();
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

    public Integer getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(Integer categoryCount) {
        this.categoryCount = categoryCount;
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

    public void setSelectedCategoryLiveData(TabsItem selectedCategoryLiveData) {
        this.selectedCategoryLiveData.setValue(selectedCategoryLiveData);
    }

    public LiveData<ShopDetailsModel> getShopDetailsLiveData() {
        return shopDetailsLiveData;
    }

    public void loadShopProducts() {

        apiRepository.getShopDetailsItem(preferenceRepository.getToken(), shopSlug, currentPage, 21, categorySlug, campaignSlug, null, brandSlug, new ResponseListener<ShopDetailsModel, String>() {
            @Override
            public void onDataFetched(ShopDetailsModel response, int statusCode) {
                shopDetailsLiveData.setValue(response);
                productArrayList.addAll(response.getData().getItems());
                productListLiveData.setValue(productArrayList);
                currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void setBrandSlug(String brandSlug) {
        this.brandSlug = brandSlug;
    }

    public void loadShopCategories() {

        isCategoryLoading = true;

        apiRepository.getCategoriesOfShop(shopSlug, campaignSlug, categoryCurrentPage, new ResponseListener<JsonObject, String>() {
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
                    tabsItem.setSlug(ob.get("category_slug").getAsString().replaceAll("\"", ""));
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

        });
    }

    public void clearProductList() {
        productArrayList.clear();
    }

}
