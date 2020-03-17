package bd.com.evaly.evalyshop.ui.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.RecyclerHeaderHomeBinding;
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

    private AppCompatActivity activityInstance;
    private Fragment fragmentInstance;
    private NavController navController;
    private Context context;
    private RecyclerHeaderHomeBinding binding;
    private HomeTabPagerAdapter pager;


    public HomePageRecyclerHeader(RecyclerHeaderHomeBinding binding, Context context, AppCompatActivity activityInstance, Fragment fragmentInstance, NavController navController, HomeTabPagerAdapter pager) {
        super(binding.getRoot());

        this.context = context;
        this.activityInstance = activityInstance;
        this.fragmentInstance = fragmentInstance;
        this.navController = navController;
        this.binding = binding;
        this.pager = pager;

        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        layoutParams.setFullSpan(true);

        initHeader(binding);

    }

    private void initHeader(RecyclerHeaderHomeBinding binding) {

        // pager = new HomeTabPagerAdapter(fragmentInstance.getFragmentManager());


        binding.shimmer.shimmer.setVisibility(View.VISIBLE);
        binding.shimmer.shimmer.startShimmer();

        binding.viewPager.setOffscreenPageLimit(2);
        binding.viewPager.setAdapter(pager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        binding.tabLayout.setSmoothScrollingEnabled(true);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.evalyStore.setOnClickListener(v -> {
            Intent ni = new Intent(context, GiftCardActivity.class);
            context.startActivity(ni);
        });

        binding.voucher.setOnClickListener(v -> navController.navigate(R.id.campaignFragment));
        binding.evalyWholesale.setOnClickListener(v -> context.startActivity(new Intent(context, NewsfeedActivity.class)));

        binding.orders.setOnClickListener(v -> {
            if (CredentialManager.getToken().equals(""))
                context.startActivity(new Intent(context, SignInActivity.class));
            else
                context.startActivity(new Intent(context, OrderListActivity.class));
        });

        // slider

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

        OnDoneListener onDoneListener = () -> binding.shimmer.shimmer.setVisibility(View.GONE);

        brandFragment.setOnDoneListener(onDoneListener);
        categoryFragment.setOnDoneListener(onDoneListener);

        HomeTabsFragment shopFragment = new HomeTabsFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("type", 3);
        bundle3.putString("slug", "root");
        bundle3.putString("category", "root");
        shopFragment.setArguments(bundle3);

        if (pager != null) {
            pager.clear();
            pager.addFragment(categoryFragment, context.getResources().getString(R.string.categories));


            pager.addFragment(brandFragment, context.getResources().getString(R.string.brands));
            pager.addFragment(shopFragment, context.getResources().getString(R.string.shops));
            pager.notifyDataSetChanged();

            binding.viewPager.post(() -> binding.viewPager.setMinimumHeight(2000));


        }


        Handler handler = new Handler();
        handler.postDelayed(() -> {
            binding.shimmer.shimmer.stopShimmer();
            binding.shimmer.shimmer.setVisibility(View.GONE);
        }, 1500);

        AuthApiHelper.getBanners(new DataFetchingListener<Response<JsonObject>>() {
            @Override
            public void onDataFetched(Response<JsonObject> response) {
                if (response.code() == 200 || response.code() == 201) {
                    ArrayList<BannerItem> sliderImages = new Gson().fromJson(response.body().get("results"), new TypeToken<List<BannerItem>>() {
                    }.getType());
                    binding.sliderPager.setAdapter(new SliderAdapter(context, activityInstance, sliderImages));
                    binding.sliderIndicator.setupWithViewPager(binding.sliderPager, true);
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
                        binding.newsfeedIndicator.setVisibility(View.VISIBLE);
                    else
                        binding.newsfeedIndicator.setVisibility(View.GONE);
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