package bd.com.evaly.evalyshop.ui.browseProduct.model;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonArray;

import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.BrowseProductModelTabsBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.adapters.FragmentTabPagerAdapter;
import bd.com.evaly.evalyshop.ui.browseProduct.tabs.SubTabsFragment;

@EpoxyModelClass(layout = R.layout.browse_product_model_tabs)
public abstract class BrowseProductTabsModel extends EpoxyModelWithHolder<BrowseProductTabsModel.HomeTabsHolder> {

    @EpoxyAttribute
    public Fragment fragmentInstance;
    @EpoxyAttribute
    public String category;

    @Override
    public void bind(@NonNull HomeTabsHolder holder) {
        super.bind(holder);
    }

    class HomeTabsHolder extends EpoxyHolder {

        View itemView;
        private FragmentTabPagerAdapter pager;
        private HashMap<Integer, ViewTreeObserver.OnGlobalLayoutListener> listenerList = new HashMap<>();
        private HashMap<Integer, View> viewList = new HashMap<>();
        private BrowseProductModelTabsBinding binding;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;

            binding = BrowseProductModelTabsBinding.bind(itemView);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true);

            pager = new FragmentTabPagerAdapter(fragmentInstance.getChildFragmentManager(), fragmentInstance.getLifecycle());

            fragmentInstance.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
                if (viewList.size() == 0)
                    return;

                for (int i = 0; i < listenerList.size(); i++) {
                    View view = viewList.get(i);
                    if (view != null)
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(listenerList.get(i));
                }

                viewList.clear();
                listenerList.clear();
            });

            binding.viewPager.setOffscreenPageLimit(1);
            binding.viewPager.setAdapter(pager);
            binding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
            binding.tabLayout.setSmoothScrollingEnabled(true);

            new TabLayoutMediator(binding.tabLayout, binding.viewPager,
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

                    if (v == null) {
                        new Handler().postDelayed(() -> {
                            try {
                                View vv = pager.getViewAtPosition(position);
                                if (vv != null)
                                    vv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            updateChildHeight(vv);
                                            viewList.put(position, vv);
                                            listenerList.put(position, this);
                                        }
                                    });
                            } catch (Exception ignored) {
                            }
                        }, 1000);
                    } else
                        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                updateChildHeight(v);
                                viewList.put(position, v);
                                listenerList.put(position, this);
                            }
                        });
                }
            });

            getSubCategories();
        }


        public void getSubCategories() {

            binding.shimmer.shimmer.startShimmer();
            binding.shimmer.shimmer.setVisibility(View.VISIBLE);

            ProductApiHelper.getSubCategories(category, new ResponseListenerAuth<JsonArray, String>() {

                @Override
                public void onDataFetched(JsonArray res, int statusCode) {

                    if (fragmentInstance.getContext() == null)
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
                            pager.addFragment(fragment, AppController.getmContext().getResources().getString(R.string.categories));
                        }
                    }

                    hideShimmer();
                    binding.tabLayout.setVisibility(View.VISIBLE);
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


            if (fragmentInstance.getContext() == null || binding == null || pager == null)
                return;
            {
                SubTabsFragment fragment = new SubTabsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                bundle.putString("slug", category);
                bundle.putString("category", category);
                bundle.putString("json", "");
                fragment.setArguments(bundle);
                pager.addFragment(fragment, AppController.getmContext().getResources().getString(R.string.brands));

            }

            {
                SubTabsFragment fragment = new SubTabsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("type", 3);
                bundle.putString("slug", category);
                bundle.putString("category", category);
                bundle.putString("json", "");
                fragment.setArguments(bundle);
                pager.addFragment(fragment, AppController.getmContext().getResources().getString(R.string.shops));

            }

            pager.notifyDataSetChanged();

        }

        public void hideShimmer() {


            binding.shimmerTabs.setVisibility(View.GONE);
            binding.shimmerHolder.animate().alpha(0.0f)
                    .setDuration(50)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            binding.shimmer.shimmer.stopShimmer();
                            binding.shimmerTabs.stopShimmer();
                            binding.shimmer.shimmer.setVisibility(View.GONE);
                        }
                    });
        }
    }
}