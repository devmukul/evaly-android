package bd.com.evaly.evalyshop.ui.refundSettlement;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RefundSettlementViewModel extends ViewModel {

    MutableLiveData<Integer> messageCount = new MutableLiveData<>();

    @Inject
    public RefundSettlementViewModel() {

    }

}
