package bd.com.evaly.evalyshop.ui.address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.address.AddressListDao;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.location.LocationResponse;
import bd.com.evaly.evalyshop.models.profile.AddressRequest;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.models.profile.AddressWholeResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class AddressViewModel extends ViewModel {

    AddressListDao addressListDao;
    List<AddressResponse> addressList = new ArrayList<>();
    LiveData<List<AddressResponse>> addressLiveData;
    CompositeDisposable compositeDisposable;
    MutableLiveData<List<LocationResponse>> divisionLiveData = new MutableLiveData<>();
    MutableLiveData<List<LocationResponse>> cityLiveData = new MutableLiveData<>();
    MutableLiveData<List<LocationResponse>> areaLiveData = new MutableLiveData<>();

    MutableLiveData<LocationResponse> selectedDivisionLiveData = new MutableLiveData<>();
    MutableLiveData<LocationResponse> selectedCityLiveData = new MutableLiveData<>();
    MutableLiveData<LocationResponse> selectedAreaLiveData = new MutableLiveData<>();
    private ApiRepository apiRepository;

    @Inject
    public AddressViewModel(AddressListDao addressListDao, ApiRepository apiRepository) {
        this.addressListDao = addressListDao;
        this.apiRepository = apiRepository;
        compositeDisposable = new CompositeDisposable();
        addressLiveData = addressListDao.getAllLive();
        loadAddressList();
        loadLocationList(null, "division");
    }

    public void loadLocationList(String parent, String type) {
        apiRepository.getLocations(parent, new ResponseListenerAuth<CommonDataResponse<List<LocationResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<LocationResponse>> response, int statusCode) {
                if (type.equals("division"))
                    divisionLiveData.setValue(response.getData());
                else if (type.equals("city"))
                    cityLiveData.setValue(response.getData());
                else if (type.equals("area"))
                    areaLiveData.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void loadAddressList() {
        apiRepository.getUserAddress(new ResponseListenerAuth<CommonDataResponse<AddressWholeResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<AddressWholeResponse> response, int statusCode) {
                if (response.getData().getAddresses() != null)
                    compositeDisposable.add(addressListDao.insertAll(response.getData().getAddresses())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableCompletableObserver() {
                                @Override
                                public void onComplete() {
                                    List<String> list = new ArrayList<>();
                                    if (response.getData() != null && response.getData().getAddresses() != null) {
                                        for (AddressResponse item : response.getData().getAddresses())
                                            list.add(item.getId());
                                        removeOldAddresses(list);
                                    }
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

    private void removeOldAddresses(List<String> list) {
        compositeDisposable.add(addressListDao.deleteByIds(list)
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                }));
    }

    public void addAddress(AddressRequest item) {
        apiRepository.addAddress(item, new ResponseListenerAuth<CommonDataResponse<AddressResponse>, String>() {
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
        apiRepository.updateAddress(id, item, new ResponseListenerAuth<CommonDataResponse<AddressResponse>, String>() {
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
        apiRepository.removeAddress(id, new ResponseListenerAuth<CommonDataResponse, String>() {
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
