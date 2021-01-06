package bd.com.evaly.evalyshop.ui.brand;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentBrandBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.recommender.RecommenderViewModel;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.ui.brand.controller.BrandController;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.StaggeredSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BrandFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Inject
    RecommenderViewModel recommenderViewModel;
    long startTime = 0;
    private BrandViewModel viewModel;
    private boolean isLoading = false;
    private FragmentBrandBinding binding;
    private BrandController controller;
    private boolean clickFromCategory = false;

    public BrandFragment() {

    }

    private void refreshFragment() {
        binding.getRoot().post(() -> NavHostFragment.findNavController(BrandFragment.this).navigate(R.id.brandFragment, getArguments()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BrandViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBrandBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startTime = System.currentTimeMillis();
        binding.swipeRefresh.setOnRefreshListener(this);

        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        new InitializeActionBar(view.findViewById(R.id.header_logo), getActivity(), "brand", mainViewModel);

        LinearLayout homeSearch = view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), GlobalSearchActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        });

        setupRecycler();
        liveEvents();
    }

    private void liveEvents() {

        viewModel.getSelectedCategoryLiveData().observe(getViewLifecycleOwner(), tabsItem -> {
            ApiClient.getUnsafeOkHttpClient().dispatcher().cancelAll();
            viewModel.clearProductList();
            clickFromCategory = true;
            String categorySlug = tabsItem.getSlug();
            viewModel.setCategorySlug(categorySlug);
            viewModel.setCurrentPage(1);
            controller.setCategoryTitle(tabsItem.getTitle());
            controller.clear();
            controller.setLoadingMore(true);
            viewModel.getProducts();
            // binding.appBarLayout.appBarLayout.setExpanded(false, true);
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
                viewModel.getProducts();
            }
        });


        viewModel.categoryListLiveData.observe(getViewLifecycleOwner(), tabsItems -> {
            controller.setCategoriesLoading(false);
            controller.setCategoryItems(tabsItems);
            controller.requestModelBuild();
        });

        viewModel.detailsLive.observe(getViewLifecycleOwner(), model -> {
            controller.setAttr(model.getName(), model.getImageUrl(), "root");
            controller.requestModelBuild();
            recommenderViewModel.insert("brand",
                    model.getSlug(),
                    model.getName(),
                    model.getImageUrl());
        });

        viewModel.liveList.observe(getViewLifecycleOwner(), productItems -> {
            isLoading = false;
            hideShimmer();
            controller.addData(productItems);
            controller.setLoadingMore(false);
            controller.requestModelBuild();
        });
    }

    private void setupRecycler() {

        controller = new BrandController();
        controller.setFilterDuplicates(true);
        controller.setActivity((AppCompatActivity) getActivity());

        binding.recyclerView.setAdapter(controller.getAdapter());

        int spanCount = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        controller.setSpanCount(spanCount);

        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        binding.recyclerView.addItemDecoration(new StaggeredSpacingItemDecoration(spanCount, spacing, true));
        binding.recyclerView.setLayoutManager(layoutManager);

        controller.requestModelBuild();

        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    viewModel.getProducts();
                    isLoading = true;
                }
            }
        });

    }

    private void updateRecommender() {
        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        recommenderViewModel.updateSpentTime("brand",
                viewModel.getSlug(),
                diff);
    }

    private void hideShimmer() {
        binding.shimmerHolder.animate().alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        binding.shimmer.stopShimmer();
                        binding.shimmerHolder.setVisibility(View.GONE);
                        binding.shimmer.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onRefresh() {
        refreshFragment();
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        updateRecommender();
    }
}
