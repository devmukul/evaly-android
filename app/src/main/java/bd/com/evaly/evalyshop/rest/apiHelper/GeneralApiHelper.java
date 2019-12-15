package bd.com.evaly.evalyshop.rest.apiHelper;

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

}
