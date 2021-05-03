package bd.com.evaly.evalyshop.ui.giftcard.myGiftCard;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GiftCardMyViewModel extends BaseViewModel {

    protected MutableLiveData<List<GiftCardListPurchasedItem>> liveList = new MutableLiveData<>();
    protected SingleLiveEvent<Boolean> redeemLiveData = new SingleLiveEvent<>();
    private int currentPage = 1;
    private String baseUrl = BuildConfig.BASE_URL + "epay-gift-cards/";
    private List<GiftCardListPurchasedItem> arrayList = new ArrayList<>();

    @Inject
    public GiftCardMyViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository, PreferenceRepository preferenceRepository, FirebaseRemoteConfig firebaseRemoteConfig) {
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


        apiRepository.getPurchasedGiftCardList("gifts", currentPage, baseUrl, new ResponseListener<CommonDataResponse<List<GiftCardListPurchasedItem>>, String>() {
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

    public void redeemCard(String invoice_no) {

        apiRepository.redeem(invoice_no, baseUrl, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                ToastUtils.show(response.get("message").getAsString());
                redeemLiveData.setValue(true);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show(errorBody);
                redeemLiveData.setValue(false);
            }

        });
    }


    public int getCurrentPage() {
        return currentPage;
    }
}

