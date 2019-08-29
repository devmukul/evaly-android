package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.ProductGrid;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.HomeTabPagerAdapter;
import bd.com.evaly.evalyshop.adapter.OrderAdapter;
import bd.com.evaly.evalyshop.adapter.OrderListTabAdapter;
import bd.com.evaly.evalyshop.fragment.OrderListFragment;
import bd.com.evaly.evalyshop.fragment.TabsFragment;
import bd.com.evaly.evalyshop.util.Orders;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.views.StickyScrollView;

public class OrderListActivity extends BaseActivity {

    UserDetails userDetails;

    String userAgent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Order List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        TabLayout tabLayout = findViewById(R.id.tabs);


        ViewPager viewPager = findViewById(R.id.pager);

        OrderListTabAdapter pager =  new OrderListTabAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pager);

        OrderListFragment all = new OrderListFragment();
        Bundle ab = new Bundle();
        ab.putString("type", "all");
        all.setArguments(ab);

        OrderListFragment pending = new OrderListFragment();
        Bundle ap = new Bundle();
        ap.putString("type", "pending");
        pending.setArguments(ap);

        OrderListFragment confirmed = new OrderListFragment();
        Bundle ac = new Bundle();
        ac.putString("type", "confirmed");
        confirmed.setArguments(ac);

        OrderListFragment processing = new OrderListFragment();
        Bundle apr = new Bundle();
        apr.putString("type", "processing");
        processing.setArguments(apr);

        OrderListFragment picked = new OrderListFragment();
        Bundle apk = new Bundle();
        apk.putString("type", "picked");
        picked.setArguments(apk);

        OrderListFragment shipped = new OrderListFragment();
        Bundle as = new Bundle();
        as.putString("type", "shipped");
        shipped.setArguments(as);

        OrderListFragment delivered = new OrderListFragment();
        Bundle ad = new Bundle();
        ad.putString("type", "delivered");
        delivered.setArguments(ad);

        OrderListFragment cancel = new OrderListFragment();
        Bundle acan = new Bundle();
        acan.putString("type", "cancel");
        cancel.setArguments(acan);

        pager.addFragment(all,"All");
        pager.addFragment(pending,"Pending");
        pager.addFragment(confirmed,"Confirmed");
        pager.addFragment(processing,"Processing");
        pager.addFragment(picked,"Picked");
        pager.addFragment(shipped,"Shipped");
        pager.addFragment(delivered,"Delivered");
        pager.addFragment(cancel,"Cancelled");

        pager.notifyDataSetChanged();

        viewPager.setOffscreenPageLimit(1);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setSmoothScrollingEnabled(true);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });





    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
