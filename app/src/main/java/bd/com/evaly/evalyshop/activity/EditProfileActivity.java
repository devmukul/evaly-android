package bd.com.evaly.evalyshop.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.util.ImageUtils;
import bd.com.evaly.evalyshop.util.RealPathUtil;
import bd.com.evaly.evalyshop.util.Token;
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
        userDetails = new UserDetails(this);

        firstname = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        update = findViewById(R.id.update);
        address = findViewById(R.id.address);
        profilePic = findViewById(R.id.picture);
        setProfilePic();

        firstname.setText(userDetails.getFirstName());
        lastName.setText(userDetails.getLastName());
        email.setText(userDetails.getEmail());
        phone.setText(userDetails.getPhone());


        if (userDetails.getJsonAddress().equals("null"))
            address.setHint("Add an address");
        else
            address.setText(userDetails.getJsonAddress());


        ImageView editPicture = findViewById(R.id.editPicture);
        editPicture.bringToFront();


        View.OnClickListener uploadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent=new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select Profile Picture"),1000);

//                Intent intent=new Intent(Intent.ACTION_PICK);
//                // Sets the type as image/*. This ensures only components of type image are selected
//                intent.setType("image/*");
//                //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
//                String[] mimeTypes = {"image/jpeg", "image/png"};
//                intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
//                // Launching the Intent
//                startActivityForResult(intent,1001);


                openImageSelector();


            }
        };

        editPicture.setOnClickListener(uploadListener);
        profilePic.setOnClickListener(uploadListener);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setUserData();
                System.out.println(userDetails.getToken());
                getUserData();
            }
        });

    }


    private void setProfilePic() {

        if (!userDetails.getProfilePictureSM().equals("null")) {
            Glide.with(this)
                    .asBitmap()
                    .load(userDetails.getProfilePictureSM())
                    .skipMemoryCache(true)
                    .fitCenter()
                    .optionalCenterCrop()
                    .placeholder(R.drawable.half_dp_bg_light)
                    .apply(new RequestOptions().override(500, 500))
                    .into(profilePic);
        }

    }


    private void openImageSelector() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    8000);

        } else {

            openSelector();

        }

    }


    private void openSelector() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, 1001);

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 8000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openSelector();
            else {

                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImage = data.getData();
            String imagePath = RealPathUtil.getRealPath(context, selectedImage);
            Log.d("json image uri", imagePath);

            try {

                String destinationDirectoryPath = context.getCacheDir().getPath() + File.separator + "images";

                try {

                    File cImage = compressImage(data.getData(),Bitmap.CompressFormat.JPEG, 60, destinationDirectoryPath);

                    Bitmap bitmap = BitmapFactory.decodeFile(destinationDirectoryPath);

                    Glide.with(this)
                            .asBitmap()
                            .load(bitmap)
                            .skipMemoryCache(true)
                            .fitCenter()
                            .optionalCenterCrop()
                            .apply(new RequestOptions().override(500, 500))
                            .into(profilePic);

                    uploadProfilePicture(bitmap);

                } catch (Exception e) {

                }

            } catch (Exception e) {

                Log.d("json image error", e.toString());
                Toast.makeText(context, "Error occurred while uploading image", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private File compressImage(Uri path, Bitmap.CompressFormat compressFormat, int quality, String destinationPath) throws IOException {
        //FileOutputStream fileOutputStream = null;
        File file = new File(destinationPath).getParentFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationPath)) {

            ImageUtils.getCorrectlyOrientedImage(EditProfileActivity.this, path).compress(compressFormat, quality, fileOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(destinationPath);

    }




    private void uploadProfilePicture(final Bitmap bitmap) {

        ProgressDialog dialog = ProgressDialog.show(this, "",
                "Uploading image...", true);

        RequestQueue rQueue;
        String url=UrlUtils.BASE_URL+"image/upload";

        Logger.d(url);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        dialog.dismiss();

                        Log.d("json image" ,new String(response.data));


                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));

                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            if(jsonObject.getBoolean("success")) {
                                String image = jsonObject.getJSONObject("data").getString("url");
                                String image_sm = jsonObject.getJSONObject("data").getString("url_sm");
                                userDetails.setProfilePicture(image);
                                userDetails.setProfilePictureSM(image_sm);
                                setProfilePic();
                                getUserData();

                            }

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
                    userInfo.put("profile_pic_url", userDetails.getProfilePicture());

                    userDetails.setFirstName(firstname.getText().toString());
                    userDetails.setLastName(lastName.getText().toString());
                    userDetails.setEmail(email.getText().toString());
                    userDetails.setPhone(phone.getText().toString());
                    userDetails.setJsonAddress(address.getText().toString());

                    setUserData(userInfo, alert);


                    Token.update(context);

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
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
        queue.add(request);
    }

    public void setUserData(JSONObject payload, ViewDialog alert) {

        String url = UrlUtils.BASE_URL+"user-info-update/";
        Log.d("json user info url", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                alert.hideDialog();
                Log.d("json user info response", response.toString());
                Toast.makeText(EditProfileActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();

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
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
