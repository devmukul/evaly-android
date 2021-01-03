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
import bd.com.evaly.evalyshop.recommender.RecommenderViewModel;
import bd.com.evaly.evalyshop.ui.home.controller.HomeController;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

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
}
