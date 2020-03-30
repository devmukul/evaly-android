package bd.com.evaly.evalyshop.ui.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.ui.browseProduct.tabs.TabsViewModel;

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

    public String getTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public View getViewAtPosition(int position) {
        return mFragmentList.get(position).getView();
    }

    public TabsViewModel getViewModel(int position) {
        int type = 1;
        if (mFragmentList.size() == 2) {
            if (position == 0)
                type = 2;
            else if (position == 1)
                type = 3;
        } else {
            if (position == 0)
                type = 1;
            else if (position == 1)
                type = 2;
            else
                type = 3;
        }
        return new ViewModelProvider(mFragmentList.get(position)).get("type_" + type, TabsViewModel.class);
    }


    public Fragment getFragmentAtPosition(int position) {
        return mFragmentList.get(position);
    }

    public void clear() {
        mFragmentList.clear();
        mFragmentTitleList.clear();
        notifyDataSetChanged();
    }
}