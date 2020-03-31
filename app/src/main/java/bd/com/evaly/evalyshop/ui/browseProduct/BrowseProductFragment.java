package bd.com.evaly.evalyshop.ui.browseProduct;

import android.content.Intent;
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

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.FragmentAppBarHeaderBinding;
import bd.com.evaly.evalyshop.databinding.FragmentBrowseProductBinding;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.browseProduct.controller.BrowseProductController;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;

public class BrowseProductFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String slug;
    private String category = "";
    private int currentPage = 1;
    private boolean isLoading = false;
    private BrowseProductController controller;
    private FragmentBrowseProductBinding binding;

    public BrowseProductFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBrowseProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        new InitializeActionBar(view.findViewById(R.id.header_logo), getActivity(), "browse", mainViewModel);

        FragmentAppBarHeaderBinding headerBinding = binding.header;
        headerBinding.homeSearch.setOnClickListener(view1 -> startActivity(new Intent(getContext(), GlobalSearchActivity.class)));

        if (!Utils.isNetworkAvailable(getContext()))
            new NetworkErrorDialog(getContext(), new NetworkErrorDialogListener() {
                @Override
                public void onRetry() {
                    NavHostFragment.findNavController(BrowseProductFragment.this).navigate(R.id.browseProductFragment);
                }

                @Override
                public void onBackPress() {
                    NavHostFragment.findNavController(BrowseProductFragment.this).navigate(R.id.homeFragment);
                }
            });


        binding.swipeRefresh.setOnRefreshListener(this);

        if (getArguments() != null) {
            if (getArguments().containsKey("slug"))
                slug = getArguments().getString("slug");
            if (getArguments().containsKey("category"))
                category = getArguments().getString("category");
        } else {
            Toast.makeText(getContext(), "Can't load this page, try again later", Toast.LENGTH_SHORT).show();
        }

        controller = new BrowseProductController();
        controller.setActivity((AppCompatActivity) getActivity());
        controller.setFragment(this);
        controller.setCategorySlug(slug);

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

    }


    private void getProducts() {

        isLoading = true;
        controller.setLoadingMore(true);

        ProductApiHelper.getCategoryBrandProducts(currentPage, slug, null, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {
                controller.setLoadingMore(false);
                controller.addData(response.getData());
                isLoading = false;

                if (currentPage == 1 && response.getData().size() == 0)
                    Toast.makeText(AppController.getmContext(), "No product is available!", Toast.LENGTH_SHORT).show();

                if (response.getCount() > 10)
                    currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                isLoading = false;
                controller.setLoadingMore(false);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    @Override
    public void onRefresh() {
        binding.swipeRefresh.setRefreshing(false);
        currentPage = 1;
        controller.clear();
        getProducts();
    }

    @Override
    public void onResume() {
        super.onResume();
        currentPage = 1;
        controller.clear();
        getProducts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding.recyclerView.setAdapter(null);
        binding = null;
    }

}
