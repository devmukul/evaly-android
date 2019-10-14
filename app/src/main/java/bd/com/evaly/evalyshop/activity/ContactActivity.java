package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thefinestartist.finestwebview.FinestWebView;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;

import static bd.com.evaly.evalyshop.activity.ViewProductActivity.setWindowFlag;

public class ContactActivity extends BaseActivity
{

    TextView call1,call2,call3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        //getSupportActionBar().setElevation(0);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Contact us");

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //make fully Android Transparent Status bar
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        LinearLayout callBox = findViewById(R.id.callBox);
        callBox.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:09638111666"));
            startActivity(intent);
        });


        LinearLayout emailBox = findViewById(R.id.emailBox);
        emailBox.setOnClickListener(v -> {

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","support@evaly.com.bd", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(emailIntent);
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
