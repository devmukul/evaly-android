package bd.com.evaly.evalyshop.ui.payment.bottomsheet;

import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.order.payment.ParitalPaymentModel;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;

public class PaymentBottomSheetViewModel extends ViewModel {

    private PaymentBottomSheetNavigator navigator;

    public void setNavigator(PaymentBottomSheetNavigator navigator) {
        this.navigator = navigator;
    }

    public void makePartialPayment(String invoice, String amount) {

        ParitalPaymentModel model = new ParitalPaymentModel();

        model.setInvoice_no(invoice);
        model.setAmount(Double.parseDouble(amount));

        OrderApiHelper.makePartialPayment(CredentialManager.getToken(), model, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                if (response != null) {
                    if (response.get("success").getAsBoolean())
                        navigator.onPaymentSuccess(response.get("message").toString());
                    else
                        navigator.onPaymentFailed(response.get("message").toString());
                } else {
                    navigator.onPaymentFailed("Payment failed, try again later");
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                navigator.onPaymentFailed("Payment failed, try again later");
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    makePartialPayment(invoice, amount);
            }
        });

    }

    public void makeCashOnDelivery(String invoice) {

        HashMap<String, String> data = new HashMap<>();
        data.put("invoice_no", invoice);
        OrderApiHelper.makeCashOnDelivery(CredentialManager.getToken(), data, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                if (response != null) {
                    if (response.get("success").getAsBoolean())
                        navigator.onPaymentSuccess(response.get("message").toString());
                    else
                        navigator.onPaymentFailed(response.get("message").toString());
                } else {
                    navigator.onPaymentFailed("Payment failed, try again later");
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                navigator.onPaymentFailed("Payment failed, try again later");
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    makeCashOnDelivery(invoice);
            }
        });

    }

    public void payViaCard(String invoice, String amount) {

        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", amount);
        payload.put("context", "order_payment");
        payload.put("context_reference", invoice);

        OrderApiHelper.payViaCard(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                if ((response != null && response.has("payment_gateway_url")) && !response.get("payment_gateway_url").isJsonNull()) {
                    String purl = response.get("payment_gateway_url").getAsString();
                    navigator.payViaCard(purl);
                } else
                    navigator.payViaCard("");
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                navigator.payViaCard("");
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    payViaCard(invoice, amount);
            }
        });

    }

    public void payViaNagad(String invoice, String amount) {

        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", amount);
        payload.put("context", "order_payment");
        payload.put("context_reference", invoice);
        payload.put("source", "MOBILE_APP");

        OrderApiHelper.payViaNagad(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                if ((response != null && response.has("callBackUrl")) && !response.get("callBackUrl").isJsonNull()) {
                    String purl = response.get("callBackUrl").getAsString();
                    navigator.payViaCard(purl);
                } else
                    navigator.payViaCard("");
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                navigator.payViaCard("");
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    payViaCard(invoice, amount);

            }
        });

    }


}
