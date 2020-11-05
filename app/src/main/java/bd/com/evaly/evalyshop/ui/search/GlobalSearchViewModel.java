package bd.com.evaly.evalyshop.ui.search;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.BaseModel;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.search.AlgoliaParams;
import bd.com.evaly.evalyshop.models.search.AlgoliaRequest;
import bd.com.evaly.evalyshop.models.search.RequestsItem;
import bd.com.evaly.evalyshop.models.search.SearchHitResponse;
import bd.com.evaly.evalyshop.models.search.filter.FilterRootItem;
import bd.com.evaly.evalyshop.models.search.filter.FilterSubItem;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.SearchApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class GlobalSearchViewModel {

    private RequestsItem requestsItem;
    private AlgoliaRequest algoliaRequest;
    private AlgoliaParams searchParams;
    private List<BaseModel> productList = new ArrayList<>();
    private MutableLiveData<List<FilterRootItem>> filterRootLiveList = new MutableLiveData<>();
    private MutableLiveData<List<FilterSubItem>> filterSubLiveList = new MutableLiveData<>();
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

    public void setSelectedFilterRoot(String selectedFilterRoot) {
        this.selectedFilterRoot = selectedFilterRoot;
    }

    public String getSelectedFilterRoot() {
        return selectedFilterRoot;
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

        searchParams.setQuery(query);
        requestsItem.setParams(searchParams.getParams());
        algoliaRequest = new AlgoliaRequest();
        algoliaRequest.addRequest(requestsItem);
        searchParams.setPage(page);

        SearchApiHelper.algoliaSearch(algoliaRequest, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                JsonObject rootResponse = response.getAsJsonArray("results").get(0).getAsJsonObject();
                JsonArray jsonArray = response.getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonArray("hits");
                Type listType = new TypeToken<List<SearchHitResponse>>() {
                }.getType();
                productList.addAll(new Gson().fromJson(jsonArray, listType));
                productListLive.setValue(productList);
                page++;

                JsonObject filterJson = rootResponse.getAsJsonObject("facets");

                List<FilterRootItem> filterRootItems = new ArrayList<>();
                filterRootItems.add(new FilterRootItem("Sort by_name", false));
                List<FilterSubItem> filterSubItems = new ArrayList<>();
                filterSubItems.add(new FilterSubItem("Relevance", null, "Sort by_name", false));
                filterSubItems.add(new FilterSubItem("Price: low to high", null, "Sort by_name", false));
                filterSubItems.add(new FilterSubItem("Price: high to low", null, "Sort by_name", false));

                Set<Map.Entry<String, JsonElement>> entries = filterJson.entrySet();

                for (Map.Entry<String, JsonElement> entry : entries) {

                    filterRootItems.add(new FilterRootItem(entry.getKey(), false));
                    Set<Map.Entry<String, JsonElement>> subEntries = entry.getValue().getAsJsonObject().entrySet();

                    for (Map.Entry<String, JsonElement> subEntry : subEntries) {
                        filterSubItems.add(
                                new FilterSubItem(subEntry.getKey(),
                                        subEntry.getValue().getAsString(),
                                        entry.getKey(), false));
                    }
                }

                filterRootLiveList.setValue(filterRootItems);
                filterSubLiveList.setValue(filterSubItems);

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
