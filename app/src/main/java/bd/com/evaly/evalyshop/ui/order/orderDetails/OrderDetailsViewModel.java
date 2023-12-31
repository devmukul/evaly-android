package bd.com.evaly.evalyshop.ui.order.orderDetails;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.hero.DeliveryHeroResponse;
import bd.com.evaly.evalyshop.models.order.OrderStatus;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.models.order.updateAddress.UpdateOrderAddressRequest;
import bd.com.evaly.evalyshop.models.pay.BalanceResponse;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigBaseUrls;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OrderDetailsViewModel extends ViewModel {

    protected MutableLiveData<DeliveryHeroResponse> deliveryHeroLiveData = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse> confirmDeliveryLiveData = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse> cancelOrderLiveData = new MutableLiveData<>();
    protected MutableLiveData<OrderDetailsModel> orderDetailsLiveData = new MutableLiveData<>();
    protected MutableLiveData<List<OrderStatus>> orderStatusListLiveData = new MutableLiveData<>();
    protected MutableLiveData<String> deliveryStatusUpdateLiveData = new MutableLiveData<>();
    protected MutableLiveData<BalanceResponse> balanceLiveData = new MutableLiveData<>();
    private PreferenceRepository preferenceRepository;
    private ApiRepository apiRepository;
    private SingleLiveEvent<Boolean> refreshPage = new SingleLiveEvent<>();
    private MutableLiveData<CommonDataResponse<String>> refundEligibilityLiveData = new MutableLiveData<>();
    private MutableLiveData<CommonDataResponse<String>> refundDeleteLiveData = new MutableLiveData<>();
    private MutableLiveData<CommonDataResponse<OrderDetailsModel>> updateAddress = new MutableLiveData<>();
    private String invoiceNo;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    @Inject
    public OrderDetailsViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository, PreferenceRepository preferenceRepository, FirebaseRemoteConfig firebaseRemoteConfig) {
        this.invoiceNo = savedStateHandle.get("orderID");
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
        this.firebaseRemoteConfig = firebaseRemoteConfig;
        getOrderDetails();
        getDeliveryHero();
        getOrderHistory();
        if (preferenceRepository.getBalance() == 0)
            updateBalance();
    }

    public void refresh() {
        getOrderDetails();
        getDeliveryHero();
        getOrderHistory();
    }

    public void updateBalance() {

        String url = null;

        RemoteConfigBaseUrls baseUrls = new Gson().fromJson(firebaseRemoteConfig.getValue("temp_urls").asString(), RemoteConfigBaseUrls.class);

        if (baseUrls == null)
            return;

        if (BuildConfig.DEBUG)
            url = baseUrls.getDevBalanceUrl();
        else
            url = baseUrls.getProdBalanceUrl();

        if (url == null)
            return;

        apiRepository.getBalance(preferenceRepository.getToken(), preferenceRepository.getUserName(), url, new ResponseListener<CommonDataResponse<BalanceResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<BalanceResponse> response, int statusCode) {
                balanceLiveData.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void updateProductDeliveryStatus(HashMap<String, String> data) {
        apiRepository.updateProductStatus(data, new ResponseListener<JsonObject, String>() {
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

        });

    }

    public void cancelOrder(String reason) {
        apiRepository.cancelOrder(preferenceRepository.getToken(), invoiceNo, reason, new ResponseListener<CommonDataResponse, String>() {
            @Override
            public void onDataFetched(CommonDataResponse response, int statusCode) {
                refresh();
                cancelOrderLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show("Can't cancel this order!");
            }

        });
    }


    public void getOrderHistory() {

        apiRepository.getOrderHistories(preferenceRepository.getToken(), invoiceNo, new ResponseListener<JsonObject, String>() {
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

        });
    }

    public void getOrderDetails() {

        apiRepository.getOrderDetails(preferenceRepository.getToken(), invoiceNo, new ResponseListener<OrderDetailsModel, String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataFetched(OrderDetailsModel response, int statusCode) {
                orderDetailsLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show("Error occurred!");
            }

        });
    }

    public void confirmDelivery() {
        apiRepository.confirmDelivery(preferenceRepository.getToken(), invoiceNo, new ResponseListener<CommonDataResponse, String>() {
            @Override
            public void onDataFetched(CommonDataResponse response, int statusCode) {
                confirmDeliveryLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                confirmDeliveryLiveData.setValue(null);
                ToastUtils.show("Error occurred! Try again later");
            }

        });
    }

    private void getDeliveryHero() {

        apiRepository.getDeliveryHero(invoiceNo, new ResponseListener<DeliveryHeroResponse, String>() {
            @Override
            public void onDataFetched(DeliveryHeroResponse response, int statusCode) {
                deliveryHeroLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                deliveryHeroLiveData.setValue(null);
            }

        });

    }

    public void withdrawRefundRequest(String invoice) {

        String url = null;

        RemoteConfigBaseUrls baseUrls = new Gson().fromJson(firebaseRemoteConfig.getValue("temp_urls").asString(), RemoteConfigBaseUrls.class);

        if (baseUrls == null)
            return;

        if (BuildConfig.DEBUG)
            url = baseUrls.getDevRefundRequestWithdrawUrl();
        else
            url = baseUrls.getProdRefundRequestWithdrawUrl();

        if (url == null)
            return;

        apiRepository.withdrawRefundRequest(url, invoice, new ResponseListener<CommonDataResponse, String>() {
            @Override
            public void onDataFetched(CommonDataResponse response, int statusCode) {
                ToastUtils.show(response.getMessage());
                refreshPage.setValue(true);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show(errorBody);
            }

        });
    }

    public void updateOrderAddress(UpdateOrderAddressRequest body) {
        apiRepository.updateAddress(body, new ResponseListener<CommonDataResponse<OrderDetailsModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<OrderDetailsModel> response, int statusCode) {
                updateAddress.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

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

    public void setRefreshPage() {
        this.refreshPage.setValue(true);
    }

    public SingleLiveEvent<Boolean> getRefreshPage() {
        return refreshPage;
    }
}
