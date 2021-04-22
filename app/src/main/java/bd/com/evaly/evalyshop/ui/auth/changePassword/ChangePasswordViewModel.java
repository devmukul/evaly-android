package bd.com.evaly.evalyshop.ui.auth.changePassword;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.concurrent.Executors;

import javax.inject.Inject;

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
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChangePasswordViewModel extends BaseViewModel {

    protected SingleLiveEvent<Boolean> loadingDialog = new SingleLiveEvent<>();
    protected SingleLiveEvent<Void> loginSuccess = new SingleLiveEvent<>();
    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;


    @Inject
    public ChangePasswordViewModel(ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
    }


    public void updatePassword(String currentPassword, String newPassword) {

        loadingDialog.setValue(true);
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("new_password", newPassword);
        parameters.put("old_password", currentPassword);

        apiRepository.changePassword(preferenceRepository.getToken(), parameters, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                loadingDialog.setValue(true);
                ToastUtils.show(response.get("message").getAsString());
                if (response.get("success").getAsBoolean()) {
                    preferenceRepository.savePassword(newPassword);
                    signInUser();
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                loadingDialog.setValue(false);
                if (errorBody != null && !errorBody.equals(""))
                    ToastUtils.show(errorBody);
                else
                    ToastUtils.show("Couldn't change password.");
            }
        });
    }


    public void syncXmpp() {
        HashMap<String, String> data = new HashMap<>();
        data.put("user", preferenceRepository.getUserName());
        data.put("host", Constants.XMPP_HOST);
        data.put("newpass", preferenceRepository.getPassword());
        apiRepository.changeXmppPassword(data, new ResponseListener<JsonPrimitive, String>() {
            @Override
            public void onDataFetched(JsonPrimitive response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void signInUser() {
        loadingDialog.setValue(true);
        HashMap<String, String> payload = new HashMap<>();
        payload.put("phone_number", preferenceRepository.getUserName());
        payload.put("password", preferenceRepository.getPassword());

        apiRepository.login(payload, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int code) {
                switch (code) {
                    case 200:
                    case 201:
                    case 202:
                        response = response.get("data").getAsJsonObject();
                        String token = response.get("access_token").getAsString();
                        preferenceRepository.saveToken(token);
                        preferenceRepository.saveRefreshToken(response.get("refresh_token").getAsString());
                        syncXmpp();
                        updateUserInfo();
                        ToastUtils.show("Successfully signed in.");
                        break;
                    default:
                        ToastUtils.show("Incorrect phone number or password. Please try again! ");
                }
                loadingDialog.setValue(false);
            }

            @Override
            public void onFailed(String body, int status) {
                loadingDialog.setValue(false);
                ToastUtils.show(body);
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
