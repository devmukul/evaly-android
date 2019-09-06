package bd.com.evaly.evalyshop.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.VolleyMultipartRequest;

public class EditProfileActivity extends BaseActivity {

    EditText firstname, lastName, email, phone, address;
    Button update;
    String username = "", token = "";
    UserDetails userDetails;

    String userAgent;
    Context context;

    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Edit Personal Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        context = this;

        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }
        firstname = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        update = findViewById(R.id.update);
        address = findViewById(R.id.address);
        profilePic = findViewById(R.id.picture);


        userDetails = new UserDetails(this);

        firstname.setText(userDetails.getFirstName());
        lastName.setText(userDetails.getLastName());
        email.setText(userDetails.getEmail());
        phone.setText(userDetails.getPhone());
        address.setText(userDetails.getJsonAddress());

        if (userDetails.getProfilePicture() != null || !userDetails.getProfilePicture().isEmpty()) {
            Glide.with(this)
                    .asBitmap()
                    .load(userDetails.getProfilePicture())
                    .skipMemoryCache(true)
                    .fitCenter()
                    .optionalCenterCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(200, 200))
                    .into(profilePic);
        }



        ImageView editPicture = findViewById(R.id.editPicture);
        editPicture.bringToFront();




        View.OnClickListener uploadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Profile Picture"),1000);


            }
        };





        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setUserData();
                System.out.println(userDetails.getToken());
                getUserData();
            }
        });

    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                uploadProfilePicture(bitmap);


            }catch(Exception e){

            }
        }
    }




    private void uploadProfilePicture(final Bitmap bitmap) {

        ProgressDialog dialog = ProgressDialog.show(this, "",
                "Uploading image...", true);

        RequestQueue rQueue;
        String url="https://api.evaly.com.bd/core/image/upload";

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        dialog.dismiss();

                        Log.d("json image",new String(response.data));


                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));

                            String image = jsonObject.getString("image");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        dialog.dismiss();
                        Toast.makeText(context, "Image upload error", Toast.LENGTH_SHORT).show();
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // params.put("tags", "ccccc");  add string parameters
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization", "Bearer " + userDetails.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);

                return headers;
            }

            /*
             *pass files using below method
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };


        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(context);
        rQueue.add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }







    public void getUserData() {
        final ViewDialog alert = new ViewDialog(this);

        alert.showDialog();

        String url = UrlUtils.REFRESH_AUTH_TOKEN + userDetails.getUserName() + "/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("onResponse", response.toString());
                try {

                    JSONObject userJson = response.getJSONObject("data");
                    JSONObject userInfo = userJson.getJSONObject("user_info");
                    userInfo.put("first_name", firstname.getText().toString());
                    userInfo.put("last_name", lastName.getText().toString());
                    userInfo.put("email", email.getText().toString());
                    userInfo.put("contact", phone.getText().toString());
                    userInfo.put("address", address.getText().toString());

                    userDetails.setFirstName(firstname.getText().toString());
                    userDetails.setLastName(lastName.getText().toString());

                    userDetails.setEmail(email.getText().toString());
                    userDetails.setPhone(phone.getText().toString());
                    userDetails.setJsonAddress(address.getText().toString());

                    setUserData(userInfo, alert);

                    Log.d("json user info", userJson.toString());

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
        RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
        queue.add(request);
    }

    public void setUserData(JSONObject payload, ViewDialog alert) {

        String url = "https://api.evaly.com.bd/core/user-info-update/";

        Log.d("json user info url", url);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                alert.hideDialog();

                Log.d("json user info response", response.toString());

                Toast.makeText(EditProfileActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();

                EditProfileActivity.this.finish();

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
                // headers.put("Content-Length", data.length()+"");
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
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
        queue.add(request);
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
