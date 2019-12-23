package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class SignInActivity extends BaseActivity {

    TextView forgot;
    LinearLayout signUp;
    ImageView close, showPassword;
    EditText phoneNumber, password;
    Button signIn;
    String token = "", userNamePhone = "", passwordValue = "";
    boolean isShowing = false;
    UserDetails userDetails;
    ViewDialog alert;
    String userAgent;

    int attempt = 0;

    RequestQueue queue;

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

        queue = Volley.newRequestQueue(SignInActivity.this);

        close.setOnClickListener(v -> onBackPressed());


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

        showPassword.setOnClickListener(v -> {
            if (!isShowing) {
                isShowing = !isShowing;
                password.setInputType(InputType.TYPE_CLASS_TEXT);
                showPassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                isShowing = !isShowing;
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPassword.setImageResource(R.drawable.ic_visibility);
            }
        });

        forgot.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
            finish();
        });

        signIn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
                Toast.makeText(SignInActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password.getText().toString())) {
                Toast.makeText(SignInActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            } else if (phoneNumber.getText().toString().length() != 11) {
                Toast.makeText(SignInActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            } else {
                alert.showDialog();
//                    AsyncTask.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            AppController.database.clearAllTables();
//                        }
//                    });
                signInUser();
            }
        });
    }

    public void signInUser() {

        JSONObject payload = new JSONObject();

        try {

            payload.put("password", password.getText().toString());
            payload.put("username", phoneNumber.getText().toString());

            Logger.d(password.getText().toString()+"    "+ phoneNumber.getText().toString());

            userNamePhone = phoneNumber.getText().toString();
            passwordValue = password.getText().toString();

        } catch (Exception e) {

        }


        String url = UrlUtils.BASE_URL_AUTH_API + "login/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload, response -> {

            alert.hideDialog();

            try {

                JSONObject data = response;

                token = data.getString("access");

                CredentialManager.saveToken(token);

                CredentialManager.saveUserName(userNamePhone);
                CredentialManager.savePassword(passwordValue);

                userDetails.setUserName(phoneNumber.getText().toString());
                userDetails.setToken(token);
                userDetails.setRefreshToken(data.getString("refresh"));


                Balance.update(SignInActivity.this, true);


            } catch (Exception e) {

                Toast.makeText(SignInActivity.this, "Incorrect phone number or password. Please try again! ", Toast.LENGTH_SHORT).show();

            }

        }, error -> {

            error.printStackTrace();

            try {

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {


                    JSONObject jsonObject = new JSONObject(new String(response.data));

                    switch (response.statusCode) {

                        case 500:
                            Toast.makeText(SignInActivity.this, "Server error, please try again after few minutes.", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(SignInActivity.this, jsonObject.getString("detail") , Toast.LENGTH_SHORT).show();
                    }
                    //Additional cases
                }

                alert.hideDialog();
                error.printStackTrace();



            } catch (Exception e) {
                e.printStackTrace();
                Logger.d(e.getMessage());
                Toast.makeText(SignInActivity.this, "Server error, please try again after few minutes.", Toast.LENGTH_SHORT).show();
            }



        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                return headers;
            }


        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }




}
