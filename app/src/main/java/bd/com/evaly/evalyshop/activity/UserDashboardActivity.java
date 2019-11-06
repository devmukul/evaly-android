package bd.com.evaly.evalyshop.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONObject;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.chat.ChatListActivity;
import bd.com.evaly.evalyshop.activity.orderDetails.PayViaBkashActivity;
import bd.com.evaly.evalyshop.adapter.AddressAdapter;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.SignupModel;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.Token;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;

public class UserDashboardActivity extends BaseActivity {

    Context context;
    TextView name, balance, address;
    UserDetails userDetails;
    ImageView addAddress;
    EditText addressET;
    RecyclerView addressList;
    ArrayList<String> addresses;
    ArrayList<Integer> addressID;
    AddressAdapter addressAdapter;
    Map<String, String> map;
    String from = "";
    ViewDialog alert;

    String userAgent;

    boolean isFromSignup;

    private AppController mChatApp = AppController.getInstance();
    private XMPPHandler xmppHandler;
//    private SessionManager sessionManager;

    private XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {


        @Override
        public void onConnected() {
            xmppHandler = AppController.getmService().xmpp;
            xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
            xmppHandler.login();
        }

        //Event Listeners
        public void onLoggedIn() {

            if (xmppHandler == null){
                xmppHandler = AppController.getmService().xmpp;
            }
            Logger.d("LOGIN =========");
            Logger.d(xmppHandler.isConnected());
            VCard vCard = xmppHandler.mVcard;
//            Logger.d(vCard.getFirstName());
            if (vCard == null) {
                Logger.d("========");
                xmppHandler.updateUserInfo(CredentialManager.getUserData());
            }else if (vCard.getLastName() == null){
                Logger.d("========");
                xmppHandler.updateUserInfo(CredentialManager.getUserData());
            }

        }

        public void onLoginFailed(String msg) {
            Logger.d(msg);
            alert.hideDialog();
            if (!msg.contains("already logged in")) {
                if (CredentialManager.getPassword() != null && !CredentialManager.getPassword().equals("")) {
//                    alert.showDialog();
                    xmppHandler.Signup(new SignupModel(CredentialManager.getUserName(), CredentialManager.getPassword(), CredentialManager.getPassword()), CredentialManager.getUserData().getFirst_name());
                }
            }
//            xmppHandler.disconnect();
//            Toast.makeText(getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
        }

        //        //Event Listeners
        public void onSignupSuccess() {
            Logger.d("Signup success");

            xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
            xmppHandler.login();

        }
//
        public void onSignupFailed(String error){
            Logger.d(error);
            if (error.contains("User already exist")) {
                alert.showDialog();
                HashMap<String, String> data = new HashMap<>();
                data.put("user", CredentialManager.getUserName());
                data.put("host", Constants.XMPP_HOST);
                data.put("newpass", CredentialManager.getPassword());
                Logger.d("===============");
                AuthApiHelper.changeXmppPassword(data, new DataFetchingListener<Response<JsonPrimitive>>() {
                    @Override
                    public void onDataFetched(Response<JsonPrimitive> response) {
                        alert.hideDialog();
//                        Logger.d(new Gson().toJson(response));
                        if (response.code() == 200 || response.code() == 201) {
                            onPasswordChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailed(int status) {
                        alert.hideDialog();
                        Logger.d("======-=-=-=-=-=-=");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();

                    }
                });

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        ButterKnife.bind(this);
        context = this;

        // getSupportActionBar().setElevation(0);


        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }


        getSupportActionBar().setTitle("Dashboard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4f);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            from = extras.getString("from");
        }

        if (!CredentialManager.getToken().equals("")) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    startXmppService();
                }
            });
        }

        name = findViewById(R.id.name);
        balance = findViewById(R.id.balance);
        address = findViewById(R.id.address);
        userDetails = new UserDetails(this);
        alert = new ViewDialog(UserDashboardActivity.this);
        // getAddress();

        name.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
        balance.setText("৳ " + userDetails.getBalance());

        if (userDetails.getJsonAddress().equals("null"))
            address.setText("Add an address");
        else
            address.setText(userDetails.getJsonAddress());


        LinearLayout orders = findViewById(R.id.order);
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderListActivity.class);
                startActivity(intent);
            }
        });


        LinearLayout notification = findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);


            }
        });


        LinearLayout addBalance = findViewById(R.id.addBalance);
        addBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, PayViaBkashActivity.class);
                startActivity(intent);


            }
        });


        LinearLayout transactionHistory = findViewById(R.id.transaction_history);
        transactionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, TransactionHistory.class);
                startActivity(intent);


            }
        });


        LinearLayout editProfile = findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditProfileActivity.class);
                startActivity(intent);
            }
        });


        LinearLayout editAddress = findViewById(R.id.addressClick);
        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditProfileActivity.class);
                startActivity(intent);
            }
        });


        LinearLayout changePassword = findViewById(R.id.changePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    private void startXmppService() {

        //Start XMPP Service (if not running already)
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

    @OnClick(R.id.llMessage)
    void gotoMessage() {
        if (!CredentialManager.getToken().equals("") && !userDetails.getToken().equals("")) {
            startActivity(new Intent(UserDashboardActivity.this, ChatListActivity.class));
        } else {
            Toast.makeText(getApplicationContext(), "Please login to see messages", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onResume() {

        super.onResume();

        mChatApp.getEventReceiver().setListener(xmppCustomEventListener);

        Balance.update(this, balance);
        Token.update(this, false);

        ImageView profilePicNav = findViewById(R.id.picture);


        if (!userDetails.getProfilePictureSM().equals("null")) {
            Glide.with(this)
                    .asBitmap()
                    .load(userDetails.getProfilePictureSM())
                    .skipMemoryCache(true)
                    .fitCenter()
                    .centerCrop()
                    .apply(new RequestOptions().override(200, 200))
                    .into(profilePicNav);
        }


    }

    @Override
    public void onBackPressed() {
        if (from.equals("signin")) {
            Intent intent = new Intent(UserDashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.logout:

                Toast.makeText(context, "Logging out...", Toast.LENGTH_SHORT).show();
                Token.logout(UserDashboardActivity.this, new DataFetchingListener<JSONObject>() {
                        @Override
                        public void onDataFetched(JSONObject response) {

                            Toast.makeText(context, "Successfully logged out...", Toast.LENGTH_SHORT).show();

                            AppController.logout(UserDashboardActivity.this);


                        }

                        @Override
                        public void onFailed(int status) {

                        }
                });


                return true;
        }
        return super.onOptionsItemSelected(item);

    }


}
