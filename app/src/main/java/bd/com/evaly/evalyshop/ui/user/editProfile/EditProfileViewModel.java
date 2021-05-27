package bd.com.evaly.evalyshop.ui.user.editProfile;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

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
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditProfileViewModel extends ViewModel {

    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;
    private MutableLiveData<Boolean> infoSavedStatus = new MutableLiveData<>();
    protected SingleLiveEvent<Boolean> dialogToggle = new SingleLiveEvent<>();
    protected SingleLiveEvent<Boolean> autoLogin = new SingleLiveEvent<>();

    @Inject
    public EditProfileViewModel(ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
    }

    public void login(HashMap<String, String> payload){
        apiRepository.login(payload, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int code) {
                dialogToggle.setValue(true);
                switch (code) {
                    case 200:
                    case 201:
                    case 202:
                        response = response.get("data").getAsJsonObject();
                        String token = response.get("access_token").getAsString();
                        preferenceRepository.saveToken(token);
                        preferenceRepository.saveRefreshToken(response.get("refresh_token").getAsString());
                        preferenceRepository.saveUserName(payload.get("user"));
                        preferenceRepository.savePassword(payload.get("password"));
                        updateUserInfo();
                        break;
                    default:
                        ToastUtils.show("Incorrect phone number or password. Please try again!");
                }
            }

            @Override
            public void onFailed(String body, int status) {
                dialogToggle.setValue(false);
                ToastUtils.show(body);
            }

        });
    }


    public void updateUserInfo() {
        dialogToggle.setValue(true);
        apiRepository.getUserProfile(preferenceRepository.getToken(), new ResponseListener<CommonDataResponse<UserModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserModel> response, int statusCode) {
                dialogToggle.setValue(false);
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
                autoLogin.setValue(true);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialogToggle.setValue(false);
                ToastUtils.show("Couldn't login, please try again");
                autoLogin.setValue(false);
            }

        });

    }


    public void uploadPicture(Bitmap bitmap) {

        apiRepository.uploadImage(bitmap, new ResponseListener<CommonDataResponse<ImageDataModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ImageDataModel> response, int statusCode) {
                HashMap<String, String> body = new HashMap<>();
                body.put("profile_pic_url", response.getData().getUrl());
                body.put("image_sm", response.getData().getUrlSm());
                setUserData(body);
                dialogToggle.setValue(false);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialogToggle.setValue(false);
                ToastUtils.show("Upload error, try again!");
            }

        });
    }

    public void setUserData(HashMap<String, String> userInfo) {
        apiRepository.setUserData(preferenceRepository.getToken(), userInfo, new ResponseListener<CommonDataResponse<UserModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserModel> response, int statusCode) {
                preferenceRepository.saveUserData(response.getData());
                infoSavedStatus.setValue(true);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }
        });
    }

    public void setUserData(JsonObject userInfo) {

        apiRepository.setUserData(preferenceRepository.getToken(), userInfo, new ResponseListener<CommonDataResponse<UserModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserModel> response, int statusCode) {
                preferenceRepository.saveUserData(response.getData());
                infoSavedStatus.setValue(true);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public LiveData<Boolean> getInfoSavedStatus() {
        return infoSavedStatus;
    }

    public void setInfoSavedStatus(boolean infoSavedStatus) {
        this.infoSavedStatus.setValue(infoSavedStatus);
    }
}
