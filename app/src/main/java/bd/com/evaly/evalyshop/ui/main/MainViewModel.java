package bd.com.evaly.evalyshop.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignProductCategoryResponse;
import bd.com.evaly.evalyshop.models.cart.CartHolderModel;
import bd.com.evaly.evalyshop.models.refundSettlement.RefundSettlementResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private MutableLiveData<Boolean> drawerOnClick = new MutableLiveData<>();
    private MutableLiveData<Boolean> backOnClick = new MutableLiveData<>();
    private MutableLiveData<Boolean> updateNewsfeed = new MutableLiveData<>();
    public MutableLiveData<Boolean> registered = new MutableLiveData<>();
    public SubCampaignResponse selectedCampaignModel;
    public CampaignProductCategoryResponse selectedCampaignProductCategoryModel;
    public SingleLiveEvent<Void> campaignFilterUpdated = new SingleLiveEvent<>();
    public SingleLiveEvent<Void> refreshCurrentFragment = new SingleLiveEvent<>();
    public SingleLiveEvent<RefundSettlementResponse> refundSettlementUpdated = new SingleLiveEvent<>();
    public LiveData<Integer> wishListLiveCount;
    public LiveData<Integer> cartLiveCount;
    private CompositeDisposable compositeDisposable;
    private CartDao cartDao;
    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;

    @Inject
    public MainViewModel(CartDao cartDao, WishListDao wishListDao, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.cartDao = cartDao;
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
        cartLiveCount = cartDao.getLiveCount();
        wishListLiveCount = wishListDao.getLiveCount();
        compositeDisposable = new CompositeDisposable();
        getCartList();
    }

    public void getCartList() {
        if (preferenceRepository.getToken().equals(""))
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

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public SingleLiveEvent<Void> getRefreshCurrentFragment() {
        return refreshCurrentFragment;
    }


    public void setRefreshCurrentFragment() {
        this.refreshCurrentFragment.call();
    }


    public LiveData<Boolean> getDrawerOnClick() {
        return drawerOnClick;
    }

    public void setDrawerOnClick(boolean drawerOnClick) {
        this.drawerOnClick.setValue(drawerOnClick);
    }

    public LiveData<Boolean> getBackOnClick() {
        return backOnClick;
    }

    public void registerXMPP() {
        HashMap<String, String> data = new HashMap<>();
        data.put("password", preferenceRepository.getPassword());
        apiRepository.registerXMPP(data, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void setBackOnClick(boolean backOnClick) {
        this.backOnClick.setValue(backOnClick);
    }

    public LiveData<Boolean> getUpdateNewsfeed() {
        return updateNewsfeed;
    }

    public void setUpdateNewsfeed(boolean updateNewsfeed) {
        this.updateNewsfeed.setValue(updateNewsfeed);
    }
}
