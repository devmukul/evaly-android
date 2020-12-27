package bd.com.evaly.evalyshop.ui.checkout;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.models.order.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.util.ToastUtils;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CheckoutViewModel extends ViewModel {

    protected LiveData<List<CartEntity>> liveList;
    protected MutableLiveData<CommonDataResponse<List<OrderDetailsModel>>> orderPlacedLiveData = new MutableLiveData<>();
    private CartDao cartDao;
    private CompositeDisposable compositeDisposable;

    @ViewModelInject
    public CheckoutViewModel(CartDao cartDao) {
        this.cartDao = cartDao;
        liveList = cartDao.getAllSelectedLive();
        compositeDisposable = new CompositeDisposable();
    }


    public void deleteSelected() {
        compositeDisposable.add(cartDao.rxDeleteSelected()
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void placeOrder(PlaceOrderItem payload) {

        OrderApiHelper.placeOrder(payload, new ResponseListenerAuth<CommonDataResponse<List<OrderDetailsModel>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<OrderDetailsModel>> response, int statusCode) {
                orderPlacedLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
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
