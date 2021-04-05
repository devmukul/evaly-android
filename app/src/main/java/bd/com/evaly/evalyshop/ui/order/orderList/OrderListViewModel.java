package bd.com.evaly.evalyshop.ui.order.orderList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.order.OrderListItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OrderListViewModel extends ViewModel {

    private final SavedStateHandle savedStateHandle;
    protected MutableLiveData<List<OrderListItem>> liveData = new MutableLiveData<>();
    protected SingleLiveEvent<Boolean> hideLoading = new SingleLiveEvent<>();
    private List<OrderListItem> arrayList = new ArrayList<>();
    private int page = 1;
    private String statusType;
    private ApiRepository apiRepository;

    @Inject
    public OrderListViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository) {
        this.savedStateHandle = savedStateHandle;
        this.apiRepository = apiRepository;
        if (savedStateHandle.contains("type") && savedStateHandle.get("type") != null)
            statusType = savedStateHandle.get("type");
        else
            statusType = "all";

        getOrderData();
    }

    public void getOrderData() {
        apiRepository.getOrderList(CredentialManager.getToken(), page, statusType, new ResponseListenerAuth<CommonResultResponse<List<OrderListItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<OrderListItem>> response, int statusCode) {
                arrayList.addAll(response.getData());
                liveData.setValue(arrayList);
                page++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                hideLoading.setValue(true);
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    getOrderData();
            }
        });
    }

}
