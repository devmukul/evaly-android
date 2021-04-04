package bd.com.evaly.evalyshop.ui.balance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import bd.com.evaly.evalyshop.data.remote.ApiRepository;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.pay.BalanceResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.PaymentApiHelper;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BalanceViewModel extends ViewModel {


    private ApiRepository apiRepository;

    public BalanceViewModel(ApiRepository apiRepository){
        this.apiRepository = apiRepository;
    }

    private MutableLiveData<BalanceResponse> data = new MutableLiveData<>();

    public LiveData<BalanceResponse> getData() {
        return data;
    }

    public void updateBalance() {


        apiRepository.getBalance(CredentialManager.getToken(), CredentialManager.getUserName(), new ResponseListenerAuth<CommonDataResponse<BalanceResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<BalanceResponse> response, int statusCode) {
                data.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void claimCashback() {

        apiRepository.claimCashback(CredentialManager.getToken(), CredentialManager.getUserName(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                updateBalance();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

}
