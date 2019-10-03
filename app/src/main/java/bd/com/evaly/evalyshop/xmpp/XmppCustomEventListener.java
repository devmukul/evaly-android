package bd.com.evaly.evalyshop.xmpp;

import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.ChatStateModel;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;


public class XmppCustomEventListener implements XMPPEventReceiver.XmppCustomEventListenerBase {

    public void onNewMessageReceived(ChatItem chatItem) {}
    public void onNewMessageSent(ChatItem chatItem) { }
    public void onLoggedIn() {}
    public void onSignupSuccess() {}
    public void onSignupFailed(String error) {}
    public void onUpdateUserSuccess() {}
    public void onUpdateUserFailed(String error) { }
    public void onAuthenticated() {}
    public void onReConnectionError() {}
    public void onConnected() {}
    public void onReConnected() {}
    public void onConnectionClosed() {}
    public void onReConnection() {}
    public void onPasswordChanged(){}
    public void onLoginFailed(String msg) {}
    public void onPresenceChanged(PresenceModel presenceModel) {}
    public void onChatStateChanged(ChatStateModel chatStateModel){}
    public void onSubscriptionRequest(String fromuser){}
}
