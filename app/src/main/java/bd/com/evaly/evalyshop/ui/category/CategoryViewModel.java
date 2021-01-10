package bd.com.evaly.evalyshop.ui.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class CategoryViewModel extends ViewModel {

    private SingleLiveEvent<CategoryEntity> selectedCategoryLiveData = new SingleLiveEvent<>();
    private MutableLiveData<List<CategoryEntity>> rootCategoryLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CategoryEntity>> subCategoryLiveData = new MutableLiveData<>();
    private String rootCategory = null;

    public CategoryViewModel() {
        loadRootCategories();
        if (rootCategory == null)
            loadTopCategories();
        else
            loadSubCategories();
    }

    public String getRootCategory() {
        return rootCategory;
    }

    public void setRootCategory(String rootCategory) {
        this.rootCategory = rootCategory;
    }

    public void loadRootCategories() {
        GeneralApiHelper.getRootCategories(new ResponseListenerAuth<CommonDataResponse<List<CategoryEntity>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CategoryEntity>> response, int statusCode) {

                rootCategoryLiveData.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void loadSubCategories() {

        GeneralApiHelper.getSubCategories(rootCategory, new ResponseListenerAuth<CommonDataResponse<List<CategoryEntity>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CategoryEntity>> response, int statusCode) {
                subCategoryLiveData.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void loadTopCategories() {
        GeneralApiHelper.getTopCategories(new ResponseListenerAuth<CommonDataResponse<List<CategoryEntity>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CategoryEntity>> response, int statusCode) {
                subCategoryLiveData.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public MutableLiveData<List<CategoryEntity>> getRootCategoryLiveData() {
        return rootCategoryLiveData;
    }

    public SingleLiveEvent<CategoryEntity> getSelectedCategoryLiveData() {
        return selectedCategoryLiveData;
    }

    public void setSelectedCategoryLiveData(CategoryEntity entity) {
        this.selectedCategoryLiveData.setValue(entity);
    }

    public LiveData<List<CategoryEntity>> getSubCategoryLiveData() {
        return subCategoryLiveData;
    }
}
