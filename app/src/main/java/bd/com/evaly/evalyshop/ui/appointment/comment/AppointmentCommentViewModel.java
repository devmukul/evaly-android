package bd.com.evaly.evalyshop.ui.appointment.comment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.appointment.comment.AppointmentCommentRequest;
import bd.com.evaly.evalyshop.models.appointment.comment.AppointmentCommentResponse;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AppointmentCommentViewModel extends ViewModel {

    private final SavedStateHandle savedStateHandle;
    protected SingleLiveEvent<Boolean> isLoadingLiveData = new SingleLiveEvent<>();
    private MutableLiveData<AppointmentResponse> appointmentLiveData = new MutableLiveData<>();
    protected MutableLiveData<List<AppointmentCommentResponse>> liveData = new MutableLiveData<>();
    private List<AppointmentCommentResponse> arrayList = new ArrayList<>();
    private int page = 1;
    private boolean isLoading = false;
    private ApiRepository apiRepository;

    @Inject
    public AppointmentCommentViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository) {
        this.savedStateHandle = savedStateHandle;
        this.appointmentLiveData.setValue(this.savedStateHandle.get("model"));
        this.apiRepository = apiRepository;
        loadFromApi();
    }

    public void loadFromApi() {
        if (isLoading)
            return;
        isLoading = true;
        isLoadingLiveData.setValue(true);
        apiRepository.getAppointmentCommentList(appointmentLiveData.getValue().getAppointmentId(), page,
                new ResponseListener<CommonDataResponse<List<AppointmentCommentResponse>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<AppointmentCommentResponse>> response, int statusCode) {
                        isLoading = false;
                        arrayList.addAll(response.getData());
                        isLoadingLiveData.setValue(false);
                        liveData.setValue(arrayList);
                        page++;
                    }

                    @Override
                    public void onFailed(String errorBody, int errorCode) {
                        isLoading = false;
                        isLoadingLiveData.setValue(false);
                    }

                });
    }

    public void createComment(AppointmentCommentRequest body) {
        apiRepository.createAppointmentComment(body, new ResponseListener<CommonDataResponse<AppointmentCommentResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<AppointmentCommentResponse> response, int statusCode) {
                if (response.getSuccess()) {
                    page = 1;
                    arrayList.clear();
                    loadFromApi();
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public MutableLiveData<AppointmentResponse> getAppointmentLiveData() {
        return appointmentLiveData;
    }

    public MutableLiveData<List<AppointmentCommentResponse>> getListLiveData() {
        return liveData;
    }
}
