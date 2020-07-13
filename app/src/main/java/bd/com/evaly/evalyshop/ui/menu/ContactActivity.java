package bd.com.evaly.evalyshop.ui.menu;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.chat.ChatDetailsActivity;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.util.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.util.xmpp.XMPPService;
import bd.com.evaly.evalyshop.util.xmpp.XmppCustomEventListener;
import retrofit2.Response;

import static bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity.setWindowFlag;

public class ContactActivity extends BaseActivity {

    TextView call1, call2, call3;

    ViewDialog dialog;
    private List<String> rosterList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);



        dialog = new ViewDialog(this);
        rosterList = new ArrayList<>();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //make fully Android Transparent Status bar
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        ImageView mapImage = findViewById(R.id.mapImage);

        Glide.with(this).load(R.drawable.map_high_res).into(mapImage);

        LinearLayout callBox = findViewById(R.id.callBox);
        callBox.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:09638111666"));
            startActivity(intent);
        });


        LinearLayout emailBox = findViewById(R.id.emailBox);
        emailBox.setOnClickListener(v -> {
            if (CredentialManager.getToken().equals("")) {
                Toast.makeText(getApplicationContext(), "Please login to send message", Toast.LENGTH_LONG).show();
            } else
               setUpXmpp();
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

        View.OnClickListener openMaps = v -> {
            Uri gmmIntentUri = Uri.parse("geo:23.754241,90.3726478?q=" + Uri.encode("Evaly.com.bd"));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {

                Utils.CustomTab("https://g.page/evaly-com-bd?share", ContactActivity.this);
            }
        };

        LinearLayout mapsBox = findViewById(R.id.mapsBox);
        mapsBox.setOnClickListener(openMaps);

        View mapsView = findViewById(R.id.mapOpener);
        mapsView.setOnClickListener(openMaps);

        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(v -> finish());

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpXmpp() {

            Intent launchIntent = new Intent("bd.com.evaly.econnect.OPEN_MAINACTIVITY");
            try {
                RosterTable roasterModel = new RosterTable();
                roasterModel.id = Constants.EVALY_NUMBER + "@" + Constants.XMPP_HOST;
                roasterModel.rosterName = "Evaly";
                roasterModel.imageUrl = Constants.EVALY_LOGO;

                launchIntent.putExtra("to", "OPEN_CHAT_DETAILS");
                launchIntent.putExtra("from", "contact");
                launchIntent.putExtra("user", CredentialManager.getUserName());
                launchIntent.putExtra("password", CredentialManager.getPassword());
                launchIntent.putExtra("userInfo", new Gson().toJson(CredentialManager.getUserData()));
                launchIntent.putExtra("roster", new Gson().toJson(roasterModel));

//                launchIntent.putExtra("to", "OPEN_CHAT_LIST");
//                launchIntent.putExtra("user", CredentialManager.getUserName());
//                launchIntent.putExtra("password", CredentialManager.getPassword());
//                launchIntent.putExtra("userInfo", new Gson().toJson(CredentialManager.getUserData()));
//                launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(launchIntent);

            } catch (ActivityNotFoundException e) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "bd.com.evaly.econnect")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "bd.com.evaly.econnect")));
                } catch (Exception e2) {
                    ToastUtils.show("Please install eConnect app for live chat");
                }

        }
    }

}
