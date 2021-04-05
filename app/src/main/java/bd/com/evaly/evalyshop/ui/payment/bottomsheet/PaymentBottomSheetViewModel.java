package bd.com.evaly.evalyshop.ui.payment.bottomsheet;

import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.data.remote.ApiRepository;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.order.payment.ParitalPaymentModel;
import bd.com.evaly.evalyshop.rest.apiHelper.apiRepository;
import bd.com.evaly.evalyshop.rest.apiHelper.apiRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PaymentBottomSheetViewModel extends ViewModel {

    private ApiRepository apiRepository;
    private PaymentBottomSheetNavigator navigator;
    
    @Inject
    public PaymentBottomSheetViewModel(ApiRepository apiRepository){
        this.apiRepository = apiRepository;
    }

    public void setNavigator(PaymentBottomSheetNavigator navigator) {
        this.navigator = navigator;
    }

    public void makePartialPayment(String invoice, String amount) {

        ParitalPaymentModel model = new ParitalPaymentModel();

        model.setInvoice_no(invoice);
        model.setAmount(Double.parseDouble(amount));

        apiRepository.makePartialPayment(CredentialManager.getToken(), model, new ResponseListenerAuth<JsonObject, String>() {
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
        apiRepository.makeCashOnDelivery(CredentialManager.getToken(), data, new ResponseListenerAuth<JsonObject, String>() {
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

    public void payViaSEBL(String invoice, String amount) {
        navigator.payViaCard(BuildConfig.WEB_URL + "sebl/payment?amount=" + amount + "&invoice=" + invoice + "&token=" + CredentialManager.getToken().replace("Bearer ", "") + "&context_reference=order_payment");
//        navigator.payViaCard("http://192.168.68.126:3200/sebl/payment?amount=10&invoice=EVL707799707&token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6IjAxOTc3NTM3OTg4IiwiZ3JvdXBzIjpbIlNob3BPd25lciIsIlJlc3RhdXJhbnRPd25lciJdLCJmaXJzdF9uYW1lIjoiSXNoIiwibGFzdF9uYW1lIjoiQWsiLCJpc19zdGFmZiI6ZmFsc2UsImlzX2FjdGl2ZSI6dHJ1ZSwiaXNfc3VwZXJ1c2VyIjpmYWxzZSwidmVyaWZpZWQiOnRydWUsInVzZXJfdHlwZSI6ImN1c3RvbWVyIiwidXNlcl9zdGF0dXMiOiJhY3RpdmUiLCJlbWFpbCI6ImlzaGFrLnN3ZUBnbWFpbC5jb20iLCJjb250YWN0IjoiMDE1MjEyMDAwNzkiLCJkYXRlX2pvaW5lZCI6IjIwMTktMDktMDFUMDY6MjE6MDEuNzU4WiIsInRva2VuX3R5cGUiOiJhY2Nlc3MiLCJpc19lYXV0aCI6dHJ1ZSwiZXhwIjoxNjA4NDY4NzU4fQ._JepnYRbDrkCrFX_vkFnkBFXD7SLSwiDs9XQAWw7dlA&context_reference=order_payment");
    }

    public void payViaCard(String invoice, String amount) {

        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", amount);
        payload.put("context", "order_payment");
        payload.put("context_reference", invoice);

        apiRepository.payViaCard(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
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


    public void payViaCityBank(String invoice, String amount) {

        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", amount);
        payload.put("context", "order_payment");
        payload.put("context_reference", invoice);

        apiRepository.payViaCityBank(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                if ((response != null && response.has("url")) && !response.get("url").isJsonNull()) {
                    String purl = response.get("url").getAsString();
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
                    payViaCityBank(invoice, amount);
            }
        });
    }

    public void payViaNagad(String invoice, String amount) {

        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", amount);
        payload.put("context", "order_payment");
        payload.put("context_reference", invoice);
        payload.put("source", "MOBILE_APP");

        apiRepository.payViaNagad(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
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
                    payViaNagad(invoice, amount);

            }
        });

    }


}
