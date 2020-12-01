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
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;

public class EditProfileViewModel extends ViewModel {

    private MutableLiveData<Boolean> infoSavedStatus = new MutableLiveData<>();

    public EditProfileViewModel() {

    }

    public void setUserData(HashMap<String, String> userInfo) {

        AuthApiHelper.setUserData(CredentialManager.getToken(), userInfo, new ResponseListenerAuth<CommonDataResponse<UserModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserModel> response, int statusCode) {
                CredentialManager.saveUserData(response.getData());
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

    public void setUserData(JsonObject userInfo) {

        AuthApiHelper.setUserData(CredentialManager.getToken(), userInfo, new ResponseListenerAuth<CommonDataResponse<UserModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserModel> response, int statusCode) {
                CredentialManager.saveUserData(response.getData());
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


    public void updateToXMPP(HashMap<String, String> userInfo) {

        AuthApiHelper.setUserDataToXmpp(userInfo, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                JsonObject ob = response.getAsJsonObject("data");
                UserModel userModel = new Gson().fromJson(ob.toString(), UserModel.class);
                if (ob.get("first_name").isJsonNull())
                    userModel.setFirstName("");

                if (ob.get("last_name").isJsonNull())
                    userModel.setLastName("");
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
