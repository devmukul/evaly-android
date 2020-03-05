package bd.com.evaly.evalyshop.ui.brand;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.HomeHeaderItem;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.brand.adapter.BrandProductAdapter;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;

public class BrandFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private String slug = "", title = "", categoryString = "", imgUrl = "", categorySlug = "";
    private View view;
    private Context context;
    private MainActivity mainActivity;
    private ProgressBar progressBar;
    private View dummyView;
    private List<ProductItem> itemListProduct;
    private BrandProductAdapter adapterProduct;
    private RecyclerView recyclerView;
    private int currentPage = 1;
    private boolean isLoading = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FrameLayout shimmerHolder;

    public BrandFragment() {

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_brand_new, container, false);

        context = getContext();
        mainActivity = (MainActivity) getActivity();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        swipeRefreshLayout.setOnRefreshListener(this);

        if (!Utils.isNetworkAvailable(context))
            new NetworkErrorDialog(context, new NetworkErrorDialogListener() {
                @Override
                public void onRetry() {
                    refreshFragment();
                }

                @Override
                public void onBackPress() {
                    if (getFragmentManager() != null)
                        NavHostFragment.findNavController(BrandFragment.this).navigate(R.id.homeFragment);
                }
            });

        new InitializeActionBar(view.findViewById(R.id.header_logo), mainActivity, "brand");

        LinearLayout homeSearch = view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), GlobalSearchActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        });

        slug = getArguments().getString("brand_slug");
        title = getArguments().getString("brand_name");
        categorySlug = getArguments().getString("category");

        if (categorySlug.equals("root"))
            categoryString = getString(R.string.all_categories);

        else {
            categoryString = categorySlug.replace('-', ' ');
            categoryString = Utils.capitalize(categoryString);
            categoryString = categoryString.replaceAll("\\w+$", "");
        }

        imgUrl = getArguments().getString("image_url");


        dummyView = view.findViewById(R.id.dummyView);
        recyclerView = view.findViewById(R.id.products);
        progressBar = view.findViewById(R.id.progressBar);

        shimmerFrameLayout = view.findViewById(R.id.shimmer);
        shimmerHolder = view.findViewById(R.id.shimmerHolder);
        shimmerFrameLayout.startShimmer();

        itemListProduct = new ArrayList<>();

        HashMap<String, String> data = new HashMap<>();
        data.put("slug", slug);
        data.put("title", title);
        data.put("categorySlug", categorySlug);
        data.put("categoryString", categoryString);
        data.put("imgUrl", imgUrl);

        adapterProduct = new BrandProductAdapter(getContext(), itemListProduct, mainActivity, this, NavHostFragment.findNavController(this), data);
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


    }


    private void getProducts() {

        isLoading = true;

        if (currentPage > 1)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);

        ProductApiHelper.getCategoryBrandProducts(currentPage, null, slug, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {

                List<ProductItem> data = response.getData();

                itemListProduct.addAll(data);
                adapterProduct.notifyItemRangeInserted(itemListProduct.size() - data.size(), data.size());
                isLoading = false;
                progressBar.setVisibility(View.INVISIBLE);


                dummyView.setVisibility(View.GONE);

                if (response.getCount() == 0) {
                    LinearLayout noItem = view.findViewById(R.id.noItem);
                    noItem.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

                if (currentPage == 1){
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerHolder.setVisibility(View.GONE);
                }


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
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(false);
        currentPage = 1;
        itemListProduct.clear();
        itemListProduct.add(new HomeHeaderItem());
        adapterProduct.notifyDataSetChanged();

        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerHolder.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();

        getProducts();

    }
}
