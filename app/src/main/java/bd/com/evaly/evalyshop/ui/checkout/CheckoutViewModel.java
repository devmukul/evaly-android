package bd.com.evaly.evalyshop.ui.checkout;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.util.ToastUtils;
import io.reactivex.disposables.CompositeDisposable;

public class CheckoutViewModel extends ViewModel {

    protected LiveData<List<CartEntity>> liveList;
    protected MutableLiveData<JsonObject> orderPlacedLiveData = new MutableLiveData<>();
    private CartDao cartDao;
    private CompositeDisposable compositeDisposable;

    @ViewModelInject
    public CheckoutViewModel(CartDao cartDao) {
        this.cartDao = cartDao;
        liveList = cartDao.getAllSelectedLive();
        compositeDisposable = new CompositeDisposable();
    }

    public void placeOrder(JsonObject payload) {

        OrderApiHelper.placeOrder(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
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
