package bd.com.evaly.evalyshop.ui.browseProduct;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;

public class BrowseProductViewModel extends ViewModel {

    private int tabPosition = -1;

    public int getTabPosition() {
        return tabPosition;
    }

    public void setTabPosition(int tabPosition) {
        this.tabPosition = tabPosition;
    }

    protected String categorySlug;
    protected MutableLiveData<JsonObject> responseLiveData = new MutableLiveData<>();
    protected MutableLiveData<JsonArray> responseArrayLiveData = new MutableLiveData<>();
    protected MutableLiveData<List<ProductItem>> productListLiveData = new MutableLiveData<>();
    private String selectedType = "product";
    private int currentPage = 1;

    @ViewModelInject
    public BrowseProductViewModel(@Assisted SavedStateHandle stateHandle) {
        this.categorySlug = stateHandle.get("category_slug");
        loadFromApi();
    }

    public void setSelectedType(String selectedType) {
        this.selectedType = selectedType;
    }

    public void loadFromApi() {
        currentPage = 1;
        switch (selectedType) {
            case "product":
                getProducts();
                break;
            case "category":
                getSubCategories();
                break;
            case "shop":
                getShopsOfCategory();
                break;
            case "brand":
                getBrandsOfCategory();
                break;
        }
    }

    private void getProducts() {


        ProductApiHelper.getCategoryBrandProducts(currentPage, categorySlug, null, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {
                productListLiveData.setValue(response.getData());
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

        ProductApiHelper.getSubCategories(categorySlug, new ResponseListenerAuth<JsonArray, String>() {

            @Override
            public void onDataFetched(JsonArray res, int statusCode) {
                responseArrayLiveData.setValue(res);
            }

            @Override
            public void onFailed(String body, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });


    }

    public void getBrandsOfCategory() {
        ProductApiHelper.getBrandsOfCategories(categorySlug, currentPage, 20, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject res, int statusCode) {
                responseLiveData.setValue(res);

            }

            @Override
            public void onFailed(String body, int errorCode) {


            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    public void getShopsOfCategory() {
        ProductApiHelper.getShopsOfCategories(categorySlug, currentPage, 20, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject res, int statusCode) {
                responseLiveData.setValue(res);
            }

            @Override
            public void onFailed(String body, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


}