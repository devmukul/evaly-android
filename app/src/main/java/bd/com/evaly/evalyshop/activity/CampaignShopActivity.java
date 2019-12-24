package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.TabsAdapter;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.TabsItem;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.views.StickyScrollView;
import retrofit2.Response;

public class CampaignShopActivity extends AppCompatActivity {

    TabsAdapter adapter;
    ArrayList<TabsItem> itemList;
    ProgressBar progressBar;
    int page=1;
    StickyScrollView nestedSV;
    RecyclerView recyclerView;
    ViewDialog dialog;
    Button loadMore;
    boolean isLoading = false;

    String title = "19.19 Shops";
    String slug = "evaly1919";

    LinearLayout not, layoutImageHolder;
    UserDetails userDetails;
    ImageView cover;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaly_shop);

        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();

        title = intent.getStringExtra("title");
        slug = intent.getStringExtra("slug");

        userDetails = new UserDetails(this);



        getSupportActionBar().setTitle(title);

        nestedSV = findViewById(R.id.sticky);
        progressBar = findViewById(R.id.progressBar);
        cover = findViewById(R.id.cover);
        not = findViewById(R.id.not);
        itemList=new ArrayList<>();

        adapter = new TabsAdapter(CampaignShopActivity.this, this, itemList, 3);
        nestedSV = findViewById(R.id.sticky);
        layoutImageHolder = findViewById(R.id.layoutImageHolder);
        recyclerView=findViewById(R.id.recycle);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        dialog=new ViewDialog(CampaignShopActivity.this);
       // dialog.showDialog();

        if (slug.equals("shop-subscriptions"))
            getFollowedShops(page);
        else
            getEvalyShops(page);


        progressBar.setVisibility(View.VISIBLE);


        nestedSV = findViewById(R.id.sticky);
        if (nestedSV != null) {

            nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {

                    if (slug.equals("shop-subscriptions"))
                        getFollowedShops(page);
                    else
                        getEvalyShops(page);

                }
            });
        }

    }



    public void getEvalyShops(int p){


        progressBar.setVisibility(View.VISIBLE);

        CampaignApiHelper.getCampaignShops(slug, p, new ResponseListenerAuth<CommonDataResponse<List<CampaignShopItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignShopItem>> response, int statusCode) {

                progressBar.setVisibility(View.INVISIBLE);

                findViewById(R.id.title).setVisibility(View.VISIBLE);


                try {
                    JsonObject meta = response.getMeta();
                    Glide.with(CampaignShopActivity.this)
                            .load(meta.get("campaign_banner").getAsString())
                            .into(cover);

                    cover.setOnClickListener(view -> {

                        Intent intent = new Intent(CampaignShopActivity.this, ImagePreview.class);
                        intent.putExtra("image", meta.get("campaign_banner").getAsString());
                        startActivity(intent);

                    });


                } catch (Exception e){

                }


                List<CampaignShopItem> list = response.getData();

                for (int i=0; i<list.size(); i++){
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setType(6);
                    tabsItem.setTitle(list.get(i).getShopName());
                    tabsItem.setImage(list.get(i).getShopImage());
                    tabsItem.setSlug(list.get(i).getSlug());
                    tabsItem.setCategory("root");
                    tabsItem.setCampaignSlug(slug);
                    itemList.add(tabsItem);
                    adapter.notifyItemInserted(itemList.size());
                }

                if (list.size() > 0)
                    page++;


                if (page == 1 && list.size() == 0){

                    not.setVisibility(View.VISIBLE);

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



    public void getFollowedShops(int p){

        layoutImageHolder.setVisibility(View.GONE);

        isLoading = true;

        progressBar.setVisibility(View.VISIBLE);

        String url = UrlUtils.BASE_URL+"shop-subscriptions";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url , new JSONObject(),
                response -> {
                    try {
                        isLoading = false;
                        JSONArray jsonArray;

                        if (slug.equals("shop-subscriptions"))
                            jsonArray = response.getJSONArray("data");
                        else
                            jsonArray = response.getJSONArray("shops");

                        boolean b=false;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            TabsItem tabsItem = new TabsItem();
                            tabsItem.setTitle(ob.getString("name"));
                            tabsItem.setImage(ob.getString("logo_image"));
                            tabsItem.setSlug(ob.getString("slug"));
                            tabsItem.setCategory("root");
                            itemList.add(tabsItem);
                            adapter.notifyItemInserted(itemList.size());
                        }

                        if (jsonArray.length() == 0)
                            not.setVisibility(View.VISIBLE);
                        else {
                            not.setVisibility(View.GONE);
                            page++;
                        }

                        // dialog.hideDialog();

                        progressBar.setVisibility(View.INVISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } , error -> {

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {

                if (error.networkResponse.statusCode == 401){

                    AuthApiHelper.refreshToken(CampaignShopActivity.this, new DataFetchingListener<Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            getEvalyShops(p);
                        }

                        @Override
                        public void onFailed(int status) {
                        }
                    });
                    return;
                }}
            progressBar.setVisibility(View.GONE);
            Toast.makeText(CampaignShopActivity.this, "All shops are loaded", Toast.LENGTH_SHORT).show();

            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", CredentialManager.getToken());
                return headers;
            }
        };
        RequestQueue rq = Volley.newRequestQueue(this);
        request.setShouldCache(false);

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
