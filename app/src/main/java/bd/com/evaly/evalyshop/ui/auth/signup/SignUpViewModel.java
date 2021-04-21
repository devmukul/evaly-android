package bd.com.evaly.evalyshop.ui.auth.signup;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;

import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.auth.captcha.CaptchaResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignUpViewModel extends BaseViewModel {

    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;
    protected MutableLiveData<CaptchaResponse> captchaModelLive = new MutableLiveData<>();
    protected SingleLiveEvent<Void> hideLoading = new SingleLiveEvent<>();
    protected SingleLiveEvent<JsonObject> signUpSuccess = new SingleLiveEvent<>();


    @Inject
    public SignUpViewModel(ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
    }

    public void getCaptcha() {
        apiRepository.getCaptcha(new ResponseListener<CommonDataResponse<CaptchaResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<CaptchaResponse> response, int statusCode) {
                captchaModelLive.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void signUpUser(String firstName, String lastName, String phoneNumber, String captchaValue) {

        if (captchaModelLive.getValue() == null) {
            ToastUtils.show("Please reload the page");
            return;
        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("first_name", firstName);
        hashMap.put("last_name", lastName);
        hashMap.put("phone_number", phoneNumber);
        hashMap.put("captcha_id", captchaModelLive.getValue().getCaptchaId());
        hashMap.put("captcha_value", captchaValue);
        hashMap.put("service_name", "evaly");

        apiRepository.register(hashMap, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                hideLoading.call();
                if (statusCode == 200) {
                    ToastUtils.show("This mobile number has already been used");
                } else if (statusCode == 201) {
                    signUpSuccess.setValue(response);
                } else {
                    ToastUtils.show(R.string.something_wrong);
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                hideLoading.call();
                ToastUtils.show(errorBody);
            }
        });
    }

}
