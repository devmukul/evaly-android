package bd.com.evaly.evalyshop.activity.password;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import java.util.HashMap;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.xmpp.SignupModel;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;

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

    private String phoneNumber, password;
    private String name;

    private SetPasswordPresenter presenter;

    private AppController mChatApp = AppController.getInstance();
    private XMPPHandler xmppHandler;

    private XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {

        //Event Listeners
        public void onConnected() {
            Logger.d("======   CONNECTED  -========");
            xmppHandler = AppController.getmService().xmpp;
            xmppHandler.autoLogin = false;
            if (password == null) {
                xmppHandler.Signup(new SignupModel(phoneNumber, etPassword.getText().toString(), etPassword.getText().toString()));
            } else {
                xmppHandler.Signup(new SignupModel(phoneNumber, password, password));
            }

        }

        public void onSignupSuccess() {
            Logger.d("SIGNUP SUCCESS");
            xmppHandler.autoLogin = true;
            CredentialManager.savePassword(password);
            xmppHandler.setUserPassword(phoneNumber, password);
            xmppHandler.login();

        }

        public void onLoggedIn() {
            Logger.d("LOGIN");
            xmppHandler.changePassword(etPassword.getText().toString());
            xmppHandler.disconnect();
            Snackbar.make(pin1Et, "Password set Successfully, Please login!", Snackbar.LENGTH_LONG).show();
            new Handler().postDelayed(() -> AppController.logout(PasswordActivity.this), 2000);
        }

        public void onLoginFailed(String msg) {
            Logger.d(msg);
            if (!msg.contains("already logged in")) {
                HashMap<String, String> data = new HashMap<>();
                data.put("user", phoneNumber);
                data.put("host", Constants.XMPP_HOST);
                data.put("newpass", etPassword.getText().toString());
                Logger.d("===============");
                AuthApiHelper.changeXmppPassword(data, new DataFetchingListener<Response<JsonPrimitive>>() {
                    @Override
                    public void onDataFetched(Response<JsonPrimitive> response) {
//                        Logger.d(new Gson().toJson(response));
                        dialog.hideDialog();
                        if (response.code() == 200 || response.code() == 201) {
                            onPasswordChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailed(int status) {
                        Logger.d("======-=-=-=-=-=-=");
                        dialog.hideDialog();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();

                    }
                });
            }
        }

        public void onSignupFailed(String error) {
            Logger.d(error);
            if (Constants.SIGNUP_ERR_CONFLICT.equalsIgnoreCase(error)) {
                xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
                xmppHandler.login();
                return;
            }
            xmppHandler.disconnect();
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        }

        public void onPasswordChanged() {
            dialog.hideDialog();
            Snackbar.make(pin1Et, "Password set Successfully, Please login!", Snackbar.LENGTH_LONG).show();
            xmppHandler.disconnect();
            AppController.logout(PasswordActivity.this);

//            xmppHandler.disconnect();
        }

        public void onPasswordChangeFailed(String msg) {
            dialog.hideDialog();
            Logger.d(msg);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Set Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ViewDialog(this);

        phoneNumber = getIntent().getStringExtra("phone");
        name = getIntent().getStringExtra("name");
//        firstName = getIntent().getStringExtra("firstName");
//        lastName = getIntent().getStringExtra("lastName");

        presenter = new SetPasswordPresenterImpl(this, this);

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
        tvPasswordWarning.setVisibility(View.GONE);
        String otp = pin1Et.getText().toString().trim() + pin2Et.getText().toString().trim() + pin3Et.getText().toString().trim()
                + pin4Et.getText().toString().trim() + pin5Et.getText().toString().trim();
        presenter.setPassword(otp, etPassword.getText().toString().trim(), etConfirmPassword.getText().toString().trim(), phoneNumber);
    }

    private void startXmppService() {
        if (!XMPPService.isServiceRunning) {
            Intent intent = new Intent(this, XMPPService.class);
            mChatApp.UnbindService();
            mChatApp.BindService(intent);
            Logger.d("++++++++++");
        } else {
            Logger.d("---------");
            xmppHandler = AppController.getmService().xmpp;
            if (!xmppHandler.isConnected()) {
                xmppHandler.connect();
            } else {
                xmppHandler.Signup(new SignupModel(phoneNumber, password, password));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatApp.getEventReceiver().setListener(xmppCustomEventListener);
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
        password = etPassword.getText().toString();
        CredentialManager.saveUserName(phoneNumber);
        startXmppService();

    }

    @Override
    public void onShortPassword() {
        dialog.hideDialog();
        etPassword.setError("At least 8 characters!");
    }

    @Override
    public void onSpecialCharMiss() {
        dialog.hideDialog();
        tvPasswordWarning.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPasswordSetFailed(String msg) {
        dialog.hideDialog();
        Snackbar.make(pin1Et, msg, Snackbar.LENGTH_LONG).show();
    }
}
