package bd.com.evaly.evalyshop.di.module;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import javax.inject.Singleton;

import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.address.AddressListDao;
import bd.com.evaly.evalyshop.data.roomdb.banner.BannerDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.di.observers.SharedObservers;
import bd.com.evaly.evalyshop.di.qualifiers.FirebaseRemoteConfigLiveData;
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
    AddressListDao addressListDao(AppDatabase appDatabase) {
        return appDatabase.addressListDao();
    }

    @Provides
    @Singleton
    BannerDao bannerDao(AppDatabase appDatabase) {
        return appDatabase.bannerDao();
    }

    @Provides
    @Singleton
    CartDao cartDao(AppDatabase appDatabase) {
        return appDatabase.cartDao();
    }

    @Provides
    @Singleton
    GlobalSearchViewModel provideGlobalSearchViewModel() {
        return new GlobalSearchViewModel();
    }

    @Provides
    @Singleton
    SharedObservers sharedObservers() {
        return new SharedObservers();
    }
}
