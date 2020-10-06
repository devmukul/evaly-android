package bd.com.evaly.evalyshop.ui.campaign.bottomsheet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class CampaignListViewModel extends ViewModel {

    private MutableLiveData<List<SubCampaignResponse>> liveData = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> hideLoadingBar = new SingleLiveEvent<>();
    private List<SubCampaignResponse> arrayList = new ArrayList<>();
    private int currentPage = 1;
    private int totalCount = 0;
    private String search = null;
    private String category = null;

    public CampaignListViewModel() {
        if (category != null)
            loadFromApi();
    }

    public LiveData<List<SubCampaignResponse>> getLiveData() {
        return liveData;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void loadFromApi() {
        CampaignApiHelper.getCampaignCategoryCampaigns(currentPage, 20, search, category, new ResponseListenerAuth<CommonDataResponse<List<SubCampaignResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<SubCampaignResponse>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveData.setValue(arrayList);
                totalCount = response.getCount();
                if (totalCount > arrayList.size())
                    currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    public SingleLiveEvent<Boolean> getHideLoadingBar() {
        return hideLoadingBar;
    }
}
