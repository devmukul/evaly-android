package bd.com.evaly.evalyshop.ui.auth.password;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseOldActivity;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.ViewDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PasswordActivity extends BaseOldActivity implements SetPasswordView {

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
    @BindView(R.id.show_new_pass)
    ImageView imageShowPassword;
    @BindView(R.id.show_confirm_pass)
    ImageView imageShowConfirmPassword;

    ViewDialog dialog;
    private boolean isFromSignUp;
    private int size = 1;
    private String phoneNumber, password, requestId;
    private String name;
    private SetPasswordPresenter presenter;
    @Inject
    ApiRepository apiRepository;
    @Inject
    PreferenceRepository preferenceRepository;
    private boolean isCurrentShowing, isNewShowing, isNewConfirmShowing;

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

        presenter = new SetPasswordPresenterImpl(this, this, apiRepository);

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

    @OnClick(R.id.show_new_pass)
    void onShowPassword() {
        if (!isNewShowing) {
            isNewShowing = true;
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            imageShowPassword.setImageResource(R.drawable.ic_visibility_off);
        } else {
            isNewShowing = false;
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageShowPassword.setImageResource(R.drawable.ic_visibility);
        }
    }

    @OnClick(R.id.show_confirm_pass)
    void onShowConfirmPassword() {
        if (!isNewConfirmShowing) {
            isNewConfirmShowing = true;
            etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            imageShowConfirmPassword.setImageResource(R.drawable.ic_visibility_off);
        } else {
            isNewConfirmShowing = false;
            etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageShowConfirmPassword.setImageResource(R.drawable.ic_visibility);
        }
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
        preferenceRepository.saveUserName(phoneNumber);
        preferenceRepository.savePassword(password);
        dialog.hideDialog();
        Toast.makeText(PasswordActivity.this, "Password set successfully!", Toast.LENGTH_SHORT).show();
        signInUser();
//        AppController.logout(PasswordActivity.this);
//
//        MyPreference.with(this).clearAll();
//        ProviderDatabase providerDatabase = ProviderDatabase.getInstance(this);
//        providerDatabase.userInfoDao().deleteAll();
//
//        new Handler().postDelayed(() -> {
//            startActivity(new Intent(this, SignInActivity.class)
//                    .putExtra("phone", phoneNumber)
//                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
//            finish();
//        }, 300);
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

    public void signInUser() {
        dialog.showDialog();
        HashMap<String, String> payload = new HashMap<>();
        payload.put("phone_number", preferenceRepository.getUserName());
        payload.put("password", preferenceRepository.getPassword());

        apiRepository.login(payload, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int code) {
                switch (code) {
                    case 200:
                    case 201:
                    case 202:
                        response = response.get("data").getAsJsonObject();
                        String token = response.get("access_token").getAsString();
                        preferenceRepository.saveToken(token);
                        preferenceRepository.saveRefreshToken(response.get("refresh_token").getAsString());
                        Balance.updateUserInfo(PasswordActivity.this, true, apiRepository, preferenceRepository);
                        Toast.makeText(PasswordActivity.this, "Successfully signed in.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(PasswordActivity.this, "Incorrect phone number or password. Please try again! ", Toast.LENGTH_SHORT).show();
                }
                dialog.hideDialog();
            }

            @Override
            public void onFailed(String body, int status) {
                dialog.hideDialog();
                Toast.makeText(PasswordActivity.this, body, Toast.LENGTH_SHORT).show();
            }

        });
    }
}
