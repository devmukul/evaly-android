package bd.com.evaly.evalyshop.ui.giftcard;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.pay.BalanceResponse;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigBaseUrls;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GiftCardViewModel extends BaseViewModel {

    private FirebaseRemoteConfig firebaseRemoteConfig;
    protected MutableLiveData<BalanceResponse> balanceLiveData = new MutableLiveData<>();

    @Inject
    public GiftCardViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository, PreferenceRepository preferenceRepository, FirebaseRemoteConfig firebaseRemoteConfig) {
        super(savedStateHandle, apiRepository, preferenceRepository);
        this.firebaseRemoteConfig = firebaseRemoteConfig;
        updateBalance();
    }

    public void updateBalance() {
        String url = null;

        RemoteConfigBaseUrls baseUrls = new Gson().fromJson(firebaseRemoteConfig.getValue("temp_urls").asString(), RemoteConfigBaseUrls.class);

        if (baseUrls == null)
            return;

        if (BuildConfig.DEBUG)
            url = baseUrls.getDevBalanceUrl();
        else
            url = baseUrls.getProdBalanceUrl();

        if (url == null)
            return;

        apiRepository.getBalance(preferenceRepository.getToken(), preferenceRepository.getUserName(), url, new ResponseListener<CommonDataResponse<BalanceResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<BalanceResponse> response, int statusCode) {
                balanceLiveData.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }


}
