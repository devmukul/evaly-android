package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.password.PasswordActivity;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class SignUpActivity extends BaseActivity {

    EditText firstName, lastName, phoneNumber;
    Button signUp;
    LinearLayout signIn;
    ImageView close;
    UserDetails userDetails;

    String userAgent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }

        firstName = findViewById(R.id.f_name);
        lastName = findViewById(R.id.l_name);
        phoneNumber = findViewById(R.id.number);
        signUp = findViewById(R.id.sign_up);
        signIn = findViewById(R.id.sign_in);
        close = findViewById(R.id.close);
        userDetails = new UserDetails(this);



        TextView privacyText = findViewById(R.id.privacyText);

        privacyText.setText(Html.fromHtml("I agree to the <a href=\"https://evaly.com.bd/about/privacy-policy\">Privacy Policy</a> and <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> of Evaly."));
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());

        CheckBox checkBox = findViewById(R.id.checkBox);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(firstName.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(lastName.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                } else if (phoneNumber.getText().toString().length() != 11) {
                    Toast.makeText(SignUpActivity.this, "Please enter your phone number correctly", Toast.LENGTH_SHORT).show();
                } else if (!checkBox.isChecked()){
                    Toast.makeText(SignUpActivity.this, "You must accept privacy policy and terms & conditions in order to sign up for Evaly", Toast.LENGTH_LONG).show();
                } else {
                    signUpUser();
                }
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
            userDetails.setRef(refText);


        AuthApiHelper.register(hashMap, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
            @Override
            public void onDataFetched(retrofit2.Response<JsonObject> response) {
                alert.hideDialog();
                if (response.code() == 200){
                    Toast.makeText(getApplicationContext(), "This mobile number has already been used", Toast.LENGTH_LONG).show();
                }else if (response.code() ==201){

                    Intent il = new Intent(SignUpActivity.this, PasswordActivity.class);
                    il.putExtra("phone", phoneNumber.getText().toString());
                    il.putExtra("name", firstName.getText().toString()+" "+lastName.getText().toString());
                    il.putExtra("type", "signup");
                    finish();
                    startActivity(il);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(int status) {
                alert.hideDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
            }
        });


    }


}
