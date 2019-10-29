package bd.com.evaly.evalyshop.util;

public class UrlUtils {

    /* Dev mode */

    public static final String DOMAIN = "https://api-dev.evaly.com.bd/";

    /* Production */

//     public static final String DOMAIN = "https://api.evaly.com.bd/";


    public static final String BASE_URL = DOMAIN + "core/";
    public static final String BASE_URL_NEWSFEED = DOMAIN + "newsfeed/";

    public static final String BASE_URL_PAYMENT = DOMAIN + "pay/";


    public static final String REFRESH_AUTH_TOKEN = BASE_URL + "refresh-auth-token/";
    public static final String BANNER = BASE_URL + "banners/";
    public static final String SHOP_ITEMS = BASE_URL + "public/shops/items/";
    public static final String PUBLIC_PRODUCTS = BASE_URL + "public/products/";


    public static final String CHANGE_PASSWORD = BASE_URL + "change-password/";
    public static final String SET_PASSWORD = BASE_URL + "set-password/";
    public static final String REGISTER = BASE_URL + "register/";
    public static final String SEND_CUSTOM_MESSAGE = BASE_URL + "custom-message/";


    public static final String CHANGE_XMPP_PASSWORD = "/api/change_password";

    public static final String ADD_ROSTER = "api/add_rosteritem";
    public static final String IMAGE_UPLOAD = "image/upload/";

    public static final String CHECK_UPDATE = BASE_URL + "apps/versions/current";


}
