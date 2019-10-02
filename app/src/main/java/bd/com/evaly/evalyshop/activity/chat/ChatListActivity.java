package bd.com.evaly.evalyshop.activity.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.models.xmpp.RoasterModel;
import bd.com.evaly.evalyshop.models.xmpp.VCardObject;
import bd.com.evaly.evalyshop.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatListActivity extends AppCompatActivity implements ChatListAdapter.RecyclerViewOnItemClickListenerChat {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvChatList)
    RecyclerView rvChatList;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;

    private ChatListAdapter adapter;
    private List<RoasterModel> mUserList;

    AppController mChatApp = AppController.getInstance();

    XMPPHandler xmppHandler;
    XMPPEventReceiver xmppEventReceiver;

    public XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {

        //On User Presence Changed
        public void onPresenceChanged(PresenceModel presenceModel) {

            Logger.d(presenceModel.getUserStatus());


            for (int i = 0; i < mUserList.size(); i++) {

                RoasterModel model = mUserList.get(i);
                Logger.d(new Gson().toJson(model));
                Logger.d(new Gson().toJson(presenceModel));

                try {
                    if (AppController.getmService().xmpp.checkSender(model.getRoasterEntryUser(), JidCreate.bareFrom(presenceModel.getUser()))) {
                        model.setStatus(presenceModel.getUserStatus());
                        mUserList.set(i, model);
                        adapter.notifyItemChanged(i);
                        break;
                    }
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
            }
//            adapter.notifyDataSetChanged();
        }

        public void onNewMessageReceived(ChatItem chatItem) {
            Logger.d(chatItem.getChat());
            chatItem.setIsMine(false);
            int position = getListPosition(chatItem);

            RoasterModel roasterModel = mUserList.get(position);
            mUserList.remove(position);
            mUserList.add(0, roasterModel);
            adapter.notifyDataSetChanged();
//            adapter.notifyItemChanged(position);


//            Logger.d(mVCard.getFrom().toString());
            Logger.d(new Gson().toJson(chatItem));
        }

        public void onNewMessageSent(ChatItem chatItem) {
            Logger.d("++++++++    SENT   ++++++++");
            chatItem.setIsMine(true);
            int position = getListPosition(chatItem);

            RoasterModel roasterModel = mUserList.get(position);
            mUserList.remove(position);
            mUserList.add(0, roasterModel);
            adapter.notifyDataSetChanged();
        }


        public void onSubscriptionRequest(final String fromUserID) {
            Logger.e("New request - " + fromUserID);
            try {
                xmppHandler.confirmSubscription(JidCreate.bareFrom(fromUserID), true);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
        }
    };

    private int getListPosition(ChatItem chatItem) {
        int pos = 0;
        for (int i = 0; i < mUserList.size(); i++) {
            Logger.d(chatItem.getSender() + "      " + mUserList.get(i).getRoasterPresenceFrom().asBareJid().toString());
            if (mUserList.get(i).getRoasterPresenceFrom().asBareJid().toString().equalsIgnoreCase(chatItem.getSender())) {
                pos = i;
            }
        }
        return pos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        xmppHandler = AppController.getmService().xmpp;
        mUserList = xmppHandler.getAllRoaster();

        Logger.d(mUserList.size());

        xmppEventReceiver = mChatApp.getEventReceiver();

        populateDate();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateDate();
            }
        });

    }

    private void populateDate() {
        adapter = new ChatListAdapter(mUserList, this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvChatList.setLayoutManager(layoutManager);
        rvChatList.setAdapter(adapter);

        Logger.d(mUserList.size());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.json(new Gson().toJson(adapter.getList()));
//        mUserList.clear();
                List<RoasterModel> list = adapter.getList();
                Collections.sort(list);
                Collections.reverse(list);
                Logger.json(new Gson().toJson(list));
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);

    }

    @OnClick(R.id.fab)
    void fab() {
        xmppHandler.createGroupChat(CredentialManager.getUserName(), "EvalyGroup");
//        xmppHandler.sendRequestTo("01673375194", CredentialManager.getUserData().getFirst_name());
//        String id = CredentialManager.getUserName();
//        if (!id.contains("@")) {
//            id = id + "@" + Constants.XMPP_DOMAIN;
//        }
//
//        BareJid jid = null;
//        try {
//            jid = JidCreate.bareFrom(id);
//        } catch (XmppStringprepException e) {
//            e.printStackTrace();
//        }
//        xmppHandler.confirmSubscription(jid, false);
    }

    @OnClick(R.id.ivBack)
    void back() {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        xmppEventReceiver.setListener(xmppCustomEventListener);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(VCard object, int position, RoasterModel roasterModel) {
        VCard vCard = (VCard) object;
        Logger.d(vCard.getTo());
        Logger.d(vCard.getFrom());

        VCardObject vCardObject = new VCardObject(vCard.getFirstName() + " " + vCard.getLastName(), vCard.getFrom(), vCard.getField("URL"), mUserList.get(position).getStatus());
        startActivity(new Intent(ChatListActivity.this, ChatDetailsActivity.class).putExtra("vcard", (Serializable) vCardObject));
    }

    @Override
    public void onAllDataLoaded(List<RoasterModel> list) {
        Logger.json(new Gson().toJson(list));
        mUserList.clear();
        Collections.sort(list);
        Logger.json(new Gson().toJson(list));
        mUserList.addAll(list);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                adapter.notifyDataSetChanged();
//            }
//        },200);
    }
}
