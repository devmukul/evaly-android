package bd.com.evaly.evalyshop.ui.address;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.address.AddressListDao;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;

public class AddressViewModel extends ViewModel {

    private AddressListDao addressListDao;
    private LiveData<List<AddressResponse>> addressLiveData;

    @ViewModelInject
    public AddressViewModel(AddressListDao addressListDao) {
        this.addressListDao = addressListDao;
        this.addressLiveData = addressListDao.getAllLive();
        loadAddressListFromAPI();
    }

    public void loadAddressListFromAPI() {
        AuthApiHelper.getUserAddress(new ResponseListenerAuth<CommonDataResponse<List<AddressResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<AddressResponse>> response, int statusCode) {
                addressListDao.insertAll(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public LiveData<List<AddressResponse>> getAddressLiveData() {
        return addressLiveData;
    }
}
