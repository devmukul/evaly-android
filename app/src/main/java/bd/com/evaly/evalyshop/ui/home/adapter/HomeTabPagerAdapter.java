package bd.com.evaly.evalyshop.ui.home.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ObjectAtPositionInterface;

public class HomeTabPagerAdapter extends FragmentStatePagerAdapter implements ObjectAtPositionInterface {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private int mCurrentPosition = -1;
    private FragmentManager manager;

    public HomeTabPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

//    @Override
//    public Object instantiateItemObject(ViewGroup container, int position) {
//        return null;
//    }

//    @Override
//    public void destroyItemObject(ViewGroup container, int position, Object object) {
//        if (position >= mFragmentList.size())
//            return;
//        mFragmentList.remove(position);
//    }


    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return mFragmentTitleList.get(position);
    }


//    @Override
//    public void setPrimaryItem(ViewGroup container, int position, Object object) {
//        super.setPrimaryItem(container, position, object);
//        if (position != mCurrentPosition) {
//            Fragment fragment = (Fragment) object;
//            WrapContentHeightViewPager pager = (WrapContentHeightViewPager) container;
//
//            if (fragment.getView() != null) {
//                mCurrentPosition = position;
//                pager.updateView(fragment.getView());
//            }
//        }
//    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }


    @Override
    public int getItemPosition(Object object) {

        // onDoneListener.onDone();

        if (mFragmentList.contains(object))
            return mFragmentList.indexOf(object);
        else
            return POSITION_NONE;
    }


    public void clear() {
//        FragmentTransaction transaction = manager.beginTransaction();
//        for (Fragment fragment : mFragmentList) {
//            transaction.remove(fragment);
//        }
//        mFragmentList.clear();
//        transaction.commitAllowingStateLoss();

        mFragmentList.clear();
        mFragmentTitleList.clear();
    }


    @Override
    public Object getObjectAtPosition(int position) {
        if (position >= mFragmentList.size())
            return null;
        return mFragmentList.get(position);
    }
}