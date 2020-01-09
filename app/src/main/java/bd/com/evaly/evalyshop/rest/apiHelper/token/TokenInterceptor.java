package bd.com.evaly.evalyshop.rest.apiHelper.token;


import androidx.annotation.NonNull;

import java.io.IOException;

import bd.com.evaly.evalyshop.manager.CredentialManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This okhttp interceptor is responsible for adding the common query parameters and headers
 * for every service calls
 */
public class TokenInterceptor implements Interceptor {


    public TokenInterceptor() {

    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();


        if (CredentialManager.getToken() != null && !originalRequest.url().toString().contains("core")) {
            Request.Builder builder = originalRequest.newBuilder();
            builder.addHeader("Authorization", "Bearer "+ CredentialManager.getToken());
            Request request = builder.build();
            return chain.proceed(request);
        }
        return chain.proceed(originalRequest);
    }
}