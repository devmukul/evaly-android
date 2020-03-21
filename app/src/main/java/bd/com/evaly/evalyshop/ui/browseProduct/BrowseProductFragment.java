package bd.com.evaly.evalyshop.ui.browseProduct;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.HomeHeaderItem;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.browseProduct.adapter.BrowseProductAdapter;
import bd.com.evaly.evalyshop.ui.home.adapter.HomeTabPagerAdapter;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;

public class BrowseProductFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private MainActivity activity;
    private TabLayout tabLayoutSub;
    private HomeTabPagerAdapter pager;
    private ViewPager viewPager;
    private TextView filter;
    private int type = 1;
    private String slug;
    private String category = "";
    private LinearLayout lin;
    private ShimmerFrameLayout shimmer;
    private ShimmerFrameLayout shimmerTabs;
    private boolean isShimmerShowed = false;
    private List<ProductItem> itemListProduct;
    private BrowseProductAdapter adapterProduct;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProgressBar progressBar1;
    private Context context;
    private int currentPage = 1;
    private boolean isLoading = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;

    public BrowseProductFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_browse_product_new, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        activity = (MainActivity) getActivity();
        context = getContext();

        if (getArguments() != null) {
            if (getArguments().containsKey("type"))
                type = getArguments().getInt("type");
            if (getArguments().containsKey("slug"))
                slug = getArguments().getString("slug");
            if (getArguments().containsKey("category"))
                category = getArguments().getString("category");
        } else {
            Toast.makeText(getContext(), "Can't load this page, try again later", Toast.LENGTH_SHORT).show();
        }

        recyclerView = view.findViewById(R.id.products);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar1 = view.findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.GONE);
        itemListProduct = new ArrayList<>();

        adapterProduct = new BrowseProductAdapter(getContext(), itemListProduct, activity, this, NavHostFragment.findNavController(this), slug);
        recyclerView.setAdapter(adapterProduct);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0) {
                    if (isLoading)
                        progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isLoading)
                        getProducts();
                }
            }
        });

        int spanCount = 2; // 3 columns
        int spacing = (int) Utils.convertDpToPixel(10, Objects.requireNonNull(getContext())); // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        new InitializeActionBar(view.findViewById(R.id.header_logo), getActivity(), "browse", mainViewModel);

        LinearLayout homeSearch = view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(view1 -> startActivity(new Intent(getContext(), GlobalSearchActivity.class)));

        if (!Utils.isNetworkAvailable(context))
            new NetworkErrorDialog(context, new NetworkErrorDialogListener() {
                @Override
                public void onRetry() {
                    NavHostFragment.findNavController(BrowseProductFragment.this).navigate(R.id.browseProductFragment);
                }

                @Override
                public void onBackPress() {
                    NavHostFragment.findNavController(BrowseProductFragment.this).navigate(R.id.homeFragment);
                }
            });
    }


    private void getProducts() {

        isLoading = true;

        if (currentPage == 1) {
            progressBar1.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else if (currentPage > 1) {
            progressBar1.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar1.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }

        ProductApiHelper.getCategoryBrandProducts(currentPage, slug, null, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {

                List<ProductItem> data = response.getData();

                itemListProduct.addAll(data);

                adapterProduct.notifyItemRangeInserted(itemListProduct.size() - data.size(), data.size());

                isLoading = false;
                progressBar.setVisibility(View.GONE);

                if (currentPage == 1 && response.getData().size() == 0) {
                    Toast.makeText(getContext(), "No product is available!", Toast.LENGTH_SHORT).show();
                    progressBar1.setVisibility(View.GONE);
                }

                if (response.getCount() > 10)
                    currentPage++;


            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                isLoading = false;
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(false);
        currentPage = 1;
        itemListProduct.clear();

        itemListProduct.add(new HomeHeaderItem());
        adapterProduct.notifyDataSetChanged();

        getProducts();

    }

    @Override
    public void onResume() {
        super.onResume();
        currentPage = 1;
        itemListProduct.clear();

        itemListProduct.add(new HomeHeaderItem());
        adapterProduct.notifyDataSetChanged();

        getProducts();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
        view = null;
    }

}
