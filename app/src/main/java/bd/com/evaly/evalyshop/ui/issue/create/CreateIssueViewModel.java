package bd.com.evaly.evalyshop.ui.issue.create;

import android.graphics.Bitmap;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.models.issueNew.category.IssueCategoryModel;
import bd.com.evaly.evalyshop.models.issueNew.create.IssueCreateBody;
import bd.com.evaly.evalyshop.models.issueNew.list.IssueListModel;
import bd.com.evaly.evalyshop.rest.apiHelper.ImageApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.IssueApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;

public class CreateIssueViewModel extends ViewModel {

    protected MutableLiveData<List<IssueCategoryModel>> categoryLiveList = new MutableLiveData<>();
    protected SingleLiveEvent<Boolean> issueCreatedLiveData = new SingleLiveEvent<>();
    protected MutableLiveData<ImageDataModel> imageLiveData = new SingleLiveEvent<>();
    protected SingleLiveEvent<String> imageErrorLiveData = new SingleLiveEvent<>();
    protected String orderStatus;
    @ViewModelInject
    public CreateIssueViewModel(@Assisted SavedStateHandle savedStateHandle) {
        this.orderStatus = savedStateHandle.get("orderStatus");
        loadCategories();
    }

    public void submitIssue(IssueCreateBody model) {

        IssueApiHelper.createIssue(model, new ResponseListenerAuth<CommonDataResponse<IssueListModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<IssueListModel> response, int statusCode) {
                issueCreatedLiveData.setValue(true);
                ToastUtils.show("Your issue has been submitted, you will be notified shortly");
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                issueCreatedLiveData.setValue(false);
                try {
                    CommonDataResponse data = new Gson().fromJson(errorBody, CommonDataResponse.class);
                    ToastUtils.show(Utils.capitalize(data.getMessage()));
                } catch (Exception e) {
                    ToastUtils.show(errorBody);
                }
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    submitIssue(model);
            }
        });
    }

    public void loadCategories() {

        IssueApiHelper.getCategories(orderStatus, new ResponseListenerAuth<CommonDataResponse<List<IssueCategoryModel>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<IssueCategoryModel>> response, int statusCode) {
                categoryLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    public void uploadImage(Bitmap bitmap) {
        ImageApiHelper.uploadImage(bitmap, new ResponseListenerAuth<CommonDataResponse<ImageDataModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ImageDataModel> response, int statusCode) {
                imageLiveData.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                imageErrorLiveData.setValue(errorBody);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public IssueCategoryModel getCategoryModelByName(String name) {
        for (IssueCategoryModel item : categoryLiveList.getValue()) {
            if (item.getName().equals(name))
                return item;
        }
        return null;
    }
}
