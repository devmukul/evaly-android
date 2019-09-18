package bd.com.evaly.evalyshop.util;

import android.content.Context;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

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

                    if (ob.has("groups")){

                        userDetails.setGroup(ob.getJSONArray("groups").toString());

                    }

                    Log.d("json group", userDetails.getGroups());

                    userDetails.setCreatedAt(ob.getString("created_at"));

                    userDetails.setToken(token);
                    userDetails.setUserName(ob.getString("username"));
                    userDetails.setFirstName(ob.getString("first_name"));
                    userDetails.setLastName(ob.getString("last_name"));

                    userDetails.setEmail(ob.getString("email"));
                    userDetails.setPhone(ob.getString("contact"));

                    //userDetails.setUserID(ob.getInt("id"));
                    userDetails.setJsonAddress(ob.getString("address"));
                    userDetails.setProfilePicture(ob.getString("profile_pic_url"));
                    userDetails.setProfilePictureSM(ob.getString("image_sm"));


                    Log.d("json token", "token updated");

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
                // headers.put("Host", "api-prod.evaly.com.bd");
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
        RequestQueue queue= Volley.newRequestQueue(context);
        queue.add(request);
    }


}
