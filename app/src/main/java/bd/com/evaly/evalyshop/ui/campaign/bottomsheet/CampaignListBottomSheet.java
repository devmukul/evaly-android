package bd.com.evaly.evalyshop.ui.campaign.bottomsheet;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomsheetCampaignListBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;

public class CampaignListBottomSheet extends BottomSheetDialogFragment {

    private BottomsheetCampaignListBinding binding;
    private CampaignListViewModel viewModel;
    private CampaignListController controller;
    private NavController navController;
    private boolean isLoading = true;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomsheetCampaignListBinding.inflate(inflater);
        navController = NavHostFragment.findNavController(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog);
        viewModel = new ViewModelProvider(this).get(CampaignListViewModel.class);
        assert getArguments() != null;
        if (viewModel.getCategory() == null) {
            viewModel.setCategory(getArguments().getString("category"));
            viewModel.loadFromApi();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
        liveEventObservers();
        clickListeners();
        initRecycler();
        initSearch();
    }

    private void initToolbar() {
        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
    }

    private void initSearch() {


    }

    private void initRecycler() {
        if (controller == null)
            controller = new CampaignListController();
        controller.setNavController(navController);
        controller.setFilterDuplicates(true);
        binding.recyclerView.setAdapter(controller.getAdapter());
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(staggeredGridLayoutManager);
        controller.setSpanCount(2);
        int spacing = (int) Utils.convertDpToPixel(10, requireActivity());
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(staggeredGridLayoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    isLoading = true;
                    controller.setLoading(true);
                    controller.requestModelBuild();
                    viewModel.loadFromApi();
                }
            }
        });
        controller.requestModelBuild();
    }

    private void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void liveEventObservers() {

        viewModel.getLiveData().observe(getViewLifecycleOwner(), list -> {
            isLoading = false;
            controller.setLoading(false);
            controller.setList(list);
            controller.requestModelBuild();
        });

        viewModel.getHideLoadingBar().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                controller.setLoading(false);
                controller.requestModelBuild();
            }
        });
    }

}
