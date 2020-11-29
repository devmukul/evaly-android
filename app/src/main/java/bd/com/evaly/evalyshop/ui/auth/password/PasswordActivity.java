package bd.com.evaly.evalyshop.ui.auth.password;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.orhanobut.logger.Logger;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.roomdb.ProviderDatabase;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.preference.MyPreference;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordActivity extends BaseActivity implements SetPasswordView {

    @BindView(R.id.pin1Et)
    EditText pin1Et;
    @BindView(R.id.pin2Et)
    EditText pin2Et;
    @BindView(R.id.pin3Et)
    EditText pin3Et;
    @BindView(R.id.pin4Et)
    EditText pin4Et;
    @BindView(R.id.pin5Et)
    EditText pin5Et;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etConfirmPassword)
    EditText etConfirmPassword;
    @BindView(R.id.tvPasswordWarning)
    TextView tvPasswordWarning;

    ViewDialog dialog;
    private boolean isFromSignUp;
    private int size = 1;
    private String phoneNumber, password, requestId;
    private String name;
    private SetPasswordPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Set Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ViewDialog(this);

        phoneNumber = getIntent().getStringExtra("phone");
        name = getIntent().getStringExtra("name");
        requestId = getIntent().getStringExtra("request_id");
//        firstName = getIntent().getStringExtra("firstName");
//        lastName = getIntent().getStringExtra("lastName");

        presenter = new SetPasswordPresenterImpl(this, this);

        pin1Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pin1Et.getText().toString().length() == size)
                    pin2Et.requestFocus();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        pin2Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pin2Et.getText().toString().length() == size)
                    pin3Et.requestFocus();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (pin2Et.getText().toString().trim().length() == 0)
                    pin1Et.requestFocus();
            }

        });

        pin3Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pin3Et.getText().toString().length() == size)
                    pin4Et.requestFocus();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (pin3Et.getText().toString().trim().length() == 0)
                    pin2Et.requestFocus();
            }

        });

        pin4Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pin4Et.getText().toString().length() == size)
                    pin5Et.requestFocus();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (pin4Et.getText().toString().trim().length() == 0)
                    pin3Et.requestFocus();
            }
        });

        pin5Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (pin5Et.getText().toString().trim().length() == 0)
                    pin4Et.requestFocus();
            }
        });

    }

    @OnClick(R.id.btnResetPassword)
    void resetPassword() {
        dialog.showDialog();
        tvPasswordWarning.setVisibility(View.GONE);
        String otp = pin1Et.getText().toString().trim() + pin2Et.getText().toString().trim() + pin3Et.getText().toString().trim()
                + pin4Et.getText().toString().trim() + pin5Et.getText().toString().trim();
        presenter.setPassword(otp, etPassword.getText().toString().trim(), etConfirmPassword.getText().toString().trim(), phoneNumber, requestId);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onOTPEmpty() {
        dialog.hideDialog();
        Toast.makeText(PasswordActivity.this, "Please input your OTP verification code", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordEmpty() {
        dialog.hideDialog();
        etPassword.setError(getResources().getString(R.string.required));
    }

    @Override
    public void onConfirmPasswordEmpty() {
        dialog.hideDialog();
        etConfirmPassword.setError(getResources().getString(R.string.required));
    }

    @Override
    public void onPasswordMismatch() {
        dialog.hideDialog();
        Toast.makeText(PasswordActivity.this, "Confirmed password is not matched!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordSetSuccess() {
        password = etPassword.getText().toString();
        CredentialManager.saveUserName(phoneNumber);
        CredentialManager.savePassword(password);
        dialog.hideDialog();
        Toast.makeText(PasswordActivity.this, "Password set Successfully, Please login!", Toast.LENGTH_SHORT).show();
        AppController.logout(PasswordActivity.this);

        MyPreference.with(this).clearAll();
        Logger.d(CredentialManager.getToken());


        ProviderDatabase providerDatabase = ProviderDatabase.getInstance(this);
        providerDatabase.userInfoDao().deleteAll();

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, SignInActivity.class)
                    .putExtra("phone", phoneNumber)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }, 300);
    }

    @Override
    public void onShortPassword() {
        dialog.hideDialog();
        etPassword.setError("Password must have at least 8 characters!");
    }

    @Override
    public void onSpecialCharMiss() {
        dialog.hideDialog();
        tvPasswordWarning.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPasswordSetFailed(String msg) {
        dialog.hideDialog();
        Toast.makeText(PasswordActivity.this, msg, Toast.LENGTH_SHORT).show();

    }
}
