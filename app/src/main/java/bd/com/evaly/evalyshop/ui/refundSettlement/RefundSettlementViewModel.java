package bd.com.evaly.evalyshop.ui.refundSettlement;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.models.refundSettlement.Bank;
import bd.com.evaly.evalyshop.models.refundSettlement.RefundSettlementResponse;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RefundSettlementViewModel extends ViewModel {

    RefundSettlementResponse accountsModel;
    MutableLiveData<String> bkashAccount = new MutableLiveData<>();
    MutableLiveData<String> nagadAccount = new MutableLiveData<>();
    MutableLiveData<Bank> bankAccount = new MutableLiveData<>();

    @Inject
    public RefundSettlementViewModel(SavedStateHandle arg) {

        Logger.d(new Gson().toJson(arg));

        if (arg != null && arg.contains("model")) {
            accountsModel = arg.get("model");
            if (accountsModel != null) {
                bkashAccount.setValue(accountsModel.getBkash());
                nagadAccount.setValue(accountsModel.getNagad());
                bankAccount.setValue(accountsModel.getBank());
            }
        }
    }
}
