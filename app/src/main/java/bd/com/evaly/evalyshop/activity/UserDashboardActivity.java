package bd.com.evaly.evalyshop.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.AddressAdapter;
import bd.com.evaly.evalyshop.util.Token;
import bd.com.evaly.evalyshop.util.User;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class UserDashboardActivity extends BaseActivity {
    
    Context context;
    TextView name,balance;
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
        addAddress=findViewById(R.id.addAddress);
        addressList=findViewById(R.id.list);
        addressList.setLayoutManager(new LinearLayoutManager(this));
        addresses=new ArrayList<>();
        addressID=new ArrayList<>();
        map=new HashMap<>();
        userDetails=new UserDetails(this);
        alert=new ViewDialog(UserDashboardActivity.this);
        addressAdapter = new AddressAdapter(this, addresses,addressID, UserDashboardActivity.this);
        addressList.setAdapter(addressAdapter);
        // getAddress();

        name.setText(userDetails.getFirstName()+" "+userDetails.getLastName());


        try {

            JSONArray addressArray= new JSONArray(userDetails.getJsonAddress());

            for(int i=0;i<addressArray.length();i++){
                if(i==4){
                    break;
                }
                addresses.add(addressArray.getJSONObject(i).getString("address"));
                addressID.add(addressArray.getJSONObject(i).getInt("id"));
                map.put(addressArray.getJSONObject(i).getString("address"),addressArray.getJSONObject(i).getString("id"));
                addressAdapter.notifyItemInserted(addresses.size());
            }


            updateUserInfo();



        }catch (Exception e){

            Log.e("ozii", e.toString());

        }

        balance.setText("৳ "+ userDetails.getBalance());

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addresses.size() < 1) {
                    Toast.makeText(context, "You can add your address during first order's checkout.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(addresses.size()==4){
                    Toast.makeText(context, "You cannot add more than four addresses. But you can edit an existing address.", Toast.LENGTH_LONG).show();
                }else{
                    addressDialog();
                }
            }
        });

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
        
        
        LinearLayout editProfile = findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
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

        getBalance();



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

    public void addressDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.WideDialog));
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_add_address, null);
        dialogBuilder.setView(dialogView);
        addressET = dialogView.findViewById(R.id.addressET);
        Button addressAdd=dialogView.findViewById(R.id.addressAdd);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        addressAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addresses.add(addressET.getText().toString());
                addressAdapter.notifyItemInserted(addresses.size());
                alertDialog.dismiss();
                getUserData();
            }
        });
    }


    public void showBackButton(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    public void hideBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void getBalance(){
        String url="https://api.evaly.com.bd/core/user-info-pay/"+userDetails.getUserName()+"/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("onResponse", response.toString());


                try {


                    response = response.getJSONObject("data");

                    userDetails.setBalance(response.getString("balance"));
                    balance.setText("৳ " + response.getString("balance"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
                //headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestQueue queue= Volley.newRequestQueue(UserDashboardActivity.this);
        queue.add(request);
    }

    public void updateUserInfo(){
        String url="https://api-prod.evaly.com.bd/api/user/detail/"+userDetails.getUserName()+"/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("onResponse", response.toString());
                try {
                    JSONObject ob=response.getJSONObject("user");

                    userDetails.setUserName(ob.getString("username"));
                    userDetails.setPhone(ob.getString("username"));
                    userDetails.setFirstName(ob.getString("first_name"));
                    userDetails.setLastName(ob.getString("last_name"));

                    name.setText(userDetails.getFirstName()+" "+userDetails.getLastName());

                    JSONArray addressArray=response.getJSONArray("addresses");

                    if(userDetails.getJsonAddress().equals(addressArray.toString()))
                        Log.d("json", "yes same address");
                    else {


                        userDetails.setJsonAddress(addressArray.toString());

                        addresses.clear();
                        addressID.clear();
                        map.clear();

                        addressAdapter.notifyDataSetChanged();



                        for (int i = 0; i < addressArray.length(); i++) {
                            if (i == 4) {
                                break;
                            }
                            addresses.add(addressArray.getJSONObject(i).getString("address"));
                            addressID.add(addressArray.getJSONObject(i).getInt("id"));
                            map.put(addressArray.getJSONObject(i).getString("address"), addressArray.getJSONObject(i).getString("id"));
                            addressAdapter.notifyItemInserted(addresses.size());
                        }
                        if (addressArray.length() != 0) {
                        } else {

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestQueue queue= Volley.newRequestQueue(UserDashboardActivity.this);
        queue.add(request);
    }

    public void getUserData(){
        final ViewDialog alert = new ViewDialog(this);
        //alert.showDialog();
        String url="https://api-prod.evaly.com.bd/api/user/detail/"+userDetails.getUserName()+"/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("onResponse", response.toString()+"   "+userDetails.getToken());
                try {
                    JSONObject userJson = response;
                    JSONArray userInfo = userJson.getJSONArray("addresses");
                    JSONObject ob=new JSONObject();
                    ob.put("address",addressET.getText().toString());
                    ob.put("id",addressID.get(addressID.size()-1)+1);
                    userInfo.put(userInfo.length(),ob);
                   // System.out.println(userInfo.get(3));
                    setUserData(userJson, alert);
                    //Log.d("json user info", userJson.toString());
                    JSONArray addressArray=response.getJSONArray("addresses");
                    //JSONObject addressOB=response.getJSONObject("addresses");
                    if(addressArray.length()!=0){
                        String addressStr="";
                        for(int i=0;i<addressArray.length();i++){
                            JSONObject addressOB=addressArray.getJSONObject(i);
                            addressStr+=addressOB.get("address")+"::";
                        }
                        userDetails.setAddresses(addressStr);
                        userDetails.setJsonAddress(addressArray.toString());
                    }else{
                        userDetails.setAddresses("");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken()); // headers.put("Host", "api-prod.evaly.com.bd");                 headers.put("Content-Type", "application/json");                 headers.put("Origin", "https://evaly.com.bd");                 headers.put("Referer", "https://evaly.com.bd/");                 headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestQueue queue= Volley.newRequestQueue(UserDashboardActivity.this);
        queue.add(request);
    }

    public void setUserData(JSONObject payload, ViewDialog alert){
        String url="https://api-prod.evaly.com.bd/api/user/detail/"+userDetails.getUserName()+"/";
        Log.d("json user info url", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               // alert.hideDialog();
                Log.d("json user info response", response.toString());
                Toast.makeText(UserDashboardActivity.this,"Address added successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken()); // headers.put("Host", "api-prod.evaly.com.bd");                 headers.put("Content-Type", "application/json");                 headers.put("Origin", "https://evaly.com.bd");                 headers.put("Referer", "https://evaly.com.bd/");                 headers.put("User-Agent", userAgent);
                // headers.put("Content-Length", data.length()+"");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestQueue queue= Volley.newRequestQueue(UserDashboardActivity.this);
        queue.add(request);
    }

}
