package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.hero.DeliveryHeroResponse;
import bd.com.evaly.evalyshop.models.order.OrderListItem;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.models.order.payment.ParitalPaymentModel;
import bd.com.evaly.evalyshop.models.order.updateAddress.UpdateOrderAddressRequest;
import bd.com.evaly.evalyshop.models.orderRequest.OrderRequestResponse;

public class OrderApiHelper extends BaseApiHelper {

    public static void updateAddress(UpdateOrderAddressRequest body, ResponseListenerAuth<CommonDataResponse<OrderDetailsModel>, String> listener) {
        getiApiClient().updateOrderAddress(CredentialManager.getToken(), body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getOrderRequestList(int page, ResponseListenerAuth<CommonDataResponse<List<OrderRequestResponse>>, String> listener) {
        getiApiClient().getOrderRequests(CredentialManager.getToken(), page).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getOrderList(String token, int page, String orderStatus, ResponseListenerAuth<CommonResultResponse<List<OrderListItem>>, String> listener) {
        if (orderStatus.equals("all"))
            orderStatus = null;

        getiApiClient().getOrderList(token, page, orderStatus).enqueue(getResponseCallBackDefault(listener));
    }


    public static void placeOrder(String token, JsonObject body, ResponseListenerAuth<JsonObject, String> listener) {

        getiApiClient().placeOrder(token, body).enqueue(getResponseCallBackDefault(listener));
    }


    public static void makePartialPayment(String token, ParitalPaymentModel body, ResponseListenerAuth<JsonObject, String> listener) {

        getiApiClient().makePartialPayment(token, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void makeCashOnDelivery(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener) {

        getiApiClient().makeCashOnDelivery(token, body).enqueue(getResponseCallBackDefault(listener));
    }


    public static void payViaCard(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener) {

        getiApiClient().payViaCard(token, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void payViaSEBL(String token, String amount, String invoice, String context, ResponseListenerAuth<JsonObject, String> listener) {

        getiApiClient().payViaSEBL(token, amount, invoice, token, context).enqueue(getResponseCallBackDefault(listener));
    }


    public static void payViaNagad(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener) {

        getiApiClient().payViaNagad(token, body).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getOrderDetails(String token, String invoiceNo, ResponseListenerAuth<OrderDetailsModel, String> listener) {

        getiApiClient().getOrderDetails(token, invoiceNo).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getOrderHistories(String token, String invoiceNo, ResponseListenerAuth<JsonObject, String> listener) {

        getiApiClient().getOrderHistories(token, invoiceNo).enqueue(getResponseCallBackDefault(listener));
    }

    public static void cancelOrder(String token, String invoiceNo, String userNote, ResponseListenerAuth<CommonDataResponse, String> listener) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("order_status", "cancel");
        hashMap.put("user_note", userNote);

        getiApiClient().cancelOrder(token, invoiceNo, hashMap).enqueue(getResponseCallBackDefault(listener));
    }

    public static void confirmDelivery(String token, String invoiceNo, ResponseListenerAuth<CommonDataResponse, String> listener) {
        getiApiClient().confirmDelivery(token, invoiceNo).enqueue(getResponseCallBackDefault(listener));
    }

    public static void requestRefund(String token, HashMap<String, String> body, ResponseListenerAuth<CommonDataResponse<String>, String> listener) {
        getiApiClient().postRequestRefund(token, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void requestRefundConfirmOTP(String token, String invoice, HashMap<String, Integer> body, ResponseListenerAuth<CommonDataResponse<String>, String> listener) {
        getiApiClient().postRequestRefundConfirmOTP(token, body, invoice).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getDeliveryHero(String invoiceNo, ResponseListenerAuth<DeliveryHeroResponse, String> listener) {
        getiApiClient().getDeliveryHero(CredentialManager.getToken(), invoiceNo).enqueue(getResponseCallBackDefault(listener));
    }

    public static void checkRefundEligibility(String invoiceNo, ResponseListenerAuth<CommonDataResponse<String>, String> listener) {
        getiApiClient().checkRefundEligibility(CredentialManager.getToken(), invoiceNo.toUpperCase()).enqueue(getResponseCallBackDefault(listener));
    }

    public static void deleteRefundTransaction(String invoiceNo, ResponseListenerAuth<CommonDataResponse<String>, String> listener) {
        getiApiClient().deleteRefundTransaction(CredentialManager.getToken(), invoiceNo.toUpperCase()).enqueue(getResponseCallBackDefault(listener));
    }
}
