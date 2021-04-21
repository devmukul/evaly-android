package bd.com.evaly.evalyshop.ui.auth.forgetPassword;

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
public class ForgetPasswordViewModel extends BaseViewModel {

    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;
    protected MutableLiveData<CaptchaResponse> captchaModelLive = new MutableLiveData<>();
    protected SingleLiveEvent<Void> hideLoading = new SingleLiveEvent<>();
    protected SingleLiveEvent<JsonObject> successLiveData = new SingleLiveEvent<>();


    @Inject
    public ForgetPasswordViewModel(ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
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

    public void resetPassword(String phoneNumber, String captchaValue) {
        if (captchaModelLive.getValue() == null) {
            ToastUtils.show("Please reload the page");
            return;
        }
        HashMap<String, String> body = new HashMap<>();
        body.put("phone_number", phoneNumber);
        body.put("captcha_id", captchaModelLive.getValue().getCaptchaId());
        body.put("captcha_value", captchaValue);
        body.put("service_name", "evaly");

        apiRepository.forgetPassword(body, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                hideLoading.call();
                ToastUtils.show(response.get("message").getAsString());
                if (statusCode == 201 || statusCode == 200) {
                    successLiveData.setValue(response);
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                hideLoading.call();
                if (errorBody != null && !errorBody.equals(""))
                    ToastUtils.show(errorBody);
                else
                    ToastUtils.show(R.string.something_wrong);
            }

        });
    }
}
