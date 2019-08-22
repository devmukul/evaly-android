package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class SignUpActivity extends BaseActivity
{

    EditText firstName,lastName,phoneNumber;
    Button signUp;
    LinearLayout signIn;
    ImageView close;
    UserDetails userDetails;

    String userAgent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }

        firstName=findViewById(R.id.f_name);
        lastName=findViewById(R.id.l_name);
        phoneNumber=findViewById(R.id.number);
        signUp=findViewById(R.id.sign_up);
        signIn=findViewById(R.id.sign_in);
        close=findViewById(R.id.close);
        userDetails=new UserDetails(this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(firstName.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(lastName.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(phoneNumber.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                }else if(phoneNumber.getText().toString().length()!=11){
                    Toast.makeText(SignUpActivity.this, "Please enter your phone number correctly", Toast.LENGTH_SHORT).show();
                }else{
                    signUpUser();
                }
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
                finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void signUpUser(){


        final ViewDialog alert = new ViewDialog(this);

        alert.showDialog();

        JSONObject params = new JSONObject();
        try {
            params.put("first_name", firstName.getText().toString());
            params.put("last_name", lastName.getText().toString());
            params.put("phone_number", phoneNumber.getText().toString());

        } catch (Exception e){

        }
        String url="https://api.evaly.com.bd/core/register/";
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                alert.hideDialog();

//                try{
//
//                }catch (Exception e){
//
//                }

                Log.d("signup_response",response.toString());

                TextView ref = findViewById(R.id.referral);

                String refText = ref.getText().toString();

                if (!refText.equals(""))
                    userDetails.setRef(refText);

                Intent il = new Intent(SignUpActivity.this,SignInActivity.class);
                il.putExtra("phone",phoneNumber.getText().toString().toString());
                il.putExtra("type","signup");
                finish();
                startActivity(il);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(SignUpActivity.this, "This phone number is already registered or server error. Please try again.", Toast.LENGTH_SHORT).show();
                alert.hideDialog();




                try{
                    error.printStackTrace();


                    String json = null;
                    JSONObject jsonObject;

                    NetworkResponse response = error.networkResponse;
                    if(response != null && response.data != null){
                        switch(response.statusCode){
                            case 400:
                                json = new String(response.data);
                                jsonObject = new JSONObject(json);
                                if(jsonObject.getString("status") != null)
                                    Toast.makeText(SignUpActivity.this, jsonObject.getString("status"), Toast.LENGTH_SHORT).show();
                                break;
                            case 500:
                                Toast.makeText(SignUpActivity.this, "Server error, please try again after few minutes.", Toast.LENGTH_SHORT).show();

                        }
                        //Additional cases
                    }



                }catch(Exception e){

                    Toast.makeText(SignUpActivity.this, "Server error, please try again after few minutes.", Toast.LENGTH_SHORT).show();


                }
            }
        }){


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        request.setShouldCache(false);
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
        RequestQueue queue= Volley.newRequestQueue(SignUpActivity.this);
        queue.add(request);
    }



}
