package bd.com.evaly.evalyshop.ui.appointment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AppointmentViewModel extends ViewModel {

    MutableLiveData<CommonDataResponse<AppointmentResponse>> cancelResponse = new MutableLiveData<>();
    MutableLiveData<List<AppointmentResponse>> liveList = new MutableLiveData<>();
    SingleLiveEvent<Void> hideLoadingBar = new SingleLiveEvent<>();
    List<AppointmentResponse> arrayList = new ArrayList<>();
    private int page;
    private ApiRepository apiRepository;

    @Inject
    public AppointmentViewModel(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
        page = 1;
        loadList();
    }

    public void loadList() {
        apiRepository.getAppointmentList(page, new ResponseListener<CommonDataResponse<List<AppointmentResponse>>, String>() {
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

        });
    }

    public void cancelAppointment(String id) {
        apiRepository.cancelAppointment(id, new ResponseListener<CommonDataResponse<AppointmentResponse>, String>() {
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

        });
    }

    public void clear() {
        arrayList.clear();
        page = 1;
    }

}
