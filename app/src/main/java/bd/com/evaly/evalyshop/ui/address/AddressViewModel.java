package bd.com.evaly.evalyshop.ui.address;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.address.AddressListDao;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.profile.AddressRequest;
import bd.com.evaly.evalyshop.models.user.AddressItem;
import bd.com.evaly.evalyshop.models.user.Addresses;
import io.reactivex.disposables.CompositeDisposable;

public class AddressViewModel extends ViewModel {

    private AddressListDao addressListDao;
    private List<AddressItem> addressList = new ArrayList<>();
    private MutableLiveData<List<AddressItem>> addressLiveData = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable;

    @ViewModelInject
    public AddressViewModel(AddressListDao addressListDao) {
        this.addressListDao = addressListDao;
        compositeDisposable = new CompositeDisposable();
        loadAddressList();
    }

    public void loadAddressList() {
        Addresses addresses = CredentialManager.getUserData().getAddresses();
        if (addresses != null && addresses.getData() != null)
            addressList = addresses.getData();
        addressLiveData.setValue(addressList);
    }

    public void deleteAddress(int id) {

    }

    public void updateAddress(AddressRequest body) {

    }

    public LiveData<List<AddressItem>> getAddressLiveData() {
        return addressLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
