package bd.com.evaly.evalyshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.GlobalSearchActivity;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.SearchCategory;
import bd.com.evaly.evalyshop.adapter.TabsAdapter;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;

public class SubTabsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TabsAdapter adapter;
    private Context context;
    private ArrayList<TabsItem> itemList;
    private int type = 1;
    private String slug = "root";
    private String category;
    private EditText search;
    private Button showMore;
    private View view;
    private int brandCounter=1,shopCounter=1;
    private ProgressBar progressBar2;
    public ShimmerFrameLayout shimmer;

    private String json = "[]";

    public SubTabsFragment(){
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_category, container, false);
        context = getContext();
        shimmer = view.findViewById(R.id.shimmer);

        Bundle bundle = getArguments();

        category = bundle.getString("category");
        type = bundle.getInt("type");
        slug = bundle.getString("slug");
        json = bundle.getString("json");

        try {
            shimmer.startShimmer();
        } catch (Exception e){
        }


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showMore = view.findViewById(R.id.showMoreBtnTabs);
        search = view.findViewById(R.id.searchBtnTabs);
        recyclerView = view.findViewById(R.id.recycle);
        progressBar2 = view.findViewById(R.id.progressBar2);
        recyclerView.setNestedScrollingEnabled(false);

        itemList = new ArrayList<>();
        adapter = new TabsAdapter(context, (MainActivity) getActivity(), itemList, type);
        // check if showing offline homepage vector categories


        recyclerView.setAdapter(adapter);

        showMore.setOnClickListener(v -> {

            progressBar2.setVisibility(View.VISIBLE);

            if (type == 2) {
                getBrandsOfCategory(++brandCounter);
            } else if (type == 3) {
                getShopsOfCategory(++shopCounter);
            }
        });

        search.setOnClickListener(v -> {

            if(type == 1){
                Intent intent = new Intent(context, SearchCategory.class);
                intent.putExtra("type", type);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, GlobalSearchActivity.class);
                intent.putExtra("type", type);
                context.startActivity(intent);
            }
        });

        if (json.equals(""))
            loadData();
        else {
            loadJsonToView(json, type);
        }


    }

    public void loadData(){
        if(!(slug.equals("root") && type == 1)) {
            if (type == 1){
                search.setHint("Search categories");
                //search.setVisibility(View.GONE);
                showMore.setVisibility(View.GONE);
                getSubCategories();
            }
            else if (type == 2){
                search.setHint("Search brands");
                getBrandsOfCategory(1);
                showMore.setText("Show More");
            }
            else if (type == 3){
                search.setHint("Search shops");
                getShopsOfCategory(1);
                showMore.setText("Show More");
            }
        }
        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                if(!(slug.equals("root") && type == 1)) {
                    if (adapter.getItemCount() < 1 || recyclerView.getHeight() < 100) {
                        adapter.notifyDataSetChanged();
                        handler.postDelayed(this, 1000);
                    }
                }
            }
        }, 1000 );
    }



    public void stopShimmer(){

        try {
            shimmer.stopShimmer();
        } catch (Exception e){

        }
        shimmer.setVisibility(View.GONE);
    }

    public void loadJsonToView(String json, int type){

        try {
            JsonParser parser = new JsonParser();
            JsonElement tradeElement = parser.parse(json);
            JsonArray response = tradeElement.getAsJsonArray();

            for (int i = 0; i < response.size(); i++) {
                try {
                    JsonObject ob = response.get(i).getAsJsonObject();
                    TabsItem tabsItem = new TabsItem();

                    if (type == 3){
                        tabsItem.setTitle(ob.get("shop_name").getAsString());
                        tabsItem.setImage(ob.get("shop_image").getAsString());
                        tabsItem.setSlug(ob.get("shop_slug").getAsString());
                    } else {
                        tabsItem.setTitle(ob.get("name").getAsString());
                        tabsItem.setImage(ob.get("image_url").getAsString());
                        tabsItem.setSlug(ob.get("slug").getAsString());
                    }

                    tabsItem.setCategory(category);
                    itemList.add(tabsItem);
                    adapter.notifyItemInserted(itemList.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            stopShimmer();
        } catch (Exception e){

        }
    }



    public void getSubCategories(){

        ProductApiHelper.getSubCategories(slug, new ResponseListenerAuth<JsonArray, String>() {

            @Override
            public void onDataFetched(JsonArray res, int statusCode) {

                progressBar2.setVisibility(View.GONE);

                try {
                    JsonArray response = res;

                    for (int i = 0; i < response.size(); i++) {
                        JsonObject ob = response.get(i).getAsJsonObject();
                        TabsItem tabsItem = new TabsItem();
                        tabsItem.setTitle(ob.get("name").getAsString());
                        tabsItem.setImage(ob.get("image_url").getAsString());
                        tabsItem.setSlug(ob.get("slug").getAsString());
                        tabsItem.setCategory(category);
                        itemList.add(tabsItem);
                        adapter.notifyItemInserted(itemList.size());
                    }
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(String body, int errorCode) {
                progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });


    }

    public void getBrandsOfCategory(int counter){
        ProductApiHelper.getBrandsOfCategories(slug, counter, 12, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject res, int statusCode) {

                progressBar2.setVisibility(View.GONE);
                try {

                    JsonArray jsonArray = res.getAsJsonArray("results");

                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject ob = jsonArray.get(i).getAsJsonObject();
                        TabsItem tabsItem = new TabsItem();
                        tabsItem.setTitle(ob.get("name").getAsString());
                        tabsItem.setImage(ob.get("image_url").getAsString());
                        tabsItem.setSlug(ob.get("slug").getAsString());
                        tabsItem.setCategory(category);
                        itemList.add(tabsItem);
                        adapter.notifyItemInserted(itemList.size());
                    }

                    stopShimmer();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "brand_error", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailed(String body, int errorCode) {
                progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    public void getShopsOfCategory(int counter){
        ProductApiHelper.getShopsOfCategories(slug, counter, 12, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject res, int statusCode) {

                progressBar2.setVisibility(View.GONE);
                try {

                    JsonArray jsonArray = res.get("data").getAsJsonArray();

                    for (int i = 0; i < jsonArray.size(); i++) {

                        JsonObject ob = jsonArray.get(i).getAsJsonObject();
                        TabsItem tabsItem = new TabsItem();
                        tabsItem.setTitle(ob.get("shop_name").getAsString());
                        tabsItem.setImage(ob.get("shop_image").getAsString());

                        if (slug.equals("root"))
                            tabsItem.setSlug(ob.get("slug").getAsString());
                        else
                            tabsItem.setSlug(ob.get("shop_slug").getAsString());

                        tabsItem.setCategory(category);
                        itemList.add(tabsItem);
                        adapter.notifyItemInserted(itemList.size());
                    }

                    stopShimmer();

                } catch (Exception e) {

                    Log.e("jsonz error", e.toString());
                    Toast.makeText(context, "ShopDetails error", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailed(String body, int errorCode) {
                progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

}
