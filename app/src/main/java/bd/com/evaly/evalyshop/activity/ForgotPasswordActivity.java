package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.password.PasswordActivity;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class ForgotPasswordActivity extends BaseActivity {

    EditText number;
    Button reset;
    ImageView close;
    ViewDialog dialog;
    String userAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }
        number = findViewById(R.id.phone);
        reset = findViewById(R.id.reset);
        close = findViewById(R.id.closeBtn);
        dialog = new ViewDialog(ForgotPasswordActivity.this);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number.getText().toString().equals("")) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter number", Toast.LENGTH_SHORT).show();
                } else if (number.getText().toString().length() != 11) {
                    Toast.makeText(ForgotPasswordActivity.this, "The length of number must be 11", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.showDialog();
                    resetPassword();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void resetPassword() {


        JSONObject params = new JSONObject();
        try {
            params.put("phone_number", number.getText().toString());
        } catch (Exception e) {
        }

        String url = UrlUtils.BASE_URL_AUTH + "forgot-password";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.hideDialog();

                Intent il = new Intent(ForgotPasswordActivity.this, PasswordActivity.class);
                il.putExtra("phone", number.getText().toString());
                finish();
                startActivity(il);

                Log.d("change_password", response.toString());
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {

                    dialog.hideDialog();

                    String json = null;
                    JSONObject jsonObject;

                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        switch (response.statusCode) {
                            case 400:
                                json = new String(response.data);
                                jsonObject = new JSONObject(json);
                                if (jsonObject.getString("success") != null)
                                    Toast.makeText(ForgotPasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                break;

                            case 401:

                                    AuthApiHelper.refreshToken(ForgotPasswordActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                                        @Override
                                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                                            resetPassword();
                                        }

                                        @Override
                                        public void onFailed(int status) {

                                        }
                                    });
                                    return;

                            case 500:
                                Toast.makeText(ForgotPasswordActivity.this, "Server error, please try again after few minutes.", Toast.LENGTH_SHORT).show();
                        }
                        //Additional cases
                    }

                    error.printStackTrace();
                } catch (Exception e) {

                    Toast.makeText(ForgotPasswordActivity.this, "Server error, please try again after few minutes.", Toast.LENGTH_SHORT).show();
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ForgotPasswordActivity.this);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotPasswordActivity.this, SignInActivity.class));
        finish();
    }
}
