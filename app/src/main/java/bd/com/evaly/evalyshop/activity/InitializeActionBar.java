package bd.com.evaly.evalyshop.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.util.UserDetails;

public class InitializeActionBar {

    private int hot_number = 0;
    private TextView ui_hot = null;
    private UserDetails userDetails;
    private Context context;


    public InitializeActionBar(LinearLayout root, MainActivity mainActivity, String type) {

        userDetails = mainActivity.getUserDetails();
        context = mainActivity;

        root.bringToFront();
        ImageView menuBtn = root.findViewById(R.id.menuBtn);
        RelativeLayout notification = root.findViewById(R.id.notification_holder);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type.equals("home")) {
                    mainActivity.drawer.openDrawer(Gravity.START);
                } else {
                    mainActivity.onBackPressed();
                }


            }
        });


        notification.setOnClickListener(v -> {

            if (userDetails.getToken().equals("")) {
                mainActivity.startActivity(new Intent(mainActivity, SignInActivity.class));
            } else {
                mainActivity.startActivity(new Intent(mainActivity, NotificationActivity.class));
            }
        });


        ui_hot = (TextView) root.findViewById(R.id.hotlist_hot);




        if (!userDetails.getToken().equals(""))
            getNotificationCount();







    }




    public void getNotificationCount(){


        String url = "https://api.evaly.com.bd/core/notifications_count/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("onResponse", response.toString());

                try {

                    int count = response.getInt("unread_notification_count");


                    updateHotCount(count);

                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                String userAgent;
                try {
                    userAgent = WebSettings.getDefaultUserAgent(context);
                } catch (Exception e) {
                    userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
                }
                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);


    }

    public void updateHotCount(final int new_hot_number) {
        hot_number = new_hot_number;
        if (ui_hot == null) return;

        if (new_hot_number == 0)
            ui_hot.setVisibility(View.INVISIBLE);
        else {
            ui_hot.setVisibility(View.VISIBLE);
            ui_hot.setText(Integer.toString(new_hot_number));
        }

    }



}
