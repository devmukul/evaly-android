package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.order.OrderListItem;

public class OrderApiHelper extends ApiHelper {


    public static void getOrderList(String token, int page, String orderStatus, ResponseListenerAuth<CommonResultResponse<List<OrderListItem>>, String> listener) {


        if (orderStatus.equals("all"))
            orderStatus = null;

        getiApiClient().getOrderList(token, page, orderStatus).enqueue(getResponseCallBackResult(listener));

    }



}
