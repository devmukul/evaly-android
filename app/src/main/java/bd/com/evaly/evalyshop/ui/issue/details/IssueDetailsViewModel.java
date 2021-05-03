package bd.com.evaly.evalyshop.ui.issue.details;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.issueNew.comment.IssueTicketCommentModel;
import bd.com.evaly.evalyshop.models.issueNew.list.IssueListModel;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class IssueDetailsViewModel extends BaseViewModel {

    private IssueListModel issueModel;
    protected MutableLiveData<List<IssueTicketCommentModel>> replyLiveList = new MutableLiveData<>();
    protected MutableLiveData<IssueTicketCommentModel> replySubmitLiveData = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse<IssueListModel>> resolveLiveData = new MutableLiveData<>();

    @Inject
    public IssueDetailsViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        super(savedStateHandle, apiRepository, preferenceRepository);
        issueModel = savedStateHandle.get("model");
        loadReplies();
    }

    public void loadReplies() {
        apiRepository.getIssueCommentList(issueModel.getId(), new ResponseListener<CommonDataResponse<List<IssueTicketCommentModel>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<IssueTicketCommentModel>> response, int statusCode) {
                replyLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void submitReply(String text) {

        apiRepository.createIssueComment(issueModel.getId(), text, new ResponseListener<CommonDataResponse<IssueTicketCommentModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<IssueTicketCommentModel> response, int statusCode) {
                replySubmitLiveData.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show(R.string.something_wrong);
                replySubmitLiveData.setValue(null);
            }

        });
    }


    public void resolveIssue() {

        apiRepository.resolveIssue("resolved", (int) issueModel.getId(), new ResponseListener<CommonDataResponse<IssueListModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<IssueListModel> response, int statusCode) {
                resolveLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show(R.string.something_wrong);
                resolveLiveData.setValue(null);
            }

        });
    }

}
