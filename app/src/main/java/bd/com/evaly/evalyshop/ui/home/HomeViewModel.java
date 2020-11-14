package bd.com.evaly.evalyshop.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.tabs.TabItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ExpressApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.util.Constants;

public class HomeViewModel extends ViewModel {

    private int tabPosition = -1;
    private MutableLiveData<List<CampaignCategoryResponse>> categoryLiveList = new MutableLiveData<>();
    private MutableLiveData<List<CampaignProductResponse>> flashSaleProductList = new MutableLiveData<>();
    private MutableLiveData<List<ProductItem>> productListLive = new MutableLiveData<>();
    private List<ProductItem> productArrayList = new ArrayList<>();
    private MutableLiveData<List<BannerItem>> bannerListLive = new MutableLiveData<>();
    private MutableLiveData<List<ExpressServiceModel>> expressListLive = new MutableLiveData<>();
    private MutableLiveData<List<CategoryEntity>> categoryListLive = new MutableLiveData<>();
    private MutableLiveData<List<TabsItem>> brandListLive = new MutableLiveData<>();
    private List<TabsItem> brandArrayList = new ArrayList<>();
    private MutableLiveData<List<TabsItem>> shopListLive = new MutableLiveData<>();
    private List<TabItem> shopArrayList = new ArrayList<>();
    private int currentPageProducts = 1;

    public HomeViewModel() {
        currentPageProducts = 1;
        loadProducts();
        loadExpressServices();
        loadCampaignCategory();
        loadFlashSaleProductList();
    }

    public int getTabPosition() {
        return tabPosition;
    }

    public void setTabPosition(int tabPosition) {
        this.tabPosition = tabPosition;
    }

    public LiveData<List<CampaignCategoryResponse>> getCampaignCategoryLiveList() {
        return categoryLiveList;
    }

    public LiveData<List<CampaignProductResponse>> getFlashSaleProductList() {
        return flashSaleProductList;
    }

    public void loadFlashSaleProductList() {

        //  flash-sale-2509b8bb  hot-deal-3b06c2c4
        CampaignApiHelper.getCampaignCategoryProducts(1, 20, null, Constants.FLASH_SALE_SLUG, null,
                new ResponseListenerAuth<CommonDataResponse<List<CampaignProductResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CampaignProductResponse>> response, int statusCode) {
                        flashSaleProductList.setValue(response.getData());
                    }

                    @Override
                    public void onFailed(String errorBody, int errorCode) {

                    }

                    @Override
                    public void onAuthError(boolean logout) {

                    }
                });
    }

    public void loadCampaignCategory() {
        CampaignApiHelper.getCampaignCategory(new ResponseListenerAuth<CommonDataResponse<List<CampaignCategoryResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignCategoryResponse>> response, int statusCode) {
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

    public void loadBanners() {

        GeneralApiHelper.getBanners(new ResponseListenerAuth<CommonResultResponse<List<BannerItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<BannerItem>> response, int statusCode) {
                bannerListLive.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    private void loadExpressServices() {
        ExpressApiHelper.getServicesList(new ResponseListenerAuth<List<ExpressServiceModel>, String>() {
            @Override
            public void onDataFetched(List<ExpressServiceModel> response, int statusCode) {
                expressListLive.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void loadProducts() {

        ProductApiHelper.getCategoryBrandProducts(currentPageProducts, "root", null, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {

                productArrayList.addAll(response.getData());
                productListLive.setValue(productArrayList);

                if (response.getCount() > productArrayList.size())
                    currentPageProducts++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    public void loadBrands(int counter) {
        ProductApiHelper.getBrandsOfCategories("root", counter, 12, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject res, int statusCode) {

                JsonArray jsonArray = res.getAsJsonArray("results");

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject ob = jsonArray.get(i).getAsJsonObject();
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(ob.get("name").getAsString());
                    tabsItem.setImage(ob.get("image_url").isJsonNull() ? null : ob.get("image_url").getAsString());
                    tabsItem.setSlug(ob.get("slug").getAsString());
                    tabsItem.setCategory("root");
                    brandArrayList.add(tabsItem);
                }

                brandListLive.setValue(brandArrayList);
            }

            @Override
            public void onFailed(String body, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    public void loadCategories() {

        GeneralApiHelper.getRootCategories(new ResponseListenerAuth<List<CategoryEntity>, String>() {
            @Override
            public void onDataFetched(List<CategoryEntity> response, int statusCode) {
                categoryListLive.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });


    }


    public MutableLiveData<List<ProductItem>> getProductListLive() {
        return productListLive;
    }

    public void setProductListLive(MutableLiveData<List<ProductItem>> productListLive) {
        this.productListLive = productListLive;
    }

    public List<ProductItem> getProductArrayList() {
        return productArrayList;
    }

    public void setProductArrayList(List<ProductItem> productArrayList) {
        this.productArrayList = productArrayList;
    }

    public MutableLiveData<List<BannerItem>> getBannerListLive() {
        return bannerListLive;
    }

    public void setBannerListLive(MutableLiveData<List<BannerItem>> bannerListLive) {
        this.bannerListLive = bannerListLive;
    }

    public MutableLiveData<List<ExpressServiceModel>> getExpressListLive() {
        return expressListLive;
    }

    public void setExpressListLive(MutableLiveData<List<ExpressServiceModel>> expressListLive) {
        this.expressListLive = expressListLive;
    }

    public MutableLiveData<List<CategoryEntity>> getCategoryListLive() {
        return categoryListLive;
    }

    public void setCategoryListLive(MutableLiveData<List<CategoryEntity>> categoryListLive) {
        this.categoryListLive = categoryListLive;
    }

    public MutableLiveData<List<TabsItem>> getBrandListLive() {
        return brandListLive;
    }

    public void setBrandListLive(MutableLiveData<List<TabsItem>> brandListLive) {
        this.brandListLive = brandListLive;
    }

    public List<TabsItem> getBrandArrayList() {
        return brandArrayList;
    }

    public void setBrandArrayList(List<TabsItem> brandArrayList) {
        this.brandArrayList = brandArrayList;
    }

    public MutableLiveData<List<TabsItem>> getShopListLive() {
        return shopListLive;
    }

    public void setShopListLive(MutableLiveData<List<TabsItem>> shopListLive) {
        this.shopListLive = shopListLive;
    }

    public List<TabItem> getShopArrayList() {
        return shopArrayList;
    }

    public void setShopArrayList(List<TabItem> shopArrayList) {
        this.shopArrayList = shopArrayList;
    }

    public int getCurrentPageProducts() {
        return currentPageProducts;
    }

    public void setCurrentPageProducts(int currentPageProducts) {
        this.currentPageProducts = currentPageProducts;
    }
}