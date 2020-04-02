package bd.com.evaly.evalyshop.ui.express;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.shop.GroupShopModel;
import bd.com.evaly.evalyshop.rest.apiHelper.ShopApiHelper;

public class EvalyExpressViewModel extends ViewModel {
    private MutableLiveData<List<GroupShopModel>> liveData;
    private int currentPage;
    private int totalCount = 0;
    private boolean loading;
    private String group, area, search;
    private boolean hasNext;

    public EvalyExpressViewModel() {
        super();

        currentPage = 1;
        totalCount = 0;
        group = "evaly-express";
        liveData = new MutableLiveData<>();

        loadShops();
    }

    public LiveData<List<GroupShopModel>> getLiveData() {
        return liveData;
    }

    public void loadShops() {

        loading = true;

        if (CredentialManager.getArea() == null)
            area = "Dhaka";
        else if (CredentialManager.getArea().equals("All"))
            area = null;
        else
            area = CredentialManager.getArea();

        ShopApiHelper.getShopsByGroup(group, currentPage, 24, area, search, new ResponseListenerAuth<CommonDataResponse<List<GroupShopModel>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<GroupShopModel>> response, int statusCode) {
                if (response == null)
                    return;

                loading = false;
                totalCount = response.getCount();


//                liveData.setValue(response.getData());
//                if (totalCount - (24 * currentPage) > 0)
//                    hasNext = true;
//                else
//                    hasNext = false;
//
//                currentPage++;

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

    public void clear() {
        liveData.setValue(null);
        currentPage = 1;
        totalCount = 0;
        search = null;
    }

    public int getTotalCount() {
        return totalCount;
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
}
