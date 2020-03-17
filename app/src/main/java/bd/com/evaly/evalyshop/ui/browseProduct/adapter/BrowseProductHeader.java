package bd.com.evaly.evalyshop.ui.browseProduct.adapter;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonArray;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.RecyclerHeaderBrowseProductBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.adapters.FragmentTabPagerAdapter;
import bd.com.evaly.evalyshop.ui.tabs.SubTabsFragment;

public class BrowseProductHeader extends RecyclerView.ViewHolder {

    private Context context;
    private boolean isShimmerShowed = false;
    private String category = "root";
    private RecyclerHeaderBrowseProductBinding binding;
    private FragmentTabPagerAdapter pager;
    private Fragment fragmentInstance;

    public BrowseProductHeader(RecyclerHeaderBrowseProductBinding binding, Context context, Fragment fragmentInstance, String category) {
        super(binding.getRoot());
        this.context = context;
        this.category = category;
        this.binding = binding;
        this.fragmentInstance = fragmentInstance;

        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        layoutParams.setFullSpan(true);
        initHeader();

    }

    private void initHeader() {

        pager = new FragmentTabPagerAdapter(fragmentInstance.getChildFragmentManager(), fragmentInstance.getLifecycle());


        binding.filterBtn.setVisibility(View.GONE);
        binding.viewPager.bringToFront();

        binding.shimmer.shimmer.startShimmer();
        binding.shimmerTabs.shimmerTabs.startShimmer();

        binding.viewPager.setOffscreenPageLimit(1);
        binding.viewPager.setAdapter(pager);
        binding.tabLayoutSubCat.setTabMode(TabLayout.MODE_FIXED);
        binding.tabLayoutSubCat.setSmoothScrollingEnabled(true);

        new TabLayoutMediator(binding.tabLayoutSubCat, binding.viewPager,
                (tab, position) -> tab.setText(pager.getTitle(position))
        ).attach();

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            private void updateChildHeight(View v) {

                ViewGroup.LayoutParams params = binding.viewPager.getLayoutParams();

                binding.viewPager.post(() -> {
                    int wMeasureSpec = View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY);
                    int hMeasureSpec = View.MeasureSpec.makeMeasureSpec(v.getHeight(), View.MeasureSpec.UNSPECIFIED);
                    v.measure(wMeasureSpec, hMeasureSpec);

                    params.height = v.getMeasuredHeight();

                    if (v.getMeasuredHeight() != binding.viewPager.getHeight()) {
                        binding.viewPager.setLayoutParams(params);
                    }
                });
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                View v = pager.getViewAtPosition(position);
                v.getViewTreeObserver().addOnGlobalLayoutListener(() -> updateChildHeight(v));

            }
        });

        getSubCategories();
    }


    public void getSubCategories() {

        binding.shimmer.shimmer.startShimmer();
        binding.shimmer.shimmer.setVisibility(View.VISIBLE);
        binding.tabLayoutSubCat.setVisibility(View.GONE);

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

                    if (pager != null) {
                        // pager.clear();
                        pager.addFragment(fragment, context.getResources().getString(R.string.sub_categories));
                    }
                }

                hideShimmer();
                binding.tabLayoutSubCat.setVisibility(View.VISIBLE);
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


        if (context == null || binding == null || pager == null)
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

        }

        pager.notifyDataSetChanged();

    }

    public void hideShimmer() {

        binding.shimmerHolder.animate().alpha(0.0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        binding.shimmerTabs.shimmerTabs.setVisibility(View.GONE);
                        binding.shimmer.shimmer.stopShimmer();
                        binding.shimmerTabs.shimmerTabs.stopShimmer();
                        binding.shimmer.shimmer.setVisibility(View.GONE);
                    }
                });


        isShimmerShowed = true;
    }
}