package bd.com.evaly.evalyshop.ui.address.addAddress;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.address.AddressListDao;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.catalog.location.LocationResponse;
import bd.com.evaly.evalyshop.models.profile.AddressRequest;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.models.profile.AddressWholeResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
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
    private ApiRepository apiRepository;

    @Inject
    public AddAddressViewModel(AddressListDao addressListDao, SavedStateHandle bundle, ApiRepository apiRepository) {
        this.addressListDao = addressListDao;
        this.apiRepository = apiRepository;
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
        apiRepository.getLocations(parent, new ResponseListener<CommonDataResponse<List<LocationResponse>>, String>() {
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

        });
    }

    public void loadAddressList() {
        apiRepository.getUserAddress(new ResponseListener<CommonDataResponse<AddressWholeResponse>, String>() {
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

        });
    }

    public void addAddress(AddressRequest item) {
        apiRepository.addAddress(item, new ResponseListener<CommonDataResponse<AddressResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<AddressResponse> response, int statusCode) {
                loadAddressList();
                dismissBottomSheet.call();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void editAddress(AddressRequest item) {
        if (modelLiveData.getValue() == null)
            return;
        apiRepository.updateAddress(modelLiveData.getValue().getId(), item, new ResponseListener<CommonDataResponse<AddressResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<AddressResponse> response, int statusCode) {
                loadAddressList();
                dismissBottomSheet.call();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

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
