package bd.com.evaly.evalyshop.ui.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.OnDoneListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.giftcard.GiftCardActivity;
import bd.com.evaly.evalyshop.ui.home.HomeTabsFragment;
import bd.com.evaly.evalyshop.ui.newsfeed.NewsfeedActivity;
import bd.com.evaly.evalyshop.ui.order.orderList.OrderListActivity;
import retrofit2.Response;

public class HomePageRecyclerHeader extends RecyclerView.ViewHolder {

    View view;
    private Fragment fragmentInstance;
    private AppCompatActivity activityInstance;
    private NavController navController;
    private Context context;

    public HomePageRecyclerHeader(View itemView, Context context, AppCompatActivity activityInstance, Fragment fragmentInstance, NavController navController) {
        super(itemView);

        this.context = context;
        this.fragmentInstance = fragmentInstance;
        this.activityInstance = activityInstance;
        this.navController = navController;

        view = itemView;
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.setFullSpan(true);
        initHeader(view);

    }


    private void initHeader(View view) {

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSmoothScrollingEnabled(true);

        LinearLayout voucher = view.findViewById(R.id.voucher);

        final ViewPager viewPager = view.findViewById(R.id.pager);
        HomeTabPagerAdapter pager = new HomeTabPagerAdapter(fragmentInstance.getFragmentManager());

        ShimmerFrameLayout shimmer = view.findViewById(R.id.shimmer);

        try {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmer();
        } catch (Exception ignored) {
        }

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(1);

        LinearLayout evalyStore = view.findViewById(R.id.evaly_store);
        evalyStore.setOnClickListener(v -> {
            Intent ni = new Intent(context, GiftCardActivity.class);
            context.startActivity(ni);
        });

        voucher.setOnClickListener(v -> {
//            CampaignBottomSheetFragment campaignBottomSheetFragment = CampaignBottomSheetFragment.newInstance();
//            if (fragmentInstance.getFragmentManager() != null)
//                campaignBottomSheetFragment.show(fragmentInstance.getFragmentManager(), "Campaign BottomSheet");

            navController.navigate(R.id.campaignFragment);

        });

        LinearLayout wholesale = view.findViewById(R.id.evaly_wholesale);
        wholesale.setOnClickListener(v -> context.startActivity(new Intent(context, NewsfeedActivity.class)));

        LinearLayout orders = view.findViewById(R.id.orders);
        orders.setOnClickListener(v -> {

            if (CredentialManager.getToken().equals("")) {
                context.startActivity(new Intent(context, SignInActivity.class));
            } else {
                context.startActivity(new Intent(context, OrderListActivity.class));
            }
        });


        // slider
        ViewPager sliderPager = view.findViewById(R.id.sliderPager);
        TabLayout sliderIndicator = view.findViewById(R.id.sliderIndicator);

        HomeTabsFragment categoryFragment = new HomeTabsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("slug", "root");
        bundle.putString("category", "root");
        categoryFragment.setArguments(bundle);

        HomeTabsFragment brandFragment = new HomeTabsFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("type", 2);
        bundle2.putString("slug", "root");
        bundle2.putString("category", "root");
        brandFragment.setArguments(bundle2);

        OnDoneListener onDoneListener = () -> shimmer.setVisibility(View.GONE);

        brandFragment.setOnDoneListener(onDoneListener);
        categoryFragment.setOnDoneListener(onDoneListener);

        HomeTabsFragment shopFragment = new HomeTabsFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("type", 3);
        bundle3.putString("slug", "root");
        bundle3.putString("category", "root");
        shopFragment.setArguments(bundle3);

        pager.addFragment(categoryFragment, context.getResources().getString(R.string.categories));
        pager.addFragment(brandFragment, context.getResources().getString(R.string.brands));
        pager.addFragment(shopFragment, context.getResources().getString(R.string.shops));
        pager.notifyDataSetChanged();


        Handler handler = new Handler();
        handler.postDelayed(() -> {
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
        }, 1500);

        AuthApiHelper.getBanners(new DataFetchingListener<Response<JsonObject>>() {
            @Override
            public void onDataFetched(Response<JsonObject> response) {
                if (response.code() == 200 || response.code() == 201) {
                    ArrayList<BannerItem> sliderImages = new Gson().fromJson(response.body().get("results"), new TypeToken<List<BannerItem>>() {
                    }.getType());
                    sliderPager.setAdapter(new SliderAdapter(context, activityInstance, sliderImages));
                    sliderIndicator.setupWithViewPager(sliderPager, true);
                }
            }

            @Override
            public void onFailed(int status) {

            }
        });


        if (!CredentialManager.getToken().equals("")) {
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

                }
            });
        }

    }
}