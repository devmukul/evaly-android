package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.CategorySearchAdapter;
import bd.com.evaly.evalyshop.adapter.NotificationAdapter;

public class SearchCategory extends AppCompatActivity {




    ArrayList<JSONObject> itemList;
    RecyclerView recyclerView;
    CategorySearchAdapter adapter;
    LinearLayout noItem, progressContainer;
    ImageView noImage;
    TextView noText;

    EditText search;

    TextView searchTitle;

    boolean isLoading = false;



    RequestQueue rq;

    String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_category);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        noItem = findViewById(R.id.noItem);
        progressContainer = findViewById(R.id.progressContainer);

        noImage = findViewById(R.id.noImage);
        noText = findViewById(R.id.noText);
        searchTitle = findViewById(R.id.searchTitle);

        noItem.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        rq = Volley.newRequestQueue(this);


        itemList = new ArrayList<>();

        adapter = new CategorySearchAdapter(itemList,this);

        recyclerView.setAdapter(adapter);



        search = findViewById(R.id.search);

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


                if (!search.getText().toString().trim().equals("")){

                    performSearch(search.getText().toString().trim());
                    query = search.getText().toString().trim();
                }

                else {

                    noItem.setVisibility(View.VISIBLE);
                    itemList.clear();
                    adapter.notifyDataSetChanged();
                    searchTitle.setVisibility(View.GONE);

                    noText.setText("Search categories here");
                }

            }
        });










    }

    // https://api-prod.evaly.com.bd/api/categories/?search=phone&page=1


    public String getQuery(){

        return query;

    }


    public void performSearch(String query) {

        if (rq != null) {
            rq.cancelAll(this);
        }



        searchTitle.setText("Search result for \"" +query+ "\"");

        progressContainer.setVisibility(View.VISIBLE);

        noItem.setVisibility(View.GONE);


        itemList.clear();
        adapter.notifyDataSetChanged();
        searchTitle.setVisibility(View.GONE);



        isLoading = true;



        String url="https://api-prod.evaly.com.bd/api/categories/?search="+query+"&page=1";
        Log.d("json search", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    isLoading = false;




                    itemList.clear();
                    adapter.notifyDataSetChanged();




                    progressContainer.setVisibility(View.GONE);

                    searchTitle.setVisibility(View.VISIBLE);

                    Log.d("json search", response.toString());

                    try {

                        JSONArray result = response.getJSONArray("results");

                        if (!search.getText().toString().trim().equals("")){

                            for (int i = 0; i < result.length(); i++) {

                                itemList.add(result.getJSONObject(i));
                                adapter.notifyItemInserted(i);
                            }
                        }


                        if (itemList.size() < 1){


                            noItem.setVisibility(View.VISIBLE);
                            noText.setText("No categories found");
                            searchTitle.setVisibility(View.GONE);

                        }



                    } catch (Exception e){

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
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
        request.setTag(this);
        request.setShouldCache(false);
        rq.getCache().clear();
        rq.add(request);


    }



    public static CharSequence highlight(String search, String originalText) {
        // ignore case and accents
        // the same thing should have been done for the search text
        String normalizedText = Normalizer
                .normalize(originalText, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ENGLISH);

        int start = normalizedText.indexOf(search.toLowerCase(Locale.ENGLISH));
        if (start < 0) {
            // not found, nothing to to
            return originalText;
        } else {
            // highlight each appearance in the original text
            // while searching in normalized text
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(),
                        originalText.length());

                //  highlighted.setSpan(new ForegroundColorSpan(Color.BLUE), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                highlighted.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = normalizedText.indexOf(search, spanEnd);
            }

            return highlighted;
        }
    }

}
