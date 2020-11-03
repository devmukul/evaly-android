package bd.com.evaly.evalyshop.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.search.AlgoliaParams;
import bd.com.evaly.evalyshop.models.search.AlgoliaRequest;
import bd.com.evaly.evalyshop.models.search.RequestsItem;
import bd.com.evaly.evalyshop.models.search.SearchHitResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.SearchApiHelper;

public class GlobalSearchViewModel {

    private RequestsItem requestsItem;
    private AlgoliaRequest algoliaRequest;
    private AlgoliaParams searchParams;
    private MutableLiveData<List<SearchHitResponse>> productList = new MutableLiveData<>();

    @Inject
    public GlobalSearchViewModel() {
        searchParams = new AlgoliaParams();
        requestsItem = new RequestsItem();
        requestsItem.setIndexName("products");
    }

    public AlgoliaParams getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(AlgoliaParams searchParams) {
        this.searchParams = searchParams;
    }

    public void setSearchQuery(String query) {
        searchParams.setQuery(query);
    }

    public void setIndex(String index) {
        requestsItem.setIndexName(index);
    }

    public void searchOnAlogia() {

        requestsItem.setParams(searchParams.getParams());
        algoliaRequest = new AlgoliaRequest();
        algoliaRequest.addRequest(requestsItem);

        SearchApiHelper.algoliaSearch(algoliaRequest, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                JsonArray jsonArray = response.getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonArray("hits");
                Type listType = new TypeToken<List<SearchHitResponse>>() {
                }.getType();
                productList.setValue(new Gson().fromJson(jsonArray, listType));
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public LiveData<List<SearchHitResponse>> getProductList() {
        return productList;
    }
}
