package bd.com.evaly.evalyshop.ui.shop;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentShopBinding;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopDetailsResponse;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.recommender.RecommenderViewModel;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.buynow.BuyNowFragment;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.shop.controller.ShopController;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShopFragment extends BaseFragment<FragmentShopBinding, ShopViewModel> implements SwipeRefreshLayout.OnRefreshListener {
    @Inject
    ApiRepository apiRepository;
    @Inject
    RecommenderViewModel recommenderViewModel;
    long startTime = 0;
    private String slug = "", campaign_slug = "", title = "";
    private String categorySlug = null;
    private boolean isLoading = false;
    private ShopController controller;
    private boolean clickFromCategory = false;
    private ShopDetailsResponse shopDetailsModel;
    private ShopDetailsModel fullShopDetailsModel;
    private String brandSlug;

    public ShopFragment() {
        super(ShopViewModel.class, R.layout.fragment_shop);
    }

    private void refreshFragment() {
        NavHostFragment.findNavController(ShopFragment.this).navigate(R.id.shopFragment);
    }

    @Override
    protected void initViews() {
        startTime = System.currentTimeMillis();
        if (getArguments() == null) {
            Toast.makeText(getContext(), "Shop not available", Toast.LENGTH_SHORT).show();
            return;
        }
        if (getArguments().containsKey("shop_name"))
            title = getArguments().getString("shop_name");
        if (getArguments().getString("campaign_slug") != null)
            campaign_slug = getArguments().getString("campaign_slug");
        slug = getArguments().getString("shop_slug");
        brandSlug = null;

        if (getArguments().containsKey("brand_slug"))
            brandSlug = getArguments().getString("brand_slug");

        binding.swipeRefresh.setOnRefreshListener(this);

        networkCheck();
        initHeader();
    }

    private void initHeader() {
        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        new InitializeActionBar(binding.appBarLayout.headerLogo, getActivity(), "shop", mainViewModel, apiRepository);
        binding.appBarLayout.homeSearch.setOnClickListener(view12 -> {
            Bundle bundle = new Bundle();
            bundle.putString("shop_slug", slug);
            bundle.putString("shop_name", shopDetailsModel.getShopName());
            bundle.putString("campaign_slug", campaign_slug);
            bundle.putString("brand_slug", brandSlug);
            NavHostFragment.findNavController(this).navigate(R.id.shopSearchActivity, bundle);
        });

        binding.appBarLayout.homeSearch.setEnabled(false);
        binding.appBarLayout.searchTitle.setText("Search in this shop...");
    }


    private void networkCheck() {
        if (!Utils.isNetworkAvailable(getContext()))
            new NetworkErrorDialog(getContext(), new NetworkErrorDialogListener() {
                @Override
                public void onRetry() {
                    refreshFragment();
                }

                @Override
                public void onBackPress() {
                    NavHostFragment.findNavController(ShopFragment.this).navigate(R.id.homeFragment);
                }
            });
    }

    @Override
    protected void liveEventsObservers() {

        viewModel.getRatingSummary().observe(getViewLifecycleOwner(), reviewSummaryModel -> {
            // reviewSummaryModel.setTotalRatings(10);
            controller.setReviewSummaryModel(reviewSummaryModel);
            if (controller.getShopInfo() != null)
                controller.requestModelBuild();
        });

        viewModel.shopDetailsModelLiveData.observe(getViewLifecycleOwner(), shopDetailsModel -> {
            controller.setSubscribed(shopDetailsModel.getData().isSubscribed());
            controller.setSubscriberCount(shopDetailsModel.getData().getSubscriberCount());
            if (controller.getShopInfo() != null)
                controller.requestModelBuild();
        });

        viewModel.campaignDetailsLiveData.observe(getViewLifecycleOwner(), subCampaignDetailsResponse -> {
            controller.setDescription(subCampaignDetailsResponse.getDescription());
            if (controller.getShopInfo() != null)
                controller.requestModelBuild();
        });

        viewModel.shopDetailsLive.observe(getViewLifecycleOwner(), response -> {
            shopDetailsModel = response;
            controller.setCashbackRate(response.getCashbackPercentage());

            if (controller.getShopInfo() == null)
                controller.setAttr(response);

            binding.appBarLayout.homeSearch.setEnabled(true);
            controller.setLoadingMore(false);
            binding.shimmerHolder.animate().alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            binding.shimmerHolder.setVisibility(View.GONE);
                        }
                    });
            initRecommender();
        });

        viewModel.getShopCategoryListLiveData().observe(getViewLifecycleOwner(), categoryList -> {
            controller.setCategoriesLoading(false);
            if (shopDetailsModel == null)
                controller.addCategoryData(categoryList, false);
            else
                controller.addCategoryData(categoryList, true);
        });

        viewModel.getOnChatClickLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                setUpXmpp();
        });

        viewModel.getBuyNowLiveData().observe(getViewLifecycleOwner(), s -> {
            if (getActivity() != null) {
                BuyNowFragment addPhotoBottomDialogFragment =
                        BuyNowFragment.newInstance(slug, s);
                addPhotoBottomDialogFragment.show(getActivity().getSupportFragmentManager(),
                        "BuyNow");
            }
        });

        viewModel.getSelectedCategoryLiveData().observe(getViewLifecycleOwner(), tabsItem -> {
            ApiClient.getUnsafeOkHttpClient().dispatcher().cancelAll();
            viewModel.clearProductList();
            clickFromCategory = true;
            categorySlug = tabsItem.getSlug();
            viewModel.setCategorySlug(categorySlug);
            viewModel.setCurrentPage(1);
            controller.setCategoryTitle(tabsItem.getTitle());
            controller.clear();
            controller.setLoadingMore(true);
            viewModel.loadShopProducts();
            binding.appBarLayout.appBarLayout.setExpanded(false, true);
        });

        viewModel.getOnResetLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                ApiClient.getUnsafeOkHttpClient().dispatcher().cancelAll();
                viewModel.clearProductList();
                viewModel.setCategorySlug(null);
                viewModel.setCurrentPage(1);
                controller.setCategoryTitle(null);
                controller.clear();
                controller.setLoadingMore(true);
                clickFromCategory = true;
                viewModel.loadShopProducts();
            }
        });

        viewModel.getProductListLiveData().observe(getViewLifecycleOwner(), shopItems -> {

            if (viewModel.getCurrentPage() < 3 && shopItems.size() == 0) {
                controller.showEmptyPage(true, true);
            }

            if (clickFromCategory) {
                binding.recyclerView.postDelayed(() -> binding.recyclerView.smoothScrollToPosition(4), 200);
                clickFromCategory = false;
            }

            isLoading = false;
            binding.recyclerView.setVisibility(View.VISIBLE);

            List<ProductItem> tempList = new ArrayList<>();
            for (int i = 0; i < shopItems.size(); i++) {
                ItemsItem shopItem = shopItems.get(i);
                ProductItem item = new ProductItem();
                item.setImageUrls(shopItem.getItemImages());
                item.setSlug(shopItem.getShopItemSlug());
                item.setName(shopItem.getItemName());
                item.setMaxPrice(String.valueOf(shopItem.getItemPrice()));
                item.setMinPrice(String.valueOf(shopItem.getItemPrice()));
                item.setMinDiscountedPrice(String.valueOf(shopItem.getDiscountedPrice()));
                item.setUniqueId(item.getSlug());
                item.setInStock(shopItem.getInStock());
                tempList.add(item);
            }

            controller.setLoadingMore(false);
            controller.addData(tempList);
        });

    }

    @Override
    protected void clickListeners() {

    }

    @Override
    protected void setupRecycler() {
        controller = new ShopController();
        controller.setActivity((AppCompatActivity) getActivity());
        controller.setFragment(this);
        controller.setViewModel(viewModel);
        controller.setCampaignSlug(campaign_slug);
        controller.setFilterDuplicates(true);

        if (fullShopDetailsModel != null)
            controller.setAttr(shopDetailsModel);

        int spanCount = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        controller.setSpanCount(spanCount);

        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        binding.recyclerView.addItemDecoration(new StaggeredSpacingItemDecoration(spanCount, spacing, true));
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(controller.getAdapter());
        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    controller.setLoadingMore(true);
                    viewModel.loadShopProducts();
                    isLoading = true;
                }
            }
        });
    }

    private void initRecommender() {
        recommenderViewModel.insert("shop",
                shopDetailsModel.getSlug(),
                shopDetailsModel.getShopName(),
                shopDetailsModel.getShopImage());
    }

    private void updateRecommender() {
        if (shopDetailsModel == null)
            return;
        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        recommenderViewModel.updateSpentTime("shop",
                shopDetailsModel.getSlug(),
                diff);
    }


    private void setUpXmpp() {

        if (shopDetailsModel == null)
            return;

        if (CredentialManager.getToken().equals("")) {
            startActivity(new Intent(getActivity(), SignInActivity.class));
            requireActivity().finish();
        } else {

            Intent launchIntent = new Intent("bd.com.evaly.econnect.OPEN_MAINACTIVITY");
            try {

                Logger.d(new Gson().toJson(shopDetailsModel));
                RosterTable rosterTable = new RosterTable();
                rosterTable.name = shopDetailsModel.getShopName();
                if (shopDetailsModel.getOwnerName() == null || shopDetailsModel.getOwnerName().isEmpty()) {
                    rosterTable.id = shopDetailsModel.getContactNumber() + "@" + Constants.XMPP_HOST;
                } else {
                    rosterTable.id = shopDetailsModel.getOwnerName() + "@" + Constants.XMPP_HOST;
                }

                rosterTable.imageUrl = shopDetailsModel.getShopImage();
                rosterTable.status = 0;
                rosterTable.lastMessage = "";
                rosterTable.time = 0;
                Logger.d(new Gson().toJson(rosterTable));

                launchIntent.putExtra("to", "OPEN_CHAT_DETAILS");
                launchIntent.putExtra("from", "shop");
                launchIntent.putExtra("user", CredentialManager.getUserName());
                launchIntent.putExtra("password", CredentialManager.getPassword());
                launchIntent.putExtra("userInfo", new Gson().toJson(CredentialManager.getUserData()));
                launchIntent.putExtra("roster", new Gson().toJson(rosterTable));

                startActivity(launchIntent);
            } catch (ActivityNotFoundException e) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "bd.com.evaly.econnect")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "bd.com.evaly.econnect")));
                } catch (Exception e2) {
                    ToastUtils.show("Please install eConnect app from Google Play Store");
                }
            }
        }
    }


    @Override
    public void onRefresh() {
        binding.swipeRefresh.setRefreshing(false);
        viewModel.setCategorySlug(null);

        binding.shimmerHolder.setVisibility(View.VISIBLE);
        binding.shimmerHolder.setAlpha(1);

        controller.clear();
        viewModel.reload();
    }


    @Override
    public void onDestroy() {
        updateRecommender();
        super.onDestroy();
    }
}

