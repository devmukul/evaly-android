package bd.com.evaly.evalyshop.ui.auth.password;

import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.ActivityPasswordBinding;
import bd.com.evaly.evalyshop.listener.OtpReceiverListener;
import bd.com.evaly.evalyshop.manager.credential.CredentialManager;
import bd.com.evaly.evalyshop.manager.credential.CredentialSaveListener;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.service.SmsBroadcastReceiver;
import bd.com.evaly.evalyshop.ui.auth.changePassword.ChangePasswordActivity;
import bd.com.evaly.evalyshop.ui.auth.login.SignInViewModel;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PasswordActivity extends BaseActivity<ActivityPasswordBinding, PasswordViewModel> implements OtpReceiverListener {

    @Inject
    ApiRepository apiRepository;
    @Inject
    PreferenceRepository preferenceRepository;

    @Inject
    CredentialManager credentialManager ;

    SignInViewModel signInViewModel ;

    ViewDialog dialog;
    private int size = 1;
    private String phoneNumber, password, requestId;
    private boolean isNewShowing, isNewConfirmShowing;

    public PasswordActivity() {
        super(PasswordViewModel.class, R.layout.activity_password);
    }

    @Override
    protected void initViews() {
        setupSmsRetrieverClient();
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Set Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog = new ViewDialog(this);
        phoneNumber = getIntent().getStringExtra("phone");
        requestId = getIntent().getStringExtra("request_id");

        SmsBroadcastReceiver.otpReceiverListener = this ;
        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        binding.pin1Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.pin1Et.getText().toString().length() == size)
                    binding.pin2Et.requestFocus();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        binding.pin2Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.pin2Et.getText().toString().length() == size)
                    binding.pin3Et.requestFocus();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (binding.pin2Et.getText().toString().trim().length() == 0)
                    binding.pin1Et.requestFocus();
            }

        });

        binding.pin3Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.pin3Et.getText().toString().length() == size)
                    binding.pin4Et.requestFocus();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (binding.pin3Et.getText().toString().trim().length() == 0)
                    binding.pin2Et.requestFocus();
            }

        });

        binding.pin4Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.pin4Et.getText().toString().length() == size)
                    binding.pin5Et.requestFocus();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (binding.pin4Et.getText().toString().trim().length() == 0)
                    binding.pin3Et.requestFocus();
            }
        });

        binding.pin5Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (binding.pin5Et.getText().toString().trim().length() == 0)
                    binding.pin4Et.requestFocus();
            }
        });
        Log.e("passwordActvity", "true");
    }

    @Override
    protected void liveEventsObservers() {

        viewModel.loginSuccess.observe(this, aVoid -> {
            dialog.hideDialog();
            saveAuthCredential();
        });

        viewModel.loadingDialog.observe(this, aBoolean -> {
            if (aBoolean)
                dialog.showDialog();
            else
                dialog.hideDialog();
        });

        viewModel.successLiveData.observe(this, aVoid -> onPasswordSetSuccess());

    }

    @Override
    protected void clickListeners() {
        binding.showConfirmPass.setOnClickListener(v -> {
            if (!isNewConfirmShowing) {
                isNewConfirmShowing = true;
                binding.etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.showConfirmPass.setImageResource(R.drawable.ic_visibility_off);
            } else {
                isNewConfirmShowing = false;
                binding.etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.showConfirmPass.setImageResource(R.drawable.ic_visibility);
            }
        });

        binding.showNewPass.setOnClickListener(v -> {
            if (!isNewShowing) {
                isNewShowing = true;
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.showNewPass.setImageResource(R.drawable.ic_visibility_off);
            } else {
                isNewShowing = false;
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.showNewPass.setImageResource(R.drawable.ic_visibility);
            }
        });

        binding.btnResetPassword.setOnClickListener(v -> {
            dialog.showDialog();
            String confirmPassword = binding.etConfirmPassword.getText().toString();
            password = binding.etPassword.getText().toString();
            binding.tvPasswordWarning.setVisibility(View.GONE);
            String otp = binding.pin1Et.getText().toString().trim() + binding.pin2Et.getText().toString().trim() + binding.pin3Et.getText().toString().trim()
                    + binding.pin4Et.getText().toString().trim() + binding.pin5Et.getText().toString().trim();

            if (otp.trim().isEmpty() || otp.trim().length() < 5) {
                onOTPEmpty();
            } else if (password.trim().isEmpty()) {
                onPasswordEmpty();
            } else if (confirmPassword.trim().isEmpty()) {
                onConfirmPasswordEmpty();
            } else if (!confirmPassword.equals(password)) {
                onPasswordMismatch();
            } else if (password.trim().length() < 8) {
                onShortPassword();
            } else if (!Utils.isStrongPassword(password.trim()).equals("yes")) {
                onPasswordSetFailed(Utils.isStrongPassword(password.trim()));
            } else {

                HashMap<String, String> data = new HashMap<>();
                data.put("otp", otp);
                data.put("password", password);
                data.put("phone_number", phoneNumber);
                data.put("request_id", requestId);
                viewModel.setPassword(data);
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onOTPEmpty() {
        dialog.hideDialog();
        Toast.makeText(PasswordActivity.this, "Please input your OTP verification code", Toast.LENGTH_SHORT).show();
    }

    public void onPasswordEmpty() {
        dialog.hideDialog();
        binding.etPassword.setError(getResources().getString(R.string.required));
    }

    public void onConfirmPasswordEmpty() {
        dialog.hideDialog();
        binding.etConfirmPassword.setError(getResources().getString(R.string.required));
    }

    public void onPasswordMismatch() {
        dialog.hideDialog();
        Toast.makeText(PasswordActivity.this, "Confirmed password is not matched!", Toast.LENGTH_SHORT).show();
    }

    public void onPasswordSetSuccess() {
        password = binding.etPassword.getText().toString();
        preferenceRepository.saveUserName(phoneNumber);
        preferenceRepository.savePassword(password);
        Toast.makeText(PasswordActivity.this, "Password set successfully!", Toast.LENGTH_SHORT).show();
        dialog.showDialog();
        viewModel.signInUser();
    }

    public void onShortPassword() {
        dialog.hideDialog();
        binding.etPassword.setError("Password must have at least 8 characters!");
    }

    public void onPasswordSetFailed(String msg) {
        dialog.hideDialog();
        Toast.makeText(PasswordActivity.this, msg, Toast.LENGTH_SHORT).show();

    }

    public void setupSmsRetrieverClient() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);

        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(aVoid -> {
        });

        task.addOnFailureListener(e -> {
        });

    }

    @Override
    public void onOtpReceive(String otp) {
        try {
            if(otp != null & binding != null) {
                String[] splitedOtp = otp.split("(?!^)");
                if(splitedOtp.length == 5){
                    binding.pin1Et.setText(splitedOtp[0]);
                    binding.pin2Et.setText(splitedOtp[1]);
                    binding.pin3Et.setText(splitedOtp[2]);
                    binding.pin4Et.setText(splitedOtp[3]);
                    binding.pin5Et.setText(splitedOtp[4]);
                    binding.pin5Et.setSelection(1);
                }
            }
        }catch (Exception exp) {
            Log.e("OtpReadExp:", exp.toString());
        }
    }

    @Override
    protected void onDestroy() {
        SmsBroadcastReceiver.otpReceiverListener = null ;
        super.onDestroy();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", "signin");
        startActivity(intent);
        finishAffinity();
    }

    private void saveAuthCredential() {
        if(!signInViewModel.isRememberMeEnable()){
            navigateToMainActivity();
            return;
        }
        credentialManager.saveCredential(
                signInViewModel.getUserPhone(),
                binding.etPassword.getText().toString(),
                new CredentialSaveListener() {
                    @Override
                    public void onCredentialSave() {
                        navigateToMainActivity();
                        Toast.makeText(PasswordActivity.this, "Credential Saved", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCredentialSaveError() {
                        navigateToMainActivity();
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SAVE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Credentials saved", Toast.LENGTH_SHORT).show();
            }
            navigateToMainActivity();
        }
    }
}
