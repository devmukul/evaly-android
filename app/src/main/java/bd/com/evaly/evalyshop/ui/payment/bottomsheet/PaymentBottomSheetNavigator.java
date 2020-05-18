package bd.com.evaly.evalyshop.ui.payment.bottomsheet;

public interface PaymentBottomSheetNavigator {

    void onPaymentSuccess(String message);
    void onPaymentFailed(String message);
    void payViaCard(String url);

}
