package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;
import bd.com.evaly.evalyshop.models.notification.NotificationItem;


public class GeneralApiHelper extends BaseApiHelper {

    public static void getNotification(String token, int page, ResponseListenerAuth<CommonResultResponse<List<NotificationItem>>, String> listener) {
        getiApiClient().getNotification(token, page).enqueue(getResponseCallBackDefault(listener));
    }

    public static void markNotificationAsRead(String token, ResponseListenerAuth<JsonObject, String> listener) {
        getiApiClient().markNewsfeedNotificationAsRead(token).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getRootCategories(ResponseListenerAuth<List<CategoryEntity>, String> listener) {
        getiApiClient().getRootCategories().enqueue(getResponseCallBackDefault(listener));
    }

    public static void getTopCategories(ResponseListenerAuth<CommonDataResponse<List<CategoryEntity>>, String> listener) {
        getiApiClient().getTopCategories().enqueue(getResponseCallBackDefault(listener));
    }

    public static void getSubCategories(String rootCategory, ResponseListenerAuth<List<CategoryEntity>, String> listener) {
        getiApiClient().getSubCategories(rootCategory).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getBanners(ResponseListenerAuth<CommonResultResponse<List<BannerItem>>, String> listener) {
        getiApiClient().getBanners().enqueue(getResponseCallBackDefault(listener));
    }


    public static void getNotificationCount(String token, String notificationType, ResponseListenerAuth<NotificationCount, String> listener) {
        getiApiClient().getNotificationCount(token, notificationType).enqueue(getResponseCallBackDefault(listener));
    }


    public static void subscribeToShop(String token, String shopSlug, boolean subscribe, ResponseListenerAuth<JsonObject, String> listener) {
        if (subscribe) {

            HashMap<String, String> body = new HashMap<>();
            body.put("shop_slug", shopSlug);

            getiApiClient().subscribeToShop(token, body).enqueue(getResponseCallBackDefault(listener));
        } else
            getiApiClient().unsubscribeShop(token, shopSlug).enqueue(getResponseCallBackDefault(listener));
    }


    public static void checkReferral(HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener) {
        getiApiClient().checkReferral(body.get("device_id"), body.get("referred_by"), body.get("token")).enqueue(getResponseCallBackDefault(listener));
    }


}
