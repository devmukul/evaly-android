package bd.com.evaly.evalyshop.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentAppBarHeaderBinding;
import bd.com.evaly.evalyshop.databinding.FragmentHomeBinding;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.recommender.RecommenderViewModel;
import bd.com.evaly.evalyshop.ui.home.controller.HomeController;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, HomeController.ClickListener {

    @Inject
    RecommenderViewModel recommenderViewModel;

    private MainActivity activity;
    private Context context;
    private boolean isLoading = true;
    private FragmentHomeBinding binding;
    private NavController navController;
    private HomeController homeController;
    private HomeViewModel viewModel;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onRefresh() {
        binding.swipeRefresh.setRefreshing(false);
        refreshFragment();
    }

    private void refreshFragment() {
        navController.navigate(HomeFragmentDirections.actionHomeFragmentPop());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        context = getContext();
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding.swipeRefresh.setOnRefreshListener(this);
        return binding.getRoot();
    }

    private void networkCheck() {
        if (!Utils.isNetworkAvailable(context))
            new NetworkErrorDialog(context, new NetworkErrorDialogListener() {
                @Override
                public void onRetry() {
                    refreshFragment();
                }

                @Override
                public void onBackPress() {
                    navController.navigate(R.id.homeFragment);
                }
            });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        networkCheck();

        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        new InitializeActionBar(view.findViewById(R.id.header_logo), getActivity(), "home", mainViewModel);
        FragmentAppBarHeaderBinding headerBinding = binding.header;

        headerBinding.homeSearch.setOnClickListener(view1 -> startActivity(new Intent(getContext(), GlobalSearchActivity.class)));

//        headerBinding.homeSearch.setOnClickListener(view1 -> {
//            navController.navigate(R.id.globalSearchFragment);
//        });

        boolean homeControllerInitialized = false;
        if (homeController == null) {
            homeController = new HomeController();
            homeControllerInitialized = true;
        }

        homeController.setFilterDuplicates(true);
        homeController.setActivity((AppCompatActivity) getActivity());
        homeController.setClickListener(this);
        homeController.setFragment(this);
        homeController.setHomeViewModel(viewModel);
        binding.recyclerView.setAdapter(homeController.getAdapter());

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        binding.recyclerView.addItemDecoration(new StaggeredSpacingItemDecoration(2, spacing, true));
        binding.recyclerView.setLayoutManager(layoutManager);

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

        if (homeControllerInitialized)
            homeController.requestModelBuild();

        liveEventObservers();
    }

    private void liveEventObservers() {

        recommenderViewModel.getRsBrandLiveData().observe(getViewLifecycleOwner(), rsEntities -> {
            homeController.setRsBrandList(rsEntities);
            homeController.requestModelBuild();
        });

        recommenderViewModel.getRsCategoryLiveData().observe(getViewLifecycleOwner(), rsEntities -> {
            homeController.setRsCategoryList(rsEntities);
            homeController.requestModelBuild();
        });

        recommenderViewModel.getRsShopLiveData().observe(getViewLifecycleOwner(), rsEntities -> {
            homeController.setRsShopList(rsEntities);
            homeController.requestModelBuild();
        });

        viewModel.getProductListLive().observe(getViewLifecycleOwner(), list -> {
            isLoading = false;
            homeController.setLoadingMore(false);
            homeController.setProductData(list);
            homeController.requestModelBuild();
        });

        viewModel.getExpressListLive().observe(getViewLifecycleOwner(), expressServiceModels -> {
            homeController.setExpressLoading(false);
            homeController.setExpressData(expressServiceModels);
            homeController.requestModelBuild();
        });

        viewModel.getCampaignCategoryLiveList().observe(getViewLifecycleOwner(), campaignCategoryResponses -> {
            homeController.setCampaignLoading(false);
            homeController.setCampaignCategoryList(campaignCategoryResponses);
            homeController.requestModelBuild();
        });

        viewModel.getFlashSaleProductList().observe(getViewLifecycleOwner(), campaignProductResponses -> {
            homeController.setFlashSaleProducts(campaignProductResponses);
            homeController.requestModelBuild();
        });

        viewModel.getBannerListLive().observe(getViewLifecycleOwner(), bannerItems -> {
            homeController.setBannerList(bannerItems);
            homeController.requestModelBuild();
        });

    }

    @Override
    public void onProductClick(ProductItem item) {
        Intent intent = new Intent(activity, ViewProductActivity.class);
        intent.putExtra("product_slug", item.getSlug());
        intent.putExtra("product_name", item.getName());
        intent.putExtra("product_price", item.getMaxPrice());
        if (item.getImageUrls().size() > 0)
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
        Intent intent = new Intent(activity, ViewProductActivity.class);
        intent.putExtra("product_slug", item1.getSlug());
        intent.putExtra("product_name", item1.getName());
        intent.putExtra("product_price", item1.getPrice());
        if (item1.getShopSlug() != null)
            intent.putExtra("shop_slug", item1.getShopSlug());
        intent.putExtra("product_image", item1.getImage());
        intent.putExtra("cashback_text", item1.getCashbackText());
        activity.startActivity(intent);
    }

    @Override
    public void onFlashSaleClick(String slug) {
        Bundle bundle = new Bundle();
        bundle.putString("category_slug", slug);
        navController.navigate(R.id.campaignDetails);
    }

    @Override
    public void onExpressClick(ExpressServiceModel model) {
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
}
