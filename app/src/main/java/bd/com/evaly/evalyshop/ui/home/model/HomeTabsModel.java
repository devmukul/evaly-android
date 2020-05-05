package bd.com.evaly.evalyshop.ui.home.model;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.express.ExpressServiceDao;
import bd.com.evaly.evalyshop.databinding.HomeModelTabsBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.rest.apiHelper.ExpressApiHelper;
import bd.com.evaly.evalyshop.ui.adapters.FragmentTabPagerAdapter;
import bd.com.evaly.evalyshop.ui.browseProduct.tabs.TabsViewModel;
import bd.com.evaly.evalyshop.ui.home.HomeTabsFragment;
import bd.com.evaly.evalyshop.ui.home.controller.ExpressController;
import bd.com.evaly.evalyshop.util.Utils;

@EpoxyModelClass(layout = R.layout.home_model_tabs)
public abstract class HomeTabsModel extends EpoxyModelWithHolder<HomeTabsModel.HomeTabsHolder> {

    @EpoxyAttribute
    public Fragment fragmentInstance;
    @EpoxyAttribute
    AppCompatActivity activity;

    private ExpressController expressController;

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
        private AppDatabase appDatabase;
        private ExpressServiceDao expressServiceDao;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;

            HomeModelTabsBinding binding = HomeModelTabsBinding.bind(itemView);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true);

            appDatabase = AppDatabase.getInstance(activity);
            expressServiceDao = appDatabase.expressServiceDao();

            expressController = new ExpressController();
            expressController.setFragment(fragmentInstance);
            expressController.setSpanCount(2);
            binding.expressServiceList.setAdapter(expressController.getAdapter());
            expressController.requestModelBuild();

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

            expressServiceDao.getAll().observe(fragmentInstance.getViewLifecycleOwner(), expressServiceModels -> {
                expressController.reAddData(expressServiceModels);
            });

            getExpressShops();
        }

        private void getExpressShops() {
            ExpressApiHelper.getServicesList(new ResponseListenerAuth<List<ExpressServiceModel>, String>() {
                @Override
                public void onDataFetched(List<ExpressServiceModel> response, int statusCode) {
                    Executors.newFixedThreadPool(4).execute(() -> {
                        expressServiceDao.insertList(response);
                        List<String> slugs = new ArrayList<>();
                        for (ExpressServiceModel item : response)
                            slugs.add(item.getSlug());

                        if (slugs.size() > 0)
                            expressServiceDao.deleteOld(slugs);
                    });
                }

                @Override
                public void onFailed(String errorBody, int errorCode) {

                }

                @Override
                public void onAuthError(boolean logout) {

                }
            });
        }
    }
}