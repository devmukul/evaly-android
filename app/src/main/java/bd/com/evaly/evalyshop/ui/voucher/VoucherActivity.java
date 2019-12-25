package bd.com.evaly.evalyshop.ui.voucher;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.BaseViewPagerAdapter;
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
