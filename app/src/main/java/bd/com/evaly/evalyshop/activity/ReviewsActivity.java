package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.ReviewsAdapter;
import bd.com.evaly.evalyshop.reviewratings.BarLabels;
import bd.com.evaly.evalyshop.reviewratings.RatingReviews;
import bd.com.evaly.evalyshop.models.ReviewItem;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;

public class ReviewsActivity extends AppCompatActivity {



    String ratingJson = "{\"total_ratings\":0,\"avg_ratings\":\"0.0\",\"star_5\":0,\"star_4\":0,\"star_3\":0,\"star_2\":0,\"star_1\":0}";

    String type= "shop";
    String item_value = "1";

    RecyclerView recyclerView;
    ReviewsAdapter adapter;
    ArrayList<ReviewItem> itemList;
    LinearLayout not;
    UserDetails userDetails;

    ProgressBar progressBar;

    FloatingActionButton floatingActionButton;


    TextView d_title;
    RatingBar d_rating_bar;
    TextView d_review_text;
    Button d_submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setTitle("Reviews & Ratings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        floatingActionButton = findViewById(R.id.floatingActionButton);

        progressBar = findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.recyclerView);
        not=findViewById(R.id.not);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        itemList=new ArrayList<>();
        adapter=new ReviewsAdapter(itemList,this);
        recyclerView.setAdapter(adapter);
        userDetails=new UserDetails(this);

        RatingReviews ratingReviews = findViewById(R.id.rating_reviews);
        Intent data = getIntent();


        if(data.hasExtra("ratingJson")) {

            ratingJson = data.getStringExtra("ratingJson");
            item_value = data.getStringExtra("item_value");
            type = data.getStringExtra("type");

            if (type.equals("shop")) {
                getShopReviews(item_value);
                checkShopEligibility(item_value);
            }
            else
                getReviews(item_value);


        }


        try {

            JSONObject response = new JSONObject(ratingJson);

            int total_ratings = response.getInt("total_ratings");

            double avg_ratings = 0.0;

            try {
                avg_ratings = response.getDouble("avg_rating");
            } catch (Exception e){

                avg_ratings = response.getDouble("avg_ratings");
            }


            int star_5 = response.getInt("star_5");
            int star_4 = response.getInt("star_4");
            int star_3 = response.getInt("star_3");
            int star_2 = response.getInt("star_2");
            int star_1 = response.getInt("star_2");

            int colors[] = new int[]{
                    Color.parseColor("#0e9d58"),
                    Color.parseColor("#0e9d58"),
                    Color.parseColor("#a6ba5d"),
                    Color.parseColor("#ef7e14"),
                    Color.parseColor("#d36259")};

            int raters[] = new int[]{
                    star_5,
                    star_4,
                    star_3,
                    star_2,
                    star_1
            };

            ((TextView)findViewById(R.id.rating_average)).setText(avg_ratings+"");
            ((TextView)findViewById(R.id.rating_counter)).setText(total_ratings+" ratings");
            ((RatingBar)findViewById(R.id.ratingBar)).setRating((float) avg_ratings);

            if (total_ratings==0)
                total_ratings = 1;


            ratingReviews.createRatingBars(total_ratings, BarLabels.STYPE1, colors, raters);


        } catch (JSONException e) {
            e.printStackTrace();
        }





        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userDetails.getToken().equals("")){

                    Toast.makeText(ReviewsActivity.this, "You need to login first to create review.", Toast.LENGTH_LONG).show();
                    return;

                }

                addReviewDialog();



            }
        });



    }



    public void addReviewDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.WideDialog));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_review, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();

        d_title = dialogView.findViewById(R.id.title);
        d_rating_bar = dialogView.findViewById(R.id.ratingBar);
        d_review_text = dialogView.findViewById(R.id.review_text);
        Button d_submit = dialogView.findViewById(R.id.submit);

        if(type.equals("shop"))
            d_title.setText("Rate the shop");

        alertDialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        alertDialog.show();


        d_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (d_rating_bar.getRating() < 1){
                    Toast.makeText(ReviewsActivity.this, "Please set star rating.", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (type.equals("shop")){

                    postShopReview(
                            alertDialog,
                            item_value,
                            userDetails.getFirstName()+" "+ userDetails.getLastName(),
                            String.valueOf(d_rating_bar.getRating()),
                            d_review_text.getText().toString()
                    );

                } else {

                    postReview(
                            alertDialog,
                            item_value,
                            userDetails.getFirstName() + " " + userDetails.getLastName(),
                            String.valueOf(d_rating_bar.getRating()),
                            d_review_text.getText().toString()
                    );
                }



            }
        });
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




    public void postReview(AlertDialog alertDialog, String sku, String user_name, String rating_value, String rating_text){


        progressBar.setVisibility(View.VISIBLE);

        String url="https://nsuer.club/evaly/reviews/?sku="+sku+"&phone="+userDetails.getPhone()+"&user_name="+user_name+"&rating_value="+rating_value+"&rating_text="+rating_text+"&type="+type;

        Log.d("json", url);

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("notifications_response", response.toString());
                try {


                    alertDialog.dismiss();

                    Toast.makeText(ReviewsActivity.this, "Review submitted. It may take some time to show on the app.", Toast.LENGTH_LONG).show();

                    getProductRating(alertDialog);


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
                return headers;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(request);
    }





    public void getProductRating(AlertDialog alertDialog) {


        String url="https://nsuer.club/evaly/reviews/?sku="+item_value+"&type="+type+"&isRating=true";
        Log.d("json rating", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {


                        ratingJson = response.toString();
                        Intent intent = getIntent();
                        intent.putExtra("ratingJson", ratingJson);
                        finish();
                        startActivity(getIntent());


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        RequestQueue rq = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setShouldCache(false);
        rq.getCache().clear();
        rq.add(request);


    }








    public void getReviews(String sku){


        progressBar.setVisibility(View.VISIBLE);

        String url="https://nsuer.club/evaly/reviews/?sku="+sku+"&type="+type;

        Log.d("json", url);

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("notifications_response", response.toString());
                try {


                    progressBar.setVisibility(View.INVISIBLE);

                    JSONArray jsonArray = response.getJSONArray("reviews");
                    if(jsonArray.length()==0){
                        not.setVisibility(View.VISIBLE);

                        Glide.with(ReviewsActivity.this)
                                .load(R.drawable.ic_reviews_vector)
                                .apply(new RequestOptions().override(800, 800))
                                .into((ImageView) findViewById(R.id.noImage));


                        recyclerView.setVisibility(View.GONE);
                    }else{
                        not.setVisibility(View.GONE);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject ob = jsonArray.getJSONObject(i);
                            ReviewItem item = new ReviewItem();

                            item.setId(ob.getInt("id"));
                            item.setUser_name(ob.getString("user_name"));
                            item.setRating_value(ob.getInt("rating_value"));
                            item.setRating_text(ob.getString("rating_text"));
                            item.setTime(ob.getString("time"));
                            item.setIs_approved(ob.getInt("is_approved"));

                            itemList.add(item);

                            adapter.notifyItemInserted(itemList.size());
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

                return headers;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }




    public void getShopReviews(String sku){


        progressBar.setVisibility(View.VISIBLE);

        String url= UrlUtils.BASE_URL+"reviews/shops/"+sku+"/";

        Log.d("json", url);

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("page", "1");
            parameters.put("limit", "15");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("notifications_response", response.toString());
                try {


                    progressBar.setVisibility(View.INVISIBLE);

                    JSONArray jsonArray = response.getJSONArray("data");
                    if(jsonArray.length()==0){
                        not.setVisibility(View.VISIBLE);

                        Glide.with(ReviewsActivity.this)
                                .load(R.drawable.ic_reviews_vector)
                                .apply(new RequestOptions().override(800, 800))
                                .into((ImageView) findViewById(R.id.noImage));


                        recyclerView.setVisibility(View.GONE);
                    }else{
                        not.setVisibility(View.GONE);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject ob = jsonArray.getJSONObject(i);
                            ReviewItem item = new ReviewItem();

                            item.setId(ob.getInt("id"));
                            item.setUser_name(ob.getString("user_name"));
                            item.setRating_value(ob.getInt("rating_value"));
                            item.setRating_text(ob.getString("rating_text"));
                            item.setTime(ob.getString("time"));
                            item.setIs_approved(ob.getInt("is_approved"));

                            itemList.add(item);

                            adapter.notifyItemInserted(itemList.size());
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

                return headers;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }





    public void postShopReview(AlertDialog alertDialog, String sku, String user_name, String rating_value, String rating_text){


        progressBar.setVisibility(View.VISIBLE);


        String url= UrlUtils.BASE_URL+"add-review/"+sku+"/";


        Log.d("json", url);

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("review_message", rating_text);
            parameters.put("rating", rating_value);
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json submit rating", response.toString());
                try {


                    alertDialog.dismiss();

                    Toast.makeText(ReviewsActivity.this, "Review submitted. ", Toast.LENGTH_LONG).show();

                    getProductRating(alertDialog);


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
                return headers;
            }

        };
        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(request);
    }



    public void checkShopEligibility(String sku) {

        String url= UrlUtils.BASE_URL+"review-eligibility/"+sku+"/";
        Log.d("json rating", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {


                    Log.d("json rating", response.toString());

                    try{

                        boolean isEligible = response.getBoolean("success");

                        if(isEligible)
                            floatingActionButton.setVisibility(View.VISIBLE);
                        else
                            floatingActionButton.setVisibility(View.GONE);


                    }catch (Exception e){

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());

                return headers;
            }
        };



        RequestQueue rq = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setShouldCache(false);
        rq.getCache().clear();
        rq.add(request);


    }






}