package bd.com.evaly.evalyshop.activity.chat.invite;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.chat.ChatDetailsActivity;
import bd.com.evaly.evalyshop.activity.chat.ChatListActivity;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.RecyclerViewOnItemClickListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.chat.EvalyUserModel;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;

public class InviteActivity extends BaseActivity implements RecyclerViewOnItemClickListener {

    private static final int REQUEST_CODE_CONTACTS = 1212;
    @BindView(R.id.rvInvite)
    RecyclerView rvInvite;
    @BindView(R.id.nestedScroll)
    NestedScrollView nestedScroll;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.etSearch)
    EditText etSearch;

    private EditText etPhoneNumber, etContactName;
    private Button btnInvite;
    private ImageView ivAddNumber, ivClose;
    private BottomSheetDialog dialog;

    private ViewDialog loading;

    InviteViewModel inviteViewModel;
    InviteAdapter adapter;

    boolean isLoading, hasNext = true;
    int currentPage = 1;
    String query = "";

    private List<EvalyUserModel> evalyUserList;
    private List<String> rosterList;

    AppController mChatApp = AppController.getInstance();

    XMPPHandler xmppHandler;
    XMPPEventReceiver xmppEventReceiver;

    public XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {

        //On User Presence Changed
        public void onLoggedIn() {
            xmppHandler = AppController.getmService().xmpp;
            rosterList = xmppHandler.rosterList;
        }

        public void onConnected() {
            xmppHandler = AppController.getmService().xmpp;
            rosterList = xmppHandler.rosterList;
        }

        public void onLoginFailed(String msg) {
            if (msg.contains("already logged in")) {

            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);

        inviteViewModel = ViewModelProviders.of(this).get(InviteViewModel.class);

        loading = new ViewDialog(this);
        evalyUserList = new ArrayList<>();
        adapter = new InviteAdapter(this, evalyUserList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvInvite.setLayoutManager(layoutManager);
        rvInvite.setAdapter(adapter);

        rosterList = new ArrayList<>();

        xmppEventReceiver = mChatApp.getEventReceiver();
        xmppHandler = AppController.getmService().xmpp;

        if (xmppHandler != null) {
            if (!xmppHandler.isLoggedin() || !xmppHandler.isConnected()) {
                startXmppService();
            }
        } else {
            startXmppService();
        }

        if (xmppHandler != null && xmppHandler.isLoggedin()) {
            rosterList = xmppHandler.rosterList;
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Logger.d(charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Logger.d(charSequence);
                currentPage = 1;
                query = charSequence.toString();
                if (query.length() > 2) {
                    evalyUserList.clear();
                    inviteViewModel.findUsers(query, currentPage, InviteActivity.this);
                }else {
                    evalyUserList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        nestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
//                    Logger.d("BOTTOM SCROLL");
//                    Logger.d(isLoading + "    " + hasNext);
                    try {
                        if (!isLoading && hasNext) {
                            isLoading = true;
                            progressBar.setVisibility(View.VISIBLE);
                            currentPage = currentPage + 1;
                            Logger.d(currentPage);
                            loadMore(currentPage);
                        }

                    } catch (Exception e) {
                        Logger.d("load more product", e.toString());
                    }
                }
            }
        });

        inviteViewModel.userList.observe(this, new Observer<List<EvalyUserModel>>() {
            @Override
            public void onChanged(@Nullable List<EvalyUserModel> list) {
                evalyUserList.addAll(list);
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        });

        inviteViewModel.hasNext.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                hasNext = aBoolean;
            }
        });

        inviteViewModel.isFailed.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (!aBoolean) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            }
        });
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

    @OnClick(R.id.back)
    void back() {
        onBackPressed();
    }

    @OnClick(R.id.ivInvite)
    void inviteUser() {
        dialog = new BottomSheetDialog(InviteActivity.this, R.style.BottomSheetDialogTheme);
        dialog.setContentView(R.layout.invite_view);

        etContactName = dialog.findViewById(R.id.etContactName);
        etPhoneNumber = dialog.findViewById(R.id.etContactNumber);
        ivAddNumber = dialog.findViewById(R.id.ivAddNumber);
        ivClose = dialog.findViewById(R.id.ivClose);
        btnInvite = dialog.findViewById(R.id.btnInvite);

        ivAddNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(InviteActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(InviteActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_CONTACTS);
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
                    invite();
                }
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        View bottomSheetInternal = dialog.findViewById(R.id.design_bottom_sheet);
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
                    dialog.dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        dialog.setCancelable(false);

        dialog.show();
    }

    private void invite() {
        if (etPhoneNumber.getText().toString().matches("^(01)[3-9][0-9]{8}$")) {

            String roasterModel = getContactFromRoster(etPhoneNumber.getText().toString());
            Logger.d(new Gson().toJson(roasterModel));
            if (!CredentialManager.getUserName().equalsIgnoreCase(etPhoneNumber.getText().toString())) {
                if (roasterModel != null) {
                    loading.hideDialog();
                    dialog.dismiss();

                    RosterTable table = new RosterTable();
                    table.id = roasterModel;
                    table.rosterName = etContactName.getText().toString();
                    table.name = "";
                    table.status = 0;
                    table.unreadCount = 0;
                    table.nick_name = "";
                    table.imageUrl = "";

                    startActivity(new Intent(InviteActivity.this, ChatDetailsActivity.class).putExtra("roster", (Serializable) roasterModel));
                } else {
                    if (xmppHandler.isLoggedin() && xmppHandler.isConnected()) {
                        loading.showDialog();
                        dialog.dismiss();

                        EvalyUserModel model = new EvalyUserModel();
                        model.setFirst_name(etContactName.getText().toString());
                        model.setLast_name("");
                        model.setUsername(etPhoneNumber.getText().toString());
                        addRoster(model);

//                        xmppHandler.sendRequestTo(etPhoneNumber.getText().toString(), etContactName.getText().toString());
//                        Toast.makeText(getApplicationContext(), "Invitation sent", Toast.LENGTH_LONG).show();

                        sendCustomMessage(etPhoneNumber.getText().toString(), etContactName.getText().toString());


                    } else {
                        startXmppService();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "You can't invite yourself!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Invalid phone number!", Toast.LENGTH_LONG).show();
        }
    }

    private void sendCustomMessage(String id, String name) {

        EntityBareJid jid = null;
        try {
            jid = JidCreate.entityBareFrom(id + "@"
                    + Constants.XMPP_HOST);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        VCard vCard = xmppHandler.getUserDetails(jid);

        Logger.d(new Gson().toJson(vCard));

        if (vCard == null || vCard.getFirstName() == null) {
            HashMap<String, String> data2 = new HashMap<>();
            data2.put("phone_number", id);
            data2.put("text", "You are invited to chat with " + CredentialManager.getUserData().getFirst_name() + " at Evaly. Please download Evaly app from here, \n https://play.google.com/store/apps/details?id=bd.com.evaly.merchant and start conversation");

            EntityBareJid finalJid = jid;
            AuthApiHelper.sendCustomMessage(data2, new DataFetchingListener<Response<JsonObject>>() {
                @Override
                public void onDataFetched(Response<JsonObject> response) {
                    loading.hideDialog();
                    if (response.code() == 200 || response.code() == 201) {
                        Toast.makeText(getApplicationContext(), "Invitation sent!", Toast.LENGTH_LONG).show();
//                            xmppHandler.sendRequestTo(etPhoneNumber.getText().toString(), etPhoneNumber.getText().toString());
                        Logger.d("[[[[[[[[[[[");
                        ChatItem chatItem = new ChatItem("Let's start a conversation", CredentialManager.getUserData().getFirst_name() + " " + CredentialManager.getUserData().getLast_name(), xmppHandler.mVcard.getField("URL"), xmppHandler.mVcard.getNickName(), System.currentTimeMillis(), xmppHandler.mVcard.getFrom().asBareJid().toString(), finalJid.asUnescapedString(), Constants.TYPE_TEXT, true, "");
                        chatItem.setReceiver_name(name);
                        chatItem.setInvitation(true);
                        try {
                            xmppHandler.sendMessage(chatItem);
                        } catch (SmackException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), "Invitation sent", Toast.LENGTH_LONG).show();


                        Logger.d(new Gson().toJson(xmppHandler.mVcard));

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    } else if (response.code() == 401) {
                        AuthApiHelper.refreshToken(InviteActivity.this, new DataFetchingListener<Response<JsonObject>>() {
                            @Override
                            public void onDataFetched(Response<JsonObject> response) {
                                sendCustomMessage(id, name);
                            }

                            @Override
                            public void onFailed(int status) {

                            }
                        });
                    } else {
                        loading.hideDialog();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailed(int status) {
                    loading.hideDialog();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();

                }
            });
        } else {
            loading.hideDialog();

            RosterTable table = new RosterTable();
            table.id = jid.asUnescapedString();
            table.rosterName = vCard.getFirstName();
            table.name = "";
            table.status = 0;
            table.unreadCount = 0;
            table.nick_name = "";
            table.imageUrl = "";
            ChatItem chatItem = new ChatItem("Let's start a conversation", CredentialManager.getUserData().getFirst_name() + " " + CredentialManager.getUserData().getLast_name(), CredentialManager.getUserData().getImage_sm(), CredentialManager.getUserData().getFirst_name(), System.currentTimeMillis(), CredentialManager.getUserName() + "@" + Constants.XMPP_HOST, jid.asUnescapedString(), Constants.TYPE_TEXT, true, "");
            chatItem.setInvitation(true);
            try {
                xmppHandler.sendMessage(chatItem);
            } catch (SmackException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(InviteActivity.this, ChatDetailsActivity.class).putExtra("roster", (Serializable) table));

        }

    }

    private String getContactFromRoster(String number) {
        String roasterModel = null;
        for (String model : rosterList) {
            if (model.contains(number)) {
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

    @Override
    protected void onResume() {
        super.onResume();
        xmppEventReceiver.setListener(xmppCustomEventListener);

//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                AppController.allDataLoaded = true;
//                rosterList = AppController.database.taskDao().getAllRosterWithoutObserve();
//            }
//        });
    }

    private void loadMore(int currentPage) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRecyclerViewItemClicked(Object object) {
        EvalyUserModel model = (EvalyUserModel) object;

        if (model.getUsername().contains(CredentialManager.getUserName())){
            Toast.makeText(getApplicationContext(), "You can't invite yourself!", Toast.LENGTH_LONG).show();
            return;
        }

        if (xmppHandler.isLoggedin()) {
            xmppHandler.getAllRoaster();
            if (xmppHandler.rosterList.contains(model.getUsername()+"@"+Constants.XMPP_HOST)){
                Toast.makeText(getApplicationContext(), "Already Invited!", Toast.LENGTH_LONG ).show();
                return;
            }
            addRoster(model);
        }
    }

    private void addRoster(EvalyUserModel model) {
        String roasterModel = getContactFromRoster(model.getUsername());
        if (roasterModel != null) {
            RosterTable table = new RosterTable();
            table.id = roasterModel;
            table.rosterName = model.getFirst_name() + " " + model.getLast_name();
            table.name = "";
            table.status = 0;
            table.unreadCount = 0;
            table.nick_name = "";
            table.imageUrl = "";
            startActivity(new Intent(InviteActivity.this, ChatDetailsActivity.class).putExtra("roster", (Serializable) table));
            return;
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("localuser", CredentialManager.getUserName());
        data.put("localserver", Constants.XMPP_HOST);
        data.put("user", model.getUsername());
        data.put("server", Constants.XMPP_HOST);
        data.put("nick", model.getFirst_name() + " " + model.getLast_name());
        data.put("subs", "both");
        data.put("group", "evaly");

        AuthApiHelper.addRoster(data, new DataFetchingListener<Response<JsonPrimitive>>() {
            @Override
            public void onDataFetched(Response<JsonPrimitive> response) {


                if (response.code() == 200 || response.code() == 201) {
                    sendCustomMessage(model.getUsername(), model.getFirst_name() + " " + model.getLast_name());
                } else if (response.code() == 401) {
                    AuthApiHelper.refreshToken(InviteActivity.this, new DataFetchingListener<Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(Response<JsonObject> response) {
                            addRoster(model);
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });
                }
            }

            @Override
            public void onFailed(int status) {

            }
        });
    }

}
