package bd.com.evaly.evalyshop.ui.issue.create;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.models.issue.IssueAnswerResponse;
import bd.com.evaly.evalyshop.models.issueNew.category.IssueCategoryModel;
import bd.com.evaly.evalyshop.models.issueNew.create.IssueCreateBody;
import bd.com.evaly.evalyshop.models.issueNew.list.IssueListModel;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CreateIssueViewModel extends ViewModel {

    protected MutableLiveData<List<IssueAnswerResponse>> answerLiveList = new MutableLiveData<>();
    protected MutableLiveData<List<IssueAnswerResponse>> subAnswerLiveList = new MutableLiveData<>();
    protected MutableLiveData<List<IssueCategoryModel>> categoryLiveList = new MutableLiveData<>();
    protected SingleLiveEvent<Boolean> issueCreatedLiveData = new SingleLiveEvent<>();
    protected MutableLiveData<ImageDataModel> imageLiveData = new SingleLiveEvent<>();
    protected SingleLiveEvent<String> imageErrorLiveData = new SingleLiveEvent<>();
    protected String orderStatus;
    private ApiRepository apiRepository;
    protected int answerId = -1;
    protected int subAnswerId = -1;

    @Inject
    public CreateIssueViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository) {
        this.orderStatus = savedStateHandle.get("orderStatus");
        this.apiRepository = apiRepository;
        loadCategories();
    }

    public void loadAnswers(String categoryId) {
        apiRepository.getIssueAnswers(categoryId, new ResponseListener<CommonDataResponse<List<IssueAnswerResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<IssueAnswerResponse>> response, int statusCode) {
                if (response.getData().size() > 0 && subAnswerId < 0)
                    loadSubAnswers(String.valueOf(response.getData().get(0).getId()));
                answerLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void loadSubAnswers(String answerId) {
        apiRepository.getIssueSubAnswers(answerId, new ResponseListener<CommonDataResponse<List<IssueAnswerResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<IssueAnswerResponse>> response, int statusCode) {
                subAnswerLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void submitIssue(IssueCreateBody model) {

        apiRepository.createIssue(model, new ResponseListener<CommonDataResponse<IssueListModel>, String>() {
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

        });
    }

    public void loadCategories() {

        apiRepository.getCategories(orderStatus, new ResponseListener<CommonDataResponse<List<IssueCategoryModel>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<IssueCategoryModel>> response, int statusCode) {
                if (response.getData().size() > 0)
                    loadAnswers(String.valueOf(response.getData().get(0).getId()));
                categoryLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }


    public void uploadImage(Bitmap bitmap) {
        apiRepository.uploadImage(bitmap, new ResponseListener<CommonDataResponse<ImageDataModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ImageDataModel> response, int statusCode) {
                imageLiveData.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                imageErrorLiveData.setValue(errorBody);
            }

        });
    }

    public IssueCategoryModel getCategoryModelByName(String name) {
        if (categoryLiveList.getValue() == null)
            return null;
        for (IssueCategoryModel item : categoryLiveList.getValue()) {
            if (item.getName().equals(name))
                return item;
        }
        return null;
    }

    public IssueAnswerResponse getAnswerModelModelByName(String name) {
        if (answerLiveList.getValue() == null)
            return null;
        for (IssueAnswerResponse item : answerLiveList.getValue()) {
            if (item.getText().equals(name))
                return item;
        }
        return null;
    }

    public IssueAnswerResponse getSubAnswerModelModelByName(String name) {
        if (subAnswerLiveList.getValue() == null)
            return null;
        for (IssueAnswerResponse item : subAnswerLiveList.getValue()) {
            if (item.getText().equals(name))
                return item;
        }
        return null;
    }

}
