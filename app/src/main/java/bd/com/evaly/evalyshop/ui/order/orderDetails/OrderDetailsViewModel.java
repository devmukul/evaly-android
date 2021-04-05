package bd.com.evaly.evalyshop.ui.order.orderDetails;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.hero.DeliveryHeroResponse;
import bd.com.evaly.evalyshop.models.order.OrderStatus;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.models.order.updateAddress.UpdateOrderAddressRequest;
import bd.com.evaly.evalyshop.models.pay.BalanceResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OrderDetailsViewModel extends ViewModel {

    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;
    protected MutableLiveData<DeliveryHeroResponse> deliveryHeroLiveData = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse> confirmDeliveryLiveData = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse> cancelOrderLiveData = new MutableLiveData<>();
    protected MutableLiveData<OrderDetailsModel> orderDetailsLiveData = new MutableLiveData<>();
    protected MutableLiveData<List<OrderStatus>> orderStatusListLiveData = new MutableLiveData<>();
    protected MutableLiveData<String> deliveryStatusUpdateLiveData = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> refreshPage = new SingleLiveEvent<>();
    private MutableLiveData<CommonDataResponse<String>> refundEligibilityLiveData = new MutableLiveData<>();
    private MutableLiveData<CommonDataResponse<String>> refundDeleteLiveData = new MutableLiveData<>();
    private MutableLiveData<CommonDataResponse<OrderDetailsModel>> updateAddress = new MutableLiveData<>();
    protected MutableLiveData<BalanceResponse> balanceLiveData = new MutableLiveData<>();
    private String invoiceNo;

    @Inject
    public OrderDetailsViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.invoiceNo = savedStateHandle.get("orderID");
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
        getOrderDetails();
        getDeliveryHero();
        getOrderHistory();
        if (CredentialManager.getBalance() == 0)
            updateBalance();
    }

    public void refresh() {
        getOrderDetails();
        getDeliveryHero();
        getOrderHistory();
    }

    public void updateBalance() {

        apiRepository.getBalance(CredentialManager.getToken(), CredentialManager.getUserName(), new ResponseListenerAuth<CommonDataResponse<BalanceResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<BalanceResponse> response, int statusCode) {
                balanceLiveData.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void updateProductDeliveryStatus(HashMap<String, String> data) {
        apiRepository.updateProductStatus(data, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                deliveryStatusUpdateLiveData.setValue(response.get("message").getAsString());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                try {
                    CommonDataResponse data = new Gson().fromJson(errorBody, CommonDataResponse.class);
                    deliveryStatusUpdateLiveData.setValue(data.getMessage());
                } catch (Exception e) {
                    deliveryStatusUpdateLiveData.setValue("Couldn't update order status");
                }
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    public void cancelOrder(String reason) {
        apiRepository.cancelOrder(CredentialManager.getToken(), invoiceNo, reason, new ResponseListenerAuth<CommonDataResponse, String>() {
            @Override
            public void onDataFetched(CommonDataResponse response, int statusCode) {
                refresh();
                cancelOrderLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show("Can't cancel this order!");
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void getOrderHistory() {

        apiRepository.getOrderHistories(CredentialManager.getToken(), invoiceNo, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                List<OrderStatus> orderStatuses = new ArrayList<>();
                JsonArray list = response.getAsJsonObject("data").getAsJsonArray("histories");

                for (int i = 0; i < list.size(); i++) {
                    JsonObject jsonObject = list.get(i).getAsJsonObject();
                    orderStatuses.add(new OrderStatus(
                            jsonObject.get("date").getAsString(),
                            jsonObject.get("order_status").getAsString(),
                            jsonObject.get("note").getAsString())
                    );
                }
                orderStatusListLiveData.setValue(orderStatuses);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void getOrderDetails() {

        apiRepository.getOrderDetails(CredentialManager.getToken(), invoiceNo, new ResponseListenerAuth<OrderDetailsModel, String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataFetched(OrderDetailsModel response, int statusCode) {
                orderDetailsLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show("Error occurred!");
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void confirmDelivery() {
        apiRepository.confirmDelivery(CredentialManager.getToken(), invoiceNo, new ResponseListenerAuth<CommonDataResponse, String>() {
            @Override
            public void onDataFetched(CommonDataResponse response, int statusCode) {
                confirmDeliveryLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                confirmDeliveryLiveData.setValue(null);
                ToastUtils.show("Error occurred! Try again later");
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    confirmDelivery();
            }
        });
    }

    private void getDeliveryHero() {

        apiRepository.getDeliveryHero(invoiceNo, new ResponseListenerAuth<DeliveryHeroResponse, String>() {
            @Override
            public void onDataFetched(DeliveryHeroResponse response, int statusCode) {
                deliveryHeroLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                deliveryHeroLiveData.setValue(null);
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
        apiRepository.withdrawRefundRequest(invoice, new ResponseListenerAuth<CommonDataResponse, String>() {
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
        apiRepository.updateAddress(body, new ResponseListenerAuth<CommonDataResponse<OrderDetailsModel>, String>() {
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
        apiRepository.checkRefundEligibility(invoice, new ResponseListenerAuth<CommonDataResponse<String>, String>() {
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
        apiRepository.deleteRefundTransaction(invoice, new ResponseListenerAuth<CommonDataResponse<String>, String>() {
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
