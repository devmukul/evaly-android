package bd.com.evaly.evalyshop.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.tabs.TabItem;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.banner.BannerDao;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.models.campaign.brand.CampaignBrandResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.models.campaign.shop.CampaignShopResponse;
import bd.com.evaly.evalyshop.models.campaign.topProducts.CampaignTopProductResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopListResponse;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.Constants;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    protected MutableLiveData<List<CampaignProductResponse>> flashSaleProductList = new MutableLiveData<>();
    protected MutableLiveData<List<CampaignBrandResponse>> flashSaleBrandList = new MutableLiveData<>();
    protected MutableLiveData<List<CampaignShopResponse>> flashSaleShopList = new MutableLiveData<>();
    protected MutableLiveData<List<ShopListResponse>> codShopList = new MutableLiveData<>();
    protected MutableLiveData<List<CampaignTopProductResponse>> topCampaignProductsLiveList = new MutableLiveData<>();
    private ApiRepository apiRepository;
    private int tabPosition = -1;
    private MutableLiveData<List<CampaignCategoryResponse>> categoryLiveList = new MutableLiveData<>();
    private MutableLiveData<List<CampaignProductResponse>> productListLive = new MutableLiveData<>();
    private List<CampaignProductResponse> productArrayList = new ArrayList<>();
    private LiveData<List<BannerItem>> bannerListLive;
    private MutableLiveData<List<ExpressServiceModel>> expressListLive = new MutableLiveData<>();
    private MutableLiveData<List<CategoryEntity>> categoryListLive = new MutableLiveData<>();
    private MutableLiveData<List<TabsItem>> brandListLive = new MutableLiveData<>();
    private List<TabsItem> brandArrayList = new ArrayList<>();
    private MutableLiveData<List<TabsItem>> shopListLive = new MutableLiveData<>();
    private List<TabItem> shopArrayList = new ArrayList<>();
    private int currentPageProducts = 1;
    private CompositeDisposable compositeDisposable;
    private BannerDao bannerDao;

    @Inject
    public HomeViewModel(BannerDao bannerDao, ApiRepository apiRepository) {
        this.bannerDao = bannerDao;
        this.apiRepository = apiRepository;

        bannerListLive = bannerDao.getAll();
        compositeDisposable = new CompositeDisposable();
        currentPageProducts = 1;
        loadBanners();
        loadCampaignTopProducts();
        loadProducts();
        loadExpressServices();
        loadFlashSaleBrandsList();
        loadFlashSaleShopList();
        loadCodShops();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public void reload() {
        brandArrayList.clear();
        productArrayList.clear();
        shopArrayList.clear();
        currentPageProducts = 1;
        loadBanners();
        loadProducts();
        loadExpressServices();
        loadCampaignCategory();
        loadFlashSaleProductList();
        loadFlashSaleBrandsList();
        loadFlashSaleShopList();
        loadCodShops();
        loadCampaignTopProducts();
    }

    public void loadCampaignTopProducts() {
        apiRepository.getCampaignCategoryTopProducts(new ResponseListener<CommonDataResponse<List<CampaignTopProductResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignTopProductResponse>> response, int statusCode) {
                topCampaignProductsLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void loadCodShops() {
        apiRepository.getShops(null, null, 1, "cod", new ResponseListener<CommonDataResponse<List<ShopListResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ShopListResponse>> response, int statusCode) {
                codShopList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
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

        apiRepository.getCampaignCategoryProducts(1, 20, null, Constants.FLASH_SALE_SLUG, null, null, null,
                new ResponseListener<CommonDataResponse<List<CampaignProductResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CampaignProductResponse>> response, int statusCode) {
                        flashSaleProductList.setValue(response.getData());
                    }

                    @Override
                    public void onFailed(String errorBody, int errorCode) {

                    }

                });
    }

    public void loadFlashSaleBrandsList() {

        apiRepository.getCampaignCategoryBrands(1, 20, null, Constants.FLASH_SALE_SLUG, null,
                new ResponseListener<CommonDataResponse<List<CampaignBrandResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CampaignBrandResponse>> response, int statusCode) {
                        flashSaleBrandList.setValue(response.getData());
                    }

                    @Override
                    public void onFailed(String errorBody, int errorCode) {

                    }

                });
    }

    public void loadFlashSaleShopList() {

        apiRepository.getCampaignCategoryShops(1, 20, null, Constants.FLASH_SALE_SLUG, null,
                new ResponseListener<CommonDataResponse<List<CampaignShopResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CampaignShopResponse>> response, int statusCode) {
                        flashSaleShopList.setValue(response.getData());
                    }

                    @Override
                    public void onFailed(String errorBody, int errorCode) {

                    }

                });
    }

    public void loadCampaignCategory() {
        apiRepository.getCampaignCategory(new ResponseListener<CommonDataResponse<List<CampaignCategoryResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignCategoryResponse>> response, int statusCode) {
                categoryLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void loadBanners() {

        apiRepository.getBanners(new ResponseListener<CommonResultResponse<JsonObject>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<JsonObject> response, int statusCode) {
                if (response.getData() == null || !response.getData().has("advertisement_list"))
                    return;
                List<BannerItem> bannerItems = new Gson().fromJson(response.getData().get("advertisement_list"), new TypeToken<List<BannerItem>>() {
                }.getType());
                bannerDao.insertListRx(bannerItems)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onComplete() {
                                List<String> slugs = new ArrayList<>();
                                for (BannerItem item : bannerItems)
                                    slugs.add(item.slug);
                                deleteOldBanners(slugs);
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                            }
                        });
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }

    private void deleteOldBanners(List<String> slugs) {
        bannerDao.deleteOldRx(slugs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }
                });
    }

    private void loadExpressServices() {
        apiRepository.getExpressServicesList(new ResponseListener<CommonDataResponse<List<ExpressServiceModel>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ExpressServiceModel>> response, int statusCode) {
                expressListLive.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void loadProducts() {

        apiRepository.getCampaignAllProducts(currentPageProducts, 20, null, new ResponseListener<CommonDataResponse<List<CampaignProductResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignProductResponse>> response, int statusCode) {
                productArrayList.addAll(response.getData());
                productListLive.setValue(productArrayList);
                currentPageProducts++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }

    public MutableLiveData<List<CampaignProductResponse>> getProductListLive() {
        return productListLive;
    }

    public LiveData<List<BannerItem>> getBannerListLive() {
        return bannerListLive;
    }

    public MutableLiveData<List<ExpressServiceModel>> getExpressListLive() {
        return expressListLive;
    }

}
