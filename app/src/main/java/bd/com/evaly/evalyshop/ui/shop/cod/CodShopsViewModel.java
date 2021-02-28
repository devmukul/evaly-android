package bd.com.evaly.evalyshop.ui.shop.cod;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopListResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CodShopsViewModel extends BaseViewModel {

    private int page;
    private String search = null;
    protected List<ShopListResponse> arrayList = new ArrayList<>();
    protected MutableLiveData<List<ShopListResponse>> liveList = new MutableLiveData<>();

    @Inject
    public CodShopsViewModel(){
        loadCodShops();
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void clearAndLoad(){
        page = 1;
        arrayList.clear();
        loadCodShops();
    }

    public void loadCodShops() {
        ProductApiHelper.getShops(null, search, page, "cod", new ResponseListenerAuth<CommonDataResponse<List<ShopListResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ShopListResponse>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveList.setValue(arrayList);
                page++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

}
