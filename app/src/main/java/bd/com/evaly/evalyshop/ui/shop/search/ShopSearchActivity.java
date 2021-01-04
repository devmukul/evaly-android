package bd.com.evaly.evalyshop.ui.shop.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentShopSearchBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.buynow.BuyNowFragment;
import bd.com.evaly.evalyshop.ui.shop.search.controller.ShopSearchController;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShopSearchActivity extends BaseActivity {

    private FragmentShopSearchBinding binding;
    private ShopSearchController controller;
    private List<ItemsItem> itemList;
    private int currentPage = 1;
    private int cashbackRate;
    private String campaignSlug = "", shopSlug = "sumash-tech", shopName;
    private boolean isLoading = false;
    private int totalCount = 0;
    private String query;
    private ShopSearchViewModel viewModel;
    private boolean firstLoad = true;
    private String brandSlug = null;

    public ShopSearchActivity() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent bundle = getIntent();
        shopSlug = bundle.getStringExtra("shop_slug");
        campaignSlug = bundle.getStringExtra("campaign_slug");
        brandSlug = bundle.getStringExtra("brand_slug");
        shopName = bundle.getStringExtra("shop_name");
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_shop_search);
        viewModel = new ViewModelProvider(this).get(ShopSearchViewModel.class);

        if (shopName != null && !shopName.equals(""))
            binding.search.setHint("Search in " + shopName + "...");
        else
            binding.search.setHint("Search in store...");

        binding.back.setOnClickListener(v -> finish());

        setupRecycler();
        searchActions();
        liveEvents();
    }

    private void liveEvents() {
        viewModel.getProductListLiveData().observe(this, shopItems -> {
            isLoading = false;
            binding.recyclerView.setVisibility(View.VISIBLE);
            List<ProductItem> tempList = new ArrayList<>();
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
                item.setInStock(shopItem.getInStock());
                tempList.add(item);
            }
            controller.setLoadingMore(false);
            controller.setList(tempList);
            controller.requestModelBuild();
        });

        viewModel.getBuyNowLiveData().observe(this, s -> {
            BuyNowFragment addPhotoBottomDialogFragment =
                    BuyNowFragment.newInstance(shopSlug, s);
            addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                    "BuyNow");
        });
    }

    private void searchActions() {
        binding.search.requestFocus();

        binding.search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (!binding.search.getText().toString().trim().equals("")) {
                    performSearch(binding.search.getText().toString().trim());
                    query = binding.search.getText().toString().trim();
                    binding.searchAction.setImageDrawable(getDrawable(R.drawable.ic_close_small));
                } else {
                    binding.searchAction.setImageDrawable(getDrawable(R.drawable.ic_search));
                    //  bindingo.noItem.setVisibility(View.VISIBLE);
                    itemList.clear();
                    controller.requestModelBuild();
//                    binding.searchTitle.setVisibility(View.GONE);
//                    binding.noText.setText("Search products here");
                }

            }
        });

        binding.searchAction.setOnClickListener(view -> {
            if (!binding.search.getText().toString().trim().equals("")) {
                binding.search.setText("");
                binding.searchAction.setImageDrawable(getDrawable(R.drawable.ic_search));
            }

        });
    }

    private void setupRecycler() {
        if (controller == null)
            controller = new ShopSearchController();
        controller.setFilterDuplicates(true);
        controller.setActivity(this);
        controller.setViewModel(viewModel);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(controller.getAdapter());
        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading)
                    viewModel.loadShopProducts();
            }
        });

        int spanCount = 2; // 3 columns
        int spacing = (int) Utils.convertDpToPixel(10, Objects.requireNonNull(this));
        boolean includeEdge = true;
        binding.recyclerView.addItemDecoration(new StaggeredSpacingItemDecoration(spanCount, spacing, includeEdge));
    }

    public String getQuery() {
        return query;
    }


    public void performSearch(String query) {
        controller.setSearch("Search result for \"" + query + "\"");
        viewModel.setSearch(query);
        isLoading = true;
        this.query = query;
        currentPage = 1;
        viewModel.loadShopProducts();
    }
}
