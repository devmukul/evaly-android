package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.reviews.ReviewItem;

public class ReviewsApiHelper extends BaseApiHelper {

    public static void getReviewSummary(String token, String slug, boolean isShop, ResponseListenerAuth<JsonObject, String> listener) {
        if (isShop)
            getiApiClient().getShopReviews(token, slug).enqueue(getResponseCallBackDefault(listener));
        else
            getiApiClient().getProductReviewSummary(token, slug).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getReviews(String token, String shopSlug, int page, int limit, boolean isShop, ResponseListenerAuth<CommonDataResponse<List<ReviewItem>>, String> listener) {
        if (isShop)
            getiApiClient().getShopReviews(token, shopSlug, page, limit).enqueue(getResponseCallBackDefault(listener));
        else
            getiApiClient().getProductReviews(token, shopSlug, page, limit).enqueue(getResponseCallBackDefault(listener));
    }

    public static void postReview(String token, String slug, JsonObject body, boolean isShop, ResponseListenerAuth<JsonObject, String> listener) {
        if (isShop)
            getiApiClient().postShopReview(token, slug, body).enqueue(getResponseCallBackDefault(listener));
        else
            getiApiClient().postProductReview(token, slug, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void checkReviewEligibility(String token, String slug, boolean isShop, ResponseListenerAuth<JsonObject, String> listener) {
        if (isShop)
            getiApiClient().checkShopReviewEligibility(token, slug).enqueue(getResponseCallBackDefault(listener));
        else
            getiApiClient().checkProductReviewEligibility(token, slug).enqueue(getResponseCallBackDefault(listener));
    }


}
