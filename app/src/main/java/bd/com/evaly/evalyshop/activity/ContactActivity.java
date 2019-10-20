package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.thefinestartist.finestwebview.FinestWebView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
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
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
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
    private List<RosterTable> rosterList;

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
//
                    rosterList = AppController.database.taskDao().getAllRosterWithoutObserve();
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

        if (AppController.getmService().xmpp != null && AppController.getmService().xmpp.isConnected()) {
            xmppHandler = AppController.getmService().xmpp;
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
//
                    rosterList = AppController.database.taskDao().getAllRosterWithoutObserve();
//                            Logger.d(new Gson().toJson(AppController.database.taskDao().getAllRoster()));
                }
            });
        } else {
            startXmppService();
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
                RosterTable roasterModel = getContactFromRoster("09638111666");
                Logger.d(new Gson().toJson(roasterModel));
                if (roasterModel != null) {
                    dialog.hideDialog();
//                        VCard vCard = null;
//                        try {
//                            vCard = xmppHandler.getUserDetails(JidCreate.bareFrom(roasterModel.id).asEntityBareJidIfPossible());
//                        } catch (XmppStringprepException e) {
//                            e.printStackTrace();
//                        }
//                        VCardObject vCardObject = new VCardObject(vCard.getFirstName() + " " + vCard.getLastName(), vCard.getFrom(), vCard.getField("URL"), 0);
                    startActivity(new Intent(ContactActivity.this, ChatDetailsActivity.class).putExtra("roster", (Serializable) roasterModel));
                } else {
                    dialog.showDialog();
                    if (xmppHandler != null && xmppHandler.isLoggedin()){
                        HashMap<String, String> data = new HashMap<>();
                        data.put("localuser", CredentialManager.getUserName());
                        data.put("localserver", Constants.XMPP_HOST);
                        data.put("user", "09638111666");
                        data.put("server", Constants.XMPP_HOST);
                        data.put("nick", "Evaly");
                        data.put("subs", "both");
                        data.put("group", "evaly");

                        addRosterByOther();

                        AuthApiHelper.addRoster(data, new DataFetchingListener<Response<JsonPrimitive>>() {
                            @Override
                            public void onDataFetched(Response<JsonPrimitive> response) {

                                if (response.code() == 200 || response.code() == 201) {
                                    try {
                                        EntityBareJid jid = JidCreate.entityBareFrom("09638111666" + "@"
                                                + Constants.XMPP_HOST);
                                        VCard vCard = xmppHandler.getUserDetails(jid);
                                        ChatItem chatItem = new ChatItem("Let's start a conversation", CredentialManager.getUserData().getFirst_name() + " " + CredentialManager.getUserData().getLast_name(), xmppHandler.mVcard.getField("URL"), xmppHandler.mVcard.getNickName(), System.currentTimeMillis(), xmppHandler.mVcard.getFrom().asBareJid().toString(), jid.asUnescapedString(), Constants.TYPE_TEXT, true, "");

                                        try {
                                            xmppHandler.sendMessage(chatItem);
                                            Logger.d("SENT ");
                                        } catch (SmackException e) {
                                            Logger.d("SENT FAILED");
                                            e.printStackTrace();
                                        }

                                        RosterTable rosterTable = new RosterTable();
                                        rosterTable.name = "Evaly";
                                        rosterTable.id = jid.asUnescapedString();
                                        rosterTable.imageUrl = vCard.getField("URL");
                                        rosterTable.status = 0;
                                        rosterTable.lastMessage = new Gson().toJson(chatItem);
                                        rosterTable.nick_name = vCard.getNickName();
                                        rosterTable.time = 0;
                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                AppController.database.taskDao().addRoster(rosterTable);
                                            }
                                        });
                                        dialog.hideDialog();
                                        startActivity(new Intent(ContactActivity.this, ChatDetailsActivity.class).putExtra("roster", (Serializable) rosterTable));


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
                    }else {

                    }
                }
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

    private RosterTable getContactFromRoster(String number) {
        RosterTable roasterModel = null;
        for (RosterTable model : rosterList) {
            if (model.id.contains(number)) {
                roasterModel = model;
            }
        }
        return roasterModel;
    }

    private void addRosterByOther() {
        HashMap<String, String> data = new HashMap<>();
        data.put("localuser", "09638111666");
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
//
                rosterList = AppController.database.taskDao().getAllRosterWithoutObserve();
//                            Logger.d(new Gson().toJson(AppController.database.taskDao().getAllRoster()));
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
