package bd.com.evaly.evalyshop.activity.chat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestListActivity extends BaseActivity implements RequestListAdapter.OnAcceptRejectListener {

    @BindView(R.id.rvRequest)
    RecyclerView rvRequest;

    private RequestListAdapter adapter;

    private List<Jid> list;

    AppController mChatApp = AppController.getInstance();

    XMPPHandler xmppHandler;
    XMPPEventReceiver xmppEventReceiver;

    public XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {

        //On User Presence Changed
        public void onLoggedIn() {
            xmppHandler = AppController.getmService().xmpp;
            list.clear();
            list.addAll(xmppHandler.getPendingRequests());
            adapter.notifyDataSetChanged();
        }

        public void onConnected() {
            xmppHandler = AppController.getmService().xmpp;
            try {
                if (xmppHandler.isLoggedin()){
                    list.clear();
                    list.addAll(xmppHandler.getPendingRequests());
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onLoginFailed(String msg) {
            if (msg.contains("already logged in")) {
                xmppHandler = AppController.getmService().xmpp;
                list.clear();
                list.addAll(xmppHandler.getPendingRequests());
                adapter.notifyDataSetChanged();

                Logger.d(list.size()+"   =========");
            }
        }


        public void onSubscriptionRequest(final String fromUserID) {
            Logger.e("New request - " + fromUserID);
            Logger.d(xmppHandler.isConnected()+"    "+xmppHandler.isLoggedin());
            try {
                Jid jid = JidCreate.from(fromUserID);
                list.add(0, jid);
                Logger.e(list.size()+"");
                adapter.notifyDataSetChanged();
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
//            try {
//                xmppHandler.confirmSubscription(JidCreate.bareFrom(fromUserID), true);
//            } catch (XmppStringprepException e) {
//                e.printStackTrace();
//            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        ButterKnife.bind(this);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Message Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = new ArrayList<>();

        adapter = new RequestListAdapter(list, this, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvRequest.setLayoutManager(layoutManager);
        rvRequest.setAdapter(adapter);


        xmppEventReceiver = mChatApp.getEventReceiver();

        xmppHandler = AppController.getmService().xmpp;

        if (xmppHandler != null && xmppHandler.isLoggedin()){
            xmppHandler = AppController.getmService().xmpp;
            list.clear();
            list.addAll(xmppHandler.getPendingRequests());
            adapter.notifyDataSetChanged();
            Logger.d(list.size()+"    =======");
        }else {
            startXmppService();
        }

    }

    private void startXmppService() {
        if (!XMPPService.isServiceRunning) {
            Intent intent = new Intent(this, XMPPService.class);
            mChatApp.UnbindService();
            mChatApp.BindService(intent);
        } else {
            xmppHandler = AppController.getmService().xmpp;
            if (!xmppHandler.isConnected()) {
                xmppHandler.connect();
            } else {
                xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
                xmppHandler.login();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatApp.getEventReceiver().setListener(xmppCustomEventListener);
        xmppEventReceiver.setListener(xmppCustomEventListener);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestAccept(Jid jid) {
        xmppHandler.confirmSubscription((BareJid) jid, true);
        Toast.makeText(getApplicationContext(), "Request Accepted!", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                list.remove(jid);
                adapter.notifyDataSetChanged();
            }
        }, 500);
    }

    @Override
    public void onRequestReject(Jid jid) {
        xmppHandler.confirmSubscription((BareJid) jid, false);
        Toast.makeText(getApplicationContext(), "Request Rejected!", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                list.remove(jid);
                adapter.notifyDataSetChanged();
            }
        }, 500);
    }
}
