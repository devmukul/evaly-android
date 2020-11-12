package bd.com.evaly.evalyshop.ui.order.orderList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.orderRequest.OrderRequestResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;

public class OrderListBaseViewModel extends ViewModel {

    private List<OrderRequestResponse> arrayList = new ArrayList<>();
    protected MutableLiveData<List<OrderRequestResponse>> liveData = new MutableLiveData<>();
    private int page;
    private int count;

    @Inject
    public OrderListBaseViewModel() {
        count = 0;
        page = 1;
        loadFromApi();
    }

    public int getCount() {
        return count;
    }

    public void loadFromApi() {
        OrderApiHelper.getOrderRequestList(page, new ResponseListenerAuth<CommonDataResponse<List<OrderRequestResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<OrderRequestResponse>> response, int statusCode) {
                count = response.getCount();
                arrayList.addAll(response.getData());
                liveData.setValue(arrayList);
                page++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

}
