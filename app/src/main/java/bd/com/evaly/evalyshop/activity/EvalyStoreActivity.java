package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonSuccessResponse;
import bd.com.evaly.evalyshop.models.TabsItem;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
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

    String title = "19.19 Shops";
    String slug = "evaly1919";

    LinearLayout not;
    UserDetails userDetails;
    
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
        not = findViewById(R.id.not);
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


    }



    public void getEvalyShops(int page){


        CampaignApiHelper.getCampaignShops(slug, new ResponseListener<CommonSuccessResponse<List<CampaignShopItem>>, String>() {
            @Override
            public void onDataFetched(CommonSuccessResponse<List<CampaignShopItem>> response, int statusCode) {

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

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }
        });


    }







    public void getEvalyShop(int p){

        isLoading = true;

        progressBar.setVisibility(View.VISIBLE);

        String url;

        url = UrlUtils.BASE_URL+"shop-groups/"+slug+"/";

        if (slug.equals("shop-subscriptions"))
            url = UrlUtils.BASE_URL+"shop-subscriptions";


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url ,(String) null,
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
                        else
                            not.setVisibility(View.GONE);

                       // dialog.hideDialog();

                        progressBar.setVisibility(View.INVISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } , error -> {

                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {

                        if (error.networkResponse.statusCode == 401){

                        AuthApiHelper.refreshToken(EvalyStoreActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
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
                    Toast.makeText(EvalyStoreActivity.this, "All shops are loaded", Toast.LENGTH_SHORT).show();

                    error.printStackTrace();
                }){


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", "Bearer " + userDetails.getToken());


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
