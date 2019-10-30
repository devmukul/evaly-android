package bd.com.evaly.evalyshop.rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;

import bd.com.evaly.evalyshop.util.UrlUtils;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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

    @POST(UrlUtils.SEND_CUSTOM_MESSAGE)
    Call<JsonObject> sendCustomMessage(@Header("Authorization") String token, @Body HashMap<String, String> data);


    @Multipart
    @POST(UrlUtils.IMAGE_UPLOAD)
    Call<JsonObject> imageUpload(@Header("Authorization") String token, @Header("Content_Type") String contentType, @Part MultipartBody.Part image);

    @GET(UrlUtils.CHECK_UPDATE)
    Call<JsonObject> checkUpdate();
}
