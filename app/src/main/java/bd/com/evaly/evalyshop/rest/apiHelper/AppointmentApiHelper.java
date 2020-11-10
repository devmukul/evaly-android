package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.appointment.AppointmentCategoryResponse;
import bd.com.evaly.evalyshop.models.appointment.AppointmentRequest;
import bd.com.evaly.evalyshop.models.appointment.AppointmentTimeSlotResponse;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;

public class AppointmentApiHelper extends BaseApiHelper {

    public static void getAppointmentList(int page, ResponseListenerAuth<CommonDataResponse<List<AppointmentResponse>>, String> listener) {
        getiApiClient().getAppointmentList(CredentialManager.getToken(), page).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getAppointmentCategoryList(ResponseListenerAuth<CommonDataResponse<List<AppointmentCategoryResponse>>, String> listener) {
        getiApiClient().getAppointmentCategoryList(CredentialManager.getToken(), 200).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getAppointmentTimeSlotList(String date, ResponseListenerAuth<CommonDataResponse<List<AppointmentTimeSlotResponse>>, String> listener) {
        getiApiClient().getAppointmentTimeSlotList(CredentialManager.getToken(), date).enqueue(getResponseCallBackDefault(listener));
    }

    public static void createAppointment(AppointmentRequest body, ResponseListenerAuth<CommonDataResponse<AppointmentResponse>, String> listener) {
        getiApiClient().createAppointment(CredentialManager.getToken(), body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void cancelAppointment(String id, ResponseListenerAuth<CommonDataResponse<AppointmentResponse>, String> listener) {
        getiApiClient().cancelAppointment(CredentialManager.getToken(), id).enqueue(getResponseCallBackDefault(listener));
    }

}
