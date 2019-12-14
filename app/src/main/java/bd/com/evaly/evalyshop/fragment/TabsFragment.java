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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.GlobalSearchActivity;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.SearchCategory;
import bd.com.evaly.evalyshop.adapter.HomeCategoryAdapter2;
import bd.com.evaly.evalyshop.adapter.TabsAdapter;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.TabsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.util.CategoryUtils;
import bd.com.evaly.evalyshop.util.UrlUtils;

public class TabsFragment extends Fragment {

    RecyclerView recyclerView;
    TabsAdapter adapter;
    Context context;
    Map<String,Integer> categoryMap;
    HomeCategoryAdapter2 adapter2;
    ArrayList<TabsItem> itemList;
    int type = 1;
    String slug = "root";
    String category;
    EditText search;
    Button showMore;

    private View view;
    boolean isEmpty = false;
    int brandCounter=1,shopCounter=1;
    ArrayList<String> titleCategory2;
    ArrayList<Integer> imageCategory2;
    ProgressBar progressBar2;
    RequestQueue rq;
    private List<CategoryEntity> categoryItems;
    public ShimmerFrameLayout shimmer;
    public ShimmerFrameLayout shimmerParent;


    public TabsFragment(){
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_category, container, false);
        context = getContext();
        categoryMap=new HashMap<>();
        shimmer = view.findViewById(R.id.shimmer);


        Bundle bundle = getArguments();

        category = bundle.getString("category");
        type = bundle.getInt("type");
        slug = bundle.getString("slug");


        try {

            shimmer.startShimmer();
        } catch (Exception e){

        }

        rq = Volley.newRequestQueue(context);

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


        categoryItems = new ArrayList<>();

        if (slug.equals("root") && type == 1) {


            CategoryUtils categoryUtils = new CategoryUtils(context);

            adapter2 = new HomeCategoryAdapter2(context, categoryItems);
            recyclerView.setAdapter(adapter2);

            adapter2.notifyDataSetChanged();

            Calendar calendar = Calendar.getInstance();

            if (categoryUtils.getLastUpdated() == 0 || (categoryUtils.getLastUpdated() != 0 && calendar.getTimeInMillis() - categoryUtils.getLastUpdated() > 43200000)){
                categoryUtils.updateFromApi(new DataFetchingListener<List<CategoryEntity>>() {
                    @Override
                    public void onDataFetched(List<CategoryEntity> response) {

                        getActivity().runOnUiThread(() -> {
                            categoryItems.addAll(response);
                            adapter2.notifyDataSetChanged();
                        });

                       // skeletonScreen.hide();
                    }

                    @Override
                    public void onFailed(int status) {

                    }
                });
            } else {

                categoryUtils.getLocalCategoryList(new DataFetchingListener<List<CategoryEntity>>() {
                    @Override
                    public void onDataFetched(List<CategoryEntity> response) {


                        getActivity().runOnUiThread(() -> {
                            categoryItems.addAll(response);
                            adapter2.notifyDataSetChanged();
                        });

                    }

                    @Override
                    public void onFailed(int status) {

                    }
                });

                //skeletonScreen.hide();

            }


            Handler handler = new Handler();
            handler.postDelayed(() -> stopShimmer(), 300);

            search.setHint("Search categories");
            showMore.setVisibility(View.GONE);
            search.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setAdapter(adapter);

        }

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


        loadData();
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
                } else {
                    if (adapter2.getItemCount() < 1 || recyclerView.getHeight() < 100) {
                        adapter2.notifyDataSetChanged();
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





    public void getSubCategories(){

        ProductApiHelper.getSubCategories(slug, new ResponseListener<retrofit2.Response<JSONArray>, String>() {

            @Override
            public void onDataFetched(retrofit2.Response<JSONArray> res, int statusCode) {

                progressBar2.setVisibility(View.GONE);

                try {

                    JSONArray response = res.body();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject ob = response.getJSONObject(i);
                        TabsItem tabsItem = new TabsItem();
                        tabsItem.setTitle(ob.getString("name"));
                        tabsItem.setImage(ob.getString("image_url"));
                        tabsItem.setSlug(ob.getString("slug"));
                        tabsItem.setCategory(category);
                        itemList.add(tabsItem);
                        adapter.notifyItemInserted(itemList.size());
                    }
                }  catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(String body, int errorCode) {
                progressBar2.setVisibility(View.GONE);
            }
        });


    }

    public void getBrandsOfCategory(int counter){
        ProductApiHelper.getBrandsOfCategories(slug, counter, 12, new ResponseListener<retrofit2.Response<JsonObject>, String>() {
            @Override
            public void onDataFetched(retrofit2.Response<JsonObject> res, int statusCode) {

                progressBar2.setVisibility(View.GONE);
                try {

                    JsonArray jsonArray = res.body().getAsJsonArray("results");

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
        });

    }







    public void getShopsOfCategory(int counter){
        String url;
        if (slug.equals("root"))
            url = UrlUtils.BASE_URL+"custom/shops/?page="+counter+"&limit=13";
        else
            url = UrlUtils.BASE_URL+"public/category/shops/"+slug+"/?limit=12&page="+counter;
        Log.d("json", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    progressBar2.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            //if(ob.getInt("selling_items__count")>0){ }
                            TabsItem tabsItem = new TabsItem();
                            tabsItem.setTitle(ob.getString("shop_name"));
                            tabsItem.setImage(ob.getString("shop_image"));

                            if (slug.equals("root"))
                                tabsItem.setSlug(ob.getString("slug"));
                            else
                                tabsItem.setSlug(ob.getString("shop_slug"));


                            tabsItem.setCategory(category);
                            itemList.add(tabsItem);
                            adapter.notifyItemInserted(itemList.size());

                        }


                        stopShimmer();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                progressBar2.setVisibility(View.GONE);
            }
        });


        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        request.setShouldCache(false);

        rq.add(request);
    }

}
