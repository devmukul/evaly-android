package bd.com.evaly.evalyshop.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.appointment.AppointmentCategoryResponse;
import bd.com.evaly.evalyshop.models.appointment.AppointmentRequest;
import bd.com.evaly.evalyshop.models.appointment.AppointmentTimeSlotResponse;
import bd.com.evaly.evalyshop.models.appointment.comment.AppointmentCommentRequest;
import bd.com.evaly.evalyshop.models.appointment.comment.AppointmentCommentResponse;
import bd.com.evaly.evalyshop.models.appointment.list.AppointmentResponse;
import bd.com.evaly.evalyshop.models.auth.LoginBody;
import bd.com.evaly.evalyshop.models.auth.LoginResponse;
import bd.com.evaly.evalyshop.models.auth.RegisterBody;
import bd.com.evaly.evalyshop.models.auth.RegisterResponse;
import bd.com.evaly.evalyshop.models.auth.SetPasswordBody;
import bd.com.evaly.evalyshop.models.auth.SetPasswordResponse;
import bd.com.evaly.evalyshop.models.auth.captcha.CaptchaResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;
import bd.com.evaly.evalyshop.models.campaign.banner.CampaignBannerResponse;
import bd.com.evaly.evalyshop.models.campaign.brand.CampaignBrandResponse;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.models.campaign.carousel.CampaignCarouselResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignProductCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.models.campaign.shop.CampaignShopResponse;
import bd.com.evaly.evalyshop.models.campaign.subcampaign.SubCampaignDetailsResponse;
import bd.com.evaly.evalyshop.models.cart.CartHolderModel;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandCatResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;
import bd.com.evaly.evalyshop.models.catalog.category.ChildCategoryResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopDetailsResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopListResponse;
import bd.com.evaly.evalyshop.models.express.ExpressServiceDetailsModel;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;
import bd.com.evaly.evalyshop.models.hero.DeliveryHeroResponse;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.models.issueNew.category.IssueCategoryModel;
import bd.com.evaly.evalyshop.models.issueNew.comment.IssueCommentBody;
import bd.com.evaly.evalyshop.models.issueNew.comment.IssueTicketCommentModel;
import bd.com.evaly.evalyshop.models.issueNew.create.IssueCreateBody;
import bd.com.evaly.evalyshop.models.issueNew.list.IssueListModel;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.createPost.CreatePostModel;
import bd.com.evaly.evalyshop.models.newsfeed.newsfeed.NewsfeedPost;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;
import bd.com.evaly.evalyshop.models.notification.NotificationItem;
import bd.com.evaly.evalyshop.models.order.AttachmentCheckResponse;
import bd.com.evaly.evalyshop.models.order.OrderIssueModel;
import bd.com.evaly.evalyshop.models.order.OrderListItem;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.models.order.payment.ParitalPaymentModel;
import bd.com.evaly.evalyshop.models.order.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.models.order.updateAddress.UpdateOrderAddressRequest;
import bd.com.evaly.evalyshop.models.orderRequest.OrderRequestResponse;
import bd.com.evaly.evalyshop.models.pay.BalanceResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopModel;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductDetailsModel;
import bd.com.evaly.evalyshop.models.profile.AddressRequest;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.models.profile.UserInfoResponse;
import bd.com.evaly.evalyshop.models.reviews.ReviewItem;
import bd.com.evaly.evalyshop.models.search.AlgoliaRequest;
import bd.com.evaly.evalyshop.models.shop.GroupShopModel;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.shop.shopItem.ShopItem;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.models.transaction.TransactionItem;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.UrlUtils;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface IApiClient {

    @GET(UrlUtils.BASE_CART + "carts/users/evaly")
    Call<CommonDataResponse<CartHolderModel>> getCartList(@Header("Authorization") String token);

    @POST(UrlUtils.BASE_CART + "carts")
    Call<CommonDataResponse<CartHolderModel>> syncCartList(@Header("Authorization") String token,
                                                            @Body CartHolderModel body);



    @POST(UrlUtils.BASE_CATALOG + "orders/checkout/buckets")
    Call<CommonDataResponse<List<AttachmentCheckResponse>>> isAttachmentRequired(@Header("Authorization") String token,
                                                                                 @Body List<Integer> list);

    @GET(UrlUtils.DOMAIN + "ecaptcha/auth/init")
    Call<CommonDataResponse<CaptchaResponse>> getCaptcha();

    @GET(UrlUtils.DOMAIN + "order-request-go/api/v1/order-requests/get-orders")
    Call<CommonDataResponse<List<OrderRequestResponse>>> getOrderRequests(@Header("Authorization") String token,
                                                                          @Query("page") int page);

    // appointment

    @GET(BuildConfig.APPOINTMENT_URL + "common/comments")
    Call<CommonDataResponse<List<AppointmentCommentResponse>>> getAppointmentCommentList(@Header("Authorization") String token,
                                                                                         @Query("appointment_id") String appointmentId,
                                                                                         @Query("offset") int page);

    @POST(BuildConfig.APPOINTMENT_URL + "common/comments")
    Call<CommonDataResponse<AppointmentCommentResponse>> createAppointmentComment(@Header("Authorization") String token,
                                                                                  @Body AppointmentCommentRequest body);


    @GET(BuildConfig.APPOINTMENT_URL + "time-slots/{date}")
    Call<CommonDataResponse<List<AppointmentTimeSlotResponse>>> getAppointmentTimeSlotList(@Header("Authorization") String token,
                                                                                           @Path("date") String date);

    @GET(BuildConfig.APPOINTMENT_URL + "users/categories")
    Call<CommonDataResponse<List<AppointmentCategoryResponse>>> getAppointmentCategoryList(@Header("Authorization") String token,
                                                                                           @Query("limit") int limit);

    @GET(BuildConfig.APPOINTMENT_URL + "users/appointments")
    Call<CommonDataResponse<List<AppointmentResponse>>> getAppointmentList(@Header("Authorization") String token,
                                                                           @Query("offset") int page);

    @POST(BuildConfig.APPOINTMENT_URL + "users/appointments")
    Call<CommonDataResponse<AppointmentResponse>> createAppointment(@Header("Authorization") String token,
                                                                    @Body AppointmentRequest body);

    @PUT(BuildConfig.APPOINTMENT_URL + "users/appointments/{id}/cancel")
    Call<CommonDataResponse<AppointmentResponse>> cancelAppointment(@Header("Authorization") String token,
                                                                    @Path("id") String id);

    //TabsItem

    @GET(UrlUtils.BASE_URL + "public/brands/")
    Call<CommonDataResponse<List<TabsItem>>> searchBrands(@Query("page") int page,
                                                          @Query("limit") int limit,
                                                          @Query("search") String search);

    @GET(UrlUtils.BASE_URL + "custom/shops/")
    Call<CommonDataResponse<List<TabsItem>>> searchShop(@Query("page") int page,
                                                        @Query("limit") int limit,
                                                        @Query("search") String search);

    // algolia

    @POST("https://eza2j926q5-dsn.algolia.net/1/indexes/*/queries")
    Call<JsonObject> searchOnAlgolia(@Query("x-algolia-agent") String agent,
                                     @Query("x-algolia-application-id") String applicationId,
                                     @Query("x-algolia-api-key") String apiKey,
                                     @Body AlgoliaRequest body);


    // new campaign
    @GET(UrlUtils.BASE_CATALOG + "campaign/{campaignSlug}")
    Call<CommonDataResponse<SubCampaignDetailsResponse>> getSubCampaignDetails(@Path("campaignSlug") String campaignSlug);

    @GET(UrlUtils.BASE_URL + "campaigns/web/home")
    Call<CommonDataResponse<List<CampaignCarouselResponse>>> getCampaignCarousel(@Query("context_type") String contextType);

    @GET(UrlUtils.BASE_URL + "campaigns/mobile/products/banners/latest")
    Call<CommonDataResponse<List<CampaignBannerResponse>>> getCampaignBanners();

    @GET(UrlUtils.BASE_CATALOG + "campaign/category/products")
    Call<CommonDataResponse<List<CampaignProductResponse>>> getCampaignCategoryProducts(@Query("page") int page,
                                                                                        @Query("limit") int limit,
                                                                                        @Query("search") String search,
                                                                                        @Query("category") String category,
                                                                                        @Query("campaign") String campaign,
                                                                                        @Query("product_category") String productCategory);

    @GET(UrlUtils.BASE_CATALOG + "campaign")
    Call<CommonDataResponse<List<SubCampaignResponse>>> getCampaignCategoryCampaigns(@Query("page") int page,
                                                                                     @Query("limit") int limit,
                                                                                     @Query("search") String search,
                                                                                     @Query("category") String category);

    @GET(UrlUtils.BASE_CATALOG + "campaign/category/brands")
    Call<CommonDataResponse<List<CampaignBrandResponse>>> getCampaignCategoryBrands(@Query("page") int page,
                                                                                    @Query("limit") int limit,
                                                                                    @Query("search") String search,
                                                                                    @Query("category") String category,
                                                                                    @Query("campaign") String campaign);

    @GET(UrlUtils.BASE_CATALOG + "campaign/category/shops")
    Call<CommonDataResponse<List<CampaignShopResponse>>> getCampaignCategoryShops(@Query("page") int page,
                                                                                  @Query("limit") int limit,
                                                                                  @Query("search") String search,
                                                                                  @Query("category") String category,
                                                                                  @Query("campaign") String campaign);

    @GET(UrlUtils.BASE_CATALOG + "campaign/category/products")
    Call<CommonDataResponse<List<CampaignProductResponse>>> getCampaignAllProducts(@Query("page") int page,
                                                                                   @Query("limit") int limit,
                                                                                   @Query("search") String search);

    @GET(UrlUtils.BASE_CATALOG + "campaign/category/campaigns")
    Call<CommonDataResponse<List<CampaignCategoryResponse>>> getCampaignCategory();

    // chat

    @GET("https://" + Constants.XMPP_DOMAIN + "/rest/messages/unread-messages/count/{username}")
    Call<CommonDataResponse<String>> getUnreadedMessageCount(@Header("Authorization") String token,
                                                             @Path("username") String username);

    // issue ticket
    @GET(UrlUtils.DOMAIN + "evaly-issue/api/v1/categories/customer")
    Call<CommonDataResponse<List<IssueCategoryModel>>> getIssueTicketCategory(@Header("Authorization") String token,
                                                                              @Query("order_status") String orderStatus,
                                                                              @Query("limit") int limit);

    @GET(UrlUtils.DOMAIN + "evaly-issue/api/v1/tickets/customer/evaly")
    Call<CommonDataResponse<List<IssueListModel>>> getIssueTicketList(@Header("Authorization") String token,
                                                                      @Query("invoice_no") String invoice,
                                                                      @Query("offset") int page);

    @PATCH(UrlUtils.DOMAIN + "evaly-issue/api/v1/tickets/customer/{tickerId}")
    Call<CommonDataResponse<IssueListModel>> resolveIssueTicketStatus(@Header("Authorization") String token,
                                                                      @Body HashMap<String, String> body,
                                                                      @Path("tickerId") int id);

    @POST(UrlUtils.DOMAIN + "evaly-issue/api/v1/tickets/customer/evaly")
    Call<CommonDataResponse<IssueListModel>> createIssueTicket(@Header("Authorization") String token,
                                                               @Body IssueCreateBody body);

    @GET(UrlUtils.DOMAIN + "evaly-issue/api/v1/tickets/customer/comments/{ticket_id}")
    Call<CommonDataResponse<List<IssueTicketCommentModel>>> getIssueTicketComment(@Header("Authorization") String token,
                                                                                  @Path("ticket_id") int ticketId);

    @POST(UrlUtils.DOMAIN + "evaly-issue/api/v1/tickets/customer/comments/{ticket_id}")
    Call<CommonDataResponse<IssueTicketCommentModel>> createIssueTicketComment(@Header("Authorization") String token,
                                                                               @Body IssueCommentBody body,
                                                                               @Path("ticket_id") int tickerId);


    @POST(UrlUtils.DOMAIN_EAUTH + "set-password")
    Call<JsonObject> setPassword(@Body HashMap<String, String> setPasswordModel);

    @POST(UrlUtils.DOMAIN + "ecaptcha/auth/forgot-password")
    Call<JsonObject> forgetPassword(@Body HashMap<String, String> body);

    @POST(UrlUtils.DOMAIN + "ecaptcha/auth/create-customer")
    Call<JsonObject> register(@Body HashMap<String, String> data);

    @POST(UrlUtils.DOMAIN_EAUTH + "login")
    Call<JsonObject> login(@Body HashMap<String, String> data);

    @POST(UrlUtils.DOMAIN_EAUTH + "change-password")
    Call<JsonObject> changePassword(@Header("Authorization") String token,
                                    @Body HashMap<String, String> data);

    @POST(UrlUtils.DOMAIN_EAUTH + "logout")
    Call<JsonObject> logout(@Header("Authorization") String token);

    @GET(UrlUtils.BASE_URL_AUTH + "user-info-pay/{username}/")
    Call<JsonObject> getUserInfoPay(@Header("Authorization") String token,
                                    @Path("username") String username);

    @GET(UrlUtils.DOMAIN + "epay-reader/api/v1/user/balance")
    Call<CommonDataResponse<BalanceResponse>> getBalance(@Header("Authorization") String token);

    @GET(UrlUtils.DOMAIN_EAUTH + "profile")
    Call<CommonDataResponse<UserModel>> getUserProfile(@Header("Authorization") String token);

    @PUT(UrlUtils.DOMAIN_EAUTH + "profile")
    Call<CommonDataResponse<UserModel>> setUserData(@Header("Authorization") String token,
                                                    @Body HashMap<String, String> data);

    @PUT(UrlUtils.DOMAIN_EAUTH + "profile")
    Call<CommonDataResponse<UserModel>> setUserData(@Header("Authorization") String token,
                                                    @Body JsonObject body);

    @POST(UrlUtils.BASE_URL_AUTH + "add-user-info/")
    Call<CommonDataResponse<UserInfoResponse>> addUserData(@Header("Authorization") String token,
                                                           @Body HashMap<String, String> data);

    @GET(UrlUtils.BASE_URL_AUTH + "user-info-details/")
    Call<CommonDataResponse<UserInfoResponse>> getUserInfo(@Header("Authorization") String token);

    @GET(UrlUtils.BASE_URL_AUTH + "user-address/list/")
    Call<CommonDataResponse<List<AddressResponse>>> getAddressList(@Header("Authorization") String token);

    @POST(UrlUtils.BASE_URL_AUTH + "add-user-address/")
    Call<CommonDataResponse<AddressResponse>> addAddress(@Header("Authorization") String token,
                                                         @Body AddressRequest body);

    @DELETE(UrlUtils.BASE_URL_AUTH + "remove-user-address/{id}")
    Call<CommonDataResponse> removeAddress(@Header("Authorization") String token,
                                           @Path("id") int id);


    @POST(UrlUtils.UPDATE_VCARD)
    Call<JsonObject> setUserDataToXmpp(@Body HashMap<String, String> data);

    @POST(UrlUtils.DOMAIN_EAUTH + "token/refresh")
    Call<JsonObject> refreshToken(@Header("Authorization") String token,
                                  @Body HashMap<String, String> data);

    @POST(UrlUtils.CHANGE_XMPP_PASSWORD)
    Call<JsonPrimitive> changeXmppPassword(@Body HashMap<String, String> data);

    @POST(UrlUtils.XMPP_REGISTER)
    Call<JsonObject> registerXmpp(@Header("Authorization") String token,
                                  @Body HashMap<String, String> data);

    @POST(UrlUtils.ADD_ROSTER)
    Call<JsonPrimitive> addRoster(@Body HashMap<String, String> data);

    @GET(UrlUtils.INVITATION_LIST + "{phone}/")
    Call<JsonArray> getInvitationList(@Header("Authorization") String token,
                                      @Path("phone") String phone);


    @POST(UrlUtils.SEND_CUSTOM_MESSAGE)
    Call<JsonObject> sendCustomMessage(@Header("Authorization") String token,
                                       @Body HashMap<String, String> data);

    @POST(UrlUtils.UPDATE_PRODUCT_STATUS)
    Call<JsonObject> updateProductStatus(@Header("Authorization") String token,
                                         @Body HashMap<String, String> data);

    @Multipart
    @POST(UrlUtils.IMAGE_UPLOAD)
    Call<JsonObject> imageUpload(@Header("Authorization") String token,
                                 @Header("Content_Type") String contentType,
                                 @Part MultipartBody.Part image);

    @GET(UrlUtils.CHECK_UPDATE)
    Call<JsonObject> checkUpdate();

    @GET(UrlUtils.EVALY_USERS)
    Call<JsonObject> searchEvalyUsers(@Header("Authorization") String token,
                                      @Query("search") String search,
                                      @Query("page") int page);

    @POST(UrlUtils.NEWS_FEED)
    Call<JsonObject> createPost(@Header("Authorization") String header,
                                @Body HashMap<String, CreatePostModel> data);

    @POST(UrlUtils.SUBMIT_ISSUE + "{invoice}/" + "order-issues/")
    Call<JsonObject> submitIssue(@Header("Authorization") String header,
                                 @Path("invoice") String invoice,
                                 @Body HashMap<String, OrderIssueModel> data);

    @POST(UrlUtils.BASE_URL + "order-issues/{invoice}/issue-replies")
    Call<JsonObject> replyIssue(@Header("Authorization") String header,
                                @Path("invoice") String invoice,
                                @Body HashMap<String, HashMap> data);

    @GET(UrlUtils.SUBMIT_ISSUE + "{invoice}/" + "order-issues/")
    Call<JsonObject> getIssueList(@Header("Authorization") String header,
                                  @Path("invoice") String invoice);

    @GET(UrlUtils.DOMAIN + "ratings/api/v1/public/ads")
    Call<CommonResultResponse<JsonObject>> getBanners();

    // balance, transaction

    @GET(UrlUtils.DOMAIN + "pay/wallet-history/{username}")
    Call<CommonDataResponse<List<TransactionItem>>> getTransactionHistory(@Header("Authorization") String token,
                                                                          @Path("username") String username,
                                                                          @Query("page") int page);


    @GET(UrlUtils.DOMAIN + "pay/apply/cashback-balance/{username}/")
    Call<JsonObject> claimCashBack(@Header("Authorization") String header,
                                   @Path("username") String username);

    @PUT(UrlUtils.DOMAIN + "pay/withdraw_refund_request/{invoice}")
    Call<CommonDataResponse> withdrawRefundRequest(@Header("Authorization") String header,
                                                   @Path("invoice") String invoice);

    // product APIs

    @GET(UrlUtils.BASE_CATALOG + "categories")
    Call<CommonDataResponse<List<ChildCategoryResponse>>> getChildCategory(@Query("parent") String parentCategorySlug);

    @GET(UrlUtils.BASE_CATALOG + "brands")
    Call<CommonDataResponse<List<BrandResponse>>> getBrands(@Query("category") String categorySlug,
                                                            @Query("search") String search,
                                                            @Query("page") int page,
                                                            @Query("limit") int limit);

    @GET(UrlUtils.BASE_CATALOG + "shops")
    Call<CommonDataResponse<List<ShopListResponse>>> getShops(@Query("category_slug") String categorySlug,
                                                              @Query("search") String search,
                                                              @Query("page") int page,
                                                              @Query("limit") int limit);

    @GET(UrlUtils.BASE_CATALOG + "products")
    Call<CommonResultResponse<List<ProductItem>>> getCategoryBrandProducts(@Query("page") int page,
                                                                           @Query("category") String category,
                                                                           @Query("brand") String brand,
                                                                           @Query("limit") int limit);

    @GET(UrlUtils.BASE_URL + "public/shops/items/{shopSlug}/")
    Call<JsonObject> getShopProducts(@Path("shopSlug") String shopSlug,
                                     @Query("page") int page,
                                     @Query("limit") int limit,
                                     @Query("category_slug") String categorySlug,
                                     @Query("search") String search);

    @GET(UrlUtils.CAMPAIGNS + "/{campaignSlug}/shops/{shopSlug}/items")
    Call<JsonObject> getCampaignShopProducts(@Path("campaignSlug") String campaignSlug,
                                             @Path("shopSlug") String shopSlug,
                                             @Query("page") int page,
                                             @Query("limit") int limit,
                                             @Query("category_slug") String categorySlug,
                                             @Query("search") String search);

    @GET(UrlUtils.BASE_CATALOG + "/shop-items/{shopSlug}/items/{shopItem}/variants")
    Call<CommonDataResponse<List<ShopItem>>> getProductVariants(@Path("shopSlug") String shopSlug,
                                                                @Path("shopItem") String shopItem);

    @GET(UrlUtils.BASE_CATALOG + "shop-items/shops/{variantId}")
    Call<CommonDataResponse<List<AvailableShopModel>>> getAvailableShop(@Path("variantId") int variantId);

    @GET(UrlUtils.BASE_CATALOG + "shop-items/shops/{variantId}/nearest")
    Call<CommonDataResponse<List<AvailableShopModel>>> getNearestAvailableShop(@Path("variantId") int variantId,
                                                                               @Query("long") double longitude,
                                                                               @Query("lat") double latitude);
    // with token

    @GET(UrlUtils.BASE_CATALOG + "shop-items/{shopSlug}/items")
    Call<ShopDetailsModel> getShopDetails(@Header("Authorization") String token,
                                          @Path("shopSlug") String shopSlug,
                                          @Query("page") int page,
                                          @Query("limit") int limit,
                                          @Query("category_slug") String categorySlug,
                                          @Query("search") String search);

    @GET(UrlUtils.BASE_CATALOG + "shops/{shopSlug}")
    Call<CommonDataResponse<ShopDetailsResponse>> getShopDetails(@Header("Authorization") String token,
                                                                 @Path("shopSlug") String shopSlug);

    @GET(UrlUtils.BASE_CATALOG + "campaign/shops/{shopSlug}")
    Call<CommonDataResponse<ShopDetailsResponse>> getCampaignShopDetails(@Header("Authorization") String token,
                                                                         @Path("shopSlug") String shopSlug);

    @GET(UrlUtils.BASE_CATALOG + "campaign/products/categories")
    Call<CommonDataResponse<List<CampaignProductCategoryResponse>>> getCampaignProductCategories(@Query("category") String categorySlug,
                                                                                                 @Query("campaign") String campaignSlug,
                                                                                                 @Query("search") String search,
                                                                                                 @Query("page") int page,
                                                                                                 @Query("limit") int limit);

    @GET(UrlUtils.CAMPAIGNS + "/{campaignSlug}/shops/{shopSlug}/items")
    Call<ShopDetailsModel> getCampaignShopDetails(@Header("Authorization") String token,
                                                  @Path("campaignSlug") String campaignSlug,
                                                  @Path("shopSlug") String shopSlug,
                                                  @Query("page") int page,
                                                  @Query("limit") int limit,
                                                  @Query("category_slug") String categorySlug,
                                                  @Query("search") String search,
                                                  @Query("brand_slug") String brandSlug);

    @GET(UrlUtils.BASE_CATALOG + "products/{slug}")
    Call<ProductDetailsModel> getProductDetails(@Path("slug") String slug);

    // Categories API

    @GET(UrlUtils.CATEGORIES)
    Call<JsonArray> getCategories(@Query("parent") String parent);

    @GET(UrlUtils.CATEGORIES)
    Call<JsonArray> getCategories();


    @GET(UrlUtils.CATEGORIES_BRANDS)
    Call<JsonObject> getBrandsCategories(@Query("category") String category,
                                         @Query("page") int page,
                                         @Query("limit") int limit);


    @GET(UrlUtils.CATEGORIES_SHOPS_ROOT)
    Call<JsonObject> getShopsOfCategories(@Query("page") int page,
                                          @Query("limit") int limit);

    @GET(UrlUtils.CATEGORIES_SHOPS + "{category}/")
    Call<JsonObject> getShopsOfCategories(@Path("category") String category,
                                          @Query("page") int page,
                                          @Query("limit") int limit);


    @GET(UrlUtils.BASE_CATALOG + "/shop-items/{shopSlug}/categories")
    Call<JsonObject> getCategoriesofShop(@Path("shopSlug") String shopSlug,
                                         @Query("page") int page);

    @GET(UrlUtils.BASE_CATALOG + "/brands/{slug}/categories")
    Call<CommonDataResponse<BrandCatResponse>> getCategoriesOfBrand(@Path("slug") String slug,
                                                                    @Query("page") int page);


    @GET(UrlUtils.CAMPAIGNS + "/{campaignSlug}/shops/{shopSlug}/categories")
    Call<JsonObject> getCategoriesOfCampaignShop(@Path("campaignSlug") String campaignSlug,
                                                 @Path("shopSlug") String shopSlug,
                                                 @Query("page") int page);


    // campaign APIs

    @GET(UrlUtils.CAMPAIGNS)
    Call<CommonDataResponse<List<CampaignItem>>> getCampaigns(@Query("page") int page,
                                                              @Query("search") String search);

    @GET(UrlUtils.CAMPAIGNS + "/{group}/shops")
    Call<CommonDataResponse<List<CampaignShopItem>>> getCampaignShops(@Path("group") String group,
                                                                      @Query("page") int page,
                                                                      @Query("limit") int limit);


    @GET(UrlUtils.BASE_URL + "shop-group-shops/{group}")
    Call<CommonDataResponse<List<GroupShopModel>>> getShopByGroup(@Path("group") String group,
                                                                  @Query("page") int page,
                                                                  @Query("limit") int limit,
                                                                  @Query("area") String area,
                                                                  @Query("search") String search);


    // Root Category

    @GET(UrlUtils.BASE_CATALOG + "categories")
    Call<CommonDataResponse<List<CategoryEntity>>> getRootCategories(@Query("limit") int limit);


    @GET(UrlUtils.BASE_CATALOG + "products/top-categories")
    Call<CommonDataResponse<List<CategoryEntity>>> getTopCategories();

    @GET(UrlUtils.BASE_CATALOG + "categories")
    Call<CommonDataResponse<List<CategoryEntity>>> getSubCategories(@Query("parent") String parent);

    // Order APIs
    @GET(UrlUtils.ORDERS)
    Call<CommonResultResponse<List<OrderListItem>>> getOrderList(@Header("Authorization") String token,
                                                                 @Query("page") int page,
                                                                 @Query("order_status") String orderStatus);

    @PUT(UrlUtils.BASE_URL + "orders/customer/cancel-order/{invoice_no}/")
    Call<CommonDataResponse> cancelOrder(@Header("Authorization") String token,
                                         @Path("invoice_no") String invoiceNo,
                                         @Body HashMap<String, String> body);

    @PUT(UrlUtils.BASE_URL + "orders/customer/deliver-order/{invoice_no}/")
    Call<CommonDataResponse> confirmDelivery(@Header("Authorization") String token,
                                             @Path("invoice_no") String invoiceNo);


    // brand

    @GET(UrlUtils.BASE_CATALOG + "brands/{brandSlug}")
    Call<CommonDataResponse<BrandResponse>> getBrandDetails(@Path("brandSlug") String brandSlug);


    // Notification

    @GET(UrlUtils.DOMAIN + "{notificationSource}/notifications_count/")
    Call<NotificationCount> getNotificationCount(@Header("Authorization") String token,
                                                 @Path("notificationSource") String notificationType);

    // shop releated

    @GET
    Call<ShopDetailsModel> getShopDetailsItems(@Header("Authorization") String token,
                                               @Url String url);

    @POST(UrlUtils.BASE_URL + "shop-subscriptions")
    Call<JsonObject> subscribeToShop(@Header("Authorization") String token,
                                     @Body HashMap<String, String> shopSlug);


    @DELETE(UrlUtils.BASE_URL + "unsubscribe-shop/{shop_slug}/")
    Call<JsonObject> unsubscribeShop(@Header("Authorization") String token,
                                     @Path("shop_slug") String shopSlug);

    @GET(UrlUtils.DOMAIN + "ratings/api/v1/public/overview")
    Call<JsonObject> getShopReviews(@Header("Authorization") String token,
                                    @Query("slug") String slug);

    @GET(UrlUtils.BASE_URL + "shop-subscriptions")
    Call<JsonObject> getFollowedShops(@Header("Authorization") String token);

    // referral

    @POST("https://nsuer.club/evaly/referral/submit-referral.php")
    Call<JsonObject> checkReferral(@Query("device_id") String deviceId,
                                   @Query("referred_by") String referredBy,
                                   @Query("token") String token);


    // order apis

    @POST(UrlUtils.DOMAIN + "order-request-go/api/v1/order-requests/place-order")
    Call<JsonObject> placeOrder(@Header("Authorization") String token,
                                @Body JsonObject body);


    @POST(UrlUtils.DOMAIN + "order-request-go/api/v1/order-requests/place-order")
    Call<CommonDataResponse<List<JsonObject>>> placeOrder(@Header("Authorization") String token,
                                                          @Body PlaceOrderItem body);

    @GET(UrlUtils.BASE_URL + "custom/orders/{invoiceNo}/")
    Call<OrderDetailsModel> getOrderDetails(@Header("Authorization") String token,
                                            @Path("invoiceNo") String invoiceNo);


    @POST(UrlUtils.BASE_URL + "orders/delivery-address/update/")
    Call<CommonDataResponse<OrderDetailsModel>> updateOrderAddress(@Header("Authorization") String token,
                                                                   @Body UpdateOrderAddressRequest body);

    @GET(UrlUtils.BASE_URL + "orders/histories/{invoiceNo}/")
    Call<JsonObject> getOrderHistories(@Header("Authorization") String token,
                                       @Path("invoiceNo") String invoiceNo);

    // gift card

    @POST(UrlUtils.DOMAIN + "pay/transactions/payment/order/gift-code/")
    Call<JsonObject> payWithGiftCard(@Header("Authorization") String token,
                                     @Body HashMap<String, String> body);

    @GET(UrlUtils.DOMAIN + "cpn/gift-cards/custom/list")
    Call<CommonDataResponse<List<GiftCardListItem>>> getGiftCardList(@Query("page") int page);

    @POST(UrlUtils.DOMAIN + "cpn/gift-card-orders/place/")
    Call<JsonObject> placeGiftCardOrder(@Header("Authorization") String token,
                                        @Body JsonObject body);

    @GET(UrlUtils.DOMAIN + "cpn/gift-cards/retrieve/{slug}")
    Call<JsonObject> getGiftCardDetails(@Path("slug") String slug);

    @GET(UrlUtils.DOMAIN + "cpn/gift-card-orders")
    Call<CommonDataResponse<List<GiftCardListPurchasedItem>>> getPurchasedGiftCardList(@Header("Authorization") String token,
                                                                                       @Query("show") String show,
                                                                                       @Query("page") int page);

    @POST(UrlUtils.DOMAIN + "cpn/gift-card-orders/gift-code/retrieve")
    Call<JsonObject> redeemGiftCard(@Header("Authorization") String token,
                                    @Body HashMap<String, String> invoiceNo);

    // payment

    @POST(UrlUtils.DOMAIN + "pay/transactions/payment/order/")
    Call<JsonObject> makePartialPayment(@Header("Authorization") String token,
                                        @Body ParitalPaymentModel body);

    @POST(UrlUtils.DOMAIN + "core/orders/payment/cod/")
    Call<JsonObject> makeCashOnDelivery(@Header("Authorization") String token,
                                        @Body HashMap<String, String> body);


    @POST(UrlUtils.DOMAIN + "pay/pg")
    Call<JsonObject> payViaCard(@Header("Authorization") String token,
                                @Body HashMap<String, String> body);

    @POST(UrlUtils.DOMAIN + "pay/city-bank")
    Call<JsonObject> payViaCityBank(@Header("Authorization") String token,
                                    @Body HashMap<String, String> body);

    @POST(BuildConfig.WEB_URL + "sebl/payment")
    Call<JsonObject> payViaSEBL(@Header("Authorization") String token,
                                @Query("amount") String amount,
                                @Query("invoice") String invoice,
                                @Query("token") String authToken,
                                @Query("context_reference") String context);

    @POST(BuildConfig.NAGAD_URL)
    Call<JsonObject> payViaNagad(@Header("Authorization") String token,
                                 @Body HashMap<String, String> body);

    @POST(UrlUtils.DOMAIN + "pay/bank_deposit/")
    Call<JsonObject> payViaBank(@Header("Authorization") String token,
                                @Body HashMap<String, String> body);

    // reviews
    @GET(UrlUtils.DOMAIN + "ratings/api/v1/public/all-reviews")
    Call<CommonDataResponse<JsonObject>> getShopReviews(@Header("Authorization") String token,
                                                        @Query("slug") String shopSlug,
                                                        @Query("page") int page,
                                                        @Query("limit") int limit);

    @POST(UrlUtils.BASE_URL + "add-review/{shopSlug}/")
    Call<JsonObject> postShopReview(@Header("Authorization") String token,
                                    @Path("shopSlug") String slug,
                                    @Body JsonObject body);

    @GET(UrlUtils.BASE_URL + "review-eligibility/{shopSlug}/")
    Call<JsonObject> checkShopReviewEligibility(@Header("Authorization") String token,
                                                @Path("shopSlug") String slug);


    // product reviews

    @GET(UrlUtils.BASE_URL + "public/product-review-summary/{sku}")
    Call<JsonObject> getProductReviewSummary(@Header("Authorization") String token,
                                             @Path("sku") String sku);

    @GET(UrlUtils.BASE_URL + "public/product-reviews/{slug}/")
    Call<CommonDataResponse<List<ReviewItem>>> getProductReviews(@Header("Authorization") String token,
                                                                 @Path("slug") String shopSlug,
                                                                 @Query("page") int page,
                                                                 @Query("limit") int limit);

    @POST(UrlUtils.BASE_URL + "add-product-review/{slug}/")
    Call<JsonObject> postProductReview(@Header("Authorization") String token,
                                       @Path("slug") String slug,
                                       @Body JsonObject body);

    @GET(UrlUtils.BASE_URL + "product-review-eligibility/{slug}/")
    Call<JsonObject> checkProductReviewEligibility(@Header("Authorization") String token,
                                                   @Path("slug") String slug);

    // Newsfeed

    @GET
    Call<JsonObject> getNewsfeedPosts(@Header("Authorization") String token,
                                      @Url String url);


    @GET
    Call<CommonDataResponse<List<NewsfeedPost>>> getNewsfeedPostsList(@Header("Authorization") String token,
                                                                      @Url String url);

    @GET(UrlUtils.BASE_URL_NEWSFEED + "posts/{post_id}")
    Call<JsonObject> getNewsfeedPostDetails(@Header("Authorization") String token,
                                            @Path("post_id") String postId);

    @GET(UrlUtils.BASE_URL_NEWSFEED + "posts/{selectedPostId}/comments/{commentId}/replies")
    Call<JsonObject> getNewsfeedReplies(@Header("Authorization") String token,
                                        @Path("selectedPostId") String postId,
                                        @Path("commentId") int commentId,
                                        @Query("page") int page);

    @GET(UrlUtils.BASE_URL_NEWSFEED + "posts/{selectedPostId}/comments/{commentId}/replies")
    Call<CommonDataResponse<List<CommentItem>>> getNewsfeedRepliesList(@Header("Authorization") String token,
                                                                       @Path("selectedPostId") String postId,
                                                                       @Path("commentId") int commentId,
                                                                       @Query("page") int page);

    @Multipart
    @POST(UrlUtils.IMAGE_UPLOAD)
    Call<CommonDataResponse<ImageDataModel>> imageUploadNew(@Header("Authorization") String token,
                                                            @Header("Content_Type") String contentType,
                                                            @Part MultipartBody.Part image);

    @GET(UrlUtils.BASE_URL_NEWSFEED + "posts/{postId}/comments")
    Call<JsonObject> getNewsfeedComments(@Header("Authorization") String token,
                                         @Path("postId") String postId,
                                         @Query("page") int page);

    @GET(UrlUtils.BASE_URL_NEWSFEED + "posts/{postId}/comments")
    Call<CommonDataResponse<List<CommentItem>>> getNewsfeedCommentsList(@Header("Authorization") String token,
                                                                        @Path("postId") String postId,
                                                                        @Query("page") int page);

    @POST(UrlUtils.BASE_URL_NEWSFEED + "posts/{selectedPostID}/comments")
    Call<JsonObject> postNewsfeedComment(@Header("Authorization") String token,
                                         @Path("selectedPostID") String selectedPostID,
                                         @Body JsonObject body);

    @POST(UrlUtils.BASE_URL_NEWSFEED + "posts/{selectedPostID}/comments/{selectedCommentID}/replies")
    Call<JsonObject> postNewsfeedReply(@Header("Authorization") String token,
                                       @Path("selectedPostID") String selectedPostID,
                                       @Path("selectedCommentID") int selectedCommentID,
                                       @Body JsonObject body);

    @POST(UrlUtils.BASE_URL_NEWSFEED + "posts/{slug}/favorite")
    Call<JsonObject> likeNewsfeedPost(@Header("Authorization") String token,
                                      @Path("slug") String postSlug);

    @DELETE(UrlUtils.BASE_URL_NEWSFEED + "posts/{slug}/favorite")
    Call<JsonObject> dislikeNewsfeedPost(@Header("Authorization") String token,
                                         @Path("slug") String postSlug);

    @DELETE
    Call<JsonObject> deleteNewsfeedItem(@Header("Authorization") String token,
                                        @Url String url);

    @PUT(UrlUtils.BASE_URL_NEWSFEED + "posts/{postId}")
    Call<JsonObject> approvePendingNewsfeedPost(@Header("Authorization") String token,
                                                @Path("postId") String postId,
                                                @Body JsonObject body);

    @DELETE(UrlUtils.BASE_URL_NEWSFEED + "posts/{postId}")
    Call<JsonObject> deletePendingNewsfeedPost(@Header("Authorization") String token,
                                               @Path("postId") String postId,
                                               @Body JsonObject body);

    @POST(UrlUtils.BASE_URL_NEWSFEED + "posts")
    Call<JsonObject> createNewsfeedPost(@Header("Authorization") String token,
                                        @Body CreatePostModel body);

    @PUT(UrlUtils.BASE_URL_NEWSFEED + "posts/{postSlug}")
    Call<JsonObject> editNewsfeedPost(@Header("Authorization") String token,
                                      @Body CreatePostModel body,
                                      @Path(("postSlug")) String postSlug);

    // newsfeed notifation

    @GET(UrlUtils.BASE_URL_NEWSFEED + "notifications")
    Call<CommonResultResponse<List<NotificationItem>>> getNewsfeedNotification(@Header("Authorization") String token,
                                                                               @Query("page") int page);

    @GET(UrlUtils.BASE_URL + "notifications/")
    Call<CommonResultResponse<List<NotificationItem>>> getNotification(@Header("Authorization") String token,
                                                                       @Query("page") int page);

    @GET(UrlUtils.BASE_URL_NEWSFEED + "update-notifications/")
    Call<JsonObject> markNewsfeedNotificationAsRead(@Header("Authorization") String token);

    @GET(UrlUtils.BASE_URL + "update-notifications/")
    Call<JsonObject> markNotificationAsRead(@Header("Authorization") String token);

    @GET(UrlUtils.BASE_URL_NEWSFEED + "notifications_count/")
    Call<NotificationCount> getNewsfeedNotificationCount(@Header("Authorization") String token);

    // refund
    @POST(UrlUtils.BASE_URL + "/orders/request-refund")
    Call<CommonDataResponse<String>> postRequestRefund(@Header("Authorization") String token,
                                                       @Body HashMap<String, String> body);

    @POST(UrlUtils.BASE_URL + "confirm/refund-request/{invoice}")
    Call<CommonDataResponse<String>> postRequestRefundConfirmOTP(@Header("Authorization") String token,
                                                                 @Body HashMap<String, Integer> body,
                                                                 @Path("invoice") String slug);


    @GET(UrlUtils.DOMAIN + "pay/refund-eligibility/{invoice}")
    Call<CommonDataResponse<String>> checkRefundEligibility(@Header("Authorization") String token,
                                                            @Path("invoice") String slug);

    @DELETE(UrlUtils.DOMAIN + "pay/delete-refund-transaction/{invoice}")
    Call<CommonDataResponse<String>> deleteRefundTransaction(@Header("Authorization") String token,
                                                             @Path("invoice") String slug);


    // auth 2.0
    @POST(UrlUtils.BASE_AUTH + "users/register")
    Call<LoginResponse> authLogin(@Body LoginBody body);

    @POST(UrlUtils.BASE_AUTH + "users/register")
    Call<RegisterResponse> authRegister(@Body RegisterBody body);

    @POST(UrlUtils.BASE_AUTH + "users/set-user-password")
    Call<SetPasswordResponse> authSetPassword(@Body SetPasswordBody body);


    // evaly express services
    @GET(UrlUtils.BASE_CATALOG + "express")
    Call<CommonDataResponse<List<ExpressServiceModel>>> getExpressServicesList();

    @GET(UrlUtils.BASE_CATALOG + "express/{slug}")
    Call<CommonDataResponse<ExpressServiceDetailsModel>> getExpressServiceDetails(@Path("slug") String slug);

    @GET(UrlUtils.BASE_CATALOG + "shops/express")
    Call<CommonResultResponse<List<GroupShopModel>>> getExpressShopList(@Query("express_slug") String serviceSlug,
                                                                        @Query("page") int page,
                                                                        @Query("limit") int limit,
                                                                        @Query("address") String address,
                                                                        @Query("address") String address2,
                                                                        @Query("search") String search,
                                                                        @Query("longitude") Double longitude,
                                                                        @Query("latitude") Double latitude);

    @GET(UrlUtils.BASE_URL + "public/express-products/")
    Call<CommonResultResponse<List<ProductItem>>> getExpressProductList(@Query("express_service") String serviceSlug,
                                                                        @Query("page") int page,
                                                                        @Query("limit") int limit,
                                                                        @Query("search") String search);

    @GET(UrlUtils.BASE_URL + "delivery/orders/{invoice_no}/hero")
    Call<DeliveryHeroResponse> getDeliveryHero(@Header("Authorization") String token,
                                               @Path("invoice_no") String invoice);

}
