package bd.com.evaly.evalyshop.di.module;

import android.app.Application;
import android.content.Context;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;

import javax.inject.Singleton;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.CartDatabase;
import bd.com.evaly.evalyshop.di.observers.SharedObservers;
import bd.com.evaly.evalyshop.di.qualifiers.FirebaseRemoteConfigLiveData;
import bd.com.evaly.evalyshop.manager.credential.CredentialManager;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    PreferenceRepository providePreferenceRepository(Context context, Gson gson) {
        return new PreferenceRepository(context, gson);
    }

    @Provides
    @Singleton
    FirebaseRemoteConfig firebaseRemoteConfig(FirebaseRemoteConfigSettings configSettings, @FirebaseRemoteConfigLiveData MutableLiveData<Boolean> isCompleted) {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> isCompleted.setValue(task.isSuccessful()));
        return mFirebaseRemoteConfig;
    }

    @Provides
    @FirebaseRemoteConfigLiveData
    @Singleton
    MutableLiveData<Boolean> remoteConfigCompleteLiveData() {
        return new MutableLiveData<>();
    }

    @Provides
    @Singleton
    FirebaseRemoteConfigSettings firebaseRemoteConfigSettings() {
        return new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(800)
                .build();
    }

    @Provides
    @Singleton
    SharedObservers sharedObservers() {
        return new SharedObservers();
    }

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "app_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    CartDatabase provideCartDatabase(Context context) {
        return Room.databaseBuilder(context, CartDatabase.class, "cart_database")
                .fallbackToDestructiveMigration()
                .build();
    }

}
