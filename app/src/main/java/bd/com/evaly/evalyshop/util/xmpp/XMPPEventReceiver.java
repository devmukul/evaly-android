package bd.com.evaly.evalyshop.util.xmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.ChatStateModel;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.util.Constants;


public class XMPPEventReceiver extends BroadcastReceiver {

    XmppCustomEventListenerBase xmppCustomEventListener;

    @Override
    public void onReceive(Context context, Intent intent) {
//        Logger.d(intent.getAction());

        switch ( intent.getAction() ){

            case Constants.EVT_LOGGED_IN:
                if(xmppCustomEventListener != null) xmppCustomEventListener.onLoggedIn();
                break;

            case Constants.EVT_SIGNUP_SUC:
                if(xmppCustomEventListener != null) xmppCustomEventListener.onSignupSuccess();
                break;

            case Constants.EVT_SIGNUP_ERR:
                Bundle intentBundle2 = intent.getExtras();
                String signupError = intentBundle2.getString( Constants.INTENT_KEY_SIGNUP_ERR );
                if(xmppCustomEventListener != null) xmppCustomEventListener.onSignupFailed(signupError);
                break;

            case Constants.EVT_UPDATE_USER_SUC:
                Logger.d("UPDATED");
                if(xmppCustomEventListener != null) xmppCustomEventListener.onUpdateUserSuccess();
                break;

            case Constants.EVT_UPDATE_USER_ERR:
                Bundle intentBundle3 = intent.getExtras();
                String updateError = intentBundle3.getString( Constants.INTENT_KEY_UPDATE_USER_ERR );
                if(xmppCustomEventListener != null) xmppCustomEventListener.onUpdateUserFailed(updateError);
                break;

            case Constants.EVT_AUTH_SUC:
                if(xmppCustomEventListener != null) xmppCustomEventListener.onAuthenticated();
                break;

            case Constants.EVT_PASSWORD_CHANGE_SUC:
                if(xmppCustomEventListener != null) xmppCustomEventListener.onPasswordChanged();
                break;

            case Constants.EVT_PASSWORD_CHANGE_FAILED:
                Bundle intentBundle4 = intent.getExtras();
                String changeError = intentBundle4.getString( Constants.INTENT_KEY_CHANGE_PASS_FAILED );
                if(xmppCustomEventListener != null) xmppCustomEventListener.onPasswordChangeFailed(changeError);
                break;

            case Constants.EVT_RECONN_ERR:
                if(xmppCustomEventListener != null) xmppCustomEventListener.onReConnectionError();
                break;

            case Constants.EVT_RECONN_WAIT:
                if(xmppCustomEventListener != null) xmppCustomEventListener.onReConnection();
                break;

            case Constants.EVT_RECONN_SUC:
                if(xmppCustomEventListener != null) xmppCustomEventListener.onReConnected();
                break;

            case Constants.EVT_CONN_SUC:
                if(xmppCustomEventListener != null) xmppCustomEventListener.onConnected();
                break;

            case Constants.EVT_CONN_CLOSE:
                if(xmppCustomEventListener != null) xmppCustomEventListener.onConnectionClosed();
                break;

            case Constants.EVT_LOGIN_ERR:
                String msg = intent.getStringExtra("msg");
                if(xmppCustomEventListener != null) xmppCustomEventListener.onLoginFailed(msg);
                break;

            case Constants.EVT_NEW_MSG:
                String data = intent.getStringExtra(Constants.INTENT_KEY_NEWMSG);
                ChatItem chatItem = new Gson().fromJson(data, ChatItem.class);
                Logger.d(new Gson().toJson(chatItem));
                if(xmppCustomEventListener != null) xmppCustomEventListener.onNewMessageReceived(chatItem);
                break;

            case Constants.EVT_NEW_MSG_SENT:
                String da = intent.getStringExtra(Constants.INTENT_KEY_NEWMSG_SENT);
//                Logger.d(da);

                ChatItem chat= new Gson().fromJson(da, ChatItem.class);
//                Logger.d(new Gson().toJson(chat));
                if(xmppCustomEventListener != null) xmppCustomEventListener.onNewMessageSent(chat);
                break;

            case Constants.EVT_PRESENCE_CHG:
                try {
                    Bundle bundle = intent.getExtras();
                    PresenceModel presenceModel = (PresenceModel) bundle.getSerializable( Constants.INTENT_KEY_PRESENCE );
//                Logger.d(new Gson().toJson(presenceModel));
                    if(xmppCustomEventListener != null) xmppCustomEventListener.onPresenceChanged(presenceModel);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case Constants.EVT_CHATSTATE_CHG:
                try {
                    Bundle intentBundle = intent.getExtras();
                    ChatStateModel chatStateModel = (ChatStateModel) intentBundle.getSerializable( Constants.INTENT_KEY_CHATSTATE );
//                Logger.d(chatStateModel.getChatState());
                    if(xmppCustomEventListener != null) xmppCustomEventListener.onChatStateChanged(chatStateModel);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case Constants.EVT_REQUEST_SUBSCRIBE:
                Bundle intentBundle1 = intent.getExtras();
                String fromUserID = intentBundle1.getString( Constants.INTENT_KEY_NEWREQUEST );
                if(xmppCustomEventListener != null) xmppCustomEventListener.onSubscriptionRequest(fromUserID);
                break;

        }
    }

    public void setListener(XmppCustomEventListener listener) {
        this.xmppCustomEventListener = listener;
    }

    public interface XmppCustomEventListenerBase{

        void onNewMessageReceived(ChatItem chatItem);
        void onNewMessageSent(ChatItem chatItem);
        void onLoggedIn();
        void onSignupSuccess();
        void onSignupFailed(String error);
        void onUpdateUserSuccess();
        void onUpdateUserFailed(String error);
        void onAuthenticated();
        void onReConnectionError();
        void onConnected();
        void onReConnected();
        void onConnectionClosed();
        void onReConnection();
        void onPasswordChanged();
        void onPasswordChangeFailed(String msg);
        void onLoginFailed(String msg);
        void onPresenceChanged(PresenceModel presenceModel);
        void onChatStateChanged(ChatStateModel chatStateModel);
        void onSubscriptionRequest(String fromuser);

    }
}
