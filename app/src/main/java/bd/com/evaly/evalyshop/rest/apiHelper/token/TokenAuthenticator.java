package bd.com.evaly.evalyshop.rest.apiHelper.token;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.rest.ApiServiceHolder;
import bd.com.evaly.evalyshop.rest.IApiClient;
import bd.com.evaly.evalyshop.util.UrlUtils;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

    private ApiServiceHolder apiServiceHolder;
    private IApiClient apiService;
    private PreferenceRepository preferencesHelper;

    public TokenAuthenticator(ApiServiceHolder apiServiceHolder, PreferenceRepository preferencesHelper) {
        this.apiServiceHolder = apiServiceHolder;
        this.preferencesHelper = preferencesHelper;
    }

    @Override
    public Request authenticate(Route route, @NotNull Response response) throws IOException {

        if (preferencesHelper.getRefreshToken().isEmpty() || preferencesHelper.getToken().isEmpty())
            return null;

        if (apiServiceHolder.getApiService() == null ||
                preferencesHelper.getToken().isEmpty())
            return null;

        apiService = apiServiceHolder.getApiService();
        Request request = response.request();

        if (request.url().toString().equals(getRefreshTokenUrl())) {
            AppController.onLogoutEvent();
            return null;
        }

        return fetchNewTokenAndUpdateRequest(apiService, request);
    }


    private synchronized Request fetchNewTokenAndUpdateRequest(IApiClient apiService, Request request) throws IOException {

        String currentToken = preferencesHelper.getToken();

        if (isAlreadyNewTokenFetched(request, currentToken))
            return getNewRequest(request, currentToken);

        HashMap<String, String> loginRequest = new HashMap<>();
        loginRequest.put("refresh_token", preferencesHelper.getRefreshToken());
        loginRequest.put("access_token", currentToken);

        retrofit2.Response<JsonObject> refreshApiResponse = apiService.refreshToken(loginRequest).execute();
        if (refreshApiResponse.code() != 401) {
            if (refreshApiResponse.body() != null &&
                    !request.url().toString().contains("ecaptcha") &&
                    !request.url().toString().contains("logout")) {

                JsonObject loginResponse = refreshApiResponse.body();
                String accessToken = loginResponse.get("data").getAsJsonObject().get("access_token").getAsString();
                preferencesHelper.saveToken(accessToken);
                preferencesHelper.saveRefreshToken(loginResponse.get("data").getAsJsonObject().get("refresh_token").getAsString());

                if (accessToken == null || accessToken.isEmpty())
                    return null;

                return this.getNewRequest(request, accessToken);
            }

        } else {
            if (!request.url().toString().contains("ecaptcha") &&
                    !request.url().toString().contains("logout"))
                AppController.onLogoutEvent();
        }

        return null;
    }

    private String getRefreshTokenUrl() {
        return UrlUtils.DOMAIN_EAUTH + "token/refresh";
    }

    private boolean isAlreadyNewTokenFetched(Request request, String currSavedToken) {
        String reqAccessToken = request.header("Authorization");
        return !Intrinsics.areEqual(currSavedToken, reqAccessToken);
    }

    private Request getNewRequest(Request request, String token) {
        return request.newBuilder().header("Authorization", "Bearer " + token).build();
    }

}
