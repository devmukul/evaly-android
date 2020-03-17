package bd.com.evaly.evalyshop.ui.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentTabPagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public FragmentTabPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }

    public String getTitle(int position){
        return mFragmentTitleList.get(position);
    }

    public View getViewAtPosition(int position){
        return mFragmentList.get(position).getView();
    }

    public Fragment getFragmentAtPosition(int position){
        return mFragmentList.get(position);
    }

    public void clear(){
        mFragmentList.clear();
        mFragmentTitleList.clear();
        notifyDataSetChanged();
    }
}