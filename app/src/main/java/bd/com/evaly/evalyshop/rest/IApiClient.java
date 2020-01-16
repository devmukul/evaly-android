package bd.com.evaly.evalyshop.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.brand.BrandDetails;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.models.newsfeed.CreatePostModel;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;
import bd.com.evaly.evalyshop.models.order.OrderIssueModel;
import bd.com.evaly.evalyshop.models.order.OrderListItem;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.models.order.payment.ParitalPaymentModel;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.reviews.ReviewItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.shop.shopItem.ShopItem;
import bd.com.evaly.evalyshop.models.transaction.TransactionItem;
import bd.com.evaly.evalyshop.models.xmpp.RosterItemModel;
import bd.com.evaly.evalyshop.util.UrlUtils;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface IApiClient {

    @POST(UrlUtils.SET_PASSWORD)
    Call<JsonObject> setPassword(@Body HashMap<String, String> setPasswordModel);

    @POST(UrlUtils.BASE_URL_AUTH + "forgot-password")
    Call<JsonObject> forgetPassword(@Body HashMap<String, String>  body);

    @POST(UrlUtils.REGISTER)
    Call<JsonObject> register(@Body HashMap<String, String> data);

    @POST(UrlUtils.LOGIN)
    Call<JsonObject> login(@Body HashMap<String, String> data);

    @POST(UrlUtils.CHANGE_PASSWORD)
    Call<JsonObject> changePassword(@Header("Authorization") String token, @Body HashMap<String, String> data);

    @GET(UrlUtils.BASE_URL_AUTH + "user-info-pay/{username}/")
    Call<JsonObject> getUserInfoPay(@Header("Authorization") String token, @Path("username") String username);

    @PUT(UrlUtils.BASE_URL_AUTH+"user-info-update/")
    Call<JsonObject> setUserData(@Header("Authorization") String token, @Body HashMap<String, String> data);

    @POST(UrlUtils.REFRESH_TOKEN)
    Call<JsonObject> refreshToken(@Body HashMap<String, String> data);

    @POST(UrlUtils.CHANGE_XMPP_PASSWORD)
    Call<JsonPrimitive> changeXmppPassword(@Body HashMap<String, String> data);

    @POST(UrlUtils.ADD_ROSTER)
    Call<JsonPrimitive> addRoster(@Body HashMap<String, String> data);

    @GET(UrlUtils.INVITATION_LIST + "{phone}/")
    Call<JsonArray> getInvitationList(@Header("Authorization") String token, @Path("phone") String phone);

    @GET(UrlUtils.ROSTER_LIST + "{phone}/")
    Call<List<RosterItemModel>> getRosterList(@Header("Authorization") String token, @Path("phone") String phone, @Query("page") int page, @Query("limit") int limit);

    @POST(UrlUtils.SEND_CUSTOM_MESSAGE)
    Call<JsonObject> sendCustomMessage(@Header("Authorization") String token, @Body HashMap<String, String> data);

    @POST(UrlUtils.UPDATE_PRODUCT_STATUS)
    Call<JsonObject> updateProductStatus(@Header("Authorization") String token, @Body HashMap<String, String> data);

    @Multipart
    @POST(UrlUtils.IMAGE_UPLOAD)
    Call<JsonObject> imageUpload(@Header("Authorization") String token, @Header("Content_Type") String contentType, @Part MultipartBody.Part image);

    @GET(UrlUtils.CHECK_UPDATE)
    Call<JsonObject> checkUpdate();

    @GET(UrlUtils.EVALY_USERS)
    Call<JsonObject> searchEvalyUsers(@Header("Authorization") String token, @Query("search") String search, @Query("page") int page);

    @POST(UrlUtils.NEWS_FEED)
    Call<JsonObject> createPost(@Header("Authorization") String header, @Body HashMap<String, CreatePostModel> data);

    @POST(UrlUtils.SUBMIT_ISSUE + "{invoice}/" + "order-issues/")
    Call<JsonObject> submitIssue(@Header("Authorization") String header, @Path("invoice") String invoice, @Body HashMap<String, OrderIssueModel> data);

    @POST(UrlUtils.BASE_URL + "order-issues/{invoice}/issue-replies")
    Call<JsonObject> replyIssue(@Header("Authorization") String header, @Path("invoice") String invoice, @Body HashMap<String, HashMap> data);

    @GET(UrlUtils.SUBMIT_ISSUE + "{invoice}/" + "order-issues/")
    Call<JsonObject> getIssueList(@Header("Authorization") String header, @Path("invoice") String invoice);

    @GET(UrlUtils.GET_BANNERS)
    Call<JsonObject> getBanners();

    // balance, transaction

    @GET(UrlUtils.DOMAIN+"pay/wallet-history/{username}")
    Call<CommonDataResponse<List<TransactionItem>>> getTransactionHistory(@Header("Authorization") String token, @Path("username") String username, @Query("page") int page);


    @GET(UrlUtils.DOMAIN+"pay/apply/cashback-balance/{username}/")
    Call<JsonObject> claimCashBack(@Header("Authorization") String header, @Path("username") String username);


    // product APIs

    @GET(UrlUtils.PUBLIC_PRODUCTS)
    Call<CommonResultResponse<List<ProductItem>>> getCategoryBrandProducts(@Query("page") int page, @Query("category") String category, @Query("brand") String brand);

    @GET(UrlUtils.BASE_URL+"public/shops/items/{shopSlug}/")
    Call<JsonObject> getShopProducts(@Path("shopSlug") String shopSlug, @Query("page") int page, @Query("limit") int limit, @Query("category_slug") String categorySlug);

    @GET(UrlUtils.CAMPAIGNS+"/{campaignSlug}/shops/{shopSlug}/items")
    Call<JsonObject> getCampaignShopProducts(@Path("campaignSlug") String campaignSlug, @Path("shopSlug") String shopSlug, @Query("page") int page, @Query("limit") int limit, @Query("category_slug") String categorySlug);

    @GET(UrlUtils.BASE_URL+"/public/shops/{shopSlug}/items/{shopItem}/variants")
    Call<CommonDataResponse<List<ShopItem>>> getProductVariants(@Path("shopSlug") String shopSlug, @Path("shopItem") String shopItem);

    // with token

    @GET(UrlUtils.BASE_URL+"public/shops/items/{shopSlug}/")
    Call<ShopDetailsModel> getShopDetails(@Header("Authorization") String token, @Path("shopSlug") String shopSlug, @Query("page") int page, @Query("limit") int limit, @Query("category_slug") String categorySlug);

    @GET(UrlUtils.CAMPAIGNS+"/{campaignSlug}/shops/{shopSlug}/items")
    Call<ShopDetailsModel> getCampaignShopDetails(@Header("Authorization") String token, @Path("campaignSlug") String campaignSlug, @Path("shopSlug") String shopSlug, @Query("page") int page, @Query("limit") int limit, @Query("category_slug") String categorySlug);


    // Categories API

    @GET(UrlUtils.CATEGORIES)
    Call<JsonArray> getCategories(@Query("parent") String parent);

    @GET(UrlUtils.CATEGORIES)
    Call<JsonArray> getCategories();


    @GET(UrlUtils.CATEGORIES_BRANDS)
    Call<JsonObject> getBrandsCategories(@Query("category") String category, @Query("page") int page, @Query("limit") int limit);


    @GET(UrlUtils.CATEGORIES_SHOPS_ROOT)
    Call<JsonObject> getShopsOfCategories(@Query("page") int page, @Query("limit") int limit);

    @GET(UrlUtils.CATEGORIES_SHOPS+"{category}/")
    Call<JsonObject> getShopsOfCategories(@Path("category") String category, @Query("page") int page, @Query("limit") int limit);


    @GET(UrlUtils.BASE_URL+"public/shops/categories/{shopSlug}/")
    Call<JsonObject> getCategoriesofShop(@Path("shopSlug") String shopSlug, @Query("page") int page);


    @GET(UrlUtils.CAMPAIGNS+"/{campaignSlug}/shops/{shopSlug}/categories")
    Call<JsonObject> getCategoriesOfCampaignShop(@Path("campaignSlug") String campaignSlug, @Path("shopSlug") String shopSlug, @Query("page") int page);


    // campaign APIs

    @GET(UrlUtils.CAMPAIGNS)
    Call<CommonDataResponse<List<CampaignItem>>> getCampaigns();

    @GET(UrlUtils.CAMPAIGNS+"/{group}/shops")
    Call<CommonDataResponse<List<CampaignShopItem>>> getCampaignShops(@Path("group") String group, @Query("page") int page, @Query("limit") int limit);


    // Root Category

    @GET(UrlUtils.ROOTCATEGORIES)
    Call<List<CategoryEntity>> getRootCategories();

    // Order APIs
    @GET(UrlUtils.ORDERS)
    Call<CommonResultResponse<List<OrderListItem>>> getOrderList(@Header("Authorization") String token, @Query("page") int page, @Query("order_status") String orderStatus);

    @PUT(UrlUtils.BASE_URL+"orders/customer/cancel-order/{invoice_no}/")
    Call<JsonObject> cancelOrder(@Header("Authorization") String token, @Path("invoice_no") String invoiceNo, @Body HashMap<String, String> body);


    // brand

    @GET(UrlUtils.BASE_URL+"public/brands/{brandSlug}/")
    Call<CommonDataResponse<BrandDetails>> getBrandDetails(@Path("brandSlug") String brandSlug);


    // Notification

    @GET(UrlUtils.DOMAIN+"{notificationSource}/notifications_count/")
    Call<NotificationCount> getNotificationCount(@Header("Authorization") String token, @Path("notificationSource") String notificationType);

    // shop releated

    @GET
    Call<ShopDetailsModel> getShopDetailsItems(@Header("Authorization") String token, @Url String url);

    @POST(UrlUtils.BASE_URL + "shop-subscriptions")
    Call<JsonObject> subscribeToShop(@Header("Authorization") String token, @Body HashMap<String, String> shopSlug);


    @DELETE(UrlUtils.BASE_URL + "unsubscribe-shop/{shop_slug}/")
    Call<JsonObject> unsubscribeShop(@Header("Authorization") String token, @Path("shop_slug") String shopSlug);

    @GET(UrlUtils.BASE_URL+"reviews/summary/shops/{sku}/")
    Call<JsonObject> getShopReviews(@Header("Authorization") String token, @Path("sku") String sku);

    @GET(UrlUtils.BASE_URL+"shop-subscriptions")
    Call<JsonObject> getFollowedShops(@Header("Authorization") String token);

    // referral

    @POST("https://nsuer.club/evaly/referral/submit-referral.php")
    Call<JsonObject> checkReferral(@Body HashMap<String, String> body);


    // order apis

    @POST(UrlUtils.BASE_URL+"custom/order/create/")
    Call<JsonObject> placeOrder(@Header("Authorization") String token, @Body JsonObject body);

    @GET(UrlUtils.BASE_URL+"custom/orders/{invoiceNo}/")
    Call<OrderDetailsModel> getOrderDetails(@Header("Authorization") String token, @Path("invoiceNo") String invoiceNo);


    @GET(UrlUtils.BASE_URL+"orders/histories/{invoiceNo}/")
    Call<JsonObject> getOrderHistories(@Header("Authorization") String token, @Path("invoiceNo") String invoiceNo);


    // gift card

    @POST(UrlUtils.DOMAIN + "pay/transactions/payment/order/gift-code/")
    Call<JsonObject> payWithGiftCard(@Header("Authorization") String token, @Body HashMap<String, String> body);

    @GET(UrlUtils.DOMAIN + "cpn/gift-cards/custom/list")
    Call<CommonDataResponse<List<GiftCardListItem>>> getGiftCardList(@Query("page") int page);

    @POST(UrlUtils.DOMAIN + "cpn/gift-card-orders/place/")
    Call<JsonObject> placeGiftCardOrder(@Header("Authorization") String token, @Body JsonObject body);

    @GET(UrlUtils.DOMAIN + "cpn/gift-cards/retrieve/{slug}")
    Call<JsonObject> getGiftCardDetails(@Path("slug") String slug);


    // payment
    @POST(UrlUtils.DOMAIN+"pay/transactions/payment/order/")
    Call<JsonObject> makePartialPayment(@Header("Authorization") String token, @Body ParitalPaymentModel body);


    @POST(UrlUtils.DOMAIN+"pay/pg")
    Call<JsonObject> payViaCard(@Header("Authorization") String token, @Body HashMap<String, String> body);


    // reviews
    @GET(UrlUtils.BASE_URL+"reviews/shops/{slug}/")
    Call<CommonDataResponse<List<ReviewItem>>> getShopReviews(@Header("Authorization") String token, @Path("slug") String shopSlug, @Query("page") int page, @Query("limit") int limit);

    @POST(UrlUtils.BASE_URL + "add-review/{shopSlug}/")
    Call<JsonObject> postShopReview(@Header("Authorization") String token, @Path("shopSlug") String slug, @Body JsonObject body);


    @GET(UrlUtils.BASE_URL + "review-eligibility/{shopSlug}/")
    Call<JsonObject> checkShopReviewEligibility(@Header("Authorization") String token, @Path("shopSlug") String slug);


    // Newsfeed

    @GET
    Call<JsonObject> getNewsfeedPosts(@Header("Authorization") String token, @Url String url);

    @GET(UrlUtils.BASE_URL_NEWSFEED + "posts/{post_id}")
    Call<JsonObject> getNewsfeedPostDetails(@Header("Authorization") String token, @Path("post_id") String postId);

    @GET(UrlUtils.BASE_URL_NEWSFEED + "posts/{selectedPostId}/comments/{commentId}/replies")
    Call<JsonObject> getNewsfeedReplies(@Header("Authorization") String token, @Path("selectedPostId") String postId, @Path("commentId") String commentId, @Query("page") int page);

    @GET(UrlUtils.BASE_URL_NEWSFEED + "posts/{postId}/comments")
    Call<JsonObject> getNewsfeedComments(@Header("Authorization") String token, @Path("postId") String postId, @Query("page") int page);

    @POST(UrlUtils.BASE_URL_NEWSFEED + "posts/{selectedPostID}/comments")
    Call<JsonObject> postNewsfeedComment(@Header("Authorization") String token, @Path("selectedPostID") String selectedPostID, @Body JsonObject body);

    @POST(UrlUtils.BASE_URL_NEWSFEED + "posts/{selectedPostID}/comments/{selectedCommentID}/replies")
    Call<JsonObject> postNewsfeedReply(@Header("Authorization") String token, @Path("selectedPostID") String selectedPostID, @Path("selectedCommentID") String selectedCommentID, @Body JsonObject body);

    @POST(UrlUtils.BASE_URL_NEWSFEED + "posts/{slug}/favorite")
    Call<JsonObject> likeNewsfeedPost(@Header("Authorization") String token, @Path("slug") String postSlug);

    @DELETE(UrlUtils.BASE_URL_NEWSFEED + "posts/{slug}/favorite")
    Call<JsonObject> dislikeNewsfeedPost(@Header("Authorization") String token, @Path("slug") String postSlug);

    @DELETE
    Call<JsonObject> deleteNewsfeedItem(@Header("Authorization") String token, @Url String url);



}
