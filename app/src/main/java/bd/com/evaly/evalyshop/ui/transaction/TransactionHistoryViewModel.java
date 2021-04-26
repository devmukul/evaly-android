package bd.com.evaly.evalyshop.ui.transaction;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigBaseUrls;
import bd.com.evaly.evalyshop.models.transaction.TransactionItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TransactionHistoryViewModel extends BaseViewModel {
    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;
    private FirebaseRemoteConfig remoteConfig;
    private int page;
    protected MutableLiveData<List<TransactionItem>> liveList = new MutableLiveData<>();
    private List<TransactionItem> arrayList = new ArrayList<>();
    protected SingleLiveEvent<Boolean> loadingBar = new SingleLiveEvent<>();
    private boolean isLoading = false;

    @Inject
    public TransactionHistoryViewModel(ApiRepository apiRepository, PreferenceRepository preferenceRepository, FirebaseRemoteConfig remoteConfig) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
        this.remoteConfig = remoteConfig;
        this.page = 1;
        getTransactionHistory();
    }

    public void getTransactionHistory() {
        if (isLoading)
            return;
        loadingBar.setValue(true);
        String url = null;
        RemoteConfigBaseUrls baseUrls = new Gson().fromJson(remoteConfig.getValue("temp_urls").asString(), RemoteConfigBaseUrls.class);

        if (baseUrls == null)
            return;

        if (BuildConfig.DEBUG)
            url = baseUrls.getDevTransectionHistoryUrl();
        else
            url = baseUrls.getProdTransectionHistoryUrl();
        if (url == null)
            return;

        url += preferenceRepository.getUserName();

        apiRepository.getTransactionHistory(preferenceRepository.getToken(), url, page,
                new ResponseListener<CommonDataResponse<List<TransactionItem>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<TransactionItem>> response, int statusCode) {
                        isLoading = false;
                        arrayList.addAll(response.getData());
                        liveList.setValue(arrayList);
                        page++;
                    }

                    @Override
                    public void onFailed(String errorBody, int errorCode) {
                        isLoading = false;
                        ToastUtils.show(errorBody);
                        loadingBar.setValue(false);
                    }

                });
    }

    public int getPage() {
        return page;
    }
}
