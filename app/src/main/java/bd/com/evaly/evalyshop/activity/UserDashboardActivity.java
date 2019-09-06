package bd.com.evaly.evalyshop.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.orderDetails.PayViaBkashActivity;
import bd.com.evaly.evalyshop.adapter.AddressAdapter;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.Token;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class UserDashboardActivity extends BaseActivity {
    
    Context context;
    TextView name,balance,address;
    UserDetails userDetails;
    ImageView addAddress;
    EditText addressET;
    RecyclerView addressList;
    ArrayList<String> addresses;
    ArrayList<Integer> addressID;
    AddressAdapter addressAdapter;
    Map<String,String> map;
    String from="";
    ViewDialog alert;

    String userAgent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        context = this;
        
        // getSupportActionBar().setElevation(0);


        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }


        getSupportActionBar().setTitle("Dashboard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4f);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            from=extras.getString("from");
        }

        name=findViewById(R.id.name);
        balance=findViewById(R.id.balance);
        address = findViewById(R.id.address);
        userDetails=new UserDetails(this);
        alert=new ViewDialog(UserDashboardActivity.this);
        // getAddress();

        name.setText(userDetails.getFirstName()+" "+userDetails.getLastName());
        balance.setText("৳ "+ userDetails.getBalance());
        address.setText(userDetails.getJsonAddress());


        ImageView profilePicNav = findViewById(R.id.picture);


        if (userDetails.getProfilePicture() != null || !userDetails.getProfilePicture().isEmpty()) {
            Glide.with(this)
                    .asBitmap()
                    .load(userDetails.getProfilePicture())
                    .skipMemoryCache(true)
                    .fitCenter()
                    .optionalCenterCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(200, 200))
                    .into(profilePicNav);
        }




        LinearLayout orders = findViewById(R.id.order);
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderListActivity.class);
                startActivity(intent);
            }
        });


        LinearLayout notification = findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);


            }
        });
        
        
        LinearLayout addBalance = findViewById(R.id.addBalance);
        addBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, PayViaBkashActivity.class);
                startActivity(intent);


            }
        });


        LinearLayout transactionHistory = findViewById(R.id.transaction_history);
        transactionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, TransactionHistory.class);
                startActivity(intent);


            }
        });

        
        
        LinearLayout editProfile = findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditProfileActivity.class);
                startActivity(intent);
            }
        });



        LinearLayout editAddress = findViewById(R.id.addressClick);
        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        
        
        LinearLayout changePassword = findViewById(R.id.changePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ChangePasswordActivity.class);
                startActivity(intent);


            }
        });


    }



    @Override
    public void onResume(){

        super.onResume();

        Balance.update(this, balance);
        Token.update(this);



    }

    @Override
    public void onBackPressed() {
        if(from.equals("signin")){
            Intent intent=new Intent(UserDashboardActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else{
            super.onBackPressed();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.logout:

                userDetails.clearAll();
                startActivity(new Intent(UserDashboardActivity.this,MainActivity.class));
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);

    }






}
