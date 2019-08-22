package bd.com.evaly.evalyshop.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.BaseViewPagerAdapter;
import bd.com.evaly.evalyshop.adapter.HomeTabPagerAdapter;
import bd.com.evaly.evalyshop.fragment.TabsFragment;
import bd.com.evaly.evalyshop.fragment.VoucherListFragment;
import bd.com.evaly.evalyshop.fragment.VoucherMyListFragment;

public class VoucherActivity extends AppCompatActivity {


    ViewPager viewPager;
    TabLayout tabLayout;
    BaseViewPagerAdapter pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewpager);

        pager = new BaseViewPagerAdapter(getSupportFragmentManager());

        pager.addFragment(new VoucherMyListFragment(),"My Voucher");
        pager.addFragment(new VoucherListFragment(),"Voucher");

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewPager.setAdapter(pager);
    }
}
