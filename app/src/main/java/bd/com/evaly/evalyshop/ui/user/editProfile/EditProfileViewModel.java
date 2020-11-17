package bd.com.evaly.evalyshop.ui.user.editProfile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.profile.UserInfoResponse;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;

public class EditProfileViewModel extends ViewModel {

    private MutableLiveData<Boolean> infoSavedStatus = new MutableLiveData<>();

    public EditProfileViewModel() {

    }

    public void setUserData(HashMap<String, String> userInfo) {

        AuthApiHelper.setUserData(CredentialManager.getToken(), userInfo, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                JsonObject ob = response.getAsJsonObject("data");
                UserModel userModel = new Gson().fromJson(ob.toString(), UserModel.class);
                if (ob.get("first_name").isJsonNull())
                    userModel.setFirst_name("");

                if (ob.get("last_name").isJsonNull())
                    userModel.setLast_name("");
                CredentialManager.saveUserData(userModel);
                UserInfoResponse userInfo = CredentialManager.getUserInfo();

                if (userInfo != null) {
                    userInfo.setFullName(userModel.getFullName());
                    userInfo.setPhoneNumber(userModel.getContacts());
                    userInfo.setGender(userModel.getGender());
                    CredentialManager.saveUserInfo(userInfo);
                }

                infoSavedStatus.setValue(true);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

                if (!logout)
                    setUserData(userInfo);

            }
        });

    }


    public void addUserData(HashMap<String, String> userInfo) {

        AuthApiHelper.addUserData(CredentialManager.getToken(), userInfo, new ResponseListenerAuth<CommonDataResponse<UserInfoResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserInfoResponse> response, int statusCode) {
                if (response.getData() != null)
                    CredentialManager.saveUserInfo(response.getData());
                infoSavedStatus.setValue(true);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    addUserData(userInfo);
            }
        });
    }

    public void updateUserDetails() {
        AuthApiHelper.getUserInfo(new ResponseListenerAuth<CommonDataResponse<UserInfoResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserInfoResponse> response, int statusCode) {
                CredentialManager.saveUserInfo(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void updateToXMPP(HashMap<String, String> userInfo) {

        AuthApiHelper.setUserDataToXmpp(userInfo, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                JsonObject ob = response.getAsJsonObject("data");
                UserModel userModel = new Gson().fromJson(ob.toString(), UserModel.class);
                if (ob.get("first_name").isJsonNull())
                    userModel.setFirst_name("");

                if (ob.get("last_name").isJsonNull())
                    userModel.setLast_name("");
                CredentialManager.saveUserData(userModel);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

                if (!logout)
                    updateToXMPP(userInfo);

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
