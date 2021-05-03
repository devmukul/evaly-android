package bd.com.evaly.evalyshop.ui.giftcard.purchasedGiftCard;

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
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigBaseUrls;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GiftCardPurchasedViewModel extends BaseViewModel {

    protected MutableLiveData<List<GiftCardListPurchasedItem>> liveList = new MutableLiveData<>();
    protected SingleLiveEvent<Boolean> redeemLiveData = new SingleLiveEvent<>();
    private int currentPage = 1;
    private String baseUrl = BuildConfig.BASE_URL + "epay-gift-cards/";
    private List<GiftCardListPurchasedItem> arrayList = new ArrayList<>();

    @Inject
    public GiftCardPurchasedViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository, PreferenceRepository preferenceRepository, FirebaseRemoteConfig firebaseRemoteConfig) {
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

    public void clear(){
        arrayList.clear();
        currentPage = 1;
    }

    public void getGiftCardList() {


        apiRepository.getPurchasedGiftCardList("purchased", currentPage, baseUrl, new ResponseListener<CommonDataResponse<List<GiftCardListPurchasedItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<GiftCardListPurchasedItem>> response, int statusCode) {
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

