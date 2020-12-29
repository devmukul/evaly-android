package bd.com.evaly.evalyshop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonObject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ActivityForgotPasswordBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.auth.password.PasswordActivity;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class ForgotPasswordActivity extends BaseActivity {


    private ViewDialog dialog;
    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);

        dialog = new ViewDialog(ForgotPasswordActivity.this);
        binding.reset.setOnClickListener(v -> {
            if (binding.phone.getText().toString().equals("")) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter number", Toast.LENGTH_SHORT).show();
            } else if (binding.phone.getText().toString().length() != 11) {
                Toast.makeText(ForgotPasswordActivity.this, "The length of number must be 11", Toast.LENGTH_SHORT).show();
            } else {
                dialog.showDialog();
                resetPassword();
            }
        });
        binding.closeBtn.setOnClickListener(v -> onBackPressed());
    }

    private void resetPassword() {

        AuthApiHelper.forgetPassword(binding.phone.getText().toString(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                dialog.hideDialog();
                Toast.makeText(ForgotPasswordActivity.this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                if (statusCode == 201 || statusCode == 200) {
                    Intent il = new Intent(ForgotPasswordActivity.this, PasswordActivity.class);
                    il.putExtra("phone", binding.phone.getText().toString());
                    il.putExtra("request_id", response.get("data").getAsJsonObject().get("request_id").getAsString());
                    finish();
                    startActivity(il);
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.hideDialog();
                if (errorBody != null && !errorBody.equals(""))
                    ToastUtils.show(errorBody);
                else
                    ToastUtils.show(R.string.something_wrong);
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
