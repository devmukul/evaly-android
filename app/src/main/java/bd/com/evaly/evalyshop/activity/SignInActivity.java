package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class SignInActivity extends BaseActivity {

    TextView forgot;
    LinearLayout signUp;
    ImageView close, showPassword;
    EditText phoneNumber, password;
    Button signIn;
    String token = "", userNamePhone = "";
    boolean isShowing = false;
    UserDetails userDetails;
    ViewDialog alert;
    String userAgent;

    int attempt = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        signUp = findViewById(R.id.signup);
        close = findViewById(R.id.closeBtn);
        phoneNumber = findViewById(R.id.phone_number);
        password = findViewById(R.id.password);
        signIn = findViewById(R.id.sign_in);
        showPassword = findViewById(R.id.show_pass);
        forgot = findViewById(R.id.forgot);
        userDetails = new UserDetails(this);
        alert = new ViewDialog(this);

        close.setOnClickListener(v -> onBackPressed());
        //userAgent = new WebView(this).getSettings().getUserAgentString();


        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }


        Log.d("json agent", userAgent);

        Intent data = getIntent();

        if (data.hasExtra("phone")) {

            phoneNumber.setText(data.getStringExtra("phone"));

            phoneNumber.clearFocus();
            password.clearFocus();

            if (data.hasExtra("type")) {

                if (data.getStringExtra("type").equals("forgetpassword"))
                    Toast.makeText(SignInActivity.this, "Password sent to your mobile. Please login using the new password", Toast.LENGTH_LONG).show();

                else if (data.getStringExtra("type").equals("signup"))
                    Toast.makeText(SignInActivity.this, "Successfully signed up. Please check SMS for password.", Toast.LENGTH_LONG).show();
            }


        } else {


            phoneNumber.requestFocus();

        }

        signUp.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowing) {
                    isShowing = !isShowing;
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    showPassword.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    isShowing = !isShowing;
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPassword.setImageResource(R.drawable.ic_visibility);
                }
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
                finish();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
                    Toast.makeText(SignInActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(SignInActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                } else if (phoneNumber.getText().toString().length() != 11) {
                    Toast.makeText(SignInActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                } else {
                    alert.showDialog();
                    signInUser();
                }
            }
        });
    }

    public void signInUser() {


        // String url="https://api-dev.evaly.com.bd/api/api-token-auth/"; // dev mode, don't use it

        JSONObject payload = new JSONObject();

        try {

            payload.put("password", password.getText().toString());
            payload.put("username", phoneNumber.getText().toString());


            userNamePhone = phoneNumber.getText().toString();

        } catch (Exception e) {

        }


        String url = UrlUtils.BASE_URL + "login/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                alert.hideDialog();

                Log.d("json login", response.toString());

                try {
                    JSONObject data = response.getJSONObject("data");

                    token = data.getString("token");


                    JSONObject ob = data.getJSONObject("user_info");
                    userDetails.setToken(token);
                    userDetails.setUserName(ob.getString("username"));
                    userDetails.setFirstName(ob.getString("first_name"));
                    userDetails.setLastName(ob.getString("last_name"));
                    userDetails.setEmail(ob.getString("email"));
                    userDetails.setPhone(ob.getString("contact"));
                    userDetails.setUserID(ob.getInt("id"));
                    userDetails.setJsonAddress(ob.getString("address"));
                    userDetails.setProfilePicture(ob.getString("profile_pic_url"));
                    userDetails.setProfilePictureSM(ob.getString("image_sm"));



                    Toast.makeText(SignInActivity.this, "Successfully signed in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignInActivity.this, UserDashboardActivity.class);
                    intent.putExtra("from", "signin");
                    startActivity(intent);
                    finish();


                } catch (Exception e) {

                    Toast.makeText(SignInActivity.this, "Incorrect phone number or password. Please try again! ", Toast.LENGTH_SHORT).show();

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
                if (attempt < 2) {

                    signInUser();

                    attempt++;

                } else {


                    try {


                        alert.hideDialog();
                        error.printStackTrace();

                        String json = null;
                        JSONObject jsonObject;

                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            switch (response.statusCode) {
                                case 400:
                                    json = new String(response.data);
                                    jsonObject = new JSONObject(json);
                                    if (jsonObject.getString("status") != null)
                                        Toast.makeText(SignInActivity.this, jsonObject.getString("status"), Toast.LENGTH_SHORT).show();
                                    break;
                                case 500:
                                    Toast.makeText(SignInActivity.this, "Server error, please try again after few minutes.", Toast.LENGTH_SHORT).show();
                            }
                            //Additional cases
                        }

                    } catch (Exception e) {
                        Toast.makeText(SignInActivity.this, "Server error, please try again after few minutes.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json, text/plain, */*");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);

                return headers;
            }


        };
        request.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(SignInActivity.this);
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


//    public void getUserData(){
//
//
//        final ViewDialog alert = new ViewDialog(this);
//
//        alert.showDialog();
//
//        String url="https://api-prod.evaly.com.bd/api/user/detail/"+phoneNumber.getText().toString()+"/";
//        JSONObject parameters = new JSONObject();
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                alert.hideDialog();
//
//                Log.d("onResponse", response.toString());
//                try {
//                    JSONObject ob=response.getJSONObject("user");
//                    userDetails.setToken(token);
//                    userDetails.setUserName(ob.getString("username"));
//                    userDetails.setPhone(ob.getString("username"));
//                    userDetails.setFirstName(ob.getString("first_name"));
//                    userDetails.setLastName(ob.getString("last_name"));
//                    JSONArray addressArray=response.getJSONArray("addresses");
//
//                    userDetails.setJsonAddress(addressArray.toString());
//
//                    //JSONObject addressOB=response.getJSONObject("addresses");
//                    if(addressArray.length()!=0){
//                        String addressStr="",addressIDStr="";
//                        for(int i=0;i<addressArray.length();i++){
//                            JSONObject addressOB=addressArray.getJSONObject(i);
//                            addressStr+=addressOB.get("address")+"::";
//                            addressIDStr+=addressOB.get("id")+"::";
//                        }
//                        userDetails.setAddresses(addressStr);
//                        userDetails.setAddressID(addressIDStr);
//                    }else{
//                        userDetails.setAddresses("");
//                        userDetails.setAddressID("");
//                    }
//                    Toast.makeText(SignInActivity.this, "Successfully signed in", Toast.LENGTH_SHORT).show();
//                    Intent intent=new Intent(SignInActivity.this, UserDashboardActivity.class);
//                    intent.putExtra("from","signin");
//                    startActivity(intent);
//                    finish();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("onErrorResponse", error.toString());
//
//                alert.hideDialog();
//                Toast.makeText(SignInActivity.this, "Server error occurred, please try again later. You will get a notification when it's fixed.", Toast.LENGTH_LONG).show();
//
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                // headers.put("Authorization", "Bearer " + token + "." + Utils.extraToken(userNamePhone));
//                headers.put("Authorization", "Bearer " + token);
//                largeLog("Token", token);
//
//                // headers.put("Host", "api-prod.evaly.com.bd");
//                headers.put("Origin", "https://evaly.com.bd");
//                headers.put("Referer", "https://evaly.com.bd/");
//                headers.put("User-Agent", userAgent);
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//        request.setShouldCache(false);
////        request.setRetryPolicy(new RetryPolicy() {
////            @Override
////            public int getCurrentTimeout() {
////                return 50000;
////            }
////
////            @Override
////            public int getCurrentRetryCount() {
////                return 50000;
////            }
////
////            @Override
////            public void retry(VolleyError error) throws VolleyError {
////
////            }
////        });
//        RequestQueue queue= Volley.newRequestQueue(SignInActivity.this);
//        queue.add(request);
//    }


    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }

}
