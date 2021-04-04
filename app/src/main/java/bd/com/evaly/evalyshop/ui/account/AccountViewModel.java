package bd.com.evaly.evalyshop.ui.account;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.remote.ApiRepository;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.token.ChatApiHelper;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AccountViewModel extends ViewModel {

    private ApiRepository apiRepository;
    MutableLiveData<Integer> messageCount = new MutableLiveData<>();

    @Inject
    public AccountViewModel(ApiRepository apiRepository){
        this.apiRepository = apiRepository;
        getMessageCount();
        updateUserDetails();
    }


    private void getMessageCount() {

        if (Calendar.getInstance().getTimeInMillis() - CredentialManager.getMessageCounterLastUpdated() < 600000) {
            messageCount.setValue(CredentialManager.getMessageCount());
            return;
        }

        apiRepository.getMessageCount(new ResponseListenerAuth<CommonDataResponse<String>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<String> response, int statusCode) {
                messageCount.setValue(response.getCount());
                CredentialManager.setMessageCounterLastUpdated();
                CredentialManager.setMessageCount(response.getCount());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    private void updateUserDetails() {

        apiRepository.getUserProfile(CredentialManager.getToken(), new ResponseListenerAuth<CommonDataResponse<UserModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserModel> response, int statusCode) {
                if (response.getData() != null)
                    CredentialManager.saveUserData(response.getData());
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
