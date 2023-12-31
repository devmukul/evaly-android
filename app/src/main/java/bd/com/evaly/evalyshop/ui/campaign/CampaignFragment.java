package bd.com.evaly.evalyshop.ui.campaign;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentCampaignBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.buynow.BuyNowFragment;
import bd.com.evaly.evalyshop.ui.campaign.controller.CampaignBannerController;
import bd.com.evaly.evalyshop.ui.campaign.controller.CampaignController;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignSliderModel_;
import bd.com.evaly.evalyshop.util.BindingUtils;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.FixedStaggeredGridLayoutManager;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;
import jp.wasabeef.glide.transformations.BlurTransformation;

@AndroidEntryPoint
public class CampaignFragment extends BaseFragment<FragmentCampaignBinding, CampaignViewModel> implements CampaignNavigator {

    @Inject
    PreferenceRepository preferenceRepository;
    int coverHeight;
    private CampaignBannerController sliderController;
    private CampaignController productController;
    private boolean isLoading = false;
    private boolean isExpanded = true;
    private boolean isSharingBottomShowing = true;
    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = binding.searchText.getText().toString().trim();
            if (text.length() == 0) {
                binding.clearSearch.setVisibility(View.GONE);
                viewModel.clear();
                viewModel.setSearch(null);
                viewModel.loadCampaignProducts();
            } else
                binding.clearSearch.setVisibility(View.VISIBLE);
        }
    };


    public CampaignFragment() {
        super(CampaignViewModel.class, R.layout.fragment_campaign);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setStatusBarColor();
    }

    @Override
    protected void initViews() {
        setupToolbar();
        initSlider();
        initSearch();
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

        if (binding.searchText.getText().toString().length() > 0)
            binding.clearSearch.setVisibility(View.VISIBLE);
        else
            binding.clearSearch.setVisibility(View.GONE);

        binding.clearSearch.setOnClickListener(view -> {
            binding.searchText.setText("");
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        binding.searchText.removeTextChangedListener(searchTextWatcher);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.searchText.addTextChangedListener(searchTextWatcher);
    }

    @Override
    protected void clickListeners() {
        binding.backArrow.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.buttonRight.setOnClickListener(view -> {
            if (!isSharingBottomShowing) {
                binding.recyclerView.smoothScrollToPosition(0);
                binding.recyclerView.postDelayed(() -> binding.recyclerView.scrollToPosition(0), 500);
            } else {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, "http://evaly.com.bd/campaign");
                startActivity(Intent.createChooser(i, "Share URL"));
            }
        });
    }

    private void initSlider() {
        if (sliderController == null)
            sliderController = new CampaignBannerController();
        sliderController.setFilterDuplicates(true);
        sliderController.setNavController(navController);
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
        // isExpanded = true;
        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;

        binding.marginFrame.setPadding(0, binding.marginFrame.getPaddingTop() + statusBarHeight, 0, 0);

        coverHeight = ((binding.coverHolder.getHeight()) + statusBarHeight + (int) Utils.convertDpToPixel(60, getContext())) * -1;
        coverHeight = (int) (Utils.convertDpToPixel(100, getContext())) * -1;
        updateToolbarOnExpand(!isExpanded);
        binding.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            appBarLayout.post(() -> {
                updateToolbarOnExpand(Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange());
            });
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateToolbarOnExpand(boolean expanded) {
        if (getContext() == null)
            return;
        if (expanded) {
            if (isExpanded)
                return;
            isExpanded = true;
            darkStatusBar();
            binding.backArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            binding.clearSearch.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            binding.buttonRight.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            binding.searchText.setTextColor(getResources().getColor(R.color.c777));
            binding.searchText.setHintTextColor(getResources().getColor(R.color.c777));
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
                }
            }
            binding.buttonRight.setColorFilter(getResources().getColor(R.color.fff), PorterDuff.Mode.SRC_ATOP);
            binding.backArrow.setColorFilter(getResources().getColor(R.color.fff), PorterDuff.Mode.SRC_ATOP);
            binding.clearSearch.setColorFilter(getResources().getColor(R.color.fff), PorterDuff.Mode.SRC_ATOP);
            binding.searchText.setTextColor(getResources().getColor(R.color.fff));
            binding.searchText.setHintTextColor(getResources().getColor(R.color.fff));
            binding.searchContainer.setBackground(getResources().getDrawable(R.drawable.input_brd_round_dark));
        }
    }

    @Override
    protected void liveEventsObservers() {

        viewModel.getBuyNowClick().observe(getViewLifecycleOwner(), campaignProductResponse -> {
            BuyNowFragment addPhotoBottomDialogFragment =
                    BuyNowFragment.newInstance(campaignProductResponse.getShopSlug(), campaignProductResponse.getSlug());
            addPhotoBottomDialogFragment.show(getActivity().getSupportFragmentManager(),
                    "BuyNow");
        });

        viewModel.getCategoryLiveList().observe(getViewLifecycleOwner(), campaignCategoryResponses -> {
            sliderController.addData(campaignCategoryResponses);
            sliderController.requestModelBuild();
            productController.setCategoryList(campaignCategoryResponses);
            requestModelBuild();
            if (binding.sliderIndicator.getTabCount() > 0 && sliderController.getAdapter().getItemCount() > 0) {
                CampaignSliderModel_ model = (CampaignSliderModel_) sliderController.getAdapter().getModelAtPosition(0);
                initHeader(model.model().getBannerImage());
            }
            requestModelBuild();

        });

        viewModel.getProductsLiveList().observe(getViewLifecycleOwner(), campaignProductResponses -> {
            productController.setLoading(false);
            isLoading = false;
            productController.setProductList(campaignProductResponses);
            requestModelBuild();
        });

        viewModel.getHideLoadingBar().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                productController.setLoading(false);
                requestModelBuild();
            }
        });
    }

    private void requestModelBuild() {
        productController.requestModelBuild();
    }

    @Override
    protected void setupRecycler() {
        if (productController == null)
            productController = new CampaignController();
        productController.setNavController(navController);
        productController.setFilterDuplicates(true);
        productController.setViewModel(viewModel);
        productController.setActivity((AppCompatActivity) getActivity());
        binding.recyclerView.setAdapter(productController.getAdapter());
        FixedStaggeredGridLayoutManager staggeredGridLayoutManager = new FixedStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(staggeredGridLayoutManager);
        productController.setSpanCount(2);
        int spacing = (int) Utils.convertDpToPixel(10, requireActivity());
        binding.recyclerView.addItemDecoration(new StaggeredSpacingItemDecoration(2, spacing, true));
        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(staggeredGridLayoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    productController.setLoading(true);
                    requestModelBuild();
                    viewModel.loadCampaignProducts();
                    isLoading = true;
                }
            }
        });
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 200) {
                    if (!isSharingBottomShowing)
                        return;
                    isSharingBottomShowing = false;
                    binding.buttonRight.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
                } else if (dy < -200) {
                    if (isSharingBottomShowing)
                        return;
                    isSharingBottomShowing = true;
                    binding.buttonRight.setImageDrawable(getResources().getDrawable(R.drawable.ic_share));
                }
            }
        });
        productController.requestModelBuild();
    }

    private void initHeader(String url) {
        Glide.with(binding.coverImage)
                .load(url == null ? R.drawable.bg_fafafa_round : url)
                .load(BindingUtils.generateResizeUrl(url, 1450, 460, false))
                .transform(new BlurTransformation(15, 2))
                .into(binding.coverImage);
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

    private void darkStatusBar() {
        if (getActivity() != null && getActivity().getWindow() != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                int flags = getActivity().getWindow().getDecorView().getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                if (preferenceRepository.isDarkMode())
                    getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
                else
                    getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.fff));
            }
        }
    }

}

