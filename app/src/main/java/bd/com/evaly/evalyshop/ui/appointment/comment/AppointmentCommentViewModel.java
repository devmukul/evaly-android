package bd.com.evaly.evalyshop.ui.appointment.comment;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.appointment.comment.AppointmentCommentRequest;
import bd.com.evaly.evalyshop.models.appointment.comment.AppointmentCommentResponse;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.AppointmentApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class AppointmentCommentViewModel extends ViewModel {

    private final SavedStateHandle savedStateHandle;
    protected SingleLiveEvent<Boolean> isLoadingLiveData = new SingleLiveEvent<>();
    private MutableLiveData<AppointmentResponse> appointmentLiveData = new MutableLiveData<>();
    protected MutableLiveData<List<AppointmentCommentResponse>> liveData = new MutableLiveData<>();
    private List<AppointmentCommentResponse> arrayList = new ArrayList<>();
    private int page = 1;
    private boolean isLoading = false;

    @ViewModelInject
    public AppointmentCommentViewModel(@Assisted SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
        this.appointmentLiveData.setValue(this.savedStateHandle.get("model"));
        Logger.d(savedStateHandle.get("model"));
        loadFromApi();
    }

    public void loadFromApi() {
        if (isLoading)
            return;
        isLoading = true;
        isLoadingLiveData.setValue(true);
        AppointmentApiHelper.getAppointmentCommentList(appointmentLiveData.getValue().getAppointmentId(), page,
                new ResponseListenerAuth<CommonDataResponse<List<AppointmentCommentResponse>>, String>() {
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

                    @Override
                    public void onAuthError(boolean logout) {
                        if (!logout)
                            loadFromApi();

                    }
                });
    }

    public void createComment(AppointmentCommentRequest body) {
        AppointmentApiHelper.createAppointmentComment(body, new ResponseListenerAuth<CommonDataResponse<AppointmentCommentResponse>, String>() {
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

            @Override
            public void onAuthError(boolean logout) {

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
