package bd.com.evaly.evalyshop.ui.express;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.remote.ApiRepository;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.express.ExpressServiceDetailsModel;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.shop.GroupShopModel;
import bd.com.evaly.evalyshop.rest.ApiClient;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EvalyExpressViewModel extends ViewModel {

    private ApiRepository apiRepository;
    private MutableLiveData<List<GroupShopModel>> liveData;
    private MutableLiveData<ExpressServiceDetailsModel> expressDetails;
    private int currentPage;
    private int totalCount = 0;
    private boolean loading;
    private String serviceSlug, area, addressSearch, shopSearch;
    private boolean hasNext;
    private boolean shouldClear = true;

    @Inject
    public EvalyExpressViewModel(SavedStateHandle arg, ApiRepository apiRepository) {
        if (arg.contains("model"))
            serviceSlug = ((ExpressServiceModel)arg.get("model")).getSlug();
        else if (arg.contains("slug"))
            serviceSlug = arg.get("slug");
        else
            serviceSlug = "";
        this.apiRepository = apiRepository;
        this.serviceSlug = serviceSlug;
        currentPage = 1;
        totalCount = 0;
        liveData = new MutableLiveData<>();
        expressDetails = new MutableLiveData<>();
        loadShops();
        loadServiceDetails();
    }


    public LiveData<List<GroupShopModel>> getLiveData() {
        if (liveData == null)
            liveData = new MutableLiveData<>();

        return liveData;
    }

    public void loadShops() {
        loading = true;

        Double longitude = null;
        Double latitude = null;

        if (CredentialManager.getArea() == null)
            area = null;
        else if (CredentialManager.getArea().contains("Districts"))
            area = null;
        else if (CredentialManager.getArea().toLowerCase().contains("near")) {
            area = null;

            if (CredentialManager.getLatitude() != null)
                latitude = Double.parseDouble(CredentialManager.getLatitude());
            if (CredentialManager.getLongitude() != null)

                longitude = Double.parseDouble(CredentialManager.getLongitude());
        } else
            area = CredentialManager.getArea();

        apiRepository.getShopList(serviceSlug, currentPage, 24, area, shopSearch, null, longitude, latitude, new ResponseListenerAuth<CommonResultResponse<List<GroupShopModel>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<GroupShopModel>> response, int statusCode) {

                if (response == null) return;
                loading = false;
                hasNext = true;

                if (liveData.getValue() != null && !shouldClear) {
                    List<GroupShopModel> oldList = liveData.getValue();
                    List<GroupShopModel> newList = response.getData();
                    for (GroupShopModel model : newList) {
                        if (!oldList.contains(model))
                            oldList.add(model);
                    }
                    liveData.setValue(oldList);

                } else {
                    liveData.setValue(response.getData());
                }

                currentPage++;
                shouldClear = false;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                loading = false;
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void loadServiceDetails() {

        apiRepository.getServiceDetails(serviceSlug, new ResponseListenerAuth<CommonDataResponse<ExpressServiceDetailsModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ExpressServiceDetailsModel> response, int statusCode) {
                expressDetails.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    public void clear() {
        shouldClear = true;
        currentPage = 1;
        totalCount = 0;
        hasNext = false;
        shopSearch = null;
        ApiClient.getUnsafeOkHttpClient().dispatcher().cancelAll();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setShopSearch(String search) {
        this.shopSearch = search;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public LiveData<ExpressServiceDetailsModel> getExpressDetails() {
        return expressDetails;
    }
}
