package bd.com.evaly.evalyshop.ui.browseProduct.model;

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
import com.google.gson.JsonArray;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.BrowseProductModelTabsBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
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

                            int row = (int) (Math.ceil(viewModel.getIntCount() / 3.0));

                            if (position == 0 && pager.getItemCount() == 3)
                                params1.height = (int) ((row * boxHeight) + marginHeight);
                            else
                                params1.height = (int) ((row * boxHeight) + barHeight);

                            binding.viewPager.post(() -> binding.viewPager.setLayoutParams(params1));

                        });
                    }
                }
            });

            SubTabsFragment fragment = new SubTabsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", 1);
            bundle.putString("slug", category);
            bundle.putString("category", category);
            fragment.setArguments(bundle);

            pager.addFragment(fragment, AppController.getmContext().getResources().getString(R.string.categories));

            loadOtherTabs();

            checkSubCategories();
        }


        public void checkSubCategories() {

            ProductApiHelper.getSubCategories(category, new ResponseListenerAuth<JsonArray, String>() {

                @Override
                public void onDataFetched(JsonArray res, int statusCode) {

                    if (fragmentInstance.getContext() == null)
                        return;

                    int length = res.size();

                    if (binding != null && length == 0 && binding.tabLayout.getTabCount() > 0)
                        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1));

                }

                @Override
                public void onFailed(String body, int errorCode) {

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
    }
}