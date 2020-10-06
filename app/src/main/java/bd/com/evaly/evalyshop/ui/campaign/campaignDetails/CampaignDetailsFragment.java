package bd.com.evaly.evalyshop.ui.campaign.campaignDetails;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentCampaignDetailsBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.ui.campaign.campaignDetails.controller.CampaignCategoryController;
import bd.com.evaly.evalyshop.util.BindingUtils;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;

public class CampaignDetailsFragment extends Fragment {

    private FragmentCampaignDetailsBinding binding;
    private CampaignDetailsViewModel viewModel;
    private CampaignCategoryController controller;
    private NavController navController;
    private boolean isLoading = true;

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCampaignDetailsBinding.inflate(inflater);
        navController = NavHostFragment.findNavController(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CampaignDetailsViewModel.class);
        viewModel.setCampaignDetailsLiveData((CampaignCategoryResponse) requireArguments().getSerializable("model"));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setStatusBarColor();
        initToolbar();
        liveEventObservers();
        clickListeners();
        initRecycler();
        initTabs();
        initSearch();
    }

    private void initToolbar() {
        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
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
                        viewModel.setType("shop");
                        break;
                    case 1:
                        viewModel.setType("campaign");
                        break;
                    case 2:
                        viewModel.setType("products");
                        break;
                    case 3:
                        viewModel.setType("brands");
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

    private void initRecycler() {
        if (controller == null)
            controller = new CampaignCategoryController();
        controller.setNavController(navController);
        controller.setFilterDuplicates(true);
        controller.setViewModel(viewModel);
        controller.setActivity((AppCompatActivity) getActivity());
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
                    viewModel.loadListFromApi();
                }
            }
        });
        controller.requestModelBuild();
    }

    private void clickListeners() {
        binding.backArrow.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void liveEventObservers() {
        viewModel.getCampaignDetailsLiveData().observe(getViewLifecycleOwner(), this::loadCampaignDetails);

        viewModel.getLiveList().observe(getViewLifecycleOwner(), campaignProductResponses -> {
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

    }

    private void loadCampaignDetails(CampaignCategoryResponse model) {

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
    }

    private void setStatusBarColor() {
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            setWindowFlag(getActivity(), WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getActivity().getWindow().setStatusBarColor(Color.WHITE);
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
                setWindowFlag(getActivity(), WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
                getActivity().getWindow().setStatusBarColor(Color.BLACK);
            }
        }
    }

}
