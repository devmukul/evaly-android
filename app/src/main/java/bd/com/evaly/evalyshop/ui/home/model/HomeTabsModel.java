package bd.com.evaly.evalyshop.ui.home.model;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.HomeModelTabsBinding;
import bd.com.evaly.evalyshop.ui.adapters.FragmentTabPagerAdapter;
import bd.com.evaly.evalyshop.ui.browseProduct.tabs.TabsViewModel;
import bd.com.evaly.evalyshop.ui.home.HomeTabsFragment;
import bd.com.evaly.evalyshop.util.Utils;

@EpoxyModelClass(layout = R.layout.home_model_tabs)
public abstract class HomeTabsModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    public Fragment fragmentInstance;

    private FragmentTabPagerAdapter pager;

    @Override
    public boolean shouldSaveViewState() {
        return true;
    }

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        HomeModelTabsBinding binding = (HomeModelTabsBinding) holder.getDataBinding();
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);

        pager = new FragmentTabPagerAdapter(fragmentInstance.getChildFragmentManager(), fragmentInstance.getLifecycle());

        binding.viewPager.setOffscreenPageLimit(3);
        binding.viewPager.setAdapter(pager);
        binding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        binding.tabLayout.setSmoothScrollingEnabled(true);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(pager.getTitle(position))
        ).attach();

        final float boxHeight = AppController.getmContext().getResources().getDimension(R.dimen.tab_height);
        final float barHeight = Utils.convertDpToPixel(65, AppController.getmContext());

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                TabsViewModel viewModel = pager.getViewModel(position);

                if (viewModel != null) {
                    if (viewModel.getItemCount().hasActiveObservers())
                        viewModel.getItemCount().removeObservers(fragmentInstance.getViewLifecycleOwner());

                    viewModel.getItemCount().observe(fragmentInstance.getViewLifecycleOwner(), integer -> {

                        ViewGroup.LayoutParams params1 = binding.viewPager.getLayoutParams();
                        int row = (int) (Math.ceil(integer == 0 ? 1 : integer / 3.0));
                        params1.height = (int) ((row * (boxHeight + 1)) + barHeight);

                        binding.viewPager.post(() -> binding.viewPager.setLayoutParams(params1));

                    });
                }
            }
        });

        if (pager != null) {
            HomeTabsFragment categoryFragment = HomeTabsFragment.getInstance(1, "root", "root");
            HomeTabsFragment brandFragment = HomeTabsFragment.getInstance(2, "root", "root");
            HomeTabsFragment shopFragment = HomeTabsFragment.getInstance(3, "root", "root");

            pager.addFragment(categoryFragment, AppController.getmContext().getResources().getString(R.string.categories));
            pager.addFragment(brandFragment, AppController.getmContext().getResources().getString(R.string.brands));
            pager.addFragment(shopFragment, AppController.getmContext().getResources().getString(R.string.shops));
            pager.notifyDataSetChanged();
        }
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }


}