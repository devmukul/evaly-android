package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonSuccessResponse;
import bd.com.evaly.evalyshop.models.brand.BrandDetails;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;


public class GeneralApiHelper extends ApiHelper{


    public static void getBrandsDetails(String brandSlug, ResponseListenerAuth<CommonSuccessResponse<BrandDetails>, String> listener) {
        getiApiClient().getBrandDetails(brandSlug).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getRootCategories(ResponseListenerAuth<List<CategoryEntity>, String> listener) {
        getiApiClient().getRootCategories().enqueue(getResponseCallBackDefault(listener));
    }


    public static void getNotificationCount(String token, String notificationType, ResponseListenerAuth<NotificationCount, String> listener) {
        getiApiClient().getNotificationCount(token, notificationType).enqueue(getResponseCallBackDefault(listener));
    }


    public static void subscribeToShop(String token, String shopSlug, boolean subscribe, ResponseListenerAuth<JsonObject, String> listener) {
        if (subscribe) {

            HashMap<String,String> body = new HashMap<>();
            body.put("shop_slug", shopSlug);

            getiApiClient().subscribeToShop(token, body).enqueue(getResponseCallBackDefault(listener));
        }
        else
            getiApiClient().unsubscribeShop(token, shopSlug).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getShopReviews(String sku, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().getShopReviews(sku).enqueue(getResponseCallBackDefault(listener));
    }



    public static void checkReferral(HashMap<String,String> body, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().checkReferral(body).enqueue(getResponseCallBackDefault(listener));
    }


}
