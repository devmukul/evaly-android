package bd.com.evaly.evalyshop.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Boolean> drawerOnClick = new MutableLiveData<>();
    private MutableLiveData<Boolean> backOnClick = new MutableLiveData<>();
    private MutableLiveData<Boolean> updateNewsfeed = new MutableLiveData<>();
    public MutableLiveData<Boolean> registered = new MutableLiveData<>();
    private SingleLiveEvent<SubCampaignResponse> campaignOnClick = new SingleLiveEvent<>();

    public void setCampaignOnClick(SubCampaignResponse model) {
        this.campaignOnClick.setValue(model);
    }

    public SingleLiveEvent<SubCampaignResponse> getCampaignOnClick() {
        return campaignOnClick;
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
