package bd.com.evaly.evalyshop.ui.search;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentGlobalSearchBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.search.controller.GlobalSearchController;
import bd.com.evaly.evalyshop.ui.search.filter.SearchFilterBottomSheet;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import bd.com.evaly.evalyshop.views.behaviors.TopSheetBehavior;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GlobalSearchFragment extends BaseFragment<FragmentGlobalSearchBinding, GlobalSearchViewModel> {

    private GlobalSearchController controller;
    private boolean isLoading = false;

    public GlobalSearchFragment() {
        super(GlobalSearchViewModel.class, R.layout.fragment_global_search);
    }

    @Override
    protected void initViews() {
        initSearchViews();
    }

    @Override
    protected void clickListeners() {
        binding.filerSearchType.setOnClickListener(view -> {
            TopSheetBehavior.from(binding.filterSheet).setState(TopSheetBehavior.STATE_EXPANDED);
        });

        binding.filter.setOnClickListener(view -> {
            SearchFilterBottomSheet bottomSheet = new SearchFilterBottomSheet();
            bottomSheet.show(getParentFragmentManager(), "filter");
        });
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
                    viewModel.searchOnAlogia();
                    isLoading = true;
                }
            }
        });

    }


}
