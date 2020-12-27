package bd.com.evaly.evalyshop.ui.cart;

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
import io.reactivex.schedulers.Schedulers;

public class CartViewModel extends ViewModel {

    protected LiveData<List<CartEntity>> liveList;
    protected MutableLiveData<Boolean> isAllSelected = new MutableLiveData<>();
    private CartDao cartDao;
    private CompositeDisposable compositeDisposable;

    @ViewModelInject
    public CartViewModel(CartDao cartDao) {
        this.cartDao = cartDao;
        liveList = cartDao.getAllLive();
        compositeDisposable = new CompositeDisposable();
    }

    public void increaseQuantity(String id) {
        compositeDisposable.add(cartDao.rxIncreaseQuantity(id)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void decreaseQuantity(String id) {
        compositeDisposable.add(cartDao.rxDecreaseQuantity(id)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void delete(String id) {
        compositeDisposable.add(cartDao.rxDeleteBySlug(id)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void deleteAll() {
        compositeDisposable.add(cartDao.rxDeleteAll()
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void deleteSelected() {
        compositeDisposable.add(cartDao.rxDeleteSelected()
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void selectAll(boolean select) {
        compositeDisposable.add(cartDao.rxSelectAll(select)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void selectBySlug(String id, boolean select) {
        compositeDisposable.add(cartDao.rxSelectById(id, select)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }


    public void placeOrder(JsonObject payload) {

        OrderApiHelper.placeOrder(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

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
