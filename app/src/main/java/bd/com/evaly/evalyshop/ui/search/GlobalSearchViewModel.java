package bd.com.evaly.evalyshop.ui.search;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.BaseModel;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.search.AlgoliaParams;
import bd.com.evaly.evalyshop.models.search.AlgoliaRequest;
import bd.com.evaly.evalyshop.models.search.RequestsItem;
import bd.com.evaly.evalyshop.models.search.filter.FilterRootItem;
import bd.com.evaly.evalyshop.models.search.filter.FilterSubItem;
import bd.com.evaly.evalyshop.models.search.product.SearchRequest;
import bd.com.evaly.evalyshop.models.search.product.SortItem;
import bd.com.evaly.evalyshop.models.search.product.response.ProductSearchResponse;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.SearchApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class GlobalSearchViewModel extends BaseViewModel {

    private RequestsItem requestsItem;
    private AlgoliaRequest algoliaRequest;
    private AlgoliaParams searchParams;
    private List<BaseModel> productList = new ArrayList<>();
    protected SingleLiveEvent<Void> updateButtonHighlights = new SingleLiveEvent<>();
    private MutableLiveData<List<FilterRootItem>> filterRootLiveList = new MutableLiveData<>();
    private MutableLiveData<List<FilterSubItem>> filterSubLiveList = new MutableLiveData<>();

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
    protected boolean isPriceRangeSelected = false;

    @SuppressLint("DefaultLocale")
    @Inject
    public GlobalSearchViewModel() {
        searchParams = new AlgoliaParams();
        searchParams.setPage(page);
        requestsItem = new RequestsItem();
        requestsItem.setIndexName("products");
        page = 1;
        type = "product";
        searchProducts();
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

    public AlgoliaParams getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(AlgoliaParams searchParams) {
        this.searchParams = searchParams;
    }

    public void setSearchQuery(String query) {
        this.query = query;
    }

    public void setIndex(String index) {
        requestsItem.setIndexName(index);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
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


    public void searchProducts() {

        SearchRequest body = new SearchRequest();
        body.setTerm(query);
        body.setBucketSize(10);
        body.setFrom(0);
        body.setSize(20);

        if (sortBy != null) {
            SortItem sortItem = new SortItem();
            sortItem.setFieldName("price");
            sortItem.setOrder(sortBy);
            List<SortItem> sortList = new ArrayList<>();
            sortList.add(sortItem);
            body.setSort(sortList);
        }

        body.setBrandFilters(selectedFilterBrandsList);
        body.setShopFilters(selectedFilterShopsList);
        body.setCategoryFilters(selectedFilterCategoriesList);
        body.setColorFilters(selectedFilterColorsList);

        SearchApiHelper.searchProducts(body, page, new ResponseListenerAuth<CommonDataResponse<ProductSearchResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ProductSearchResponse> response, int statusCode) {
                productList.addAll(response.getData().getProducts());
                productListLive.setValue(productList);
                page++;

                filterBrandsList = response.getData().getFacets().getBrands();
                filterCategoriesList = response.getData().getFacets().getCategories();
                filterShopsList = response.getData().getFacets().getShops();
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
        SearchApiHelper.searchShops(page, query, new ResponseListenerAuth<CommonDataResponse<List<TabsItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<TabsItem>> response, int statusCode) {
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
        SearchApiHelper.searchBrands(page, query, new ResponseListenerAuth<CommonDataResponse<List<TabsItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<TabsItem>> response, int statusCode) {
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

    public LiveData<List<FilterRootItem>> getFilterRootLiveList() {
        return filterRootLiveList;
    }

    public LiveData<List<FilterSubItem>> getFilterSubLiveList() {
        return filterSubLiveList;
    }
}
