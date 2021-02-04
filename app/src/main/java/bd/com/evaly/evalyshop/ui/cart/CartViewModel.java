package bd.com.evaly.evalyshop.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
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
}
