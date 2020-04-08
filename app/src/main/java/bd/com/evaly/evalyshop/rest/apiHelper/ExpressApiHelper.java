package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.express.ExpressServiceDetailsModel;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.shop.GroupShopModel;

public class ExpressApiHelper extends BaseApiHelper {

    public static void getServicesList(ResponseListenerAuth<List<ExpressServiceModel>, String> listener) {
        getiApiClient().getExpressServicesList().enqueue(getResponseCallBackDefault(listener));
    }


    public static void getServiceDetails(String slug, ResponseListenerAuth<ExpressServiceDetailsModel, String> listener) {
        getiApiClient().getExpressServiceDetails(slug).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getShopList(String serviceSug,
                                   int page,
                                   int limit,
                                   String address,
                                   String address2,
                                   String search,
                                   Double longitude,
                                   Double latitude,
                                   ResponseListenerAuth<CommonResultResponse<List<GroupShopModel>>, String> listener) {
        getiApiClient().getExpressShopList(serviceSug, page, limit, address, address2, search, longitude, latitude).enqueue(getResponseCallBackDefault(listener));
    }

}