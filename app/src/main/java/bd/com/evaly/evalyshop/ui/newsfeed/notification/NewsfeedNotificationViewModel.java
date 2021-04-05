package bd.com.evaly.evalyshop.ui.newsfeed.notification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.notification.NotificationItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NewsfeedNotificationViewModel extends ViewModel {

    private MutableLiveData<List<NotificationItem>> notificationsMutableLiveData = new MutableLiveData<>();
    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;

    @Inject
    public NewsfeedNotificationViewModel(ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
    }

    public LiveData<List<NotificationItem>> getNotificationsLiveData() {
        return notificationsMutableLiveData;
    }

    public void getNotification(int page) {
        apiRepository.getNewsfeedNotification(preferenceRepository.getToken(), page, new ResponseListenerAuth<CommonResultResponse<List<NotificationItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<NotificationItem>> response, int statusCode) {
                notificationsMutableLiveData.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

                if (!logout)
                    getNotification(page);

            }
        });

    }


    public void markAsRead() {

        apiRepository.markNotificationAsRead(preferenceRepository.getToken(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

                if (!logout)
                    markAsRead();

            }
        });

    }


}
