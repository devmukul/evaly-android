package bd.com.evaly.evalyshop.ui.search;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.BaseModel;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopListResponse;
import bd.com.evaly.evalyshop.models.search.filter.FilterRootItem;
import bd.com.evaly.evalyshop.models.search.filter.FilterSubItem;
import bd.com.evaly.evalyshop.models.search.product.SearchRequest;
import bd.com.evaly.evalyshop.models.search.product.SortItem;
import bd.com.evaly.evalyshop.models.search.product.response.Facets;
import bd.com.evaly.evalyshop.models.search.product.response.ProductSearchResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GlobalSearchViewModel extends BaseViewModel {


    @Inject
    ApiRepository apiRepository;
    private List<BaseModel> productList = new ArrayList<>();
    protected SingleLiveEvent<Void> updateButtonHighlights = new SingleLiveEvent<>();
    private MutableLiveData<List<FilterRootItem>> filterRootLiveList = new MutableLiveData<>();
    private MutableLiveData<List<FilterSubItem>> filterSubLiveList = new MutableLiveData<>();
    protected MutableLiveData<Facets> facetsMutableLiveData = new MutableLiveData<>();

    protected List<FilterSubItem> filterBrandsList = new ArrayList<>();
    protected List<FilterSubItem> filterCategoriesList = new ArrayList<>();
    protected List<FilterSubItem> filterShopsList = new ArrayList<>();
    protected List<FilterSubItem> filterColorsList = new ArrayList<>();

    public List<String> selectedFilterBrandsList = new ArrayList<>();
    public List<String> selectedFilterCategoriesList = new ArrayList<>();
    public List<String> selectedFilterShopsList = new ArrayList<>();
    public List<String> selectedFilterColorsList = new ArrayList<>();

    private MutableLiveData<List<BaseModel>> productListLive = new MutableLiveData<>();
    private SingleLiveEvent<Void> reloadFilters = new SingleLiveEvent<>();
    private int page;
    private String type;
    private String query = "";
    private String sortBy = null;
    private Integer minPrice = null;
    private Integer maxPrice = null;
    private boolean isPriceRangeSelected = false;

    @SuppressLint("DefaultLocale")
    @Inject
    public GlobalSearchViewModel(SavedStateHandle bundle, ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
        page = 1;
        type = "product";
        if (bundle.contains("type"))
            type = bundle.get("type");
        performSearch();
    }

    public void clearFilters() {
        selectedFilterBrandsList.clear();
        selectedFilterCategoriesList.clear();
        selectedFilterShopsList.clear();
        selectedFilterColorsList.clear();
        minPrice = null;
        maxPrice = null;
        isPriceRangeSelected = false;
    }

    public String getType() {
        return type;
    }

    public void setPriceRangeSelected(boolean priceRangeSelected) {
        isPriceRangeSelected = priceRangeSelected;
    }

    public boolean isPriceRangeSelected() {
        return isPriceRangeSelected;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setReloadFilters() {
        reloadFilters.call();
    }

    public SingleLiveEvent<Void> getReloadFilters() {
        return reloadFilters;
    }

    public void setSearchQuery(String query) {
        this.query = query;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void performSearch() {
        page = 1;
        productList.clear();
        if (type.equals("product"))
            searchProducts();
        else if (type.equals("brand"))
            getBrands();
        else if (type.equals("shop"))
            getShops();
    }

    public void loadMore() {
        if (type.equals("product"))
            searchProducts();
        else if (type.equals("brand"))
            getBrands();
        else if (type.equals("shop"))
            getShops();
    }


    public void searchProducts() {

        SearchRequest body = new SearchRequest();
        body.setTerm(query);
        body.setBucketSize(10);
        body.setFrom(0);
        body.setSize(20);

        if (sortBy != null) {
            SortItem sortItem = new SortItem();
            sortItem.setFieldName("discounted_price");
            sortItem.setOrder(sortBy);
            List<SortItem> sortList = new ArrayList<>();
            sortList.add(sortItem);
            body.setSort(sortList);
        }

        body.setBrandFilters(selectedFilterBrandsList);
        body.setShopFilters(selectedFilterShopsList);
        body.setCategoryFilters(selectedFilterCategoriesList);
        body.setColorFilters(selectedFilterColorsList);
        body.setMinPrice(minPrice);
        body.setMaxPrice(maxPrice);

        apiRepository.searchProducts(body, page, new ResponseListenerAuth<CommonDataResponse<ProductSearchResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ProductSearchResponse> response, int statusCode) {
                productList.addAll(response.getData().getProducts());
                productListLive.setValue(productList);
                page++;

                filterBrandsList = response.getData().getFacets().getBrands();
                filterCategoriesList = response.getData().getFacets().getCategories();
                filterShopsList = response.getData().getFacets().getShops();
                facetsMutableLiveData.setValue(response.getData().getFacets());
                // filterColorsLiveList.setValue(response.getData().getFacets().getColors());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    public void getShops() {

        apiRepository.getShops(null, query, page, null, new ResponseListenerAuth<CommonDataResponse<List<ShopListResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ShopListResponse>> response, int statusCode) {
                productList.addAll(response.getData());
                productListLive.setValue(productList);
                page++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void getBrands() {
        apiRepository.getBrands(null, query, page, new ResponseListenerAuth<CommonDataResponse<List<BrandResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<BrandResponse>> response, int statusCode) {
                productList.addAll(response.getData());
                productListLive.setValue(productList);
                page++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public LiveData<List<BaseModel>> getProductList() {
        return productListLive;
    }

}
