package bd.com.evaly.evalyshop.ui.followedShops;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.shop.FollowResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FollowedShopsViewModel extends BaseViewModel {

    protected List<FollowResponse> arrayList = new ArrayList<>();
    protected MutableLiveData<List<FollowResponse>> liveList = new MutableLiveData<>();
    private int page;
    private String search = null;
    private boolean isLoading;
    private ApiRepository apiRepository;

    @Inject
    public FollowedShopsViewModel(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
        page = 1;
        isLoading = false;
        loadShops();
    }

    public void clearAndLoad() {
        page = 1;
        arrayList.clear();
        loadShops();
    }

    public void loadShops() {
        if (isLoading)
            return;
        isLoading = true;

        apiRepository.getFollowedShop(page, new ResponseListener<CommonDataResponse<JsonObject>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<JsonObject> response, int statusCode) {

                isLoading = false;
                if (response.getData() != null && response.getData().has("shop_list") && !response.getData().get("shop_list").isJsonNull()) {
                    List<FollowResponse> list = new Gson().fromJson(response.getData().get("shop_list").getAsJsonArray(), new TypeToken<List<FollowResponse>>() {
                    }.getType());
                    arrayList.addAll(list);
                }
                liveList.setValue(arrayList);
                page++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }
        });

    }

}
