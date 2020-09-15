package bd.com.evaly.evalyshop.ui.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class CategoryViewModel extends ViewModel {

    private SingleLiveEvent<CategoryEntity> selectedCategoryLiveData = new SingleLiveEvent<>();
    private MutableLiveData<List<CategoryEntity>> rootCategoryLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CategoryEntity>> subCategoryLiveData = new MutableLiveData<>();
    private String rootCategory = null;

    public CategoryViewModel() {

    }

    public void loadRootCategories() {
        GeneralApiHelper.getRootCategories(new ResponseListenerAuth<List<CategoryEntity>, String>() {
            @Override
            public void onDataFetched(List<CategoryEntity> response, int statusCode) {
                rootCategoryLiveData.setValue(response);
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

        GeneralApiHelper.getSubCategories(rootCategory, new ResponseListenerAuth<List<CategoryEntity>, String>() {
            @Override
            public void onDataFetched(List<CategoryEntity> response, int statusCode) {
                subCategoryLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void setSelectedCategoryLiveData(CategoryEntity entity) {
        this.selectedCategoryLiveData.setValue(entity);
    }

    public MutableLiveData<List<CategoryEntity>> getRootCategoryLiveData() {
        return rootCategoryLiveData;
    }

    public SingleLiveEvent<CategoryEntity> getSelectedCategoryLiveData() {
        return selectedCategoryLiveData;
    }

    public LiveData<List<CategoryEntity>> getSubCategoryLiveData() {
        return subCategoryLiveData;
    }

    public void setRootCategory(String rootCategory) {
        this.rootCategory = rootCategory;
    }
}
