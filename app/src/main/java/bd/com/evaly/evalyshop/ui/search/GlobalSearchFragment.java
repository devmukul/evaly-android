package bd.com.evaly.evalyshop.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentGlobalSearchBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.search.controller.GlobalSearchController;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import bd.com.evaly.evalyshop.views.behaviors.TopSheetBehavior;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GlobalSearchFragment extends BaseFragment<FragmentGlobalSearchBinding, GlobalSearchViewModel> {

    private GlobalSearchController controller;
    private boolean isLoading = false;
    private TopSheetBehavior filterTopSheetBehavior;

    public GlobalSearchFragment() {
        super(GlobalSearchViewModel.class, R.layout.fragment_global_search);
    }

    @Override
    protected void initViews() {
        initSearchViews();
        initFilterTopSheet();
    }

    private void initFilterTopSheet() {

        binding.filterTypeRadioGroup.setOnCheckedChangeListener((radioGroup, id) -> {
            if (id == R.id.filterTypeProducts) {
                viewModel.setType("product");
            } else if (id == R.id.filterTypeShops) {
                viewModel.setType("shop");
            } else if (id == R.id.filterTypeBrands) {
                viewModel.setType("brand");
            }
            viewModel.performSearch();
            hideFilterSheet();
        });

        binding.filterSortRadioGroup.setOnCheckedChangeListener((radioGroup, id) -> {
            if (id == R.id.filterSortRelevance) {
                viewModel.setSortBy(null);
            } else if (id == R.id.filterSortPriceHighToLow) {
                viewModel.setSortBy("desc");
            } else if (id == R.id.filterSortPriceLowToHigh) {
                viewModel.setSortBy("asc");
            }
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
        toggleFilterHolderVisibility(binding.holderFilterPrice);
        binding.filterPriceRangeSlider.setLabelFormatter(value -> (int) value + "");
        binding.filterPriceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            binding.filterPriceMax.setText("Maximum: " + binding.filterPriceRangeSlider.getValues().get(1).intValue());
            binding.filterPriceMin.setText("Minimum: " + binding.filterPriceRangeSlider.getValues().get(0).intValue());
        });
    }

    private void showFilerSearchType() {
        toggleFilterHolderVisibility(binding.holderFilterType);
        binding.filterPriceRangeSlider.setLabelFormatter(value -> (int) value + "");
    }

    private void showFilerSortBy() {
        toggleFilterHolderVisibility(binding.holderFilterSort);
    }

    private void toggleFilterHolderVisibility(LinearLayout selected) {
        if (getCurrentVisibleFilterHolderId() == selected) {
            hideFilterSheet();
            return;
        } else
            showFilterSheet();

        binding.holderFilterType.setVisibility(selected == binding.holderFilterType ? View.VISIBLE : View.GONE);
        binding.holderFilterPrice.setVisibility(selected == binding.holderFilterPrice ? View.VISIBLE : View.GONE);
        binding.holderFilterSort.setVisibility(selected == binding.holderFilterSort ? View.VISIBLE : View.GONE);

        binding.filterActionButtonHolder.setVisibility(selected == binding.holderFilterType || selected == binding.holderFilterSort ? View.GONE : View.VISIBLE);
    }

    private void hideAllFilterHolders() {
        binding.holderFilterType.setVisibility(View.GONE);
        binding.holderFilterPrice.setVisibility(View.GONE);
        binding.holderFilterSort.setVisibility(View.GONE);
        binding.filterActionButtonHolder.setVisibility(View.GONE);
    }

    private LinearLayout getCurrentVisibleFilterHolderId() {
        if (binding.holderFilterType.getVisibility() == View.VISIBLE)
            return binding.holderFilterType;
        else if (binding.holderFilterPrice.getVisibility() == View.VISIBLE)
            return binding.holderFilterPrice;
        else if (binding.holderFilterSort.getVisibility() == View.VISIBLE)
            return binding.holderFilterSort;
        else
            return new LinearLayout(getContext());
    }

    @Override
    protected void clickListeners() {
        binding.bgOverlay.setOnClickListener(view -> {
            hideFilterSheetBgOverlay(false);
            hideFilterSheet();
        });

        binding.filerSearchType.setOnClickListener(view -> {
            showFilerSearchType();
        });

        binding.filterSort.setOnClickListener(view -> {
            showFilerSortBy();
        });

        binding.filterPrice.setOnClickListener(view -> {
            showFilerByPrice();
        });
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
                .setDuration(isFast ? 150 : 500)
                .withEndAction(() -> binding.bgOverlay.setVisibility(View.GONE));
    }

    private void initSearchViews() {

        binding.searchClear.setOnClickListener(v -> {
            if (binding.searchClear.getTag().equals("clear"))
                binding.searchText.setText("");
        });

        binding.searchText.addTextChangedListener(new TextWatcher() {
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
                }
            }
        });

        binding.searchText.setOnEditorActionListener((v, actionId, event) -> {
            if ((actionId == EditorInfo.IME_ACTION_DONE) || (event != null && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
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

    }


    @Override
    protected void liveEventsObservers() {
        viewModel.getProductList().observe(getViewLifecycleOwner(), searchHitResponses -> {
            isLoading = false;
            controller.setLoadingMore(false);
            controller.setList(searchHitResponses);
            controller.requestModelBuild();
        });
    }

    @Override
    protected void setupRecycler() {

        if (controller == null)
            controller = new GlobalSearchController();

        controller.setFilterDuplicates(true);
        binding.recyclerView.setAdapter(controller.getAdapter());

        int spanCount = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        controller.setSpanCount(spanCount);

        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        binding.recyclerView.addItemDecoration(new StaggeredSpacingItemDecoration(spanCount, spacing, true));
        binding.recyclerView.setLayoutManager(layoutManager);

        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    controller.setLoadingMore(true);
                    controller.requestModelBuild();
                    viewModel.searchProducts();
                    isLoading = true;
                }
            }
        });

    }
}
