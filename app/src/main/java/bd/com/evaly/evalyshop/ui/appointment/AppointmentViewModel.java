package bd.com.evaly.evalyshop.ui.appointment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.AppointmentApiHelper;

public class AppointmentViewModel extends ViewModel {

    MutableLiveData<List<AppointmentResponse>> liveList = new MutableLiveData<>();
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
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

}
