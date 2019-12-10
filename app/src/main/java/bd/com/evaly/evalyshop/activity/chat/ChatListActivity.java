package bd.com.evaly.evalyshop.activity.chat;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.chat.invite.InviteActivity;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.models.xmpp.RoasterModel;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.viewmodel.RoomWIthRxViewModel;
import bd.com.evaly.evalyshop.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatListActivity extends AppCompatActivity implements ChatListAdapter.RecyclerViewOnItemClickListenerChat {

    private static final int REQUEST_CODE_CONTACTS = 145;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvChatList)
    RecyclerView rvChatList;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.not)
    LinearLayout not;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvBody)
    TextView tvBody;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvEvalyUnreadCount)
    TextView tvEvalyUnreadCount;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.nestedScroll)
    NestedScrollView nestedScroll;

    private boolean isFirst;
    int limit = 20;
    int currentPage = 1;
    boolean hasNext;

    private ChatListAdapter adapter;
    private List<RosterTable> rosterList;

    AppController mChatApp = AppController.getInstance();

    XMPPHandler xmppHandler;
    XMPPEventReceiver xmppEventReceiver;

    RoomWIthRxViewModel viewModel;
    RosterTable evalyTable;


    VCard mVCard;

    public XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {

        //On User Presence Changed
        public void onLoggedIn() {
            xmppHandler = AppController.getmService().xmpp;
            try {
                ChatItem chatItem = xmppHandler.getLastMessage(JidCreate.bareFrom(Constants.EVALY_NUMBER + "@" + Constants.XMPP_HOST));
                updateEvalyChat(chatItem);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
        }

        public void onConnected() {
            xmppHandler = AppController.getmService().xmpp;
            try {
                ChatItem chatItem = xmppHandler.getLastMessage(JidCreate.bareFrom(Constants.EVALY_NUMBER + "@" + Constants.XMPP_HOST));
                updateEvalyChat(chatItem);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
        }

        public void onLoginFailed(String msg) {
            if (msg.contains("already logged in")) {
                try {
                    ChatItem chatItem = xmppHandler.getLastMessage(JidCreate.bareFrom(Constants.EVALY_NUMBER + "@" + Constants.XMPP_HOST));
                    updateEvalyChat(chatItem);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onPresenceChanged(PresenceModel presenceModel) {

//            Logger.d(presenceModel.getUserStatus());

            try {
                for (int i = 0; i < rosterList.size(); i++) {

                    RosterTable model = rosterList.get(i);
//                    Logger.d(new Gson().toJson(model));
//                    Logger.d(new Gson().toJson(presenceModel));


                    try {
                        if (AppController.getmService().xmpp.checkSender(JidCreate.bareFrom(model.id), JidCreate.bareFrom(presenceModel.getUser()))) {
                            model.status = presenceModel.getUserStatus();
                            rosterList.set(i, model);
                            adapter.notifyItemChanged(i);
                            break;
                        }
                    } catch (XmppStringprepException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
//            adapter.notifyDataSetChanged();
        }

        public void onNewMessageReceived(ChatItem chatItem) {
            Logger.d(chatItem.getChat());
            chatItem.setIsMine(false);
            int position = getListPosition(chatItem);

            if (chatItem.getSender().contains(Constants.EVALY_NUMBER)) {
                evalyTable.unreadCount = evalyTable.unreadCount + 1;
                tvEvalyUnreadCount.setVisibility(View.VISIBLE);
                tvEvalyUnreadCount.setText(evalyTable.unreadCount + "");
                updateEvalyChat(chatItem);
            }

            if (position == -1) {

            } else {
                RosterTable roasterModel = rosterList.get(position);

                if (chatItem.getSender().contains(Constants.EVALY_NUMBER)) {
                    updateEvalyChat(chatItem);
                } else {

                    roasterModel.lastMessage = new Gson().toJson(chatItem);
                    roasterModel.unreadCount = roasterModel.unreadCount + 1;
                    rosterList.set(position, roasterModel);
                    adapter.notifyItemChanged(position);

                    rosterList.remove(position);

                    rosterList.add(0, roasterModel);
                    adapter.notifyItemMoved(position, 0);
                    rvChatList.smoothScrollToPosition(0);
                }
            }

            Logger.d(new Gson().toJson(chatItem));
        }

        public void onNewMessageSent(ChatItem chatItem) {
            Logger.d("++++++++    SENT   ++++++++");
//            chatItem.setIsMine(true);
//            int position = getListPosition(chatItem);
//
//            RoasterModel roasterModel = mUserList.get(position);
//            mUserList.remove(position);
//            mUserList.add(0, roasterModel);
//            adapter.notifyDataSetChanged();
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
        int pos = -1;
        for (int i = 0; i < rosterList.size(); i++) {
//            Logger.d(chatItem.getSender() + "      " + rosterList.get(i).id);
            if (rosterList.get(i).id.equalsIgnoreCase(chatItem.getSender())) {
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

        rosterList = new ArrayList<>();
        evalyTable = new RosterTable();

        viewModel = ViewModelProviders.of(this).get(RoomWIthRxViewModel.class);

        viewModel.rosterList.observe(this, new Observer<List<RosterTable>>() {
            @Override
            public void onChanged(@Nullable List<RosterTable> rosterItemModels) {
                if (currentPage == 1) {
                    rosterList.clear();
                }
                rosterList.addAll(rosterItemModels);
                progressBar.setVisibility(View.GONE);
                populateData(rosterList);
            }
        });

        viewModel.isSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                progressBar.setVisibility(View.GONE);
                if (!aBoolean) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            }
        });

        viewModel.hasNext.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    currentPage = currentPage + 1;
                }
                Logger.d(aBoolean + "    =======");
                hasNext = aBoolean;
            }
        });

        nestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                String TAG = "nested_sync";
//
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {

                    try {
                        if (hasNext) {
                            viewModel.loadRosterList(CredentialManager.getUserName(), currentPage, limit);
                        }

                    } catch (Exception e) {
                        Log.e("load more product", e.toString());
                    }


                }
            }
        });


        startXmppService();

        if (xmppHandler != null && xmppHandler.isConnected()) {
            try {
                ChatItem chatItem = xmppHandler.getLastMessage(JidCreate.bareFrom(Constants.EVALY_NUMBER + "@" + Constants.XMPP_HOST));
                updateEvalyChat(chatItem);
                Logger.d(new Gson().toJson(chatItem));
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
        }

        xmppEventReceiver = mChatApp.getEventReceiver();
//        mVCard = xmppHandler.mVcard;

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                rosterList.clear();
                viewModel.loadRosterList(CredentialManager.getUserName(), currentPage, limit);

            }
        });

    }

    @OnClick(R.id.llEvaly)
    void evaly() {
        RosterTable roasterModel = new RosterTable();
        roasterModel.id = Constants.EVALY_NUMBER + "@" + Constants.XMPP_HOST;
        roasterModel.rosterName = "Evaly";
        roasterModel.imageUrl = Constants.EVALY_LOGO;
        startActivity(new Intent(ChatListActivity.this, ChatDetailsActivity.class)
                .putExtra("roster", (Serializable) roasterModel));
    }

    private void updateEvalyChat(ChatItem chatItem) {
        if (chatItem == null) {

        } else {
            if (chatItem.getSender().contains(CredentialManager.getUserName())) {
                if (chatItem.getMessageType().equalsIgnoreCase(Constants.TYPE_IMAGE)) {
                    tvBody.setText("You: Sent an image");
                } else {
                    tvBody.setText("You: " + chatItem.getChat());
                }

            } else {
                if (chatItem.getMessageType().equalsIgnoreCase(Constants.TYPE_IMAGE)) {
                    tvBody.setText("Sent an image");
                } else {
                    tvBody.setText(chatItem.getChat());
                }
            }
            tvTime.setText(chatItem.getTime());

        }
    }

    private void populateData(List<RosterTable> roasterModelList) {
        swipeRefreshLayout.setRefreshing(false);
        rosterList = roasterModelList;
        adapter = new ChatListAdapter(rosterList, this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvChatList.setLayoutManager(layoutManager);
        rvChatList.setAdapter(adapter);

//        Logger.d(mUserList.size());


//                Logger.json(new Gson().toJson(adapter.getList()));
//        mUserList.clear();
//                List<RoasterModel> list = adapter.getList();
//                Collections.sort(list);
//                Collections.reverse(list);
//        Collections.sort(mUserList);
//        Collections.reverse(mUserList);
////                Logger.json(new Gson().toJson(list));
//        adapter = new ChatListAdapter(mUserList, this, this);
//        rvChatList.setAdapter(adapter);
    }

    @OnClick(R.id.fab)
    void fab() {
        startActivity(new Intent(ChatListActivity.this, InviteActivity.class));

    }

    @OnClick(R.id.llRequest)
    void showRequest() {
        startActivity(new Intent(ChatListActivity.this, RequestListActivity.class));
    }


    @OnClick(R.id.ivBack)
    void back() {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatApp.getEventReceiver().setListener(xmppCustomEventListener);

        progressBar.setVisibility(View.VISIBLE);
        currentPage = 1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                viewModel.loadRosterList(CredentialManager.getUserName(), currentPage, limit);
            }
        }).start();

        if (xmppHandler != null) {
            try {
                ChatItem chatItem = xmppHandler.getLastMessage(JidCreate.bareFrom(Constants.EVALY_NUMBER + "@" + Constants.XMPP_HOST));
                updateEvalyChat(chatItem);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (xmppHandler!= null){
            disconnectXmpp();
        }
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    private void disconnectXmpp(){
        xmppHandler.disconnect();
        stopService(new Intent(ChatListActivity.this, XMPPService.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(RosterTable roasterModel) {
//        VCard vCard = (VCard) object;
//        Logger.d(vCard.getTo());
//        Logger.d(vCard.getFrom());

//        adapter.notifyItemChanged(position);

//
//        VCardObject vCardObject = new VCardObject(vCard.getFirstName() + " " + vCard.getLastName(), vCard.getFrom(), vCard.getField("URL"), mUserList.get(position).getStatus());
        startActivity(new Intent(ChatListActivity.this, ChatDetailsActivity.class)
                .putExtra("roster", (Serializable) roasterModel));

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
    public void onAllDataLoaded(List<RoasterModel> list) {
//        Logger.json(new Gson().toJson(list));
//        hideLoading();
//        mUserList.clear();
//        Collections.sort(list);
//        Logger.json(new Gson().toJson(list));
//        mUserList.addAll(list);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                adapter.notifyDataSetChanged();
//            }
//        },200);
    }
}
