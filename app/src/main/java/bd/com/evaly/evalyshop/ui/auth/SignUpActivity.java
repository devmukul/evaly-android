package bd.com.evaly.evalyshop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.pref.ReferPref;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.auth.password.PasswordActivity;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class SignUpActivity extends BaseActivity {

    private EditText firstName, lastName, phoneNumber;
    private Button signUp;
    private LinearLayout signIn;
    private ImageView close;
    private ReferPref referPref;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_new);


        firstName = findViewById(R.id.f_name);
        lastName = findViewById(R.id.l_name);
        phoneNumber = findViewById(R.id.number);
        signUp = findViewById(R.id.sign_up);
        signIn = findViewById(R.id.signinHolder);
        close = findViewById(R.id.close);
        referPref = new ReferPref(this);
        TextView privacyText = findViewById(R.id.privacyText);

        privacyText.setText(Html.fromHtml("I agree to the <a href=\"https://evaly.com.bd/about/privacy-policy\">Privacy Policy</a> and <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> of Evaly."));
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());

        CheckBox checkBox = findViewById(R.id.checkBox);

        signUp.setOnClickListener(v -> {
            if (TextUtils.isEmpty(firstName.getText().toString())) {
                Toast.makeText(SignUpActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(lastName.getText().toString())) {
                Toast.makeText(SignUpActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
                Toast.makeText(SignUpActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            } else if (phoneNumber.getText().toString().length() != 11) {
                Toast.makeText(SignUpActivity.this, "Please enter your phone number correctly", Toast.LENGTH_SHORT).show();
            } else if (!checkBox.isChecked()) {
                Toast.makeText(SignUpActivity.this, "You must accept privacy policy and terms & conditions in order to sign up for Evaly", Toast.LENGTH_LONG).show();
            } else {
                signUpUser();
            }
        });

        signIn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        });

        close.setOnClickListener(v -> onBackPressed());
    }

    public void signUpUser() {


        final ViewDialog alert = new ViewDialog(this);

        alert.showDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("first_name", firstName.getText().toString());
        hashMap.put("last_name", lastName.getText().toString());
        hashMap.put("phone_number", phoneNumber.getText().toString());

        TextView ref = findViewById(R.id.referral);
        String refText = ref.getText().toString();
        if (!refText.equals(""))
            referPref.setRef(refText);

        AuthApiHelper.register(hashMap, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                alert.hideDialog();
                if (statusCode == 200) {
                    Toast.makeText(getApplicationContext(), "This mobile number has already been used", Toast.LENGTH_LONG).show();
                } else if (statusCode == 201) {
                    Intent il = new Intent(SignUpActivity.this, PasswordActivity.class);
                    il.putExtra("phone", phoneNumber.getText().toString());
                    il.putExtra("name", firstName.getText().toString() + " " + lastName.getText().toString());
                    il.putExtra("request_id", response.get("data").getAsJsonObject().get("request_id").getAsString());
                    il.putExtra("type", "signup");
                    finish();
                    startActivity(il);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                alert.hideDialog();
                ToastUtils.show(errorBody);
            }
        });
    }


}
