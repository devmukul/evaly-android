package bd.com.evaly.evalyshop.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.activity.UserDashboardActivity;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.user.UserModel;


public class Balance {

    public static void update(Activity context, boolean openDashboard) {

        UserDetails userDetails = new UserDetails(context);


        String url = UrlUtils.BASE_URL_AUTH + "user-info-pay/" + userDetails.getUserName() + "/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Log.d("onResponse", response.toString());
                try {
                    JSONObject data = response.getJSONObject("data");

                    userDetails.setBalance(data.getString("balance"));

                    JSONObject ob = data.getJSONObject("user");

                    if (ob.has("groups"))
                        userDetails.setGroup(ob.getJSONArray("groups").toString());

                    userDetails.setUserName(ob.getString("username"));
                    userDetails.setFirstName(ob.getString("first_name"));
                    userDetails.setLastName(ob.getString("last_name"));
                    userDetails.setEmail(ob.getString("email"));
                    userDetails.setPhone(ob.getString("contact"));
                    userDetails.setJsonAddress(ob.getString("address"));
                    userDetails.setProfilePicture(ob.getString("profile_pic_url"));
                    userDetails.setProfilePictureSM(ob.getString("image_sm"));

                    UserModel userModel = new Gson().fromJson(ob.toString(), UserModel.class);
//
                    Logger.d(new Gson().toJson(userModel));
                    CredentialManager.saveUserData(userModel);


                    if (openDashboard) {

                        Toast.makeText(context, "Successfully signed in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, UserDashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("from", "signin");
                        context.startActivity(intent);
                        context.finishAffinity();

                    }


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
                    if (error.networkResponse.statusCode == 401){

                    AuthApiHelper.refreshToken(context, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            update(context,openDashboard);
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                }}

            }
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
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }


    public static void update(Activity context, TextView textView) {

        UserDetails userDetails = new UserDetails(context);


        String url = UrlUtils.BASE_URL_AUTH + "user-info-pay/" + userDetails.getUserName() + "/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("onResponse", response.toString());


                try {

                    response = response.getJSONObject("data");
                    userDetails.setBalance(response.getString("balance"));

                    textView.setText("৳ " + response.getString("balance"));


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
                    if (error.networkResponse.statusCode == 401){

                    AuthApiHelper.refreshToken(context, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            update(context,textView);
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                }}

            }
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
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }


}
