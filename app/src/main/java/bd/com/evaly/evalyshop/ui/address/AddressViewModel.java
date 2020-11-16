package bd.com.evaly.evalyshop.ui.address;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.address.AddressListDao;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.profile.AddressRequest;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AddressViewModel extends ViewModel {

    private AddressListDao addressListDao;
    private LiveData<List<AddressResponse>> addressLiveData;
    private CompositeDisposable compositeDisposable;

    @ViewModelInject
    public AddressViewModel(AddressListDao addressListDao) {
        this.addressListDao = addressListDao;
        this.addressLiveData = addressListDao.getAllLive();
        compositeDisposable = new CompositeDisposable();
        loadAddressListFromAPI();
    }

    public void loadAddressListFromAPI() {
        AuthApiHelper.getUserAddress(new ResponseListenerAuth<CommonDataResponse<List<AddressResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<AddressResponse>> response, int statusCode) {
                compositeDisposable.add(addressListDao
                        .insertAll(response.getData())
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                );
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    loadAddressListFromAPI();

            }
        });
    }

    public void deleteAddress(int id){
        AuthApiHelper.removeAddress(id, new ResponseListenerAuth<CommonDataResponse, String>() {
            @Override
            public void onDataFetched(CommonDataResponse response, int statusCode) {
               // if (response.getSuccess())
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void updateAddress(AddressRequest body) {
        AuthApiHelper.addAddress(body, new ResponseListenerAuth<CommonDataResponse<AddressResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<AddressResponse> response, int statusCode) {
                compositeDisposable.add(addressListDao
                        .insert(response.getData())
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                );
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

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
