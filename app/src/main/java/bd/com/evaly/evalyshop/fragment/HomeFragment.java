package bd.com.evaly.evalyshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import bd.com.evaly.evalyshop.activity.EvalyStoreActivity;
import bd.com.evaly.evalyshop.activity.GlobalSearchActivity;
import bd.com.evaly.evalyshop.activity.InitializeActionBar;
import bd.com.evaly.evalyshop.activity.InviteEarn;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.OrderListActivity;
import bd.com.evaly.evalyshop.activity.SignInActivity;
import bd.com.evaly.evalyshop.activity.VoucherActivity;
import bd.com.evaly.evalyshop.adapter.HomeTabPagerAdapter;
import bd.com.evaly.evalyshop.ProductGrid;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.SliderAdapter;
import bd.com.evaly.evalyshop.models.BannerItem;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.SliderViewPager;

public class HomeFragment extends Fragment {

    MainActivity activity;

    HomeTabPagerAdapter pager;
    SliderViewPager sliderPager;
    TabLayout sliderIndicator;
    List<BannerItem> sliderImages;
    LinearLayout homeSearch,evalyStore;
    TabLayout tabLayout;
    LinearLayout voucher;
    ShimmerFrameLayout shimmer;
    private boolean isShimmerShowed = false;
    Timer timer;
    RequestQueue rq;
    View view;
    String defaultCategory = "root";
    UserDetails userDetails;
    Context context;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        activity = (MainActivity) getActivity();
        context = getContext();
        rq = Volley.newRequestQueue(context);
        evalyStore=view.findViewById(R.id.evaly_store);
        evalyStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, EvalyStoreActivity.class));
            }
        });

//        Random Dice = new Random();
//        int n = Dice.nextInt(Data.homeRandomCategory.length);
//        defaultCategory = Data.homeRandomCategory[n];

        return view;
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSmoothScrollingEnabled(true);
        voucher=view.findViewById(R.id.voucher);
        homeSearch=view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GlobalSearchActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        });

        final ViewPager viewPager = view.findViewById(R.id.pager);
        pager = new HomeTabPagerAdapter(getChildFragmentManager());

        shimmer = view.findViewById(R.id.shimmer);
        try {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmer();
        } catch (Exception e){

        }

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pager);
        
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        viewPager.setOffscreenPageLimit(1);

        voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context, VoucherActivity.class));


            }
        });


        LinearLayout wholesale = view.findViewById(R.id.evaly_wholesale);

        wholesale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userDetails.getToken().equals("")){
                    startActivity(new Intent(context, SignInActivity.class));
                }else {
                    startActivity(new Intent(context, InviteEarn.class));
                }
            }
        });


        userDetails = new UserDetails(context);

        InitializeActionBar InitializeActionbar = new InitializeActionBar((LinearLayout) view.findViewById(R.id.header_logo), activity, "home");


        LinearLayout orders = view.findViewById(R.id.orders);

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userDetails.getToken().equals("")){
                    startActivity(new Intent(context, SignInActivity.class));
                }else{
                    startActivity(new Intent(context, OrderListActivity.class));
                }
            }
        });


        ProductGrid productGrid = new ProductGrid(context, view.findViewById(R.id.products), defaultCategory, view.findViewById(R.id.progressBar));

        // slider
        sliderPager =  view.findViewById(R.id.sliderPager);
        sliderIndicator=view.findViewById(R.id.sliderIndicator);

        sliderImages = new ArrayList<>();
        getSliderImage();

        //timer = new Timer();
        // timer.scheduleAtFixedRate(new SliderTimer(), 6000, 6000);


        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout);
        CoordinatorLayout rootLayout = view.findViewById(R.id.root_coordinator);

        NestedScrollView nestedSV = view.findViewById(R.id.stickyScrollView);

        if (nestedSV != null) {

            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";

                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        Log.i(TAG, "BOTTOM SCROLL");

                        try {

                            ((ProgressBar) view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);

                            productGrid.loadNextPage();

                        } catch (Exception e)
                        {
                            Log.e("load more product", e.toString());
                        }



                    }
                }
            });
        }




        try {
            checkReferral();
        }catch (Exception e){
            Log.e("exception", e.toString());
        }






        TabsFragment categoryFragment = new TabsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("slug", "root");
        bundle.putString("category", "root");
        categoryFragment.setArguments(bundle);

        TabsFragment brandFragment = new TabsFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("type", 2);
        bundle2.putString("slug", "root");
        bundle2.putString("category", "root");
        brandFragment.setArguments(bundle2);

        TabsFragment shopFragment = new TabsFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("type", 3);
        bundle3.putString("slug", "root");
        bundle3.putString("category", "root");
        shopFragment.setArguments(bundle3);

        pager.addFragment(categoryFragment,"Categories");
        pager.addFragment(brandFragment,"Brands");
        pager.addFragment(shopFragment,"Shops");
        pager.notifyDataSetChanged();



    }


    private void checkReferral(){



        if (!userDetails.getRef().equals("")){

            String url = "https://nsuer.club/evaly/referral/submit-referral.php";

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d("json", response);

                    try{

                        JSONObject jsonObject = new JSONObject(response);

                        String message = jsonObject.getString("message");

                        if (!message.equals("")) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                            userDetails.setRef("");

                        }

                    } catch (Exception e){

                        Toast.makeText(context, "Couldn't verify invitation code.", Toast.LENGTH_LONG).show();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("onErrorResponse", error.toString());

                    Toast.makeText(context, "Server error occurred, couldn't verify invitation code.", Toast.LENGTH_LONG).show();

                }
            }) {

                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("token", userDetails.getToken().trim());
                    params.put("referred_by", userDetails.getRef().trim());
                    params.put("device_id", Utils.getDeviceID(context).trim());

                    Log.d("json params", params.toString());

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    return headers;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(context);
            request.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            queue.add(request);



        }

    }



    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of HomeFragment");
        super.onResume();


    }

    @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of HomeFragment");
        super.onPause();
    }




    public void hideShimmer(){

        try {

            shimmer.stopShimmer();
        } catch (Exception e){

        }
        shimmer.setVisibility(View.GONE);
        isShimmerShowed = true;

    }




    private class SliderTimer extends TimerTask {
        @Override
        public void run() {

            try {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (sliderPager.getCurrentItem() < sliderImages.size() - 1) {
                            sliderPager.setCurrentItem(sliderPager.getCurrentItem() + 1);
                        } else {
                            sliderPager.setCurrentItem(0);
                        }
                    }
                });
            } catch (Exception e){

            }
        }
    }

    public void getSliderImage(){
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, UrlUtils.BANNER, (String) null,
                responsez -> {


                    JSONArray response;

            try {

                response = responsez.getJSONArray("results");

            } catch (Exception e){

                response = new JSONArray();

            }
                    if(response.length() < 1){

                        RelativeLayout sliderHolder = view.findViewById(R.id.sliderHolder);
                        sliderHolder.setVisibility(View.GONE);


                    }

                    for(int i=0;i<response.length();i++){
                        try {

                            JSONObject ob = response.getJSONObject(i);

                            BannerItem bannerItem = new BannerItem();
                            bannerItem.setImage(ob.getString("image"));
                            bannerItem.setName(ob.getString("name"));
                            bannerItem.setSlug(ob.getString("slug"));
                            bannerItem.setStatus(ob.getString("status"));
                            bannerItem.setType(ob.getString("type"));
                            bannerItem.setUrl(ob.getString("url"));

                            sliderImages.add(bannerItem);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    sliderPager.setAdapter(new SliderAdapter(context, activity, sliderImages));
                    sliderIndicator.setupWithViewPager(sliderPager, true);
                },
                error -> Log.d("Error.Response", error.toString())
        );

        getRequest.setShouldCache(true);
        getRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        rq.add(getRequest);
    }
}
