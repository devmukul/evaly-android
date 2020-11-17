package bd.com.evaly.evalyshop.ui.address;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.address.AddressListDao;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.profile.AddressRequest;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
                List<Integer> list = new ArrayList<>();
                for (AddressResponse item : response.getData())
                    list.add(item.getId());
                Completable deleteAll = addressListDao.deleteByIds(list);
                Completable insertAll = addressListDao.insertAll(response.getData());
                compositeDisposable.add(deleteAll
                        .andThen(Completable.fromAction(() -> Logger.d("Delete finished")))
                        .andThen(insertAll)
                        .andThen(Completable.fromAction(() -> Logger.d("Insert finished")))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.single())
                        .subscribe());
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

    public void deleteAddress(int id) {
        AuthApiHelper.removeAddress(id, new ResponseListenerAuth<CommonDataResponse, String>() {
            @Override
            public void onDataFetched(CommonDataResponse response, int statusCode) {
                if (response.getSuccess())
                    compositeDisposable.add(addressListDao.deleteById(id).subscribeOn(Schedulers.io()).subscribe());
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
