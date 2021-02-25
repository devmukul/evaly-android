package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.location.LocationResponse;

public class LocationApiHelper extends BaseApiHelper {

    public static void getLocations(String parent, ResponseListenerAuth<CommonDataResponse<List<LocationResponse>>, String> listener) {
        getiApiClient().getLocations(parent).enqueue(getResponseCallBackDefault(listener));
    }

}
