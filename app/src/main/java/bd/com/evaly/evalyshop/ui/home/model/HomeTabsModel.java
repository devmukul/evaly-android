package bd.com.evaly.evalyshop.ui.home.model;

import android.os.Bundle;
import android.os.Handler;
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

import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.HomeModelTabsBinding;
import bd.com.evaly.evalyshop.ui.adapters.FragmentTabPagerAdapter;
import bd.com.evaly.evalyshop.ui.home.HomeTabsFragment;

@EpoxyModelClass(layout = R.layout.home_model_tabs)
public abstract class HomeTabsModel extends EpoxyModelWithHolder<HomeTabsModel.HomeTabsHolder> {

    @EpoxyAttribute
    public Fragment fragmentInstance;

    @Override
    public void bind(@NonNull HomeTabsHolder holder) {
        super.bind(holder);
    }

    class HomeTabsHolder extends EpoxyHolder {

        View itemView;
        private FragmentTabPagerAdapter pager;
        private HashMap<Integer, ViewTreeObserver.OnGlobalLayoutListener> listenerList = new HashMap<>();
        private HashMap<Integer, View> viewList = new HashMap<>();

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;

            HomeModelTabsBinding binding = HomeModelTabsBinding.bind(itemView);
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

            HomeTabsFragment categoryFragment = new HomeTabsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", 1);
            bundle.putString("slug", "root");
            bundle.putString("category", "root");
            categoryFragment.setArguments(bundle);

            HomeTabsFragment brandFragment = new HomeTabsFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putInt("type", 2);
            bundle2.putString("slug", "root");
            bundle2.putString("category", "root");
            brandFragment.setArguments(bundle2);

            // OnDoneListener onDoneListener = () -> binding.shimmer.shimmer.setVisibility(View.GONE);

            //  brandFragment.setOnDoneListener(onDoneListener);
            // categoryFragment.setOnDoneListener(onDoneListener);

            HomeTabsFragment shopFragment = new HomeTabsFragment();
            Bundle bundle3 = new Bundle();
            bundle3.putInt("type", 3);
            bundle3.putString("slug", "root");
            bundle3.putString("category", "root");
            shopFragment.setArguments(bundle3);

            if (pager != null) {
                pager.addFragment(categoryFragment, AppController.getmContext().getResources().getString(R.string.categories));
                pager.addFragment(brandFragment, AppController.getmContext().getResources().getString(R.string.brands));
                pager.addFragment(shopFragment, AppController.getmContext().getResources().getString(R.string.shops));
                pager.notifyDataSetChanged();
            }
        }



    }
}