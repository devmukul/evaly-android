package bd.com.evaly.evalyshop.ui.search;

import com.google.gson.JsonObject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.search.AlgoliaParams;
import bd.com.evaly.evalyshop.models.search.AlgoliaRequest;
import bd.com.evaly.evalyshop.rest.apiHelper.SearchApiHelper;

public class GlobalSearchViewModel {

    private AlgoliaRequest algoliaRequest;
    private AlgoliaParams searchParams;

    public GlobalSearchViewModel() {
        searchParams = new AlgoliaParams();
    }

    public void setSearchParams(AlgoliaParams searchParams) {
        this.searchParams = searchParams;
    }

    public AlgoliaParams getSearchParams() {
        return searchParams;
    }

    public void searchOnAlogia() {
        SearchApiHelper.algoliaSearch(algoliaRequest, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }
}
