package bd.com.evaly.evalyshop.ui.browseProduct;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentProductBrowseBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.browseProduct.controller.BrowseProductController;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.RecyclerSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BrowseProductFragment extends BaseFragment<FragmentProductBrowseBinding, BrowseProductViewModel> implements BrowseProductController.ClickListener, SwipeRefreshLayout.OnRefreshListener {

    private BrowseProductController controller;
    private RecyclerView.LayoutManager layoutManager;
    private PaginationScrollListener paginationScrollListener;
    private RecyclerSpacingItemDecoration recyclerSpacingItemDecoration;
    private boolean isTabSwitched = false;
    private MainViewModel mainViewModel;
    private boolean isLoading = true;

    public BrowseProductFragment() {
        super(BrowseProductViewModel.class, R.layout.fragment_product_browse);
    }

    @Override
    protected void initViews() {
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        binding.swipeRefresh.setOnRefreshListener(this);
        initHeader();
        setupTabs();
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.liveList.observe(getViewLifecycleOwner(), baseModels -> {
            if (isTabSwitched)
                setLayoutManager(viewModel.getSelectedType());
            controller.setList(baseModels);
            controller.setLoadingMore(false);
            controller.requestModelBuild();
            isLoading = false;
        });
    }

    private void initHeader() {
        new InitializeActionBar(binding.headerLogo, getActivity(), "browse", mainViewModel);
        binding.homeSearch.setOnClickListener(view1 -> startActivity(new Intent(getContext(), GlobalSearchActivity.class)));
    }

    @Override
    protected void setupRecycler() {
        if (controller == null)
            controller = new BrowseProductController();
        controller.setFilterDuplicates(true);
        controller.setClickListener(this);
        controller.setViewModel(viewModel);

        paginationScrollListener = new PaginationScrollListener() {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    isLoading = true;
                    isTabSwitched = false;
                    viewModel.loadFromApi();
                    controller.setLoadingMore(true);
                    controller.requestModelBuild();
                }
            }
        };

        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        recyclerSpacingItemDecoration = new RecyclerSpacingItemDecoration(2, spacing, true);
        binding.recyclerView.addItemDecoration(recyclerSpacingItemDecoration);
        binding.recyclerView.addOnScrollListener(paginationScrollListener);

        setLayoutManager(viewModel.getSelectedType());
        binding.recyclerView.setAdapter(controller.getAdapter());
        controller.requestModelBuild();
    }

    private void setLayoutManager(String type) {
        if (type == null)
            return;
        if (type.toLowerCase().equals("products")) {
            recyclerSpacingItemDecoration.setSpanCount(2);
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            paginationScrollListener.setStaggeredGridLayoutManager((StaggeredGridLayoutManager) layoutManager);
        } else {
            recyclerSpacingItemDecoration.setSpanCount(3);
            layoutManager = new GridLayoutManager(getContext(), 3);
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(controller.getSpanSizeLookup());
            paginationScrollListener.setGridLayoutManager((GridLayoutManager) layoutManager);
        }
        binding.recyclerView.setLayoutManager(layoutManager);
    }

    private void setupTabs() {

        String type = viewModel.getSelectedType();
        if (type != null) {
            type = type.toLowerCase();
            if (type.contains("products"))
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0));
            else if (type.contains("categories"))
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1));
            else if (type.contains("shops"))
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(2));
            else if (type.contains("brands"))
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(3));
        }

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setLayoutManager(viewModel.getSelectedType());
                String type = tab.getText().toString();
                isTabSwitched = true;
                viewModel.setSelectedType(type);
                controller.clearList();
                controller.setLoadingMore(true);
                controller.requestModelBuild();
                viewModel.clear();
                viewModel.loadFromApi();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void clickListeners() {

    }

    @Override
    public void onProductClick(ProductItem model) {
        Intent intent = new Intent(getContext(), ViewProductActivity.class);
        intent.putExtra("product_slug", model.getSlug());
        intent.putExtra("product_name", model.getName());
        intent.putExtra("product_price", model.getMaxPrice());
        if (model.getImageUrls().size() > 0)
            intent.putExtra("product_image", model.getImageUrls().get(0));
        getActivity().startActivity(intent);
    }

    @Override
    public void onGridItemClick(String type, String title, String image, String slug) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);

        if (type.equals("category")) {
            bundle.putString("category_slug", slug);
            navController.navigate(R.id.browseProductFragment, bundle);
        } else if (type.equals("shop")) {
            bundle.putString("shop_slug", slug);
            navController.navigate(R.id.shopFragment, bundle);
        } else if (type.equals("brand")) {
            bundle.putString("brand_slug", slug);
            bundle.putString("category_slug", viewModel.getCategorySlug());
            navController.navigate(R.id.brandFragment, bundle);
        }
    }

    @Override
    public void onRefresh() {
        binding.swipeRefresh.setRefreshing(false);
        viewModel.reload();
    }
}
