package bd.com.evaly.evalyshop.rest;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.models.SetPasswordModel;
import bd.com.evaly.evalyshop.util.UrlUtils;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IApiClient {

    @POST(UrlUtils.SET_PASSWORD)
    Call<JsonObject> setPassword(@Body SetPasswordModel setPasswordModel);

    @POST(UrlUtils.REGISTER)
    Call<JsonObject> register(@Body HashMap<String, String> data);

}
