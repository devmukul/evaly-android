package bd.com.evaly.evalyshop.ui.auth;

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

import com.google.gson.JsonObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class SignInActivity extends BaseActivity {

    TextView forgot;
    LinearLayout signUp;
    ImageView close, showPassword;
    EditText phoneNumber, password;
    Button signIn;
    String token = "", userNamePhone = "", passwordValue = "";
    boolean isShowing = false;
    ViewDialog alert;
    int attempt = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_new);

        signUp = findViewById(R.id.sign_up);
        close = findViewById(R.id.closeBtn);
        phoneNumber = findViewById(R.id.phone_number);
        password = findViewById(R.id.password);
        signIn = findViewById(R.id.sign_in);
        showPassword = findViewById(R.id.show_pass);
        forgot = findViewById(R.id.forgot);
        alert = new ViewDialog(this);
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
        } else phoneNumber.requestFocus();

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
                signInUser();
            }
        });
    }


    public void signInUser() {

        HashMap<String, String> payload = new HashMap<>();
        payload.put("phone_number", phoneNumber.getText().toString());
        payload.put("password", password.getText().toString());

        AuthApiHelper.login(payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int code) {
                alert.hideDialog();
                switch (code) {
                    case 200:
                    case 201:
                    case 202:
                        response = response.get("data").getAsJsonObject();
                        token = response.get("access_token").getAsString();
                        CredentialManager.saveToken(token);
                        CredentialManager.saveRefreshToken(response.get("refresh_token").getAsString());
                        CredentialManager.saveUserName(phoneNumber.getText().toString());
                        CredentialManager.savePassword(password.getText().toString());
                        Balance.updateUserInfo(SignInActivity.this, true);
                        Toast.makeText(SignInActivity.this, "Successfully signed in.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(SignInActivity.this, "Incorrect phone number or password. Please try again! ", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailed(String errorBody, int status) {
                alert.hideDialog();
                if (errorBody != null && !errorBody.equals(""))
                    ToastUtils.show(errorBody);
                else
                    ToastUtils.show(R.string.something_wrong);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

}
