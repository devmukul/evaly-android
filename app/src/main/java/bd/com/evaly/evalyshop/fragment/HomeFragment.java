package bd.com.evaly.evalyshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.ProductGrid;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.GlobalSearchActivity;
import bd.com.evaly.evalyshop.activity.InitializeActionBar;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.OrderListActivity;
import bd.com.evaly.evalyshop.activity.SignInActivity;
import bd.com.evaly.evalyshop.activity.giftcard.GiftCardActivity;
import bd.com.evaly.evalyshop.activity.newsfeed.NewsfeedActivity;
import bd.com.evaly.evalyshop.adapter.HomeTabPagerAdapter;
import bd.com.evaly.evalyshop.adapter.SliderAdapter;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.BannerItem;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.ui.campaign.CampaignBottomSheetFragment;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.SliderViewPager;
import retrofit2.Response;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private MainActivity activity;
    private HomeTabPagerAdapter pager;
    private SliderViewPager sliderPager;
    private TabLayout sliderIndicator;
    private List<BannerItem> sliderImages;
    private LinearLayout homeSearch,evalyStore;
    private TabLayout tabLayout;
    private LinearLayout voucher;
    private ShimmerFrameLayout shimmer;
    private boolean isShimmerShowed = false;
    private View view;
    private String defaultCategory = "root";
    private UserDetails userDetails;
    private Context context;
    private SwipeRefreshLayout swipeLayout;




    @Override
    public void onRefresh() {

        swipeLayout.setRefreshing(false);

        refreshFragment();


    }

    public HomeFragment() {
        // Required empty public constructor
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


    private void refreshFragment(){

        //getFragmentManager().beginTransaction().detach(this).attach(this).commit();


        if (getFragmentManager() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getFragmentManager().beginTransaction().detach(this).commitNow();
                getFragmentManager().beginTransaction().attach(this).commitNow();
            } else {
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }

            pager.notifyDataSetChanged();

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                shimmer.stopShimmer();
                shimmer.setVisibility(View.GONE);
            }, 300);
        }

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
                    if (getFragmentManager() != null) {
                        HomeFragment homeFragment = new HomeFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).addToBackStack("Home").commit();
                    }
                }
            });


        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSmoothScrollingEnabled(true);
        voucher = view.findViewById(R.id.voucher);
        homeSearch = view.findViewById(R.id.home_search);

        homeSearch.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, GlobalSearchActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        });

        final ViewPager viewPager = view.findViewById(R.id.pager);
        pager = new HomeTabPagerAdapter(getChildFragmentManager());

        shimmer = view.findViewById(R.id.shimmer);
        try {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmer();
        } catch (Exception e) {

        }

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(1);


        evalyStore = view.findViewById(R.id.evaly_store);
        evalyStore.setOnClickListener(v -> {

            Intent ni = new Intent(context, GiftCardActivity.class);
            startActivity(ni);

        });

        voucher.setOnClickListener(v -> {
            CampaignBottomSheetFragment campaignBottomSheetFragment = CampaignBottomSheetFragment.newInstance();
            campaignBottomSheetFragment.show(getFragmentManager(), "Campaign BottomSheet");
        });


        LinearLayout wholesale = view.findViewById(R.id.evaly_wholesale);
        wholesale.setOnClickListener(v -> startActivity(new Intent(context, NewsfeedActivity.class)));

        userDetails = new UserDetails(context);

        InitializeActionBar InitializeActionbar = new InitializeActionBar((LinearLayout) view.findViewById(R.id.header_logo), activity, "home");

        LinearLayout orders = view.findViewById(R.id.orders);

        orders.setOnClickListener(v -> {

            if (userDetails.getToken().equals("")) {
                startActivity(new Intent(context, SignInActivity.class));
            } else {
                startActivity(new Intent(context, OrderListActivity.class));
            }
        });


        NestedScrollView nestedSV = view.findViewById(R.id.stickyScrollView);

        ProductGrid productGrid = new ProductGrid(context, view.findViewById(R.id.products), defaultCategory, view.findViewById(R.id.progressBar));
        productGrid.setScrollView(nestedSV);

        // slider
        sliderPager = view.findViewById(R.id.sliderPager);
        sliderIndicator = view.findViewById(R.id.sliderIndicator);

        sliderImages = new ArrayList<>();
        getSliderImage();

        if (nestedSV != null) {
            nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    try {
                        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
                        productGrid.loadNextPage();

                    } catch (Exception e) {
                        Log.e("load more product", e.toString());
                    }
                }
            });
        }


        try {
            checkReferral();
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

        HomeTabsFragment categoryFragment = new HomeTabsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("slug", "root");
        bundle.putString("category", "root");
        categoryFragment.setArguments(bundle);


        categoryFragment.setOnDoneListener(() -> {

            shimmer.setVisibility(View.GONE);


        });


        HomeTabsFragment brandFragment = new HomeTabsFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("type", 2);
        bundle2.putString("slug", "root");
        bundle2.putString("category", "root");
        brandFragment.setArguments(bundle2);

        brandFragment.setOnDoneListener(() -> {

            shimmer.setVisibility(View.GONE);


        });


        HomeTabsFragment shopFragment = new HomeTabsFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("type", 3);
        bundle3.putString("slug", "root");
        bundle3.putString("category", "root");
        shopFragment.setArguments(bundle3);

        pager.addFragment(categoryFragment, "Categories");
        pager.addFragment(brandFragment, "Brands");
        pager.addFragment(shopFragment, "Shops");
        pager.notifyDataSetChanged();


        Handler handler = new Handler();
        handler.postDelayed(() -> {
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
        }, 1500);



    }



    public void getNotificationCount(){

        if (CredentialManager.getToken().equals(""))
            return;

        GeneralApiHelper.getNotificationCount(CredentialManager.getToken(), "newsfeed", new ResponseListenerAuth<NotificationCount, String>() {
            @Override
            public void onDataFetched(NotificationCount response, int statusCode) {
                if (response.getCount()>0)
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
                else
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(),"Token expired, please login again", Toast.LENGTH_LONG).show();
                        AppController.logout(getActivity());
                    }
            }
        });
    }


    private void checkReferral(){

        if (!userDetails.getRef().equals("")){

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

    public void getSliderImage(){

        AuthApiHelper.getBanners(new DataFetchingListener<Response<JsonObject>>() {
            @Override
            public void onDataFetched(Response<JsonObject> response) {
                if (response.code() == 200 || response.code() == 201){
                    sliderImages = new Gson().fromJson(response.body().get("results"), new TypeToken<List<BannerItem>>(){}.getType());
                    sliderPager.setAdapter(new SliderAdapter(context, activity, sliderImages));
                    sliderIndicator.setupWithViewPager(sliderPager, true);
                }else {
                    // Toast.makeText(getContext(), getContext().getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(int status) {
                // Toast.makeText(getContext(), getContext().getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
            }
        });

    }
}
