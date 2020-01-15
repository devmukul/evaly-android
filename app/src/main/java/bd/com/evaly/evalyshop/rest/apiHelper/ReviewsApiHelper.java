package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.reviews.ReviewItem;

public class ReviewsApiHelper extends BaseApiHelper {

    public static void getShopReviews(String token, String shopSlug, int page, int limit, ResponseListenerAuth<CommonDataResponse<List<ReviewItem>>, String> listener) {
        getiApiClient().getShopReviews(token,shopSlug,page,limit).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getShopRatings(String token, String slug, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().getShopReviews(token, slug).enqueue(getResponseCallBackDefault(listener));
    }

    public static void postShopReview(String token, String slug, JsonObject body, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().postShopReview(token, slug, body).enqueue(getResponseCallBackDefault(listener));
    }

}
