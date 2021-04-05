package bd.com.evaly.evalyshop.ui.payment.giftcard;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GiftCardPaymentViewModel extends ViewModel {

    public SingleLiveEvent<CommonDataResponse> onPaymentSuccess = new SingleLiveEvent<>();
    public SingleLiveEvent<String> onPaymentFailed = new SingleLiveEvent<>();
    private ApiRepository apiRepository;

    @Inject
    public GiftCardPaymentViewModel(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    public void makePaymentViaGiftCard(String giftCode, String invoice, String amount) {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("invoice_no", invoice);
        payload.put("gift_code", giftCode);
        payload.put("amount", amount);
        apiRepository.payWithGiftCard(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                CommonDataResponse data = new Gson().fromJson(response, CommonDataResponse.class);
                onPaymentSuccess.setValue(data);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                try {
                    CommonDataResponse data = new Gson().fromJson(errorBody, CommonDataResponse.class);
                    onPaymentFailed.setValue(data.getMessage());
                } catch (Exception e) {
                    onPaymentFailed.setValue("Payment unsuccessful");
                }
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    makePaymentViaGiftCard(giftCode, invoice, amount);
            }
        });
    }
}
