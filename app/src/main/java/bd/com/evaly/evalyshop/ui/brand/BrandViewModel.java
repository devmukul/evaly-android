package bd.com.evaly.evalyshop.ui.brand;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandCatResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.CategoriesItem;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.BrandApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;

public class BrandViewModel extends ViewModel {

    protected MutableLiveData<List<ProductItem>> liveList = new MutableLiveData<>();
    protected MutableLiveData<BrandCatResponse> detailsLive = new MutableLiveData<>();
    protected MutableLiveData<List<TabsItem>> categoryListLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> onResetLiveData = new MutableLiveData<>();
    private List<ProductItem> arrayList = new ArrayList<>();
    private String slug;
    private String categorySlug = null;
    private int currentPage = 1;
    private boolean isCategoryLoading;
    private List<TabsItem> categoryArrayList = new ArrayList<>();
    private MutableLiveData<TabsItem> selectedCategoryLiveData = new MutableLiveData<>();
    private int categoryCurrentPage = 1;

    @ViewModelInject
    public BrandViewModel(@Assisted SavedStateHandle savedStateHandle) {
        this.slug = savedStateHandle.get("brand_slug");
        if (savedStateHandle.contains("category_slug"))
            this.categorySlug = savedStateHandle.get("category_slug");
        getProducts();
        loadCategories();
    }

    public void clearProductList() {
        arrayList.clear();
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public String getSlug() {
        return slug;
    }

    public LiveData<TabsItem> getSelectedCategoryLiveData() {
        return selectedCategoryLiveData;
    }

    public void setSelectedCategoryLiveData(TabsItem selectedCategoryLiveData) {
        this.selectedCategoryLiveData.setValue(selectedCategoryLiveData);
    }

    public MutableLiveData<Boolean> getOnResetLiveData() {
        return onResetLiveData;
    }

    public void setOnResetLiveData(boolean onResetLiveData) {
        this.onResetLiveData.setValue(onResetLiveData);
    }

    public void getProducts() {

        ProductApiHelper.getCategoryBrandProducts(currentPage, categorySlug, slug, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveList.setValue(arrayList);
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

    public void loadCategories() {
        isCategoryLoading = true;
        BrandApiHelper.getCategories(slug, categoryCurrentPage, new ResponseListenerAuth<CommonDataResponse<BrandCatResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<BrandCatResponse> response, int statusCode) {
                detailsLive.setValue(response.getData());
                isCategoryLoading = false;
                for (CategoriesItem item : response.getData().getCategories()) {
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(item.getName());
                    tabsItem.setImage(item.getImageUrl());
                    tabsItem.setSlug(item.getSlug());
                    categoryArrayList.add(tabsItem);
                }
                categoryListLiveData.setValue(categoryArrayList);
                categoryCurrentPage++;
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
