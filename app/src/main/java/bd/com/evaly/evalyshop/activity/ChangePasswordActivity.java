package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;

public class ChangePasswordActivity extends BaseActivity {

    EditText oldPassword, newPassword, confirmPassword;
    Button changePassword;
    UserDetails userDetails;
    ImageView showCurrent, showNew, showNewConfirm;
    boolean isCurrentShowing, isNewShowing, isNewConfirmShowing;

    String userAgent;
    ViewDialog dialog;

    private AppController mChatApp = AppController.getInstance();
    private XMPPHandler xmppHandler;

    private XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener(){

        //Event Listeners
        public void onConnected() {
            xmppHandler = AppController.getmService().xmpp;
            xmppHandler.setUserPassword(CredentialManager.getUserName(), oldPassword.getText().toString());
            xmppHandler.login();

            Logger.d("======   CONNECTED  -========");
        }

        public void onLoggedIn(){
            xmppHandler.changePassword(newPassword.getText().toString());
        }

        public void onPasswordChanged(){
            dialog.hideDialog();
            Snackbar.make(newPassword, "Password change successfully, Please Login!", Snackbar.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppController.logout(ChangePasswordActivity.this);
                }
            }, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }
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

        showNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNewShowing) {
                    isNewShowing = !isNewShowing;
                    newPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    showNew.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    isNewShowing = !isNewShowing;
                    newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showNew.setImageResource(R.drawable.ic_visibility);
                }
            }
        });

        showCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCurrentShowing) {
                    isCurrentShowing = !isCurrentShowing;
                    oldPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    showCurrent.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    isCurrentShowing = !isCurrentShowing;
                    oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showCurrent.setImageResource(R.drawable.ic_visibility);
                }
            }
        });

        showNewConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNewConfirmShowing) {
                    isNewConfirmShowing = !isNewConfirmShowing;
                    confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    showNewConfirm.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    isNewConfirmShowing = !isNewConfirmShowing;
                    confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showNewConfirm.setImageResource(R.drawable.ic_visibility);
                }
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                } else if(!Utils.isStrongPassword(confirmPassword.getText().toString()).equals("yes")) {
                    Toast.makeText(ChangePasswordActivity.this, Utils.isStrongPassword(confirmPassword.getText().toString()), Toast.LENGTH_SHORT).show();
                } else {


                    dialog.showDialog();


                    JSONObject parameters = new JSONObject();
                    try {

                        parameters.put("new_password", newPassword.getText().toString());
                        parameters.put("old_password", oldPassword.getText().toString());
                    } catch (Exception e) {

                    }

                    Log.d("json", parameters.toString());

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtils.CHANGE_PASSWORD, parameters, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            dialog.hideDialog();

                            Log.d("change_password", response.toString());

                            try {

                                Toast.makeText(ChangePasswordActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                if (response.getBoolean("success")){
                                    CredentialManager.savePassword(newPassword.getText().toString());
                                    startXmppService();
                                }


                            } catch (Exception e) {

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                if (error.networkResponse.statusCode == 401) {

                                    AuthApiHelper.refreshToken(ChangePasswordActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                                        @Override
                                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                                            Toast.makeText(ChangePasswordActivity.this, "Please try again now", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailed(int status) {

                                        }
                                    });

                                    return;

                                }
                            }

                            try {

                                dialog.hideDialog();
                                Toast.makeText(ChangePasswordActivity.this, "Couldn't change password.", Toast.LENGTH_SHORT).show();


                                error.printStackTrace();
                            } catch (Exception e) {

                            }
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {

                            Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Bearer " + userDetails.getToken());

                            return headers;
                        }
                    };
                    request.setShouldCache(false);
                    request.setRetryPolicy(new DefaultRetryPolicy(50000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueue queue = Volley.newRequestQueue(ChangePasswordActivity.this);
                    queue.add(request);
                }
            }
        });

    }

    private void startXmppService() {
        if( !XMPPService.isServiceRunning ) {
            Intent intent = new Intent(this, XMPPService.class);
            mChatApp.UnbindService();
            mChatApp.BindService(intent);
            Logger.d("++++++++++");
        } else {
            Logger.d("---------");
            xmppHandler = AppController.getmService().xmpp;
            if(!xmppHandler.isConnected()){
                xmppHandler.connect();
            } else {
                xmppHandler.changePassword(newPassword.getText().toString());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatApp.getEventReceiver().setListener(xmppCustomEventListener);

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
