package bd.com.evaly.evalyshop.ui.brand;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentBrandBinding;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.recommender.RecommenderViewModel;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.brand.controller.BrandController;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BrandFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Inject
    RecommenderViewModel recommenderViewModel;
    long startTime = 0;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String slug = "", title = "", categoryString = "", imgUrl = "", categorySlug = "";
    private int currentPage = 1;
    private boolean isLoading = false;
    private FragmentBrandBinding binding;
    private BrandController controller;

    public BrandFragment() {

    }

    private void refreshFragment() {
        binding.getRoot().post(() -> NavHostFragment.findNavController(BrandFragment.this).navigate(R.id.brandFragment, getArguments()));
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

        if (!Utils.isNetworkAvailable(getContext()))
            new NetworkErrorDialog(getContext(), new NetworkErrorDialogListener() {
                @Override
                public void onRetry() {
                    refreshFragment();
                }

                @Override
                public void onBackPress() {
                    NavHostFragment.findNavController(BrandFragment.this).navigate(R.id.homeFragment);
                }
            });


        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        new InitializeActionBar(view.findViewById(R.id.header_logo), getActivity(), "brand", mainViewModel);

        LinearLayout homeSearch = view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), GlobalSearchActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        });

        if (getArguments() != null) {
            if (getArguments().containsKey("brand_slug"))
                slug = getArguments().getString("brand_slug");

            if (getArguments().containsKey("brand_name"))
                title = getArguments().getString("brand_name");


            if (getArguments().containsKey("category"))
                categorySlug = getArguments().getString("category");
            else
                categorySlug = "root";

            if (categorySlug == null || categorySlug.equals("root")) {
                categorySlug = "root";
                categoryString = getString(R.string.all_categories);
            } else {
                categoryString = categorySlug.replace('-', ' ');
                categoryString = Utils.capitalize(categoryString);
                categoryString = categoryString.replaceAll("\\w+$", "");
            }

            if (getArguments().containsKey("image_url"))
                imgUrl = getArguments().getString("image_url");

        } else {
            Toast.makeText(getContext(), "This page is not available!", Toast.LENGTH_SHORT).show();
        }

        controller = new BrandController();
        controller.setActivity((AppCompatActivity) getActivity());
        controller.setAttr(title, imgUrl, categoryString);

        binding.recyclerView.setAdapter(controller.getAdapter());

        int spanCount = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        controller.setSpanCount(spanCount);

        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        binding.recyclerView.setLayoutManager(layoutManager);

        controller.requestModelBuild();

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

                    if (!isLoading)
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount)
                            getProducts();
                }
            }
        });
        getProducts();
        initRecommender();
    }


    private void initRecommender() {
        recommenderViewModel.insert("brand",
                slug,
                title,
                imgUrl);
    }

    private void updateRecommender() {

        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;

        recommenderViewModel.updateSpentTime("brand",
                slug,
                diff);
    }

    private void getProducts() {

        isLoading = true;

        if (currentPage > 1)
            controller.setLoadingMore(true);

        controller.showEmptyPage(false, false);

        if (categorySlug != null && categorySlug.equals(""))
            categorySlug = null;

        ProductApiHelper.getCategoryBrandProducts(currentPage, categorySlug, slug, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {

                controller.setLoadingMore(false);

                List<ProductItem> list = response.getData();

                long timeInMill = Calendar.getInstance().getTimeInMillis();

                for (ProductItem item : list)
                    item.setUniqueId(item.getSlug() + timeInMill);

                controller.addData(list);
                isLoading = false;

                hideShimmer();

                if (response.getCount() == 0)
                    controller.showEmptyPage(true, true);

                if (response.getCount() > 10)
                    currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                hideShimmer();

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

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
