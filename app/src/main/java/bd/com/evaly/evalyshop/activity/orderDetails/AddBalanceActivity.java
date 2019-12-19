package bd.com.evaly.evalyshop.activity.orderDetails;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class AddBalanceActivity extends BaseActivity
{


    /*
        H. M. Tamim
        26/Jun/2019
     */


    UserDetails userDetails;
    ViewDialog dialog;
    Button button;
    EditText input;

    Context context;

    String userAgent;
    int paymentMethod = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_balance);


        startActivity(new Intent(this, PayViaBkashActivity.class));

        finish();


        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            try {

                getWindow().setStatusBarColor(Color.parseColor("#f7f7f7"));
                // getWindow().getDecorView().setSystemUiVisibility(0);
            } catch (Exception e){

            }
        }



        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Add Balance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        userDetails = new UserDetails(this);
        dialog = new ViewDialog(this);

        userAgent = new WebView(this).getSettings().getUserAgentString();


        NestedScrollView scrollView = findViewById(R.id.scroll);

        button = findViewById(R.id.button);


        ImageView visa = findViewById(R.id.visa);
        ImageView bkash = findViewById(R.id.bkash);


        TabLayout tabs = findViewById(R.id.tab_layout);


        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 1){

                    TabLayout.Tab tabz = tabs.getTabAt(0);
                    tabz.select();

                    Intent intent = new Intent(AddBalanceActivity.this, PayViaBkashActivity.class);
                    startActivity(intent);


                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });










        // select payment method

        visa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                paymentMethod = 1;


            }
        });


        visa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.performClick();
                }
            }
        });


        bkash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                paymentMethod = 2;


            }
        });


        bkash.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.performClick();
                }
            }
        });



        visa.requestFocus();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(paymentMethod == 1)
                    addBalance();
                else {

                    Intent intent = new Intent(AddBalanceActivity.this, PayViaBkashActivity.class);
                    startActivity(intent);

                }
            }
        });

        input = findViewById(R.id.input);

        Intent data = getIntent();

        if(data.hasExtra("due")){

            Toast.makeText(context,"Insufficient Balance", Toast.LENGTH_LONG).show();
            input.setText(String.valueOf(data.getDoubleExtra("due", 0.0)));
        }



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







    public void addBalance() {

        String url = "https://api-prod.evaly.com.bd/pay/pg";

        Log.d("json order url", url);



        JSONObject payload = new JSONObject();


        String balance = input.getText().toString();

        if (balance.equals("")){
            Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            payload.put("amount", balance);
        } catch (Exception e){

        }



        dialog.showDialog();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.hideDialog();

                try {
                    String purl = response.getString("payment_gateway_url");


                    Utils.CustomTab(url, AddBalanceActivity.this);

                }catch (Exception e){


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    if (error.networkResponse.statusCode == 401){

                    AuthApiHelper.refreshToken(AddBalanceActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            addBalance();
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                }}


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());

                return headers;
            }




        };
        RequestQueue queue= Volley.newRequestQueue(AddBalanceActivity.this);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }




}
