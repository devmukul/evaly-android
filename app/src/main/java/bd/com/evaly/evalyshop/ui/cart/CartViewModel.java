package bd.com.evaly.evalyshop.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.remote.ApiRepository;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.cart.Cart;
import bd.com.evaly.evalyshop.models.cart.CartHolderModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public final class CartViewModel extends ViewModel {

    protected LiveData<List<CartEntity>> liveList;
    private CartDao cartDao;
    private CompositeDisposable compositeDisposable;
    private ApiRepository apiRepository;

    @Inject
    public CartViewModel(CartDao cartDao, ApiRepository apiRepository) {
        this.cartDao = cartDao;
        this.apiRepository = apiRepository;
        getCartList();
        liveList = cartDao.getAllLive();
        compositeDisposable = new CompositeDisposable();
    }

    public int getItemCount() {
        if (liveList == null || liveList.getValue() == null)
            return 0;

        return liveList.getValue().size();
    }

    public int getSelectedItemCount() {
        if (liveList == null || liveList.getValue() == null)
            return 0;
        int count = 0;
        for (CartEntity item : liveList.getValue())
            if (item.isSelected())
                count++;

        return count;
    }

    public void getCartList() {
        if (CredentialManager.getToken().equals(""))
            return;

        apiRepository.getCartList(new ResponseListenerAuth<CommonDataResponse<CartHolderModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<CartHolderModel> response, int statusCode) {

                compositeDisposable.add(cartDao.insertAllIgnore(response.getData().getCart().getItems())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                List<String> slugs = new ArrayList<>();
                                for (CartEntity item : response.getData().getCart().getItems())
                                    slugs.add(item.getProductID());
                                deleteOld(slugs);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                            }
                        }));
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void deleteOld(List<String> list) {
        compositeDisposable.add(cartDao.deleteOldRx(list)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }


    public void insert(CartEntity cartEntity) {
        cartDao.checkExistsRx(cartEntity.getProductID())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<CartEntity>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<CartEntity> cartEntities) {
                        Logger.e("checked exits");
                        if (cartEntities.size() == 0)
                            insertCompletable(cartEntity);
                        else
                            increaseQuantity(cartEntity.getProductID());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    private void insertCompletable(CartEntity entity) {

        compositeDisposable.add(cartDao.insertRx(entity)
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncWithLocalToRemote();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                }));

    }

    public void syncWithLocalToRemote() {
        cartDao.getListRx()
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<CartEntity>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<CartEntity> cartEntities) {
                        updateToRemote(cartEntities);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    public void updateToRemote(List<CartEntity> list) {
        CartHolderModel body = new CartHolderModel();
        body.setContext("evaly");
        Cart cart = new Cart();
        cart.setItems(list);
        body.setCart(cart);
        apiRepository.syncCartList(body, new ResponseListenerAuth<CommonDataResponse<CartHolderModel>, String>() {
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
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncWithLocalToRemote();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                }));
    }

    public void deleteAll() {
        compositeDisposable.add(cartDao.rxDeleteAll()
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncWithLocalToRemote();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                }));
    }

    public void deleteSelected() {
        compositeDisposable.add(cartDao.rxDeleteSelected()
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        syncWithLocalToRemote();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                }));
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
