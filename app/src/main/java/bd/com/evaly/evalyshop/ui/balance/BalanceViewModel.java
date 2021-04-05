package bd.com.evaly.evalyshop.ui.balance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.pay.BalanceResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BalanceViewModel extends ViewModel {


    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;

    @Inject
    public BalanceViewModel(ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
    }

    private MutableLiveData<BalanceResponse> data = new MutableLiveData<>();

    public LiveData<BalanceResponse> getData() {
        return data;
    }

    public void updateBalance() {


        apiRepository.getBalance(preferenceRepository.getToken(), preferenceRepository.getUserName(), new ResponseListenerAuth<CommonDataResponse<BalanceResponse>, String>() {
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

        apiRepository.claimCashback(preferenceRepository.getToken(), preferenceRepository.getUserName(), new ResponseListenerAuth<JsonObject, String>() {
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
