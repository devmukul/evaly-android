package bd.com.evaly.evalyshop.rest;

import com.google.gson.JsonObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.util.UrlUtils;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IApiClient {

    @POST(UrlUtils.SET_PASSWORD)
    Call<JsonObject> setPassword(@Body HashMap<String, String> setPasswordModel);

    @POST(UrlUtils.REGISTER)
    Call<JsonObject> register(@Body HashMap<String, String> data);

}
