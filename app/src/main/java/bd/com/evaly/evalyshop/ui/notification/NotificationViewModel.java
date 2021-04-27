package bd.com.evaly.evalyshop.ui.notification;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.notification.NotificationItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NotificationViewModel extends BaseViewModel {

    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;
    protected MutableLiveData<List<NotificationItem>> liveList = new MutableLiveData<>();

    @Inject
    public NotificationViewModel(ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
        getNotifications();
    }

    public void getNotifications() {

        apiRepository.getNotification(preferenceRepository.getToken(), 1, new ResponseListener<CommonResultResponse<List<NotificationItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<NotificationItem>> response, int statusCode) {
                liveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }
        });
    }

    public void markAsRead() {

        apiRepository.markNotificationAsRead(preferenceRepository.getToken(), new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                ToastUtils.show("Marked as read!");
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }
        });
    }

}
