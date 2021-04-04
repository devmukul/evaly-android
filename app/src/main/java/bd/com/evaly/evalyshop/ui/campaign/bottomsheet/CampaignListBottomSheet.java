package bd.com.evaly.evalyshop.ui.campaign.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomsheetCampaignListBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignProductCategoryResponse;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.util.ScreenUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CampaignListBottomSheet extends BottomSheetDialogFragment {

    private BottomsheetCampaignListBinding binding;
    private CampaignListViewModel viewModel;
    private MainViewModel mainViewModel;
    private CampaignListController controller;
    private NavController navController;
    private boolean isLoading = true;
    private boolean showClear = false;
    private StaggeredSpacingItemDecoration staggeredSpacingItemDecoration;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private boolean updateGridAfterLoad = false;


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
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        assert getArguments() != null;
        if (getArguments().containsKey("show_clear"))
            showClear = getArguments().getBoolean("show_clear");
        if (viewModel.getCategory() == null) {
            viewModel.setCategory((CampaignCategoryResponse) getArguments().getSerializable("category"));
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
        updateViews();
    }

    private void updateViews() {

        binding.filterTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab != null && tab.getText() != null) {
                    if (tab.getText().toString().contains("Category")) {
                        viewModel.setType("categories");
                        binding.search.setHint("Search categories");
                    } else if (tab.getText().toString().contains("Campaign")) {
                        viewModel.setType("campaigns");
                        binding.search.setHint("Search campaigns");
                    }
                    updateGridAfterLoad = true;
                    viewModel.clear();
                    viewModel.loadFromApi();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        if (showClear)
            binding.clearFilter.setVisibility(View.VISIBLE);
        else
            binding.clearFilter.setVisibility(View.GONE);
    }

    private void initToolbar() {
        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
    }

    private void initSearch() {
        binding.search.setHint("Search " + viewModel.getType());
        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = binding.search.getText().toString();
                if (query.length() > 0)
                    binding.clear.setVisibility(View.VISIBLE);
                else {
                    binding.clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.search.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        viewModel.clear();
                        viewModel.setSearch(binding.search.getText().toString().trim());
                        viewModel.loadFromApi();
                        binding.search.clearFocus();
                        hideKeyboard();
                        return true;
                    }
                    return false;
                });

        binding.clear.setOnClickListener(view -> {
            binding.search.setText("");
            viewModel.clear();
            viewModel.setSearch(null);
            viewModel.loadFromApi();
            hideKeyboard();
        });

    }

    private void hideKeyboard() {
        InputMethodManager in = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert in != null;
        in.hideSoftInputFromWindow(binding.search.getWindowToken(), 0);
    }

    private void initRecycler() {
        if (controller == null)
            controller = new CampaignListController();
        controller.setFilterDuplicates(true);
        controller.setSpanCount(3);
        controller.setClickListener(new CampaignListController.ClickListener() {
            @Override
            public void onClick(SubCampaignResponse model) {
                mainViewModel.selectedCampaignModel = model;
                mainViewModel.campaignFilterUpdated.call();
                dismissAllowingStateLoss();
            }

            @Override
            public void onProductCategoryClick(CampaignProductCategoryResponse model) {
                mainViewModel.selectedCampaignProductCategoryModel = model;
                mainViewModel.campaignFilterUpdated.call();
                dismissAllowingStateLoss();
            }
        });

        binding.recyclerView.setAdapter(controller.getAdapter());

        int spacing = (int) Utils.convertDpToPixel(10, requireActivity());
        staggeredSpacingItemDecoration = new StaggeredSpacingItemDecoration(3, spacing, true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(staggeredGridLayoutManager);

        binding.recyclerView.addItemDecoration(staggeredSpacingItemDecoration);
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
        binding.clearFilter.setOnClickListener(view -> {
            mainViewModel.selectedCampaignProductCategoryModel = null;
            mainViewModel.selectedCampaignModel = null;
            mainViewModel.campaignFilterUpdated.call();
            dismissAllowingStateLoss();
        });
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void liveEventObservers() {

        viewModel.getLiveData().observe(getViewLifecycleOwner(), list -> {
            isLoading = false;
            controller.setLoading(false);
            controller.setList(list);
            controller.requestModelBuild();

            if (updateGridAfterLoad) {
                if (viewModel.getType().contains("categories")) {
                    staggeredGridLayoutManager.setSpanCount(3);
                    staggeredSpacingItemDecoration.setSpanCount(3);
                } else if (viewModel.getType().contains("campaigns")) {
                    staggeredGridLayoutManager.setSpanCount(2);
                    staggeredSpacingItemDecoration.setSpanCount(2);
                }
            }
            updateGridAfterLoad = false;
        });

        viewModel.getHideLoadingBar().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                controller.setLoading(false);
                controller.requestModelBuild();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialogz -> {
            BottomSheetDialog dialog = (BottomSheetDialog) dialogz;
            dialog.setCancelable(false);
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (getContext() != null && bottomSheet != null) {
                ScreenUtils screenUtils = new ScreenUtils(getContext());
                LinearLayout dialogLayoutReply = dialog.findViewById(R.id.container2);
                assert dialogLayoutReply != null;
                dialogLayoutReply.setMinimumHeight(screenUtils.getHeight());
                BottomSheetBehavior.from(bottomSheet).setDraggable(true);
                BottomSheetBehavior.from(bottomSheet).setHideable(true);
                // BottomSheetBehavior.from(bottomSheet).setPeekHeight(screenUtils.getHeight());
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return bottomSheetDialog;
    }

}
