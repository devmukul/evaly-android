package bd.com.evaly.evalyshop.ui.browseProduct.model;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.BrowseProductModelTabsBinding;
import bd.com.evaly.evalyshop.ui.adapters.FragmentTabPagerAdapter;
import bd.com.evaly.evalyshop.ui.browseProduct.tabs.SubTabsFragment;
import bd.com.evaly.evalyshop.ui.browseProduct.tabs.TabsViewModel;
import bd.com.evaly.evalyshop.util.Utils;

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
        private BrowseProductModelTabsBinding binding;
        private boolean firstTime;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;

            binding = BrowseProductModelTabsBinding.bind(itemView);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true);

            pager = new FragmentTabPagerAdapter(fragmentInstance.getChildFragmentManager(), fragmentInstance.getLifecycle());

            binding.viewPager.setOffscreenPageLimit(2);
            binding.viewPager.setAdapter(pager);
            binding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
            binding.tabLayout.setSmoothScrollingEnabled(true);

            new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                    (tab, position) -> tab.setText(pager.getTitle(position))
            ).attach();

            final float boxHeight = AppController.getmContext().getResources().getDimension(R.dimen.tab_height) + 2;
            final float barHeight = Utils.convertDpToPixel(60, AppController.getmContext());
            final float marginHeight = AppController.getmContext().getResources().getDimension(R.dimen.eight_dp);

            firstTime = true;

            binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);

                    TabsViewModel viewModel = pager.getViewModel(position);

                    if (viewModel != null) {
                        if (viewModel.getItemCount().hasActiveObservers()) {
                            firstTime = false;
                            viewModel.getItemCount().removeObservers(fragmentInstance.getViewLifecycleOwner());
                        }
                        viewModel.getItemCount().observe(fragmentInstance.getViewLifecycleOwner(), integer -> {
                            ViewGroup.LayoutParams params1 = binding.viewPager.getLayoutParams();

                            int row = (int) (Math.ceil(integer == 0 ? 1 : integer / 3.0));

                            if (position == 0)
                                params1.height = (int) ((row * boxHeight) + marginHeight);
                            else
                                params1.height = (int) ((row * boxHeight) + barHeight);

                            binding.viewPager.post(() -> {
                                binding.viewPager.setLayoutParams(params1);
                                if (integer == 0 && firstTime) {
                                    if (binding != null && binding.tabLayout.getTabCount() > 0)
                                        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1));
                                }
                            });
                        });
                    }
                }
            });


            SubTabsFragment categoryFragment = SubTabsFragment.getInstance(1, category, category);
            SubTabsFragment brandFragment = SubTabsFragment.getInstance(2, category, category);
            SubTabsFragment shopFragment = SubTabsFragment.getInstance(3, category, category);

            pager.addFragment(categoryFragment, AppController.getmContext().getResources().getString(R.string.categories));
            pager.addFragment(brandFragment, AppController.getmContext().getResources().getString(R.string.brands));
            pager.addFragment(shopFragment, AppController.getmContext().getResources().getString(R.string.shops));
            
            pager.notifyDataSetChanged();
        }

    }
}