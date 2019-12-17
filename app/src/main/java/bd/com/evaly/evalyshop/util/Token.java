package bd.com.evaly.evalyshop.util;

import android.app.Activity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;

public class Token {

    public static void logout(Activity context, DataFetchingListener<JSONObject> listener){

        UserDetails userDetails = new UserDetails(context);

        if(userDetails.getToken().equals(""))
            return;

        String url = UrlUtils.BASE_URL_AUTH_API+"logout/";
        JSONObject parameters = new JSONObject();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters, response -> {
            listener.onDataFetched(response);
        }, error -> Log.e("onErrorResponse", error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
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
