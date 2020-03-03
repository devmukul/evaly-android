package bd.com.evaly.evalyshop.ui.newsfeed.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;
import bd.com.evaly.evalyshop.rest.apiHelper.NewsfeedApiHelper;


public class NewsfeedViewModel extends ViewModel {

    private MutableLiveData<Integer> notificationCountLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> logoutLiveData = new MutableLiveData<>();


    public LiveData<Integer> getNotificationCountLiveData() {
        return notificationCountLiveData;
    }

    public LiveData<Boolean> getLogoutLiveData() {
        return logoutLiveData;
    }


    public void updateNotificationCount() {


        NewsfeedApiHelper.getNotificationCount(CredentialManager.getToken(), new ResponseListenerAuth<NotificationCount, String>() {
            @Override
            public void onDataFetched(NotificationCount response, int statusCode) {

                notificationCountLiveData.setValue(response.getCount());

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                notificationCountLiveData.setValue(0);
            }

            @Override
            public void onAuthError(boolean logout) {

                if (!logout)
                    updateNotificationCount();
                else
                    logoutLiveData.setValue(true);

            }
        });


    }


}
