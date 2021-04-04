package bd.com.evaly.evalyshop.di.module;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.remote.ApiRepository;
import bd.com.evaly.evalyshop.rest.ApiHandler;
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
    TokenAuthenticator provideTokenAuthenticator(IApiClient apiService, PreferenceRepository preferencesHelper) {
        return new TokenAuthenticator(apiService, preferencesHelper);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(PreferenceRepository preferencesHelper, TokenAuthenticator tokenAuthenticator) {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.connectTimeout(120, TimeUnit.SECONDS);
        okHttpClient.readTimeout(120, TimeUnit.SECONDS);
        okHttpClient.writeTimeout(120, TimeUnit.SECONDS);
        okHttpClient.addInterceptor(new TokenInterceptor(preferencesHelper));
        okHttpClient.authenticator(tokenAuthenticator);
        if (BuildConfig.DEBUG)
            okHttpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        return okHttpClient.build();
    }


    @Provides
    @Singleton
    IApiClient provideApiService(Retrofit retrofit) {
        return retrofit.create(IApiClient.class);
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
