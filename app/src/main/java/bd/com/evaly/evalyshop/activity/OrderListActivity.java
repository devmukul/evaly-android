package bd.com.evaly.evalyshop.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.OrderListTabAdapter;
import bd.com.evaly.evalyshop.fragment.OrderListFragment;
import bd.com.evaly.evalyshop.util.UserDetails;

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


        pager.addFragment(OrderListFragment.getInstance("all"),"All");
        pager.addFragment(OrderListFragment.getInstance("pending"),"Pending");
        pager.addFragment(OrderListFragment.getInstance("confirmed"),"Confirmed");
        pager.addFragment(OrderListFragment.getInstance("processing"),"Processing");
        pager.addFragment(OrderListFragment.getInstance("picked"),"Picked");
        pager.addFragment(OrderListFragment.getInstance("shipped"),"Shipped");
        pager.addFragment(OrderListFragment.getInstance("delivered"),"Delivered");
        pager.addFragment(OrderListFragment.getInstance("cancel"),"Cancelled");

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
