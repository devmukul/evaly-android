package bd.com.evaly.evalyshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
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
import bd.com.evaly.evalyshop.util.TabsItem;

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


    Fragment parentIntance;


    private View view;

    boolean isEmpty = false;
    int brandCounter=1,shopCounter=1;
    ArrayList<String> titleCategory2;
    ArrayList<Integer> imageCategory2;
    ProgressBar progressBar2;
    RequestQueue rq;

    public static String[] titleCategory = {
            "Bags & Luggage",
            "Beauty & Body Care",
            "Books",
            "Burmese Products",
            "Construction Materials",
            "Decoration Materials",
            "Dhaka Bank  MSME  Bazar",
            "Electronics & Appliance",
            "Electric & Parts",
            "Event & Media",
            "Food & Beverage",
            "Food & Restaurants",
            "Furniture",
            "Glasses",
            "Grocery",
            "Handmade",
            "Harvesting & Agriculture",
            "Health Care & Pharmaceutical",
            "Home & Living",
            "Home Garden",
            "Hotels Booking",
            "Jewellery",
            "Kids",
            "Kitchen & Dining",
            "Leather Goods",
            "LP Gas",
            "Machineries",
            "Men",
            "Paints",
            "Pet & Poultry Supplies",
            "Plastic Made Products",
            "Property",
            "Services",
            "Shoes",
            "Sports",
            "Stationeries",
            "Vehicles & Parts",
            "Watch & Clock",
            "Women"


    } ;

    public static int[] imageCategory = {
            R.drawable.ic_bags_set,
            R.drawable.ic_color_lotion,
            R.drawable.ic_color_books,
            R.drawable.burmes_item,
            R.drawable.ic_color_construction,
            R.drawable.ic_color_decoration,
            R.drawable.dhaka_bank_logo,
            R.drawable.ic_color_multiple_devices,
            R.drawable.ic_color_electric,

            R.drawable.ic_color_event,
            R.drawable.ic_color_beverage,
            R.drawable.ic_color_food_plate,
            R.drawable.ic_color_sliding_door_closet,
            R.drawable.ic_color_glasses_new,
            R.drawable.ic_color_ingredients,
            R.drawable.ic_color_potters_wheel,
            R.drawable.ic_color_harvest,
            R.drawable.ic_color_health_checkup_1,
            R.drawable.ic_color_open_curtains,
            R.drawable.ic_color_orchid,
            R.drawable.ic_color_hotel_building,
            R.drawable.ic_color_jewelry,
            R.drawable.ic_color_kids,
            R.drawable.ic_color_kitchen,
            R.drawable.ic_color_jacket_bag,
            R.drawable.ic_color_gas,
            R.drawable.ic_color_sewing_machine,
            R.drawable.men_fashion,
            R.drawable.ic_color_paint_bucket,
            R.drawable.ic_color_dog,
            R.drawable.ic_color_bucket,
            R.drawable.ic_color_building,
            R.drawable.ic_color_maintenance,
            R.drawable.ic_color_new_shoes2,
            R.drawable.ic_color_sports,
            R.drawable.ic_color_pot,
            R.drawable.ic_color_vehicles,
            R.drawable.ic_color_wathces,
            R.drawable.female_fashion,

    };


    public ShimmerFrameLayout shimmer;

    public ShimmerFrameLayout shimmerParent;



    public TabsFragment(){

    }

    public TabsFragment(int type, String slug, String category, Fragment parentIntance){

        this.category = category;
        this.type = type;
        this.slug = slug;
        this.parentIntance = parentIntance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_category, container, false);
        context = getContext();
        categoryMap=new HashMap<>();
        shimmer = view.findViewById(R.id.shimmer);


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
        if (slug.equals("root") && type == 1) {
            titleCategory2 = new ArrayList<>();
            imageCategory2 = new ArrayList<>();
            for (int i = 0; i < titleCategory.length; i++) {
                titleCategory2.add(titleCategory[i]);
                imageCategory2.add(imageCategory[i]);
                categoryMap.put(titleCategory[i], imageCategory[i]);
            }
            adapter2 = new HomeCategoryAdapter2(context, titleCategory2, imageCategory2);
            recyclerView.setAdapter(adapter2);
            adapter2.notifyDataSetChanged();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                     stopShimmer();

                }
            }, 300);
            search.setHint("Search categories");
            showMore.setVisibility(View.GONE);
            search.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setAdapter(adapter);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (shimmer.isShimmerStarted())
                        hideParentShimmer();
                }
            }, 1200);
        }

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

    public void hideParentShimmer(){


        try {


            if (parentIntance instanceof HomeFragment)
                ((HomeFragment) parentIntance).hideShimmer();
            else if (parentIntance instanceof BrowseProductFragment)
                ((BrowseProductFragment) parentIntance).hideShimmer();
        } catch (Exception e){


            Log.e("ozii shimmer", e.toString());
        }
    }

    public void stopShimmer(){

        // hide parent shimmer
        hideParentShimmer();

        try {

            shimmer.stopShimmer();
        } catch (Exception e){

        }
        shimmer.setVisibility(View.GONE);
    }

    public void getSubCategories(){
        String url;
        if (slug.equals("root"))
            url = "https://api.evaly.com.bd/core/public/categories/";
        else
            url = "https://api.evaly.com.bd/core/public/categories/?parent="+slug;
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
                    if(response.length() < 1 && parentIntance instanceof BrowseProductFragment) {
                        ((BrowseProductFragment) parentIntance).removeTab(0);
                    }
                    Log.d("Response", response.toString());
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
            url = "https://api.evaly.com.bd/core/public/brands/?limit=12&page="+counter;
        else
            url = "https://api.evaly.com.bd/core/public/brands/?limit=12&category="+slug+"&page="+counter;


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
                        if(itemList.size() < 1 && parentIntance instanceof BrowseProductFragment) {
                             ((BrowseProductFragment) parentIntance).removeTab(1);
                        }
                        stopShimmer();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "brand_error", Toast.LENGTH_SHORT).show();
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

            request.setShouldCache(false);
            rq.getCache().clear();
        }
        rq.add(request);
    }

    public void getShopsOfCategory(int counter){
        String url;
        if (slug.equals("root"))
            url = "https://api.evaly.com.bd/core/custom/shops/?page="+counter+"&limit=13";
        else
            url = "https://api.evaly.com.bd/core/public/category/shops/"+slug+"/?limit=12&page="+counter;
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

                        if(itemList.size() < 1 && parentIntance instanceof BrowseProductFragment) {
                            ((BrowseProductFragment) parentIntance).removeTab(2);
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
