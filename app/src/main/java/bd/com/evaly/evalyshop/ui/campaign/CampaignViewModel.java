package bd.com.evaly.evalyshop.ui.campaign;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;

public class CampaignViewModel extends ViewModel {

    private CampaignNavigator navigator;
    private int currentPage = 1;
    private MutableLiveData<List<CampaignItem>> liveList = new MutableLiveData<>();
    private List<CampaignItem> list = new ArrayList<>();
    private String search = null;

    public CampaignViewModel() {
        currentPage = 1;
        loadCampaigns();
    }

    public void setNavigator(CampaignNavigator navigator) {
        this.navigator = navigator;
    }

    public void loadCampaigns() {

        CampaignApiHelper.getCampaigns(currentPage, search, new ResponseListenerAuth<CommonDataResponse<List<CampaignItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignItem>> response, int statusCode) {
                list.addAll(response.getData());
                liveList.setValue(list);
                if (response.getCount() > list.size())
                    currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                navigator.onListFailed(errorBody, errorCode);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    public void clear() {
        list.clear();
        currentPage = 1;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }


    public LiveData<List<CampaignItem>> getLiveList() {
        return liveList;
    }

}
