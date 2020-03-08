package bd.com.evaly.evalyshop.ui.browseProduct;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

        Bundle bundle = new Bundle();
        type = getArguments().getInt("type");
        slug = getArguments().getString("slug");
        category = getArguments().getString("category");
        bundle.putString("category", category);

        recyclerView = view.findViewById(R.id.products);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        itemListProduct = new ArrayList<>();
        adapterProduct = new BrowseProductAdapter(getContext(), itemListProduct, activity, this, NavHostFragment.findNavController(this), slug);
        recyclerView.setAdapter(adapterProduct);

        itemListProduct.add(new HomeHeaderItem());
        adapterProduct.notifyItemInserted(0);

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

        getProducts();

        return view;

    }

    private void refreshFragment() {
        if (getFragmentManager() != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getFragmentManager().beginTransaction().detach(this).commitNow();
                getFragmentManager().beginTransaction().attach(this).commitNow();
            } else {
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new InitializeActionBar(view.findViewById(R.id.header_logo), activity, "browse");

        LinearLayout homeSearch = view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(view1 -> startActivity(new Intent(getContext(), GlobalSearchActivity.class)));

        if (!Utils.isNetworkAvailable(context))
            new NetworkErrorDialog(context, new NetworkErrorDialogListener() {
                @Override
                public void onRetry() {
                    refreshFragment();
                }

                @Override
                public void onBackPress() {
                    if (getFragmentManager() != null)
                        NavHostFragment.findNavController(BrowseProductFragment.this).navigate(R.id.homeFragment);
                }
            });


//        if (nestedSV != null) {
//
//            nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
//                    try {
//                        progressBar.setVisibility(View.VISIBLE);
//                        productGrid.loadNextPage();
//                    } catch (Exception e) {
//                        Log.e("load more product", e.toString());
//                    }
//                }
//            });
//        }
    }


    private void getProducts() {

        isLoading = true;

        if (currentPage > 1)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);

        ProductApiHelper.getCategoryBrandProducts(currentPage, slug, null, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {

                List<ProductItem> data = response.getData();

                itemListProduct.addAll(data);
                adapterProduct.notifyItemRangeInserted(itemListProduct.size() - data.size(), data.size());
                isLoading = false;
                progressBar.setVisibility(View.GONE);

                if (response.getCount() > 10)
                    currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(false);
        currentPage = 1;
        itemListProduct.clear();

        itemListProduct.add(new HomeHeaderItem());
        adapterProduct.notifyItemInserted(0);

        adapterProduct.notifyDataSetChanged();

        getProducts();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

}
