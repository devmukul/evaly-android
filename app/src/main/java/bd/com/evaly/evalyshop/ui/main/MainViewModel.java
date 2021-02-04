package bd.com.evaly.evalyshop.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignProductCategoryResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;
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
    public SingleLiveEvent<Void> getRefreshCurrentFragment() {
        return refreshCurrentFragment;
    }
    public LiveData<Integer> wishListLiveCount;
    public LiveData<Integer> cartLiveCount;


    @Inject
    public MainViewModel(CartDao cartDao, WishListDao wishListDao){
        cartLiveCount = cartDao.getLiveCount();
        wishListLiveCount = wishListDao.getLiveCount();
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
