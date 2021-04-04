package bd.com.evaly.evalyshop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.preference.ReferPref;
import bd.com.evaly.evalyshop.data.remote.ApiRepository;
import bd.com.evaly.evalyshop.databinding.ActivitySignupNewBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.auth.captcha.CaptchaResponse;
import bd.com.evaly.evalyshop.ui.auth.password.PasswordActivity;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignUpActivity extends BaseActivity {

    @Inject
    ApiRepository apiRepository;
    @Inject
    PreferenceRepository preferenceRepository;
    private ActivitySignupNewBinding binding;
    private ReferPref referPref;
    private CaptchaResponse captchaModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup_new);
        referPref = new ReferPref(this);
        binding.privacyText.setText(Html.fromHtml("I agree to the <a href=\"https://evaly.com.bd/about/privacy-policy\">Privacy Policy</a> and <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> of Evaly."));
        binding.privacyText.setMovementMethod(LinkMovementMethod.getInstance());

        binding.signUp.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.fName.getText().toString())) {
                Toast.makeText(SignUpActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.lName.getText().toString())) {
                Toast.makeText(SignUpActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.number.getText().toString())) {
                Toast.makeText(SignUpActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            } else if (binding.number.getText().toString().length() != 11) {
                Toast.makeText(SignUpActivity.this, "Please enter your phone number correctly", Toast.LENGTH_SHORT).show();
            } else if (!binding.checkBox.isChecked()) {
                Toast.makeText(SignUpActivity.this, "You must accept privacy policy and terms & conditions in order to sign up for Evaly", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(binding.captchaInput.getText().toString())) {
                Toast.makeText(SignUpActivity.this, "Please enter captcha verification code", Toast.LENGTH_SHORT).show();
            } else {
                signUpUser();
            }
        });

        binding.signinHolder.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        });

        binding.close.setOnClickListener(v -> onBackPressed());

        binding.reloadCaptcha.setOnClickListener(view -> getCaptcha());

        getCaptcha();
    }

    private void getCaptcha() {
        apiRepository.getCaptcha(new ResponseListenerAuth<CommonDataResponse<CaptchaResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<CaptchaResponse> response, int statusCode) {
                captchaModel = response.getData();
                Glide.with(getApplicationContext())
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

    public void signUpUser() {
        if (captchaModel == null) {
            ToastUtils.show("Please reload the page");
            return;
        }

        final ViewDialog alert = new ViewDialog(this);

        alert.showDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("first_name", binding.fName.getText().toString());
        hashMap.put("last_name", binding.lName.getText().toString());
        hashMap.put("phone_number", binding.number.getText().toString());
        hashMap.put("captcha_id", captchaModel.getCaptchaId());
        hashMap.put("captcha_value", binding.captchaInput.getText().toString().trim());
        hashMap.put("service_name", "evaly");

        apiRepository.register(hashMap, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                alert.hideDialog();
                if (statusCode == 200) {
                    Toast.makeText(getApplicationContext(), "This mobile number has already been used", Toast.LENGTH_LONG).show();
                } else if (statusCode == 201) {
                    Intent il = new Intent(SignUpActivity.this, PasswordActivity.class);
                    il.putExtra("phone", binding.number.getText().toString());
                    il.putExtra("name", binding.fName.getText().toString() + " " + binding.lName.getText().toString());
                    il.putExtra("request_id", response.get("data").getAsJsonObject().get("request_id").getAsString());
                    il.putExtra("type", "signup");
                    finish();
                    startActivity(il);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                alert.hideDialog();
                ToastUtils.show(errorBody);
            }

            @Override
            public void onAuthError(boolean logout) {
                alert.hideDialog();
                ToastUtils.show("Invalid captcha");
                getCaptcha();
            }
        });
    }


}
