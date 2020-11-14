package bd.com.evaly.evalyshop.ui.campaign.bottomsheet;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomsheetCampaignListBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;

public class CampaignListBottomSheet extends BottomSheetDialogFragment {

    private BottomsheetCampaignListBinding binding;
    private CampaignListViewModel viewModel;
    private MainViewModel mainViewModel;
    private CampaignListController controller;
    private NavController navController;
    private boolean isLoading = true;
    private boolean showClear = false;


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
        binding.search.setHint("Search in " + viewModel.getCategory().getName());
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
        controller.setNavController(navController);
        controller.setFilterDuplicates(true);
        controller.setClickListener(model -> {
            mainViewModel.setCampaignOnClick(model);
            dismissAllowingStateLoss();
        });
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
        binding.clearFilter.setOnClickListener(view -> {
            mainViewModel.setCampaignOnClick(null);
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
        });

        viewModel.getHideLoadingBar().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                controller.setLoading(false);
                controller.requestModelBuild();
            }
        });
    }

}