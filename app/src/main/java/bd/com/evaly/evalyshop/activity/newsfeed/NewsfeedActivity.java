package bd.com.evaly.evalyshop.activity.newsfeed;

import android.app.Dialog;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.newsfeed.adapters.NewsfeedPager;
import bd.com.evaly.evalyshop.util.ScreenUtils;

public class NewsfeedActivity extends AppCompatActivity {



    private ViewPager viewPager;
    private NewsfeedPager pager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Newsfeed");


        BottomSheetDialog dialog = new BottomSheetDialog(NewsfeedActivity.this, R.style.BottomSheetDialogTheme);
        dialog.setContentView(R.layout.alert_comments);

        View bottomSheetInternal = dialog.findViewById(android.support.design.R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        ScreenUtils screenUtils = new ScreenUtils(NewsfeedActivity.this);
//                bottomSheetBehavior.setPeekHeight(15000);

        LinearLayout dialogLayout = dialog.findViewById(R.id.container2);
        dialogLayout.setMinimumHeight(screenUtils.getHeight());



        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tab_layout);

        pager = new NewsfeedPager(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(pager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
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


        NewsfeedFragment publicFragment = NewsfeedFragment.newInstance("public");
        pager.addFragment(publicFragment,"PUBLIC");


        NewsfeedFragment announcementFragment = NewsfeedFragment.newInstance("announcement");
        pager.addFragment(announcementFragment,"ANNOUNCEMENT");



        NewsfeedFragment ceoFragment = NewsfeedFragment.newInstance("ceo");
        pager.addFragment(ceoFragment,"CEO");


        pager.notifyDataSetChanged();




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
