package bd.com.evaly.evalyshop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.auth.password.PasswordActivity;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class ForgotPasswordActivity extends BaseActivity {

    private EditText number;
    private Button reset;
    private ImageView close;
    private ViewDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        number = findViewById(R.id.phone);
        reset = findViewById(R.id.reset);
        close = findViewById(R.id.closeBtn);
        dialog = new ViewDialog(ForgotPasswordActivity.this);

        reset.setOnClickListener(v -> {
            if (number.getText().toString().equals("")) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter number", Toast.LENGTH_SHORT).show();
            } else if (number.getText().toString().length() != 11) {
                Toast.makeText(ForgotPasswordActivity.this, "The length of number must be 11", Toast.LENGTH_SHORT).show();
            } else {
                dialog.showDialog();
                resetPassword();
            }
        });

        close.setOnClickListener(v -> onBackPressed());
    }

    private void resetPassword() {

        AuthApiHelper.forgetPassword(number.getText().toString(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                dialog.hideDialog();

                Toast.makeText(ForgotPasswordActivity.this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();

                if (statusCode == 201) {
                    Intent il = new Intent(ForgotPasswordActivity.this, PasswordActivity.class);
                    il.putExtra("phone", number.getText().toString());
                    finish();
                    startActivity(il);
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                dialog.hideDialog();
                if (errorCode == 404)
                    Toast.makeText(ForgotPasswordActivity.this, "This phone number is not registered yet", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ForgotPasswordActivity.this, "Error occurred!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAuthError(boolean logout) {

                dialog.hideDialog();

            }
        });

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotPasswordActivity.this, SignInActivity.class));
        finish();
    }
}
