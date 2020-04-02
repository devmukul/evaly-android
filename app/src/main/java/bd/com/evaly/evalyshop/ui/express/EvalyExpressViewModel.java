package bd.com.evaly.evalyshop.ui.express;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.shop.shopGroup.ShopGroupResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.ShopApiHelper;

public class EvalyExpressViewModel extends ViewModel {
    private MutableLiveData<ShopGroupResponse> liveData = new MutableLiveData<>();

    public EvalyExpressViewModel() {
        super();
        getShops("evaly-express", 1);
    }

    public LiveData<ShopGroupResponse> getLiveData() {
        return liveData;
    }

    public void getShops(String group, int page){

        ShopApiHelper.getShopsByGroup(group, page, 100, new ResponseListenerAuth<ShopGroupResponse, String>() {
            @Override
            public void onDataFetched(ShopGroupResponse response, int statusCode) {
                liveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                liveData.setValue(new ShopGroupResponse());

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void clear(){
        liveData.setValue(null);
    }

}
