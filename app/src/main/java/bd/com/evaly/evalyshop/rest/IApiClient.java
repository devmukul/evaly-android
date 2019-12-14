package bd.com.evaly.evalyshop.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.models.CommonSuccessResponse;
import bd.com.evaly.evalyshop.models.CreatePostModel;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;
import bd.com.evaly.evalyshop.models.order.OrderIssueModel;
import bd.com.evaly.evalyshop.models.xmpp.RosterItemModel;
import bd.com.evaly.evalyshop.util.UrlUtils;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IApiClient {

    @POST(UrlUtils.SET_PASSWORD)
    Call<JsonObject> setPassword(@Body HashMap<String, String> setPasswordModel);

    @POST(UrlUtils.REGISTER)
    Call<JsonObject> register(@Body HashMap<String, String> data);

    @POST(UrlUtils.REFRESH_TOKEN)
    Call<JsonObject> refreshToken(@Body HashMap<String, String> data);

    @POST(UrlUtils.CHANGE_XMPP_PASSWORD)
    Call<JsonPrimitive> changeXmppPassword(@Body HashMap<String, String> data);

    @POST(UrlUtils.ADD_ROSTER)
    Call<JsonPrimitive> addRoster(@Body HashMap<String, String> data);

    @GET(UrlUtils.INVITATION_LIST + "{phone}/")
    Call<JsonArray> getInvitationList(@Header("Authorization") String token,@Path("phone") String phone);

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
    Call<JsonObject> getBanners(@Header("Authorization") String token);



    // product APIs

    @GET(UrlUtils.CATEGORIES)
    Call<JSONArray> getCategories(@Query("parent") String parent);

    @GET(UrlUtils.CATEGORIES)
    Call<JSONArray> getCategories();

    @GET(UrlUtils.CATEGORIES_BRANDS)
    Call<JsonObject> getBrandsCategories(@Query("page") int page, @Query("limit") int limit);

    @GET(UrlUtils.CATEGORIES_BRANDS)
    Call<JsonObject> getBrandsCategories(@Query("category") String category, @Query("page") int page, @Query("limit") int limit);


    // campaign APIs

    @GET(UrlUtils.CAMPAIGNS)
    Call<CommonSuccessResponse<List<CampaignItem>>> getCampaigns();

    @GET(UrlUtils.CAMPAIGNS+"/{group}/shops")
    Call<CommonSuccessResponse<List<CampaignShopItem>>> getCampaignShops(@Path("group") String group, @Query("page") int page, @Query("limit") int limit);


    // Root Category

    @GET(UrlUtils.DOMAIN + "core/public/categories/")
    Call<List<CategoryEntity>> getRootCategories();



}
