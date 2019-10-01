package bd.com.evaly.evalyshop.activity.giftcard;


import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.BaseViewPagerAdapter;

public class GiftCardActivity extends AppCompatActivity {


    ViewPager viewPager;
    TabLayout tabLayout;
    BaseViewPagerAdapter pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giftcards);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewpager);

        pager = new BaseViewPagerAdapter(getSupportFragmentManager());

        pager.addFragment(new GiftCardListFragment(),"MY GIFT CARDS");
        pager.addFragment(new GiftCardListFragment(),"GIFT CARDS");

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewPager.setAdapter(pager);
    }
}
