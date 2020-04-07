package bd.com.evaly.evalyshop.ui.express;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.express.ExpressServiceDetailsModel;
import bd.com.evaly.evalyshop.models.shop.GroupShopModel;
import bd.com.evaly.evalyshop.rest.apiHelper.ExpressApiHelper;

public class EvalyExpressViewModel extends ViewModel {
    private MutableLiveData<List<GroupShopModel>> liveData;
    private MutableLiveData<ExpressServiceDetailsModel> expressDetails;
    private int currentPage;
    private int totalCount = 0;
    private boolean loading;
    private String serviceSlug, area, addressSearch, shopSearch;
    private boolean hasNext;

    public EvalyExpressViewModel(String serviceSlug) {
        super();
        this.serviceSlug = serviceSlug;
        currentPage = 1;
        totalCount = 0;
        liveData = new MutableLiveData<>();

        loadShops();
        loadServiceDetails();
    }


    public LiveData<List<GroupShopModel>> getLiveData() {
        return liveData;
    }

    public void loadShops() {

        loading = true;

        if (CredentialManager.getArea() == null)
            area = null;
        else if (CredentialManager.getArea().contains("Districts"))
            area = null;
        else
            area = CredentialManager.getArea();

        ExpressApiHelper.getShopList(serviceSlug, currentPage, 24, area, addressSearch, shopSearch, null, null, new ResponseListenerAuth<CommonResultResponse<List<GroupShopModel>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<GroupShopModel>> response, int statusCode) {
                if (response == null)
                    return;

                loading = false;
                totalCount = response.getCount();

                if (totalCount - (24 * currentPage) > 0)
                    hasNext = true;
                else
                    hasNext = false;

                if (liveData.getValue() != null) {

                    List<GroupShopModel> oldList = liveData.getValue();
                    oldList.addAll(response.getData());
                    liveData.setValue(oldList);
                    currentPage++;

                } else {
                    liveData.setValue(response.getData());

                    currentPage++;
                }
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

        ExpressApiHelper.getServiceDetails(serviceSlug, new ResponseListenerAuth<ExpressServiceDetailsModel, String>() {
            @Override
            public void onDataFetched(ExpressServiceDetailsModel response, int statusCode) {
                expressDetails.setValue(response);
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
        liveData.setValue(null);
        currentPage = 1;
        totalCount = 0;
        hasNext = true;
        search = null;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public LiveData<ExpressServiceDetailsModel> getExpressDetails() {
        return expressDetails;
    }
}
