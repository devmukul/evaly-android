package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.TabsAdapter;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonSuccessResponse;
import bd.com.evaly.evalyshop.models.TabsItem;
import bd.com.evaly.evalyshop.models.campaign.CampaignShopItem;
import bd.com.evaly.evalyshop.rest.apiHelper.CampaignApiHelper;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.views.StickyScrollView;

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

    LinearLayout not;
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
        recyclerView=findViewById(R.id.recycle);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        dialog=new ViewDialog(CampaignShopActivity.this);
       // dialog.showDialog();
        getEvalyShops(page);


        progressBar.setVisibility(View.VISIBLE);


    }



    public void getEvalyShops(int page){


        CampaignApiHelper.getCampaignShops(slug, new ResponseListener<CommonSuccessResponse<List<CampaignShopItem>>, String>() {
            @Override
            public void onDataFetched(CommonSuccessResponse<List<CampaignShopItem>> response, int statusCode) {

                progressBar.setVisibility(View.GONE);

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


                if (list.size() == 0){

                    not.setVisibility(View.VISIBLE);

                }


            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                progressBar.setVisibility(View.GONE);

            }

        });


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
