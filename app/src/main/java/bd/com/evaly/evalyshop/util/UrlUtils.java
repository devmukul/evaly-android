package bd.com.evaly.evalyshop.util;

public class UrlUtils {

    /* Dev mode */

    public static final String DOMAIN = "https://api-dev.evaly.com.bd/";
    public static final String PRODUCT_BASE_URL = "https://beta.evaly.com.bd/products/";


    /* Production */
//    public static final String DOMAIN = "https://api.evaly.com.bd/";
//    public static final String PRODUCT_BASE_URL = "https://evaly.com.bd/products/";


    public static final String DOMAIN5000 = DOMAIN+"auth/";

    public static final String BASE_URL = DOMAIN + "core/";

    public static final String BASE_URL_AUTH = DOMAIN5000 + "";

    public static final String BASE_URL_AUTH_API = DOMAIN5000 + "api/";

    public static final String BASE_URL_NEWSFEED = DOMAIN + "newsfeed/";

    public static final String BASE_URL_PAYMENT = DOMAIN + "pay/";


    public static final String REFRESH_AUTH_TOKEN = BASE_URL + "refresh-auth-token/";
    public static final String BANNER = BASE_URL + "banners/";
    public static final String SHOP_ITEMS = BASE_URL + "public/shops/items/";
    public static final String PUBLIC_PRODUCTS = BASE_URL + "public/products/";
    public static final String UPDATE_PRODUCT_STATUS = BASE_URL + "orders/confirm/delivery";


    public static final String CHANGE_PASSWORD = BASE_URL_AUTH + "change-password/";

    // changed
    public static final String SET_PASSWORD = BASE_URL_AUTH + "set-password/";

    public static final String REFRESH_TOKEN = BASE_URL_AUTH_API + "refresh/";


    public static final String REGISTER = BASE_URL_AUTH + "register/";
    public static final String SEND_CUSTOM_MESSAGE = BASE_URL_AUTH + "custom-message/";


    public static final String CHANGE_XMPP_PASSWORD = "/api/change_password";

    public static final String ADD_ROSTER = "api/add_rosteritem";
    public static final String INVITATION_LIST = "rest/messages/invitations/";
    public static final String ROSTER_LIST = "rest/messages/user/last-messages/";
    public static final String IMAGE_UPLOAD = "image/upload/";

    public static final String CHECK_UPDATE = BASE_URL + "apps/versions/current";
    public static final String EVALY_USERS = DOMAIN5000 + "evaly-users/";

    public static final String NEWS_FEED = DOMAIN + "newsfeed/posts";

    public static final String SUBMIT_ISSUE = BASE_URL+"orders/";
}
