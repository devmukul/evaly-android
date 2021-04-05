package bd.com.evaly.evalyshop.rest.apiHelper.token;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.rest.ApiServiceHolder;
import bd.com.evaly.evalyshop.rest.IApiClient;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

    private boolean isRefreshApiCalled = false;
    private ApiServiceHolder apiServiceHolder;
    private IApiClient apiService;
    private PreferenceRepository preferencesHelper;

    public TokenAuthenticator(ApiServiceHolder apiServiceHolder, PreferenceRepository preferencesHelper) {
        this.apiServiceHolder = apiServiceHolder;
        this.preferencesHelper = preferencesHelper;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (apiServiceHolder.getApiService() == null)
            return null;
        apiService = apiServiceHolder.getApiService();

        if (!isRefreshApiCalled) {
            HashMap<String, String> loginRequest = new HashMap<>();
            loginRequest.put("refresh_token", preferencesHelper.getRefreshToken());
            loginRequest.put("access_token", preferencesHelper.getToken());

            retrofit2.Response<JsonObject> refreshApiResponse = apiService.refreshToken(loginRequest).execute();
            if (refreshApiResponse.code() != 401) {
                if (refreshApiResponse.body() != null && !response.request().url().toString().contains("ecaptcha")) {
                    JsonObject loginResponse = refreshApiResponse.body();
                    preferencesHelper.saveToken(loginResponse.get("data").getAsJsonObject().get("access_token").getAsString());
                    preferencesHelper.saveRefreshToken(loginResponse.get("data").getAsJsonObject().get("refresh_token").getAsString());
                    return response.request().newBuilder()
                            .addHeader("Authorization", preferencesHelper.getToken())
                            .build();
                }
            } else {
                if (!response.request().url().toString().contains("ecaptcha"))
                    AppController.getInstance().logout();
            }
        }

        isRefreshApiCalled = false;
        return null;
    }

    public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
        // Null indicates no attempt to authenticate.
        return null;
    }
}
