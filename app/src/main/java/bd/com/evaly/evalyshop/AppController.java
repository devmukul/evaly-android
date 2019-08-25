package bd.com.evaly.evalyshop;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import io.fabric.sdk.android.Fabric;

public class AppController extends Application {

    public static AppController mAppController;
    public static Context mContext;
//    public static AlertDialog dialog;

    public static AppController getInstance() {
        return mAppController;
    }

    public static Context getmContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAppController = this;
        mContext = getApplicationContext();

//        internetConnectionDialog();
        Logger.addLogAdapter(new AndroidLogAdapter());
    }
}
