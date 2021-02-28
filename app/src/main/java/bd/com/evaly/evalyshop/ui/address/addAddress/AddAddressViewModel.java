package bd.com.evaly.evalyshop.ui.address.addAddress;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.address.AddressListDao;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.location.LocationResponse;
import bd.com.evaly.evalyshop.models.profile.AddressRequest;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.models.profile.AddressWholeResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.LocationApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class AddAddressViewModel extends ViewModel {

    private Boolean isEdit = false;
    AddressListDao addressListDao;
    CompositeDisposable compositeDisposable;
    MutableLiveData<List<LocationResponse>> divisionLiveData = new MutableLiveData<>();
    MutableLiveData<List<LocationResponse>> cityLiveData = new MutableLiveData<>();
    MutableLiveData<List<LocationResponse>> areaLiveData = new MutableLiveData<>();
    MutableLiveData<AddressResponse> modelLiveData = new MutableLiveData<>();

    MutableLiveData<LocationResponse> selectedDivisionLiveData = new MutableLiveData<>();
    MutableLiveData<LocationResponse> selectedCityLiveData = new MutableLiveData<>();
    MutableLiveData<LocationResponse> selectedAreaLiveData = new MutableLiveData<>();
    SingleLiveEvent<Void> dismissBottomSheet = new SingleLiveEvent<>();

    @Inject
    public AddAddressViewModel(AddressListDao addressListDao, SavedStateHandle bundle) {
        this.addressListDao = addressListDao;
        modelLiveData.setValue(bundle.get("model"));
        isEdit = bundle.get("is_edit");
        compositeDisposable = new CompositeDisposable();
        loadLocationList(null, "division");
    }

    public boolean isEdit() {
        if (isEdit == null)
            return false;
        return isEdit;
    }

    public void loadLocationList(String parent, String type) {
        LocationApiHelper.getLocations(parent, new ResponseListenerAuth<CommonDataResponse<List<LocationResponse>>, String>() {
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
        AuthApiHelper.getUserAddress(new ResponseListenerAuth<CommonDataResponse<AddressWholeResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<AddressWholeResponse> response, int statusCode) {
                if (response.getData().getAddresses() != null)
                    compositeDisposable.add(addressListDao.insertAll(response.getData().getAddresses())
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
                loadAddressList();
                dismissBottomSheet.call();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void editAddress(AddressRequest item) {
        if (modelLiveData.getValue() == null)
            return;
        AuthApiHelper.updateAddress(modelLiveData.getValue().getId(), item, new ResponseListenerAuth<CommonDataResponse<AddressResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<AddressResponse> response, int statusCode) {
                loadAddressList();
                dismissBottomSheet.call();
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

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
