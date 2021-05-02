package bd.com.evaly.evalyshop.ui.giftcard.giftCardList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigBaseUrls;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GiftCardListViewModel extends BaseViewModel {

    protected MutableLiveData<List<GiftCardListItem>> liveList = new MutableLiveData<>();
    private int currentPage = 1;
    private String baseUrl = BuildConfig.BASE_URL + "epay-gift-cards/";
    private List<GiftCardListItem> arrayList = new ArrayList<>();

    @Inject
    public GiftCardListViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository, PreferenceRepository preferenceRepository, FirebaseRemoteConfig firebaseRemoteConfig) {
        super(savedStateHandle, apiRepository, preferenceRepository);

        RemoteConfigBaseUrls baseUrls = new Gson().fromJson(firebaseRemoteConfig.getValue("temp_urls").asString(), RemoteConfigBaseUrls.class);
        String url = null;
        if (baseUrls != null) {
            if (BuildConfig.DEBUG)
                url = baseUrls.getDevGiftCardBaseUrl();
            else
                url = baseUrls.getProdGiftCardBaseUrl();
        }

        if (url != null)
            baseUrl = url;

        getGiftCardList();
    }

    public void getGiftCardList() {

        apiRepository.getGiftCard(currentPage, baseUrl, new ResponseListener<CommonDataResponse<List<GiftCardListItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<GiftCardListItem>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveList.setValue(arrayList);
                currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }

    public int getCurrentPage() {
        return currentPage;
    }
}
