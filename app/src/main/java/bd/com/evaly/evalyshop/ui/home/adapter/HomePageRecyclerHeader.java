package bd.com.evaly.evalyshop.ui.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
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
import bd.com.evaly.evalyshop.ui.adapters.FragmentTabPagerAdapter;
import bd.com.evaly.evalyshop.ui.giftcard.GiftCardActivity;
import bd.com.evaly.evalyshop.ui.home.HomeTabsFragment;
import bd.com.evaly.evalyshop.ui.newsfeed.NewsfeedActivity;
import retrofit2.Response;

public class HomePageRecyclerHeader extends RecyclerView.ViewHolder {

    private AppCompatActivity activityInstance;
    private Fragment fragmentInstance;
    private NavController navController;
    private Context context;
    private RecyclerHeaderHomeBinding binding;
    private FragmentTabPagerAdapter pager;
    private HashMap<Integer, ViewTreeObserver.OnGlobalLayoutListener> listenerList = new HashMap<>();
    private HashMap<Integer, View> viewList = new HashMap<>();


    public HomePageRecyclerHeader(RecyclerHeaderHomeBinding binding, Context context, AppCompatActivity activityInstance, Fragment fragmentInstance, NavController navController) {
        super(binding.getRoot());

        this.context = context;
        this.activityInstance = activityInstance;
        this.fragmentInstance = fragmentInstance;
        this.navController = navController;
        this.binding = binding;

        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        layoutParams.setFullSpan(true);

        initHeader(binding);

    }

    private void initHeader(RecyclerHeaderHomeBinding binding) {

        pager = new FragmentTabPagerAdapter(fragmentInstance.getChildFragmentManager(), fragmentInstance.getLifecycle());

        fragmentInstance.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (viewList.size() == 0)
                return;

            for (int i = 0; i < listenerList.size(); i++) {
                View view = viewList.get(i);
                if (view != null)
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(listenerList.get(i));
            }

            viewList.clear();
            listenerList.clear();
//            binding.viewPager.setAdapter(null);
//            pager = null;
        });

        binding.shimmer.shimmer.setVisibility(View.VISIBLE);
        binding.shimmer.shimmer.startShimmer();

        binding.viewPager.setOffscreenPageLimit(1);
        binding.viewPager.setAdapter(pager);

        binding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        binding.tabLayout.setSmoothScrollingEnabled(true);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(pager.getTitle(position))
        ).attach();

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            private void updateChildHeight(View v) {

                ViewGroup.LayoutParams params = binding.viewPager.getLayoutParams();

                binding.viewPager.post(() -> {
                    int wMeasureSpec = View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY);
                    int hMeasureSpec = View.MeasureSpec.makeMeasureSpec(v.getHeight(), View.MeasureSpec.UNSPECIFIED);
                    v.measure(wMeasureSpec, hMeasureSpec);

                    params.height = v.getMeasuredHeight();

                    if (v.getMeasuredHeight() != binding.viewPager.getHeight()) {
                        binding.viewPager.setLayoutParams(params);
                    }
                });
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                View v = pager.getViewAtPosition(position);

                if (v == null) {
                    new Handler().postDelayed(() -> {

                        try {
                            View vv = pager.getViewAtPosition(position);
                            if (vv != null)
                                vv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        updateChildHeight(vv);
                                        viewList.put(position, vv);
                                        listenerList.put(position, this);
                                    }
                                });

                        } catch (Exception ignored) { }
                    }, 1000);


                } else
                    v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            updateChildHeight(v);
                            viewList.put(position, v);
                            listenerList.put(position, this);
                        }
                    });
            }
        });


        binding.evalyStore.setOnClickListener(v -> {
            Intent ni = new Intent(context, GiftCardActivity.class);
            context.startActivity(ni);
        });

        binding.voucher.setOnClickListener(v -> navController.navigate(R.id.campaignFragment));
        binding.evalyWholesale.setOnClickListener(v -> context.startActivity(new Intent(context, NewsfeedActivity.class)));

        binding.orders.setOnClickListener(v -> {

            navController.navigate(R.id.evalyExpressFragment);

//            if (CredentialManager.getToken().equals(""))
//                context.startActivity(new Intent(context, SignInActivity.class));
//            else
//                context.startActivity(new Intent(context, OrderListActivity.class));
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
            pager.addFragment(categoryFragment, context.getResources().getString(R.string.categories));
            pager.addFragment(brandFragment, context.getResources().getString(R.string.brands));
            pager.addFragment(shopFragment, context.getResources().getString(R.string.shops));
            pager.notifyDataSetChanged();

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