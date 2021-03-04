package bd.com.evaly.evalyshop.ui.search;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
    private MutableLiveData<List<FilterRootItem>> filterRootLiveList = new MutableLiveData<>();
    private MutableLiveData<List<FilterSubItem>> filterSubLiveList = new MutableLiveData<>();
    private MutableLiveData<List<FilterSubItem>> filterBrandsLiveList = new MutableLiveData<>();
    private MutableLiveData<List<FilterSubItem>> filterCategoriesLiveList = new MutableLiveData<>();
    private MutableLiveData<List<FilterSubItem>> filterShopsLiveList = new MutableLiveData<>();
    private MutableLiveData<List<FilterSubItem>> filterColorsLiveList = new MutableLiveData<>();
    private MutableLiveData<List<BaseModel>> productListLive = new MutableLiveData<>();
    private SingleLiveEvent<Void> reloadFilters = new SingleLiveEvent<>();
    private int page;
    private String type;
    private String query = "";
    private String selectedFilterRoot;

    @SuppressLint("DefaultLocale")
    @Inject
    public GlobalSearchViewModel() {
        selectedFilterRoot = "Sort by_name";
        searchParams = new AlgoliaParams();
        searchParams.setPage(page);
        requestsItem = new RequestsItem();
        requestsItem.setIndexName("products");
        page = 1;
        type = "product";
        searchOnAlogia();
    }

    public String getSelectedFilterRoot() {
        return selectedFilterRoot;
    }

    public void setSelectedFilterRoot(String selectedFilterRoot) {
        this.selectedFilterRoot = selectedFilterRoot;
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
            searchOnAlogia();
        else if (type.equals("brand"))
            getBrands();
        else if (type.equals("shop"))
            getShops();
    }


    public void searchOnAlogia() {

        SearchRequest body = new SearchRequest();
        body.setTerm(query);
        body.setBucketSize(10);
        body.setFrom(0);
        body.setSize(20);

        SearchApiHelper.searchProducts(body, page, new ResponseListenerAuth<CommonDataResponse<ProductSearchResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ProductSearchResponse> response, int statusCode) {
                productList.addAll(response.getData().getProducts());
                productListLive.setValue(productList);
                page++;

                filterBrandsLiveList.setValue(response.getData().getFacets().getBrands());
                filterCategoriesLiveList.setValue(response.getData().getFacets().getCategories());
                filterShopsLiveList.setValue(response.getData().getFacets().getShops());
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
