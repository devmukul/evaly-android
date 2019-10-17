package bd.com.evaly.evalyshop.activity.chat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.models.xmpp.RoasterModel;
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

    private EditText etPhoneNumber, etContactName;
    private Button btnInvite;
    private ImageView ivAddNumber, ivClose;
    private BottomSheetDialog dialog;
    private boolean isFirst;

    private ChatListAdapter adapter;
    private List<RosterTable> rosterList;

    AppController mChatApp = AppController.getInstance();

    XMPPHandler xmppHandler;
    XMPPEventReceiver xmppEventReceiver;

    RoomWIthRxViewModel viewModel;

    VCard mVCard;

    public XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {

        //On User Presence Changed
        public void onPresenceChanged(PresenceModel presenceModel) {

            Logger.d(presenceModel.getUserStatus());


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
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppController.database.taskDao().updateLastMessage(new Gson().toJson(chatItem), chatItem.getLognTime(), roasterModel.id, roasterModel.unreadCount+1);
                    }
                });
                roasterModel.lastMessage = new Gson().toJson(chatItem);
//                roasterModel.unreadCount = roasterModel.unreadCount + 1;
                rosterList.set(position, roasterModel);
                adapter.notifyItemChanged(position);

                rosterList.remove(position);

                rosterList.add(0, roasterModel);
                adapter.notifyItemMoved(position, 0);
                rvChatList.smoothScrollToPosition(0);
            }

            Logger.d(new Gson().toJson(chatItem));
        }

        public void onNewMessageSent(ChatItem chatItem) {
//            Logger.d("++++++++    SENT   ++++++++");
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
            Logger.d(chatItem.getSender() + "      " + rosterList.get(i).id);
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

//        xmppHandler = AppController.getmService().xmpp;
        rosterList = new ArrayList<>();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                rosterList = AppController.database.taskDao().getAllRosterWithoutObserve();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        populateData(rosterList);
                    }
                });
            }
        });

        viewModel = new RoomWIthRxViewModel(getApplication());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                viewModel.getList()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(list -> {
                            rosterList.clear();
                            rosterList.addAll(list);
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                            Logger.d(list.size() + "  =======");
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    populateData(rosterList);
//                                }
//                            });
                        }, e -> {

                        });

//                Logger.d(new Gson().toJson(AppController.database.taskDao().getAllRoster()));
            }
        });

//        Logger.d(mUserList.size());
//        showLoading();
//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                loadRoster();
//            }
//        });

        startXmppService();

        xmppEventReceiver = mChatApp.getEventReceiver();
//        mVCard = xmppHandler.mVcard;

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {

//                        rosterList = (List<RosterTable>) AppController.database.taskDao().getAllRoster()
//                                .subscribeOn(Schedulers.computation())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(list -> {
//                                    //consume modelClasses here which is a list of ModelClass
//                                    Logger.d("RoomWithRx: " + list.size());
//
//                                }, e -> System.out.println("RoomWithRx: " +e.getMessage()));
                        rosterList = AppController.database.taskDao().getAllRosterWithoutObserve();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                populateData(rosterList);
                            }
                        });
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                populateData(rosterList);
                            }
                        });
                        Logger.d(new Gson().toJson(AppController.database.taskDao().getAllRoster()));
                    }
                });
            }
        });

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


//        xmppHandler.createGroupChat(CredentialManager.getUserName(), "EvalyGroup");
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


    private RosterTable getContactFromRoster(String number) {
        RosterTable roasterModel = null;
        for (RosterTable model : rosterList) {
            if (model.id.contains(number)) {
                roasterModel = model;
            }
        }
        return roasterModel;
    }

    private void getContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_CONTACTS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContact();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTACTS && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                Uri contactData = data.getData();
                String cNumber = "";
                Cursor c = managedQuery(contactData, null, null, null, null);
                if (c.moveToFirst()) {
                    try {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones.getColumnIndex("data1"));
                            System.out.println("number is:" + cNumber);
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        cNumber = cNumber.replaceAll("\\D", "");
                        if (cNumber.length() == 13) {
                            cNumber = cNumber.substring(2);
                        }
                        Logger.d(name + "     " + cNumber);

                        etPhoneNumber.setText(cNumber);
                        etContactName.setText(name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
//        Logger.d(data.getData());
    }

    @OnClick(R.id.ivBack)
    void back() {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatApp.getEventReceiver().setListener(xmppCustomEventListener);

        if (isFirst) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
//                            rosterList = (List<RosterTable>) AppController.database.taskDao().getAllRoster()
//                                    .subscribeOn(Schedulers.computation())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(list -> {
//                                        //consume modelClasses here which is a list of ModelClass
//                                        Logger.d("RoomWithRx: " + list.size());
//
//                                    }, e -> System.out.println("RoomWithRx: " +e.getMessage()));
                            rosterList = AppController.database.taskDao().getAllRosterWithoutObserve();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    populateData(rosterList);
                                }
                            });
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    populateData(rosterList);
                                }
                            });
//                            Logger.d(new Gson().toJson(AppController.database.taskDao().getAllRoster()));
                        }
                    });
                }
            }, 300);
        }
        isFirst = true;

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
