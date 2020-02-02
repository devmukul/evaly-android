package bd.com.evaly.evalyshop.ui.shop.delivery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import bd.com.evaly.evalyshop.models.shop.shopDetails.DeliveryOption;

public class DeliveryBottomSheetViewModel extends ViewModel{

    private MutableLiveData<List<DeliveryOption>> data = new MutableLiveData<>();

    public LiveData<List<DeliveryOption>> getData(){
        return data;
    }


}
