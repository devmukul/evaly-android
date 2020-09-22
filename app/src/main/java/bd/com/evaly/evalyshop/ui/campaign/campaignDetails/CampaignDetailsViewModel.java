package bd.com.evaly.evalyshop.ui.campaign.campaignDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;

public class CampaignDetailsViewModel extends ViewModel {
    private MutableLiveData<CampaignCategoryResponse> campaignDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CampaignProductResponse>> productLiveList = new MutableLiveData<>();
    private List<CampaignProductResponse> productArrayList = new ArrayList<>();
    private int currentPage = 1;
    private String search = null;


    public CampaignDetailsViewModel() {
        loadProductList();
    }

    public void loadProductList() {
        if (campaignDetailsLiveData.getValue() == null)
            return;

        CampaignApiHelper.getCampaignCategoryProducts(currentPage, 20, search, campaignDetailsLiveData.getValue().getSlug(), new ResponseListenerAuth<CommonDataResponse<List<CampaignProductResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignProductResponse>> response, int statusCode) {
                productArrayList.addAll(response.getData());
                productLiveList.setValue(productArrayList);
                if (response.getCount() > productArrayList.size())
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

    public MutableLiveData<List<CampaignProductResponse>> getProductLiveList() {
        return productLiveList;
    }

    public LiveData<CampaignCategoryResponse> getCampaignDetailsLiveData() {
        return campaignDetailsLiveData;
    }

    public void setCampaignDetailsLiveData(CampaignCategoryResponse model) {
        this.campaignDetailsLiveData.setValue(model);
        if (productLiveList.getValue() == null) {
            loadProductList();
        }
    }

    public void createLiveDataAndLoad() {
        if (campaignDetailsLiveData == null) {
            campaignDetailsLiveData = new MutableLiveData<>();
            loadProductList();
        }
    }
}
