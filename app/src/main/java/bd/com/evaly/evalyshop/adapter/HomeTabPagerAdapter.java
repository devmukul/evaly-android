package bd.com.evaly.evalyshop.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import bd.com.evaly.evalyshop.views.WrapContentHeightViewPager;

public class HomeTabPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private int mCurrentPosition = -1;


    public HomeTabPagerAdapter(FragmentManager manager) {

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
        if (position != mCurrentPosition) {
            Fragment fragment = (Fragment) object;
            WrapContentHeightViewPager pager = (WrapContentHeightViewPager) container;


            if (fragment != null && fragment.getView() != null) {
                mCurrentPosition = position;
                pager.measureCurrentView(fragment.getView());
            }
        }
    }

    public void removeTabPage(int position) {
        if (!mFragmentList.isEmpty() && position<mFragmentList.size()) {

        }

        String toFind = "Sub Categories";
        if(position == 2)
            toFind = "Brands";
        else if(position == 3)
            toFind = "Shops";




        for (int i = 0; i<mFragmentList.size(); i++) {

            if(mFragmentTitleList.get(i).equals(toFind)) {
                mFragmentList.remove(position);
                mFragmentTitleList.remove(position);
                notifyDataSetChanged();
            }
        }

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



    @Override
    public int getItemPosition(Object object) {



        if (mFragmentList.contains(object))
            return mFragmentList.indexOf(object);
        else
            return POSITION_NONE;
    }

    public Fragment getCurrentFragment() {
        return mFragmentList.get(mCurrentPosition);
    }


}