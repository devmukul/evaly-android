package bd.com.evaly.evalyshop.controller;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.facebook.stetho.Stetho;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class AppController extends Application {

    public static AppController mAppController;
    public static Context mContext;
    private static AppEvent appEvent;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Inject
    ApiRepository apiRepository;

    @Inject
    PreferenceRepository preferenceRepository;

    public static synchronized void setAppEvent(AppEvent event) {
        appEvent = event;
    }

    public static Context getContext() {
        return mContext;
    }

    public static synchronized AppController getInstance() {
        return mAppController;
    }

    public static synchronized void onLogoutEvent() {
        if (appEvent != null) appEvent.onLogout();
    }

    public PreferenceRepository getPreferenceRepository() {
        return preferenceRepository;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppController = this;
        mContext = getApplicationContext();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (BuildConfig.DEBUG)
            Stetho.initializeWithDefaults(this);
    }

    public interface AppEvent {
        void onLogout();
    }
}
