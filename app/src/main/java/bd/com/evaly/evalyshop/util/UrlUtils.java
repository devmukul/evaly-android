package bd.com.evaly.evalyshop.util;

import bd.com.evaly.evalyshop.BuildConfig;

public class UrlUtils {

    /* Force Production Test */

//    public static final String DOMAIN = "https://api.evaly.com.bd/";
//    public static final String PRODUCT_BASE_URL = "https://evaly.com.bd/products/";
//    public static final String DOMAIN_AUTH = "http://192.168.1.230:5000/";

    /* Prod, Dev Auto Switch */

    public static final String DOMAIN = BuildConfig.BASE_URL;
    public static final String PRODUCT_BASE_URL = BuildConfig.WEB_URL + "products/";
    public static final String BASE_AUTH = "http://beta.gateway.evaly.com.bd/";
    public static final String DOMAIN_AUTH = DOMAIN+"auth/";

    public static final String BASE_URL = DOMAIN + "core/";
    public static final String BASE_URL_AUTH = DOMAIN_AUTH + "";
    public static final String BASE_URL_AUTH_API = DOMAIN_AUTH + "api/";
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
    public static final String LOGIN = BASE_URL_AUTH_API + "login/";
    public static final String SEND_CUSTOM_MESSAGE = BASE_URL_AUTH + "custom-message/";
    public static final String CHANGE_XMPP_PASSWORD = "/api/change_password";
    public static final String XMPP_REGISTER = "/rest/users/new-user/";
    public static final String UPDATE_VCARD = "/api/set_vcard";
    public static final String ADD_ROSTER = "api/add_rosteritem";
    public static final String INVITATION_LIST = "rest/messages/invitations/";
    public static final String ROSTER_LIST = "rest/messages/user/last-messages/";
    public static final String IMAGE_UPLOAD = BASE_URL+"image/upload/";
    public static final String CHECK_UPDATE = BASE_URL + "apps/versions/current";
    public static final String EVALY_USERS = DOMAIN_AUTH + "evaly-users/";
    public static final String NEWS_FEED = DOMAIN + "newsfeed/posts";
    public static final String SUBMIT_ISSUE = BASE_URL+"orders/";
    public static final String GET_BANNERS = BASE_URL+"banners/";

    // product api endpoints
    public static final String CATEGORIES = BASE_URL+"public/categories/";
    public static final String CATEGORIES_BRANDS = BASE_URL+"public/brands/";

    public static final String CATEGORIES_SHOPS_ROOT = BASE_URL+"custom/shops/";
    public static final String CATEGORIES_SHOPS = BASE_URL+"public/category/shops/";

    // campaigns
    public static final String CAMPAIGNS = BASE_URL+"campaigns/public";

    // Orders
    public static final String ORDERS = BASE_URL+"custom/orders/";

    // categories
    public static final String ROOTCATEGORIES = BASE_URL+"public/categories/";


    // Auth 2.0


}
