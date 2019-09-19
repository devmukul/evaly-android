package bd.com.evaly.evalyshop.activity.newsfeed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.LinearLayout;

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

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.NotificationActivity;
import bd.com.evaly.evalyshop.adapter.NotificationAdapter;
import bd.com.evaly.evalyshop.models.Notifications;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;

public class NewsfeedNotification extends AppCompatActivity {


    RecyclerView recyclerView;
    NotificationAdapter adapter;
    ArrayList<Notifications> notifications;
    UserDetails userDetails;
    LinearLayout not;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed_notification);


        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=findViewById(R.id.recycle);
        not=findViewById(R.id.empty);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        notifications=new ArrayList<>();
        adapter=new NotificationAdapter(notifications,this);
        recyclerView.setAdapter(adapter);
        userDetails=new UserDetails(this);
        getNotifications();



    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void getNotifications(){
        String url= UrlUtils.BASE_URL_NEWSFEED+"notifications/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);


    }

}
