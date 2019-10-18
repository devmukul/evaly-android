package bd.com.evaly.evalyshop.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.activity.SignInActivity;
import bd.com.evaly.evalyshop.activity.UserDashboardActivity;

public class Token {


    public static void update(Context context){


        UserDetails userDetails = new UserDetails(context);

        if(userDetails.getToken().equals(""))
            return;

        String url = UrlUtils.BASE_URL+"refresh-auth-token/"+userDetails.getUserName()+"/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json onResponse", response.toString());

                try {
                    JSONObject data = response.getJSONObject("data");

                    String token = data.getString("token");
                    JSONObject ob = data.getJSONObject("user_info");

                    if (ob.has("groups"))
                        userDetails.setGroup(ob.getJSONArray("groups").toString());

                    userDetails.setCreatedAt(ob.getString("created_at"));
                    userDetails.setToken(token);
                    userDetails.setUserName(ob.getString("username"));
                    userDetails.setFirstName(ob.getString("first_name"));
                    userDetails.setLastName(ob.getString("last_name"));
                    userDetails.setEmail(ob.getString("email"));
                    userDetails.setPhone(ob.getString("contact"));
                    userDetails.setJsonAddress(ob.getString("address"));
                    userDetails.setProfilePicture(ob.getString("profile_pic_url"));
                    userDetails.setProfilePictureSM(ob.getString("image_sm"));


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    if (response.statusCode != 201) {
                        Toast.makeText(context, "Your login token is expired, please login again", Toast.LENGTH_LONG).show();
                        userDetails.clearAll();
                        context.startActivity(new Intent(context, SignInActivity.class));
                    }
                }
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());

                return headers;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


}
