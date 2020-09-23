package bd.com.evaly.evalyshop.ui.campaign;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentCampaignBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.ui.campaign.controller.CampaignBannerController;
import bd.com.evaly.evalyshop.ui.campaign.controller.CampaignController;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignSliderModel_;
import bd.com.evaly.evalyshop.util.BindingUtils;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class CampaignFragment extends Fragment implements CampaignNavigator {

    int coverHeight;
    private FragmentCampaignBinding binding;
    private CampaignViewModel viewModel;
    private NavController navController;
    private CampaignBannerController sliderController;
    private CampaignController productController;
    private boolean isLoading = false;
    private boolean isExpanded = true;

    public static CampaignFragment newInstance() {
        final CampaignFragment fragment = new CampaignFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCampaignBinding.inflate(inflater);
        navController = NavHostFragment.findNavController(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        viewModel = new ViewModelProvider(this).get(CampaignViewModel.class);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupToolbar();
        initSlider();
        initRecycler();
        initSearch();
        liveEventsObserver();
        clickListeners();

    }

    private void initSearch() {

        binding.searchText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.searchText.getText().toString().trim();
                if (query.length() == 0) {
                    ToastUtils.show("Write what you want to search!");
                    return false;
                }
                viewModel.clear();
                viewModel.setSearch(query);
                viewModel.loadCampaignProducts();
                binding.searchText.clearFocus();
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(binding.searchText.getWindowToken(), 0);
                return true;
            }
            return false;
        });

    }

    private void clickListeners() {
        binding.backArrow.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void initSlider() {
        if (sliderController == null)
            sliderController = new CampaignBannerController();
        sliderController.setFilterDuplicates(true);
        binding.sliderPager.setAdapter(sliderController.getAdapter());

        new TabLayoutMediator(binding.sliderIndicator, binding.sliderPager,
                (tab, position) -> tab.setText("")
        ).attach();

        binding.sliderIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (binding.sliderIndicator.getTabCount() > 0 && sliderController.getAdapter().getItemCount() > 0) {
                    CampaignSliderModel_ model = (CampaignSliderModel_) sliderController.getAdapter().getModelAtPosition(tab.getPosition());
                    initHeader(model.model().getBannerImage());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (binding.sliderIndicator.getTabCount() > 0 && sliderController.getAdapter().getItemCount() > 0) {
                    CampaignSliderModel_ model = (CampaignSliderModel_) sliderController.getAdapter().getModelAtPosition(tab.getPosition());
                    initHeader(model.model().getBannerImage());
                }
            }
        });
    }

    private void setupToolbar() {
        isExpanded = true;
        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;

        binding.marginFrame.setPadding(0, binding.marginFrame.getPaddingTop() + statusBarHeight, 0, 0);

        // binding.collapsingToolbar.setScrimVisibleHeightTrigger((binding.appBar.getHeight()-1));
        coverHeight = ((binding.coverHolder.getHeight()) + statusBarHeight + (int) Utils.convertDpToPixel(60, getContext())) * -1;
        coverHeight = (int) (Utils.convertDpToPixel(100, getContext())) * -1;
        binding.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            appBarLayout.post(() -> {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    if (isExpanded)
                        return;
                    isExpanded = true;
                    darkStatusBar();
                    getActivity().getWindow().setStatusBarColor(Color.WHITE);
                    binding.backArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
                    binding.searchText.setTextColor(Color.parseColor("#777777"));
                    binding.searchContainer.setBackground(getResources().getDrawable(R.drawable.input_brd_round_light));
                } else {
                    if (!isExpanded)
                        return;
                    isExpanded = false;
                    binding.collapsingToolbar.setScrimsShown(false, true);
                    if (getActivity() != null && getActivity().getWindow() != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
                            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            getActivity().getWindow().setStatusBarColor(Color.WHITE);
                    }

                    binding.backArrow.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
                    binding.searchText.setTextColor(Color.parseColor("#ffffff"));
                    binding.searchContainer.setBackground(getResources().getDrawable(R.drawable.input_brd_round_dark));
                }
            });
        });
    }

    private void liveEventsObserver() {

        viewModel.getCategoryLiveList().observe(getViewLifecycleOwner(), campaignCategoryResponses -> {
            sliderController.addData(campaignCategoryResponses);
            sliderController.requestModelBuild();
            productController.setCategoryList(campaignCategoryResponses);
            productController.requestModelBuild();
            if (binding.sliderIndicator.getTabCount() > 0 && sliderController.getAdapter().getItemCount() > 0) {
                CampaignSliderModel_ model = (CampaignSliderModel_) sliderController.getAdapter().getModelAtPosition(0);
                initHeader(model.model().getBannerImage());
            }
        });

        viewModel.getProductsLiveList().observe(getViewLifecycleOwner(), campaignProductResponses -> {
            productController.setLoading(false);
            isLoading = false;
            productController.setProductList(campaignProductResponses);
            productController.requestModelBuild();
        });
    }

    private void initRecycler() {
        if (productController == null)
            productController = new CampaignController();
        productController.setNavController(navController);
        binding.recyclerView.setAdapter(productController.getAdapter());
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(staggeredGridLayoutManager);
        productController.setSpanCount(2);
        int spacing = (int) Utils.convertDpToPixel(10, requireActivity());
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(staggeredGridLayoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    productController.setLoading(true);
                    productController.requestModelBuild();
                    viewModel.loadCampaignProducts();
                    isLoading = true;
                }
            }
        });
    }

    private void initHeader(String url) {
        Glide.with(binding.coverImage)
                .asBitmap()
                .load(url == null ? R.drawable.bg_fafafa_round : url)
                .load(BindingUtils.generateResizeUrl(url, 1450, 460))
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(15, 2)))
                .into(binding.coverImage);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onListLoaded(List<CampaignItem> list) {

    }

    @Override
    public void onListFailed(String errorBody, int errorCode) {
        if (getActivity() == null || getActivity().isFinishing() || binding == null)
            return;
        ToastUtils.show("Error occurred!");
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

