package bd.com.evaly.evalyshop.ui.reviews;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.reviews.ReviewItem;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ReviewsApiHelper;
import bd.com.evaly.evalyshop.ui.reviews.adapter.ReviewsAdapter;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.reviewratings.BarLabels;
import bd.com.evaly.evalyshop.util.reviewratings.RatingReviews;

public class ReviewsActivity extends AppCompatActivity {

    private String ratingJson = "{\"total_ratings\":0,\"avg_ratings\":\"0.0\",\"star_5\":0,\"star_4\":0,\"star_3\":0,\"star_2\":0,\"star_1\":0}";
    private String type = "shop";
    private String item_value = "1";
    private RecyclerView recyclerView;
    private ReviewsAdapter adapter;
    private ArrayList<ReviewItem> itemList;
    private LinearLayout not;
    private UserDetails userDetails;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;
    private TextView d_title;
    private RatingBar d_rating_bar;
    private TextView d_review_text;
    private RequestQueue rq;
    private int currentPage;
    private RatingReviews ratingReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setTitle("Reviews & Ratings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        not = findViewById(R.id.not);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        itemList = new ArrayList<>();
        adapter = new ReviewsAdapter(itemList, this);
        recyclerView.setAdapter(adapter);
        userDetails = new UserDetails(this);
        rq = Volley.newRequestQueue(this);

        currentPage = 1;

        ratingReviews = findViewById(R.id.rating_reviews);

        Intent data = getIntent();

        if (data.hasExtra("ratingJson")) {

            ratingJson = data.getStringExtra("ratingJson");
            item_value = data.getStringExtra("item_value");
            type = data.getStringExtra("type");

            if (type.equals("shop")) {
                getShopRatings(item_value);
                getShopReviews(item_value);
                checkShopEligibility(item_value);
            } else
                getReviews(item_value);
        }


        loadRatingsToView(ratingJson);

        floatingActionButton.setOnClickListener(v -> {

            if (userDetails.getToken().equals("")) {
                Toast.makeText(ReviewsActivity.this, "You need to login first to create review.", Toast.LENGTH_LONG).show();
                return;
            }
            addReviewDialog();
        });

        NestedScrollView nestedSV = findViewById(R.id.stickyScrollView);

        nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    getShopReviews(item_value);
                }
            }
        });
    }


    public void loadRatingsToView(String ratingJsonz) {


        try {

            JSONObject response = new JSONObject(ratingJsonz);
            int total_ratings = response.getInt("total_ratings");
            double avg_ratings = 0.0;

            try {
                avg_ratings = response.getDouble("avg_rating");
            } catch (Exception e) {
                avg_ratings = response.getDouble("avg_ratings");
            }

            int star_5 = response.getInt("star_5");
            int star_4 = response.getInt("star_4");
            int star_3 = response.getInt("star_3");
            int star_2 = response.getInt("star_2");
            int star_1 = response.getInt("star_1");

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

            ((TextView) findViewById(R.id.rating_average)).setText(avg_ratings + "");
            ((TextView) findViewById(R.id.rating_counter)).setText(total_ratings + " ratings");
            ((RatingBar) findViewById(R.id.ratingBar)).setRating((float) avg_ratings);

            if (total_ratings == 0)
                total_ratings = 1;

            ratingReviews.createRatingBars(total_ratings, BarLabels.STYPE1, colors, raters);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void addReviewDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.WideDialog));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_review, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();

        d_title = dialogView.findViewById(R.id.title);
        d_rating_bar = dialogView.findViewById(R.id.ratingBar);
        d_review_text = dialogView.findViewById(R.id.review_text);
        Button d_submit = dialogView.findViewById(R.id.submit);

        if (type.equals("shop"))
            d_title.setText("Rate the shop");

        alertDialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        alertDialog.show();


        d_submit.setOnClickListener(v -> {

            if (d_rating_bar.getRating() < 1) {
                Toast.makeText(ReviewsActivity.this, "Please set star rating.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (type.equals("shop")) {
                postShopReview(
                        alertDialog,
                        item_value,
                        userDetails.getFirstName() + " " + userDetails.getLastName(),
                        (int) d_rating_bar.getRating(),
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


    public void postReview(AlertDialog alertDialog, String sku, String user_name, String rating_value, String rating_text) {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://nsuer.club/evaly/reviews/?sku=" + sku + "&phone=" + userDetails.getPhone() + "&user_name=" + user_name + "&rating_value=" + rating_value + "&rating_text=" + rating_text + "&type=" + type;

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters, response -> {
            Log.d("notifications_response", response.toString());
            try {
                alertDialog.dismiss();
                Toast.makeText(ReviewsActivity.this, "Review submitted. It may take some time to show on the app.", Toast.LENGTH_LONG).show();
                getProductRating(alertDialog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.e("onErrorResponse", error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());
                return headers;
            }
        };
        rq.add(request);
    }


    public void getProductRating(AlertDialog alertDialog) {

        String url = "https://nsuer.club/evaly/reviews/?sku=" + item_value + "&type=" + type + "&isRating=true";
        Log.d("json rating", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
                response -> {
                    ratingJson = response.toString();
                    Intent intent = getIntent();
                    intent.putExtra("ratingJson", ratingJson);
                    finish();
                    startActivity(getIntent());
                }, Throwable::printStackTrace);

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setShouldCache(false);
        rq.getCache().clear();
        rq.add(request);


    }

    public void getReviews(String sku) {


        progressBar.setVisibility(View.VISIBLE);
        String url = "https://nsuer.club/evaly/reviews/?sku=" + sku + "&type=" + type;

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters, response -> {
            Log.d("notifications_response", response.toString());
            try {

                progressBar.setVisibility(View.INVISIBLE);

                JSONArray jsonArray = response.getJSONArray("reviews");
                if (jsonArray.length() == 0) {
                    not.setVisibility(View.VISIBLE);

                    Glide.with(ReviewsActivity.this)
                            .load(R.drawable.ic_reviews_vector)
                            .apply(new RequestOptions().override(800, 800))
                            .into((ImageView) findViewById(R.id.noImage));


                    recyclerView.setVisibility(View.GONE);
                } else {
                    not.setVisibility(View.GONE);
                    for (int i = 0; i < jsonArray.length(); i++) {
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
        }, error -> Log.e("onErrorResponse", error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());

                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(request);
    }


    public void getShopReviews(String sku) {

        progressBar.setVisibility(View.VISIBLE);

        ReviewsApiHelper.getShopReviews(CredentialManager.getToken(), sku, currentPage, 20, new ResponseListenerAuth<CommonDataResponse<List<ReviewItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ReviewItem>> response, int statusCode) {

                progressBar.setVisibility(View.INVISIBLE);
                List<ReviewItem> list = response.getData();

                if (list.size() == 0 && currentPage == 1) {
                    not.setVisibility(View.VISIBLE);
                    Glide.with(ReviewsActivity.this)
                            .load(R.drawable.ic_reviews_vector)
                            .apply(new RequestOptions().override(800, 800))
                            .into((ImageView) findViewById(R.id.noImage));

                    recyclerView.setVisibility(View.GONE);
                } else {
                    itemList.addAll(list);
                    adapter.notifyItemRangeChanged(itemList.size() - list.size(), list.size());
                    not.setVisibility(View.GONE);
                    currentPage++;
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    public void getShopRatings(String sku) {


        progressBar.setVisibility(View.VISIBLE);

        ReviewsApiHelper.getShopRatings(CredentialManager.getToken(), sku, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                response = response.getAsJsonObject("data");
                ratingJson = response.toString();
                loadRatingsToView(ratingJson);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });


    }


    public void postShopReview(AlertDialog alertDialog, String sku, String user_name, int rating_value, String rating_text) {

        ViewDialog progressDialog = new ViewDialog(this);
        progressDialog.showDialog();

        String url = UrlUtils.BASE_URL + "add-review/" + sku + "/";

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("review_message", rating_text);
            parameters.put("rating", rating_value);
        } catch (Exception e) {
        }


        Log.d("json body", parameters.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters, response -> {
            Log.d("json submit rating", response.toString());
            try {

                progressDialog.hideDialog();
                alertDialog.dismiss();
                Toast.makeText(ReviewsActivity.this, "Review submitted. ", Toast.LENGTH_LONG).show();
                getProductRating(alertDialog);

            } catch (Exception e) {

                progressDialog.hideDialog();

                e.printStackTrace();
            }
        }, error -> {
            Log.e("onErrorResponse", error.toString());

            progressDialog.hideDialog();

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401) {

                    AuthApiHelper.refreshToken(ReviewsActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            postShopReview(alertDialog, sku, user_name, rating_value, rating_text);
                        }

                        @Override
                        public void onFailed(int status) {
                            progressDialog.hideDialog();
                        }
                    });


                } else {
                    Toast.makeText(ReviewsActivity.this, "Server error!", Toast.LENGTH_SHORT).show();
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());
                return headers;
            }
        };
        rq.add(request);
    }


    public void checkShopEligibility(String sku) {

        String url = UrlUtils.BASE_URL + "review-eligibility/" + sku + "/";
        Log.d("json rating", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
                response -> {
                    try {
                        boolean isEligible = response.getBoolean("success");

                        if (isEligible)
                            floatingActionButton.setVisibility(View.VISIBLE);
                        else
                            floatingActionButton.setVisibility(View.GONE);
                    } catch (Exception e) {

                    }

                }, error -> {
            error.printStackTrace();

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401) {

                    AuthApiHelper.refreshToken(ReviewsActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            checkShopEligibility(sku);
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                } else
                    floatingActionButton.setVisibility(View.GONE);

            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());

                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setShouldCache(false);
        rq.getCache().clear();
        rq.add(request);
    }

}