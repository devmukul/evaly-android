package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.order.OrderListItem;
import bd.com.evaly.evalyshop.models.order.payment.ParitalPaymentModel;

public class OrderApiHelper extends BaseApiHelper {


    public static void getOrderList(String token, int page, String orderStatus, ResponseListenerAuth<CommonResultResponse<List<OrderListItem>>, String> listener) {
        if (orderStatus.equals("all"))
            orderStatus = null;

        getiApiClient().getOrderList(token, page, orderStatus).enqueue(getResponseCallBackDefault(listener));
    }


    public static void placeOrder(String token, JsonObject body, ResponseListenerAuth<JsonObject, String> listener) {

        getiApiClient().placeOrder(token, body).enqueue(getResponseCallBackDefault(listener));
    }


    public static void makePartialPayment(String token, ParitalPaymentModel body, ResponseListenerAuth<JsonObject, String> listener){

        getiApiClient().makePartialPayment(token,body).enqueue(getResponseCallBackDefault(listener));

    }


}
