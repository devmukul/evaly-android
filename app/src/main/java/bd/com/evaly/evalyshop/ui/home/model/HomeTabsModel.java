package bd.com.evaly.evalyshop.ui.home.model;

import android.os.Bundle;
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
import bd.com.evaly.evalyshop.databinding.HomeModelTabsBinding;
import bd.com.evaly.evalyshop.ui.adapters.FragmentTabPagerAdapter;
import bd.com.evaly.evalyshop.ui.browseProduct.tabs.TabsViewModel;
import bd.com.evaly.evalyshop.ui.home.HomeTabsFragment;
import bd.com.evaly.evalyshop.util.Utils;

@EpoxyModelClass(layout = R.layout.home_model_tabs)
public abstract class HomeTabsModel extends EpoxyModelWithHolder<HomeTabsModel.HomeTabsHolder> {

    @EpoxyAttribute
    public Fragment fragmentInstance;

    @Override
    public void bind(@NonNull HomeTabsHolder holder) {
        super.bind(holder);
    }

    @Override
    public void unbind(@NonNull HomeTabsHolder holder) {
        super.unbind(holder);
        holder.itemView = null;
    }

    class HomeTabsHolder extends EpoxyHolder {

        View itemView;
        private FragmentTabPagerAdapter pager;



        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;

            HomeModelTabsBinding binding = HomeModelTabsBinding.bind(itemView);
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
            final float marginHeight = AppController.getmContext().getResources().getDimension(R.dimen.eight_dp);

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

                            if (position == 0)
                                params1.height = (int) (((row * boxHeight) + barHeight) - marginHeight);
                            else
                                params1.height = (int) ((row * (boxHeight + 1)) + barHeight);

                            binding.viewPager.post(() -> binding.viewPager.setLayoutParams(params1));

                        });
                    }
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