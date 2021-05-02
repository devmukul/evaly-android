package bd.com.evaly.evalyshop.ui.giftcard.giftCardList.details;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigBaseUrls;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GiftCardDetailsViewModel extends BaseViewModel {

    SingleLiveEvent<Boolean> onResponse = new SingleLiveEvent<>();
    MutableLiveData<CommonDataResponse<GiftCardListItem>> giftCardDetailsLiveData = new MutableLiveData<>();
    String baseUrl = BuildConfig.BASE_URL + "epay-gift-cards/";
    String slug;


    @Inject
    public GiftCardDetailsViewModel(SavedStateHandle savedStateHandle,
                                    ApiRepository apiRepository,
                                    PreferenceRepository preferenceRepository,
                                    FirebaseRemoteConfig firebaseRemoteConfig) {
        super(savedStateHandle, apiRepository, preferenceRepository);
        this.slug = savedStateHandle.get("slug");

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

        getGiftCardDetails();

    }

    public void getGiftCardDetails() {

        apiRepository.getGiftCardDetails(slug, baseUrl, new ResponseListener<CommonDataResponse<GiftCardListItem>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<GiftCardListItem> response, int statusCode) {
                giftCardDetailsLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }
        });

    }


    public void createOrder(String phoneNumber, String quantity) {
        JsonObject parameters = new JsonObject();

        parameters.addProperty("to", phoneNumber);
        parameters.addProperty("gift_card", slug);
        int q = Integer.parseInt(quantity);
        parameters.addProperty("quantity", q);

        apiRepository.placeGiftCardOrder(preferenceRepository.getToken(), parameters, baseUrl, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                ToastUtils.show(response.get("message").getAsString());
                onResponse.setValue(true);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                onResponse.setValue(false);
                ToastUtils.show(errorBody);
            }

        });
    }
}
