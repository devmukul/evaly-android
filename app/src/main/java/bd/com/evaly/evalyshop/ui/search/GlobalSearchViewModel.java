package bd.com.evaly.evalyshop.ui.search;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.BaseModel;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.search.AlgoliaParams;
import bd.com.evaly.evalyshop.models.search.AlgoliaRequest;
import bd.com.evaly.evalyshop.models.search.RequestsItem;
import bd.com.evaly.evalyshop.models.search.SearchHitResponse;
import bd.com.evaly.evalyshop.models.shop.shopItem.ShopItem;
import bd.com.evaly.evalyshop.rest.apiHelper.SearchApiHelper;

public class GlobalSearchViewModel {

    private RequestsItem requestsItem;
    private AlgoliaRequest algoliaRequest;
    private AlgoliaParams searchParams;
    private List<BaseModel> productList = new ArrayList<>();
    private MutableLiveData<List<BaseModel>> productListLive = new MutableLiveData<>();
    private int page;
    private String type;
    private String query;

    @SuppressLint("DefaultLocale")
    @Inject
    public GlobalSearchViewModel() {
        searchParams = new AlgoliaParams();
        searchParams.setPage(String.format("%d", page));
        requestsItem = new RequestsItem();
        requestsItem.setIndexName("products");
        page = 1;
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

    public void searchOnAlogia() {

        searchParams.setQuery(query);
        requestsItem.setParams(searchParams.getParams());
        algoliaRequest = new AlgoliaRequest();
        algoliaRequest.addRequest(requestsItem);
        searchParams.setPage(String.format("%d", page));

        SearchApiHelper.algoliaSearch(algoliaRequest, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                JsonArray jsonArray = response.getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonArray("hits");
                Type listType = new TypeToken<List<SearchHitResponse>>() {
                }.getType();
                productList.addAll(new Gson().fromJson(jsonArray, listType));
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


    public void getShops(){

        SearchApiHelper.searchShops(page, query, new ResponseListenerAuth<CommonDataResponse<List<ShopItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ShopItem>> response, int statusCode) {

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
