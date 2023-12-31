package bd.com.evaly.evalyshop.ui.appointment.create;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.appointment.AppointmentCategoryResponse;
import bd.com.evaly.evalyshop.models.appointment.AppointmentRequest;
import bd.com.evaly.evalyshop.models.appointment.AppointmentTimeSlotResponse;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CreateAppointmentViewModel extends ViewModel {

    MutableLiveData<List<AppointmentCategoryResponse>> categoryLiveList = new MutableLiveData<>();
    MutableLiveData<List<AppointmentTimeSlotResponse>> timeSlotLiveList = new MutableLiveData<>();
    MutableLiveData<CommonDataResponse<AppointmentResponse>> createdLiveList = new MutableLiveData<>();
    SingleLiveEvent<String> createErrorMessage = new SingleLiveEvent<>();
    MutableLiveData<String> timeErrorMessage = new MutableLiveData<>();
    private ApiRepository apiRepository;


    @Inject
    public CreateAppointmentViewModel(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
        loadCategoryList();
    }

    public void loadCategoryList() {
        apiRepository.getAppointmentCategoryList(new ResponseListener<CommonDataResponse<List<AppointmentCategoryResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<AppointmentCategoryResponse>> response, int statusCode) {
                categoryLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public String getCategorySlug(String name) {
        if (categoryLiveList.getValue() == null || categoryLiveList.getValue().size() == 0)
            return null;
        for (AppointmentCategoryResponse item : categoryLiveList.getValue()) {
            if (item.getName().equals(name))
                return item.getSlug();
        }
        return null;
    }

    public void getTimeSlot(String date) {
        apiRepository.getAppointmentTimeSlotList(date, new ResponseListener<CommonDataResponse<List<AppointmentTimeSlotResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<AppointmentTimeSlotResponse>> response, int statusCode) {
                timeSlotLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                if (errorBody.contains("message")) {
                    CommonDataResponse commonDataResponse = new Gson().fromJson(errorBody, CommonDataResponse.class);
                    timeErrorMessage.setValue(commonDataResponse.getMessage());
                } else
                    timeErrorMessage.setValue("Error occurred");
            }

        });
    }

    public void createAppointment(AppointmentRequest body) {
        apiRepository.createAppointment(body, new ResponseListener<CommonDataResponse<AppointmentResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<AppointmentResponse> response, int statusCode) {
                createdLiveList.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                createErrorMessage.setValue(errorBody);
            }

        });
    }

}
