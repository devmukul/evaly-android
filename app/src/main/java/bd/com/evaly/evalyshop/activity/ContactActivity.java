package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;
import com.thefinestartist.finestwebview.FinestWebView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.chat.ChatDetailsActivity;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;
import retrofit2.Response;

import static bd.com.evaly.evalyshop.activity.ViewProductActivity.setWindowFlag;

public class ContactActivity extends BaseActivity {

    TextView call1, call2, call3;

    ViewDialog dialog;
    private List<String> rosterList;

    AppController mChatApp = AppController.getInstance();

    XMPPHandler xmppHandler;
    XMPPEventReceiver xmppEventReceiver;

    public XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {

        public void onConnected() {
            xmppHandler = AppController.getmService().xmpp;
        }

        public void onLoggedIn() {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    xmppHandler = AppController.getmService().xmpp;
                    rosterList = xmppHandler.rosterList;
//                            Logger.d(new Gson().toJson(AppController.database.taskDao().getAllRoster()));
                }
            });
        }
        //On User Presence Changed

    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        //getSupportActionBar().setElevation(0);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Contact us");

        dialog = new ViewDialog(this);
        rosterList = new ArrayList<>();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //make fully Android Transparent Status bar
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        xmppEventReceiver = mChatApp.getEventReceiver();

        ImageView mapImage = findViewById(R.id.mapImage);

        Glide.with(this).load(R.drawable.map_high_res).into(mapImage);

        LinearLayout callBox = findViewById(R.id.callBox);
        callBox.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:09638111666"));
            startActivity(intent);
        });

        if (!CredentialManager.getToken().equals("")){
            if (AppController.getmService() != null && AppController.getmService().xmpp != null && AppController.getmService().xmpp.isConnected()) {
                xmppHandler = AppController.getmService().xmpp;
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        rosterList = xmppHandler.rosterList;
                    }
                });
            } else {
                startXmppService();
            }
        }

        LinearLayout emailBox = findViewById(R.id.emailBox);
        emailBox.setOnClickListener(v -> {

//            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//                    "mailto","support@evaly.com.bd", null));
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
//            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
//            startActivity(emailIntent);

            if (CredentialManager.getToken().equals("")) {
                Toast.makeText(getApplicationContext(), "Please login to send message", Toast.LENGTH_LONG).show();
            } else {

                RosterTable roasterModel = new RosterTable();
                roasterModel.id = Constants.EVALY_NUMBER + "@" + Constants.XMPP_HOST;
                roasterModel.rosterName = "Evaly";
                roasterModel.imageUrl = Constants.EVALY_LOGO;
                startActivity(new Intent(ContactActivity.this, ChatDetailsActivity.class)
                        .putExtra("roster", (Serializable) roasterModel));

            }
        });


        LinearLayout facebookBox = findViewById(R.id.facebookBox);
        facebookBox.setOnClickListener(v -> {

            try {
                getPackageManager().getPackageInfo("com.facebook.katana", 0);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/1567333923329329")));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/evaly.com.bd/")));
            }

        });


        View.OnClickListener openMaps = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:23.754241,90.3726478?q=" + Uri.encode("Evaly.com.bd"));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {

                    new FinestWebView.Builder(ContactActivity.this)
                            .titleDefault("Google Maps")
                            .webViewBuiltInZoomControls(false)
                            .webViewDisplayZoomControls(false)
                            .dividerHeight(0)
                            .gradientDivider(false)
                            .setCustomAnimations(R.anim.activity_open_enter, R.anim.activity_open_exit, R.anim.activity_close_enter, R.anim.activity_close_exit)
                            .show("https://g.page/evaly-com-bd?share");

                }
            }
        };


        LinearLayout mapsBox = findViewById(R.id.mapsBox);
        mapsBox.setOnClickListener(openMaps);


        View mapsView = findViewById(R.id.mapOpener);
        mapsView.setOnClickListener(openMaps);


        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(v -> finish());


    }

    private void addRosterByOther() {
        HashMap<String, String> data = new HashMap<>();
        data.put("localuser", Constants.EVALY_NUMBER);
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

    private void startXmppService() {
        if (!XMPPService.isServiceRunning) {
            Intent intent = new Intent(this, XMPPService.class);
            mChatApp.UnbindService();
            mChatApp.BindService(intent);
            Logger.d("++++++++++");
        } else {
            Logger.d("---------");
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

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (xmppHandler != null){
                    rosterList = xmppHandler.rosterList;
                }
            }
        });
    }

    //    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
