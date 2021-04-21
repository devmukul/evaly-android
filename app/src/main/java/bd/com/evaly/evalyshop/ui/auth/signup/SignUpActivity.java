package bd.com.evaly.evalyshop.ui.auth.signup;

import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ActivitySignupNewBinding;
import bd.com.evaly.evalyshop.ui.auth.login.SignInActivity;
import bd.com.evaly.evalyshop.ui.auth.password.PasswordActivity;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignUpActivity extends BaseActivity<ActivitySignupNewBinding, SignUpViewModel> {

    private ViewDialog alert;

    public SignUpActivity() {
        super(SignUpViewModel.class, R.layout.activity_signup_new);
    }

    @Override
    protected void initViews() {
        alert = new ViewDialog(this);
        binding.privacyText.setText(Html.fromHtml("I agree to the <a href=\"https://evaly.com.bd/about/privacy-policy\">Privacy Policy</a> and <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> of Evaly."));
        binding.privacyText.setMovementMethod(LinkMovementMethod.getInstance());
        viewModel.getCaptcha();
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.captchaModelLive.observe(this, captchaResponse -> {
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(captchaResponse.getCaptcha())
                    .into(binding.captchaImage);
        });

        viewModel.signUpSuccess.observe(this, response -> {
            Intent intent = new Intent(SignUpActivity.this, PasswordActivity.class);
            intent.putExtra("phone", binding.number.getText().toString());
            intent.putExtra("name", binding.fName.getText().toString() + " " + binding.lName.getText().toString());
            intent.putExtra("request_id", response.get("data").getAsJsonObject().get("request_id").getAsString());
            intent.putExtra("type", "signup");
            finish();
            startActivity(intent);
        });

        viewModel.hideLoading.observe(this, aVoid -> {
            alert.hideDialog();
        });
    }

    @Override
    protected void clickListeners() {
        binding.signUp.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.fName.getText().toString())) {
                ToastUtils.show("Please enter your first name");
            } else if (TextUtils.isEmpty(binding.lName.getText().toString())) {
                ToastUtils.show("Please enter your last name");
            } else if (TextUtils.isEmpty(binding.number.getText().toString())) {
                ToastUtils.show("Please enter your phone number");
            } else if (binding.number.getText().toString().length() != 11) {
                ToastUtils.show("Please enter your phone number correctly");
            } else if (!binding.checkBox.isChecked()) {
                ToastUtils.show("You must accept privacy policy and terms & conditions in order to sign up for Evaly");
            } else if (TextUtils.isEmpty(binding.captchaInput.getText().toString())) {
                ToastUtils.show("Please enter captcha verification code");
            } else {

                alert.showDialog();
                viewModel.signUpUser(binding.fName.getText().toString(),
                        binding.lName.getText().toString(),
                        binding.number.getText().toString(),
                        binding.captchaInput.getText().toString().trim());
            }
        });

        binding.signinHolder.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        });

        binding.close.setOnClickListener(v -> onBackPressed());

        binding.reloadCaptcha.setOnClickListener(view -> viewModel.getCaptcha());
    }


}
