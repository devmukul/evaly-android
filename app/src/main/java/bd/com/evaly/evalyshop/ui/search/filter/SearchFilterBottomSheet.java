package bd.com.evaly.evalyshop.ui.search.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.databinding.BottomsheetSearchFilterBinding;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchViewModel;
import bd.com.evaly.evalyshop.ui.search.controller.FilterRootController;
import bd.com.evaly.evalyshop.ui.search.controller.FilterSubController;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFilterBottomSheet extends BottomSheetDialogFragment {

    @Inject
    GlobalSearchViewModel viewModel;
    private BottomsheetSearchFilterBinding binding;
    private FilterRootController filterRootController;
    private FilterSubController filterSubController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomsheetSearchFilterBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initFilterModal();
        liveEventObservers();
    }

    private void initFilterModal() {
        if (filterRootController == null)
            filterRootController = new FilterRootController();
        filterRootController.setFilterDuplicates(true);
        filterRootController.setViewModel(viewModel);

        binding.rvRoot.setAdapter(filterRootController.getAdapter());

        if (filterSubController == null)
            filterSubController = new FilterSubController();
        filterSubController.setFilterDuplicates(true);
        filterSubController.setViewModel(viewModel);

        binding.rvSub.setAdapter(filterSubController.getAdapter());
    }

    private void initViews() {

    }

    private void liveEventObservers() {
        viewModel.getFilterRootLiveList().observe(getViewLifecycleOwner(), filterRootItems -> {
            Logger.d(new Gson().toJson(filterRootItems));
            filterRootController.setList(filterRootItems);
            filterRootController.requestModelBuild();
        });

        viewModel.getFilterSubLiveList().observe(getViewLifecycleOwner(), filterSubItems -> {
            filterSubController.setList(filterSubItems);
            filterSubController.requestModelBuild();
        });

        viewModel.getReloadFilters().observe(getViewLifecycleOwner(), aVoid -> {
            filterRootController.requestModelBuild();
            filterSubController.requestModelBuild();
        });
    }

}