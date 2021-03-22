package bd.com.evaly.evalyshop.ui.payment.builder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import bd.com.evaly.evalyshop.ui.payment.activity.PaymentWebActivity;
import bd.com.evaly.evalyshop.ui.payment.listener.PaymentListener;
import bd.com.evaly.evalyshop.ui.payment.model.PurchaseRequestInfo;


public class PaymentWebBuilder {
    private static PaymentListener listener;
    private static PurchaseRequestInfo purchaseInfo;
    private static String LOAD_URL, SUCCESS_URL, toolbarTitle;
    private Context context;

    /**
     * Constructor
     *
     * @param context
     **/
    public PaymentWebBuilder(Activity context) {
        this.context = context;
    }

    public static PurchaseRequestInfo getPurchaseInformation() {
        return purchaseInfo;
    }

    private void setPurchaseInformation(PurchaseRequestInfo purchaseRequestInfo) {
        purchaseInfo = purchaseRequestInfo;
    }

    public static PaymentListener getUpayListener() {
        return listener;
    }

    public static String getRequestURL() {
        return LOAD_URL;
    }

    public static String getSuccessUrl() {
        return SUCCESS_URL;
    }

    public static String getToolbarTitle() {
        return toolbarTitle;
    }

    public void setToolbarTitle(String title) {
        toolbarTitle = title;
    }

    /**
     * This method is use to send a upay on delivery request from the client side.
     *
     * @param purchaseRequestInfo -  Object, takes two parameters from client side , @param TOKEN for verify the client  & @param Amount for purchase.
     **/
    public void loadPaymentURL(String loadUrl, String successURL,
                               PurchaseRequestInfo purchaseRequestInfo) {
        LOAD_URL = loadUrl;
        SUCCESS_URL = successURL;
        setPurchaseInformation(purchaseRequestInfo);
        startWebViewActivity();
    }

    private void startWebViewActivity() {
        if (listener != null) {
            /** Start Web View Here **/
            Intent intent = new Intent(context, PaymentWebActivity.class);
            context.startActivity(intent);
        }
    }

    /**
     * @param paymentListener - Interface,  responsible  for   success & failure callback.
     * @param paymentListener
     */
    public void setPaymentListener(PaymentListener paymentListener) {
        listener = paymentListener;
    }

}
