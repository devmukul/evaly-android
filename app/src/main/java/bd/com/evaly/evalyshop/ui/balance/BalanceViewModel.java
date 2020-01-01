package bd.com.evaly.evalyshop.ui.balance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BalanceViewModel extends ViewModel{


    private MutableLiveData<BalanceModel> data = new MutableLiveData<>();

    public LiveData<BalanceModel> getData(){
        return data;
    }


}
