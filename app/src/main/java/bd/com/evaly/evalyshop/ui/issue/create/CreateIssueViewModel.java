package bd.com.evaly.evalyshop.ui.issue.create;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.issueNew.category.IssueCategoryModel;
import bd.com.evaly.evalyshop.models.issueNew.create.IssueCreateBody;
import bd.com.evaly.evalyshop.models.issueNew.list.IssueListModel;
import bd.com.evaly.evalyshop.rest.apiHelper.IssueApiHelper;
import bd.com.evaly.evalyshop.util.ToastUtils;

public class CreateIssueViewModel extends ViewModel {

    protected MutableLiveData<List<IssueCategoryModel>> categoryLiveList = new MutableLiveData<>();

    @ViewModelInject
    public CreateIssueViewModel(@Assisted SavedStateHandle savedStateHandle) {
        loadCategories();
    }

    public void submitIssue(IssueCreateBody model) {

        IssueApiHelper.createIssue(model, new ResponseListenerAuth<CommonDataResponse<IssueListModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<IssueListModel> response, int statusCode) {
                ToastUtils.show("Your issue has been submitted, you will be notified shortly");
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    submitIssue(model);
            }
        });
    }

    public void loadCategories() {

        IssueApiHelper.getCategories(new ResponseListenerAuth<CommonDataResponse<List<IssueCategoryModel>>, String>() {
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


    public IssueCategoryModel getCategoryModelByName(String name) {
        for (IssueCategoryModel item : categoryLiveList.getValue()) {
            if (item.getName().equals(name))
                return item;
        }
        return null;
    }

}
