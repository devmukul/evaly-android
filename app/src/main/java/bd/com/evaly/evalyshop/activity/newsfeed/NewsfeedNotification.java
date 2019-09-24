package bd.com.evaly.evalyshop.activity.newsfeed;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import bd.com.evaly.evalyshop.activity.NotificationActivity;
import bd.com.evaly.evalyshop.adapter.NotificationAdapter;
import bd.com.evaly.evalyshop.adapter.NotificationNewsfeedAdapter;
import bd.com.evaly.evalyshop.models.Notifications;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;

public class NewsfeedNotification extends AppCompatActivity {


    RecyclerView recyclerView;
    NotificationNewsfeedAdapter adapter;
    ArrayList<Notifications> notifications;
    UserDetails userDetails;
    LinearLayout not;
    int hot_number;
    TextView hotlist_hot;

    int page = 1;

    RequestQueue queue;


    // newfeed scroller
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed_notification);


        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Newsfeed Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queue= Volley.newRequestQueue(this);

        recyclerView=findViewById(R.id.recycle);
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

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                loading = true;

                page++;

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

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
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

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("onResponse", response.toString());

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
                return headers;
            }
        };
         
        queue.add(request);


    }









}
