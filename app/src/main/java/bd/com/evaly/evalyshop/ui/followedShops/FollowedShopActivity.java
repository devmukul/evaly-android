package bd.com.evaly.evalyshop.ui.followedShops;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;
import bd.com.evaly.evalyshop.models.shop.FollowResponse;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.ImagePreview;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.views.StickyScrollView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FollowedShopActivity extends AppCompatActivity {

    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    ApiRepository apiRepository;
    private TabsAdapter adapter;
    private ArrayList<TabsItem> itemList;
    private ProgressBar progressBar;
    private int page = 1;
    private StickyScrollView nestedSV;
    private RecyclerView recyclerView;
    private ViewDialog dialog;
    private boolean isLoading = false;
    private String title = "19.19 Shops";
    private String slug = "evaly1919";
    private LinearLayout not, layoutImageHolder;
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


        getSupportActionBar().setTitle(title);

        nestedSV = findViewById(R.id.sticky);
        progressBar = findViewById(R.id.progressBar);
        cover = findViewById(R.id.cover);
        not = findViewById(R.id.not);
        itemList = new ArrayList<>();

        adapter = new TabsAdapter(FollowedShopActivity.this, this, itemList, 3);
        nestedSV = findViewById(R.id.sticky);
        layoutImageHolder = findViewById(R.id.layoutImageHolder);
        recyclerView = findViewById(R.id.recycle);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        dialog = new ViewDialog(FollowedShopActivity.this);

        if (slug.equals("shop-subscriptions"))
            getFollowedShops(page);
        else
            getEvalyShops(page);

        progressBar.setVisibility(View.VISIBLE);

        nestedSV = findViewById(R.id.sticky);

        nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                if (!isLoading) {
                    if (slug.equals("shop-subscriptions"))
                        getFollowedShops(page);
                    else
                        getEvalyShops(page);
                }
            }
        });
    }


    public void getEvalyShops(int p) {

        progressBar.setVisibility(View.VISIBLE);

        apiRepository.getCampaignShops(slug, p, new ResponseListener<CommonDataResponse<List<CampaignShopItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CampaignShopItem>> response, int statusCode) {

                progressBar.setVisibility(View.INVISIBLE);
                findViewById(R.id.title).setVisibility(View.VISIBLE);

                try {
                    JsonObject meta = response.getMeta();
                    Glide.with(FollowedShopActivity.this)
                            .load(meta.get("campaign_banner").getAsString())
                            .into(cover);

                    cover.setOnClickListener(view -> {
                        Intent intent = new Intent(FollowedShopActivity.this, ImagePreview.class);
                        intent.putExtra("image", meta.get("campaign_banner").getAsString());
                        startActivity(intent);

                    });
                } catch (Exception ignored) {
                }

                List<CampaignShopItem> list = response.getData();

                for (int i = 0; i < list.size(); i++) {
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

        });
    }


    public void getFollowedShops(int p) {

        layoutImageHolder.setVisibility(View.GONE);
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        apiRepository.getFollowedShop(page, new ResponseListener<CommonDataResponse<JsonObject>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<JsonObject> response, int statusCode) {
                isLoading = false;

                List<FollowResponse> list = new Gson().fromJson(response.getData().get("shop_list").getAsJsonArray(), new TypeToken<List<FollowResponse>>() {
                }.getType());

                for (FollowResponse item : list) {
                    TabsItem tabsItem = new TabsItem();
                    tabsItem.setTitle(item.getShopName());
                    tabsItem.setImage(item.getShopImageUrl());
                    tabsItem.setSlug(item.getShopSlug());
                    tabsItem.setCategory("root");
                    itemList.add(tabsItem);
                    adapter.notifyItemInserted(itemList.size());
                }

                if (response.getData().get("count").getAsInt() == 0)
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
