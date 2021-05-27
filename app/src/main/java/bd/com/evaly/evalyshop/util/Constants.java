package bd.com.evaly.evalyshop.util;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.models.order.OrderIssueModel;

public class Constants {

    // flash-sale-2509b8bb
    public static final String FLASH_SALE_SLUG = BuildConfig.DEBUG ? "evaly-foorty-32f4bb42" : "flash-sale-2509b8bb"; // payment-on-delivery-pod-180c4287; mr-quick-deal-1d2de849

    public static final String XMPP_DOMAIN = BuildConfig.CHAT_BASE;
    public static final String XMPP_HOST = BuildConfig.CHAT_BASE;
    public static final String EVALY_NUMBER = BuildConfig.EVALY_NUMBER;
    public static final String BUILD = BuildConfig.BUILD;

    public static final boolean XMPP_DEBUG = true;

    public static final String CASH_ON_DELIVERY = "Cash on Delivery";
    public static final String EVALY_ACCOUNT = "Evaly Account";
    public static final String BKASH = "bKash";
    public static final String NAGAD = "Nagad";
    public static final String CARD = "Visa/Mastercard";
    public static final String CITYBANK = "City Bank";
    public static final String OTHERS = "SSLCommerz";
    public static final String BALANCE_WITH_CASH = "Evaly Account + Cash on Delivery";
    // Event specific constants
    public static final String EVT_SIGNUP_ERR = "xmpp_signup_error";
    public static final String EVT_NEW_MSG_SENT = "xmpp_new_msg_sent";
    public static final String EVT_AUTH_SUC = "xmpp_auth_success";
    public static final String EVT_RECONN_ERR = "xmpp_recon_error";
    public static final String EVT_CONN_SUC = "xmpp_conn_success";
    public static final String EVT_LOGIN_ERR = "xmpp_login_error";
    public static final String EVT_REQUEST_SUBSCRIBE = "xmpp_request_subscribe";


    public static final String INTENT_KEY_CHATSTATE = "chatstate";
    //Presence States (Strings)
    public static final String PRESENCE_MODE_AVAILABLE = "Online";
    public static final String PRESENCE_MODE_AWAY = "Away";
    public static final String PRESENCE_MODE_DND = "Do not disturb";
    public static final String PRESENCE_MODE_XA = "Offline";

    //Presence States (int)
    public static final int PRESENCE_MODE_AVAILABLE_INT = 1;
    public static final int PRESENCE_MODE_AWAY_INT = 2;
    public static final int PRESENCE_MODE_DND_INT = 3;
    public static final int PRESENCE_MODE_OFFLINE_INT = 0;


    //For now long, after user has finished typing, should we wait to mark his typing status "Paused"?
    public static final int PAUSE_THRESHOLD = 2; //in seconds

    //Signup Error messages
    public static final String SIGNUP_ERR_INVALIDPASS = "Password invalid";
    public static final String SIGNUP_ERR_FIELDERR = "Some Fields are empty";

    // Message Type
    public static final String EVALY_LOGO = "https://s3-ap-southeast-1.amazonaws.com/media.evaly.com.bd/media/2019-08-04_090235.843922android-icon-200x200.png";

    public static final String BKASH_SUCCESS_URL = "success.html";
    public static final String SSL_SUCCESS_URL = "evaly.com.bd/order";
    public static final String SEBL_SUCCESS_URL = "evaly.com.bd/order";
    public static final String CITYBANK_SUCCESS_URL = "evaly.com.bd/order";

    public static List<OrderIssueModel> getDelivaryIssueList() {

        List<OrderIssueModel> list = new ArrayList<>();
        list.add(new OrderIssueModel("delivery", "Delivery Issue"));
        list.add(new OrderIssueModel("cashback", "Cashback Issue"));
        list.add(new OrderIssueModel("product", "Product Issue"));
        list.add(new OrderIssueModel("refund", "Refund Issue"));
        list.add(new OrderIssueModel("payment", "Payment Issue"));
        list.add(new OrderIssueModel("bank payment", "Bank Payment"));
        list.add(new OrderIssueModel("return", "Product return"));
        list.add(new OrderIssueModel("order cancel", "Request for order cancel"));
        list.add(new OrderIssueModel("others", "Others"));
        return list;
    }

    public static List<OrderIssueModel> getIssueListPending() {

        List<OrderIssueModel> list = new ArrayList<>();
        list.add(new OrderIssueModel("payment", "Payment Issue"));
        list.add(new OrderIssueModel("bank payment", "Bank Payment"));
        return list;
    }

}
