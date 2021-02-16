package bd.com.evaly.evalyshop.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.orhanobut.logger.Logger;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.cart.CartHolderModel;
import bd.com.evaly.evalyshop.rest.apiHelper.CartApiHelper;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public final class CartViewModel extends ViewModel {

    protected LiveData<List<CartEntity>> liveList;
    private CartDao cartDao;
    private CompositeDisposable compositeDisposable;

    @Inject
    public CartViewModel(CartDao cartDao) {
        this.cartDao = cartDao;
        getCartList();
        liveList = cartDao.getAllLive();
        compositeDisposable = new CompositeDisposable();
    }

    public void getCartList() {
        if (CredentialManager.getToken().equals(""))
            return;

        CartApiHelper.getCartList(new ResponseListenerAuth<CommonDataResponse<CartHolderModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<CartHolderModel> response, int statusCode) {
                if (response.getData() == null || response.getData().getCart() == null || response.getData().getCart().getItems() == null)
                    return;
                compositeDisposable.add(cartDao.insertAllIgnore(response.getData().getCart().getItems())
                        .subscribeOn(Schedulers.io())
                        .onErrorComplete(throwable -> {
                            Logger.e(throwable.getMessage());
                            return false;
                        })
                        .subscribe());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void syncCartList() {
        CartHolderModel body = new CartHolderModel();
        CartApiHelper.syncCartList(body, new ResponseListenerAuth<CommonDataResponse<CartHolderModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<CartHolderModel> response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

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
}
