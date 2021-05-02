package bd.com.evaly.evalyshop.ui.order.orderDetails.refund;

import androidx.lifecycle.SavedStateHandle;

import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RefundViewModel extends BaseViewModel {

    SingleLiveEvent<Boolean> responseLiveData = new SingleLiveEvent<>();


    @Inject
    public RefundViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        super(savedStateHandle, apiRepository, preferenceRepository);
    }


    public void requestRefund(HashMap<String, String> body) {

        apiRepository.requestRefund(preferenceRepository.getToken(), body, new ResponseListener<CommonDataResponse<String>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<String> response, int statusCode) {
                ToastUtils.show(response.getMessage());
                if (response.getSuccess())
                    responseLiveData.setValue(true);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                responseLiveData.setValue(false);
                ToastUtils.show(errorBody);

            }

        });
    }

}
