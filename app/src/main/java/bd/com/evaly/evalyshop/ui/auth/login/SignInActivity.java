package bd.com.evaly.evalyshop.ui.auth.login;

import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ActivitySignInNewBinding;
import bd.com.evaly.evalyshop.ui.auth.forgetPassword.ForgotPasswordActivity;
import bd.com.evaly.evalyshop.ui.auth.signup.SignUpActivity;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignInActivity extends BaseActivity<ActivitySignInNewBinding, SignInViewModel> {

    private boolean isShowing = false;
    private ViewDialog alert;

    public SignInActivity() {
        super(SignInViewModel.class, R.layout.activity_sign_in_new);
    }

    @Override
    protected void initViews() {

        alert = new ViewDialog(this);
        Intent data = getIntent();

        if (data.hasExtra("phone")) {
            binding.phoneNumber.setText(data.getStringExtra("phone"));
            binding.phoneNumber.clearFocus();
            binding.password.clearFocus();
            if (data.hasExtra("type")) {
                if (data.getStringExtra("type").equals("forgetpassword"))
                    ToastUtils.show("Password sent to your mobile. Please login using the new password");

                else if (data.getStringExtra("type").equals("signup"))
                    ToastUtils.show("Successfully signed up. Please check SMS for password.");
            }
        } else
            binding.phoneNumber.requestFocus();
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.showToast.observe(this, s -> ToastUtils.show(s));
        viewModel.hideLoading.observe(this, aVoid -> {
            if (alert.isShowing())
                alert.hideDialog();
        });
        viewModel.loginSuccess.observe(this, aVoid -> {
            Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "signin");
            startActivity(intent);
            finishAffinity();
        });
    }

    @Override
    protected void clickListeners() {

        binding.closeBtn.setOnClickListener(v -> onBackPressed());

        binding.signUp.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });

        binding.showPass.setOnClickListener(v -> {
            if (!isShowing) {
                isShowing = true;
                binding.password.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.showPass.setImageResource(R.drawable.ic_visibility_off);
            } else {
                isShowing = false;
                binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.showPass.setImageResource(R.drawable.ic_visibility);
            }
        });

        binding.forgot.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
            finish();
        });

        binding.signIn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.phoneNumber.getText().toString())) {
                ToastUtils.show("Please enter your phone number");
            } else if (TextUtils.isEmpty(binding.password.getText().toString())) {
                ToastUtils.show("Please enter your password");
            } else if (binding.phoneNumber.getText().toString().length() != 11) {
                ToastUtils.show("Please enter a valid phone number");
            } else {
                alert.showDialog();
                viewModel.signInUser(binding.phoneNumber.getText().toString(), binding.password.getText().toString());
            }
        });
    }

}
