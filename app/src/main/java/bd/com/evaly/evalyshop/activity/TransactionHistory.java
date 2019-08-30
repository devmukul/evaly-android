package bd.com.evaly.evalyshop.activity;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.NotificationAdapter;
import bd.com.evaly.evalyshop.adapter.TransactionHistoryAdapter;
import bd.com.evaly.evalyshop.models.Notifications;
import bd.com.evaly.evalyshop.models.TransactionItem;
import bd.com.evaly.evalyshop.util.UserDetails;

public class TransactionHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    TransactionHistoryAdapter adapter;
    ArrayList<TransactionItem> itemList;
    UserDetails userDetails;
    LinearLayout not;
    String userAgent;
    ProgressBar progressBar;
    int currentPage = 0;

    NestedScrollView nestedSV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Transaction History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }


        nestedSV = findViewById(R.id.stickyScrollView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.recycle);
        not=findViewById(R.id.empty);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        itemList = new ArrayList<>();
        adapter = new TransactionHistoryAdapter(itemList,this);
        recyclerView.setAdapter(adapter);
        userDetails = new UserDetails(this);


        getTransactionHistory(++currentPage);



        if (nestedSV != null) {

            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";


                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i(TAG, "BOTTOM SCROLL");

                        try {
                            getTransactionHistory(++currentPage);
                        }catch (Exception e){
                            Log.e("scroll error", e.toString());
                        }
                    }
                }
            });
        }


    }



    public void getTransactionHistory(int page){

        progressBar.setVisibility(View.VISIBLE);

        String url="https://api.evaly.com.bd/pay/wallet-history/01751977045?page="+page;

        Log.d("json url", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                progressBar.setVisibility(View.GONE);

                Log.d("notifications_response", response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    if(jsonArray.length()==0 && page == 1){
                        not.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                    }else{
                        not.setVisibility(View.GONE);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject ob = jsonArray.getJSONObject(i);

                            itemList.add(
                                    new TransactionItem(
                                            ob.getInt("amount"),
                                            ob.getString("date_time"),
                                            ob.getString("event"))
                            );

                            adapter.notifyItemInserted(itemList.size());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());

                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
                headers.put("Host", "api.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                //headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(this);
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
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
