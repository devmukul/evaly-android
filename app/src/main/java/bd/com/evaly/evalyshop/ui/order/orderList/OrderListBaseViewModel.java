package bd.com.evaly.evalyshop.ui.order.orderList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.orderRequest.OrderRequestResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OrderListBaseViewModel extends ViewModel {

    private List<OrderRequestResponse> arrayList = new ArrayList<>();
    public MutableLiveData<List<OrderRequestResponse>> liveData = new MutableLiveData<>();
    protected SingleLiveEvent<Void> logoutLiveData = new SingleLiveEvent<>();
    private int page;
    private int count;
    private ApiRepository apiRepository;

    @Inject
    public OrderListBaseViewModel(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
        count = 0;
        page = 1;
        loadFromApi();
    }

    public int getCount() {
        return count;
    }

    public void loadFromApi() {
        apiRepository.getOrderRequestList(page, new ResponseListener<CommonDataResponse<List<OrderRequestResponse>>, String>() {
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

        });
    }

}
