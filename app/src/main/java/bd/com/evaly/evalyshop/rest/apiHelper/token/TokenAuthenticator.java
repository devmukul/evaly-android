package bd.com.evaly.evalyshop.rest.apiHelper.token;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;

import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.rest.IApiClient;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

    public TokenAuthenticator() {
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {

        HashMap<String, String> loginRequest = new HashMap<>();
        loginRequest.put("access", CredentialManager.getTokenNoBearer());
        loginRequest.put("refresh", CredentialManager.getRefreshToken());

        IApiClient apiClient = ApiClient.getClient().create(IApiClient.class);

        JsonObject loginResponse = apiClient.refreshToken(loginRequest).execute().body();
        if (loginResponse != null) {
            CredentialManager.saveToken(loginResponse.get("access").getAsString());
            CredentialManager.saveRefreshToken(loginResponse.get("refresh").getAsString());

            return response.request().newBuilder()
                    .addHeader("Authorization", "Bearer" + CredentialManager.getToken())
                    .build();
        }

        return null;
    }

    public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
        // Null indicates no attempt to authenticate.
        return null;
    }
}
