package bd.com.evaly.evalyshop.ui.refundSettlement;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.models.refundSettlement.Bank;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RefundSettlementViewModel extends ViewModel {

    MutableLiveData<String> bkashAccount = new MutableLiveData<>();
    MutableLiveData<String> nagadAccount = new MutableLiveData<>();
    MutableLiveData<Bank> bankAccount = new MutableLiveData<>();

    @Inject
    public RefundSettlementViewModel(SavedStateHandle arg) {
        if (arg != null && arg.contains("model")) {
            bkashAccount.setValue(arg.get("bkash"));
            nagadAccount.setValue(arg.get("nagad"));
            bankAccount.setValue(arg.get("bank"));
        }
    }

}
