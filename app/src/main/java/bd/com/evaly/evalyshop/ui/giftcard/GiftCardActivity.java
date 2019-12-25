package bd.com.evaly.evalyshop.ui.giftcard;


import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.BaseViewPagerAdapter;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;

public class GiftCardActivity extends AppCompatActivity {


    private ViewPager viewPager;
    private TabLayout tabLayout;
    private BaseViewPagerAdapter pager;
    private TextView balance;
    private UserDetails userDetails;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giftcards);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Gift Cards");




        userDetails = new UserDetails(this);



        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewpager);
        balance = findViewById(R.id.balance);

        pager = new BaseViewPagerAdapter(getSupportFragmentManager());


        if (!userDetails.getToken().equals("")) {

            pager.addFragment(new GiftCardMyFragment(), "MY GIFTS");
            pager.addFragment(new GiftCardPurchasedFragment(), "PURCHASED");
        }

        pager.addFragment(new GiftCardListFragment(),"STORE");


        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(1);

        balance.setVisibility(View.GONE);

        updateBalance();



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


    public void updateBalance() {



        String url = UrlUtils.BASE_URL_AUTH + "user-info-pay/" + userDetails.getUserName() + "/";
        JSONObject parameters = new JSONObject();

        Log.d("onResponse", url);
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), response -> {
            Log.d("onResponse", response.toString());

            try {
                response = response.getJSONObject("data");
                balance.setText("Gift Card Balance: ৳ " + response.getString("gift_card_balance"));
                balance.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.e("onErrorResponse", error.toString());

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401){

                    AuthApiHelper.refreshToken(GiftCardActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            updateBalance();
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;
                }}
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());

                return headers;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(GiftCardActivity.this);
        queue.add(request);
    }

}
