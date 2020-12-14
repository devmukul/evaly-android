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
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.models.campaign.shop.CampaignShopResponse;
import bd.com.evaly.evalyshop.models.campaign.subcampaign.SubCampaignDetailsResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;

public class CampaignDetailsViewModel extends ViewModel {
    private MutableLiveData<CampaignCategoryResponse> campaignDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CampaignParentModel>> liveList = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> hideLoadingBar = new SingleLiveEvent<>();
    protected SingleLiveEvent<Boolean> hideProgressDialog = new SingleLiveEvent<>();
    protected MutableLiveData<SubCampaignResponse> subCampaignLiveData = new MutableLiveData<>();
    private List<CampaignParentModel> arrayList = new ArrayList<>();
    private int currentPage = 1;
    private int totalCount = 0;
    private String search = null;
    private String type = "shop";
    private String campaign = null;

    private SingleLiveEvent<CampaignProductResponse> buyNowClick = new SingleLiveEvent<>();

    public void setBuyNowClick(CampaignProductResponse model) {
        this.buyNowClick.setValue(model);
    }

    public SingleLiveEvent<CampaignProductResponse> getBuyNowClick() {
        return buyNowClick;
    }

    public CampaignDetailsViewModel() {
        loadListFromApi();
    }

    public void loadSubCampaignDetails(String category_slug, String campaign_slug) {
        CampaignApiHelper.getSubCampaignDetails(campaign_slug, new ResponseListenerAuth<CommonDataResponse<SubCampaignDetailsResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<SubCampaignDetailsResponse> response, int statusCode) {
                SubCampaignDetailsResponse data = response.getData();
                SubCampaignResponse model = new SubCampaignResponse();
                model.setName(data.getName());
                model.setImage(data.getImage());
                model.setSlug(data.getSlug());
                model.setCashbackText(data.getCashbackPercentage() + "% Percentage");
                model.setBadgeText(null);

                subCampaignLiveData.setValue(model);
                setCampaign(campaign_slug);
                setCampaignDetailsLiveData(data.getCategory());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void findCampaignCategoryDetails(String slug) {
        CampaignApiHelper.getCampaignCategory(new ResponseListenerAuth<CommonDataResponse<List<CampaignCategoryResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignCategoryResponse>> response, int statusCode) {
                boolean found = false;
                for (CampaignCategoryResponse item : response.getData()) {
                    if (item.getSlug().equals(slug)) {
                        campaignDetailsLiveData.setValue(item);
                        loadListFromApi();
                        found = true;
                        break;
                    }
                }

                if (!found)
                    hideProgressDialog.setValue(true);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show("Please reload the page!");
                hideProgressDialog.setValue(true);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void loadListFromApi() {

        if (campaignDetailsLiveData.getValue() == null)
            return;

        if (currentPage > 1 && totalCount <= arrayList.size()) {
            hideLoadingBar.setValue(true);
            return;
        }

        if (type.equals("product"))
            loadProductList();
        else if (type.equals("shop"))
            loadShopList();
        else if (type.equals("brand"))
            loadBrandList();
        else if (type.equals("campaign"))
            loadCampaignList();
    }

    public void loadCampaignList() {
        CampaignApiHelper.getCampaignCategoryCampaigns(currentPage, 20, search, campaign,
                new ResponseListenerAuth<CommonDataResponse<List<SubCampaignResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<SubCampaignResponse>> response, int statusCode) {
                        arrayList.addAll(response.getData());
                        liveList.setValue(arrayList);
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

    public void loadProductList() {

        CampaignApiHelper.getCampaignCategoryProducts(currentPage, 20, search, getCategorySlug(), campaign,
                new ResponseListenerAuth<CommonDataResponse<List<CampaignProductResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CampaignProductResponse>> response, int statusCode) {
                        arrayList.addAll(response.getData());
                        liveList.setValue(arrayList);
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


    public void loadBrandList() {
        CampaignApiHelper.getCampaignCategoryBrands(currentPage, 20, search, getCategorySlug(), campaign,
                new ResponseListenerAuth<CommonDataResponse<List<CampaignBrandResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CampaignBrandResponse>> response, int statusCode) {
                        arrayList.addAll(response.getData());
                        liveList.setValue(arrayList);
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


    public void loadShopList() {
        CampaignApiHelper.getCampaignCategoryShops(currentPage, 20, search, getCategorySlug(), campaign,
                new ResponseListenerAuth<CommonDataResponse<List<CampaignShopResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CampaignShopResponse>> response, int statusCode) {
                        arrayList.addAll(response.getData());
                        liveList.setValue(arrayList);
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

    public void clear() {
        arrayList.clear();
        currentPage = 1;
        totalCount = 0;
    }

    private String getCategorySlug() {
        if (campaignDetailsLiveData == null || campaignDetailsLiveData.getValue() == null)
            return null;
        return campaignDetailsLiveData.getValue().getSlug();
    }

    public SingleLiveEvent<Boolean> getHideLoadingBar() {
        return hideLoadingBar;
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

    public String getCampaign() {
        return campaign;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCampaignDetailsLiveData(CampaignCategoryResponse model) {
        this.campaignDetailsLiveData.setValue(model);
        if (liveList.getValue() == null) {
            loadListFromApi();
        }
    }

    public void setCampaign(String slug) {
        this.campaign = slug;
    }

    public String getType() {
        return type;
    }

    public void createLiveDataAndLoad() {
        if (campaignDetailsLiveData == null) {
            campaignDetailsLiveData = new MutableLiveData<>();
            loadProductList();
        }
    }
}
