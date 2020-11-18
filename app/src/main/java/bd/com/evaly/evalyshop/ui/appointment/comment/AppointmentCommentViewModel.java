package bd.com.evaly.evalyshop.ui.appointment.comment;

import androidx.hilt.Assisted;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.appointment.comment.AppointmentCommentResponse;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.AppointmentApiHelper;

public class AppointmentCommentViewModel extends ViewModel {

    private final SavedStateHandle savedStateHandle;
    private MutableLiveData<AppointmentResponse> appointmentLiveData = new MutableLiveData<>();
    protected MutableLiveData<List<AppointmentCommentResponse>> liveData = new MutableLiveData<>();
    private List<AppointmentCommentResponse> arrayList = new ArrayList<>();
    private int page = 1;
    private boolean isLoading = false;

    @Inject
    public AppointmentCommentViewModel(@Assisted SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
        this.appointmentLiveData.setValue(this.savedStateHandle.get("model"));
        loadFromApi();
    }

    public void loadFromApi() {
        if (isLoading)
            return;
        isLoading = true;
        AppointmentApiHelper.getAppointmentCommentList(appointmentLiveData.getValue().getAppointmentId(), page, new ResponseListenerAuth<CommonDataResponse<List<AppointmentCommentResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<AppointmentCommentResponse>> response, int statusCode) {
                isLoading = false;
                arrayList.addAll(response.getData());
                liveData.setValue(arrayList);
                if (response.getData().size() > 10)
                    page++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                isLoading = false;

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    loadFromApi();

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
