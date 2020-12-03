package bd.com.evaly.evalyshop.ui.address;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.address.AddressListDao;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.user.AddressItem;
import bd.com.evaly.evalyshop.models.user.Addresses;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
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

    public List<AddressItem> getAddressList() {
        return addressList;
    }

    public void addAddress(AddressItem item) {
        addressList.add(item);
    }

    public void editAddress(AddressItem item, int position){
        addressList.set(position, item);
    }

    public void saveAddress() {

        Addresses addresses = new Addresses();
        addresses.setData(addressList);
        JsonObject jsonObject = new Gson().toJsonTree(addresses).getAsJsonObject();
        JsonObject requestBody = new JsonObject();
        requestBody.add("addresses", jsonObject);

        AuthApiHelper.setUserData(CredentialManager.getToken(), requestBody, new ResponseListenerAuth<CommonDataResponse<UserModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserModel> response, int statusCode) {
                CredentialManager.saveUserData(response.getData());
                loadAddressList();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    saveAddress();
            }
        });
    }

    public void deleteAddress(AddressItem item) {
        addressList.remove(item);
        saveAddress();
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
