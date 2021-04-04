package bd.com.evaly.evalyshop.ui.auth;

import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.remote.ApiRepository;
import bd.com.evaly.evalyshop.databinding.ActivityChangePasswordBinding;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Response;

@AndroidEntryPoint
public class ChangePasswordActivity extends BaseActivity {


    @Inject
    ApiRepository apiRepository;
    @Inject
    PreferenceRepository preferenceRepository;

    private ViewDialog dialog;
    private ActivityChangePasswordBinding binding;
    private boolean isCurrentShowing, isNewShowing, isNewConfirmShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);

        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ViewDialog(this);

        binding.showCurrentPass.setOnClickListener(v -> {
            if (!isCurrentShowing) {
                isCurrentShowing = true;
                binding.currentPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.showCurrentPass.setImageResource(R.drawable.ic_visibility_off);
            } else {
                isCurrentShowing = false;
                binding.currentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.showCurrentPass.setImageResource(R.drawable.ic_visibility);
            }
        });

        binding.showNewPass.setOnClickListener(v -> {
            if (!isNewShowing) {
                isNewShowing = true;
                binding.newPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.showNewPass.setImageResource(R.drawable.ic_visibility_off);
            } else {
                isNewShowing = false;
                binding.newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.showNewPass.setImageResource(R.drawable.ic_visibility);
            }
        });

        binding.showNewPassConfirm.setOnClickListener(v -> {
            if (!isNewConfirmShowing) {
                isNewConfirmShowing = true;
                binding.newPasswordConfirmation.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.showNewPassConfirm.setImageResource(R.drawable.ic_visibility_off);
            } else {
                isNewConfirmShowing = false;
                binding.newPasswordConfirmation.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.showNewPassConfirm.setImageResource(R.drawable.ic_visibility);
            }
        });

        binding.change.setOnClickListener(v -> {
            String oldPass = binding.currentPassword.getText().toString().trim();
            String newPass = binding.newPassword.getText().toString().trim();
            String confirmPass = binding.newPasswordConfirmation.getText().toString().trim();
            if (oldPass.equals("")) {
                ToastUtils.show("Please enter your current password");
            } else if (newPass.equals("")) {
                ToastUtils.show("Please enter your new password");
            } else if (confirmPass.equals("")) {
                ToastUtils.show("Please confirm your new password");
            } else if (!confirmPass.equals(newPass)) {
                ToastUtils.show("Password didn't match. Please enter your new password again.");
            } else if (!Utils.isStrongPassword(confirmPass).equals("yes")) {
                ToastUtils.show(Utils.isStrongPassword(confirmPass));
            } else {
                updatePassword();
            }
        });
    }


    private void updatePassword() {

        dialog.showDialog();
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("new_password", binding.newPassword.getText().toString());
        parameters.put("old_password", binding.currentPassword.getText().toString());

        apiRepository.changePassword(preferenceRepository.getToken(), parameters, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                dialog.hideDialog();
                ToastUtils.show(response.get("message").getAsString());
                if (response.get("success").getAsBoolean()) {
                    preferenceRepository.savePassword(binding.newPassword.getText().toString());
                    HashMap<String, String> data = new HashMap<>();
                    data.put("user", preferenceRepository.getUserName());
                    data.put("host", Constants.XMPP_HOST);
                    data.put("newpass", binding.newPassword.getText().toString());
                    AuthApiHelper.changeXmppPassword(data, new DataFetchingListener<Response<JsonPrimitive>>() {
                        @Override
                        public void onDataFetched(Response<JsonPrimitive> response) {
                            dialog.hideDialog();
                            if (response.code() == 200 || response.code() == 201) {
                                preferenceRepository.savePassword(binding.newPassword.getText().toString());
                                Snackbar.make(binding.newPassword, "Password change successfully!", Snackbar.LENGTH_LONG).show();
                                signInUser();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailed(int status) {
                            dialog.hideDialog();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.hideDialog();
                if (errorBody != null && !errorBody.equals(""))
                    ToastUtils.show(errorBody);
                else
                    ToastUtils.show("Couldn't change password.");
            }

            @Override
            public void onAuthError(boolean logout) {

                if (logout)
                    AppController.logout(ChangePasswordActivity.this);
                else
                    updatePassword();
            }
        });
    }


    public void signInUser() {
        dialog.showDialog();
        HashMap<String, String> payload = new HashMap<>();
        payload.put("phone_number", preferenceRepository.getUserName());
        payload.put("password", preferenceRepository.getPassword());

        apiRepository.login(payload, new ResponseListenerAuth<JsonObject, String>() {
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
                        Balance.updateUserInfo(ChangePasswordActivity.this, true);
                        ToastUtils.show("Successfully signed in.");
                        break;
                    default:
                        ToastUtils.show("Incorrect phone number or password. Please try again! ");
                }
                dialog.hideDialog();
            }

            @Override
            public void onFailed(String body, int status) {
                dialog.hideDialog();
                ToastUtils.show(body);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
