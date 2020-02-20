package bd.com.evaly.evalyshop.ui.order.orderDetails.payment;

public interface PaymentBottomSheetNavigator {


    void onPaymentSuccess(String message);

    void onPaymentFailed(String message);

    void payViaCard(String url);

}
