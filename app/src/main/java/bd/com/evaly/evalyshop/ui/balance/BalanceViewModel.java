package bd.com.evaly.evalyshop.ui.balance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.pay.BalanceResponse;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigBaseUrls;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BalanceViewModel extends ViewModel {

    private MutableLiveData<BalanceResponse> data = new MutableLiveData<>();
    private FirebaseRemoteConfig remoteConfigLiveData;
    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;

    @Inject
    public BalanceViewModel(ApiRepository apiRepository, PreferenceRepository preferenceRepository, FirebaseRemoteConfig remoteConfigLiveData) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
        this.remoteConfigLiveData = remoteConfigLiveData;
    }


    public LiveData<BalanceResponse> getData() {
        return data;
    }

    public void updateBalance() {

        String url = null;

        RemoteConfigBaseUrls baseUrls = new Gson().fromJson(remoteConfigLiveData.getValue("temp_urls").asString(), RemoteConfigBaseUrls.class);
        if (BuildConfig.DEBUG)
            url = baseUrls.getDevBalanceUrl();
        else
            url = baseUrls.getProdBalanceUrl();

        if (url == null)
            return;

        apiRepository.getBalance(preferenceRepository.getToken(), preferenceRepository.getUserName(), url, new ResponseListener<CommonDataResponse<BalanceResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<BalanceResponse> response, int statusCode) {
                data.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void claimCashback() {
        String url = null;
        RemoteConfigBaseUrls baseUrls = new Gson().fromJson(remoteConfigLiveData.getValue("temp_urls").asString(), RemoteConfigBaseUrls.class);
        if (BuildConfig.DEBUG)
            url = baseUrls.getDevCashbackClaimUrl();
        else
            url = baseUrls.getProdCashbackClaimUrl();
        if (url == null)
            return;

        apiRepository.claimCashback(preferenceRepository.getToken(), preferenceRepository.getUserName(), url, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                updateBalance();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

}
