package bd.com.evaly.evalyshop.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignProductCategoryResponse;
import bd.com.evaly.evalyshop.models.cart.CartHolderModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.CartApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

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
    public LiveData<Integer> wishListLiveCount;
    public LiveData<Integer> cartLiveCount;
    private CompositeDisposable compositeDisposable;
    private CartDao cartDao;

    @Inject
    public MainViewModel(CartDao cartDao, WishListDao wishListDao) {
        this.cartDao = cartDao;
        cartLiveCount = cartDao.getLiveCount();
        wishListLiveCount = wishListDao.getLiveCount();
        compositeDisposable = new CompositeDisposable();
        getCartList();
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
        data.put("password", CredentialManager.getPassword());
        AuthApiHelper.registerXMPP(data, new DataFetchingListener<Response<JsonObject>>() {
            @Override
            public void onDataFetched(Response<JsonObject> response) {

            }

            @Override
            public void onFailed(int status) {

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
