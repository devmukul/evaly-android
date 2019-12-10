package bd.com.evaly.evalyshop;

import android.app.Activity;
import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import bd.com.evaly.evalyshop.activity.SignInActivity;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.db.AppDatabase;
import bd.com.evaly.evalyshop.preference.MyPreference;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.Token;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.xmpp.LocalBinder;
import bd.com.evaly.evalyshop.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import io.fabric.sdk.android.Fabric;
import retrofit2.Response;

public class AppController extends Application implements Application.ActivityLifecycleCallbacks{

    public static AppController mAppController;
    public static Context mContext;
    public static boolean allDataLoaded;
    private final String TAG = getClass().getSimpleName();
//    public static AlertDialog dialog;

    public static XMPPService xmppService;
    public Boolean mBounded = false;
//    public static AppDatabase database;

    static UserDetails userDetails;
    IntentFilter intentFilter;


    //Our broadCast receive to update us on various events
    private XMPPEventReceiver mEventReceiver;

    public static Context getmContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppController = this;
        mContext = getApplicationContext();

//        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "evalyDB").build();

        userDetails = new UserDetails(this);

        EmojiManager.install(new GoogleEmojiProvider());


//        internetConnectionDialog();
        Logger.addLogAdapter(new AndroidLogAdapter());

        if (mEventReceiver == null) mEventReceiver = new XMPPEventReceiver();

        intentFilter = new IntentFilter(Constants.EVT_LOGGED_IN);
        intentFilter.addAction(Constants.EVT_SIGNUP_SUC);
        intentFilter.addAction(Constants.EVT_PASSWORD_CHANGE_SUC);
        intentFilter.addAction(Constants.EVT_PASSWORD_CHANGE_FAILED);
        intentFilter.addAction(Constants.EVT_NEW_MSG_SENT);
        intentFilter.addAction(Constants.EVT_UPDATE_USER_SUC);
        intentFilter.addAction(Constants.EVT_UPDATE_USER_ERR);
        intentFilter.addAction(Constants.EVT_SIGNUP_ERR);
        intentFilter.addAction(Constants.EVT_NEW_MSG);
        intentFilter.addAction(Constants.EVT_AUTH_SUC);
        intentFilter.addAction(Constants.EVT_RECONN_ERR);
        intentFilter.addAction(Constants.EVT_RECONN_WAIT);
        intentFilter.addAction(Constants.EVT_RECONN_SUC);
        intentFilter.addAction(Constants.EVT_CONN_SUC);
        intentFilter.addAction(Constants.EVT_CONN_CLOSE);
        intentFilter.addAction(Constants.EVT_LOGIN_ERR);
        intentFilter.addAction(Constants.EVT_PRESENCE_CHG);
        intentFilter.addAction(Constants.EVT_CHATSTATE_CHG);
        intentFilter.addAction(Constants.EVT_REQUEST_SUBSCRIBE);

        registerReceiver(mEventReceiver, intentFilter);

        registerActivityLifecycleCallbacks(this);
    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name,
                                       final IBinder service) {
            xmppService = ((LocalBinder<XMPPService>) service).getService();
            mBounded = true;
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            xmppService = null;
            mBounded = false;
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    public XMPPEventReceiver getEventReceiver() {
        return mEventReceiver;
    }


    public static synchronized AppController getInstance() {
        return mAppController;
    }

    public void BindService(Intent intent) {
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void UnbindService() {
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }

    public static XMPPService getmService() {
        return xmppService;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
//        Logger.d("RESUMED");
        registerReceiver(mEventReceiver, intentFilter);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
//        Logger.d("PAUSED");
        try {
            if (mEventReceiver != null) unregisterReceiver(mEventReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    public static void logout(Activity context) {

        try {
            String email = CredentialManager.getUserName();
            String strNew = email.replaceAll("[^A-Za-z0-9]", "");

            FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.BUILD+"_"+strNew);
        }catch (Exception e){
            e.printStackTrace();
        }

        MyPreference.with(context).clearAll();
//        getInstance().clearAppData();
        Logger.d(CredentialManager.getToken());
        if (xmppService != null && xmppService.xmpp != null) {
            xmppService.xmpp.disconnect();
        }
        try {
            userDetails.clearAll();
            getInstance().UnbindService();
            XMPPHandler.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                database.clearAllTables();
//            }
//        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.stopService(new Intent(context, XMPPService.class));
                context.startActivity(new Intent(context, SignInActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                context.finish();
            }
        }, 300);


    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
