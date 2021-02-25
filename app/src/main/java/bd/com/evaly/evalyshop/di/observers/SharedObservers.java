package bd.com.evaly.evalyshop.di.observers;

import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.models.user.AddressItem;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class SharedObservers {
    public SingleLiveEvent<Void> giftCardPaymentSuccess = new SingleLiveEvent<>();
    public SingleLiveEvent<AddressResponse> onAddressChanged = new SingleLiveEvent<>();
}
