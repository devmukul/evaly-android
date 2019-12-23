package bd.com.evaly.evalyshop.activity;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

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
