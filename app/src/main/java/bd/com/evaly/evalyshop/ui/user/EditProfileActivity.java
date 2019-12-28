package bd.com.evaly.evalyshop.ui.user;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.ImageUtils;
import bd.com.evaly.evalyshop.util.RealPathUtil;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.VolleyMultipartRequest;
import bd.com.evaly.evalyshop.util.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.util.xmpp.XMPPService;
import bd.com.evaly.evalyshop.util.xmpp.XmppCustomEventListener;


public class EditProfileActivity extends BaseActivity {

    EditText firstname, lastName, email, phone, address;
    Button update;
    String username = "", token = "";
    UserDetails userDetails;
    String userAgent;
    Context context;
    ImageView profilePic;
    private ViewDialog dialog;

    private AppController mChatApp = AppController.getInstance();
    private XMPPHandler xmppHandler;

    private XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener(){

        //Event Listeners
        public void onConnected() {
            xmppHandler = AppController.getmService().xmpp;
            Logger.d("======   CONNECTED  -========");
        }

        public void onLoggedIn(){
            xmppHandler = AppController.getmService().xmpp;
            xmppHandler.updateUserInfo(CredentialManager.getUserData());
        }

        public void onUpdateUserSuccess(){
            dialog.hideDialog();
            Toast.makeText(EditProfileActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
            try {
                onBackPressed();
            } catch (Exception e){
                finish();
            }
        }

        public void onUpdateUserFailed( String error ){
            Logger.d(error);

            xmppHandler.disconnect();
            Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Edit Personal Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        userDetails = new UserDetails(this);

        dialog = new ViewDialog(this);

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


        View.OnClickListener uploadListener = v -> openImageSelector();

        editPicture.setOnClickListener(uploadListener);
        profilePic.setOnClickListener(uploadListener);


        update.setOnClickListener(v -> {
            //setUserData();

            if (!Utils.isValidNumber(phone.getText().toString())){
                Toast.makeText(context, "Please enter a correct phone number", Toast.LENGTH_SHORT).show();
                return;
            }else if (firstname.getText().toString().trim().isEmpty()){
                firstname.setError("Required");
                return;
            }else if (lastName.getText().toString().trim().isEmpty()){
                lastName.setError("Required");
                return;
            }else if (email.getText().toString().trim().isEmpty()){
                email.setError("Required");
                return;
            }

            setUserData();
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


        ProgressDialog dialog = ProgressDialog.show(EditProfileActivity.this, "",
                "Uploading image...", true);

        RequestQueue rQueue;
        String url=UrlUtils.BASE_URL+"image/upload";

        Logger.d(url);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                response -> {

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
//                                getUserData();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        if (error.networkResponse.statusCode == 401){

                                AuthApiHelper.refreshToken(EditProfileActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                                    @Override
                                    public void onDataFetched(retrofit2.Response<JsonObject> response) {
                                        uploadProfilePicture(bitmap);
                                    }

                                    @Override
                                    public void onFailed(int status) {

                                    }
                                });

                        return;

                    }}


                    dialog.dismiss();
                    Toast.makeText(context, "Image upload error", Toast.LENGTH_SHORT).show();
                }) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // params.put("tags", "ccccc");  add string parameters
                return params;
            }


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization", CredentialManager.getToken());

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
                xmppHandler.updateUserInfo(CredentialManager.getUserData());
            }
        }
    }

    public void setUserData() {

        dialog.showDialog();

        HashMap<String, String> userInfo =  new HashMap<>();

        userInfo.put("first_name", firstname.getText().toString());
        userInfo.put("last_name", lastName.getText().toString());
        userInfo.put("email", email.getText().toString());
        userInfo.put("contact", phone.getText().toString());
        userInfo.put("address", address.getText().toString());
        userInfo.put("profile_pic_url", userDetails.getProfilePicture());



        AuthApiHelper.setUserData(CredentialManager.getToken(), userInfo, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                mChatApp.getEventReceiver().setListener(xmppCustomEventListener);
                dialog.hideDialog();

                JsonObject ob = response.getAsJsonObject("data");

                if (!ob.get("first_name").isJsonNull())
                    userDetails.setFirstName(ob.get("first_name").getAsString());

                if (!ob.get("last_name").isJsonNull())
                    userDetails.setLastName(ob.get("last_name").getAsString());

                if (!ob.get("email").isJsonNull())
                    userDetails.setEmail(ob.get("email").getAsString());

                if (!ob.get("contact").isJsonNull())
                    userDetails.setPhone(ob.get("contact").getAsString());

                if (!ob.get("address").isJsonNull())
                    userDetails.setJsonAddress(ob.get("address").getAsString());

                if (!ob.get("profile_pic_url").isJsonNull())
                    userDetails.setProfilePicture(ob.get("profile_pic_url").getAsString());

                if (!ob.get("image_sm").isJsonNull())
                    userDetails.setProfilePictureSM(ob.get("image_sm").getAsString());

                UserModel userModel = new Gson().fromJson(ob.toString(), UserModel.class);

                if (ob.get("first_name").isJsonNull())
                    userModel.setFirst_name("");

                if (ob.get("last_name").isJsonNull())
                    userModel.setLast_name("");

                CredentialManager.saveUserData(userModel);

                startXmppService();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                dialog.hideDialog();
            }

            @Override
            public void onAuthError(boolean logout) {

                if (logout)
                    AppController.logout(EditProfileActivity.this);
                else
                    setUserData();


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
