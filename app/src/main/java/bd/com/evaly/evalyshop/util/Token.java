package bd.com.evaly.evalyshop.util;

import android.app.Activity;
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
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.activity.SignInActivity;
import bd.com.evaly.evalyshop.activity.UserDashboardActivity;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;

public class Token {


    public static void update(Activity context, boolean openDashboard){


        UserDetails userDetails = new UserDetails(context);

        if(userDetails.getToken().equals(""))
            return;

        String url = UrlUtils.BASE_URL_AUTH_API+"refresh/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("access", userDetails.getToken());
            parameters.put("refresh", userDetails.getRefreshToken());
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json onResponse", response.toString());

                try {
                    userDetails.setToken(response.getString("access"));
                    userDetails.setRefreshToken(response.getString("refresh"));


                    CredentialManager.saveToken(response.getString("access"));
                    CredentialManager.saveRefreshToken(response.getString("refresh"));

                } catch (Exception e){

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    if (response.statusCode != 401) {
                        Toast.makeText(context, "Your login token is expired, please login again", Toast.LENGTH_LONG).show();
                        AppController.logout(context);
                    }
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
        RequestQueue queue= Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }



    public static void logout(Activity context, DataFetchingListener<JSONObject> listener){


        UserDetails userDetails = new UserDetails(context);

        if(userDetails.getToken().equals(""))
            return;

        String url = UrlUtils.BASE_URL_AUTH_API+"logout/";
        JSONObject parameters = new JSONObject();

        Log.d("jsonz url", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                Log.d("jsonz response", response.toString());

                listener.onDataFetched(response);


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
                headers.put("Authorization", CredentialManager.getToken());

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
