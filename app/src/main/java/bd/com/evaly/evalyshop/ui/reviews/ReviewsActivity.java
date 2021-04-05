package bd.com.evaly.evalyshop.ui.reviews;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.reviews.ReviewItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.reviews.adapter.ReviewsAdapter;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.reviewratings.BarLabels;
import bd.com.evaly.evalyshop.util.reviewratings.RatingReviews;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReviewsActivity extends AppCompatActivity {

    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    ApiRepository apiRepository;
    private String ratingJson = "{\"total_ratings\":0,\"avg_ratings\":\"0.0\",\"star_5\":0,\"star_4\":0,\"star_3\":0,\"star_2\":0,\"star_1\":0}";
    private String type = "shop";
    private String item_value = "1";
    private RecyclerView recyclerView;
    private ReviewsAdapter adapter;
    private ArrayList<ReviewItem> itemList;
    private LinearLayout not;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;
    private TextView d_title;
    private RatingBar d_rating_bar;
    private TextView d_review_text;
    private RequestQueue rq;
    private int currentPage;
    private RatingReviews ratingReviews;
    private boolean isShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setTitle(R.string.rating_and_reviews);
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
        rq = Volley.newRequestQueue(this);

        currentPage = 1;

        ratingReviews = findViewById(R.id.rating_reviews);

        Intent data = getIntent();

        if (data.hasExtra("ratingJson")) {

            ratingJson = data.getStringExtra("ratingJson");
            item_value = data.getStringExtra("item_value");
            type = data.getStringExtra("type");

            if (type.equals("shop"))
                isShop = true;
            else
                isShop = false;

            getRatings(item_value);
            getReviews(item_value);

            if (!preferenceRepository.getToken().equals(""))
                checkEligibility(item_value);
        }

        loadRatingsToView(ratingJson);

        floatingActionButton.setOnClickListener(v -> {
            if (preferenceRepository.getToken().equals("")) {
                Toast.makeText(ReviewsActivity.this, "You need to login first to create review.", Toast.LENGTH_LONG).show();
                return;
            }
            addReviewDialog();
        });

        NestedScrollView nestedSV = findViewById(R.id.stickyScrollView);

        nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY)
                    getReviews(item_value);
            }
        });
    }


    public void loadRatingsToView(String ratingJsonz) {
        try {

            JSONObject response = new JSONObject(ratingJsonz);
            int total_ratings = response.getInt("total_ratings");
            double avg_ratings = 0.0;

            avg_ratings = response.getDouble("avg_rating");
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

            ((TextView) findViewById(R.id.rating_average)).setText(Utils.formatPrice(avg_ratings));
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

        alertDialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        alertDialog.show();

        d_submit.setOnClickListener(v -> {
            if (d_rating_bar.getRating() < 1) {
                Toast.makeText(ReviewsActivity.this, "Please set star rating.", Toast.LENGTH_SHORT).show();
                return;
            }
            postReview(
                    alertDialog,
                    item_value,
                    preferenceRepository.getUserData().getFullName(),
                    (int) d_rating_bar.getRating(),
                    d_review_text.getText().toString());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getReviews(String slug) {

        progressBar.setVisibility(View.VISIBLE);
        apiRepository.getReviews(preferenceRepository.getToken(), slug, currentPage, 20, isShop, new ResponseListenerAuth<CommonDataResponse<JsonObject>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<JsonObject> response, int statusCode) {
                progressBar.setVisibility(View.INVISIBLE);
                List<ReviewItem> list = new ArrayList<>();
                if (response.getData() != null && response.getData().has("feedback_list")) {
                    list = new Gson().fromJson(response.getData().get("feedback_list").getAsJsonArray(), new TypeToken<List<ReviewItem>>() {
                    }.getType());
                }

                if (list.size() == 0 && currentPage == 1) {
                    not.setVisibility(View.VISIBLE);
                    if (!isFinishing() && !isDestroyed())
                        Glide.with(getApplicationContext())
                                .load(R.drawable.ic_reviews_vector)
                                .apply(new RequestOptions().override(800, 800))
                                .into((ImageView) findViewById(R.id.noImage));
                    recyclerView.setVisibility(View.GONE);
                } else {
                    itemList.addAll(list);
                    adapter.notifyItemRangeChanged(itemList.size() - list.size(), list.size());
                    if (currentPage == 1) {
                        recyclerView.setVisibility(View.VISIBLE);
                        not.setVisibility(View.GONE);
                    }
                    currentPage++;
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    getReviews(slug);
            }
        });
    }

    public void getRatings(String slug) {

        progressBar.setVisibility(View.VISIBLE);
        apiRepository.getReviewSummary(preferenceRepository.getToken(), slug, isShop, new ResponseListenerAuth<JsonObject, String>() {
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
                if (!logout)
                    getRatings(slug);
            }
        });
    }


    public void postReview(AlertDialog alertDialog, String slug, String user_name, int rating_value, String rating_text) {

        ViewDialog progressDialog = new ViewDialog(this);
        progressDialog.showDialog();

        JsonObject parameters = new JsonObject();
        parameters.addProperty("review_message", rating_text);
        parameters.addProperty("rating", rating_value);

        apiRepository.postReview(preferenceRepository.getToken(), slug, parameters, isShop, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                progressDialog.hideDialog();
                alertDialog.dismiss();
                if (statusCode == 201) {
                    Toast.makeText(ReviewsActivity.this, "Your review has been submitted. It might take a while for approval.", Toast.LENGTH_LONG).show();
                }

                getRatings(slug);
                currentPage = 1;
                itemList.clear();
                adapter.notifyDataSetChanged();
                getReviews(slug);
                checkEligibility(slug);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                progressDialog.hideDialog();
                alertDialog.dismiss();
                Toast.makeText(ReviewsActivity.this, "Couldn't post review!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    postReview(alertDialog, slug, user_name, rating_value, rating_text);
            }
        });
    }


    public void checkEligibility(String slug) {

        apiRepository.checkReviewEligibility(preferenceRepository.getToken(), slug, isShop, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                boolean isEligible = false;
                if (statusCode == 200)
                    isEligible = true;

                if (isEligible)
                    floatingActionButton.setVisibility(View.VISIBLE);
                else
                    floatingActionButton.setVisibility(View.GONE);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                floatingActionButton.setVisibility(View.GONE);
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    checkEligibility(slug);
            }
        });
    }
}