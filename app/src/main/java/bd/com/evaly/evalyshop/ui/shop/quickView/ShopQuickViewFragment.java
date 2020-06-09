package bd.com.evaly.evalyshop.ui.shop.quickView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentShopQuickCategoryBinding;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.ui.buynow.BuyNowFragment;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModelFactory;
import bd.com.evaly.evalyshop.ui.shop.quickView.controllers.ShopQuickViewCategoryController;
import bd.com.evaly.evalyshop.ui.shop.quickView.controllers.ShopQuickViewProductController;

public class ShopQuickViewFragment extends Fragment {

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int pastVisiblesItems2, visibleItemCount2, totalItemCount2;
    private FragmentShopQuickCategoryBinding binding;
    private ShopViewModelFactory factory;
    private ShopQuickViewModel viewModel;
    private String shopSlug = "chaldal", shopName = "Chaldal", campaignSlug = null, categorySlug = null;

    private ShopQuickViewCategoryController categoryController;
    private ShopQuickViewProductController productController;
    private boolean isLoading = false, isLoadingCategory = false;
    private int currentPage = 1, totalCount = 0, totalCountCategory = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentShopQuickCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            shopName = getArguments().getString("shop_name");
            campaignSlug = getArguments().getString("campaign_slug");
            shopSlug = getArguments().getString("shop_slug");
        }

        binding.toolbar.setTitle(shopName);
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });
        inflateSearchMenu();

        factory = new ShopViewModelFactory(categorySlug, campaignSlug, shopSlug);
        viewModel = new ViewModelProvider(this, factory).get(ShopQuickViewModel.class);

        categoryController = new ShopQuickViewCategoryController();
        categoryController.setActivity((AppCompatActivity) getActivity());
        categoryController.setFragment(this);
        categoryController.setViewModel(viewModel);

        productController = new ShopQuickViewProductController();
        productController.setActivity((AppCompatActivity) getActivity());
        productController.setFragment(this);
        productController.setShopSlug(shopSlug);
        productController.setViewModel(viewModel);
        productController.setLoadingMore(true);

        binding.rvCategory.setAdapter(categoryController.getAdapter());
        binding.rvProducts.setAdapter(productController.getAdapter());

        viewModelLiveDataObservers();

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.rvProducts.setLayoutManager(layoutManager);
        binding.rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    int[] firstVisibleItems = null;
                    firstVisibleItems = layoutManager.findFirstVisibleItemPositions(null);
                    if (firstVisibleItems != null && firstVisibleItems.length > 0)
                        pastVisiblesItems = firstVisibleItems[0];

                    if (!isLoading && totalItemCount < totalCount)
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            productController.showEmptyPage(false, false);
                            productController.setLoadingMore(true);
                            viewModel.loadShopProducts();
                            isLoading = true;
                        }
                }
            }
        });

        LinearLayoutManager layoutManagerCategory = new LinearLayoutManager(getContext());
        binding.rvCategory.setLayoutManager(layoutManagerCategory);

        binding.rvCategory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount2 = layoutManagerCategory.getChildCount();
                    totalItemCount2 = layoutManagerCategory.getItemCount();
                    pastVisiblesItems2 = layoutManagerCategory.findFirstVisibleItemPosition();
                    if (!isLoadingCategory && totalItemCount2 < totalCountCategory)
                        if ((visibleItemCount2 + pastVisiblesItems2) >= totalItemCount2) {
                            categoryController.setLoadingMore(true, true);
                            viewModel.loadShopCategories();
                            isLoadingCategory = true;
                        }
                }
            }
        });

    }

    private void inflateSearchMenu() {
        binding.toolbar.inflateMenu(R.menu.search_btn);
        MenuItem item = binding.toolbar.getMenu().findItem(R.id.action_search);

        binding.toolbar.setOnMenuItemClickListener(item1 -> {
            switch (item1.getItemId()) {
                case R.id.action_search:
                    Bundle bundle = new Bundle();
                    bundle.putString("shop_slug", shopSlug);
                    bundle.putString("shop_name", shopName);
                    bundle.putString("campaign_slug", campaignSlug);
                    NavHostFragment.findNavController(ShopQuickViewFragment.this).navigate(R.id.shopSearchActivity, bundle);
            }
            return true;
        });

    }

    private void viewModelLiveDataObservers() {

        viewModel.getBuyNowLiveData().observe(getViewLifecycleOwner(), s -> {
            if (getActivity() != null) {
                BuyNowFragment addPhotoBottomDialogFragment =
                        BuyNowFragment.newInstance(shopSlug, s);
                addPhotoBottomDialogFragment.show(getActivity().getSupportFragmentManager(),
                        "BuyNow");
            }
        });

        viewModel.getShopCategoryListLiveData().observe(getViewLifecycleOwner(), tabsItems -> {
            if (viewModel.getCategoryCount() != null)
                totalCountCategory = viewModel.getCategoryCount();
            isLoadingCategory = false;
            categoryController.setLoadingMore(false, true);
            categoryController.addCategoryData(tabsItems, true);
        });

        viewModel.getSelectedCategoryLiveData().observe(getViewLifecycleOwner(), tabsItem -> {

            binding.selectedBrd.setVisibility(View.GONE);
            if (viewModel.getCategoryCount() != null)
                totalCountCategory = viewModel.getCategoryCount();
            isLoadingCategory = false;
            categorySlug = tabsItem.getSlug();
            viewModel.setCategorySlug(categorySlug);
            viewModel.setCurrentPage(1);
            productController.setCategoryTitle(tabsItem.getTitle());

            viewModel.clearProductList();
            currentPage = 1;
            productController.clear();
            productController.setLoadingMore(true);
            viewModel.loadShopProducts();
        });

        binding.allCategories.setOnClickListener(view -> {
            ApiClient.getUnsafeOkHttpClient().dispatcher().cancelAll();
            productController.clear();
            viewModel.setCategorySlug(null);
            viewModel.setCurrentPage(1);
            productController.setCategoryTitle(null);
            currentPage = 1;
            productController.setLoadingMore(true);
            viewModel.clearProductList();
            viewModel.loadShopProducts();
            binding.selectedBrd.setVisibility(View.VISIBLE);
            categoryController.deselectCategory();
        });

        viewModel.getShopDetailsLiveData().observe(getViewLifecycleOwner(), shopDetailsModel -> {
            if (shopDetailsModel.getData().getMeta() != null)
                productController.setCashbackRate(shopDetailsModel.getData().getMeta().get("cashback_rate").getAsInt());
            totalCount = shopDetailsModel.getCount();
            isLoading = false;
        });

        viewModel.getProductListLiveData().observe(getViewLifecycleOwner(), shopItems -> {
            binding.rvProducts.setVisibility(View.VISIBLE);
            productController.setLoadingMore(false);
            List<ProductItem> tempList = new ArrayList<>();
            long timeInMill = Calendar.getInstance().getTimeInMillis();
            for (int i = 0; i < shopItems.size(); i++) {
                if (i == 0)
                    currentPage++;

                ItemsItem shopItem = shopItems.get(i);
                ProductItem item = new ProductItem();
                item.setImageUrls(shopItem.getItemImages());
                item.setSlug(shopItem.getShopItemSlug());
                item.setName(shopItem.getItemName());
                item.setMaxPrice(String.valueOf(shopItem.getItemPrice()));
                item.setMinPrice(String.valueOf(shopItem.getItemPrice()));
                item.setMinDiscountedPrice(String.valueOf(shopItem.getDiscountedPrice()));
                item.setUniqueId(item.getSlug());

                tempList.add(item);
            }
            productController.addData(tempList);
        });

    }
}
