package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.search.AlgoliaRequest;

public class SearchApiHelper extends BaseApiHelper {

    public static void algoliaSearch(AlgoliaRequest body, ResponseListenerAuth<JsonObject, String> listener) {
        getiApiClient().searchOnAlgolia("Algolia%20for%20JavaScript%20(3.35.1)%3B%20Browser%20(lite)%3B%20react%20(16.13.1)%3B%20react-instantsearch%20(5.7.0)%3B%20JS%20Helper%20(2.28.1)",
                "EZA2J926Q5",
                "ca9abeea06c16b7d531694d6783a8f04", body).enqueue(getResponseCallBackDefault(listener));
    }

}
