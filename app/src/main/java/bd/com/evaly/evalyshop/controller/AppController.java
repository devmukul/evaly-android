package bd.com.evaly.evalyshop.controller;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import bd.com.evaly.evalyshop.data.roomdb.ProviderDatabase;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.preference.MyPreference;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class AppController extends Application implements Application.ActivityLifecycleCallbacks {

    public static AppController mAppController;
    public static Context mContext;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static Context getmContext() {
        return mContext;
    }

    public static synchronized AppController getInstance() {
        return mAppController;
    }

    public static void logout(Activity context) {
        getInstance().logoutFromServer();
        try {
            String email = CredentialManager.getUserName();
            String strNew = email.replaceAll("[^A-Za-z0-9]", "");
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.BUILD + "_" + strNew);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MyPreference.with(context).clearAll();
        ProviderDatabase providerDatabase = ProviderDatabase.getInstance(getmContext());
        providerDatabase.userInfoDao().deleteAll();

        new Handler().postDelayed(() -> {
            context.startActivity(new Intent(context, SignInActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            context.finish();
        }, 300);
    }


    public static void logout() {
        getInstance().logoutFromServer();
        try {
            String email = CredentialManager.getUserName();
            String strNew = email.replaceAll("[^A-Za-z0-9]", "");
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.BUILD + "_" + strNew);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyPreference.with(getmContext().getApplicationContext()).clearAll();
        ProviderDatabase providerDatabase = ProviderDatabase.getInstance(getmContext());
        providerDatabase.userInfoDao().deleteAll();
        ToastUtils.show("Logged out");
        new Handler().postDelayed(() -> {
            getInstance().getApplicationContext().startActivity(new Intent(getInstance().getApplicationContext(), SignInActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            System.exit(0);
        }, 300);
    }

    private void logoutFromServer(){
        AuthApiHelper.logout(new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppController = this;
        mContext = getApplicationContext();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Logger.addLogAdapter(new AndroidLogAdapter());

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
