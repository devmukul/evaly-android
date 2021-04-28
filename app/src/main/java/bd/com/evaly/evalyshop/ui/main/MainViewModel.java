package bd.com.evaly.evalyshop.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignProductCategoryResponse;
import bd.com.evaly.evalyshop.models.cart.CartHolderModel;
import bd.com.evaly.evalyshop.models.refundSettlement.RefundSettlementResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class MainViewModel extends ViewModel {

    public SingleLiveEvent<Void> transparentStatusBarInitiated = new SingleLiveEvent<>();
    public SubCampaignResponse selectedCampaignModel;
    public CampaignProductCategoryResponse selectedCampaignProductCategoryModel;
    public SingleLiveEvent<Void> campaignFilterUpdated = new SingleLiveEvent<>();
    public SingleLiveEvent<Void> refreshCurrentFragment = new SingleLiveEvent<>();
    public SingleLiveEvent<RefundSettlementResponse> refundSettlementUpdated = new SingleLiveEvent<>();
    public LiveData<Integer> wishListLiveCount;
    public LiveData<Integer> cartLiveCount;
    protected SingleLiveEvent<Boolean> onLogoutResponse = new SingleLiveEvent<>();
    private final MutableLiveData<Boolean> drawerOnClick = new MutableLiveData<>();
    private final MutableLiveData<Boolean> backOnClick = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateNewsfeed = new MutableLiveData<>();
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


    public void onLogoutFromServer() {
        apiRepository.logout(new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                clearUserData();
                onLogoutResponse.setValue(true);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                clearUserData();
                onLogoutResponse.setValue(true);
            }

        });
    }

    private void clearUserData() {
        try {
            String email = preferenceRepository.getUserName();
            String strNew = email.replaceAll("[^A-Za-z0-9]", "");
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.BUILD + "_" + strNew);
        } catch (Exception e) {
            e.printStackTrace();
        }
        preferenceRepository.clearAll();
    }

    public void getCartList() {
        if (preferenceRepository.getToken().equals(""))
            return;

        apiRepository.getCartList(new ResponseListener<CommonDataResponse<CartHolderModel>, String>() {
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

    public void setBackOnClick(boolean backOnClick) {
        this.backOnClick.setValue(backOnClick);
    }

    public void registerXMPP() {
        HashMap<String, String> data = new HashMap<>();
        data.put("password", preferenceRepository.getPassword());
        apiRepository.registerXMPP(data, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public LiveData<Boolean> getUpdateNewsfeed() {
        return updateNewsfeed;
    }

    public void setUpdateNewsfeed(boolean updateNewsfeed) {
        this.updateNewsfeed.setValue(updateNewsfeed);
    }
}
