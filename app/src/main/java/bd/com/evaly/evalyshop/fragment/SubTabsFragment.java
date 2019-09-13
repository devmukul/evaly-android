package bd.com.evaly.evalyshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.GlobalSearchActivity;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.SearchCategory;
import bd.com.evaly.evalyshop.adapter.HomeCategoryAdapter2;
import bd.com.evaly.evalyshop.adapter.TabsAdapter;
import bd.com.evaly.evalyshop.models.TabsItem;
import bd.com.evaly.evalyshop.models.TransactionItem;
import bd.com.evaly.evalyshop.util.UrlUtils;

public class SubTabsFragment extends Fragment {

    RecyclerView recyclerView;
    TabsAdapter adapter;
    Context context;
    HomeCategoryAdapter2 adapter2;
    ArrayList<TabsItem> itemList;
    int type = 1;
    String slug = "root";
    String category;
    EditText search;
    Button showMore;
    Fragment parentIntance;
    private View view;
    boolean isEmpty = false;
    int brandCounter=1,shopCounter=1;
    ArrayList<String> titleCategory2;
    ArrayList<Integer> imageCategory2;
    ProgressBar progressBar2;
    RequestQueue rq;
    public ShimmerFrameLayout shimmer;
    public ShimmerFrameLayout shimmerParent;

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


        recyclerView.setAdapter(adapter);


        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar2.setVisibility(View.VISIBLE);

                if (type == 2) {
                    getBrandsOfCategory(++brandCounter);
                } else if (type == 3) {
                    getShopsOfCategory(++shopCounter);
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(type == 1){

                    Intent intent = new Intent(context, SearchCategory.class);
                    intent.putExtra("type", type);
                    context.startActivity(intent);

                } else {

                    Intent intent = new Intent(context, GlobalSearchActivity.class);

                    intent.putExtra("type", type);
                    context.startActivity(intent);
                }

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




    public void loadJsonToView(String json, int type){

        try {

            JSONArray response = new JSONArray(json);


            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject ob = response.getJSONObject(i);
                    TabsItem tabsItem = new TabsItem();

                    if (type == 3){
                        tabsItem.setTitle(ob.getString("shop_name"));
                        tabsItem.setImage(ob.getString("shop_image"));
                        tabsItem.setSlug(ob.getString("shop_slug"));
                    } else {
                        tabsItem.setTitle(ob.getString("name"));
                        tabsItem.setImage(ob.getString("image_url"));
                        tabsItem.setSlug(ob.getString("slug"));
                    }

                    tabsItem.setCategory(category);
                    itemList.add(tabsItem);
                    adapter.notifyItemInserted(itemList.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            stopShimmer();
        } catch (Exception e){

        }


    }





    public void getSubCategories(){
        String url;
        if (slug.equals("root"))
            url = UrlUtils.BASE_URL+"public/categories/";
        else
            url = UrlUtils.BASE_URL+"public/categories/?parent="+slug;
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null,
                response -> {


                    progressBar2.setVisibility(View.GONE);

                    Log.d("sub_categories", response.toString());
                    for(int i=0;i<response.length();i++){
                        try {
                            JSONObject ob = response.getJSONObject(i);
                            TabsItem tabsItem = new TabsItem();
                            tabsItem.setTitle(ob.getString("name"));
                            tabsItem.setImage(ob.getString("image_url"));
                            tabsItem.setSlug(ob.getString("slug"));
                            tabsItem.setCategory(category);
                            itemList.add(tabsItem);
                            adapter.notifyItemInserted(itemList.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    stopShimmer();
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                progressBar2.setVisibility(View.GONE);
            }
        });


        getRequest.setShouldCache(false);
        getRequest.setRetryPolicy(new RetryPolicy() {
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
                getSubCategories();
            }
        });

        // rq.getCache().clear();
        rq.add(getRequest);
    }

    public void getBrandsOfCategory(int counter){


        String url;
        if (slug.equals("root"))
            url = UrlUtils.BASE_URL+"public/brands/?limit=12&page="+counter;
        else
            url = UrlUtils.BASE_URL+"public/brands/?limit=12&category="+slug+"&page="+counter;


        Log.d("json", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {


                    progressBar2.setVisibility(View.GONE);

                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        Log.d("category_brands",jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            TabsItem tabsItem = new TabsItem();
                            tabsItem.setTitle(ob.getString("name"));
                            tabsItem.setImage(ob.getString("image_url"));
                            tabsItem.setSlug(ob.getString("slug"));
                            tabsItem.setCategory(category);
                            itemList.add(tabsItem);
                            adapter.notifyItemInserted(itemList.size());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "brand_error", Toast.LENGTH_SHORT).show();
                    }

                    stopShimmer();

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                progressBar2.setVisibility(View.GONE);
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
        if(counter>1) {

            request.setShouldCache(false);
            rq.getCache().clear();
        }
        rq.add(request);
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

        if(counter>1) {
            rq.getCache().clear();
            request.setShouldCache(false);
        }
        rq.add(request);
    }

}
