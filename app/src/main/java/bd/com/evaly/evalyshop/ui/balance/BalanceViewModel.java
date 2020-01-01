package bd.com.evaly.evalyshop.ui.balance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;

public class BalanceViewModel extends ViewModel{


    private MutableLiveData<BalanceModel> data = new MutableLiveData<>();

    public LiveData<BalanceModel> getData(){
        return data;
    }


    public void updateBalance(){

        AuthApiHelper.getUserInfoPay(CredentialManager.getToken(), CredentialManager.getUserName(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                JsonObject obj = response.getAsJsonObject("data");

                BalanceModel model = new BalanceModel();
                model.setBalance(obj.get("balance").getAsDouble());
                model.setHolding_balance(obj.get("holding_balance").getAsDouble());
                model.setGift_card_balance(obj.get("gift_card_balance").getAsDouble());
                model.setCashback_balance(obj.get("cashback_balance").getAsDouble());

                data.setValue(model);

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });


    }

}
