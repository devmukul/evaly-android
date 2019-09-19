package bd.com.evaly.evalyshop.activity.newsfeed.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import bd.com.evaly.evalyshop.views.WrapContentHeightViewPager;

public class NewsfeedPager extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private int mCurrentPosition = -1;


    public NewsfeedPager(FragmentManager manager) {

        super(manager);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Log.i("PosTab",""+position);

        return mFragmentTitleList.get(position);
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

    }




    @Override
    public Fragment getItem(int position) {
        Log.i("PosTabItem",""+position);
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }



//    @Override
//    public int getItemPosition(Object object) {
//
//        if (mFragmentList.contains(object))
//            return mFragmentList.indexOf(object);
//        else
//            return POSITION_NONE;
//    }

    public Fragment getCurrentFragment() {
        return mFragmentList.get(0);
    }


}