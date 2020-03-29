package bd.com.evaly.evalyshop.ui.campaign;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ShopApiHelper;
import bd.com.evaly.evalyshop.ui.browseProduct.tabs.adapter.TabsAdapter;
import bd.com.evaly.evalyshop.util.ImagePreview;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.views.StickyScrollView;

public class CampaignShopActivity extends AppCompatActivity {

    private TabsAdapter adapter;
    private ArrayList<TabsItem> itemList;
    private ProgressBar progressBar;
    private int page=1;
    private StickyScrollView nestedSV;
    private RecyclerView recyclerView;
    private ViewDialog dialog;
    private boolean isLoading = false;
    private String title = "19.19 Shops";
    private String slug = "evaly1919";
    private LinearLayout not, layoutImageHolder;
    private UserDetails userDetails;
    private ImageView cover;
    
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


        if (slug.equals("shop-subscriptions"))
            getFollowedShops(page);
        else
            getEvalyShops(page);

        progressBar.setVisibility(View.VISIBLE);

        nestedSV = findViewById(R.id.sticky);

//        if (nestedSV != null) {
//            nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
//                    if (slug.equals("shop-subscriptions"))
//                        getFollowedShops(page);
//                    else
//                        getEvalyShops(page);
//                }
//            });
//        }

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
                } catch (Exception ignored){ }

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

                if (page == 1 && list.size() == 0)
                    not.setVisibility(View.VISIBLE);

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

        ShopApiHelper.getFollowedShop(CredentialManager.getToken(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                isLoading = false;
                JsonArray jsonArray;

                if (slug.equals("shop-subscriptions"))
                    jsonArray = response.getAsJsonArray("data");
                else
                    jsonArray = response.getAsJsonArray("shops");

                boolean b=false;
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject ob = jsonArray.get(i).getAsJsonObject();
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(ob.get("name").getAsString());
                    tabsItem.setImage(ob.get("logo_image").getAsString());
                    tabsItem.setSlug(ob.get("slug").getAsString());
                    tabsItem.setCategory("root");
                    itemList.add(tabsItem);
                    adapter.notifyItemInserted(itemList.size());
                }

                if (jsonArray.size() == 0)
                    not.setVisibility(View.VISIBLE);
                else {
                    not.setVisibility(View.GONE);
                    page++;
                }


                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

                if (!logout)
                    getFollowedShops(p);

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
}
