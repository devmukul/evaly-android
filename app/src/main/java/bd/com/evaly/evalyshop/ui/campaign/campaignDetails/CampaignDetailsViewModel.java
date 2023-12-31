package bd.com.evaly.evalyshop.ui.campaign.campaignDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignParentModel;
import bd.com.evaly.evalyshop.models.campaign.brand.CampaignBrandResponse;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignProductCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.models.campaign.shop.CampaignShopResponse;
import bd.com.evaly.evalyshop.models.campaign.subcampaign.SubCampaignDetailsResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CampaignDetailsViewModel extends ViewModel {
    public SingleLiveEvent<Void> openFilterModal = new SingleLiveEvent<>();
    protected SingleLiveEvent<Boolean> hideProgressDialog = new SingleLiveEvent<>();
    protected MutableLiveData<SubCampaignResponse> subCampaignLiveData = new MutableLiveData<>();
    protected SingleLiveEvent<String> switchTab = new SingleLiveEvent<>();
    protected List<CampaignProductCategoryResponse> productCategoriesList = new ArrayList<>();
    protected MutableLiveData<List<CampaignProductCategoryResponse>> productCategoriesLiveData = new MutableLiveData<>();
    protected SingleLiveEvent<CampaignProductCategoryResponse> onCategoryChanged = new SingleLiveEvent<>();
    private MutableLiveData<CampaignCategoryResponse> campaignCategoryLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CampaignParentModel>> liveList = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> hideLoadingBar = new SingleLiveEvent<>();
    private List<CampaignParentModel> arrayList = new ArrayList<>();
    private int currentPage = 1;
    private int totalCount = 0;
    private String search = null;
    private String type = "product";
    private String campaign = null;
    private String selectTypeAfterLoading = null;
    private String sort = null;
    private SingleLiveEvent<CampaignProductResponse> buyNowClick = new SingleLiveEvent<>();
    private CampaignProductCategoryResponse selectedCategoryModel;
    private int productCategoryPage = 1;
    private boolean isProductCategoryLoading = false;
    private ApiRepository apiRepository;

    @Inject
    public CampaignDetailsViewModel(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
        loadListFromApi();
    }

    public void setSelectTypeAfterLoading(String selectTypeAfterLoading) {
        this.selectTypeAfterLoading = selectTypeAfterLoading;
    }

    public CampaignProductCategoryResponse getSelectedCategoryModel() {
        return selectedCategoryModel;
    }

    public void setSelectedCategoryModel(CampaignProductCategoryResponse selectedCategoryModel) {
        this.selectedCategoryModel = selectedCategoryModel;
        onCategoryChanged.setValue(selectedCategoryModel);
    }

    public String getSelectedCategorySlug() {
        if (selectedCategoryModel == null)
            return null;
        return selectedCategoryModel.getCategorySlug();
    }

    public String getSelectedCategoryTitle() {
        if (selectedCategoryModel == null)
            return null;
        return selectedCategoryModel.getCategoryName();
    }

    public SingleLiveEvent<CampaignProductResponse> getBuyNowClick() {
        return buyNowClick;
    }

    public void setBuyNowClick(CampaignProductResponse model) {
        this.buyNowClick.setValue(model);
    }

    public void loadProductCategories() {
        if (isProductCategoryLoading || getCategorySlug() == null)
            return;
        isProductCategoryLoading = true;

        apiRepository.getCampaignProductCategories(getCategorySlug(), campaign, null, productCategoryPage, new ResponseListener<CommonDataResponse<List<CampaignProductCategoryResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignProductCategoryResponse>> response, int statusCode) {
                productCategoriesList.addAll(response.getData());
                productCategoriesLiveData.setValue(productCategoriesList);
                productCategoryPage++;
                isProductCategoryLoading = false;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void loadSubCampaignDetails(String campaign_slug) {
        apiRepository.getSubCampaignDetails(campaign_slug, new ResponseListener<CommonDataResponse<SubCampaignDetailsResponse>, String>() {
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
                setCampaignCategoryLiveData(data.getCategory());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                Logger.d(errorBody);
                subCampaignLiveData.setValue(null);
            }

        });
    }

    public void findCampaignCategoryDetails(String slug) {
        apiRepository.getCampaignCategory(new ResponseListener<CommonDataResponse<List<CampaignCategoryResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignCategoryResponse>> response, int statusCode) {
                boolean found = false;
                for (CampaignCategoryResponse item : response.getData()) {
                    if (item.getSlug().equals(slug)) {
                        campaignCategoryLiveData.setValue(item);
                        if (selectTypeAfterLoading == null)
                            loadListFromApi();
                        else {
                            switchTab.setValue(selectTypeAfterLoading);
                            selectTypeAfterLoading = null;
                        }
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

        });
    }

    public void loadListFromApi() {
        if (campaignCategoryLiveData.getValue() == null)
            return;

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
        apiRepository.getCampaignCategoryCampaigns(currentPage, 20, search, campaign,
                new ResponseListener<CommonDataResponse<List<SubCampaignResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<SubCampaignResponse>> response, int statusCode) {
                        arrayList.addAll(response.getData());
                        liveList.setValue(arrayList);
                        currentPage++;
                    }

                    @Override
                    public void onFailed(String errorBody, int errorCode) {

                    }

                });
    }

    public void loadProductList() {
        apiRepository.getCampaignCategoryProducts(currentPage, 20, search, getCategorySlug(), campaign, getSelectedCategorySlug(), sort,
                new ResponseListener<CommonDataResponse<List<CampaignProductResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CampaignProductResponse>> response, int statusCode) {
                        arrayList.addAll(response.getData());
                        liveList.setValue(arrayList);
                        currentPage++;
                    }

                    @Override
                    public void onFailed(String errorBody, int errorCode) {
                        hideLoadingBar.setValue(true);
                    }

                });
    }

    public void loadBrandList() {
        apiRepository.getCampaignCategoryBrands(currentPage, 20, search, getCategorySlug(), campaign,
                new ResponseListener<CommonDataResponse<List<CampaignBrandResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CampaignBrandResponse>> response, int statusCode) {
                        arrayList.addAll(response.getData());
                        liveList.setValue(arrayList);
                        currentPage++;
                    }

                    @Override
                    public void onFailed(String errorBody, int errorCode) {
                        hideLoadingBar.setValue(true);
                    }

                });
    }

    public void loadShopList() {
        apiRepository.getCampaignCategoryShops(currentPage, 20, search, getCategorySlug(), campaign,
                new ResponseListener<CommonDataResponse<List<CampaignShopResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CampaignShopResponse>> response, int statusCode) {
                        arrayList.addAll(response.getData());
                        liveList.setValue(arrayList);
                        currentPage++;
                    }

                    @Override
                    public void onFailed(String errorBody, int errorCode) {
                        hideLoadingBar.setValue(true);
                    }

                });
    }

    public void clear() {
        arrayList.clear();
        currentPage = 1;
        totalCount = 0;
    }

    public boolean isProductCategoryLoading() {
        return isProductCategoryLoading;
    }

    private String getCategorySlug() {
        if (campaignCategoryLiveData == null || campaignCategoryLiveData.getValue() == null)
            return null;
        return campaignCategoryLiveData.getValue().getSlug();
    }

    public SingleLiveEvent<Boolean> getHideLoadingBar() {
        return hideLoadingBar;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public MutableLiveData<List<CampaignParentModel>> getLiveList() {
        return liveList;
    }

    public LiveData<CampaignCategoryResponse> getCampaignCategoryLiveData() {
        return campaignCategoryLiveData;
    }

    public void setCampaignCategoryLiveData(CampaignCategoryResponse model) {
        this.campaignCategoryLiveData.setValue(model);
        if (liveList.getValue() == null) {
            if (selectTypeAfterLoading == null)
                loadListFromApi();
            else {
                switchTab.setValue(selectTypeAfterLoading);
                selectTypeAfterLoading = null;
            }
        }
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String slug) {
        this.campaign = slug;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void createLiveDataAndLoad() {
        if (campaignCategoryLiveData == null) {
            campaignCategoryLiveData = new MutableLiveData<>();
            loadProductList();
        }
    }
}
