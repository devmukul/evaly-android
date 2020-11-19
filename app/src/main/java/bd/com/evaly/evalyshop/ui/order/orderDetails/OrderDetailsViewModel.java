package bd.com.evaly.evalyshop.ui.order.orderDetails;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.hero.DeliveryHeroResponse;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.models.order.updateAddress.UpdateOrderAddressRequest;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;

public class OrderDetailsViewModel extends ViewModel {

    private SingleLiveEvent<Boolean> refreshPage = new SingleLiveEvent<>();
    private MutableLiveData<CommonDataResponse<String>> refundEligibilityLiveData = new MutableLiveData<>();
    private MutableLiveData<CommonDataResponse<String>> refundDeleteLiveData = new MutableLiveData<>();
    private MutableLiveData<CommonDataResponse<OrderDetailsModel>> updateAddress = new MutableLiveData<>();
    protected MutableLiveData<DeliveryHeroResponse> deliveryHeroLiveData = new MutableLiveData<>();
    private String invoiceNo;

    @ViewModelInject
    public OrderDetailsViewModel(@Assisted SavedStateHandle savedStateHandle) {
        this.invoiceNo = savedStateHandle.get("invoice_no");
        getDeliveryHero();
    }

    private void getDeliveryHero() {

        OrderApiHelper.getDeliveryHero(invoiceNo, new ResponseListenerAuth<DeliveryHeroResponse, String>() {
            @Override
            public void onDataFetched(DeliveryHeroResponse response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    getDeliveryHero();
            }
        });

    }

    public LiveData<CommonDataResponse<OrderDetailsModel>> getUpdateAddress() {
        return updateAddress;
    }

    public LiveData<CommonDataResponse<String>> getRefundEligibilityLiveData() {
        return refundEligibilityLiveData;
    }

    public LiveData<CommonDataResponse<String>> getRefundDeleteLiveData() {
        return refundDeleteLiveData;
    }

    public void withdrawRefundRequest(String invoice) {
        AuthApiHelper.withdrawRefundRequest(invoice, new ResponseListenerAuth<CommonDataResponse, String>() {
            @Override
            public void onDataFetched(CommonDataResponse response, int statusCode) {
                ToastUtils.show(response.getMessage());
                refreshPage.setValue(true);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show(errorBody);
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    withdrawRefundRequest(invoice);
            }
        });
    }

    public void updateOrderAddress(UpdateOrderAddressRequest body) {
        OrderApiHelper.updateAddress(body, new ResponseListenerAuth<CommonDataResponse<OrderDetailsModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<OrderDetailsModel> response, int statusCode) {
                updateAddress.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
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
