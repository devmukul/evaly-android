package bd.com.evaly.evalyshop.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentGlobalSearchBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.ui.search.controller.GlobalSearchController;
import bd.com.evaly.evalyshop.ui.search.filter.SearchFilterBottomSheet;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GlobalSearchFragment extends Fragment {

    @Inject
    GlobalSearchViewModel viewModel;
    private FragmentGlobalSearchBinding binding;
    private GlobalSearchController controller;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGlobalSearchBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initSearchViews();
        intRecyclerView();
        clickListeners();
        liveEventObservers();
    }

    private void clickListeners() {
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

    private void initViews() {

    }

    private void liveEventObservers() {
        viewModel.getProductList().observe(getViewLifecycleOwner(), searchHitResponses -> {
            isLoading = false;
            controller.setLoadingMore(false);
            controller.setList(searchHitResponses);
            controller.requestModelBuild();
        });

    }

    private void intRecyclerView() {
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
