package bd.com.evaly.evalyshop.ui.checkout;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.order.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CheckoutViewModel extends ViewModel {

    protected LiveData<List<CartEntity>> liveList = new MutableLiveData<>();
    protected SingleLiveEvent<Boolean> errorOrder = new SingleLiveEvent<>();
    protected MutableLiveData<CommonDataResponse<List<JsonObject>>> orderPlacedLiveData = new MutableLiveData<>();
    private CartDao cartDao;
    private CompositeDisposable compositeDisposable;

    @ViewModelInject
    public CheckoutViewModel(CartDao cartDao, @Assisted SavedStateHandle savedStateHandle) {
        this.cartDao = cartDao;

        if (savedStateHandle != null && savedStateHandle.contains("model")) {
            List<CartEntity> list = new ArrayList<>();
            list.add(savedStateHandle.get("model"));
            MutableLiveData<List<CartEntity>> tempList = new MutableLiveData<>();
            tempList.setValue(list);
            liveList = tempList;
        }else
            liveList = cartDao.getAllSelectedLive();

        compositeDisposable = new CompositeDisposable();
    }


    public void deleteSelected() {
        compositeDisposable.add(cartDao.rxDeleteSelected()
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void placeOrder(PlaceOrderItem payload) {

        OrderApiHelper.placeOrder(payload, new ResponseListenerAuth<CommonDataResponse<List<JsonObject>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<JsonObject>> response, int statusCode) {
                orderPlacedLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                errorOrder.setValue(true);
                ToastUtils.show("Couldn't place order, try again later.");
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    placeOrder(payload);
            }
        });
    }

}
