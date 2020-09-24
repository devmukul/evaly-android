package bd.com.evaly.evalyshop.ui.order.orderDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class OrderDetailsViewModel extends ViewModel {

    private SingleLiveEvent<Boolean> refreshPage = new SingleLiveEvent<>();
    private MutableLiveData<CommonDataResponse<String>> refundEligibilityLiveData = new MutableLiveData<>();
    private MutableLiveData<CommonDataResponse<String>> refundDeleteLiveData = new MutableLiveData<>();

    public LiveData<CommonDataResponse<String>> getRefundEligibilityLiveData() {
        return refundEligibilityLiveData;
    }

    public LiveData<CommonDataResponse<String>> getRefundDeleteLiveData() {
        return refundDeleteLiveData;
    }

    public void checkRefundEligibility(String invoice) {
        OrderApiHelper.checkRefundEligibility(invoice, new ResponseListenerAuth<CommonDataResponse<String>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<String> response, int statusCode) {
                refundEligibilityLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                if (errorCode == 404) {
                    CommonDataResponse<String> response = new CommonDataResponse<>();
                    response.setSuccess(false);
                    response.setMessage("Not eligible");
                    refundEligibilityLiveData.setValue(response);
                }
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void deleteRefundTransaction(String invoice) {
        OrderApiHelper.deleteRefundTransaction(invoice, new ResponseListenerAuth<CommonDataResponse<String>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<String> response, int statusCode) {
                refundDeleteLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void setRefreshPage() {
        this.refreshPage.setValue(true);
    }

    public SingleLiveEvent<Boolean> getRefreshPage() {
        return refreshPage;
    }
}
