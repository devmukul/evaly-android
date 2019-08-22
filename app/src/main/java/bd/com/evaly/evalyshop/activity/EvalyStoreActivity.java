package bd.com.evaly.evalyshop.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.TabsAdapter;
import bd.com.evaly.evalyshop.util.TabsItem;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.views.StickyScrollView;

public class EvalyStoreActivity extends AppCompatActivity {

    TabsAdapter adapter;
    ArrayList<TabsItem> itemList;
    ProgressBar progressBar;
    int page=1;
    StickyScrollView nestedSV;
    RecyclerView recyclerView;
    ViewDialog dialog;
    Button loadMore;

    boolean isLoading = false;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaly_shop);

        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Evaly Stores");

        nestedSV = findViewById(R.id.sticky);
        progressBar = findViewById(R.id.progressBar);
        itemList=new ArrayList<>();


        adapter = new TabsAdapter(EvalyStoreActivity.this, this, itemList, 3);
        nestedSV = findViewById(R.id.sticky);
        recyclerView=findViewById(R.id.recycle);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        dialog=new ViewDialog(EvalyStoreActivity.this);
       // dialog.showDialog();
        getEvalyShops(page);


        progressBar.setVisibility(View.VISIBLE);

        if (nestedSV != null) {
            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i(TAG, "BOTTOM SCROLL");
                        try {

                            if(!isLoading)
                                loadNextShops();

                        } catch (Exception e) {
                            Log.e("load more product", e.toString());
                        }
                    }
                }
            });
        }



    }

    public void getEvalyShops(int p){

        isLoading = true;

        progressBar.setVisibility(View.VISIBLE);

        String url="https://api.evaly.com.bd/core/custom/shops/?&search=evaly&page="+p;
        Log.d("json", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {

                        isLoading = false;

                        JSONArray jsonArray = response.getJSONArray("data");
                        Log.d("search_result",response.toString());
                        boolean b=false;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            if(ob.getInt("total_shop_items")>0){
                                TabsItem tabsItem = new TabsItem();
                                tabsItem.setTitle(ob.getString("shop_name"));
                                tabsItem.setImage(ob.getString("shop_image"));
                                tabsItem.setSlug(ob.getString("slug"));
                                tabsItem.setCategory("root");
                                itemList.add(tabsItem);
                                adapter.notifyItemInserted(itemList.size());
                                b=true;
                            }
                        }

                       // dialog.hideDialog();


                        if(p == 1)
                            loadNextShops();

                        progressBar.setVisibility(View.INVISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(EvalyStoreActivity.this, "All evaly stores are loaded", Toast.LENGTH_SHORT).show();

                error.printStackTrace();
            }
        });
        RequestQueue rq = Volley.newRequestQueue(this);
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
        rq.add(request);
    }

    public void loadNextShops(){
        getEvalyShops(++page);
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
