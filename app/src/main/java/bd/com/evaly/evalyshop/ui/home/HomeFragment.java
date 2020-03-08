package bd.com.evaly.evalyshop.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.pref.ReferPref;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.HomeHeaderItem;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.home.adapter.HomeProductGridAdapter;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private MainActivity activity;
    private View view;
    private UserDetails userDetails;
    private Context context;
    private SwipeRefreshLayout swipeLayout;
    private int currentPage = 1;
    private List<ProductItem> productItemList;
    private HomeProductGridAdapter adapterProducts;
    private RecyclerView productRecyclerView;
    private boolean isLoading = false;
    private LinearLayout homeSearch;
    private ProgressBar progressBar;
    private boolean loading = true;
    private ReferPref referPref;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(false);
        refreshFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        activity = (MainActivity) getActivity();
        context = getContext();

        swipeLayout = view.findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);

        return view;
    }


    private void refreshFragment() {
        NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.homeFragment);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!Utils.isNetworkAvailable(context))
            new NetworkErrorDialog(context, new NetworkErrorDialogListener() {
                @Override
                public void onRetry() {
                    refreshFragment();
                }

                @Override
                public void onBackPress() {
                        NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.homeFragment);
                }
            });


        userDetails = new UserDetails(context);
        referPref = new ReferPref(context);
        new InitializeActionBar(view.findViewById(R.id.header_logo), activity, "home");

        homeSearch = view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(view1 -> startActivity(new Intent(getContext(), GlobalSearchActivity.class)));
        progressBar = view.findViewById(R.id.progressBar);

        productItemList = new ArrayList<>();
        productRecyclerView = view.findViewById(R.id.products);
        productRecyclerView.setHasFixedSize(false);

        StaggeredGridLayoutManager mLayoutManager;
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        productRecyclerView.setLayoutManager(mLayoutManager);

        adapterProducts = new HomeProductGridAdapter(getContext(), productItemList, activity, this, NavHostFragment.findNavController(this));
        productRecyclerView.setAdapter(adapterProducts);


        productItemList.add(new HomeHeaderItem());
        adapterProducts.notifyItemInserted(0);

        productRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0) {
                    if (isLoading)
                        progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        productRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    int[] firstVisibleItems = null;
                    firstVisibleItems = mLayoutManager.findFirstVisibleItemPositions(null);
                    if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                        pastVisiblesItems = firstVisibleItems[0];
                    }

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            getProducts();
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (currentPage > 1)
                        progressBar.setVisibility(View.VISIBLE);
                    else
                        progressBar.setVisibility(View.GONE);
                }
            }
        });


        int spanCount = 2; // 3 columns
        int spacing = (int) Utils.convertDpToPixel(10, Objects.requireNonNull(getContext())); // 50px
        boolean includeEdge = true;
        productRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        currentPage = 1;

        checkReferral();
        getProducts();

    }


    private void getProducts() {

        isLoading = true;


        ProductApiHelper.getCategoryBrandProducts(currentPage, "root", null, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {

                List<ProductItem> data = response.getData();

                productItemList.addAll(data);
                adapterProducts.notifyItemRangeInserted(productItemList.size() - data.size(), data.size());
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


    private void checkReferral() {

        if (!referPref.getRef().equals("")) {

            HashMap<String, String> params = new HashMap<>();

            params.put("token", userDetails.getToken().trim());
            params.put("referred_by", referPref.getRef().trim());
            params.put("device_id", Utils.getDeviceID(context).trim());

            GeneralApiHelper.checkReferral(params, new ResponseListenerAuth<JsonObject, String>() {
                @Override
                public void onDataFetched(JsonObject response, int statusCode) {

                    String message = response.get("message").getAsString();

                    if (!message.equals("")) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        referPref.setRef("");
                    }
                }

                @Override
                public void onFailed(String errorBody, int errorCode) {
                    Toast.makeText(context, "Server error occurred, couldn't verify invitation code.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onAuthError(boolean logout) {

                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }


}
