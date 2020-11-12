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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.express.ExpressServiceDao;
import bd.com.evaly.evalyshop.databinding.FragmentAppBarHeaderBinding;
import bd.com.evaly.evalyshop.databinding.FragmentHomeBinding;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.recommender.RecommenderViewModel;
import bd.com.evaly.evalyshop.ui.home.controller.HomeController;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Inject
    RecommenderViewModel recommenderViewModel;

    private MainActivity activity;
    private Context context;
    private List<ProductItem> productItemList;
    private boolean isLoading = true;
    private FragmentHomeBinding binding;
    private NavController navController;
    private HomeController homeController;
    private AppDatabase appDatabase;
    private ExpressServiceDao expressServiceDao;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        context = getContext();
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        appDatabase = AppDatabase.getInstance(getActivity());

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

        expressServiceDao = appDatabase.expressServiceDao();
        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        new InitializeActionBar(view.findViewById(R.id.header_logo), getActivity(), "home", mainViewModel);
        FragmentAppBarHeaderBinding headerBinding = binding.header;

        headerBinding.homeSearch.setOnClickListener(view1 -> startActivity(new Intent(getContext(), GlobalSearchActivity.class)));

//        headerBinding.homeSearch.setOnClickListener(view1 -> {
//            navController.navigate(R.id.globalSearchFragment);
//        });

        productItemList = new ArrayList<>();

        if (homeController == null)
            homeController = new HomeController();
        homeController.setFilterDuplicates(true);
        homeController.setActivity((AppCompatActivity) getActivity());
        homeController.setFragment(this);
        homeController.setHomeViewModel(viewModel);
        binding.recyclerView.setAdapter(homeController.getAdapter());

        int spanCount = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        homeController.setSpanCount(spanCount);

        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        binding.recyclerView.setLayoutManager(layoutManager);

        // GridLayoutManager

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
            homeController.setLoadingMore(false);
            homeController.setProductData(list);
            homeController.requestModelBuild();
            isLoading = false;
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

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
