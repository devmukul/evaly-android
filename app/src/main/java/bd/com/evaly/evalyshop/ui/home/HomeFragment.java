package bd.com.evaly.evalyshop.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.epoxy.EpoxyController;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Random;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentHomeBinding;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.campaign.brand.CampaignBrandResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.models.campaign.shop.CampaignShopResponse;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.recommender.RecommenderViewModel;
import bd.com.evaly.evalyshop.ui.home.controller.HomeController;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.FixedStaggeredGridLayoutManager;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, HomeController.ClickListener {

    @Inject
    RecommenderViewModel recommenderViewModel;

    @Inject
    FirebaseRemoteConfig firebaseRemoteConfig;

    private boolean isLoading = true;
    private FragmentHomeBinding binding;
    private NavController navController;
    private HomeController homeController;
    private HomeViewModel viewModel;
    private MainViewModel mainViewModel;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeController.cancelPendingModelBuild();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        networkCheck();
        navController = NavHostFragment.findNavController(this);
        binding.swipeRefresh.setOnRefreshListener(this);

        initAppHeader();
        setupRecycler();
        liveEventObservers();
    }

    private void networkCheck() {
        if (getContext() != null && !Utils.isNetworkAvailable(getContext()))
            new NetworkErrorDialog(getContext(), new NetworkErrorDialogListener() {
                @Override
                public void onRetry() {
                    viewModel.reload();
                }

                @Override
                public void onBackPress() {
                    navController.navigate(R.id.homeFragment);
                }
            });
    }

    private void initAppHeader() {
        new InitializeActionBar(binding.header.headerLogo, getActivity(), "home", mainViewModel);
        binding.header.homeSearch.setOnClickListener(view1 -> startActivity(new Intent(getContext(), GlobalSearchActivity.class)));
    }

    @Override
    public void onRefresh() {
        binding.swipeRefresh.setRefreshing(false);
        viewModel.reload();
    }

    private void setupRecycler() {

        if (homeController == null || homeController.getAdapter() == null)
            homeController = new HomeController();

        homeController.setFilterDuplicates(true);
        homeController.setActivity((AppCompatActivity) getActivity());
        homeController.setClickListener(this);
        homeController.setFragment(this);
        homeController.setHomeViewModel(viewModel);
        homeController.setSpanCount(2);
        homeController.setCycloneOngoing(firebaseRemoteConfig.getBoolean("cyclone_ongoing"));
        homeController.setCycloneBanner(firebaseRemoteConfig.getString("cyclone_banner"));

        HandlerThread handlerThread = new HandlerThread("epoxy");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        EpoxyController.defaultDiffingHandler = handler;
        EpoxyController.defaultModelBuildingHandler = handler;

        binding.recyclerView.setAdapter(homeController.getAdapter());

        FixedStaggeredGridLayoutManager layoutManager = new FixedStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        binding.recyclerView.addItemDecoration(new StaggeredSpacingItemDecoration(2, spacing, true));
        binding.recyclerView.setLayoutManager(layoutManager);


        if (!binding.recyclerView.isComputingLayout())
            homeController.requestModelBuild();

        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    viewModel.loadProducts();
                    isLoading = true;
                    homeController.setLoadingMore(true);
                    homeController.requestModelBuild();
                }
            }
        });

    }

    private void requestModelBuild() {
        if (!binding.recyclerView.isComputingLayout())
            homeController.requestModelBuild();
        //if (!binding.recyclerView.isComputingLayout() && !homeController.hasPendingModelBuild())
        //homeController.requestDelayedModelBuild(randInt(100, 200));
    }

    private void liveEventObservers() {

        recommenderViewModel.getRsBrandLiveData().observe(getViewLifecycleOwner(), rsEntities -> {
            homeController.setRsBrandList(rsEntities);
            if (rsEntities.size() > 0)
                requestModelBuild();
        });

        recommenderViewModel.getRsCategoryLiveData().observe(getViewLifecycleOwner(), rsEntities -> {
            homeController.setRsCategoryList(rsEntities);
            if (rsEntities.size() > 0)
                requestModelBuild();
        });

//        recommenderViewModel.getRsShopLiveData().observe(getViewLifecycleOwner(), rsEntities -> {
//            homeController.setRsShopList(rsEntities);
//            if (rsEntities.size() > 0)
//                requestModelBuild();
//        });

        viewModel.getProductListLive().observe(getViewLifecycleOwner(), list -> {
            isLoading = false;
            homeController.setLoadingMore(false);
            homeController.setProductData(list);
            requestModelBuild();
        });

        viewModel.getExpressListLive().observe(getViewLifecycleOwner(), expressServiceModels -> {
            homeController.setExpressLoading(false);
            homeController.setExpressData(expressServiceModels);
            requestModelBuild();
        });

        viewModel.getCampaignCategoryLiveList().observe(getViewLifecycleOwner(), campaignCategoryResponses -> {
            homeController.setCampaignLoading(false);
            homeController.setCampaignCategoryList(campaignCategoryResponses);
            requestModelBuild();
        });

        viewModel.getFlashSaleProductList().observe(getViewLifecycleOwner(), campaignProductResponses -> {
            homeController.setFlashSaleProducts(campaignProductResponses);
            requestModelBuild();
        });

        viewModel.flashSaleBrandList.observe(getViewLifecycleOwner(), campaignBrandResponses -> {
            homeController.setFlashSaleBrands(campaignBrandResponses);
            requestModelBuild();
        });

        viewModel.flashSaleShopList.observe(getViewLifecycleOwner(), campaignShopResponses -> {
            homeController.setFlashSaleShops(campaignShopResponses);
            requestModelBuild();
        });

        viewModel.getBannerListLive().observe(getViewLifecycleOwner(), bannerItems -> {
            homeController.setBannerList(bannerItems);
            requestModelBuild();
        });

    }

    @Override
    public void onProductClick(ProductItem item) {
        Intent intent = new Intent(getContext(), ViewProductActivity.class);
        intent.putExtra("product_slug", item.getSlug());
        intent.putExtra("product_name", item.getName());
        intent.putExtra("product_price", item.getMaxPrice());
        if (item.getImageUrls() != null && item.getImageUrls().size() > 0)
            intent.putExtra("product_image", item.getImageUrls().get(0));
        getContext().startActivity(intent);
    }

    @Override
    public void onCategoryClick(String slug, String category) {
        Bundle bundle = new Bundle();
        bundle.putString("slug", slug);
        bundle.putString("category_slug", category);
        navController.navigate(R.id.browseProductFragment, bundle);
    }

    @Override
    public void onBrandClick(String brand_slug, String brand_name, String image_url) {
        Bundle bundle = new Bundle();
        bundle.putString("brand_slug", brand_slug);
        bundle.putString("brand_name", brand_name);
        bundle.putString("image_url", image_url);
        navController.navigate(R.id.brandFragment, bundle);
    }

    @Override
    public void onShopClick(String shop_slug, String shop_name) {
        Bundle bundle = new Bundle();
        bundle.putString("shop_slug", shop_slug);
        bundle.putString("shop_name", shop_name);
        navController.navigate(R.id.shopFragment, bundle);
    }

    @Override
    public void onCampaignCategoryClick(CampaignCategoryResponse model) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", model);
        navController.navigate(R.id.campaignDetails, bundle);
    }

    @Override
    public void onCampaignProductClick(CampaignProductResponse item1) {
        Intent intent = new Intent(getContext(), ViewProductActivity.class);
        intent.putExtra("product_slug", item1.getSlug());
        intent.putExtra("product_name", item1.getName());
        intent.putExtra("product_price", item1.getPrice());
        if (item1.getShopSlug() != null)
            intent.putExtra("shop_slug", item1.getShopSlug());
        intent.putExtra("product_image", item1.getImage());
        intent.putExtra("cashback_text", item1.getCashbackText());
        getContext().startActivity(intent);
    }

    @Override
    public void onFlashSaleClick(String slug) {
        Bundle bundle = new Bundle();
        bundle.putString("category_slug", slug);
        navController.navigate(R.id.campaignDetails);
    }

    private void openEfoodApp() {
        try {
            Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("bd.com.evaly.efood");
            if (launchIntent != null) {
                startActivity(launchIntent);
            } else
                openPlaystore();
        } catch (android.content.ActivityNotFoundException e) {
            openPlaystore();
        } catch (Exception ee) {
            ToastUtils.show("Couldn't open eFood, please install from Google Playstore");
        }
    }

    private void openPlaystore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "bd.com.evaly.efood")));
        } catch (android.content.ActivityNotFoundException anfe) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "bd.com.evaly.efood")));
            } catch (Exception e3) {
                ToastUtils.show("Couldn't open eFood App, please install from Google Playstore");
            }
        }
    }

    @Override
    public void onExpressClick(ExpressServiceModel model) {
        if (model.getSlug().equals("express-bullet-food")) {
            openEfoodApp();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", model);
        navController.navigate(R.id.evalyExpressFragment, bundle);
    }

    @Override
    public void onShowMoreCategoryClick() {
        navController.navigate(R.id.categoryFragment);
    }

    @Override
    public void onShowMoreShopClick() {
        Intent intent = new Intent(getContext(), GlobalSearchActivity.class);
        intent.putExtra("type", 3);
        getContext().startActivity(intent);
    }

    @Override
    public void onShowMoreBrandClick() {
        Intent intent = new Intent(getContext(), GlobalSearchActivity.class);
        intent.putExtra("type", 2);
        getContext().startActivity(intent);
    }

    @Override
    public void onShowMoreExpressClick() {
        navController.navigate(R.id.expressProductSearchFragment);
    }

    @Override
    public void onShowMoreCampaignClick() {
        navController.navigate(R.id.campaignFragment);
    }

    @Override
    public void navigateToUrl(String url) {
        try {
            navController.navigate(Uri.parse(url));
        } catch (Exception e) {
            ToastUtils.show("Show not found");
        }
    }

    @Override
    public void onCampaignBrandClick(CampaignBrandResponse model) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("shop_name", model.getName());
        bundle.putString("logo_image", model.getImage());
        bundle.putString("shop_slug", model.getShopSlug());
        bundle.putString("category", "root");
        bundle.putString("brand_slug", model.getSlug());
        bundle.putString("campaign_slug", model.getCampaignSlug());
        navController.navigate(R.id.shopFragment, bundle);
    }

    @Override
    public void onCampaignShopClick(CampaignShopResponse model) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 2);
        bundle.putString("shop_name", model.getName());
        bundle.putString("logo_image", model.getImage());
        bundle.putString("shop_slug", model.getSlug());
        bundle.putString("category", "root");
        bundle.putString("campaign_slug", model.getCampaignSlug());
        navController.navigate(R.id.shopFragment, bundle);
    }
}
