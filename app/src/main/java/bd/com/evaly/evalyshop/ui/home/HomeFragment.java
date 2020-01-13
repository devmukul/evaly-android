package bd.com.evaly.evalyshop.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.home.adapter.HomeHeaderItem;
import bd.com.evaly.evalyshop.ui.home.adapter.HomeProductGridAdapter;
import bd.com.evaly.evalyshop.ui.home.adapter.HomeTabPagerAdapter;
import bd.com.evaly.evalyshop.ui.home.adapter.SliderAdapter;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.SliderViewPager;
import retrofit2.Response;

public class HomeFragment extends Fragment  {

    private MainActivity activity;
    private HomeTabPagerAdapter pager;
    private SliderViewPager sliderPager;
    private TabLayout sliderIndicator;
    private List<BannerItem> sliderImages;
    private LinearLayout homeSearch, evalyStore;
    private TabLayout tabLayout;
    private LinearLayout voucher;
    private ShimmerFrameLayout shimmer;
    private boolean isShimmerShowed = false;
    private View view;
    private String defaultCategory = "root";
    private UserDetails userDetails;
    private Context context;
    private SwipeRefreshLayout swipeLayout;
    private int currentPage = 1;
    private List<ProductItem> productItemList;
    private HomeProductGridAdapter adapterProducts;
    private RecyclerView productRecyclerView;
    private NestedScrollView nestedSV;
    private boolean isLoading = false;
    private ProgressBar progressBar;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;


    public HomeFragment() {
        // Required empty public constructor
    }



    // implements SwipeRefreshLayout.OnRefreshListener

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

//    @Override
//    public void onRefresh() {
//        swipeLayout.setRefreshing(false);
//        refreshFragment();
//    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        activity = (MainActivity) getActivity();
        context = getContext();

      //  swipeLayout = view.findViewById(R.id.swipe_refresh);
      //  swipeLayout.setOnRefreshListener(this);

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

                    if (getFragmentManager() != null)
                        NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.homeFragment);

                }
            });


//        homeSearch.setOnClickListener(view1 -> {
//            Intent intent = new Intent(context, GlobalSearchActivity.class);
//            intent.putExtra("type", 1);
//            startActivity(intent);
//        });


        userDetails = new UserDetails(context);
        new InitializeActionBar(view.findViewById(R.id.header_logo), activity, "home");


        homeSearch = view.findViewById(R.id.home_search);
        progressBar = view.findViewById(R.id.progressBar);


//
//        tabLayout = view.findViewById(R.id.tab_layout);
//        tabLayout.setTabMode(TabLayout.MODE_FIXED);
//        tabLayout.setSmoothScrollingEnabled(true);
//        voucher = view.findViewById(R.id.voucher);



//        final ViewPager viewPager = view.findViewById(R.id.pager);
//        pager = new HomeTabPagerAdapter(getChildFragmentManager());
//
//        shimmer = view.findViewById(R.id.shimmer);
//        try {
//            shimmer.setVisibility(View.VISIBLE);
//            shimmer.startShimmer();
//        } catch (Exception e) {
//
//        }
//
//        tabLayout.setupWithViewPager(viewPager);
//        viewPager.setAdapter(pager);
//        tabLayout.setupWithViewPager(viewPager);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        viewPager.setOffscreenPageLimit(1);
//
//
//        evalyStore = view.findViewById(R.id.evaly_store);
//        evalyStore.setOnClickListener(v -> {
//            Intent ni = new Intent(context, GiftCardActivity.class);
//            startActivity(ni);
//        });
//
//        voucher.setOnClickListener(v -> {
//            CampaignBottomSheetFragment campaignBottomSheetFragment = CampaignBottomSheetFragment.newInstance();
//            if (getFragmentManager() != null)
//                campaignBottomSheetFragment.show(getFragmentManager(), "Campaign BottomSheet");
//        });
//
//
//        LinearLayout wholesale = view.findViewById(R.id.evaly_wholesale);
//        wholesale.setOnClickListener(v -> startActivity(new Intent(context, NewsfeedActivity.class)));


//        LinearLayout orders = view.findViewById(R.id.orders);
//        orders.setOnClickListener(v -> {
//
//            if (userDetails.getToken().equals("")) {
//                startActivity(new Intent(context, SignInActivity.class));
//            } else {
//                startActivity(new Intent(context, OrderListActivity.class));
//            }
//        });

//
//        nestedSV = view.findViewById(R.id.stickyScrollView);

//        // slider
//        sliderPager = view.findViewById(R.id.sliderPager);
//        sliderIndicator = view.findViewById(R.id.sliderIndicator);

        sliderImages = new ArrayList<>();
      //  getSliderImage();


        productItemList = new ArrayList<>();
        productRecyclerView = view.findViewById(R.id.products);
        // productRecyclerView.setNestedScrollingEnabled(false);
        productRecyclerView.setHasFixedSize(false);
        adapterProducts = new HomeProductGridAdapter(getContext(), productItemList, activity,this);
       // adapterProducts.setHasStableIds(true);
        productRecyclerView.setAdapter(adapterProducts);


        productRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 1);

        StaggeredGridLayoutManager mLayoutManager =
                (StaggeredGridLayoutManager) productRecyclerView.getLayoutManager();

        productItemList.add(new HomeHeaderItem());

        productRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    int[] firstVisibleItems = mLayoutManager.findFirstVisibleItemPositions(null);
                    if (firstVisibleItems != null && firstVisibleItems.length > 0)
                        pastVisiblesItems = firstVisibleItems[0];

                    if (!isLoading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//                            (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
                            getProducts();
                        }
                    } }
            }
        });



        try {
            checkReferral();
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }



        getProducts();


    }


    private void getProducts() {

        isLoading = true;

        progressBar.setVisibility(View.VISIBLE);

        ProductApiHelper.getCategoryBrandProducts(currentPage, "root", null, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {

                List<ProductItem> data = response.getData();

                productItemList.addAll(data);
                adapterProducts.notifyItemRangeInserted(productItemList.size()-data.size(),  data.size());

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

    private void getNotificationCount() {

        if (CredentialManager.getToken().equals(""))
            return;

        GeneralApiHelper.getNotificationCount(CredentialManager.getToken(), "newsfeed", new ResponseListenerAuth<NotificationCount, String>() {
            @Override
            public void onDataFetched(NotificationCount response, int statusCode) {
                if (response.getCount() > 0)
                    view.findViewById(R.id.newsfeedIndicator).setVisibility(View.VISIBLE);
                else
                    view.findViewById(R.id.newsfeedIndicator).setVisibility(View.GONE);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

                if (!logout)
                    getNotificationCount();
                else if (getActivity() != null) {
                    // Toast.makeText(getActivity(),"Token expired, please login again", Toast.LENGTH_LONG).show();
                    AppController.logout(getActivity());
                }
            }
        });
    }


    private void checkReferral() {

        if (!userDetails.getRef().equals("")) {

            HashMap<String, String> params = new HashMap<>();

            params.put("token", userDetails.getToken().trim());
            params.put("referred_by", userDetails.getRef().trim());
            params.put("device_id", Utils.getDeviceID(context).trim());

            GeneralApiHelper.checkReferral(params, new ResponseListenerAuth<JsonObject, String>() {
                @Override
                public void onDataFetched(JsonObject response, int statusCode) {

                    String message = response.get("message").getAsString();

                    if (!message.equals("")) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        userDetails.setRef("");
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
        getNotificationCount();
        super.onResume();
    }

    public void getSliderImage() {

        AuthApiHelper.getBanners(new DataFetchingListener<Response<JsonObject>>() {
            @Override
            public void onDataFetched(Response<JsonObject> response) {
                if (response.code() == 200 || response.code() == 201) {
                    sliderImages = new Gson().fromJson(response.body().get("results"), new TypeToken<List<BannerItem>>() {
                    }.getType());
                    sliderPager.setAdapter(new SliderAdapter(context, activity, sliderImages));
                    sliderIndicator.setupWithViewPager(sliderPager, true);
                }
            }

            @Override
            public void onFailed(int status) {

            }
        });

    }

    private void onDone() {
        shimmer.setVisibility(View.GONE);
    }
}
