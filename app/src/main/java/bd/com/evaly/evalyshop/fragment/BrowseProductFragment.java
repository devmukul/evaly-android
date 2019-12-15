package bd.com.evaly.evalyshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

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
import bd.com.evaly.evalyshop.util.UrlUtils;

public class BrowseProductFragment extends Fragment {


    MainActivity activity;
    TabLayout tabLayout;
    HomeTabPagerAdapter pager;
    ViewPager viewPager;
    TextView filter;
    int type = 1;
    String slug;
    String category = "";
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

    SkeletonScreen skeletonTabHeader;



    public BrowseProductFragment() {
        // Required empty public constructor
    }

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


        filter = view.findViewById(R.id.filterBtn);
        recyclerView = view.findViewById(R.id.products);
        filter.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progressBar);
        itemListProduct = new ArrayList<>();
        adapterProduct = new ProductGridAdapter(getContext(), itemListProduct);
        rq = Volley.newRequestQueue(getContext());
        map = new HashMap<>();



        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InitializeActionBar InitializeActionbar = new InitializeActionBar((LinearLayout) view.findViewById(R.id.header_logo), activity, "browse");

        tabLayout = view.findViewById(R.id.tab_layout_sub);

        try {

            skeletonTabHeader = Skeleton.bind(tabLayout)
                    .load(R.layout.skeleton_tablayout_header)
                    .color(R.color.ddd)
                    .show();

        } catch (Exception e){

        }

        LinearLayout homeSearch = view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), GlobalSearchActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        });


        viewPager = view.findViewById(R.id.pager_sub);

        pager =  new HomeTabPagerAdapter(getChildFragmentManager());


        NestedScrollView nestedSV = view.findViewById(R.id.stickyScrollView);

        final ProductGrid productGrid = new ProductGrid(getContext(), view.findViewById(R.id.products),slug, view.findViewById(R.id.progressBar));
        productGrid.setScrollView(nestedSV);

        TextView filterBtn = view.findViewById(R.id.filterBtn);
        //filterBtn.setOnClickListener(view -> drawer.openDrawer(Gravity.RIGHT));

        shimmer = view.findViewById(R.id.shimmer);
        shimmer.startShimmer();


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

        getSubCategories();




        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout);
        CoordinatorLayout rootLayout = view.findViewById(R.id.root_coordinator);


        if (nestedSV != null) {

            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";

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


    public void hideShimmer(){

        try {


            shimmer.stopShimmer();
        } catch (Exception e){
            Log.e("ozii shimmer", e.toString());

        }
        shimmer.setVisibility(View.GONE);
        isShimmerShowed = true;

    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        try {

            skeletonTabHeader.hide();

        } catch (Exception e){}

    }

    public void getSubCategories(){
        String url = UrlUtils.BASE_URL+"public/categories/?parent="+slug;
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null,
                response -> {

                    try {

                        skeletonTabHeader.hide();

                    }catch (Exception e){}

                    int length = response.length();

                    if (length>0)
                    {
                        SubTabsFragment fragment = new SubTabsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 1);
                        bundle.putString("slug", slug);
                        bundle.putString("category", category);
                        bundle.putString("json", response.toString());

                        fragment.setArguments(bundle);

                        pager.addFragment(fragment,"Sub Categories");
                        pager.notifyDataSetChanged();

                    }

                    
                    hideShimmer();
                    loadOtherTabs();


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });


        getRequest.setShouldCache(false);
        getRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // rq.getCache().clear();
        rq.add(getRequest);
    }



    public void loadOtherTabs(){

        {
            SubTabsFragment fragment = new SubTabsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", 2);
            bundle.putString("slug", slug);
            bundle.putString("category", category);
            bundle.putString("json", "");
            fragment.setArguments(bundle);
            pager.addFragment(fragment, "Brands");
            pager.notifyDataSetChanged();

        }


        {
            SubTabsFragment fragment = new SubTabsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", 3);
            bundle.putString("slug", slug);
            bundle.putString("category", category);
            bundle.putString("json", "");
            fragment.setArguments(bundle);
            pager.addFragment(fragment, "Shops");
            pager.notifyDataSetChanged();
        }


    }
}
