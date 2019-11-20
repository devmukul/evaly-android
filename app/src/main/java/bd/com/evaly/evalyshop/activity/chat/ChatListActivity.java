package bd.com.evaly.evalyshop.activity.chat;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.chat.invite.InviteActivity;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.models.xmpp.RoasterModel;
import bd.com.evaly.evalyshop.models.xmpp.RosterItemModel;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.viewmodel.RoomWIthRxViewModel;
import bd.com.evaly.evalyshop.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

    ViewDialog loading;


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


            if (position == -1) {

            } else {
                RosterTable roasterModel = rosterList.get(position);

                if (chatItem.getSender().contains(Constants.EVALY_NUMBER)) {
                    updateEvalyChat(chatItem);
                } else {

//                    AsyncTask.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            AppController.database.taskDao().updateLastMessage(new Gson().toJson(chatItem), chatItem.getLognTime(), roasterModel.id, roasterModel.unreadCount + 1);
//                        }
//                    });
                    roasterModel.lastMessage = new Gson().toJson(chatItem);
//                roasterModel.unreadCount = roasterModel.unreadCount + 1;
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

        loading = new ViewDialog(this);

        rosterList = new ArrayList<>();

        viewModel = ViewModelProviders.of(this).get(RoomWIthRxViewModel.class);

        viewModel.rosterList.observe(this, new Observer<List<RosterTable>>() {
            @Override
            public void onChanged(@Nullable List<RosterTable> rosterItemModels) {
                loading.hideDialog();
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
                if (aBoolean){
                    currentPage = currentPage+1;
                }
                Logger.d(aBoolean+"    =======");
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
                        if (hasNext){
                            viewModel.loadRosterList(CredentialManager.getUserName(), currentPage, limit);
                        }

                    } catch (Exception e) {
                        Log.e("load more product", e.toString());
                    }


                }
            }
        });

//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//
//                viewModel.getList()
//                        .subscribeOn(Schedulers.computation())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(list -> {
//                            rosterList.clear();
//                            rosterList.addAll(list);
//                            for (int i = 0; i < rosterList.size(); i++) {
//                                if (rosterList.get(i).id.contains(Constants.EVALY_NUMBER)) {
//                                    RosterTable table = rosterList.get(i);
//                                    if (table.lastMessage == null || table.lastMessage.trim().equals("")) {
////                                            sendMessage();
//                                    }
//                                    Logger.d(new Gson().toJson(table));
//
//                                    table.status = 1;
//                                    rosterList.remove(i);
//                                    rosterList.add(0, table);
//                                }
//                            }
////                                if (rosterList.size() > 0) {
////                                    not.setVisibility(View.GONE);
////                                } else {
////                                    not.setVisibility(View.VISIBLE);
////                                }
//                            if (adapter != null) {
//                                adapter.notifyDataSetChanged();
//                            }
//
//                            Logger.d(list.size() + "  =======");
////                            new Handler(Looper.getMainLooper()).post(new Runnable() {
////                                @Override
////                                public void run() {
////                                    populateData(rosterList);
////                                }
////                            });
//                        }, e -> {
//
//                        });
//
////                Logger.d(new Gson().toJson(AppController.database.taskDao().getAllRoster()));
//            }
//        });

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
//                hasNext = false;
                viewModel.loadRosterList(CredentialManager.getUserName(), currentPage, limit);
//                AsyncTask.execute(new Runnable() {
//                    @Override
//                    public void run() {
//
////                        rosterList = (List<RosterTable>) AppController.database.taskDao().getAllRoster()
////                                .subscribeOn(Schedulers.computation())
////                                .observeOn(AndroidSchedulers.mainThread())
////                                .subscribe(list -> {
////                                    //consume modelClasses here which is a list of ModelClass
////                                    Logger.d("RoomWithRx: " + list.size());
////
////                                }, e -> System.out.println("RoomWithRx: " +e.getMessage()));
////                        rosterList = AppController.database.taskDao().getAllRosterWithoutObserve();
////                        for (int i = 0; i < rosterList.size(); i++) {
////                            if (rosterList.get(i).id.contains(Constants.EVALY_NUMBER)) {
////                                RosterTable table = rosterList.get(i);
////                                table.status = 1;
////                                rosterList.remove(i);
////                                rosterList.add(0, table);
////                            }
////                        }
////                        new Handler(Looper.getMainLooper()).post(new Runnable() {
////                            @Override
////                            public void run() {
////                                populateData(rosterList);
////                            }
////                        });
////                        new Handler(Looper.getMainLooper()).post(new Runnable() {
////                            @Override
////                            public void run() {
////                                populateData(rosterList);
////                            }
////                        });
////                        Logger.d(new Gson().toJson(AppController.database.taskDao().getAllRoster()));
//                    }
//                });
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
        rosterList.clear();
        currentPage = 1;
        viewModel.loadRosterList(CredentialManager.getUserName(), currentPage, limit);
//        if (isFirst) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    AsyncTask.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            rosterList = AppController.database.taskDao().getAllRosterWithoutObserve();
//                            for (int i = 0; i < rosterList.size(); i++) {
//                                if (rosterList.get(i).id.contains(Constants.EVALY_NUMBER)) {
//                                    RosterTable table = rosterList.get(i);
//                                    table.status = 1;
//                                    rosterList.remove(i);
//                                    rosterList.add(0, table);
//                                }
//                            }
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    populateData(rosterList);
//                                }
//                            });
//                        }
//                    });
//                }
//            }, 300);
//        }
//        isFirst = true;

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
