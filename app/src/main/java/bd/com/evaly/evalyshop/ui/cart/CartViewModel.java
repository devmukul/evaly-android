package bd.com.evaly.evalyshop.ui.cart;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CartViewModel extends ViewModel {

    protected LiveData<List<CartEntity>> liveList;
    private CartDao cartDao;
    private CompositeDisposable compositeDisposable;

    @ViewModelInject
    public CartViewModel(CartDao cartDao) {
        this.cartDao = cartDao;
        liveList = cartDao.getAllLive();
        compositeDisposable = new CompositeDisposable();
    }

    public void increaseQuantity(String slug) {
        compositeDisposable.add(cartDao.rxIncreaseQuantity(slug)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void decreaseQuantity(String slug) {
        compositeDisposable.add(cartDao.rxDecreaseQuantity(slug)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void delete(String slug) {
        compositeDisposable.add(cartDao.rxDeleteBySlug(slug)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void deleteAll() {
        compositeDisposable.add(cartDao.rxDeleteAll()
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
