package bd.com.evaly.evalyshop.ui.refundSettlement.preOtp;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.refundSettlement.OtpResponse;
import bd.com.evaly.evalyshop.models.refundSettlement.RefundSettlementResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PreOtpViewModel extends ViewModel {

    private ApiRepository apiRepository;
    private String requestId = "";
    protected MutableLiveData<RefundSettlementResponse> settlementResponse = new MutableLiveData<>();

    @Inject
    public PreOtpViewModel(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
        generateOtp();
    }

    public void generateOtp() {
        apiRepository.generateOtp(CredentialManager.getToken(), new ResponseListenerAuth<CommonDataResponse<OtpResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<OtpResponse> response, int statusCode) {
                ToastUtils.show("An OTP has been sent to your phone number");
                requestId = response.getData().getRequestId();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show(errorBody);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void getSettlementAccounts(String otp) {
        if (requestId == null || requestId.isEmpty()) {
            generateOtp();
            ToastUtils.show("Please try again");
        }
        apiRepository.getSettlementAccounts(CredentialManager.getToken(), otp, requestId, new ResponseListenerAuth<CommonDataResponse<RefundSettlementResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<RefundSettlementResponse> response, int statusCode) {
                settlementResponse.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show(errorBody);
                settlementResponse.setValue(null);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

}
