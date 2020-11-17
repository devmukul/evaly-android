package bd.com.evaly.evalyshop.di.module;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import javax.inject.Singleton;

import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.address.AddressListDao;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchViewModel;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
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
    FirebaseRemoteConfig firebaseRemoteConfig(FirebaseRemoteConfigSettings configSettings) {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        return mFirebaseRemoteConfig;
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
    AddressListDao addressListDao(AppDatabase appDatabase) {
        return appDatabase.addressListDao();
    }

    @Provides
    @Singleton
    GlobalSearchViewModel provideGlobalSearchViewModel() {
        return new GlobalSearchViewModel();
    }
}
