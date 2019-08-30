package bd.com.evaly.evalyshop.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.ProductGrid;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.GlobalSearchActivity;
import bd.com.evaly.evalyshop.activity.InitializeActionBar;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.adapter.HomeTabPagerAdapter;
import bd.com.evaly.evalyshop.adapter.ProductGridAdapter;
import bd.com.evaly.evalyshop.models.ProductListItem;

public class BrowseProductFragment extends Fragment {


    MainActivity activity;
    TabLayout tabLayout;
    HomeTabPagerAdapter pager;
    ViewPager viewPager;
    TextView filter;
    int type = 1;
    String slug;
    String category;
    LinearLayout lin;
    ShimmerFrameLayout shimmer;
    private boolean isShimmerShowed = false;
    RequestQueue rq;
    Map<String,String> map;
    String filterURL="";
    ArrayList<ProductListItem> itemListProduct;
    ProductGridAdapter adapterProduct;
    RecyclerView recyclerView;
    EditText minimum,maximum;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_product, container, false);

        activity = (MainActivity) getActivity();



        Bundle bundle = new Bundle();

        type = getArguments().getInt("type");
        slug = getArguments().getString("slug");
        category = getArguments().getString("category");

        Log.d("json", category);

        bundle.putString("category", category);
        filter=view.findViewById(R.id.filterBtn);
        recyclerView=view.findViewById(R.id.products);
        filter.setVisibility(View.GONE);
        progressBar=view.findViewById(R.id.progressBar);
        itemListProduct=new ArrayList<>();
        adapterProduct = new ProductGridAdapter(getContext(), itemListProduct);
        rq = Volley.newRequestQueue(getContext());
        map=new HashMap<>();

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
                ProductGrid productGrid = new ProductGrid(getContext(), view.findViewById(R.id.products),slug, view.findViewById(R.id.progressBar));
            }
        });

        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InitializeActionBar InitializeActionbar = new InitializeActionBar((LinearLayout) view.findViewById(R.id.header_logo), activity, "browse");

        tabLayout = view.findViewById(R.id.tab_layout_sub);

        LinearLayout homeSearch = view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), GlobalSearchActivity.class);


                intent.putExtra("type", 1);

                startActivity(intent);


            }
        });


        viewPager = view.findViewById(R.id.pager_sub);

        pager =  new HomeTabPagerAdapter(getChildFragmentManager());
        final ProductGrid productGrid = new ProductGrid(getContext(), view.findViewById(R.id.products),slug, view.findViewById(R.id.progressBar));


        TextView filterBtn = view.findViewById(R.id.filterBtn);
        //filterBtn.setOnClickListener(view -> drawer.openDrawer(Gravity.RIGHT));

        shimmer = view.findViewById(R.id.shimmer);
        shimmer.startShimmer();



        pager.addFragment(new TabsFragment(1, slug, category, this),"Sub Categories");
        pager.addFragment(new TabsFragment(2, slug, category, this),"Brands");
        pager.addFragment(new TabsFragment(3, slug, category, this),"Shops");


        viewPager.setOffscreenPageLimit(1);

        viewPager.setAdapter(pager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSmoothScrollingEnabled(true);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());



                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
        });


        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout);
        CoordinatorLayout rootLayout = view.findViewById(R.id.root_coordinator);

        NestedScrollView nestedSV = view.findViewById(R.id.stickyScrollView);

        if (nestedSV != null) {

            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";

//
//                    if (oldScrollY-scrollY >= 300) {
//                        // Log.i(TAG, "Scroll UP");
//
//
//                        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
//                        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//                        if (behavior != null && behavior.getTopAndBottomOffset() !=0) {
//                            behavior.setTopAndBottomOffset(0);
//                            appBarLayout.setExpanded(true, true);
//                        }
//
//                    }


                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        try {
                            progressBar.setVisibility(View.VISIBLE);
                            productGrid.loadNextPage();
                        } catch (Exception e)
                        {
                            Log.e("load more product", e.toString());
                        }
                    }
                }
            });
        }
    }

    public void removeTab(int position){



        if (tabLayout.getTabCount() >= 1 && position<tabLayout.getTabCount()) {
        }

        try {


            if(position == 0) {
                TabLayout.Tab tab = tabLayout.getTabAt(position+1);
                tab.select();
            }

            filter.setVisibility(View.VISIBLE);

            tabLayout.removeTabAt(position);


            pager.removeTabPage(position);


            pager.notifyDataSetChanged();

            //viewPager.setAdapter(pager);


             {
                TabLayout.Tab tab = tabLayout.getTabAt(0);
                tab.select();

            }




        } catch (Exception e){

            Log.e("ozii", e.toString());

        }

    }

    public void hideShimmer(){

        try {


            shimmer.stopShimmer();
        } catch (Exception e){
            Log.e("ozii shimmer", e.toString());

        }
        shimmer.setVisibility(View.GONE);
        isShimmerShowed = true;

    }

    public void getFilterAttributes(){
        String url="https://api-prod.evaly.com.bd/api/attributes/?category__slug="+slug;
        Log.d("filter_url",url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        if(response.length()==0){
                            //noFilterText();
                        }else{
                            Log.d("filtered_attr",response.toString());
                            for(int i=0;i<response.length();i++){
                                JSONObject ob=response.getJSONObject(i);
                                getAttributeOptions(ob.getString("name"),ob.getString("slug"),slug);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

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

    public void getAttributeOptions(String header,String slug,String filterSlug){
        ((LinearLayout) lin).removeAllViews();
        String url="https://api-prod.evaly.com.bd/api/options/?attribute__slug="+slug;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        ArrayList<String> values=new ArrayList<>();
                        values.add("Select Filter");
                        for(int i=0;i<response.length();i++){
                            JSONObject ob=response.getJSONObject(i);
                            values.add(ob.getString("value"));
                            map.put(ob.getString("value"),ob.getString("slug"));
                            //Log.d("abcdefg",header+"  "+ob.getString("value"));
                            if(i==response.length()-1){
                                TextView valueTV = new TextView(getContext());
                                valueTV.setText(header);
                                valueTV.setTextColor(Color.BLACK);
                                valueTV.setTypeface(null, Typeface.BOLD);
                                valueTV.setTextSize(16f);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(20,60,0,0);
                                valueTV.setLayoutParams(params);
                                ((LinearLayout) lin).addView(valueTV);
                                Spinner spinner = new Spinner(getContext());
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, values){
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View v = super.getView(position, convertView, parent);
                                        TextView tv = ((TextView) v);
                                        tv.setTextColor(Color.parseColor("#777777"));
                                        tv.setTypeface(null, Typeface.NORMAL);
                                        tv.setEllipsize(TextUtils.TruncateAt.END);
                                        tv.setTextSize(16f);
                                        return v;
                                    }
                                };
                                spinner.setAdapter(spinnerArrayAdapter);
                                ((LinearLayout) lin).addView(spinner);
                                View v=new View(getContext());
                                v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                                v.setBackgroundColor(Color.parseColor("#eeeeee"));
                                ((LinearLayout) lin).addView(v);
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                        if(!parentView.getItemAtPosition(position).equals("Select Filter")){
                                            if(filterURL.equals("")){
                                                //filterURL="https://api-prod.evaly.com.bd/api/product_filter/?category__slug="+filterSlug+"&page="+page+"&"+slug+"="+map.get(parentView.getItemAtPosition(position).toString());
                                                filterURL="https://api-prod.evaly.com.bd/api/product_filter/?category__slug="+filterSlug+"&"+slug+"="+map.get(parentView.getItemAtPosition(position).toString());
                                            }else{
                                                filterURL+="&"+slug+"="+map.get(parentView.getItemAtPosition(position).toString());
                                            }
                                        }else{
                                            if(!filterURL.equals("")){
                                                filterURL=filterURL.replace("&"+slug+"="+map.get(parentView.getItemAtPosition(position).toString()),"");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                        // your code here
                                    }

                                });
                                //filterButton.setClickable(true);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

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

    public void getFilteredItems(){
        //int index=filterURL.indexOf("page=");
        //filterURL=filterURL.replace(filterURL.charAt(index+5)+"",p+"");
        if(!minimum.getText().toString().equals("") && !maximum.getText().toString().equals("")){
            int minPrice=Integer.parseInt(minimum.getText().toString());
            int maxPrice=Integer.parseInt(maximum.getText().toString());
            filterURL="https://api-prod.evaly.com.bd/api/product_filter/?category__slug="+slug+"&price_max="+maxPrice+"&price_min="+minPrice;
        }
        Log.d("filter_url",filterURL);
        itemListProduct.clear();
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setAdapter(adapterProduct);
        recyclerView.setLayoutManager(mLayoutManager);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, filterURL,(String) null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        Log.d("search_result",response.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            ProductListItem tabsItem = new ProductListItem();
                            tabsItem.setName(ob.getString("name"));
                            tabsItem.setThumbnailSM(ob.getString("thumbnail"));
                            try {
                                tabsItem.setPriceMin((int) ob.getJSONObject("price_range").getDouble("price__min"));
                                tabsItem.setPriceMax((int) ob.getJSONObject("price_range").getDouble("price__max"));
                            } catch (Exception e) {
                                tabsItem.setPriceMin(0);
                                tabsItem.setPriceMax(0);
                            }
                            tabsItem.setSlug(ob.getString("slug"));
                            tabsItem.setCategorySlug(ob.getJSONObject("category").getString("slug"));
                            itemListProduct.add(tabsItem);
                            adapterProduct.notifyItemInserted(itemListProduct.size());
                        }
                        //progressBar.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

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

    public void filterDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.WideDialog));
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.product_filter_dialog, null);
        Button filterButton=dialogView.findViewById(R.id.header);
        lin=dialogView.findViewById(R.id.lin);
        minimum=dialogView.findViewById(R.id.minimum);
        maximum=dialogView.findViewById(R.id.maximum);
        dialogBuilder.setView(dialogView);
        getFilterAttributes();
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                getFilteredItems();
            }
        });
    }



}
