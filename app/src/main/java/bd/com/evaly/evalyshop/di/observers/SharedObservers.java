package bd.com.evaly.evalyshop.di.observers;

import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class SharedObservers {

    public SingleLiveEvent<Void> giftCardPaymentSuccess = new SingleLiveEvent<>();
}
