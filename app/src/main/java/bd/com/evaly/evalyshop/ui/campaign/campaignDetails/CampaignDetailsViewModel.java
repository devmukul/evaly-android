package bd.com.evaly.evalyshop.ui.campaign.campaignDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignParentModel;
import bd.com.evaly.evalyshop.models.campaign.brand.CampaignBrandResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.models.campaign.shop.CampaignShopResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;

public class CampaignDetailsViewModel extends ViewModel {
    private MutableLiveData<CampaignCategoryResponse> campaignDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CampaignParentModel>> liveList = new MutableLiveData<>();
    private List<CampaignParentModel> arrayList = new ArrayList<>();
    private int currentPage = 1;
    private String search = null;
    private String type = "product";


    public CampaignDetailsViewModel() {
        loadListFromApi();
    }

    public void loadListFromApi() {
        if (campaignDetailsLiveData.getValue() == null)
            return;

        if (type.equals("product"))
            loadProductList();
        else if (type.equals("shop"))
            loadShopList();
        else if (type.equals("brand"))
            loadBrandList();
    }

    public void loadProductList() {
        CampaignApiHelper.getCampaignCategoryProducts(currentPage, 20, search, campaignDetailsLiveData.getValue().getSlug(),
                new ResponseListenerAuth<CommonDataResponse<List<CampaignProductResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CampaignProductResponse>> response, int statusCode) {
                        arrayList.addAll(response.getData());
                        liveList.setValue(arrayList);
                        if (response.getCount() > arrayList.size())
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

    public void loadBrandList() {
        CampaignApiHelper.getCampaignCategoryBrands(currentPage, 20, search, campaignDetailsLiveData.getValue().getSlug(),
                new ResponseListenerAuth<CommonDataResponse<List<CampaignBrandResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CampaignBrandResponse>> response, int statusCode) {
                        arrayList.addAll(response.getData());
                        liveList.setValue(arrayList);
                        if (response.getCount() > arrayList.size())
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


    public void loadShopList() {
        CampaignApiHelper.getCampaignCategoryShops(currentPage, 20, search, campaignDetailsLiveData.getValue().getSlug(),
                new ResponseListenerAuth<CommonDataResponse<List<CampaignShopResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CampaignShopResponse>> response, int statusCode) {
                        arrayList.addAll(response.getData());
                        liveList.setValue(arrayList);
                        if (response.getCount() > arrayList.size())
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

    public void clear(){
        arrayList.clear();
        currentPage = 1;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MutableLiveData<List<CampaignParentModel>> getLiveList() {
        return liveList;
    }

    public LiveData<CampaignCategoryResponse> getCampaignDetailsLiveData() {
        return campaignDetailsLiveData;
    }

    public void setCampaignDetailsLiveData(CampaignCategoryResponse model) {
        this.campaignDetailsLiveData.setValue(model);
        if (liveList.getValue() == null) {
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
