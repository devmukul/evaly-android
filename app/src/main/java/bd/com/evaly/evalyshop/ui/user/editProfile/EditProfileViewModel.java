package bd.com.evaly.evalyshop.ui.user.editProfile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditProfileViewModel extends ViewModel {

    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;
    private MutableLiveData<Boolean> infoSavedStatus = new MutableLiveData<>();

    @Inject
    public EditProfileViewModel(ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
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

    public void updateToXMPP(HashMap<String, String> userInfo) {

        apiRepository.setUserDataToXmpp(userInfo, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                JsonObject ob = response.getAsJsonObject("data");
                UserModel userModel = new Gson().fromJson(ob.toString(), UserModel.class);
                if (ob.get("first_name").isJsonNull())
                    userModel.setFirstName("");

                if (ob.get("last_name").isJsonNull())
                    userModel.setLastName("");
                preferenceRepository.saveUserData(userModel);
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
