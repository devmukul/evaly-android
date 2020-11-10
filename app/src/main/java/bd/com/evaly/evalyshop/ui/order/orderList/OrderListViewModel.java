package bd.com.evaly.evalyshop.ui.order.orderList;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.order.OrderListItem;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;

public class OrderListViewModel extends ViewModel {

    private final SavedStateHandle savedStateHandle;
    protected MutableLiveData<List<OrderListItem>> liveData = new MutableLiveData<>();
    private List<OrderListItem> arrayList = new ArrayList<>();
    private int page = 1;
    private String statusType;

    @ViewModelInject
    public OrderListViewModel(@Assisted SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
        if (savedStateHandle.contains("type") && savedStateHandle.get("type") != null)
            statusType = savedStateHandle.get("type");
        else
            statusType = "all";

        getOrderData();
    }

    public void getOrderData() {
        OrderApiHelper.getOrderList(CredentialManager.getToken(), page, statusType, new ResponseListenerAuth<CommonResultResponse<List<OrderListItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<OrderListItem>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveData.setValue(arrayList);
                page++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {


            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    getOrderData();
            }
        });
    }

}
