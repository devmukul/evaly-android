package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.reviews.ReviewItem;

public class ReviewsApiHelper extends BaseApiHelper {

    public static void getShopReviews(String token, String shopSlug, int page, int limit, ResponseListenerAuth<CommonDataResponse<List<ReviewItem>>, String> listener) {

        getiApiClient().getShopReviews(token,shopSlug,page,limit).enqueue(getResponseCallBackDefault(listener));

    }


}
