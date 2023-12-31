package bd.com.evaly.evalyshop.ui.shop.cod;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopListResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CodShopsViewModel extends BaseViewModel {

    private int page;
    private String search = null;
    protected List<ShopListResponse> arrayList = new ArrayList<>();
    protected MutableLiveData<List<ShopListResponse>> liveList = new MutableLiveData<>();
    private boolean isLoading;
    private ApiRepository apiRepository;

    @Inject
    public CodShopsViewModel(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
        page = 1;
        isLoading = false;
        loadCodShops();
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void clearAndLoad() {
        page = 1;
        arrayList.clear();
        loadCodShops();
    }

    public void loadCodShops() {
        if (isLoading)
            return;
        isLoading = true;
        apiRepository.getShops(null, search, page, "cod", new ResponseListener<CommonDataResponse<List<ShopListResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ShopListResponse>> response, int statusCode) {
                isLoading = false;
                arrayList.addAll(response.getData());
                liveList.setValue(arrayList);
                page++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

}
