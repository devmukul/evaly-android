package bd.com.evaly.evalyshop.ui.giftcard.giftCardList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigBaseUrls;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GiftCardListViewModel extends BaseViewModel {

    private int currentPage = 1;
    private String baseUrl = BuildConfig.BASE_URL + "epay-gift-cards/";
    private List<GiftCardListItem> arrayList = new ArrayList<>();
    private MutableLiveData<List<GiftCardListItem>> liveList = new MutableLiveData<>();

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
    }

    public void getGiftCardList() {

        loading = false;

        if (currentPage == 1) {
            progressContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            progressContainer.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        apiRepository.getGiftCard(currentPage, baseUrl, new ResponseListener<CommonDataResponse<List<GiftCardListItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<GiftCardListItem>> response, int statusCode) {

                loading = true;
                progressBar.setVisibility(View.GONE);

                List<GiftCardListItem> list = response.getData();

                itemList.addAll(list);
                adapter.notifyItemRangeChanged(itemList.size() - list.size(), list.size());

                if (currentPage == 1)
                    progressContainer.setVisibility(View.GONE);

                if (list.size() == 0 && currentPage == 1)
                    noItem.setVisibility(View.VISIBLE);

                currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }


    public void getGiftCardDetails(String slug) {

        if (preferenceRepository.getToken().equals("")) {
            Toast.makeText(context, "You need to login first", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.showDialog();
        initializeBottomSheet();


        apiRepository.getGiftCardDetails(slug, baseUrl, new ResponseListener<JsonObject, String>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                dialog.hideDialog();
                if (response.get("success").getAsBoolean()) {

                    Gson gson = new Gson();
                    GiftCardListItem item = gson.fromJson(response.get("data").toString(), GiftCardListItem.class);

                    name.setText(item.getName());
                    details.setText(item.getDescription());
                    voucherAmount = item.getPrice();
                    amount.setText(String.format("৳ %s", Utils.formatPrice(voucherAmount)));
                    total.setText(String.format("৳ %s", Utils.formatPrice(voucherAmount)));
                    cardValue.setText(String.format("৳ %d", item.getValue()));

                    if (getContext() != null) {
                        if (item.getImageUrl() == null)
                            Glide.with(getContext()).load("https://beta.evaly.com.bd/static/images/gift-card.jpg").placeholder(R.drawable.ic_placeholder_small).into(image);
                        else
                            Glide.with(getContext()).load(item.getImageUrl()).placeholder(R.drawable.ic_placeholder_small).into(image);
                    }
                    bottomSheetDialog.show();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


                } else {
                    Toast.makeText(context, "Sorry the gift card is not available", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }
        });
    }


    public void createOrder(String slug) {

        dialog.showDialog();

        JsonObject parameters = new JsonObject();

        parameters.addProperty("to", phoneNumber.getText().toString().trim());
        parameters.addProperty("gift_card", slug);
        int q = Integer.parseInt(quantity.getText().toString());
        parameters.addProperty("quantity", q);

        apiRepository.placeGiftCardOrder(preferenceRepository.getToken(), parameters, baseUrl, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                dialog.hideDialog();
                Toast.makeText(context, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                bottomSheetDialog.hide();
                NavHostFragment.findNavController(GiftCardListFragment.this).popBackStack();
                Bundle bundle = new Bundle();
                bundle.putString("type", "purchased");
                NavHostFragment.findNavController(GiftCardListFragment.this).navigate(R.id.giftCardFragment, bundle);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.hideDialog();
                ToastUtils.show(errorBody);
            }

        });
    }
}
