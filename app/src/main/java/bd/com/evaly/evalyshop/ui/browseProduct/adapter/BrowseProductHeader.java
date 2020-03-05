package bd.com.evaly.evalyshop.ui.browseProduct.adapter;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.home.adapter.HomeTabPagerAdapter;
import bd.com.evaly.evalyshop.ui.tabs.SubTabsFragment;

public class BrowseProductHeader extends RecyclerView.ViewHolder {

    View view;
    private Fragment fragmentInstance;
    private AppCompatActivity activityInstance;
    private NavController navController;
    private Context context;
    private TabLayout tabLayoutSub;
    private HomeTabPagerAdapter pager;
    private ViewPager viewPager;
    private LinearLayout lin;
    private ShimmerFrameLayout shimmer;
    private ShimmerFrameLayout shimmerTabs;
    private FrameLayout shimmerHolder;
    private boolean isShimmerShowed = false;
    private String category = "root";

    public BrowseProductHeader(View itemView, Context context, AppCompatActivity activityInstance, Fragment fragmentInstance, NavController navController, String category) {
        super(itemView);
        this.context = context;
        this.fragmentInstance = fragmentInstance;
        this.activityInstance = activityInstance;
        this.navController = navController;
        this.category = category;

        view = itemView;
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.setFullSpan(true);
        initHeader(view);


        Log.d("hmt", "loading other tabs");
    }

    private void initHeader(View view) {

        tabLayoutSub = view.findViewById(R.id.tab_layout_sub_cat);

        TextView filter = view.findViewById(R.id.filterBtn);
        filter.setVisibility(View.GONE);
        viewPager = view.findViewById(R.id.pager_sub);

        pager = new HomeTabPagerAdapter(fragmentInstance.getFragmentManager());

        shimmer = view.findViewById(R.id.shimmer);
        shimmer.startShimmer();
        shimmerTabs = view.findViewById(R.id.shimmerTabs);
        shimmerHolder = view.findViewById(R.id.shimmerHolder);
        shimmerTabs.startShimmer();

        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(pager);
        tabLayoutSub.setupWithViewPager(viewPager);

        tabLayoutSub.setTabMode(TabLayout.MODE_FIXED);
        tabLayoutSub.setSmoothScrollingEnabled(true);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayoutSub));
        tabLayoutSub.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
    }


    public void getSubCategories() {

        shimmer.startShimmer();
        shimmer.setVisibility(View.VISIBLE);
        tabLayoutSub.setVisibility(View.GONE);

        ProductApiHelper.getSubCategories(category, new ResponseListenerAuth<JsonArray, String>() {

            @Override
            public void onDataFetched(JsonArray res, int statusCode) {

                if (context == null)
                    return;

                int length = res.size();

                if (length > 0) {
                    SubTabsFragment fragment = new SubTabsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 1);
                    bundle.putString("slug", category);
                    bundle.putString("category", category);
                    bundle.putString("json", res.toString());
                    fragment.setArguments(bundle);

                    pager.addFragment(fragment, context.getResources().getString(R.string.sub_categories));
                    pager.notifyDataSetChanged();
                }

                hideShimmer();
                tabLayoutSub.setVisibility(View.VISIBLE);
                loadOtherTabs();
            }

            @Override
            public void onFailed(String body, int errorCode) {

                Log.d("jsonz", "Response " + body);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    private void loadOtherTabs() {


        if (context == null)
            return;
        {
            SubTabsFragment fragment = new SubTabsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", 2);
            bundle.putString("slug", category);
            bundle.putString("category", category);
            bundle.putString("json", "");
            fragment.setArguments(bundle);
            pager.addFragment(fragment, context.getResources().getString(R.string.brands));
            pager.notifyDataSetChanged();
        }

        {
            SubTabsFragment fragment = new SubTabsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", 3);
            bundle.putString("slug", category);
            bundle.putString("category", category);
            bundle.putString("json", "");
            fragment.setArguments(bundle);
            pager.addFragment(fragment, context.getResources().getString(R.string.shops));
            pager.notifyDataSetChanged();
        }
    }

    public void hideShimmer() {

        shimmerHolder.animate().alpha(0.0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        shimmerTabs.setVisibility(View.GONE);
                        shimmer.stopShimmer();
                        shimmerTabs.stopShimmer();
                        shimmer.setVisibility(View.GONE);
                    }
                });


        isShimmerShowed = true;
    }
}