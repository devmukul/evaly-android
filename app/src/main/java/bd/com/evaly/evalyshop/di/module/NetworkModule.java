package bd.com.evaly.evalyshop.di.module;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.rest.ApiHandler;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.rest.ApiServiceHolder;
import bd.com.evaly.evalyshop.rest.IApiClient;
import bd.com.evaly.evalyshop.rest.apiHelper.token.TokenAuthenticator;
import bd.com.evaly.evalyshop.rest.apiHelper.token.TokenInterceptor;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    ApiRepository provideApiRepository(IApiClient apiService, PreferenceRepository preferenceRepository, ApiHandler apiHandler) {
        return new ApiRepository(apiService, preferenceRepository, apiHandler);
    }

    @Provides
    @Singleton
    ApiServiceHolder apiServiceHolder() {
        return new ApiServiceHolder();
    }

    @Provides
    @Singleton
    TokenAuthenticator provideTokenAuthenticator(ApiServiceHolder apiServiceHolder, PreferenceRepository preferencesHelper) {
        return new TokenAuthenticator(apiServiceHolder, preferencesHelper);
    }

    @Provides
    @Singleton
    TokenInterceptor provideTokenInterceptor(PreferenceRepository preferencesHelper) {
        return new TokenInterceptor(preferencesHelper);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(PreferenceRepository preferenceRepository, TokenAuthenticator tokenAuthenticator, TokenInterceptor tokenInterceptor) {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.connectTimeout(120, TimeUnit.SECONDS);
        okHttpClient.readTimeout(120, TimeUnit.SECONDS);
        okHttpClient.writeTimeout(120, TimeUnit.SECONDS);
        okHttpClient.addInterceptor(tokenInterceptor);
        okHttpClient.authenticator(tokenAuthenticator);
        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            okHttpClient.addNetworkInterceptor(new StethoInterceptor());
        }
        return okHttpClient.build();
    }

    @Provides
    @Singleton
    IApiClient provideApiService(Retrofit retrofit, ApiServiceHolder apiServiceHolder) {
        IApiClient client = retrofit.create(IApiClient.class);
        apiServiceHolder.setApiService(client);
        return client;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    ApiHandler provideApiHandler() {
        return new ApiHandler();
    }


}
