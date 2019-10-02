package bd.com.evaly.evalyshop;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.xmpp.LocalBinder;
import bd.com.evaly.evalyshop.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import io.fabric.sdk.android.Fabric;

public class AppController extends Application implements Application.ActivityLifecycleCallbacks{

    public static AppController mAppController;
    public static Context mContext;
    private final String TAG = getClass().getSimpleName();
//    public static AlertDialog dialog;

    public static XMPPService xmppService;
    public Boolean mBounded = false;

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


//        internetConnectionDialog();
        Logger.addLogAdapter(new AndroidLogAdapter());

        if (mEventReceiver == null) mEventReceiver = new XMPPEventReceiver();

        IntentFilter intentFilter = new IntentFilter(Constants.EVT_LOGGED_IN);
        intentFilter.addAction(Constants.EVT_SIGNUP_SUC);
        intentFilter.addAction(Constants.EVT_PASSWORD_CHANGE_SUC);
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

    public XMPPEventReceiver getEventReceiver(){
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

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (mEventReceiver != null) unregisterReceiver(mEventReceiver);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
