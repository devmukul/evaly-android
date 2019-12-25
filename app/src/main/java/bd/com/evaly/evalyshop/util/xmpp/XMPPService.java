package bd.com.evaly.evalyshop.util.xmpp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;


import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.jxmpp.jid.Jid;

import java.io.Serializable;

import bd.com.evaly.evalyshop.models.xmpp.ChatStateModel;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.util.Constants;


public class XMPPService extends Service {

    private final String TAG = getClass().getSimpleName();
    public XMPPHandler xmpp;
    public static boolean isServiceRunning = false;

    public XMPPService(){}

    @Override
    public void onCreate() {
        super.onCreate();
        xmpp = new XMPPHandler(XMPPService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(intent != null){
            if (xmpp == null){
                xmpp = new XMPPHandler(XMPPService.this);
            }
            xmpp.connect();
            XMPPService.isServiceRunning = true;
        }

        return new LocalBinder<>(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("SERVICE DESTROYED");
        if(xmpp != null) xmpp.disconnect();
//        xmpp = null;
        XMPPService.isServiceRunning = false;
    }

    public void onNewMessage(final String chatItem) {
        Logger.d("New MESSAGE");
        Intent intent = new Intent(Constants.EVT_NEW_MSG);
        intent.putExtra(Constants.INTENT_KEY_NEWMSG, chatItem);
        sendBroadcast(intent);
    }

    public void onNewMessageSent(String chatItem){
        Intent intent = new Intent(Constants.EVT_NEW_MSG_SENT);
        intent.putExtra(Constants.INTENT_KEY_NEWMSG_SENT, chatItem);
        sendBroadcast(intent);
    }

    public void onAuthenticated() {
        sendBroadcast(new Intent(Constants.EVT_AUTH_SUC));
    }

    public void onReConnectionError() {
        sendBroadcast(new Intent(Constants.EVT_RECONN_ERR));
    }

    public void onConnected() {
        sendBroadcast(new Intent(Constants.EVT_CONN_SUC));
    }

    public void onReConnected() {
        sendBroadcast(new Intent(Constants.EVT_RECONN_SUC));
    }

    public void onConnectionClosed() {
        sendBroadcast(new Intent(Constants.EVT_CONN_CLOSE));
    }

    public void onReConnection() {
        sendBroadcast(new Intent(Constants.EVT_RECONN_WAIT));
    }

    public void onLoginFailed(String msg){
        sendBroadcast(new Intent(Constants.EVT_LOGIN_ERR).putExtra("msg", msg));
    }

    public void onLoggedIn(){
        sendBroadcast(new Intent(Constants.EVT_LOGGED_IN));
    }

    public void onSignupSuccess(){
        sendBroadcast(new Intent(Constants.EVT_SIGNUP_SUC));
    }

    public void onPasswordChanged(){
        sendBroadcast(new Intent(Constants.EVT_PASSWORD_CHANGE_SUC));
    }

    public void onPasswordChangeFailed(String msg){
        Intent intent = new Intent(Constants.EVT_PASSWORD_CHANGE_FAILED);
        intent.putExtra(Constants.INTENT_KEY_CHANGE_PASS_FAILED,msg);
        sendBroadcast(intent);
    }

    public void onSignupFailed(String error) {
        Intent intent = new Intent(Constants.EVT_SIGNUP_ERR);
        intent.putExtra(Constants.INTENT_KEY_SIGNUP_ERR,error);
        sendBroadcast(intent);
    }
    public void onUpdateUserSuccess(){
        sendBroadcast(new Intent(Constants.EVT_UPDATE_USER_SUC));
    }

    public void onUpdateUserFailed(String error) {
        Intent intent = new Intent(Constants.EVT_UPDATE_USER_ERR);
        intent.putExtra(Constants.INTENT_KEY_UPDATE_USER_ERR,error);
        sendBroadcast(intent);
    }

    public void onPresenceChange(PresenceModel presenceModel){
        Intent intent = new Intent(Constants.EVT_PRESENCE_CHG);
//        Logger.d(new Gson().toJson(presenceModel));
        intent.putExtra(Constants.INTENT_KEY_PRESENCE,presenceModel);
        sendBroadcast(intent);
    }

    public void onChatStateChange(ChatStateModel chatStateModel){
        Logger.d(new Gson().toJson(chatStateModel));
        Intent intent = new Intent(Constants.EVT_CHATSTATE_CHG);
        intent.putExtra(Constants.INTENT_KEY_CHATSTATE,chatStateModel);
        sendBroadcast(intent);
    }

    public void onRequestSubscribe(Jid fromUserID) {
        Intent intent = new Intent(Constants.EVT_REQUEST_SUBSCRIBE);
        intent.putExtra(Constants.INTENT_KEY_NEWREQUEST, (Serializable) fromUserID);
        sendBroadcast(intent);
    }
}
