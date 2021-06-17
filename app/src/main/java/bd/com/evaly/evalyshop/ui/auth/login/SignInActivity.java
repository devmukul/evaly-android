package bd.com.evaly.evalyshop.ui.auth.login;

import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.google.android.gms.auth.api.credentials.Credential;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ActivitySignInNewBinding;
import bd.com.evaly.evalyshop.manager.credential.CredentialSaveListener;
import bd.com.evaly.evalyshop.manager.credential.CredentialManager;
import bd.com.evaly.evalyshop.ui.auth.forgetPassword.ForgotPasswordActivity;
import bd.com.evaly.evalyshop.ui.auth.signup.SignUpActivity;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignInActivity extends BaseActivity<ActivitySignInNewBinding, SignInViewModel> {

    private boolean isShowing = false;
    private ViewDialog alert;

    private final String TAG = "SignInActivity";

    public SignInActivity() {
        super(SignInViewModel.class, R.layout.activity_sign_in_new);
    }

    @Inject
    CredentialManager credentialManager;

    @Override
    protected void initViews() {

        alert = new ViewDialog(this);
        Intent data = getIntent();
        retrieveUserCredential();

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

        binding.cbRememberMe.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setRememberMe(isChecked);
        });
        binding.cbRememberMe.setChecked(viewModel.isRememberMeEnable());
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.showToast.observe(this, s -> ToastUtils.show(s));
        viewModel.hideLoading.observe(this, aVoid -> {
            if (alert.isShowing())
                alert.hideDialog();
        });
        viewModel.loginSuccess.observe(this, aVoid -> {
            saveAuthCredential();
        });

    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", "signin");
        startActivity(intent);
        finishAffinity();
    }

    private void saveAuthCredential() {
        if(!viewModel.isRememberMeEnable()){
            navigateToMainActivity();
            return;
        }
        credentialManager.saveCredential(
                binding.phoneNumber.getText().toString(),
                binding.password.getText().toString(),
                new CredentialSaveListener() {
                    @Override
                    public void onCredentialSave() {
                        navigateToMainActivity();
                        Toast.makeText(SignInActivity.this, "Credential Saved", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCredentialSaveError() {
                        navigateToMainActivity();
                    }
                }
        );
    }

    private void retrieveUserCredential() {
        if(viewModel.isRememberMeEnable()){
            credentialManager.retrieveUserCredential(this::updateCredentialToView);
        }
    }

    private void updateCredentialToView(Credential credential) {
        binding.phoneNumber.setText(credential.getId());
        binding.phoneNumber.setSelection(credential.getId().length()) ;
        binding.password.setText(credential.getPassword());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SAVE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "SAVE: OK");
                Toast.makeText(this, "Credentials saved", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "SAVE: Canceled by user");
            }
            navigateToMainActivity();
        }
        if (requestCode == Constants.RC_READ) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    updateCredentialToView(credential);
                }
            } else {
                Log.e(TAG, "Credential Read: NOT OK");
                Toast.makeText(this, "Credential Read Failed", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
