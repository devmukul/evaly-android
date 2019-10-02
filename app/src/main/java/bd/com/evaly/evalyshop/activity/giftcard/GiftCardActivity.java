package bd.com.evaly.evalyshop.activity.giftcard;


import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.BaseViewPagerAdapter;
import bd.com.evaly.evalyshop.util.UserDetails;

public class GiftCardActivity extends AppCompatActivity {


    ViewPager viewPager;
    TabLayout tabLayout;
    BaseViewPagerAdapter pager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giftcards);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Gift Cards");

        UserDetails userDetails = new UserDetails(this);

        if (userDetails.getToken().equals(""))
        {
            Toast.makeText(this, "You need to login first", Toast.LENGTH_SHORT).show();
            finish();
        }

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewpager);

        pager = new BaseViewPagerAdapter(getSupportFragmentManager());

        pager.addFragment(new GiftCardListFragment(),"MY GIFT CARDS");
        pager.addFragment(new GiftCardListFragment(),"GIFT CARDS");

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewPager.setAdapter(pager);



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
