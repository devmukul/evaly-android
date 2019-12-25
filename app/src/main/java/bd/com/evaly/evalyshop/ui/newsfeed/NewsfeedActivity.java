package bd.com.evaly.evalyshop.ui.newsfeed;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.newsfeed.adapters.NewsfeedPager;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.newsfeed.NewsfeedItem;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.ImageUtils;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.RealPathUtil;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.VolleyMultipartRequest;

public class NewsfeedActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private NewsfeedPager pager;
    private TabLayout tabLayout;
    private FloatingActionButton createBtn;
    private Context context;
    private String selectedImage = "";
    private BottomSheetDialog createPostDialog;
    private UserDetails userDetails;
    private String postType = "CEO";
    private String postBody = "";
    private String postSlug= "";

    private int hot_number;
    TextView ui_hot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getPackageManager().hasSystemFeature("android.software.webview") && Utils.isPackageExisted("com.google.android.webview", this)) {

        }else {
            Toast.makeText(this, "Please install WebView from Google Play Store", Toast.LENGTH_LONG).show();
            finish();
        }

        setContentView(R.layout.activity_newsfeed);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Newsfeed");
        context = this;
        userDetails = new UserDetails(context);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tab_layout);
        createBtn = findViewById(R.id.addPost);

        pager = new NewsfeedPager(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(pager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSmoothScrollingEnabled(true);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        createPostDialog = new BottomSheetDialog(NewsfeedActivity.this, R.style.BottomSheetDialogTheme);
        createPostDialog.setContentView(R.layout.alert_create_post);
        
        View bottomSheetInternal = createPostDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheetInternal.setPadding(0, 0, 0, 0);
        
        new KeyboardUtil(NewsfeedActivity.this, bottomSheetInternal);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {

                    bottomSheet.post(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

                } else if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_HALF_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        createPostDialog.setCanceledOnTouchOutside(false);
        ImageView close = createPostDialog.findViewById(R.id.close);
        TextView text = createPostDialog.findViewById(R.id.text);
        LinearLayout addPhoto = createPostDialog.findViewById(R.id.addPhoto);
        Button share = createPostDialog.findViewById(R.id.share);
        Spinner typeSpinner = createPostDialog.findViewById(R.id.type);
        LinearLayout spinnerHolder = createPostDialog.findViewById(R.id.spinner_holder);
        TextView addPhotoText = createPostDialog.findViewById(R.id.add_photo_text);
        RelativeLayout postImageHolder = createPostDialog.findViewById(R.id.postImageHolder);
        ImageView cancelPostImage = createPostDialog.findViewById(R.id.cancelImage);

        if (!userDetails.getGroups().contains("EvalyEmployee"))
            spinnerHolder.setVisibility(View.GONE);


        addPhoto.setOnClickListener(view -> openImageSelector());

        share.setOnClickListener(view -> {

            postBody = text.getText().toString();

            int typePosition = typeSpinner.getSelectedItemPosition();
            if (typePosition == 0)
                postType = "ceo";
            else if (typePosition == 1)
                postType = "announcement";
            else
                postType = "public";


            createPost();
        });

        close.setOnClickListener(view -> {

            if (text.getText().toString().equals(""))
                createPostDialog.dismiss();
            else {
                new AlertDialog.Builder(NewsfeedActivity.this)
                        .setMessage("Are you sure you want to discard the status?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("YES", (dialog, whichButton) -> createPostDialog.hide())
                        .setNegativeButton("NO", null).show();
            }
        });
        
        createBtn.setOnClickListener(view -> {

            selectedImage = "";
            postType = "CEO";
            postBody = "";
            postSlug = "";

            spinnerHolder.setVisibility(View.VISIBLE);
            postImageHolder.setVisibility(View.GONE);
            text.setText("");
            addPhotoText.setText("Add Photo");

            createPostDialog.show();

        });


        cancelPostImage.setOnClickListener(view -> {

            selectedImage = "";
            postImageHolder.setVisibility(View.GONE);
            addPhotoText.setText("Add Photo");

        });

        if (userDetails.getToken().equals(""))
            createBtn.setVisibility(View.GONE);


        NewsfeedFragment publicFragment = NewsfeedFragment.newInstance("public");
        pager.addFragment(publicFragment,"ALL");


        NewsfeedFragment announcementFragment = NewsfeedFragment.newInstance("announcement");
        pager.addFragment(announcementFragment,"ANNOUNCEMENT");

        NewsfeedFragment ceoFragment = NewsfeedFragment.newInstance("ceo");
        pager.addFragment(ceoFragment,"CEO");


        if (!userDetails.getToken().equals("")){

            NewsfeedFragment myFragment = NewsfeedFragment.newInstance("my");
            pager.addFragment(myFragment,"MY POSTS");
        }

        if (userDetails.getGroups().contains("EvalyEmployee")) {
            NewsfeedPendingFragment pendingFragment = NewsfeedPendingFragment.newInstance("pending");
            pager.addFragment(pendingFragment,"Pending");
        }

        pager.notifyDataSetChanged();

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.badge_newsfeed_menu, menu);
        final View menu_hotlist = menu.findItem(R.id.menu_messages).getActionView();
        ui_hot = (TextView) menu_hotlist.findViewById(R.id.hotlist_hot);

        ui_hot.setVisibility(View.INVISIBLE);

        menu_hotlist.setOnClickListener(viw -> startActivityForResult(new Intent(NewsfeedActivity.this, NewsfeedNotification.class), 1));


        if (userDetails.getToken().equals(""))
            return false;
        else
            return true;

    }



    public void openEditBottomSheet(NewsfeedItem item){


        postType = "CEO";
        postBody = item.getBody();
        postSlug = item.getSlug();

        TextView text = createPostDialog.findViewById(R.id.text);
        LinearLayout addPhoto = createPostDialog.findViewById(R.id.addPhoto);
        Spinner typeSpinner = createPostDialog.findViewById(R.id.type);
        Button share = createPostDialog.findViewById(R.id.share);
        LinearLayout spinnerHolder = createPostDialog.findViewById(R.id.spinner_holder);
        ImageView postImage = createPostDialog.findViewById(R.id.postImage);
        TextView addPhotoText = createPostDialog.findViewById(R.id.add_photo_text);

        RelativeLayout postImageHolder = createPostDialog.findViewById(R.id.postImageHolder);
        ImageView cancelPostImage = createPostDialog.findViewById(R.id.cancelImage);

        spinnerHolder.setVisibility(View.GONE);


        text.setText(item.getBody());

        Log.d("json img url", item.getAttachment());

        if (!item.getAttachment().equals("null")){

            selectedImage = item.getAttachment();
            postImage.setVisibility(View.VISIBLE);
            postImageHolder.setVisibility(View.VISIBLE);
            addPhotoText.setText("Change Photo");


            Glide.with(this)
                    .asBitmap()
                    .load(selectedImage)
                    .skipMemoryCache(true)
                    .fitCenter()
                    .optionalCenterCrop()
                    .placeholder(R.drawable.half_dp_bg_light)
                    .apply(new RequestOptions().override(500, 500))
                    .into(postImage);

        } else {

            addPhotoText.setText("Add Photo");
            postImageHolder.setVisibility(View.GONE);
            selectedImage = "";

        }

        createPostDialog.show();

    }

    @Override
    public void onResume(){

        getNotificationCount();
        super.onResume();
    }

    public void getNotificationCount(){

        String url = UrlUtils.BASE_URL_NEWSFEED+"notifications_count/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters, response -> {
            Log.d("onResponse", response.toString());

            try {
                int count = response.getInt("unread_notification_count");
                updateHotCount(count);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("onErrorResponse", error.toString());

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401){

                AuthApiHelper.refreshToken(NewsfeedActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                    @Override
                    public void onDataFetched(retrofit2.Response<JsonObject> response) {
                        getNotificationCount();
                    }

                    @Override
                    public void onFailed(int status) {

                    }
                });

                return;

            }}

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);


    }

    public void updateHotCount(final int new_hot_number) {
        hot_number = new_hot_number;
        if (ui_hot == null) return;

        if (new_hot_number == 0)
            ui_hot.setVisibility(View.INVISIBLE);
        else {
            ui_hot.setVisibility(View.VISIBLE);
            if (new_hot_number > 99)
                ui_hot.setText("99");
            else
                ui_hot.setText(Integer.toString(new_hot_number));
        }

    }


    public void createPost(){

        if (postBody.equals("") || postBody == null) {
            Toast.makeText(context, "Please write something!", Toast.LENGTH_SHORT).show();
            return;
        }

        createBtn.setEnabled(false);

        String url;

        url = UrlUtils.BASE_URL_NEWSFEED+"posts";

        if (!postSlug.equals(""))
            url = UrlUtils.BASE_URL_NEWSFEED+"posts/"+postSlug;

        JSONObject parameters = new JSONObject();
        JSONObject parametersPost = new JSONObject();
        try {
            parameters.put("title", "null");


            if (userDetails.getGroups().contains("EvalyEmployee") && postSlug.equals(""))
                parameters.put("type", postType);

            parameters.put("body", postBody);

            if (!(selectedImage.equals("") || selectedImage.equals("null")))
                parameters.put("attachment", selectedImage);
            else
                parameters.put("attachment", JSONObject.NULL);

            parameters.put("tags", new JSONArray());
            parametersPost.put("post", parameters);


        } catch (Exception e) {
        }

        Log.d("json body", parametersPost.toString());


        JsonObjectRequest request = new JsonObjectRequest((postSlug.equals("") ? Request.Method.POST : Request.Method.PUT), url, parametersPost, response -> {
            Log.d("json response", response.toString());
            createBtn.setEnabled(true);
            try {


                if (!userDetails.getGroups().contains("EvalyEmployee"))
                    Toast.makeText(context, "Your post has successfully posted. It may take few hours to get approved.", Toast.LENGTH_LONG).show();

                //JSONArray jsonArray = response.getJSONObject("data");
                createPostDialog.dismiss();
                finish();
                startActivity(getIntent());

            } catch (Exception e) {
                e.printStackTrace();
            }


        }, error -> {
            Log.e("onErrorResponse", error.toString());

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401){

                AuthApiHelper.refreshToken(NewsfeedActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                    @Override
                    public void onDataFetched(retrofit2.Response<JsonObject> response) {
                        createPost();
                    }

                    @Override
                    public void onFailed(int status) {

                    }
                });

                return;

            }}

            Toast.makeText(context, "Couldn't create status", Toast.LENGTH_SHORT).show();
            createBtn.setEnabled(true);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", CredentialManager.getToken());
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(context);

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    private void setPostPic() {

        if (selectedImage != null && createPostDialog.isShowing()) {

            createPostDialog.findViewById(R.id.postImage).setVisibility(View.VISIBLE);
            createPostDialog.findViewById(R.id.postImageHolder).setVisibility(View.VISIBLE);

            ((TextView) createPostDialog.findViewById(R.id.add_photo_text)).setText("Change Photo");


            Glide.with(this)
                    .asBitmap()
                    .load(selectedImage)
                    .skipMemoryCache(true)
                    .fitCenter()
                    .optionalCenterCrop()
                    .placeholder(R.drawable.half_dp_bg_light)
                    .apply(new RequestOptions().override(500, 500))
                    .into((ImageView) createPostDialog.findViewById(R.id.postImage));
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



        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

                NewsfeedFragment newsfeedFragment = (NewsfeedFragment) pager.getCurrentFragment();

                String type = data.getStringExtra("type");
                String status_id = data.getStringExtra("status_id");
                String comment_id = data.getStringExtra("comment_id");
                newsfeedFragment.openCommentBottomSheet(status_id,"","", false,"","","");

        }

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImage = data.getData();
            String imagePath = RealPathUtil.getRealPath(context, selectedImage);
            Log.d("json image uri", imagePath);

            try {

                String destinationDirectoryPath = context.getCacheDir().getPath() + File.separator + "images";

                try {

                    File cImage = compressImage(data.getData(), Bitmap.CompressFormat.JPEG, 70, destinationDirectoryPath);

                    Bitmap bitmap = BitmapFactory.decodeFile(destinationDirectoryPath);

                    if (selectedImage != null && createPostDialog.isShowing()) {
                        Glide.with(this)
                                .asBitmap()
                                .load(selectedImage)
                                .skipMemoryCache(true)
                                .fitCenter()
                                .optionalCenterCrop()
                                .placeholder(R.drawable.half_dp_bg_light)
                                .apply(new RequestOptions().override(500, 500))
                                .into((ImageView) createPostDialog.findViewById(R.id.postImage));
                    }

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

            ImageUtils.getCorrectlyOrientedImage(NewsfeedActivity.this, path).compress(compressFormat, quality, fileOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(destinationPath);

    }


    private void uploadProfilePicture(final Bitmap bitmap) {

        ProgressDialog dialog = ProgressDialog.show(this, "",
                "Uploading image...", true);

        RequestQueue rQueue;
        String url= UrlUtils.BASE_URL+"image/upload";

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
                            selectedImage = image;
                            setPostPic();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        if (error.networkResponse.statusCode == 401){

                                AuthApiHelper.refreshToken(NewsfeedActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
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
                    Log.e("json error", error.toString());
                    Toast.makeText(context, "Image upload error", Toast.LENGTH_SHORT).show();
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


}
