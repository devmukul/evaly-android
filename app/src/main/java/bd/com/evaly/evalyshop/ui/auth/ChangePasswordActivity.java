package bd.com.evaly.evalyshop.ui.auth;

import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseActivity {

    String userAgent;
    ViewDialog dialog;
    private EditText oldPassword, newPassword, confirmPassword;
    private Button changePassword;
    private UserDetails userDetails;
    private ImageView showCurrent, showNew, showNewConfirm;
    private boolean isCurrentShowing, isNewShowing, isNewConfirmShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        oldPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.newPasswordConfirmation);
        changePassword = findViewById(R.id.change);
        showCurrent = findViewById(R.id.show_current_pass);
        showNew = findViewById(R.id.show_new_pass);
        showNewConfirm = findViewById(R.id.show_new_pass_confirm);
        userDetails = new UserDetails(this);

        Log.d("token", userDetails.getToken());

        dialog = new ViewDialog(this);

        showNew.setOnClickListener(v -> {
            if (!isNewShowing) {
                isNewShowing = true;
                newPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                showNew.setImageResource(R.drawable.ic_visibility_off);
            } else {
                isNewShowing = false;
                newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showNew.setImageResource(R.drawable.ic_visibility);
            }
        });

        showCurrent.setOnClickListener(v -> {
            if (!isCurrentShowing) {
                isCurrentShowing = !isCurrentShowing;
                oldPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                showCurrent.setImageResource(R.drawable.ic_visibility_off);
            } else {
                isCurrentShowing = !isCurrentShowing;
                oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showCurrent.setImageResource(R.drawable.ic_visibility);
            }
        });

        showNewConfirm.setOnClickListener(v -> {
            if (!isNewConfirmShowing) {
                isNewConfirmShowing = !isNewConfirmShowing;
                confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                showNewConfirm.setImageResource(R.drawable.ic_visibility_off);
            } else {
                isNewConfirmShowing = !isNewConfirmShowing;
                confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showNewConfirm.setImageResource(R.drawable.ic_visibility);
            }
        });

        changePassword.setOnClickListener(v -> {
            System.out.println(userDetails.getToken());
            Log.d("old_password", oldPassword.getText().toString());
            Log.d("new_password", newPassword.getText().toString());
            if (oldPassword.getText().toString().equals("")) {
                Toast.makeText(ChangePasswordActivity.this, "Please enter your current password", Toast.LENGTH_SHORT).show();
            } else if (newPassword.getText().toString().equals("")) {
                Toast.makeText(ChangePasswordActivity.this, "Please enter your new password", Toast.LENGTH_SHORT).show();
            } else if (confirmPassword.getText().toString().equals("")) {
                Toast.makeText(ChangePasswordActivity.this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
            } else if (!confirmPassword.getText().toString().equals(newPassword.getText().toString())) {
                Toast.makeText(ChangePasswordActivity.this, "Password didn't match. Please enter your new password again.", Toast.LENGTH_SHORT).show();
            } else if (!Utils.isStrongPassword(confirmPassword.getText().toString()).equals("yes")) {
                Toast.makeText(ChangePasswordActivity.this, Utils.isStrongPassword(confirmPassword.getText().toString()), Toast.LENGTH_SHORT).show();
            } else {
                updatePassword();
            }

        });

    }


    private void updatePassword() {

        dialog.showDialog();

        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("new_password", newPassword.getText().toString());
        parameters.put("old_password", oldPassword.getText().toString());

        AuthApiHelper.changePassword(CredentialManager.getToken(), parameters, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                dialog.hideDialog();

                Toast.makeText(ChangePasswordActivity.this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                if (response.get("success").getAsBoolean()) {
                    CredentialManager.savePassword(newPassword.getText().toString());
                    HashMap<String, String> data = new HashMap<>();
                    data.put("user", CredentialManager.getUserName());
                    data.put("host", Constants.XMPP_HOST);
                    data.put("newpass", newPassword.getText().toString());
                    AuthApiHelper.changeXmppPassword(data, new DataFetchingListener<Response<JsonPrimitive>>() {
                        @Override
                        public void onDataFetched(Response<JsonPrimitive> response) {
                            dialog.hideDialog();
                            if (response.code() == 200 || response.code() == 201) {
                                Snackbar.make(newPassword, "Password change successfully, Please Login!", Snackbar.LENGTH_LONG).show();
                                new Handler().postDelayed(() -> AppController.logout(ChangePasswordActivity.this), 2000);
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
                Toast.makeText(ChangePasswordActivity.this, "Couldn't change password.", Toast.LENGTH_SHORT).show();
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
