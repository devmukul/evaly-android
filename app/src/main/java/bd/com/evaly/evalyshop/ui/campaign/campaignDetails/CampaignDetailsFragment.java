package bd.com.evaly.evalyshop.ui.campaign.campaignDetails;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import bd.com.evaly.evalyshop.databinding.FragmentCampaignDetailsBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.ui.campaign.campaignDetails.controller.CampaignCategoryController;
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
        liveEventObservers();
        clickListeners();
        initRecycler();
    }

    private void initRecycler() {
        if (controller == null)
            controller = new CampaignCategoryController();
        controller.setNavController(navController);
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
                    viewModel.loadProductList();
                    isLoading = true;
                }
            }
        });
    }

    private void clickListeners() {
        binding.backArrow.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void liveEventObservers() {
        viewModel.getCampaignDetailsLiveData().observe(getViewLifecycleOwner(), model -> {
            loadCampaignDetails(model);
        });

        viewModel.getProductLiveList().observe(getViewLifecycleOwner(), campaignProductResponses -> {
            ToastUtils.show("loaded");
            controller.setProductList(campaignProductResponses);
            controller.requestModelBuild();
        });

    }

    private void loadCampaignDetails(CampaignCategoryResponse model) {

        if (Utils.isValidColor(model.getBannerPrimaryBgColor())) {
            ViewCompat.setBackgroundTintList(
                    binding.coverImage,
                    ColorStateList.valueOf(Color.parseColor(model.getBannerPrimaryBgColor())));
            binding.zToolbar.setBackgroundColor(Color.parseColor(model.getBannerPrimaryBgColor()));
            binding.collapsingToolbar.setContentScrimColor(Color.parseColor(model.getBannerPrimaryBgColor()));
            binding.collapsingToolbar.setStatusBarScrimColor(Color.parseColor(model.getBannerPrimaryBgColor()));
        }
        Glide.with(binding.getRoot())
                .asBitmap()
                .load(model.getBannerImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(binding.bannerImage);
    }

    private void setStatusBarColor() {
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            setWindowFlag(getActivity(), WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void darkStatusBar() {
        if (getActivity() != null && getActivity().getWindow() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int flags = getActivity().getWindow().getDecorView().getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().setStatusBarColor(Color.BLACK);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getActivity().getWindow().setStatusBarColor(Color.WHITE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().setStatusBarColor(Color.WHITE);
            }
        }
    }
}
