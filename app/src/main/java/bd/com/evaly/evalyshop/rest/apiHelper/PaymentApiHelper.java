package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.order.payment.ParitalPaymentModel;
import bd.com.evaly.evalyshop.models.pay.BalanceResponse;

public class PaymentApiHelper extends BaseApiHelper {

    // cashback claim

    public static void claimCashback(String token, String username, String url, ResponseListenerAuth<JsonObject, String> listener) {
        getiApiClient().claimCashBack(token, username, url).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getBalance(String token, String username, String url, ResponseListenerAuth<CommonDataResponse<BalanceResponse>, String> listener) {
        getiApiClient().getBalance(token, url).enqueue(getResponseCallBackDefault(listener));
    }

    public static void makePartialPayment(String token, ParitalPaymentModel body, ResponseListenerAuth<JsonObject, String> listener) {
        getiApiClient().makePartialPayment(token, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void payViaCard(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener) {
        getiApiClient().payViaCard(token, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void payViaCityBank(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener) {
        getiApiClient().payViaCityBank(token, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void payViaBank(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener) {
        getiApiClient().payViaBank(token, body).enqueue(getResponseCallBackDefault(listener));
    }
}
