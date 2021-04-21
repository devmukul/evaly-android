package bd.com.evaly.evalyshop.ui.auth.login;

import android.content.Intent;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.roomdb.ProviderDatabase;
import bd.com.evaly.evalyshop.data.roomdb.userInfo.UserInfoDao;
import bd.com.evaly.evalyshop.data.roomdb.userInfo.UserInfoEntity;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignInViewModel extends BaseViewModel {

    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;
    protected SingleLiveEvent<Void> hideLoading = new SingleLiveEvent<>();
    protected SingleLiveEvent<String> showToast = new SingleLiveEvent<>();
    protected SingleLiveEvent<Void> loginSuccess = new SingleLiveEvent<>();


    @Inject
    public SignInViewModel(ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
    }

    public void signInUser(String phoneNumber, String password) {

        HashMap<String, String> payload = new HashMap<>();
        payload.put("phone_number", phoneNumber);
        payload.put("password", password);

        apiRepository.login(payload, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int code) {
                hideLoading.call();
                switch (code) {
                    case 200:
                    case 201:
                    case 202:
                        response = response.get("data").getAsJsonObject();
                        String token = response.get("access_token").getAsString();
                        preferenceRepository.saveToken(token);
                        preferenceRepository.saveRefreshToken(response.get("refresh_token").getAsString());
                        preferenceRepository.saveUserName(phoneNumber);
                        preferenceRepository.savePassword(password);
                        updateUserInfo();
                        showToast.setValue("Successfully signed in.");
                        break;
                    default:
                        showToast.setValue("Incorrect phone number or password. Please try again!");
                }

            }

            @Override
            public void onFailed(String errorBody, int status) {
                hideLoading.call();
                if (errorBody != null && !errorBody.equals(""))
                    showToast.setValue(errorBody);
                else
                    ToastUtils.show(R.string.something_wrong);
            }

        });
    }


    public void updateUserInfo() {
        apiRepository.getUserProfile(preferenceRepository.getToken(), new ResponseListener<CommonDataResponse<UserModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserModel> response, int statusCode) {
                preferenceRepository.saveUserData(response.getData());
                ProviderDatabase providerDatabase = ProviderDatabase.getInstance(AppController.getContext());
                UserInfoDao userInfoDao = providerDatabase.userInfoDao();

                Executors.newSingleThreadExecutor().execute(() -> {
                    UserInfoEntity entity = new UserInfoEntity();
                    entity.setToken(preferenceRepository.getToken());
                    entity.setRefreshToken(preferenceRepository.getRefreshToken());
                    entity.setName(response.getData().getFullName());
                    entity.setImage(response.getData().getImageSm());
                    entity.setUsername(preferenceRepository.getUserName());
                    entity.setPassword(preferenceRepository.getPassword());
                    userInfoDao.insert(entity);
                });

                loginSuccess.call();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }
}
