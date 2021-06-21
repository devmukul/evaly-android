package bd.com.evaly.evalyshop.ui.auth.forgetPassword;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.credentials.Credential;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ActivityForgotPasswordBinding;
import bd.com.evaly.evalyshop.manager.credential.CredentialManager;
import bd.com.evaly.evalyshop.ui.auth.login.SignInActivity;
import bd.com.evaly.evalyshop.ui.auth.password.PasswordActivity;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgotPasswordActivity extends BaseActivity<ActivityForgotPasswordBinding, ForgetPasswordViewModel> {

    private ViewDialog dialog;

    @Inject
    CredentialManager credentialManager ;

    public ForgotPasswordActivity() {
        super(ForgetPasswordViewModel.class, R.layout.activity_forgot_password);
    }

    @Override
    protected void initViews() {
        dialog = new ViewDialog(ForgotPasswordActivity.this);
        viewModel.getCaptcha();
        credentialManager.requestHint();
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.captchaModelLive.observe(this, captchaResponse -> {
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(captchaResponse.getCaptcha())
                    .into(binding.captchaImage);
        });

        viewModel.successLiveData.observe(this, response -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, PasswordActivity.class);
            intent.putExtra("phone", binding.phone.getText().toString());
            intent.putExtra("request_id", response.get("data").getAsJsonObject().get("request_id").getAsString());
            finish();
            startActivity(intent);
        });

        viewModel.hideLoading.observe(this, aVoid -> {
            dialog.hideDialog();
        });
    }

    @Override
    protected void clickListeners() {
        binding.reset.setOnClickListener(v -> {
            if (binding.phone.getText().toString().equals("")) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter number", Toast.LENGTH_SHORT).show();
            } else if (binding.phone.getText().toString().length() != 11) {
                Toast.makeText(ForgotPasswordActivity.this, "The length of number must be 11", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.captchaInput.getText().toString())) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter captcha verification code", Toast.LENGTH_SHORT).show();
            } else {
                dialog.showDialog();
                viewModel.resetPassword(binding.phone.getText().toString(), binding.captchaInput.getText().toString().trim());
            }
        });

        binding.closeBtn.setOnClickListener(v -> onBackPressed());
        binding.reloadCaptcha.setOnClickListener(view -> viewModel.getCaptcha());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotPasswordActivity.this, SignInActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PHONE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    updateCredentialToView(credential);
                }
            }
        }
    }

    private void updateCredentialToView(Credential credential) {
        if(credential.getId() != null){
            String number = credential.getId() ;
            if(number.contains("+88")){
                number = number.replace("+88", "");
            }
            binding.phone.setText(number);
            binding.phone.setSelection(number.length()) ;
        }
    }
}
