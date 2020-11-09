package bd.com.evaly.evalyshop.ui.appointment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.AppointmentApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class AppointmentViewModel extends ViewModel {

    MutableLiveData<CommonDataResponse<AppointmentResponse>> cancelResponse = new MutableLiveData<>();
    MutableLiveData<List<AppointmentResponse>> liveList = new MutableLiveData<>();
    SingleLiveEvent<Void> hideLoadingBar = new SingleLiveEvent<>();
    List<AppointmentResponse> arrayList = new ArrayList<>();
    private int page;

    @Inject
    public AppointmentViewModel() {
        page = 1;
        loadList();
    }

    public void loadList() {
        AppointmentApiHelper.getAppointmentList(page, new ResponseListenerAuth<CommonDataResponse<List<AppointmentResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<AppointmentResponse>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveList.setValue(arrayList);
                page++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                hideLoadingBar.call();
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void cancelAppointment(String id) {
        AppointmentApiHelper.cancelAppointment(id, new ResponseListenerAuth<CommonDataResponse<AppointmentResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<AppointmentResponse> response, int statusCode) {
                cancelResponse.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                if (errorBody.contains("message")) {
                    CommonDataResponse commonDataResponse = new Gson().fromJson(errorBody, CommonDataResponse.class);
                    cancelResponse.setValue(commonDataResponse);
                }
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void clear() {
        arrayList.clear();
        page = 1;
    }

}
