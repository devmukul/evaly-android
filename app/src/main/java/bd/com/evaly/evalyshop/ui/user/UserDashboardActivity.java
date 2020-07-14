package bd.com.evaly.evalyshop.ui.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.ui.auth.ChangePasswordActivity;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.balance.BalanceFragment;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.chat.ChatListActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.notification.NotificationActivity;
import bd.com.evaly.evalyshop.ui.order.PayViaBkashActivity;
import bd.com.evaly.evalyshop.ui.order.orderList.OrderListActivity;
import bd.com.evaly.evalyshop.ui.transaction.TransactionHistory;
import bd.com.evaly.evalyshop.ui.user.editProfile.EditProfileActivity;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.Token;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.util.xmpp.XMPPService;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserDashboardActivity extends BaseActivity {

    Context context;
    TextView name, balance, address;
    Map<String, String> map;
    String from = "";
    ViewDialog alert;
    boolean isFromSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        ButterKnife.bind(this);
        context = this;

        getSupportActionBar().setTitle(getString(R.string.dashboard));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4f);

        if (CredentialManager.getToken().equals("")) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            from = extras.getString("from");
        }

        name = findViewById(R.id.name);
        balance = findViewById(R.id.balance);
        address = findViewById(R.id.address);
        alert = new ViewDialog(UserDashboardActivity.this);

        name.setText(String.format("%s %s", CredentialManager.getUserData().getFirst_name(), CredentialManager.getUserData().getLast_name()));

        if (CredentialManager.getUserData().getAddresses().equals("null"))
            address.setText("Add an address");
        else
            address.setText(CredentialManager.getUserData().getAddresses());


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

        LinearLayout changeLanguage = findViewById(R.id.changeLanguage);
        changeLanguage.setOnClickListener(view -> {

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            CharSequence[] items = new CharSequence[]{"English", "বাংলা"};

            int selectedPos = 0;
            if (CredentialManager.getLanguage().equalsIgnoreCase("bn"))
                selectedPos = 1;

            adb.setSingleChoiceItems(items, selectedPos, (d, n) -> {
                Locale myLocale;
                if (n == 1) {
                    CredentialManager.setLanguage("BN");
                    myLocale = new Locale("BN");
                } else {
                    CredentialManager.setLanguage("EN");
                    myLocale = new Locale("EN");
                }

                Locale.setDefault(myLocale);
                android.content.res.Configuration config = new android.content.res.Configuration();
                config.locale = myLocale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());

                startActivity(new Intent(UserDashboardActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();

            });
            adb.setNegativeButton(R.string.cancel, null);
            adb.setTitle(R.string.select_language);
            adb.show();
        });


        balance.setOnClickListener(view -> {
            BalanceFragment balanceFragment = BalanceFragment.newInstance();
            balanceFragment.show(getSupportFragmentManager(), "balance");
        });

    }

    @OnClick(R.id.llMessage)
    void gotoMessage() {
        if (!CredentialManager.getToken().equals("")) {
            startActivity(new Intent(UserDashboardActivity.this, ChatListActivity.class));
        } else {
            Toast.makeText(getApplicationContext(), "Please login to see messages", Toast.LENGTH_LONG).show();
        }
//        Intent launchIntent = new Intent("bd.com.evaly.econnect.OPEN_MAINACTIVITY");
//        try {
//            if (launchIntent != null) {
//                launchIntent.putExtra("to", "OPEN_CHAT_LIST");
//                launchIntent.putExtra("user", CredentialManager.getUserName());
//                launchIntent.putExtra("password", CredentialManager.getPassword());
//                launchIntent.putExtra("userInfo", new Gson().toJson(CredentialManager.getUserData()));
////                launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(launchIntent);
////                finish();
//            }
//        }catch (ActivityNotFoundException e){
//            try {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "bd.com.evaly.econnect")));
//            } catch (android.content.ActivityNotFoundException anfe) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "bd.com.evaly.econnect")));
//            }
//        }


    }


    @Override
    public void onResume() {
        super.onResume();

        Balance.update(this, false);

        ImageView profilePicNav = findViewById(R.id.picture);

        if (!CredentialManager.getUserData().getImage_sm().equals("null")) {

            Glide.with(this)
                    .asBitmap()
                    .load(CredentialManager.getUserData().getImage_sm())
                    .skipMemoryCache(true)
                    .fitCenter()
                    .placeholder(R.drawable.user_image)
                    .centerCrop()
                    .apply(new RequestOptions().override(200, 200))
                    .into(profilePicNav);
        }

        name.setText(CredentialManager.getUserData().getFullName());
        address.setText(CredentialManager.getUserData().getAddresses());

    }

    @Override
    public void onBackPressed() {
        if (from != null)
            if (from.equals("signin")) {
                Intent intent = new Intent(UserDashboardActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                disconnectXmpp();
                super.onBackPressed();
            }
        else
            super.onBackPressed();
    }

    private void disconnectXmpp() {
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
