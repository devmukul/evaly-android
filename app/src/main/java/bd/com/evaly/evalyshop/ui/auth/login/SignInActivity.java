package bd.com.evaly.evalyshop.ui.auth.login;

import android.content.Intent;
import android.content.IntentSender;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ActivitySignInNewBinding;
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

    private CredentialsClient credentialClient;
    private CredentialRequest credentialRequest;
    private final int RC_SAVE = 1000;
    private final String TAG = "SignInActivity";

    public SignInActivity() {
        super(SignInViewModel.class, R.layout.activity_sign_in_new);
    }

    @Override
    protected void initViews() {

        alert = new ViewDialog(this);
        Intent data = getIntent();
        initCredential();
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
    }

    private void initCredential() {
        CredentialsOptions options = new CredentialsOptions.Builder()
                .forceEnableSaveDialog()
                .build();
        credentialClient = Credentials.getClient(this, options);

        credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .setAccountTypes(IdentityProviders.GOOGLE)
                .build();
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.showToast.observe(this, s -> ToastUtils.show(s));
        viewModel.hideLoading.observe(this, aVoid -> {
            if (alert.isShowing())
                alert.hideDialog();
        });
        viewModel.loginSuccess.observe(this, aVoid -> {
            saveAuthCredential(binding.phoneNumber.getText().toString(), binding.password.getText().toString());
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", "signin");
        startActivity(intent);
        finishAffinity();
    }

    private void saveAuthCredential(String phone, String password) {
        Credential credential = new Credential.Builder(phone)
                .setPassword(password)
                .setName(phone)
                .build();

        credentialClient.save(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("SignInActivity:", "SAVE: OK");
                Toast.makeText(this, "Credentials saved", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
                return;
            }

            Exception e = task.getException();
            if (e instanceof ResolvableApiException) {

                ResolvableApiException rae = (ResolvableApiException) e;
                try {
                    rae.startResolutionForResult(this, RC_SAVE);
                } catch (IntentSender.SendIntentException exception) {
                    Log.e(TAG, "Failed to send resolution.", exception);
                    Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                }
            } else {
                // Request has no resolution
                Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            }

        });
    }

    private void retrieveUserCredential() {
        credentialClient.request(credentialRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Credential credential = task.getResult().getCredential();
                updateCredentialForSingleUser(credential);
                return;
            } else {
                Log.e("credentialName", "Faield To Retrieve");
            }
        });
    }

    private void updateCredentialForSingleUser(Credential credential) {
        if (credential != null) {
            binding.phoneNumber.setText(credential.getId());
            binding.password.setText(credential.getPassword());
        }
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
        if (requestCode == RC_SAVE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "SAVE: OK");
                Toast.makeText(this, "Credentials saved", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "SAVE: Canceled by user");
            }
            navigateToMainActivity();
        }
    }
}
