package bd.com.evaly.evalyshop.ui.browseProduct;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.tabs.TabLayout;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentProductBrowseBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.browseProduct.controller.ProductBrowseController;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.RecyclerSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProductBrowseFragment extends Fragment implements ProductBrowseController.ClickListener {

    private FragmentProductBrowseBinding binding;
    private BrowseProductViewModel viewModel;
    private ProductBrowseController controller;
    private RecyclerView.LayoutManager layoutManager;
    private PaginationScrollListener paginationScrollListener;
    private NavController navController;
    private RecyclerSpacingItemDecoration recyclerSpacingItemDecoration;
    private boolean isTabSwitched = false;
    private MainViewModel mainViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BrowseProductViewModel.class);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductBrowseBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        initHeader();
        setupTabs();
        clickListeners();
        setupRecycler();
        liveEvents();
    }

    private void initHeader() {
        new InitializeActionBar(binding.headerLogo, getActivity(), "browse", mainViewModel);
        binding.homeSearch.setOnClickListener(view1 -> startActivity(new Intent(getContext(), GlobalSearchActivity.class)));
    }

    private void setupRecycler() {
        if (controller == null)
            controller = new ProductBrowseController();
        controller.setFilterDuplicates(true);
        controller.setClickListener(this);

        paginationScrollListener = new PaginationScrollListener() {
            @Override
            public void loadMoreItem() {
                viewModel.loadFromApi();
                controller.setLoadingMore(true);
            }
        };

        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        recyclerSpacingItemDecoration = new RecyclerSpacingItemDecoration(2, spacing, true);
        binding.recyclerView.addItemDecoration(recyclerSpacingItemDecoration);

        setLayoutManager(viewModel.getSelectedType());
        binding.recyclerView.setAdapter(controller.getAdapter());
    }

    private void setLayoutManager(String type) {
        if (type == null)
            return;
        if (type.toLowerCase().equals("products")) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            paginationScrollListener.setStaggeredGridLayoutManager((StaggeredGridLayoutManager) layoutManager);
            binding.recyclerView.setLayoutManager(layoutManager);
            recyclerSpacingItemDecoration.setSpanCount(2);
        } else {
            layoutManager = new GridLayoutManager(getContext(), 3);
            paginationScrollListener.setGridLayoutManager((GridLayoutManager) layoutManager);
            binding.recyclerView.setLayoutManager(layoutManager);
            recyclerSpacingItemDecoration.setSpanCount(3);
        }
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
                controller.clearList();
                controller.requestModelBuild();

                String type = tab.getText().toString();
                isTabSwitched = true;
                viewModel.setSelectedType(type);
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

    private void clickListeners() {

    }

    private void liveEvents() {

        viewModel.liveList.observe(getViewLifecycleOwner(), baseModels -> {
            controller.setList(baseModels);
            controller.setLoadingMore(false);
            controller.requestModelBuild();
            if (isTabSwitched)
                setLayoutManager(viewModel.getSelectedType());
        });
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
        bundle.putString("slug", slug);

        if (type.equals("category"))
            navController.navigate(R.id.productBrowseFragment, bundle);
        else if (type.equals("shop")) {
            bundle.putString("shop_slug", slug);
            navController.navigate(R.id.shopFragment, bundle);
        } else if (type.equals("brand")) {
            bundle.putString("brand_slug", slug);
            navController.navigate(R.id.brandFragment, bundle);
        }
    }
}
