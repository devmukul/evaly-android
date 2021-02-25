package bd.com.evaly.evalyshop.ui.address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.address.AddressListDao;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.profile.AddressRequest;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class AddressViewModel extends ViewModel {

    AddressListDao addressListDao;
    List<AddressResponse> addressList = new ArrayList<>();
    LiveData<List<AddressResponse>> addressLiveData;
    CompositeDisposable compositeDisposable;

    @Inject
    public AddressViewModel(AddressListDao addressListDao) {
        this.addressListDao = addressListDao;
        compositeDisposable = new CompositeDisposable();
        addressLiveData = addressListDao.getAllLive();
        loadAddressList();
    }

    public void loadAddressList() {
        AuthApiHelper.getUserAddress(new ResponseListenerAuth<CommonDataResponse<List<AddressResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<AddressResponse>> response, int statusCode) {
                compositeDisposable.add(addressListDao.insertAll(response.getData())
                        .subscribeOn(Schedulers.io())
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

    public void addAddress(AddressRequest item) {
        AuthApiHelper.addAddress(item, new ResponseListenerAuth<CommonDataResponse<AddressResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<AddressResponse> response, int statusCode) {
                compositeDisposable.add(addressListDao.insert(response.getData())
                        .subscribeOn(Schedulers.io())
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

    public void editAddress(AddressRequest item, String id) {
        AuthApiHelper.updateAddress(id, item, new ResponseListenerAuth<CommonDataResponse<AddressResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<AddressResponse> response, int statusCode) {
                compositeDisposable.add(addressListDao.insert(response.getData())
                        .subscribeOn(Schedulers.io())
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

    public void saveAddress() {

    }

    public void deleteAddress(String id) {
        AuthApiHelper.removeAddress(id, new ResponseListenerAuth<CommonDataResponse, String>() {
            @Override
            public void onDataFetched(CommonDataResponse response, int statusCode) {
                loadAddressList();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
