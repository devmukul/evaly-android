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
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import bd.com.evaly.evalyshop.data.roomdb.ProviderDatabase;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.preference.MyPreference;

public class AppController extends Application implements Application.ActivityLifecycleCallbacks {

    public static AppController mAppController;
    public static Context mContext;
    public static boolean allDataLoaded;
    private final String TAG = getClass().getSimpleName();
    public Boolean mBounded = false;

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

    @Override
    public void onCreate() {
        super.onCreate();
        mAppController = this;
        mContext = getApplicationContext();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
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
