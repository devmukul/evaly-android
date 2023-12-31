package bd.com.evaly.evalyshop.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentGlobalSearchBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.search.product.response.ProductsItem;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.search.controller.FilterSubController;
import bd.com.evaly.evalyshop.ui.search.controller.GlobalSearchController;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import bd.com.evaly.evalyshop.views.behaviors.TopSheetBehavior;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GlobalSearchFragment extends BaseFragment<FragmentGlobalSearchBinding, GlobalSearchViewModel> implements GlobalSearchController.ClickListener {

    private GlobalSearchController controller;
    private boolean isLoading = false;
    private TopSheetBehavior filterTopSheetBehavior;
    private FilterSubController filterController;
    private StaggeredGridLayoutManager layoutManager;
    private StaggeredSpacingItemDecoration recyclerDecoration;
    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() != 0) {
                binding.searchClear.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_smallest));
                binding.searchClear.setTag("clear");
            } else {
                binding.searchClear.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
                binding.searchClear.setTag("search");
                viewModel.setSearchQuery(null);
                viewModel.clearFilters();
                viewModel.updateButtonHighlights.call();
                viewModel.performSearch();
            }
        }
    };

    public GlobalSearchFragment() {
        super(GlobalSearchViewModel.class, R.layout.fragment_global_search);
    }

    @Override
    protected void initViews() {
        initSearchViews();
        handleArguments();
        initFilterTopSheet();
    }

    private void handleArguments() {
        if (getArguments() != null && getArguments().containsKey("type")) {
            String type = getArguments().getString("type");
            if (type != null) {
                if (type.contains("brand")) {
                    binding.filterTypeRadioGroup.check(R.id.filterTypeBrands);
                } else if (type.contains("shop")) {
                    binding.filterTypeRadioGroup.check(R.id.filterTypeShops);
                } else
                    binding.filterSearchType.setText(R.string.products);
            }
        }
    }

    private void resetListController() {
        viewModel.setPage(1);
        controller.setList(new ArrayList<>());
        controller.setLoadingMore(true);
        controller.requestModelBuild();
    }

    private void initFilterTopSheet() {

        if (viewModel.getType().contains("product"))
            binding.filterTypeProducts.setChecked(true);
        else if (viewModel.getType().contains("brand"))
            binding.filterTypeBrands.setChecked(true);
        else if (viewModel.getType().contains("shop"))
            binding.filterTypeShops.setChecked(true);

        binding.filterTypeRadioGroup.setOnCheckedChangeListener((radioGroup, id) -> {
            if (id == R.id.filterTypeProducts) {
                viewModel.setType("product");
                binding.filterSearchType.setText(R.string.products);
            } else if (id == R.id.filterTypeShops) {
                viewModel.setType("shop");
                binding.filterSearchType.setText(R.string.shops);
            } else if (id == R.id.filterTypeBrands) {
                viewModel.setType("brand");
                binding.filterSearchType.setText(R.string.brands);
            }
            updateFilterButtonsVisibility();
            resetListController();
            viewModel.performSearch();
            updateRecyclerViewSpan();
            hideFilterSheet();
        });

        if (viewModel.getSortBy() == null)
            binding.filterSortRelevance.setChecked(true);
        else if (viewModel.getSortBy().equals("asc"))
            binding.filterSortPriceLowToHigh.setChecked(true);
        else if (viewModel.getSortBy().equals("desc"))
            binding.filterSortPriceHighToLow.setChecked(true);

        binding.filterSortRadioGroup.setOnCheckedChangeListener((radioGroup, id) -> {
            if (id == R.id.filterSortRelevance) {
                viewModel.setSortBy(null);
            } else if (id == R.id.filterSortPriceHighToLow) {
                viewModel.setSortBy("desc");
            } else if (id == R.id.filterSortPriceLowToHigh) {
                viewModel.setSortBy("asc");
            }
            resetListController();
            viewModel.performSearch();
            hideFilterSheet();
        });

        filterTopSheetBehavior = TopSheetBehavior.from(binding.filterSheet);
        filterTopSheetBehavior.setTopSheetCallback(new TopSheetBehavior.TopSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View topSheet, int newState) {
                if (newState == TopSheetBehavior.STATE_HIDDEN ||
                        newState == TopSheetBehavior.STATE_COLLAPSED) {
                    hideFilterSheetBgOverlay(true);
                    hideAllFilterHolders();
                    viewModel.updateButtonHighlights.call();
                    if (filterController != null) {
                        filterController.setList(new ArrayList<>());
                        filterController.requestModelBuild();
                    }
                } else if (newState == TopSheetBehavior.STATE_EXPANDED) {
                    showFilterSheetBgOverlay();
                }
            }

            @Override
            public void onSlide(@NonNull View topSheet, float slideOffset, @Nullable Boolean isOpening) {
                binding.bgOverlay.setAlpha(slideOffset);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showFilerByPrice() {
        toggleFilterHolderVisibility(binding.holderFilterPrice, "");
        binding.filterPriceRangeSlider.setLabelFormatter(value -> (int) value + "");
        binding.filterPriceRangeSlider.setLabelFormatter(value -> Utils.formatPriceSymbol((int) value));
        binding.filterPriceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            binding.filterPriceMax.setText("Maximum: " + Utils.formatPriceSymbol(binding.filterPriceRangeSlider.getValues().get(1).intValue()));
            binding.filterPriceMin.setText("Minimum: " + Utils.formatPriceSymbol(binding.filterPriceRangeSlider.getValues().get(0).intValue()));
        });
    }

    private void showFilerSearchType() {
        toggleFilterHolderVisibility(binding.holderFilterType, "");
        binding.filterPriceRangeSlider.setLabelFormatter(value -> (int) value + "");
    }

    private void showFilerSortBy() {
        toggleFilterHolderVisibility(binding.holderFilterSort, "");
    }

    private void toggleFilterHolderVisibility(LinearLayout selected, String type) {
        if (binding.filterDynamicTitle.getText().toString().length() > 0 && type.length() > 0) {
            if (binding.filterDynamicTitle.getText().toString().toLowerCase().contains(type.toLowerCase()) && getCurrentVisibleFilterHolderId() == selected) {
                hideFilterSheet();
                return;
            } else
                showFilterSheet();
        } else if (getCurrentVisibleFilterHolderId() == selected) {
            hideFilterSheet();
            return;
        } else
            showFilterSheet();

        binding.holderFilterType.setVisibility(selected == binding.holderFilterType ? View.VISIBLE : View.GONE);
        binding.holderFilterPrice.setVisibility(selected == binding.holderFilterPrice ? View.VISIBLE : View.GONE);
        binding.holderFilterSort.setVisibility(selected == binding.holderFilterSort ? View.VISIBLE : View.GONE);
        binding.holderFilterDynamicList.setVisibility(selected == binding.holderFilterDynamicList ? View.VISIBLE : View.GONE);

        binding.filterActionButtonHolder.setVisibility(selected == binding.holderFilterType || selected == binding.holderFilterSort ? View.GONE : View.VISIBLE);
    }

    private void hideAllFilterHolders() {
        binding.holderFilterType.setVisibility(View.GONE);
        binding.holderFilterPrice.setVisibility(View.GONE);
        binding.holderFilterSort.setVisibility(View.GONE);
        binding.holderFilterDynamicList.setVisibility(View.GONE);
        binding.filterActionButtonHolder.setVisibility(View.GONE);
        binding.filterDynamicTitle.setText("");
    }

    private LinearLayout getCurrentVisibleFilterHolderId() {
        if (binding.holderFilterType.getVisibility() == View.VISIBLE)
            return binding.holderFilterType;
        else if (binding.holderFilterPrice.getVisibility() == View.VISIBLE)
            return binding.holderFilterPrice;
        else if (binding.holderFilterSort.getVisibility() == View.VISIBLE)
            return binding.holderFilterSort;
        else if (binding.holderFilterDynamicList.getVisibility() == View.VISIBLE)
            return binding.holderFilterDynamicList;
        else
            return new LinearLayout(getContext());
    }

    @Override
    protected void clickListeners() {

        binding.priceFilterUnder1k.setOnClickListener(view -> {
            binding.filterPriceRangeSlider.setValues(Math.min(binding.filterPriceRangeSlider.getValueTo(), 1000f),
                    Math.max(binding.filterPriceRangeSlider.getValueFrom(), 0));
            viewModel.setMaxPrice(1000);
            viewModel.setMinPrice(0);
            viewModel.setPriceRangeSelected(true);
            viewModel.performSearch();
            hideFilterSheet();
        });

        binding.priceFilter1kTo5k.setOnClickListener(view -> {
            binding.filterPriceRangeSlider.setValues(Math.min(binding.filterPriceRangeSlider.getValueTo(), 5000f),
                    Math.max(binding.filterPriceRangeSlider.getValueFrom(), 1000f));
            viewModel.setMinPrice(1000);
            viewModel.setMaxPrice(5000);
            viewModel.setPriceRangeSelected(true);
            viewModel.performSearch();
            hideFilterSheet();
        });

        binding.priceFilterAbove10k.setOnClickListener(view -> {
            binding.filterPriceRangeSlider.setValues(binding.filterPriceRangeSlider.getValueTo(),
                    Math.max(binding.filterPriceRangeSlider.getValueFrom(), 10000f));
            viewModel.setMinPrice(10000);
            viewModel.setMaxPrice(null);
            viewModel.setPriceRangeSelected(true);
            viewModel.performSearch();
            hideFilterSheet();
        });

        binding.filterApply.setOnClickListener(view -> {

            if (binding.holderFilterDynamicList.getVisibility() == View.VISIBLE) {
                if (filterController.getType().contains("categor"))
                    viewModel.selectedFilterCategoriesList = filterController.getSelectedValues();
                else if (filterController.getType().contains("brand"))
                    viewModel.selectedFilterBrandsList = filterController.getSelectedValues();
                else if (filterController.getType().contains("shop"))
                    viewModel.selectedFilterShopsList = filterController.getSelectedValues();
            } else if (binding.holderFilterPrice.getVisibility() == View.VISIBLE) {
                int val0 = binding.filterPriceRangeSlider.getValues().get(0).intValue();
                int val1 = binding.filterPriceRangeSlider.getValues().get(1).intValue();

                viewModel.setMinPrice(Math.min(val0, val1));
                viewModel.setMaxPrice(Math.max(val0, val1));
                viewModel.setPriceRangeSelected(true);
            }

            viewModel.performSearch();
            hideFilterSheet();
        });

        binding.filterReset.setOnClickListener(view -> {
            if (binding.holderFilterDynamicList.getVisibility() == View.VISIBLE) {
                if (filterController != null) {
                    if (filterController.getType().contains("categor"))
                        viewModel.selectedFilterCategoriesList = new ArrayList<>();
                    else if (filterController.getType().contains("brand"))
                        viewModel.selectedFilterBrandsList = new ArrayList<>();
                    else if (filterController.getType().contains("shop"))
                        viewModel.selectedFilterShopsList = new ArrayList<>();
                }
            } else if (binding.holderFilterPrice.getVisibility() == View.VISIBLE) {
                viewModel.setMinPrice(null);
                viewModel.setMaxPrice(null);
                viewModel.setPriceRangeSelected(false);
            }

            viewModel.performSearch();
            hideFilterSheet();
        });

        binding.back.setOnClickListener(backPressClickListener);

        binding.bgOverlay.setOnClickListener(view -> {
            hideFilterSheetBgOverlay(false);
            hideFilterSheet();
        });

        binding.filterSearchType.setOnClickListener(view -> {
            showFilerSearchType();
        });

        binding.filterSort.setOnClickListener(view -> {
            showFilerSortBy();
        });

        binding.filterPrice.setOnClickListener(view -> {
            if (viewModel.facetsMutableLiveData.getValue() == null) {
                ToastUtils.show("Try after page has loaded");
                return;
            }
            showFilerByPrice();
        });

        binding.filterCategory.setOnClickListener(view -> {
            if (viewModel.facetsMutableLiveData.getValue() == null) {
                ToastUtils.show("Try after page has loaded");
                return;
            }
            showFilerDynamic("categories");
        });

        binding.filterShop.setOnClickListener(view -> {
            if (viewModel.facetsMutableLiveData.getValue() == null) {
                ToastUtils.show("Try after page has loaded");
                return;
            }
            showFilerDynamic("shops");
        });

        binding.filterBrand.setOnClickListener(view -> {
            if (viewModel.facetsMutableLiveData.getValue() == null) {
                ToastUtils.show("Try after page has loaded");
                return;
            }
            showFilerDynamic("brands");
        });
    }

    private void showFilerDynamic(String type) {
        toggleFilterHolderVisibility(binding.holderFilterDynamicList, type);
        binding.filterDynamicTitle.setText("Filter by " + Utils.toFirstCharUpperAll(type));
        if (filterController == null)
            filterController = new FilterSubController();
        filterController.setViewModel(viewModel);
        filterController.setFilterDuplicates(true);
        filterController.setType(type);
        binding.filterDynamicRecyclerView.setAdapter(filterController.getAdapter());
        if (type.equals("brands"))
            filterController.setList(viewModel.filterBrandsList);
        else if (type.equals("shops"))
            filterController.setList(viewModel.filterShopsList);
        else if (type.equals("categories"))
            filterController.setList(viewModel.filterCategoriesList);
        filterController.requestModelBuild();
    }

    private void showFilterSheet() {
        filterTopSheetBehavior.setState(TopSheetBehavior.STATE_EXPANDED);
        showFilterSheetBgOverlay();
    }

    private void hideFilterSheet() {
        filterTopSheetBehavior.setState(TopSheetBehavior.STATE_COLLAPSED);
        hideFilterSheetBgOverlay(false);
    }

    private void showFilterSheetBgOverlay() {
        binding.bgOverlay.setVisibility(View.VISIBLE);
        binding.bgOverlay.animate().alpha(1f);
    }

    private void hideFilterSheetBgOverlay(boolean isFast) {
        binding.bgOverlay
                .animate()
                .alpha(0.0f)
                .setDuration(isFast ? 150 : 400)
                .withEndAction(() -> binding.bgOverlay.setVisibility(View.GONE));
    }

    private void initSearchViews() {

        binding.searchClear.setOnClickListener(v -> {
            if (binding.searchClear.getTag().equals("clear"))
                binding.searchText.setText("");
        });

        binding.searchText.setOnFocusChangeListener((view, b) -> {
            if (b)
                hideFilterSheet();
        });

        binding.searchText.setOnEditorActionListener((v, actionId, event) -> {
            if ((actionId == EditorInfo.IME_ACTION_DONE) || (event != null && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                hideFilterSheet();
                viewModel.setPage(1);
                viewModel.setSearchQuery(binding.searchText.getText().toString().trim());
                viewModel.performSearch();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(binding.searchText.getWindowToken(), 0);
                binding.searchText.clearFocus();
                return true;
            } else {
                return false;
            }
        });

        updateFilterButtonsVisibility();

    }

    private void updateFilterButtonsVisibility() {
        if (viewModel.getType() != null && !viewModel.getType().isEmpty()) {
            boolean show = viewModel.getType().equals("product");
            binding.filterSort.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.filterPrice.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.filterCategory.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.filterBrand.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.filterShop.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.searchText.removeTextChangedListener(searchTextWatcher);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.searchText.addTextChangedListener(searchTextWatcher);
    }

    @Override
    protected void liveEventsObservers() {

        viewModel.searchTypeLiveData.observe(getViewLifecycleOwner(), s -> {
            if (s.contains("product"))
                binding.filterSearchType.setText(R.string.products);
            else if (s.contains("brand"))
                binding.filterSearchType.setText(R.string.brands);
            else if (s.contains("shop"))
                binding.filterSearchType.setText(R.string.shops);
        });

        viewModel.facetsMutableLiveData.observe(getViewLifecycleOwner(), facets -> {
            if (facets != null &&
                    facets.getMaxPrice() != null && facets.getMinPrice() != null &&
                    facets.getMaxPrice() > 0 && facets.getMinPrice() > 0 &&
                    viewModel.getMaxPrice() == null && viewModel.getMinPrice() == null) {

                binding.filterPriceRangeSlider.setValueFrom(facets.getMinPrice().floatValue());
                binding.filterPriceRangeSlider.setValueTo(facets.getMaxPrice().floatValue() + 1);
                binding.filterPriceRangeSlider.setValues(facets.getMaxPrice().floatValue(), facets.getMinPrice().floatValue());

                binding.filterPriceMin.setText("Minimum: " + Utils.formatPriceSymbol(facets.getMinPrice().intValue()));
                binding.filterPriceMax.setText("Maximum: " + Utils.formatPriceSymbol(facets.getMaxPrice().intValue()));

                binding.priceFilterUnder1k.setVisibility(facets.getMinPrice() < 1000 && facets.getMaxPrice() > 1000 ? View.VISIBLE : View.GONE);
                binding.priceFilterAbove10k.setVisibility(facets.getMaxPrice().intValue() > 10000 && facets.getMinPrice() < 10000 ? View.VISIBLE : View.GONE);
                binding.priceFilter1kTo5k.setVisibility(facets.getMinPrice() < 1000 && facets.getMaxPrice() > 5000 ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getProductList().observe(getViewLifecycleOwner(), searchHitResponses -> {
            isLoading = false;
            controller.setLoadingMore(false);
            controller.setList(searchHitResponses);
            controller.requestModelBuild();
        });

        viewModel.updateButtonHighlights.observe(getViewLifecycleOwner(), aVoid -> {
            updateFilterButton(binding.filterBrand, R.string.brand, viewModel.selectedFilterBrandsList.size());
            updateFilterButton(binding.filterCategory, R.string.category, viewModel.selectedFilterCategoriesList.size());
            updateFilterButton(binding.filterShop, R.string.shop, viewModel.selectedFilterShopsList.size());

            updateFilterButton(binding.filterSort, viewModel.getSortBy() != null);
            updateFilterButton(binding.filterPrice, viewModel.isPriceRangeSelected());
        });

    }

    private void updateFilterButton(TextView button, @StringRes int name, int count) {
        if (count > 0)
            button.setText(String.format("%s (%d)", getString(name), count));
        else
            button.setText(name);
        updateFilterButton(button, count > 0);
    }

    private void updateFilterButton(TextView button, boolean select) {
        if (select) {
            button.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.btn_search_chip_selected));
            button.setTextColor(getResources().getColor(R.color.fff));
            setTextViewDrawableColor(button, R.color.fff);
        } else {
            button.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.btn_search_chip));
            button.setTextColor(getResources().getColor(R.color.c444));
            setTextViewDrawableColor(button, R.color.c888);
        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private int updateRecyclerViewSpan() {
        if (viewModel.getType().contains("product")) {
            if (layoutManager != null)
                layoutManager.setSpanCount(2);
            if (recyclerDecoration != null)
                recyclerDecoration.setSpanCount(2);
            return 2;
        } else {
            if (layoutManager != null)
                layoutManager.setSpanCount(3);
            if (recyclerDecoration != null)
                recyclerDecoration.setSpanCount(3);
            return 3;
        }
    }

    @Override
    protected void setupRecycler() {

        if (controller == null)
            controller = new GlobalSearchController();

        controller.setFilterDuplicates(true);
        controller.setClickListener(this);
        controller.setViewModel(viewModel);
        binding.recyclerView.setAdapter(controller.getAdapter());

        int spanCount = updateRecyclerViewSpan();
        int spacing = (int) Utils.convertDpToPixel(10, getContext());

        layoutManager = new StaggeredGridLayoutManager(updateRecyclerViewSpan(), StaggeredGridLayoutManager.VERTICAL);
        recyclerDecoration = new StaggeredSpacingItemDecoration(spanCount, spacing, true);

        updateRecyclerViewSpan();
        controller.setSpanCount(spanCount);

        binding.recyclerView.addItemDecoration(recyclerDecoration);
        binding.recyclerView.setLayoutManager(layoutManager);

        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    controller.setLoadingMore(true);
                    controller.requestModelBuild();
                    viewModel.loadMore();
                    isLoading = true;
                }
            }
        });

        controller.requestModelBuild();
    }

    @Override
    public void onProductClick(ProductsItem model) {
        Intent intent = new Intent(getContext(), ViewProductActivity.class);
        intent.putExtra("product_slug", model.getSlug());
        intent.putExtra("product_name", model.getName());
        intent.putExtra("product_price", model.getPrice());
        intent.putExtra("product_image", model.getProductImage());
        getActivity().startActivity(intent);
    }

    @Override
    public void onGridItemClick(String type, String title, String image, String slug) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        if (type.equals("shop")) {
            bundle.putString("shop_slug", slug);
            navController.navigate(R.id.shopFragment, bundle);
        } else if (type.equals("brand")) {
            bundle.putString("brand_slug", slug);
            navController.navigate(R.id.brandFragment, bundle);
        }
    }
}
