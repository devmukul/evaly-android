package bd.com.evaly.evalyshop.ui.newsfeed;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
import bd.com.evaly.evalyshop.adapter.NotificationNewsfeedAdapter;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.notification.Notifications;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;

public class NewsfeedNotification extends AppCompatActivity {


    RecyclerView recyclerView;
    NotificationNewsfeedAdapter adapter;
    ArrayList<Notifications> notifications;
    UserDetails userDetails;
    LinearLayout not, progressContainer;
    int hot_number;
    TextView hotlist_hot;

    int page = 1;

    RequestQueue queue;

    // newfeed scroller
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed_notification);


        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Newsfeed Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queue= Volley.newRequestQueue(this);

        recyclerView=findViewById(R.id.recycle);
        progressBar = findViewById(R.id.progressBar);
        progressContainer = findViewById(R.id.progressContainer);

        not=findViewById(R.id.empty);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        notifications=new ArrayList<>();
        adapter=new NotificationNewsfeedAdapter(notifications,this);
        recyclerView.setAdapter(adapter);
        userDetails=new UserDetails(this);

        getNotifications();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    pastVisiblesItems = manager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            getNotifications();

                        }
                    }
                }
            }
        });


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void getNotifications(){
        String url= UrlUtils.BASE_URL_NEWSFEED+"notifications?page="+page;
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }

        loading = false;

        Log.d("json url", url);

        if (page == 1)
            progressContainer.setVisibility(View.VISIBLE);

        if (page>1)
            progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters, response -> {

            loading = true;

            page++;

            progressContainer.setVisibility(View.GONE);
            progressBar.setVisibility(View.INVISIBLE);

            Log.d("notifications_response", response.toString());
            try {
                JSONArray jsonArray = response.getJSONArray("results");
                if(jsonArray.length()==0){
                    not.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else{
                    not.setVisibility(View.GONE);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject ob = jsonArray.getJSONObject(i);

                        Notifications item = new Notifications();
                        item.setId(ob.getString("id"));
                        item.setImageURL(ob.getString("thumb_url"));
                        item.setMessage(ob.getString("message"));
                        item.setTime(ob.getString("created_at"));
                        item.setContent_type(ob.getString("content_type"));
                        item.setContent_url(ob.getString("content_url"));
                        item.setRead(ob.getBoolean("read"));
                        item.setMeta_data(ob.getString("meta_data"));

                        notifications.add(item);

                        adapter.notifyItemInserted(notifications.size());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            markAsRead();

        }, error -> {
            Log.e("onErrorResponse", error.toString());

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401){

                AuthApiHelper.refreshToken(NewsfeedNotification.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                    @Override
                    public void onDataFetched(retrofit2.Response<JsonObject> response) {
                        getNotifications();
                    }

                    @Override
                    public void onFailed(int status) {

                    }
                });

                return;

            }}

            progressContainer.setVisibility(View.GONE);

            progressBar.setVisibility(View.GONE);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());
                return headers;
            }
        };
         
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }




    public void markAsRead(){


        String url = UrlUtils.BASE_URL_NEWSFEED+"update-notifications/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters, response -> Log.d("onResponse", response.toString()), error -> {
            Log.e("onErrorResponse", error.toString());

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401){

                AuthApiHelper.refreshToken(NewsfeedNotification.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                    @Override
                    public void onDataFetched(retrofit2.Response<JsonObject> response) {
                        markAsRead();
                    }

                    @Override
                    public void onFailed(int status) {

                    }
                });

                return;

            }}

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());
                return headers;
            }
        };
         
        queue.add(request);


    }


}
