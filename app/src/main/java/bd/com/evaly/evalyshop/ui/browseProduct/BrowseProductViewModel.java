package bd.com.evaly.evalyshop.ui.browseProduct;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.BaseModel;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;
import bd.com.evaly.evalyshop.models.catalog.category.ChildCategoryResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopListResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BrowseProductViewModel extends ViewModel {

    protected String categorySlug;
    protected MutableLiveData<List<BaseModel>> liveList = new MutableLiveData<>();
    protected List<BaseModel> arrayList = new ArrayList<>();
    private int tabPosition = -1;
    private String selectedType = "products";
    private int currentPage = 1;
    private ApiRepository apiRepository;

    @Inject
    public BrowseProductViewModel(SavedStateHandle stateHandle, ApiRepository apiRepository) {
        this.categorySlug = stateHandle.get("category_slug");
        this.apiRepository = apiRepository;
        currentPage = 1;
        loadFromApi();
    }

    public int getTabPosition() {
        return tabPosition;
    }

    public void setTabPosition(int tabPosition) {
        this.tabPosition = tabPosition;
    }

    public void reload() {
        arrayList.clear();
        currentPage = 1;
        loadFromApi();
    }

    public void clear() {
        currentPage = 1;
        arrayList.clear();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public String getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(String selectedType) {
        this.selectedType = selectedType;
    }

    public void loadFromApi() {
        switch (selectedType.toLowerCase()) {
            case "products":
                getProducts();
                break;
            case "categories":
                getSubCategories();
                break;
            case "shops":
                getShops();
                break;
            case "brands":
                getBrands();
                break;
        }
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    private void getProducts() {

        apiRepository.getCategoryBrandProducts(currentPage, categorySlug, null, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveList.setValue(arrayList);
                if (response.getData().size() > 10)
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


    public void getSubCategories() {

        apiRepository.getChildCategories(categorySlug, new ResponseListenerAuth<CommonDataResponse<List<ChildCategoryResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ChildCategoryResponse>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveList.setValue(arrayList);
                if (response.getData().size() > 10)
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

    public void getBrands() {
        apiRepository.getBrands(categorySlug, null, currentPage, new ResponseListenerAuth<CommonDataResponse<List<BrandResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<BrandResponse>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveList.setValue(arrayList);
                if (response.getData().size() > 10)
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


    public void getShops() {
        apiRepository.getShops(categorySlug, null, currentPage, null, new ResponseListenerAuth<CommonDataResponse<List<ShopListResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ShopListResponse>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveList.setValue(arrayList);
                if (response.getData().size() > 10)
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


}