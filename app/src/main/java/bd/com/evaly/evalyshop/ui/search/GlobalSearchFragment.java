package bd.com.evaly.evalyshop.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.databinding.FragmentGlobalSearchBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.ui.search.controller.GlobalSearchController;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;
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
        liveEventObservers();
    }

    private void initSearchViews() {

    }

    private void initViews() {

    }

    private void liveEventObservers() {
        viewModel.setSearchQuery("iphone");
        viewModel.searchOnAlogia();
        viewModel.getProductList().observe(getViewLifecycleOwner(), searchHitResponses -> {
            isLoading = false;
            controller.setList(searchHitResponses);
            controller.requestModelBuild();
        });
    }

    private void intRecyclerView() {
        if (controller == null)
            controller = new GlobalSearchController();
        binding.recyclerView.setAdapter(controller.getAdapter());

        int spanCount = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        controller.setSpanCount(spanCount);

        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        binding.recyclerView.setLayoutManager(layoutManager);

        controller.requestModelBuild();
        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    viewModel.searchOnAlogia();
                    /// controller.setLoadingMore(true);
                    isLoading = true;
                }
            }
        });

    }


}
