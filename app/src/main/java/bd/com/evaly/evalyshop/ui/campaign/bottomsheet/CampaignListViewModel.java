package bd.com.evaly.evalyshop.ui.campaign.bottomsheet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.BaseModel;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignProductCategoryResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CampaignListViewModel extends ViewModel {

    private MutableLiveData<List<BaseModel>> liveData = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> hideLoadingBar = new SingleLiveEvent<>();
    private List<BaseModel> arrayList = new ArrayList<>();
    private int currentPage = 1;
    private int totalCount = 0;
    private String search = null;
    private CampaignCategoryResponse category;
    private String type = "categories";
    private ApiRepository apiRepository;

    @Inject
    public CampaignListViewModel(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
        if (category != null)
            loadFromApi();
    }

    public void setType(String type) {
        this.type = type;
    }

    private String getCategorySlug() {
        if (category == null)
            return null;
        return category.getSlug();
    }

    public void loadProductCategories() {
        if (getCategorySlug() == null)
            return;
        apiRepository.getCampaignProductCategories(getCategorySlug(), null, search, currentPage, new ResponseListener<CommonDataResponse<List<CampaignProductCategoryResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignProductCategoryResponse>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveData.setValue(arrayList);
                currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public LiveData<List<BaseModel>> getLiveData() {
        return liveData;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public CampaignCategoryResponse getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public void setCategory(CampaignCategoryResponse category) {
        this.category = category;
    }

    public void loadFromApi() {
        if (type.contains("categories"))
            loadProductCategories();
        else if (type.contains("campaigns"))
            loadCampaignList();
    }

    public void loadCampaignList() {
        apiRepository.getCampaignCategoryCampaigns(currentPage, 20, search, getCategorySlug(), new ResponseListener<CommonDataResponse<List<SubCampaignResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<SubCampaignResponse>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveData.setValue(arrayList);
                currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void clear() {
        currentPage = 1;
        arrayList.clear();
    }

    public SingleLiveEvent<Boolean> getHideLoadingBar() {
        return hideLoadingBar;
    }
}
