package bd.com.evaly.evalyshop.activity.chat;

import android.Manifest;
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
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.models.xmpp.RoasterModel;
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
import retrofit2.Response;

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
    private ViewDialog dialog;
    private BottomSheetDialog bottomSheetDialog;
    private boolean isFirst;

    private ChatListAdapter adapter;
    private List<RosterTable> rosterList;

    AppController mChatApp = AppController.getInstance();

    XMPPHandler xmppHandler;
    XMPPEventReceiver xmppEventReceiver;

    RoomWIthRxViewModel viewModel;

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


            RosterTable roasterModel = rosterList.get(position);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    AppController.database.taskDao().updateLastMessage(new Gson().toJson(chatItem), chatItem.getLognTime(), roasterModel.id);
                }
            });

            roasterModel.lastMessage = new Gson().toJson(chatItem);
            roasterModel.unreadCount = roasterModel.unreadCount + 1;
            rosterList.set(position, roasterModel);
            adapter.notifyItemChanged(position);

            rosterList.remove(position);

            rosterList.add(0, roasterModel);
            adapter.notifyItemMoved(position, 0);
            rvChatList.smoothScrollToPosition(0);


//            adapter.notifyItemChanged(position);


//            Logger.d(mVCard.getFrom().toString());
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
        int pos = 0;
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

        xmppHandler = AppController.getmService().xmpp;


        dialog = new ViewDialog(this);
//        Logger.d(mUserList.size());
        rosterList = new ArrayList<>();

        viewModel = new RoomWIthRxViewModel(getApplication());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                viewModel.getList()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(list -> {
                            rosterList = list;
                            Logger.d(list.size()+"  =======");
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    populateData(rosterList);
                                }
                            });
                        }, e ->{

                        });

//                Logger.d(new Gson().toJson(AppController.database.taskDao().getAllRoster()));
            }
        });

        startXmppService();

        xmppEventReceiver = mChatApp.getEventReceiver();

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
    }

    @OnClick(R.id.fab)
    void fab() {

        bottomSheetDialog = new BottomSheetDialog(ChatListActivity.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.invite_view);

        etContactName = bottomSheetDialog.findViewById(R.id.etContactName);
        etPhoneNumber = bottomSheetDialog.findViewById(R.id.etContactNumber);
        ivAddNumber = bottomSheetDialog.findViewById(R.id.ivAddNumber);
        ivClose = bottomSheetDialog.findViewById(R.id.ivClose);
        btnInvite = bottomSheetDialog.findViewById(R.id.btnInvite);

        ivAddNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(ChatListActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatListActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_CONTACTS);
                } else {
                    getContact();
                }
            }
        });

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etPhoneNumber.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter phone number!", Toast.LENGTH_LONG).show();
                } else if (etContactName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter name!", Toast.LENGTH_LONG).show();
                } else {
                    if (etPhoneNumber.getText().toString().matches("^(01)[3-9][0-9]{8}$")) {

                        RosterTable roasterModel = getContactFromRoster(etPhoneNumber.getText().toString());
                        Logger.d(new Gson().toJson(roasterModel));
                        if (!CredentialManager.getUserName().equalsIgnoreCase(etPhoneNumber.getText().toString())) {
                            if (roasterModel != null) {
                                dialog.hideDialog();
                                bottomSheetDialog.dismiss();
//                        VCard vCard = null;
//                        try {
//                            vCard = xmppHandler.getUserDetails(JidCreate.bareFrom(roasterModel.id).asEntityBareJidIfPossible());
//                        } catch (XmppStringprepException e) {
//                            e.printStackTrace();
//                        }
//                        VCardObject vCardObject = new VCardObject(vCard.getFirstName() + " " + vCard.getLastName(), vCard.getFrom(), vCard.getField("URL"), 0);
                                startActivity(new Intent(ChatListActivity.this, ChatDetailsActivity.class).putExtra("roster", (Serializable) roasterModel));
                            } else {
                                dialog.showDialog();
                                bottomSheetDialog.dismiss();
                                HashMap<String, String> data = new HashMap<>();
                                data.put("localuser", CredentialManager.getUserName());
                                data.put("localserver", Constants.XMPP_HOST);
                                data.put("user", etPhoneNumber.getText().toString());
                                data.put("server", Constants.XMPP_HOST);
                                data.put("nick", etContactName.getText().toString());
                                data.put("subs", "both");
                                data.put("group", "evaly");

                                addRosterByOther();

                                AuthApiHelper.addRoster(data, new DataFetchingListener<Response<JsonPrimitive>>() {
                                    @Override
                                    public void onDataFetched(Response<JsonPrimitive> response) {

                                        if (response.code() == 200 || response.code() == 201) {
                                            try {
                                                EntityBareJid jid = JidCreate.entityBareFrom(etPhoneNumber.getText().toString().trim() + "@"
                                                        + Constants.XMPP_HOST);
                                                VCard vCard = xmppHandler.getUserDetails(jid);
                                                HashMap<String, String> data1 = new HashMap<>();
                                                data1.put("phone_number", etPhoneNumber.getText().toString().trim());
                                                data1.put("text", "You are invited to \n https://play.google.com/store/apps/details?id=bd.com.evaly.merchant");

                                                Logger.d(new Gson().toJson(vCard.getFirstName())+"       ====");
                                                if (vCard.getFirstName() == null) {
                                                    AuthApiHelper.sendCustomMessage(data1, new DataFetchingListener<Response<JsonObject>>() {
                                                        @Override
                                                        public void onDataFetched(Response<JsonObject> response) {
                                                            dialog.hideDialog();
                                                            if (response.code() == 200 || response.code() == 201) {
                                                                Toast.makeText(getApplicationContext(), "Invitation sent!", Toast.LENGTH_LONG).show();
//                                                                xmppHandler.sendRequestTo(etPhoneNumber.getText().toString(), etPhoneNumber.getText().toString());
                                                                Logger.d("[[[[[[[[[[[");
                                                                RosterTable table = new RosterTable();
                                                                table.id = jid.asUnescapedString();
                                                                table.rosterName = etContactName.getText().toString();
                                                                table.name = "";
                                                                table.status = 0;
                                                                table.unreadCount = 0;
                                                                table.nick_name = "";
                                                                table.imageUrl = "";
                                                                table.lastMessage = "";
                                                                AsyncTask.execute(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Logger.d("NEW ENTRY");
                                                                        AppController.database.taskDao().addRoster(table);
                                                                    }
                                                                });
                                                                bottomSheetDialog.dismiss();
                                                            } else {
                                                                dialog.hideDialog();
                                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailed(int status) {
                                                            dialog.hideDialog();
                                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();

                                                        }
                                                    });
                                                } else {
                                                    dialog.hideDialog();
                                                    RosterTable rosterTable = new RosterTable();
                                                    rosterTable.name = vCard.getFirstName() + " " + vCard.getLastName();
                                                    rosterTable.id = vCard.getFrom().asUnescapedString();
                                                    rosterTable.imageUrl = vCard.getField("URL");
                                                    rosterTable.status = 0;
                                                    rosterTable.lastMessage = "";
                                                    rosterTable.nick_name = vCard.getNickName();
                                                    rosterTable.time = 0;
                                                    startActivity(new Intent(ChatListActivity.this, ChatDetailsActivity.class).putExtra("roster", (Serializable) rosterTable));

                                                }
                                            } catch (XmppStringprepException e) {
                                                e.printStackTrace();
                                            }


                                        } else {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailed(int status) {
                                        dialog.hideDialog();
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "You can't invite yourself!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid phone number!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        View bottomSheetInternal = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    Logger.d("=---==========------");
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    etContactName.setText("");
                    etPhoneNumber.setText("");
                    bottomSheetDialog.dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        bottomSheetDialog.setCancelable(false);

        bottomSheetDialog.show();

//        startXmppService();
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

    private void addRosterByOther() {
        HashMap<String, String> data = new HashMap<>();
        data.put("localuser", etPhoneNumber.getText().toString());
        data.put("localserver", Constants.XMPP_HOST);
        data.put("user", CredentialManager.getUserName());
        data.put("server", Constants.XMPP_HOST);
        data.put("nick", CredentialManager.getUserData().getFirst_name());
        data.put("subs", "both");
        data.put("group", "evaly");
        AuthApiHelper.addRoster(data, new DataFetchingListener<Response<JsonPrimitive>>() {
            @Override
            public void onDataFetched(Response<JsonPrimitive> response) {

            }

            @Override
            public void onFailed(int status) {

            }
        });
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
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
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
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
        Logger.d(data.getData());
    }

    @OnClick(R.id.ivBack)
    void back() {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        xmppEventReceiver.setListener(xmppCustomEventListener);

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
//
//        VCardObject vCardObject = new VCardObject(vCard.getFirstName() + " " + vCard.getLastName(), vCard.getFrom(), vCard.getField("URL"), mUserList.get(position).getStatus());
//        startActivity(new Intent(ChatListActivity.this, ChatDetailsActivity.class).putExtra("vcard", (Serializable) vCardObject));
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
//        mUserList.clear();
//        Collections.sort(list);
//        Logger.json(new Gson().toJson(list));
//        mUserList.addAll(list);
    }
}
