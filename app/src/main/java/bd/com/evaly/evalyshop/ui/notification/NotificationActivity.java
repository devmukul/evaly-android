package bd.com.evaly.evalyshop.ui.notification;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.notification.Notifications;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.notification.adapter.NotificationAdapter;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;

public class NotificationActivity extends BaseActivity {

    RecyclerView recyclerView;
    NotificationAdapter adapter;
    ArrayList<Notifications> notifications;
    UserDetails userDetails;
    LinearLayout not;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiivity_notification);
        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setTitle(R.string.notifications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = findViewById(R.id.recycle);
        not = findViewById(R.id.empty);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications, this);
        recyclerView.setAdapter(adapter);
        userDetails = new UserDetails(this);
        getNotifications();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void getNotifications() {
        String url = UrlUtils.BASE_URL + "notifications/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters, response -> {
            Log.d("notifications_response", response.toString());
            try {
                JSONArray jsonArray = response.getJSONArray("results");
                if (jsonArray.length() == 0) {
                    not.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    not.setVisibility(View.GONE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject ob = jsonArray.getJSONObject(i);

                        Notifications item = new Notifications();
                        item.setId(ob.getString("id"));
                        item.setImageURL(ob.getString("thumb_url"));
                        item.setMessage(ob.getString("message"));
                        item.setTime(ob.getString("created_at"));
                        item.setContent_type(ob.getString("content_type"));
                        item.setContent_url(ob.getString("content_url"));
                        item.setRead(ob.getBoolean("read"));
                        notifications.add(item);

                        adapter.notifyItemInserted(notifications.size());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//                markAsRead();

        }, error -> {
            Log.e("onErrorResponse", error.toString());

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401) {

                    AuthApiHelper.refreshToken(NotificationActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            getNotifications();
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


    public void markAsRead() {


        String url = UrlUtils.BASE_URL + "update-notifications/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters, response -> {
            Log.d("onResponse", response.toString());
            Toast.makeText(getApplicationContext(), "Marked as read!", Toast.LENGTH_LONG).show();
        }, error -> {
            Log.e("onErrorResponse", error.toString());

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401) {

                    AuthApiHelper.refreshToken(NotificationActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            markAsRead();
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(NotificationActivity.this);
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_mark_as_read) {
            markAsRead();
        }
        return super.onOptionsItemSelected(item);
    }
}
