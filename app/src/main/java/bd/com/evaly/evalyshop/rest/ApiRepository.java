package bd.com.evaly.evalyshop.rest;

import android.graphics.Bitmap;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.listener.ResponseListener;
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
import bd.com.evaly.evalyshop.models.campaign.topProducts.CampaignTopProductResponse;
import bd.com.evaly.evalyshop.models.cart.CartHolderModel;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandCatResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.BrandResponse;
import bd.com.evaly.evalyshop.models.catalog.brands.CategoriesItem;
import bd.com.evaly.evalyshop.models.catalog.category.ChildCategoryResponse;
import bd.com.evaly.evalyshop.models.catalog.location.LocationResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopDetailsResponse;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopListResponse;
import bd.com.evaly.evalyshop.models.express.ExpressServiceDetailsModel;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;
import bd.com.evaly.evalyshop.models.hero.DeliveryHeroResponse;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.models.issue.IssueAnswerResponse;
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
import bd.com.evaly.evalyshop.models.order.OrderListItem;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.models.order.payment.ParitalPaymentModel;
import bd.com.evaly.evalyshop.models.order.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.models.order.updateAddress.UpdateOrderAddressRequest;
import bd.com.evaly.evalyshop.models.orderRequest.OrderRequestResponse;
import bd.com.evaly.evalyshop.models.pay.BalanceResponse;
import bd.com.evaly.evalyshop.models.points.PointsResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopResponse;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductDetailsModel;
import bd.com.evaly.evalyshop.models.profile.AddressRequest;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.models.profile.AddressWholeResponse;
import bd.com.evaly.evalyshop.models.profile.UserInfoResponse;
import bd.com.evaly.evalyshop.models.refundSettlement.OtpResponse;
import bd.com.evaly.evalyshop.models.refundSettlement.RefundSettlementResponse;
import bd.com.evaly.evalyshop.models.refundSettlement.request.BankAccountRequest;
import bd.com.evaly.evalyshop.models.refundSettlement.request.MFSAccountRequest;
import bd.com.evaly.evalyshop.models.reviews.ReviewSummaryModel;
import bd.com.evaly.evalyshop.models.search.product.SearchRequest;
import bd.com.evaly.evalyshop.models.search.product.response.ProductSearchResponse;
import bd.com.evaly.evalyshop.models.shop.FollowResponse;
import bd.com.evaly.evalyshop.models.shop.GroupShopModel;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.shop.shopItem.ShopItem;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.models.transaction.TransactionItem;
import bd.com.evaly.evalyshop.models.user.UserModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ApiRepository {

    private IApiClient apiService;
    private PreferenceRepository preferenceRepository;
    private ApiHandler apiHandler;

    @Inject
    public ApiRepository(IApiClient apiService,
                         PreferenceRepository preferenceRepository,
                         ApiHandler apiHandler) {
        this.apiService = apiService;
        this.preferenceRepository = preferenceRepository;
        this.apiHandler = apiHandler;
    }

    /* ------------- Auth APIs ------------- */

    public void getPoints(ResponseListener<CommonDataResponse<PointsResponse>, String> listener){
        apiHandler.createCall(apiService.getPoints("evaly"), listener);
    }

    public void saveSettlementMFSAccount(String type, MFSAccountRequest body, ResponseListener<CommonDataResponse<RefundSettlementResponse>, String> listener) {
        if (type.equalsIgnoreCase("bkash"))
            apiHandler.createCall(apiService.saveSettlementBkashAccount(body), listener);
        else if (type.equalsIgnoreCase("nagad"))
            apiHandler.createCall(apiService.saveSettlementNagadAccount(body), listener);
    }

    public void saveSettlementBankAccount(BankAccountRequest body, ResponseListener<CommonDataResponse<RefundSettlementResponse>, String> listener) {
        apiHandler.createCall(apiService.saveSettlementBankAccount(body), listener);
    }

    public void getSettlementAccounts(String token, String otp, String requestId, ResponseListener<CommonDataResponse<RefundSettlementResponse>, String> listener) {
        HashMap<String, String> body = new HashMap<>();
        body.put("otp", otp);
        body.put("request_id", requestId);
        apiHandler.createCall(apiService.getSettlementAccounts(body), listener);
    }

    public void generateOtp(String token, ResponseListener<CommonDataResponse<OtpResponse>, String> listener) {
        apiHandler.createCall(apiService.generateOtp(), listener);
    }

    public void getCaptcha(ResponseListener<CommonDataResponse<CaptchaResponse>, String> listener) {
        apiHandler.createCall(apiService.getCaptcha(), listener);
    }


    public void setPassword(HashMap<String, String> model, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.setPassword(model), listener);
    }

    public void register(HashMap<String, String> data, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.register(data), listener);
    }


    public void login(HashMap<String, String> data, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.login(data), listener);
    }


    public void changeXmppPassword(HashMap<String, String> data, ResponseListener<JsonPrimitive, String> listener) {
        apiHandler.createCall(apiService.changeXmppPassword(data), listener);
    }

    public void registerXMPP(HashMap<String, String> data, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.registerXmpp(data), listener);
    }

    // user info pay

    public void getUserInfoPay(String token, String username, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.getUserInfoPay(username), listener);
    }

    public void getUserProfile(String token, ResponseListener<CommonDataResponse<UserModel>, String> listener) {
        apiHandler.createCall(apiService.getUserProfile(), listener);
    }

    public void withdrawRefundRequest(String url, String invoice, ResponseListener<CommonDataResponse, String> listener) {
        url += invoice;
        apiHandler.createCall(apiService.withdrawRefundRequest(url), listener);
    }

    // change password

    public void changePassword(String token, HashMap<String, String> body, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.changePassword(body), listener);
    }

    public void logout(ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.logout(), listener);
    }

    // update profile data

    public void setUserData(String token, HashMap<String, String> body, ResponseListener<CommonDataResponse<UserModel>, String> listener) {
        apiHandler.createCall(apiService.setUserData(body), listener);
    }

    public void setUserData(String token, JsonObject body, ResponseListener<CommonDataResponse<UserModel>, String> listener) {
        apiHandler.createCall(apiService.setUserData(body), listener);
    }

    public void addUserData(String token, HashMap<String, String> body, ResponseListener<CommonDataResponse<UserInfoResponse>, String> listener) {
        apiHandler.createCall(apiService.addUserData(body), listener);
    }

    public void getUserInfo(ResponseListener<CommonDataResponse<UserInfoResponse>, String> listener) {
        apiHandler.createCall(apiService.getUserInfo(), listener);
    }

    public void setUserDataToXmpp(HashMap<String, String> body, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.setUserDataToXmpp(body), listener);
    }

    // balance, transaction
    public void getTransactionHistory(String token, String username, int page, ResponseListener<CommonDataResponse<List<TransactionItem>>, String> listener) {
        apiHandler.createCall(apiService.getTransactionHistory(username, page), listener);
    }

    // forget password

    public void forgetPassword(HashMap<String, String> body, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.forgetPassword(body), listener);
    }

    public void getUserAddress(ResponseListener<CommonDataResponse<AddressWholeResponse>, String> listener) {
        apiHandler.createCall(apiService.getAddressList(), listener);
    }

    public void addAddress(AddressRequest body, ResponseListener<CommonDataResponse<AddressResponse>, String> listener) {
        apiHandler.createCall(apiService.addAddress(body), listener);
    }

    public void updateAddress(String id, AddressRequest body, ResponseListener<CommonDataResponse<AddressResponse>, String> listener) {
        apiHandler.createCall(apiService.updateAddress(id, body), listener);
    }

    public void removeAddress(String id, ResponseListener<CommonDataResponse, String> listener) {
        apiHandler.createCall(apiService.removeAddress(id), listener);
    }

    // auth 2.0

    public void authLogin(LoginBody body, ResponseListener<LoginResponse, String> listener) {
        apiHandler.createCall(apiService.authLogin(body), listener);
    }

    public void authRegister(RegisterBody body, ResponseListener<RegisterResponse, String> listener) {
        apiHandler.createCall(apiService.authRegister(body), listener);
    }


    public void authSetPassword(SetPasswordBody body, ResponseListener<SetPasswordResponse, String> listener) {
        apiHandler.createCall(apiService.authSetPassword(body), listener);
    }


    public void updateProductStatus(HashMap<String, String> data, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.updateProductStatus(data), listener);
    }


    /* ------------- Campaign APIs ------------- */


    public void getCampaignProductCategories(String category, String campaignSlug, String search, int page, ResponseListener<CommonDataResponse<List<CampaignProductCategoryResponse>>, String> listener) {
        apiHandler.createCall(apiService.getCampaignProductCategories(category, campaignSlug, search, page, 30), listener);
    }

    public void getSubCampaignDetails(String campaignSlug, ResponseListener<CommonDataResponse<SubCampaignDetailsResponse>, String> listener) {
        apiHandler.createCall(apiService.getSubCampaignDetails(campaignSlug), listener);
    }

    public void getCampaignCarousel(String context, ResponseListener<CommonDataResponse<List<CampaignCarouselResponse>>, String> listener) {
        apiHandler.createCall(apiService.getCampaignCarousel(context), listener);
    }

    public void getCampaignBanners(ResponseListener<CommonDataResponse<List<CampaignBannerResponse>>, String> listener) {
        apiHandler.createCall(apiService.getCampaignBanners(), listener);
    }

    public void getCampaignCategoryCampaigns(int page, int limit, String search, String category, ResponseListener<CommonDataResponse<List<SubCampaignResponse>>, String> listener) {
        apiHandler.createCall(apiService.getCampaignCategoryCampaigns(page, limit, search, category), listener);
    }

    public void getCampaignCategoryProducts(int page, int limit, String search, String category, String campaign, String productCategory, String priceSort, ResponseListener<CommonDataResponse<List<CampaignProductResponse>>, String> listener) {
        apiHandler.createCall(apiService.getCampaignCategoryProducts(page, limit, search, category, campaign, productCategory, priceSort), listener);
    }

    public void getCampaignCategoryBrands(int page, int limit, String search, String category, String campaign, ResponseListener<CommonDataResponse<List<CampaignBrandResponse>>, String> listener) {
        apiHandler.createCall(apiService.getCampaignCategoryBrands(page, limit, search, category, campaign), listener);
    }

    public void getCampaignCategoryShops(int page, int limit, String search, String category, String campaign, ResponseListener<CommonDataResponse<List<CampaignShopResponse>>, String> listener) {
        apiHandler.createCall(apiService.getCampaignCategoryShops(page, limit, search, category, campaign), listener);
    }

    public void getCampaignAllProducts(int page, int limit, String search, ResponseListener<CommonDataResponse<List<CampaignProductResponse>>, String> listener) {
        apiHandler.createCall(apiService.getCampaignAllProducts(page, limit, search), listener);
    }

    public void getCampaignCategory(ResponseListener<CommonDataResponse<List<CampaignCategoryResponse>>, String> listener) {
        apiHandler.createCall(apiService.getCampaignCategory(), listener);
    }

    public void getCampaignCategoryTopProducts(ResponseListener<CommonDataResponse<List<CampaignTopProductResponse>>, String> listener) {
        apiHandler.createCall(apiService.getCampaignCategoryTopProducts(), listener);
    }

    public void getCampaigns(int page, String search, ResponseListener<CommonDataResponse<List<CampaignItem>>, String> listener) {
        apiHandler.createCall(apiService.getCampaigns(page, search), listener);
    }

    public void getCampaignShops(String group, int page, ResponseListener<CommonDataResponse<List<CampaignShopItem>>, String> listener) {
        apiHandler.createCall(apiService.getCampaignShops(group, page, 21), listener);
    }




    /* ------------- Brand APIs ------------- */

    public void getBrandsDetails(String brandSlug, ResponseListener<CommonDataResponse<BrandResponse>, String> listener) {
        apiHandler.createCall(apiService.getBrandDetails(brandSlug), listener);
    }


    public void getCampaignCategories(String brandSlug, String campaignSlug, int page, ResponseListener<CommonDataResponse<List<CategoriesItem>>, String> listener) {
        apiHandler.createCall(apiService.getCategoriesOfBrand(brandSlug, campaignSlug, page), listener);
    }

    public void getCategories(String brandSlug, int page, ResponseListener<CommonDataResponse<BrandCatResponse>, String> listener) {
        apiHandler.createCall(apiService.getCategoriesOfBrand(brandSlug, page), listener);
    }


    public void getAppointmentList(int page, ResponseListener<CommonDataResponse<List<AppointmentResponse>>, String> listener) {
        apiHandler.createCall(apiService.getAppointmentList(page), listener);
    }

    public void getAppointmentCategoryList(ResponseListener<CommonDataResponse<List<AppointmentCategoryResponse>>, String> listener) {
        apiHandler.createCall(apiService.getAppointmentCategoryList(200), listener);
    }

    public void getAppointmentTimeSlotList(String date, ResponseListener<CommonDataResponse<List<AppointmentTimeSlotResponse>>, String> listener) {
        apiHandler.createCall(apiService.getAppointmentTimeSlotList(date), listener);
    }

    public void createAppointmentComment(AppointmentCommentRequest body, ResponseListener<CommonDataResponse<AppointmentCommentResponse>, String> listener) {
        apiHandler.createCall(apiService.createAppointmentComment(body), listener);
    }

    public void getAppointmentCommentList(String id, int page, ResponseListener<CommonDataResponse<List<AppointmentCommentResponse>>, String> listener) {
        apiHandler.createCall(apiService.getAppointmentCommentList(id, page), listener);
    }

    public void createAppointment(AppointmentRequest body, ResponseListener<CommonDataResponse<AppointmentResponse>, String> listener) {
        apiHandler.createCall(apiService.createAppointment(body), listener);
    }

    public void cancelAppointment(String id, ResponseListener<CommonDataResponse<AppointmentResponse>, String> listener) {
        apiHandler.createCall(apiService.cancelAppointment(id), listener);
    }


    /* ------------- Cart APIs ------------- */


    public void getCartList(ResponseListener<CommonDataResponse<CartHolderModel>, String> listener) {
        apiHandler.createCall(apiService.getCartList(), listener);
    }

    public void syncCartList(CartHolderModel body, ResponseListener<CommonDataResponse<CartHolderModel>, String> listener) {
        apiHandler.createCall(apiService.syncCartList(body), listener);
    }


    /* ------------- Express APIs ------------- */

    public void getMessageCount(ResponseListener<CommonDataResponse<String>, String> listener) {
        apiHandler.createCall(apiService.getUnreadedMessageCount(preferenceRepository.getUserName()), listener);
    }


    /* ------------- Express APIs ------------- */

    public void getExpressServicesList(ResponseListener<CommonDataResponse<List<ExpressServiceModel>>, String> listener) {
        apiHandler.createCall(apiService.getExpressServicesList(), listener);
    }


    public void getServiceDetails(String slug, ResponseListener<CommonDataResponse<ExpressServiceDetailsModel>, String> listener) {
        apiHandler.createCall(apiService.getExpressServiceDetails(slug), listener);
    }

    public void getShopList(String serviceSug,
                            int page,
                            int limit,
                            String address,
                            String address2,
                            String search,
                            Double longitude,
                            Double latitude,
                            ResponseListener<CommonResultResponse<List<GroupShopModel>>, String> listener) {

        apiHandler.createCall(apiService.getExpressShopList(serviceSug, page, limit, address, address2, search, longitude, latitude), listener);
    }

    public void getProductList(String serviceSug,
                               int page,
                               int limit,
                               String search,
                               ResponseListener<CommonResultResponse<List<ProductItem>>, String> listener) {
        apiHandler.createCall(apiService.getExpressProductList(serviceSug, page, limit, search), listener);
    }



    /* ------------- General APIs ------------- */

    public void getNotification(String token, int page, ResponseListener<CommonResultResponse<List<NotificationItem>>, String> listener) {
        apiHandler.createCall(apiService.getNotification(page), listener);
    }

    public void markNotificationAsRead(String token, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.markNotificationAsRead(), listener);
    }

    public void getRootCategories(ResponseListener<CommonDataResponse<List<CategoryEntity>>, String> listener) {
        apiHandler.createCall(apiService.getRootCategories(200), listener);
    }

    public void getTopCategories(ResponseListener<CommonDataResponse<List<CategoryEntity>>, String> listener) {
        apiHandler.createCall(apiService.getTopCategories(), listener);
    }

    public void getSubCategories(String rootCategory, ResponseListener<CommonDataResponse<List<CategoryEntity>>, String> listener) {
        apiHandler.createCall(apiService.getSubCategories(rootCategory), listener);
    }

    public void getBanners(ResponseListener<CommonResultResponse<JsonObject>, String> listener) {
        apiHandler.createCall(apiService.getBanners(), listener);
    }


    public void getNotificationCount(String token, String notificationType, ResponseListener<NotificationCount, String> listener) {
        apiHandler.createCall(apiService.getNotificationCount(notificationType), listener);
    }


    public void subscribeToShop(String shopName, String shopImage, String shopSlug, boolean subscribe, ResponseListener<JsonObject, String> listener) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("shop_name", shopName);
        body.put("shop_image_url", shopImage);
        body.put("shop_slug", shopSlug);
        body.put("status", subscribe);
        apiHandler.createCall(apiService.subscribeToShop(body), listener);
    }


    public void checkReferral(HashMap<String, String> body, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.checkReferral(body.get("device_id"), body.get("referred_by"), body.get("token")), listener);
    }


    /* ------------- Giftcard APIs ------------- */


    public void payWithGiftCard(String token, HashMap<String, String> body, ResponseListener<JsonObject, String> listener) {

        apiHandler.createCall(apiService.payWithGiftCard(body), listener);
    }


    public void getGiftCard(int page, String url, ResponseListener<CommonDataResponse<List<GiftCardListItem>>, String> listener) {
        url += "gift-cards/custom/list";
        apiHandler.createCall(apiService.getGiftCardList(url, page), listener);
    }

    public void placeGiftCardOrder(String token, JsonObject body, String url, ResponseListener<JsonObject, String> listener) {
        url += "gift-card-orders/place/";
        apiHandler.createCall(apiService.placeGiftCardOrder(url, body), listener);
    }

    public void getGiftCardDetails(String slug, String url, ResponseListener<CommonDataResponse<GiftCardListItem>, String> listener) {
        url += "gift-cards/retrieve/" + slug;
        apiHandler.createCall(apiService.getGiftCardDetails(url), listener);
    }

    public void getPurchasedGiftCardList(String show, int page, String url, ResponseListener<CommonDataResponse<List<GiftCardListPurchasedItem>>, String> listener) {
        url += "gift-card-orders";
        apiHandler.createCall(apiService.getPurchasedGiftCardList(url, show, page), listener);
    }

    public void redeem(String invoiceNo, String url, ResponseListener<JsonObject, String> listener) {

        HashMap<String, String> body = new HashMap<>();
        body.put("invoice_no", invoiceNo);
        url += "gift-card-orders/gift-code/retrieve";
        apiHandler.createCall(apiService.redeemGiftCard(url, body), listener);
    }


    /* ------------- Image APIs ------------- */

    public void uploadImage(Bitmap imageBitmap, ResponseListener<CommonDataResponse<ImageDataModel>, String> listener) {

        File f = new File(AppController.getContext().getCacheDir(), "image.jpg");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Logger.d(e.getMessage());
            e.printStackTrace();
        }

        RequestBody requestFile = RequestBody.create(f, MediaType.parse("image/jpg"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", f.getName(), requestFile);

        apiHandler.createCall(apiService.imageUploadNew("multipart/form-data", body), listener);
    }


    /* ------------- IssueTicket APIs ------------- */

    public void getIssueSubAnswers(String answerId, ResponseListener<CommonDataResponse<List<IssueAnswerResponse>>, String> listener) {
        apiHandler.createCall(apiService.getIssueSubAnswers(answerId), listener);
    }

    public void getIssueAnswers(String categoryId, ResponseListener<CommonDataResponse<List<IssueAnswerResponse>>, String> listener) {
        apiHandler.createCall(apiService.getIssueAnswers(categoryId), listener);
    }

    public void getCategories(String orderStatus, ResponseListener<CommonDataResponse<List<IssueCategoryModel>>, String> listener) {
        apiHandler.createCall(apiService.getIssueTicketCategory(orderStatus, "evaly_order", 250), listener);
    }

    public void getIssueList(String invoice, int page, ResponseListener<CommonDataResponse<List<IssueListModel>>, String> listener) {
        apiHandler.createCall(apiService.getIssueTicketList(invoice, page), listener);
    }

    public void resolveIssue(String status, int id, ResponseListener<CommonDataResponse<IssueListModel>, String> listener) {
        HashMap<String, String> body = new HashMap<>();
        body.put("order_status", status);
        apiHandler.createCall(apiService.resolveIssueTicketStatus(body, id), listener);
    }

    public void createIssue(IssueCreateBody body, ResponseListener<CommonDataResponse<IssueListModel>, String> listener) {
        apiHandler.createCall(apiService.createIssueTicket(body), listener);
    }

    public void getIssueCommentList(int ticketId, ResponseListener<CommonDataResponse<List<IssueTicketCommentModel>>, String> listener) {
        apiHandler.createCall(apiService.getIssueTicketComment(ticketId), listener);
    }

    public void createIssueComment(int ticketId, String comment, ResponseListener<CommonDataResponse<IssueTicketCommentModel>, String> listener) {
        IssueCommentBody body = new IssueCommentBody();
        body.setComment(comment);
        body.setTicket(ticketId);
        apiHandler.createCall(apiService.createIssueTicketComment(body, ticketId), listener);
    }



    /* ------------- Location APIs ------------- */

    public void getLocations(String parent, ResponseListener<CommonDataResponse<List<LocationResponse>>, String> listener) {
        apiHandler.createCall(apiService.getLocations(parent), listener);
    }

    /* ------------- Newsfeed APIs ------------- */

    public void getNewsfeedPosts(String token, String url, ResponseListener<JsonObject, String> listener) {
        if (token.equals(""))
            token = null;

        apiHandler.createCall(apiService.getNewsfeedPosts(url), listener);
    }


    public void getNewsfeedPostsList(String token, String url, ResponseListener<CommonDataResponse<List<NewsfeedPost>>, String> listener) {
        if (token.equals(""))
            token = null;

        apiHandler.createCall(apiService.getNewsfeedPostsList(url), listener);
    }

    public void getPostDetails(String token, String postId, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.getNewsfeedPostDetails(postId), listener);
    }

    public void getReplies(String token, String postId, int commentId, int page, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.getNewsfeedReplies(postId, commentId, page), listener);
    }

    public void getRepliesList(String token, String postId, int commentId, int page, ResponseListener<CommonDataResponse<List<CommentItem>>, String> listener) {
        apiHandler.createCall(apiService.getNewsfeedRepliesList(postId, commentId, page), listener);
    }

    public void getComments(String token, String postId, int page, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.getNewsfeedComments(postId, page), listener);
    }


    public void getCommentList(String token, String postId, int page, ResponseListener<CommonDataResponse<List<CommentItem>>, String> listener) {
        apiHandler.createCall(apiService.getNewsfeedCommentsList(postId, page), listener);
    }

    public void postComment(String token, String postId, JsonObject body, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.postNewsfeedComment(postId, body), listener);
    }

    public void postReply(String token, String postId, int commentId, JsonObject body, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.postNewsfeedReply(postId, commentId, body), listener);
    }

    public void postLike(String token, String postSlug, boolean dislike, ResponseListener<JsonObject, String> listener) {
        if (!dislike)
            apiHandler.createCall(apiService.likeNewsfeedPost(postSlug), listener);
        else
            apiHandler.createCall(apiService.dislikeNewsfeedPost(postSlug), listener);
    }

    public void deleteItem(String token, String url, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.deleteNewsfeedItem(url), listener);
    }

    public void actionPendingPost(String token, String postId, String type, JsonObject body, ResponseListener<JsonObject, String> listener) {

        if (type.equals("delete"))
            apiHandler.createCall(apiService.deletePendingNewsfeedPost(postId, body), listener);
        else
            apiHandler.createCall(apiService.approvePendingNewsfeedPost(postId, body), listener);
    }

    public void getNewsfeedNotification(String token, int page, ResponseListener<CommonResultResponse<List<NotificationItem>>, String> listener) {
        apiHandler.createCall(apiService.getNewsfeedNotification(page), listener);
    }

    public void markFeedNotificationAsRead(String token, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.markNewsfeedNotificationAsRead(), listener);
    }

    public void getNotificationCount(String token, ResponseListener<NotificationCount, String> listener) {
        apiHandler.createCall(apiService.getNewsfeedNotificationCount(), listener);
    }

    public void post(String token, CreatePostModel body, String postSlug, ResponseListener<JsonObject, String> listener) {

        if (postSlug == null)
            apiHandler.createCall(apiService.createNewsfeedPost(body), listener);
        else
            apiHandler.createCall(apiService.editNewsfeedPost(body, postSlug), listener);

    }


    /* ------------- Order APIs ------------- */


    public void isAttachmentRequired(List<Integer> productIdList, ResponseListener<CommonDataResponse<List<AttachmentCheckResponse>>, String> listener) {
        apiHandler.createCall(apiService.isAttachmentRequired(productIdList), listener);
    }

    public void updateAddress(UpdateOrderAddressRequest body, ResponseListener<CommonDataResponse<OrderDetailsModel>, String> listener) {
        apiHandler.createCall(apiService.updateOrderAddress(body), listener);
    }

    public void getOrderRequestList(int page, ResponseListener<CommonDataResponse<List<OrderRequestResponse>>, String> listener) {
        apiHandler.createCall(apiService.getOrderRequests(page), listener);
    }

    public void getOrderList(String token, int page, String orderStatus, ResponseListener<CommonResultResponse<List<OrderListItem>>, String> listener) {
        if (orderStatus.equals("all"))
            orderStatus = null;

        apiHandler.createCall(apiService.getOrderList(page, orderStatus), listener);
    }

    public void placeOrder(String token, JsonObject body, ResponseListener<JsonObject, String> listener) {

        apiHandler.createCall(apiService.placeOrder(body), listener);
    }

    public void placeOrder(PlaceOrderItem body, ResponseListener<CommonDataResponse<List<JsonObject>>, String> listener) {
        apiHandler.createCall(apiService.placeOrder(body), listener);
    }

    public void makeCashOnDelivery(String token, HashMap<String, String> body, ResponseListener<JsonObject, String> listener) {

        apiHandler.createCall(apiService.makeCashOnDelivery(body), listener);
    }


    public void payViaSEBL(String token, String amount, String invoice, String context, ResponseListener<JsonObject, String> listener) {

        apiHandler.createCall(apiService.payViaSEBL(amount, invoice, token, context), listener);
    }


    public void payViaNagad(String token, HashMap<String, String> body, ResponseListener<JsonObject, String> listener) {

        apiHandler.createCall(apiService.payViaNagad(body), listener);
    }


    public void getOrderDetails(String token, String invoiceNo, ResponseListener<OrderDetailsModel, String> listener) {

        apiHandler.createCall(apiService.getOrderDetails(invoiceNo), listener);
    }


    public void getOrderHistories(String token, String invoiceNo, ResponseListener<JsonObject, String> listener) {

        apiHandler.createCall(apiService.getOrderHistories(invoiceNo), listener);
    }

    public void cancelOrder(String token, String invoiceNo, String userNote, ResponseListener<CommonDataResponse, String> listener) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("order_status", "cancel");
        hashMap.put("user_note", userNote);

        apiHandler.createCall(apiService.cancelOrder(invoiceNo, hashMap), listener);
    }

    public void confirmDelivery(String token, String invoiceNo, ResponseListener<CommonDataResponse, String> listener) {
        apiHandler.createCall(apiService.confirmDelivery(invoiceNo), listener);
    }

    public void requestRefund(String token, HashMap<String, String> body, ResponseListener<CommonDataResponse<String>, String> listener) {
        apiHandler.createCall(apiService.postRequestRefund(body), listener);
    }

    public void requestRefundConfirmOTP(String token, String invoice, HashMap<String, Integer> body, ResponseListener<CommonDataResponse<String>, String> listener) {
        apiHandler.createCall(apiService.postRequestRefundConfirmOTP(body, invoice), listener);
    }

    public void getDeliveryHero(String invoiceNo, ResponseListener<DeliveryHeroResponse, String> listener) {
        apiHandler.createCall(apiService.getDeliveryHero(invoiceNo), listener);
    }

    public void checkRefundEligibility(String invoiceNo, ResponseListener<CommonDataResponse<String>, String> listener) {
        apiHandler.createCall(apiService.checkRefundEligibility(invoiceNo.toUpperCase()), listener);
    }

    public void deleteRefundTransaction(String invoiceNo, ResponseListener<CommonDataResponse<String>, String> listener) {
        apiHandler.createCall(apiService.deleteRefundTransaction(invoiceNo.toUpperCase()), listener);
    }


    /* ------------- Payment APIs ------------- */


    public void claimCashback(String token, String username, String url, ResponseListener<JsonObject, String> listener) {
        url += username + "/";
        apiHandler.createCall(apiService.claimCashBack(url), listener);
    }

    public void getBalance(String token, String username, String url, ResponseListener<CommonDataResponse<BalanceResponse>, String> listener) {
        apiHandler.createCall(apiService.getBalance(url), listener);
    }

    public void makePartialPayment(String token, ParitalPaymentModel body, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.makePartialPayment(body), listener);
    }

    public void payViaCard(String token, HashMap<String, String> body, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.payViaCard(body), listener);
    }

    public void payViaCityBank(String token, HashMap<String, String> body, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.payViaCityBank(body), listener);
    }

    public void payViaBank(String token, HashMap<String, String> body, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.payViaBank(body), listener);
    }


    /* ------------- Catalog APIs ------------- */


    public void getChildCategories(String parentCategorySlug, ResponseListener<CommonDataResponse<List<ChildCategoryResponse>>, String> listener) {
        apiHandler.createCall(apiService.getChildCategory(parentCategorySlug), listener);
    }

    public void getBrands(String categorySlug, String search, int page, ResponseListener<CommonDataResponse<List<BrandResponse>>, String> listener) {
        apiHandler.createCall(apiService.getBrands(categorySlug, search, page, 20), listener);
    }

    public void getShops(String categorySlug, String search, int page, String paymentType, ResponseListener<CommonDataResponse<List<ShopListResponse>>, String> listener) {
        apiHandler.createCall(apiService.getShops(categorySlug, search, page, 20, paymentType), listener);
    }

    public void getShopProducts(String shopSlug, int page, int limit, String categorySlug, String campaignSlug, String search, ResponseListener<JsonObject, String> listener) {

        if (categorySlug != null && categorySlug.equals(""))
            categorySlug = null;

        if (!campaignSlug.equals(""))
            apiHandler.createCall(apiService.getCampaignShopProducts(campaignSlug, shopSlug, page, limit, categorySlug, search), listener);
        else
            apiHandler.createCall(apiService.getShopProducts(shopSlug, page, limit, categorySlug, search), listener);

    }

    public void getCategoryBrandProducts(int page, String category, String brands, ResponseListener<CommonResultResponse<List<ProductItem>>, String> listener) {

        if (category != null)
            if (category.equals("root"))
                category = null;

        int limit = 48;
        if (page == 1)
            limit = 21;

        apiHandler.createCall(apiService.getCategoryBrandProducts(page, category, brands, limit), listener);
    }


    public void getCampaignBrandProducts(int page, String category, String brands, String campaign, ResponseListener<CommonResultResponse<List<ProductItem>>, String> listener) {

        if (category != null)
            if (category.equals("root"))
                category = null;

        int limit = 48;
        if (page == 1)
            limit = 21;

        if (campaign == null)
            apiHandler.createCall(apiService.getCategoryBrandProducts(page, category, brands, limit), listener);
        else
            apiHandler.createCall(apiService.getCampaignBrandProducts(brands, campaign, page, category, limit), listener);
    }


    public void getBrandsOfCategories(String category, int page, int limit, ResponseListener<JsonObject, String> listener) {
        if (category != null && category.equals("root"))
            apiHandler.createCall(apiService.getBrandsCategories(null, page, limit), listener);
        else
            apiHandler.createCall(apiService.getBrandsCategories(category, page, limit), listener);
    }


    public void getShopsOfCategories(String category, int page, int limit, ResponseListener<JsonObject, String> listener) {
        if (category != null && category.equals("root"))
            apiHandler.createCall(apiService.getShopsOfCategories(page, limit), listener);
        else
            apiHandler.createCall(apiService.getShopsOfCategories(category, page, limit), listener);
    }

    public void getCategoriesOfShop(String shopSlug, String campaign, int page, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.getCategoriesofShop(shopSlug, page), listener);
    }

    public void getProductVariants(String shopSlug, String shopItem, ResponseListener<CommonDataResponse<List<ShopItem>>, String> listener) {
        apiHandler.createCall(apiService.getProductVariants(shopSlug, shopItem), listener);
    }

    public void getProductDetails(String productSlug, ResponseListener<ProductDetailsModel, String> listener) {
        apiHandler.createCall(apiService.getProductDetails(productSlug), listener);
    }

    public void getAvailableShops(int variantID, ResponseListener<CommonDataResponse<List<AvailableShopResponse>>, String> listener) {
        apiHandler.createCall(apiService.getAvailableShop(variantID), listener);
    }

    public void getNearestAvailableShops(int variantId, double longitude, double latitude, ResponseListener<CommonDataResponse<List<AvailableShopResponse>>, String> listener) {
        apiHandler.createCall(apiService.getNearestAvailableShop(variantId, longitude, latitude), listener);
    }


    /* ------------- Reviews APIs ------------- */

    public void getReviewSummary(String token, String slug, boolean isShop, ResponseListener<JsonObject, String> listener) {
        apiHandler.createCall(apiService.getShopReviews(slug), listener);
    }

    public void getReviewSummary(String token, String slug, ResponseListener<CommonDataResponse<ReviewSummaryModel>, String> listener) {
        apiHandler.createCall(apiService.getReviews(slug), listener);
    }

    public void getReviews(String token, String shopSlug, int page, int limit, boolean isShop, ResponseListener<CommonDataResponse<JsonObject>, String> listener) {
        apiHandler.createCall(apiService.getShopReviews(shopSlug, page, limit), listener);
    }

    public void postReview(String token, String slug, JsonObject body, boolean isShop, ResponseListener<JsonObject, String> listener) {
        if (isShop)
            apiHandler.createCall(apiService.postShopReview(slug, body), listener);
        else
            apiHandler.createCall(apiService.postProductReview(slug, body), listener);
    }

    public void checkReviewEligibility(String token, String slug, boolean isShop, ResponseListener<JsonObject, String> listener) {
        if (isShop)
            apiHandler.createCall(apiService.checkShopReviewEligibility(slug), listener);
        else
            apiHandler.createCall(apiService.checkProductReviewEligibility(slug), listener);
    }

    public void getFollowStatus(String slug, ResponseListener<CommonDataResponse<FollowResponse>, String> listener) {
        apiHandler.createCall(apiService.getFollowStatus(slug), listener);
    }


    /* ------------- Search APIs ------------- */

    public void searchProducts(SearchRequest body, int page, ResponseListener<CommonDataResponse<ProductSearchResponse>, String> listener) {
        apiHandler.createCall(apiService.searchProducts(body, page), listener);
    }

    public void searchShops(int page, String search, ResponseListener<CommonDataResponse<List<TabsItem>>, String> listener) {
        apiHandler.createCall(apiService.searchShop(page, 20, search), listener);
    }

    public void searchBrands(int page, String search, ResponseListener<CommonDataResponse<List<TabsItem>>, String> listener) {
        apiHandler.createCall(apiService.searchBrands(page, 20, search), listener);
    }


    /* ------------- Shops APIs ------------- */

    public void getShopDetailsItem(String token, String url, ResponseListener<ShopDetailsModel, String> listener) {
        apiHandler.createCall(apiService.getShopDetailsItems(url), listener);
    }

    public void getShopDetailsItem(String token, String shopSlug, int page, int limit, String categorySlug, String campaignSlug, String search, String brandSlug, ResponseListener<ShopDetailsModel, String> listener) {
        apiHandler.createCall(apiService.getShopDetails(shopSlug, page, limit, categorySlug, search), listener);
    }

    public void getShopsList(String categorySlug, String search, int page, String paymentType, ResponseListener<CommonDataResponse<List<ShopListResponse>>, String> listener) {
        apiHandler.createCall(apiService.getShops(categorySlug, search, page, 20, paymentType), listener);
    }

    public void getShopDetails(String slug, String campaignSlug, ResponseListener<CommonDataResponse<ShopDetailsResponse>, String> listener) {
        apiHandler.createCall(apiService.getShopDetails(slug), listener);
    }

    public void getFollowedShop(int page, ResponseListener<CommonDataResponse<JsonObject>, String> listener) {
        apiHandler.createCall(apiService.getFollowedShops(page, 40), listener);
    }

    public void getShopsByGroup(String group, int page, int limit, String area, String search, ResponseListener<CommonDataResponse<List<GroupShopModel>>, String> listener) {
        apiHandler.createCall(apiService.getShopByGroup(group, page, limit, area, search), listener);
    }
}
