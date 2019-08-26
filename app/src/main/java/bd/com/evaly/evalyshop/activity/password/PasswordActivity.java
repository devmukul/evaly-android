package bd.com.evaly.evalyshop.activity.password;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.SignInActivity;
import bd.com.evaly.evalyshop.activity.UserDashboardActivity;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
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

    ViewDialog dialog;

    private UserDetails userDetails;

    private int size = 1;

    private String phoneNumber;

    private SetPasswordPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Set Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ViewDialog(this);

        phoneNumber = getIntent().getStringExtra("phone");

        presenter = new SetPasswordPresenterImpl(this, this);

        userDetails=new UserDetails(this);

        pin1Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (pin1Et.getText().toString().length() == size) {
                    pin2Et.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        pin2Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (pin2Et.getText().toString().length() == size) {
                    pin3Et.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (pin2Et.getText().toString().trim().length() == 0) {
                    pin1Et.requestFocus();
                }
            }

        });

        pin3Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (pin3Et.getText().toString().length() == size) {
                    pin4Et.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (pin3Et.getText().toString().trim().length() == 0) {
                    pin2Et.requestFocus();
                }
            }

        });

        pin4Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (pin4Et.getText().toString().length() == size) {
                    pin5Et.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (pin4Et.getText().toString().trim().length() == 0) {
                    pin3Et.requestFocus();
                }
            }

        });

        pin5Et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (pin5Et.getText().toString().trim().length() == 0) {
                    pin4Et.requestFocus();
                }
            }

        });
    }

    @OnClick(R.id.btnResetPassword)
    void resetPassword() {
        dialog.showDialog();
        String otp = pin1Et.getText().toString().trim() + pin2Et.getText().toString().trim() + pin3Et.getText().toString().trim()
                + pin4Et.getText().toString().trim() + pin5Et.getText().toString().trim();
        presenter.setPassword(otp, etPassword.getText().toString().trim(), etConfirmPassword.getText().toString().trim(), phoneNumber);
    }

    @Override
    public void onBackPressed() {
        finish();
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
        Snackbar.make(pin1Et, "Please input your otp", Snackbar.LENGTH_LONG).show();
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
        Snackbar.make(pin1Et, "Password mismatch", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPasswordSetSuccess() {
        dialog.hideDialog();
        Snackbar.make(pin1Et, "Password set Successfully, Please login!", Snackbar.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                userDetails.clearAll();
                startActivity(new Intent(PasswordActivity.this, SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finishAffinity();
            }
        }, 2000);
    }

    @Override
    public void onShortPassword() {
        dialog.hideDialog();
        etPassword.setError("At least 8 characters!");
    }

    @Override
    public void onPasswordSetFailed(String msg) {
        dialog.hideDialog();
        Snackbar.make(pin1Et, msg, Snackbar.LENGTH_LONG).show();
    }
}
