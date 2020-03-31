package bd.com.evaly.evalyshop.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.util.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.util.xmpp.XMPPService;

public class XmppConnectionIntentService extends IntentService {
    AppController mChatApp = AppController.getInstance();
    XMPPHandler xmppHandler;

    public XmppConnectionIntentService() {
        super("My Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!XMPPService.isServiceRunning) {
            Intent i = new Intent(this, XMPPService.class);
            mChatApp.UnbindService();
            mChatApp.BindService(i);
        } else {
            if (AppController.getmService() == null)
                return;
            if (AppController.getmService().xmpp == null)
                return;

            xmppHandler = AppController.getmService().xmpp;
            if (!xmppHandler.isConnected()) {
                xmppHandler.connect();
            } else {
                xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
                xmppHandler.login();
            }
        }
    }
}
