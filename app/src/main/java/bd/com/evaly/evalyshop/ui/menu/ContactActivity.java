package bd.com.evaly.evalyshop.ui.menu;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.ActivityContactBinding;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

import static bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity.setWindowFlag;

@AndroidEntryPoint
public class ContactActivity extends BaseActivity {

    @Inject
    PreferenceRepository preferenceRepository;

    private ActivityContactBinding binding;
    private ViewDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact);

        dialog = new ViewDialog(this);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);


        Glide.with(this).load(R.drawable.map_high_res).into(binding.mapImage);

        binding.callBox.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:09638111666"));
            startActivity(intent);
        });

        binding.emailBox.setOnClickListener(v -> {
            if (preferenceRepository.getToken().equals("")) {
                Toast.makeText(getApplicationContext(), "Please login to send message", Toast.LENGTH_LONG).show();
            } else
                openEconnect();
        });

        binding.facebookBox.setOnClickListener(v -> {
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


        binding.mapsBox.setOnClickListener(openMaps);
        binding.mapOpener.setOnClickListener(openMaps);
        binding.backArrow.setOnClickListener(v -> finish());

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void openEconnect() {
        Intent launchIntent = new Intent("bd.com.evaly.econnect.OPEN_MAINACTIVITY");
        try {
            RosterTable roasterModel = new RosterTable();
            roasterModel.id = Constants.EVALY_NUMBER + "@" + Constants.XMPP_HOST;
            roasterModel.rosterName = "Evaly";
            roasterModel.imageUrl = Constants.EVALY_LOGO;
            launchIntent.putExtra("to", "OPEN_CHAT_DETAILS");
            launchIntent.putExtra("from", "contact");
            launchIntent.putExtra("user", preferenceRepository.getUserName());
            launchIntent.putExtra("password", preferenceRepository.getPassword());
            launchIntent.putExtra("userInfo", new Gson().toJson(preferenceRepository.getUserData()));
            launchIntent.putExtra("roster", new Gson().toJson(roasterModel));
            startActivity(launchIntent);

        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "bd.com.evaly.econnect")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "bd.com.evaly.econnect")));
            } catch (Exception e2) {
                ToastUtils.show("Please install eConnect app for live chat");
            }

        } catch (Exception e) {
            ToastUtils.show("Please install eConnect app");
        }
    }

}
