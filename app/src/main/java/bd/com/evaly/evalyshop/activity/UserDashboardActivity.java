package bd.com.evaly.evalyshop.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.menu.MenuBuilder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.chat.ChatListActivity;
import bd.com.evaly.evalyshop.ui.order.PayViaBkashActivity;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.xmpp.SignupModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.Token;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.util.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.util.xmpp.XMPPService;
import bd.com.evaly.evalyshop.util.xmpp.XmppCustomEventListener;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;

public class UserDashboardActivity extends BaseActivity {

    Context context;
    TextView name, balance, address;
    UserDetails userDetails;
    Map<String, String> map;
    String from = "";
    ViewDialog alert;
    boolean isFromSignup;

    private AppController mChatApp = AppController.getInstance();
    private XMPPHandler xmppHandler;
    XMPPEventReceiver xmppEventReceiver;

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

            if (xmppHandler != null) {

                VCard vCard = xmppHandler.mVcard;
                if (CredentialManager.getUserData() != null)
                    if (vCard == null) {
                        xmppHandler.updateUserInfo(CredentialManager.getUserData());
                    } else if (vCard.getLastName() == null || vCard.getFirstName() == null) {
                        xmppHandler.updateUserInfo(CredentialManager.getUserData());
                    }
            }

        }

        public void onLoginFailed(String msg) {
//            Logger.d(msg);
            alert.hideDialog();
            if (!msg.contains("already logged in")) {
                if (CredentialManager.getPassword() != null && !CredentialManager.getPassword().equals("")) {
//                    alert.showDialog();
                    xmppHandler.Signup(new SignupModel(CredentialManager.getUserName(), CredentialManager.getPassword(), CredentialManager.getPassword()));
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

        getSupportActionBar().setTitle("Dashboard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4f);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            from = extras.getString("from");
        }

        if (!CredentialManager.getToken().equals("") && !CredentialManager.isUserRegistered()) {
            AsyncTask.execute(() -> {
                Logger.e("==========");
                startXmppService();
            });
        }

        xmppEventReceiver = mChatApp.getEventReceiver();

        name = findViewById(R.id.name);
        balance = findViewById(R.id.balance);
        address = findViewById(R.id.address);
        userDetails = new UserDetails(this);
        alert = new ViewDialog(UserDashboardActivity.this);
        // getAddress();

        name.setText(String.format("%s %s", userDetails.getFirstName(), userDetails.getLastName()));
        balance.setText(String.format("৳ %s", userDetails.getBalance()));

        if (userDetails.getJsonAddress().equals("null"))
            address.setText("Add an address");
        else
            address.setText(userDetails.getJsonAddress());


        LinearLayout orders = findViewById(R.id.order);
        orders.setOnClickListener(view -> {
            Intent intent = new Intent(context, OrderListActivity.class);
            startActivity(intent);
        });


        LinearLayout notification = findViewById(R.id.notification);
        notification.setOnClickListener(view -> {

            Intent intent = new Intent(context, NotificationActivity.class);
            startActivity(intent);

        });


        LinearLayout addBalance = findViewById(R.id.addBalance);
        addBalance.setOnClickListener(view -> {

            Intent intent = new Intent(context, PayViaBkashActivity.class);
            startActivity(intent);

        });


        LinearLayout transactionHistory = findViewById(R.id.transaction_history);
        transactionHistory.setOnClickListener(view -> {

            Intent intent = new Intent(context, TransactionHistory.class);
            startActivity(intent);

        });


        LinearLayout editProfile = findViewById(R.id.editProfile);
        editProfile.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditProfileActivity.class);
            startActivity(intent);
        });


        LinearLayout editAddress = findViewById(R.id.addressClick);
        editAddress.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditProfileActivity.class);
            startActivity(intent);
        });


        LinearLayout changePassword = findViewById(R.id.changePassword);
        changePassword.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChangePasswordActivity.class);
            startActivity(intent);
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
        xmppEventReceiver.setListener(xmppCustomEventListener);

        Balance.update(this, balance);

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

        name.setText(String.format("%s %s", userDetails.getFirstName(), userDetails.getLastName()));
        address.setText(userDetails.getJsonAddress());


    }

    @Override
    public void onBackPressed() {
        if (from.equals("signin")) {
            Intent intent = new Intent(UserDashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            disconnectXmpp();
            super.onBackPressed();
        }
    }

    private void disconnectXmpp(){
        XMPPHandler.disconnect();
        stopService(new Intent(UserDashboardActivity.this, XMPPService.class));
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
