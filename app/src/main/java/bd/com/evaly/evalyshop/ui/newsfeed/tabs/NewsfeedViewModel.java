package bd.com.evaly.evalyshop.ui.newsfeed.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.remote.ApiRepository;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NewsfeedViewModel extends ViewModel {

    private MutableLiveData<Integer> notificationCountLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> logoutLiveData = new MutableLiveData<>();
    private ApiRepository apiRepository;

    @Inject
    public NewsfeedViewModel(ApiRepository apiRepository){
        this.apiRepository = apiRepository;
    }

    public LiveData<Integer> getNotificationCountLiveData() {
        return notificationCountLiveData;
    }

    public LiveData<Boolean> getLogoutLiveData() {
        return logoutLiveData;
    }


    public void updateNotificationCount() {


        apiRepository.getNotificationCount(CredentialManager.getToken(), new ResponseListenerAuth<NotificationCount, String>() {
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
