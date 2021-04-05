package bd.com.evaly.evalyshop.rest.apiHelper.token;


import androidx.annotation.NonNull;

import java.io.IOException;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class TokenInterceptor implements Interceptor {

    private PreferenceRepository preferenceRepository;

    public TokenInterceptor(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (!preferenceRepository.getToken().isEmpty()) {
            Request.Builder builder = originalRequest.newBuilder();
            builder.addHeader("Authorization", preferenceRepository.getToken());
            Request request = builder.build();
            return chain.proceed(request);
        }
        return chain.proceed(originalRequest);
    }

}