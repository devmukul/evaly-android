package bd.com.evaly.evalyshop.activity.newsfeed;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
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
import bd.com.evaly.evalyshop.activity.newsfeed.adapters.NewsfeedPager;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.comment.RepliesItem;
import bd.com.evaly.evalyshop.util.ImageUtils;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.RealPathUtil;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Newsfeed");
        context = this;
        userDetails = new UserDetails(context);


//        BottomSheetDialog dialog = new BottomSheetDialog(NewsfeedActivity.this, R.style.BottomSheetDialogTheme);
//        dialog.setContentView(R.layout.alert_comments);
//
//        View bottomSheetInternal = dialog.findViewById(android.support.design.R.id.design_bottom_sheet);
//        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
//
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        ScreenUtils screenUtils = new ScreenUtils(NewsfeedActivity.this);
////                bottomSheetBehavior.setPeekHeight(15000);
//
//        LinearLayout dialogLayout = dialog.findViewById(R.id.container2);
//        dialogLayout.setMinimumHeight(screenUtils.getHeight());


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
        
        View bottomSheetInternal = createPostDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
        bottomSheetInternal.setPadding(0, 0, 0, 0);
        
        new KeyboardUtil(NewsfeedActivity.this, bottomSheetInternal);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {

                    bottomSheet.post(new Runnable() {
                        @Override
                        public void run() {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });

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

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageSelector();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postBody = text.getText().toString();

                int typePosition = typeSpinner.getSelectedItemPosition();
                if (typePosition == 0)
                    postType = "ceo";
                else if (typePosition == 1)
                    postType = "announcement";
                else
                    postType = "public";


                createPost();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (text.getText().toString().equals(""))
                    createPostDialog.dismiss();
                else {
                    new AlertDialog.Builder(NewsfeedActivity.this)
                            .setMessage("Are you sure you want to discard the status?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    createPostDialog.hide();
                                }})
                            .setNegativeButton("NO", null).show();
                }
            }
        });
        
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                createPostDialog.show();

            }
        });
        

        NewsfeedFragment publicFragment = NewsfeedFragment.newInstance("public");
        pager.addFragment(publicFragment,"ALL");


        NewsfeedFragment announcementFragment = NewsfeedFragment.newInstance("announcement");
        pager.addFragment(announcementFragment,"ANNOUNCEMENT");

        NewsfeedFragment ceoFragment = NewsfeedFragment.newInstance("ceo");
        pager.addFragment(ceoFragment,"CEO");

        pager.notifyDataSetChanged();


        loadComments("1568624048849");
        

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








    public void loadComments(String post_id){


        String url= UrlUtils.BASE_URL_NEWSFEED+"posts/"+post_id+"/comments?page=1";



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json response", response.toString());


                try {


                    JSONArray jsonArray = response.getJSONArray("data");

                    for (int i=0; i < jsonArray.length(); i++) {

                        Gson gson = new Gson();
                        CommentItem item = gson.fromJson(jsonArray.getJSONObject(i).toString(), CommentItem.class);


                        Logger.d(item.getReplies().get(0).getBody());

                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
                Toast.makeText(context, "Couldn't create status", Toast.LENGTH_SHORT).show();


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", "Bearer " + userDetails.getToken());

                headers.put("Content-Type", "application/json");

                return headers;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(context);

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }









    public void createPost(){

        if (postBody.equals("") || postBody == null) {
            Toast.makeText(context, "Please write something!", Toast.LENGTH_SHORT).show();
            return;
        }

        String url= UrlUtils.BASE_URL_NEWSFEED+"posts";

        JSONObject parameters = new JSONObject();
        JSONObject parametersPost = new JSONObject();
        try {
            parameters.put("title", "null");
            parameters.put("type", postType);
            parameters.put("body", postBody);

            if (!selectedImage.equals(""))
                parameters.put("attachment", selectedImage);

            parameters.put("tags", new JSONArray());

            parametersPost.put("post", parameters);



        } catch (Exception e) {
        }

        Log.d("json body", parametersPost.toString());


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parametersPost,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json response", response.toString());


                try {
                    //JSONArray jsonArray = response.getJSONObject("data");

                        createPostDialog.dismiss();

                        finish();
                        startActivity(getIntent());



                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
                Toast.makeText(context, "Couldn't create status", Toast.LENGTH_SHORT).show();


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", "Bearer " + userDetails.getToken());

                headers.put("Content-Type", "application/json");

                return headers;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(context);

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }


    private void setPostPic() {

        if (selectedImage != null && createPostDialog.isShowing()) {


            createPostDialog.findViewById(R.id.postImage).setVisibility(View.VISIBLE);


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
                                selectedImage = image;
                                setPostPic();

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
                        Log.e("json error", error.toString());
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
