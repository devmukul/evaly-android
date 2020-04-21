package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.order.payment.ParitalPaymentModel;

public class PaymentApiHelper extends BaseApiHelper {

    public static void makePartialPayment(String token, ParitalPaymentModel body, ResponseListenerAuth<JsonObject, String> listener){

        getiApiClient().makePartialPayment(token,body).enqueue(getResponseCallBackDefault(listener));
    }


    public static void payViaCard(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener){

        getiApiClient().payViaCard(token, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void payViaBank(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener){

        getiApiClient().payViaBank(token, body).enqueue(getResponseCallBackDefault(listener));
    }
}
