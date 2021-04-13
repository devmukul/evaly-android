package bd.com.evaly.evalyshop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.ActivityForgotPasswordBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.auth.captcha.CaptchaResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.auth.password.PasswordActivity;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgotPasswordActivity extends BaseActivity {


    @Inject
    ApiRepository apiRepository;
    @Inject
    PreferenceRepository preferenceRepository;
    private ViewDialog dialog;
    private ActivityForgotPasswordBinding binding;
    private CaptchaResponse captchaModel;

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
            } else if (TextUtils.isEmpty(binding.captchaInput.getText().toString())) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter captcha verification code", Toast.LENGTH_SHORT).show();
            } else {
                dialog.showDialog();
                resetPassword();
            }
        });

        binding.closeBtn.setOnClickListener(v -> onBackPressed());
        binding.reloadCaptcha.setOnClickListener(view -> getCaptcha());

        getCaptcha();
    }

    private void resetPassword() {
        if (captchaModel == null){
            ToastUtils.show("Please reload the page");
            return;
        }
        HashMap<String, String> body = new HashMap<>();
        body.put("phone_number", binding.phone.getText().toString());
        body.put("captcha_id", captchaModel.getCaptchaId());
        body.put("captcha_value", binding.captchaInput.getText().toString().trim());
        body.put("service_name", "evaly");

        apiRepository.forgetPassword(body, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                dialog.hideDialog();
                Toast.makeText(ForgotPasswordActivity.this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                if (statusCode == 201 || statusCode == 200) {
                    Intent intent = new Intent(ForgotPasswordActivity.this, PasswordActivity.class);
                    intent.putExtra("phone", binding.phone.getText().toString());
                    intent.putExtra("request_id", response.get("data").getAsJsonObject().get("request_id").getAsString());
                    finish();
                    startActivity(intent);
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
                ToastUtils.show("Invalid captcha");
                dialog.hideDialog();
                getCaptcha();
            }
        });
    }

    private void getCaptcha() {
        apiRepository.getCaptcha(new ResponseListenerAuth<CommonDataResponse<CaptchaResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<CaptchaResponse> response, int statusCode) {
                captchaModel = response.getData();
                if (!isDestroyed() && !isFinishing())
                    Glide.with(AppController.getContext())
                            .asBitmap()
                            .load(captchaModel.getCaptcha())
                            .into(binding.captchaImage);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotPasswordActivity.this, SignInActivity.class));
        finish();
    }
}
