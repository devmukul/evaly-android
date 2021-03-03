package bd.com.evaly.evalyshop.ui.payment.listener;

import java.util.HashMap;

public  interface PaymentListener {
    void onPaymentSuccess(HashMap<String, String> values);
    void onPaymentFailure(HashMap<String, String> values);
    void onPaymentSuccess(String message);
}
