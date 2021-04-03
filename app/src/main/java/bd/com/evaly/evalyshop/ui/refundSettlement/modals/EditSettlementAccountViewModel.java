package bd.com.evaly.evalyshop.ui.refundSettlement.modals;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.refundSettlement.OtpResponse;
import bd.com.evaly.evalyshop.models.refundSettlement.RefundSettlementResponse;
import bd.com.evaly.evalyshop.models.refundSettlement.request.BankAccountRequest;
import bd.com.evaly.evalyshop.models.refundSettlement.request.MFSAccountRequest;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditSettlementAccountViewModel extends ViewModel {

    private String requestId = "";
    protected MutableLiveData<String> typeLiveData = new MutableLiveData<>();
    protected RefundSettlementResponse accountsModel;
    protected MutableLiveData<RefundSettlementResponse> responseLiveData = new MutableLiveData<>();

    @Inject
    public EditSettlementAccountViewModel(SavedStateHandle arg) {
        accountsModel = arg.get("model");
        typeLiveData.setValue(arg.get("type"));
        generateOtp();
    }

    public void generateOtp() {
        AuthApiHelper.generateOtp(CredentialManager.getToken(), new ResponseListenerAuth<CommonDataResponse<OtpResponse>, String>() {
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

    public void submitBankInfo(BankAccountRequest body) {
        if (requestId == null || requestId.isEmpty() || typeLiveData.getValue() == null) {
            generateOtp();
            ToastUtils.show("Please try again");
        }
        body.setRequestId(requestId);

        AuthApiHelper.saveSettlementBankAccount(body, new ResponseListenerAuth<CommonDataResponse<RefundSettlementResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<RefundSettlementResponse> response, int statusCode) {
                responseLiveData.setValue(response.getData());
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

    public void submitMFSInfo(MFSAccountRequest body) {
        if (requestId == null || requestId.isEmpty() || typeLiveData.getValue() == null) {
            generateOtp();
            ToastUtils.show("Please try again");
        }
        body.setRequestId(requestId);
        AuthApiHelper.saveSettlementMFSAccount(typeLiveData.getValue(), body, new ResponseListenerAuth<CommonDataResponse<RefundSettlementResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<RefundSettlementResponse> response, int statusCode) {
                responseLiveData.setValue(response.getData());
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

}
