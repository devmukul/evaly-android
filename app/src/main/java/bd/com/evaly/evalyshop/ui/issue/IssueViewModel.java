package bd.com.evaly.evalyshop.ui.issue;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.issueNew.list.IssueListModel;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class IssueViewModel extends BaseViewModel {
    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;
    private int page;
    private String invoice;
    private List<IssueListModel> arrayList = new ArrayList<>();
    protected MutableLiveData<List<IssueListModel>> liveList = new MutableLiveData<>();
    protected SingleLiveEvent<Boolean> loadingBar = new SingleLiveEvent<>();

    @Inject
    public IssueViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
        this.invoice = savedStateHandle.get("invoice");
        this.page = 1;
        getIssuesList();
    }

    public void getIssuesList() {
        apiRepository.getIssueList(invoice, page, new ResponseListener<CommonDataResponse<List<IssueListModel>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<IssueListModel>> response, int statusCode) {
                loadingBar.setValue(false);
                arrayList.addAll(response.getData());
                liveList.setValue(arrayList);
                page++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                loadingBar.setValue(false);
                ToastUtils.show(errorBody);
            }
        });
    }

}
