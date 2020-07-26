package bd.com.evaly.evalyshop.ui.menu;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.pref.ReferPref;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class InviteEarn extends AppCompatActivity {

    private TextView referText, message, statistics;
    private ViewDialog dialog;
    private NestedScrollView scrollView;
    private Context context;
    private ReferPref referPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_earn);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Evaly referral program");

        message = findViewById(R.id.message);
        referText = findViewById(R.id.ref_code);
        statistics = findViewById(R.id.statistics);
        scrollView = findViewById(R.id.scrollView);
        referPref = new ReferPref(this);
        dialog = new ViewDialog(this);
        getReferralInfo();

        context = this;

        message.setText(referPref.getRefMessage());
        referText.setText("EVALY-" + CredentialManager.getUserName());
        statistics.setText(Html.fromHtml(referPref.getRefStatistics()));

        Button rate = findViewById(R.id.rate);

        rate.setOnClickListener(v -> addDialog());


        if (referPref.isRated())
            rate.setVisibility(View.GONE);


        TextView copy = findViewById(R.id.copy);

        copy.setOnClickListener(v -> {
            try {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("referral", referText.getText().toString().trim());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(InviteEarn.this, "Invitation code copied.", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {

                Toast.makeText(InviteEarn.this, "Can't copy invitation code.", Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void getReferralInfo() {


        // scrollView.setVisibility(View.GONE);

        String url = "https://nsuer.club/evaly/referral/info.php?username=" + CredentialManager.getUserName();
        JSONObject parameters = new JSONObject();


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), response -> {
            //Log.d("onResponse", response.toString());

            try {

                boolean isAvailable = response.getBoolean("isAvailable");
                String messageString = response.getString("message");
                String statisticsString = response.getString("statistics");


                if (!isAvailable) {

                    Toast.makeText(InviteEarn.this, "Invite referral program is not available right now", Toast.LENGTH_LONG).show();
                    finish();

                }


                scrollView.setVisibility(View.VISIBLE);


                referPref.setRefMessage(messageString);
                referPref.setRefStatistics(statisticsString);
                message.setText(messageString);
                statistics.setText(Html.fromHtml(statisticsString));


            } catch (Exception e) {

            }


        }, error -> {
            Log.e("onErrorResponse", error.toString());
            dialog.hideDialog();
            Toast.makeText(InviteEarn.this, "Server error occurred, check your network settings.", Toast.LENGTH_LONG).show();
            finish();


        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(InviteEarn.this);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);


    }


    public void addDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.WideDialog));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_rating_playstore, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();

        RatingBar d_rating_bar = dialogView.findViewById(R.id.ratingBar);
        Button d_submit = dialogView.findViewById(R.id.submit);


        alertDialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        alertDialog.show();


        d_submit.setOnClickListener(v -> {

            if (d_rating_bar.getRating() < 1) {
                Toast.makeText(InviteEarn.this, "Please set star rating.", Toast.LENGTH_SHORT).show();
                return;
            } else if (d_rating_bar.getRating() < 4) {


                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "hm.tamim@evaly.com.bd", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Evaly App Feedback");
                //emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

                alertDialog.dismiss();

                return;

            } else {


                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                }

                ratingUpdate();

                referPref.setRated(true);

                alertDialog.dismiss();

            }


        });
    }


    public void ratingUpdate() {


        String url = "https://nsuer.club/evaly/referral/submit-rating.php";


        //Log.d("json url", url);

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            //Log.d("json", response);


        }, error -> Log.e("onErrorResponse", error.toString())) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("token", CredentialManager.getToken().trim());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
