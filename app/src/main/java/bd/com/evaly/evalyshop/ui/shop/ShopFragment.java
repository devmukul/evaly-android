package bd.com.evaly.evalyshop.ui.shop;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentShopBinding;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.Data;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.Shop;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.buynow.BuyNowFragment;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.shop.controller.ShopController;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;


public class ShopFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ShopViewModelFactory viewModelFactory;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String slug = "", campaign_slug = "", title = "";
    private String categorySlug = null;
    private int currentPage;
    private int totalCount = 0;
    private boolean isLoading = false;
    private List<String> rosterList;
    private ShopController controller;
    private boolean clickFromCategory = false;
    private ShopViewModel viewModel;
    private FragmentShopBinding binding;
    private Shop shopDetailsModel;
    private ShopDetailsModel fullShopDetailsModel;

    public ShopFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentShopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void refreshFragment() {
        NavHostFragment.findNavController(ShopFragment.this).navigate(R.id.shopFragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) {
            Toast.makeText(getContext(), "Shop not available", Toast.LENGTH_SHORT).show();
            return;
        }
        if (getArguments().containsKey("shop_name"))
            title = getArguments().getString("shop_name");
        if (getArguments().getString("campaign_slug") != null)
            campaign_slug = getArguments().getString("campaign_slug");

        slug = getArguments().getString("shop_slug");
        viewModelFactory = new ShopViewModelFactory(categorySlug, campaign_slug, slug);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(ShopViewModel.class);
        binding.swipeRefresh.setOnRefreshListener(this);

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

        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        new InitializeActionBar(view.findViewById(R.id.header_logo), getActivity(), "shop", mainViewModel);
        binding.appBarLayout.homeSearch.setOnClickListener(view12 -> {
            Bundle bundle = new Bundle();
            bundle.putString("shop_slug", slug);
            bundle.putString("shop_name", shopDetailsModel.getName());
            bundle.putString("campaign_slug", campaign_slug);
            NavHostFragment.findNavController(this).navigate(R.id.shopSearchActivity, bundle);
        });

        binding.appBarLayout.homeSearch.setEnabled(false);
        binding.appBarLayout.searchTitle.setText("Search in this shop...");

        binding.shimmer.startShimmer();
        controller = new ShopController();
        controller.setActivity((AppCompatActivity) getActivity());
        controller.setFragment(this);
        controller.setViewModel(viewModel);
        controller.setCampaignSlug(campaign_slug);
        controller.setFilterDuplicates(true);

        if (fullShopDetailsModel != null)
            controller.setAttr(fullShopDetailsModel);

        int spanCount = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        controller.setSpanCount(spanCount);

        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        binding.recyclerView.setLayoutManager(layoutManager);

        binding.recyclerView.setAdapter(controller.getAdapter());

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    int[] firstVisibleItems = null;
                    firstVisibleItems = layoutManager.findFirstVisibleItemPositions(null);
                    if (firstVisibleItems != null && firstVisibleItems.length > 0)
                        pastVisiblesItems = firstVisibleItems[0];

                    if (!isLoading && totalItemCount < totalCount)
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            controller.showEmptyPage(false, false);
                            controller.setLoadingMore(true);
                            viewModel.loadShopProducts();
                            isLoading = true;
                        }
                }
            }
        });
        viewModelLiveDataObservers();
    }

    private void viewModelLiveDataObservers() {

        viewModel.getShopCategoryListLiveData().observe(getViewLifecycleOwner(), categoryList -> {
            controller.setCategoriesLoading(false);
            if (fullShopDetailsModel == null)
                controller.addCategoryData(categoryList, false);
            else
                controller.addCategoryData(categoryList, true);
        });

        viewModel.getOnChatClickLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                setUpXmpp();
        });

        viewModel.getShopDetailsLiveData().observe(getViewLifecycleOwner(), shopDetailsModel -> loadShopDetails(shopDetailsModel));

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
            currentPage = 1;
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
                currentPage = 1;
                controller.clear();
                controller.setLoadingMore(true);
                clickFromCategory = true;
                viewModel.loadShopProducts();
            }
        });

        viewModel.getProductListLiveData().observe(getViewLifecycleOwner(), shopItems -> {
            binding.recyclerView.setVisibility(View.VISIBLE);

            List<ProductItem> tempList = new ArrayList<>();
            long timeInMill = Calendar.getInstance().getTimeInMillis();
            for (int i = 0; i < shopItems.size(); i++) {
                if (i == 0)
                    currentPage++;
                ItemsItem shopItem = shopItems.get(i);
                ProductItem item = new ProductItem();
                item.setImageUrls(shopItem.getItemImages());
                item.setSlug(shopItem.getShopItemSlug());
                item.setName(shopItem.getItemName());
                item.setMaxPrice(String.valueOf(shopItem.getItemPrice()));
                item.setMinPrice(String.valueOf(shopItem.getItemPrice()));
                item.setMinDiscountedPrice(String.valueOf(shopItem.getDiscountedPrice()));
                item.setUniqueId(item.getSlug());
                tempList.add(item);
            }

            controller.addData(tempList);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void loadShopDetails(ShopDetailsModel response) {

        fullShopDetailsModel = response;

        isLoading = false;

        Data shopData = response.getData();
        Shop shopDetails = shopData.getShop();

        totalCount = response.getCount();

        if (controller.getShopInfo() == null) {
            controller.setAttr(response);
            if (shopData.getMeta() != null)
                controller.setCashbackRate(shopData.getMeta().get("cashback_rate").getAsInt());
            shopDetailsModel = shopDetails;
        }

        binding.appBarLayout.homeSearch.setEnabled(true);

        controller.setLoadingMore(false);

        totalCount = response.getCount();

        if (totalCount == 0)
            controller.showEmptyPage(true, true);

        if (clickFromCategory) {
            binding.recyclerView.postDelayed(() -> binding.recyclerView.smoothScrollToPosition(4), 200);
            clickFromCategory = false;
        }

        binding.shimmerHolder.animate().alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        binding.shimmer.stopShimmer();
                        binding.shimmer.setVisibility(View.GONE);
                        binding.shimmerHolder.setVisibility(View.GONE);
                    }
                });
    }

    private void setUpXmpp() {

        if (shopDetailsModel == null)
            return;

        if (CredentialManager.getToken().equals("")) {
            startActivity(new Intent(getActivity(), SignInActivity.class));
            Objects.requireNonNull(getActivity()).finish();
        } else {

            Intent launchIntent = new Intent("bd.com.evaly.econnect.OPEN_MAINACTIVITY");
            try {
                if (launchIntent != null) {

                    Logger.d(new Gson().toJson(shopDetailsModel));
                    RosterTable rosterTable = new RosterTable();
                    rosterTable.name = shopDetailsModel.getName();
                    if (shopDetailsModel.getOwnerName() == null || shopDetailsModel.getOwnerName().isEmpty()){
                        rosterTable.id = shopDetailsModel.getContactNumber() +"@"+Constants.XMPP_HOST;
                    }else{
                        rosterTable.id = shopDetailsModel.getOwnerName() +"@"+Constants.XMPP_HOST;
                    }

                    rosterTable.imageUrl = shopDetailsModel.getLogoImage();
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
                }
            }catch (ActivityNotFoundException e){
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "bd.com.evaly.econnect")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "bd.com.evaly.econnect")));
                } catch (Exception e2){
                    ToastUtils.show("Please install eConnect app from Google Play Store");
                }
            }
        }
    }

    private String getContactFromRoster(String number) {
        String roasterModel = null;
        if (rosterList != null)
            for (String model : rosterList) {
                if (model.contains(number)) {
                    roasterModel = model;
                }
            }
        return roasterModel;
    }

    @Override
    public void onRefresh() {

        binding.swipeRefresh.setRefreshing(false);
        currentPage = 1;
        viewModel.setCategorySlug(null);

        binding.shimmer.setVisibility(View.VISIBLE);
        binding.shimmerHolder.setVisibility(View.VISIBLE);
        binding.shimmerHolder.setAlpha(1);
        binding.shimmer.startShimmer();

        controller.clear();
        viewModel.clearProductList();
        viewModel.setCurrentPage(1);
        viewModel.loadShopProducts();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}

