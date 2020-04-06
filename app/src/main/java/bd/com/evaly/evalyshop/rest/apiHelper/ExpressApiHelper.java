package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.express.ExpressServiceDetailsModel;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;

public class ExpressApiHelper extends BaseApiHelper {

    public static void getServicesList(ResponseListenerAuth<List<ExpressServiceModel>, String> listener) {
        getiApiClient().getExpressServicesList().enqueue(getResponseCallBackDefault(listener));
    }


    public static void getServiceDetails(String slug, ResponseListenerAuth<ExpressServiceDetailsModel, String> listener) {
        getiApiClient().getExpressServiceDetails(slug).enqueue(getResponseCallBackDefault(listener));
    }

}
