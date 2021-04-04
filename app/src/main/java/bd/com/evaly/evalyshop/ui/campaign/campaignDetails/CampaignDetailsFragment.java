package bd.com.evaly.evalyshop.ui.campaign.campaignDetails;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentCampaignDetailsBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.buynow.BuyNowFragment;
import bd.com.evaly.evalyshop.ui.campaign.bottomsheet.CampaignListBottomSheet;
import bd.com.evaly.evalyshop.ui.campaign.campaignDetails.controller.CampaignCategoryController;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.util.BindingUtils;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CampaignDetailsFragment extends BaseFragment<FragmentCampaignDetailsBinding, CampaignDetailsViewModel> {

    private MainViewModel mainViewModel;
    private CampaignCategoryController controller;
    private boolean isLoading = true;
    private ViewDialog progressDialog;

    public CampaignDetailsFragment() {
        super(CampaignDetailsViewModel.class, R.layout.fragment_campaign_details);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        progressDialog = new ViewDialog(getActivity());
    }

    private void checkArguments() {
        if (getArguments() != null && getArguments().containsKey("sub_model") && getArguments().getSerializable("sub_model") != null) {
            SubCampaignResponse subCampaignResponse = (SubCampaignResponse) getArguments().getSerializable("sub_model");
            mainViewModel.selectedCampaignModel = subCampaignResponse;
            mainViewModel.campaignFilterUpdated.call();
            viewModel.setCampaign(subCampaignResponse.getSlug());
        }

        if (checkArg("model"))
            viewModel.setCampaignCategoryLiveData((CampaignCategoryResponse) requireArguments()
                    .getSerializable("model"));
        else if (checkArg("category_slug") &&
                !requireArguments().containsKey("campaign_slug"))
            viewModel.findCampaignCategoryDetails(requireArguments()
                    .getString("category_slug"));
        else if (checkArg("category_slug_suppliers")) {
            viewModel.setSelectTypeAfterLoading("shop");
            viewModel.findCampaignCategoryDetails(requireArguments()
                    .getString("category_slug_suppliers"));
        } else if (checkArg("category_slug_brands")) {
            viewModel.setSelectTypeAfterLoading("brand");
            viewModel.findCampaignCategoryDetails(requireArguments()
                    .getString("category_slug_brands"));
        } else if (checkArg("category_slug_products")) {
            // viewModel.setSelectTypeAfterLoading("product");
            viewModel.findCampaignCategoryDetails(requireArguments()
                    .getString("category_slug_products"));
        } else if (checkArg("category_slug") && requireArguments().containsKey("campaign_slug"))
            viewModel.loadSubCampaignDetails(requireArguments()
                    .getString("campaign_slug"));
        if (checkArg("type") && requireArguments().getString("type") != null) {
            String type = requireArguments().getString("type");
            assert type != null;
            if (!type.contains("shop") && !type.contains("supplier"))
                viewModel.setSelectTypeAfterLoading(requireArguments().getString("type"));
        }

        viewModel.loadProductCategories();
    }

    private boolean checkArg(String key) {
        if (getArguments() == null)
            return false;
        return requireArguments().containsKey(key);
    }

    @Override
    protected void initViews() {
        checkArguments();
        initToolbar();
        initTabs();
        initSearch();
        if (getArguments() != null && getArguments().containsKey("open_filter"))
            openFilterModal();
    }

    private void initToolbar() {
        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        if (viewModel.getCampaign() == null)
            binding.filterIndicator.setVisibility(View.GONE);
        else
            binding.filterIndicator.setVisibility(View.VISIBLE);

        updateSortIndicator();
    }

    private void updateSortIndicator() {
        if (viewModel.getSort() == null)
            binding.sortIndicator.setVisibility(View.GONE);
        else
            binding.sortIndicator.setVisibility(View.VISIBLE);
    }

    private void initSearch() {
        ImageView icon = binding.searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        icon.setColorFilter(Color.WHITE);
        binding.searchView.setMaxWidth(Integer.MAX_VALUE);
        binding.searchView.setOnSearchClickListener(view -> {
            binding.title.setVisibility(View.GONE);
        });
        binding.searchView.setOnCloseListener(() -> {
            binding.title.setVisibility(View.VISIBLE);
            viewModel.clear();
            viewModel.setSearch(null);
            viewModel.loadListFromApi();
            return false;
        });
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() == 0) {
                    ToastUtils.show("Write what you want to search!");
                    return false;
                }
                viewModel.clear();
                viewModel.setSearch(query);
                viewModel.loadListFromApi();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void initTabs() {
        String type = viewModel.getType();
        if (type != null) {
            if (type.equals("product"))
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0));
            else if (type.equals("shop"))
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1));
            else if (type.equals("brand"))
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(2));
        }
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewModel.clear();
                controller.setList(new ArrayList<>());
                controller.setLoading(true);
                controller.requestModelBuild();

                switch (tab.getPosition()) {
                    case 0:
                        viewModel.setType("product");
                        binding.sortHolder.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        viewModel.setType("shop");
                        binding.sortHolder.setVisibility(View.GONE);
                        break;
                    case 2:
                        viewModel.setType("brand");
                        binding.sortHolder.setVisibility(View.GONE);
                        break;
                }

                viewModel.loadListFromApi();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void setupRecycler() {

        if (controller == null)
            controller = new CampaignCategoryController();
        controller.setNavController(navController);
        controller.setFilterDuplicates(true);
        controller.setViewModel(viewModel);
        controller.setMainViewModel(mainViewModel);
        controller.setActivity((AppCompatActivity) getActivity());
        binding.recyclerView.setAdapter(controller.getAdapter());
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(staggeredGridLayoutManager);
        controller.setSpanCount(2);
        int spacing = (int) Utils.convertDpToPixel(10, requireActivity());
        binding.recyclerView.addItemDecoration(new StaggeredSpacingItemDecoration(2, spacing, true));
        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(staggeredGridLayoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    isLoading = true;
                    controller.setLoading(true);
                    controller.requestModelBuild();
                    viewModel.loadListFromApi();
                }
            }
        });
        controller.requestModelBuild();
    }

    @Override
    protected void clickListeners() {

        binding.sortBtn.setOnClickListener(v -> {
            showSortDialog();
        });

        binding.filterBtn.setOnClickListener(view -> {
            openFilterModal();
        });
        binding.backArrow.setOnClickListener(v -> requireActivity().onBackPressed());

    }

    private void showSortDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sort Products By\n");
        final String[] a = {"Latest", "Price: low to high", "Price: high to low"};

        int checkItem = 0;

        if (viewModel.getSort() != null) {
            if (viewModel.getSort().equals("asc"))
                checkItem = 1;
            else if (viewModel.getSort().equals("desc"))
                checkItem = 2;
        }

        builder.setSingleChoiceItems(a, checkItem, (dialogInterface, i) -> {
            if (i == 0)
                viewModel.setSort(null);
            else if (i == 1)
                viewModel.setSort("asc");
            else if (i == 2)
                viewModel.setSort("desc");

            viewModel.clear();
            viewModel.loadListFromApi();
            updateSortIndicator();
            dialogInterface.dismiss();
        });
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();

    }

    private void openFilterModal() {
        if (viewModel.getCampaignCategoryLiveData() != null && viewModel.getCampaignCategoryLiveData().getValue() != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("category", Objects.requireNonNull(viewModel.getCampaignCategoryLiveData().getValue()));
            if (viewModel.getSelectedCategoryModel() != null)
                bundle.putSerializable("product_category", Objects.requireNonNull(viewModel.getSelectedCategoryModel()));
            bundle.putBoolean("show_clear", viewModel.getCampaign() != null || viewModel.getSelectedCategorySlug() != null);
            // navController.navigate(R.id.campaignListBottomSheet, bundle);
            CampaignListBottomSheet bottomSheet = new CampaignListBottomSheet();
            bottomSheet.setArguments(bundle);
            bottomSheet.show(getParentFragmentManager(), "FilterBottomSheet");
        }
    }

    @Override
    protected void liveEventsObservers() {

        viewModel.onCategoryChanged.observe(getViewLifecycleOwner(), model -> {
            if (model == null && mainViewModel.selectedCampaignModel == null)
                binding.filterIndicator.setVisibility(View.GONE);
            else
                binding.filterIndicator.setVisibility(View.VISIBLE);
        });

        viewModel.openFilterModal.observe(getViewLifecycleOwner(), aVoid -> openFilterModal());

        viewModel.productCategoriesLiveData.observe(getViewLifecycleOwner(), campaignProductCategoryResponses -> {
            controller.setCategoryLoading(false);
            controller.setCategoryList(campaignProductCategoryResponses);
            controller.requestModelBuild();
            if (viewModel.getCurrentPage() <= 2) {
                binding.recyclerView.postDelayed(() -> binding.recyclerView.smoothScrollToPosition(0), 200);
            }
        });

        viewModel.switchTab.observe(getViewLifecycleOwner(), s -> {
            if (s.contains("product"))
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0));
            else if (s.contains("shop") || s.contains("supplier"))
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1));
            else if (s.contains("brand"))
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(2));
        });

        viewModel.subCampaignLiveData.observe(getViewLifecycleOwner(), subCampaignResponse -> {
            if (subCampaignResponse != null) {
                mainViewModel.selectedCampaignModel = subCampaignResponse;
                mainViewModel.campaignFilterUpdated.call();
            } else {
                ToastUtils.show("Campaign is not running now!");
                if (getActivity() != null)
                    getActivity().onBackPressed();
            }
        });

        viewModel.hideProgressDialog.observe(getViewLifecycleOwner(), aBoolean -> {
            progressDialog.hideDialog();
        });

        viewModel.getBuyNowClick().observe(getViewLifecycleOwner(), campaignProductResponse -> {
            BuyNowFragment addPhotoBottomDialogFragment =
                    BuyNowFragment.newInstance(campaignProductResponse.getShopSlug(), campaignProductResponse.getSlug());
            addPhotoBottomDialogFragment.show(getActivity().getSupportFragmentManager(),
                    "BuyNow");
        });

        viewModel.getCampaignCategoryLiveData().observe(getViewLifecycleOwner(), this::loadCampaignDetails);

        viewModel.getLiveList().observe(getViewLifecycleOwner(), campaignProductResponses -> {
            progressDialog.hideDialog();
            isLoading = false;
            controller.setLoading(false);
            controller.setList(campaignProductResponses);
            controller.requestModelBuild();
        });

        viewModel.getHideLoadingBar().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                controller.setLoading(false);
                controller.requestModelBuild();
            }
        });

        mainViewModel.campaignFilterUpdated.observe(getViewLifecycleOwner(), aVoid -> {
            viewModel.clear();
            if (mainViewModel.selectedCampaignModel == null && mainViewModel.selectedCampaignProductCategoryModel == null) {
                binding.filterIndicator.setVisibility(View.GONE);
                viewModel.setCampaign(null);
                viewModel.setSelectedCategoryModel(null);
            } else {
                if (mainViewModel.selectedCampaignModel != null)
                    viewModel.setCampaign(mainViewModel.selectedCampaignModel.getSlug());
                viewModel.setSelectedCategoryModel(mainViewModel.selectedCampaignProductCategoryModel);
                binding.filterIndicator.setVisibility(View.VISIBLE);
            }
            viewModel.loadListFromApi();
            binding.recyclerView.scrollToPosition(0);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadCampaignDetails(CampaignCategoryResponse model) {

        if (model == null)
            return;

        if (Utils.isValidColor(model.getBannerPrimaryBgColor())) {
            ViewCompat.setBackgroundTintList(
                    binding.coverImage,
                    ColorStateList.valueOf(Color.parseColor(model.getBannerPrimaryBgColor())));
            binding.header.setBackgroundColor(Color.parseColor(model.getBannerPrimaryBgColor()));
            binding.collapsingToolbar.setStatusBarScrimColor(Color.parseColor(model.getBannerPrimaryBgColor()));
            requireActivity().getWindow().setStatusBarColor(Color.parseColor(model.getBannerPrimaryBgColor()));
        }
        BindingUtils.setImage(binding.bannerImage, model.getBannerImage(), R.drawable.bg_fafafa_round, R.drawable.ic_evaly_placeholder, 1450, 460, false);
        binding.title.setText(model.getName());
        viewModel.loadProductCategories();
    }

}
